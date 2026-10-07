#!/usr/bin/env bash
# Deploys the server on this machine: tests, build, copy to ~/server/shoppingapp, restart.
# Runs from any folder: server/deploy.sh
set -euo pipefail

SERVER_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# The server runs from this copy, so a new build doesn't disturb the running one.
INSTALL_DIR="$HOME/server/shoppingapp"
SERVICE=shoppingapp-server
# The ports of all servers on this machine are listed in ~/server/README.md.
PORT=8080

: "${JAVA_HOME:?JAVA_HOME must point to JDK 21: the server builds and runs on it}"

# --- 1. Tests and build ---

# A failed test stops the script here and leaves the running server alone.
cd "$SERVER_DIR"
./gradlew test installDist

# --- 2. systemd service ---

# systemd restarts the server after a crash and starts it after a reboot.
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
# The JVM exits with 143 on SIGTERM; without this, every stop is logged as a failure.
SuccessExitStatus=143

[Install]
WantedBy=default.target
EOF
systemctl --user daemon-reload

# --- 3. Replacing the running version ---

# Stop first: the server reads the files about to be overwritten.
systemctl --user stop "$SERVICE"
rm -rf "$INSTALL_DIR/app" "$INSTALL_DIR/data"
mkdir -p "$INSTALL_DIR"
cp -r build/install/server "$INSTALL_DIR/app"
cp -r data "$INSTALL_DIR/data"
systemctl --user enable --now "$SERVICE"

# --- 4. Checks ---

# Without linger, user services don't start after a reboot until the user logs in.
if [[ "$(loginctl show-user "$USER" -p Linger --value)" != yes ]]; then
    echo "Warning: the server won't start after a reboot. Run once: sudo loginctl enable-linger $USER" >&2
fi

for _ in $(seq 30); do
    if curl -sf "http://localhost:$PORT/products?page=1&pageSize=1" > /dev/null; then
        echo "Server is running on port $PORT"
        exit 0
    fi
    sleep 1
done
echo "Server didn't respond in 30 seconds. Logs: journalctl --user -u $SERVICE -n 50" >&2
exit 1
