package com.example.doloresapp.data.cart

import com.example.doloresapp.domain.model.Producto

object CartRepository {
    private data class CartItem(val product: Producto, var quantity: Int)
    private val items: MutableList<CartItem> = mutableListOf()

    @Synchronized
    fun add(producto: Producto, quantity: Int = 1) {
        val existing = items.firstOrNull { it.product.id == producto.id }
        if (existing != null) {
            existing.quantity += quantity
        } else {
            items.add(CartItem(producto, quantity))
        }
    }

    @Synchronized
    fun remove(productId: Int, quantity: Int = 1) {
        val idx = items.indexOfFirst { it.product.id == productId }
        if (idx == -1) return
        val entry = items[idx]
        entry.quantity -= quantity
        if (entry.quantity <= 0) items.removeAt(idx)
    }

    @Synchronized
    fun clear() { items.clear() }

    @Synchronized
    fun getItems(): List<Pair<Producto, Int>> = items.map { it.product to it.quantity }

    @Synchronized
    fun getCount(): Int = items.sumOf { it.quantity }
}
