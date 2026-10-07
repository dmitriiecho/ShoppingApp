# ShoppingApp server

[Русская версия](README.ru.md)

A small Ktor server for the app: catalog, promo codes and cart validation.

- **Address:** `http://2.56.204.151:8080/` (no domain, so no HTTPS).
- **Build:** a separate Gradle build, not included in the app's `settings.gradle.kts`.
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

### `GET /products`

| Parameter | Meaning |
|---|---|
| `query` | Search by name. Ignores case and surrounding spaces; empty returns the whole catalog. |
| `page` | Page number, from 1. |
| `pageSize` | From 1 to 100. |

A wrong `page` or `pageSize` gets 400. Products come in `products.json` order.

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
- `availableQuantity` is how many can be ordered; `0` means out of stock.

### `GET /products/{id}`

One product in the same format, or 404.

### `GET /promo-codes`

All codes in `promo-codes.json` order. The app shows them as a hint on the promo code screen.

```json
[{"code": "SALE10", "discountPercent": 10}, {"code": "SALE25", "discountPercent": 25}]
```

### `GET /promo-codes/{code}`

One code, or 404. Ignores case and surrounding spaces: `sale10` returns `SALE10`, as written in the file.

```json
{"code": "SALE10", "discountPercent": 10}
```

### `GET /images/{file}`

A file from `data/images/`. A product's `imageUrl` points here.

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

`issues` is empty when the cart matches the catalog. Each item can get these issues:

| `type` | When | Extra field |
|---|---|---|
| `unavailable` | Out of stock or not in the catalog | — |
| `priceChanged` | The price differs from the catalog | `newPrice` |
| `notEnoughStock` | More than `availableQuantity` | `availableQuantity` |

- An `unavailable` item gets no other issues.
- Price and stock are checked separately: one item can get both `priceChanged` and `notEnoughStock`.
- `promoCodeValid` is `false` when the sent code is no longer in `promo-codes.json`, and `true` when no code is sent.
- A body in the wrong format gets 400.

## Data

| File | Contents |
|---|---|
| `data/products.json` | Products |
| `data/promo-codes.json` | Promo codes |
| `data/images/` | Product images |

The JSON files are read once at startup: after an edit, run `./deploy.sh`. Images are read on every request.

### Rules

- **Products are never removed**: an out-of-stock product gets `"availableQuantity": 0`. The app's paging relies on it (`ProductApi` in `feature/catalog`).
- **A new product** gets an id greater than the existing ones and goes to the end of the file.
- **Prices** are in US dollars, as whole cents: `"price": 14999` is $149.99.
- **`availableQuantity`** is never negative.
- **`discountPercent`** is from 1 to 100 and never changes: a different discount gets a new code.
- **A removed promo code** stops working: an app that applied it finds out at cart validation.

The server refuses to start on duplicate ids or codes, a JSON error or an unknown field, and the tests catch these at build time.

### Products the settings screen relies on

The app's settings have items that put a copy of a product in the cart to show a cart issue (use cases in `feature/settings`). These products must keep their values, otherwise the cart shows a different issue.

| Product | Must keep | Settings item | Adds to the cart |
|---|---|---|---|
| `16` Hoodie | stock 0 | Add an out-of-stock item to the cart | 1 copy |
| `48` Laundry Basket | stock below 10, price 4699 | Add more of an item than is in stock | 10 copies |
| `40` Cutting Board | stock above 0, price not 4900 | Add an item with an outdated price | 1 copy at 4900 |
| `32` Notebook | stock 1–9, price not 995 | Add an item with two changes | 10 copies at 995 |

## Running

```sh
./gradlew test            # tests, including reading the files in data/
./gradlew run             # run locally on port 8080 with data/
./gradlew spotlessCheck   # code style (rules in the root .editorconfig); spotlessApply fixes it
./deploy.sh               # test, build and restart the server on this machine
```

On the machine, the server is the user systemd service `shoppingapp-server`. It runs from a copy in `~/server/shoppingapp`, so a new build doesn't touch the running version. It starts by itself after a reboot and needs no sudo.

```sh
journalctl --user -u shoppingapp-server -f        # logs, including every request
systemctl --user status shoppingapp-server        # is it running (also: stop, restart)
```
