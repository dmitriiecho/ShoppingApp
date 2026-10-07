#!/usr/bin/env bash
# Деплой сервера ShoppingApp на эту машину: тесты, сборка, копирование в ~/server/shoppingapp и перезапуск.
# Запуск из любой папки: server/deploy.sh

# Остановить скрипт при первой же ошибке, чтобы не продолжать с недособранным сервером.
set -euo pipefail

# --- Настройки ---

# Папка с исходниками сервера — та, где лежит этот скрипт.
SERVER_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Куда кладётся готовый сервер. Он работает из этой копии, поэтому новая сборка не мешает запущенной версии.
INSTALL_DIR="$HOME/server/shoppingapp"
SERVICE=shoppingapp-server
# Порты всех серверов на машине перечислены в ~/server/README.md.
PORT=8080

# Сервер собирается и работает на JDK 21. Если JAVA_HOME не задан, скрипт остановится с этим сообщением.
: "${JAVA_HOME:?Нужен JAVA_HOME с JDK 21: на нём собирается и работает сервер}"

# --- 1. Тесты и сборка ---

# Если тест упадёт, скрипт остановится здесь, а работающий сервер останется нетронутым.
# Собранный сервер со всеми библиотеками появляется в build/install/server.
cd "$SERVER_DIR"
./gradlew test installDist

# --- 2. Сервис systemd ---

# systemd держит сервер запущенным в фоне: перезапускает при падении и запускает после перезагрузки машины.
# Текст между <<EOF и EOF записывается в файл сервиса, вместо $ПЕРЕМЕННЫХ подставляются их значения.
SERVICE_FILE="$HOME/.config/systemd/user/$SERVICE.service"
mkdir -p "$(dirname "$SERVICE_FILE")"
cat > "$SERVICE_FILE" <<EOF
[Unit]
Description=ShoppingApp server

[Service]
Environment=JAVA_HOME=$JAVA_HOME
Environment=PORT=$PORT
Environment=DATA_DIR=$INSTALL_DIR/data
ExecStart=$INSTALL_DIR/app/bin/server
Restart=on-failure
RestartSec=5

[Install]
WantedBy=default.target
EOF
# Сообщить systemd, что файл сервиса мог измениться.
systemctl --user daemon-reload

# --- 3. Замена работающей версии ---

# Сначала останавливаем сервер: он читает файлы, которые сейчас будут перезаписаны.
systemctl --user stop "$SERVICE"
rm -rf "$INSTALL_DIR/app" "$INSTALL_DIR/data"
mkdir -p "$INSTALL_DIR"
cp -r build/install/server "$INSTALL_DIR/app"
cp -r data "$INSTALL_DIR/data"
# Запустить сервер и включить ему автозапуск.
systemctl --user enable --now "$SERVICE"

# --- 4. Проверки ---

# Без linger сервисы пользователя не стартуют после перезагрузки машины, пока он не зайдёт по SSH.
if [[ "$(loginctl show-user "$USER" -p Linger --value)" != yes ]]; then
    echo "Внимание: после перезагрузки сервер сам не запустится. Включи один раз: sudo loginctl enable-linger $USER" >&2
fi

# Ждём до 30 секунд, пока сервер начнёт отвечать на запросы.
for _ in $(seq 30); do
    if curl -sf "http://localhost:$PORT/products?page=1&pageSize=1" > /dev/null; then
        echo "Сервер работает на порту $PORT"
        exit 0
    fi
    sleep 1
done
echo "Сервер не ответил за 30 секунд. Логи: journalctl --user -u $SERVICE -n 50" >&2
exit 1
