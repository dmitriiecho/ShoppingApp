package krio.systemdesign.shoppingapp.shared.domain.model

// A cart line can grow while it holds fewer than the stock. Callers pass the stock they know.
fun canAddOneMore(
    inCart: Int,
    stock: Int,
): Boolean = inCart < stock
