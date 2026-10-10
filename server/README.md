# ShoppingApp server

[Русская версия](README.ru.md)

A small Ktor server for the app: it serves the catalog and promo codes and validates the cart.

- **Address:** `http://2.56.204.151:8080/` (there's no domain, so no HTTPS).
- **Build:** a separate Gradle build that the app's `settings.gradle.kts` doesn't include.
- **Formats** match the app's DTOs: `ProductDTO` in `feature/catalog`, `PromoCodeDTO` in `feature/promo`, `CartValidationDTO.kt` in `:shared:data`.

## API

| Request | Returns |
|---|---|
| `GET /products` | A catalog page |
| `GET /products/{id}` | One product |
| `GET /promo-codes` | All promo codes |
| `GET /promo-codes/{code}` | One promo code |
| `GET /images/{file}` | A product image |
| `POST /cart/validate` | Where the cart differs from the catalog |

Every request and response the app relies on has a sample in [`api-samples/`](api-samples/), and `requests.json` in that folder lists each request's address. The server's tests check that it responds exactly as the samples show, and the app's tests read the same files, so changing a format or an address on one side fails that side's tests.

### `GET /products`

| Parameter | Meaning |
|---|---|
| `query` | Search by name. Case and leading or trailing spaces are ignored; an empty query returns the whole catalog. |
| `page` | Page number, from 1. |
| `pageSize` | From 1 to 100. |

An invalid `page` or `pageSize` returns 400. Products are returned in the order they appear in `products.json`.

```json
{
  "products": [
    {
      "id": "1",
      "name": "Wireless Headphones",
      "price": 14999,
      "imageUrl": "http://2.56.204.151:8080/images/1.png",
      "description": "Over-ear wireless headphones…",
      "availableQuantity": 3
    }
  ],
  "endReached": false
}
```

- `price` is in US cents: `14999` is $149.99.
- `availableQuantity` is how many units can be ordered; `0` means out of stock.

### `GET /products/{id}`

One product in the same format, or 404.

### `GET /promo-codes`

All codes, in the order they appear in `promo-codes.json`. The app shows them as a hint on the promo code screen.

```json
[{"code": "SALE10", "discountPercent": 10}, {"code": "SALE25", "discountPercent": 25}]
```

### `GET /promo-codes/{code}`

One code, or 404. Case and leading or trailing spaces are ignored: `sale10` returns `SALE10`, as written in the file.

```json
{"code": "SALE10", "discountPercent": 10}
```

### `GET /images/{file}`

A file from `data/images/`. A product's `imageUrl` points here: it is built from the server's address (`PUBLIC_URL`), `/images/` and the product's `image`.

### `POST /cart/validate`

Checks the cart before checkout. `promoCode` is optional.

```json
{
  "items": [{"productId": "1", "price": 7999, "quantity": 2}],
  "promoCode": "SALE10"
}
```

Response:

```json
{
  "issues": [{"type": "priceChanged", "productId": "1", "newPrice": 8999}],
  "promoCodeValid": true
}
```

`issues` is empty when the cart matches the catalog. Each item can have the following issues:

| `type` | When | Extra field |
|---|---|---|
| `unavailable` | Out of stock or not in the catalog | — |
| `priceChanged` | The price differs from the catalog | `newPrice` |
| `notEnoughStock` | The quantity is more than `availableQuantity` | `availableQuantity` |

- An `unavailable` item has no other issues.
- Price and stock are checked separately, so one item can have both `priceChanged` and `notEnoughStock`.
- `promoCodeValid` is `false` when the sent code is no longer in `promo-codes.json`, and `true` when no code is sent.
- A body in the wrong format or a `quantity` below 1 returns 400.

## Data

| File | Contents |
|---|---|
| `data/products.json` | Products |
| `data/promo-codes.json` | Promo codes |
| `data/images/` | Product images, `<id>.png` |

The JSON files are read once at startup, so run `./deploy.sh` after editing them. Images are read on every request.

### Rules

- **Products are never removed**: an out-of-stock product gets `"availableQuantity": 0`. The app's paging relies on this (`ProductApi` in `feature/catalog`).
- **A new product** gets an id greater than all existing ones and goes at the end of the file.
- **Prices** are in US dollars, stored as whole cents: `"price": 14999` is $149.99.
- **`availableQuantity`** is never negative.
- **`discountPercent`** is between 1 and 100 and never changes: a different discount needs a new code.
- **A removed promo code** stops working: an app that has already applied it finds out during cart validation.
- **`image`** is the file name in `data/images/`, `<id>.png`. The full address isn't stored, because it depends on where the server runs.
- **Product `3` (USB-C Hub) deliberately has no image file**, so it shows what the app draws when an image fails to load. Every other product has its own `<id>.png`; `DataFilesTest` checks both.

The server refuses to start if it finds duplicate ids or codes, invalid JSON or an unknown field, and the tests catch these problems at build time.

### Products the settings screen relies on

The app's settings have items that put a product into the cart to demonstrate a cart issue (use cases in `feature/settings`). These products must keep their values; otherwise the cart would show a different issue.

| Product | Must keep | Settings item | Adds to the cart |
|---|---|---|---|
| `16` Hoodie | stock 0 | Add an out-of-stock item to the cart | 1 copy |
| `48` Laundry Basket | stock 1–9, price 4699 | Add more of an item than is in stock | 10 copies |
| `40` Cutting Board | stock above 0, price not 4900 | Add an item with an outdated price | 1 copy at 4900 |
| `32` Notebook | stock 1–9, price not 995 | Add an item with two changes | 10 copies at 995 |

`DataFilesTest` sends these carts to a server running on the real `data/products.json` and checks that each one still gets exactly its intended issue, so `./gradlew test` (and CI) fails if any of them breaks.

## Running

```sh
./gradlew test            # run the tests, including reading the files in data/
./gradlew run             # run locally on port 8080 with data/; image URLs start with http://localhost:8080
./gradlew spotlessCheck   # check code style (rules in the root .editorconfig); spotlessApply fixes it
./deploy.sh               # test, build and restart the server on this machine
```

`deploy.sh` passes the server `PORT`, `DATA_DIR` and `PUBLIC_URL`, the address the app uses to reach it (`ServerConfig.BASE_URL` in `core/config`).

On the machine, the server runs as the user systemd service `shoppingapp-server`. It runs from a copy in `~/server/shoppingapp`, so a new build doesn't affect the running version. It starts automatically after a reboot and doesn't need sudo.

```sh
journalctl --user -u shoppingapp-server -f        # logs, including every request
systemctl --user status shoppingapp-server        # check whether it is running (also: stop, restart)
```
