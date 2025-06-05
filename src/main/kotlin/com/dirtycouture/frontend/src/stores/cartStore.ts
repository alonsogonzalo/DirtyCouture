import { defineStore } from 'pinia'
import api from '../services/api' // ← instancia de Axios con interceptor

export const useCartStore = defineStore('cart', {
    state: () => ({
        // Siempre arranca como array vacío
        cartItems: [] as any[]
    }),
    actions: {
        /**
         * Obtiene el carrito de un usuario.
         * Llama a GET /api/cart/{userId} (Bearer token dentro de api).
         */
        async fetchCart(userId: number) {
            try {
                const response = await api.get(`/cart/${userId}`)
                console.log('RESPUESTA CART:', response.data) // <-- Depuración
                this.cartItems = Array.isArray(response.data) ? response.data : []
            } catch (err) {
                console.error('Error fetching cart:', err)
                this.cartItems = []
            }
        },

        /**
         * Elimina un item del carrito.
         * Llama a DELETE /api/cart/delete/{userId}/{variantId}.
         */
        async removeFromCart(userId: number, variantId: number) {
            try {
                await api.delete(`/cart/delete/${userId}/${variantId}`)
                await this.fetchCart(userId)
            } catch (err) {
                console.error('Error removing from cart:', err)
            }
        },

        /**
         * Vacia todo el carrito.
         * Llama a DELETE /api/cart/clear/{userId}.
         */
        async clearCartFromBackend(userId: number) {
            try {
                await api.delete(`/cart/clear/${userId}`)
                await this.fetchCart(userId)
            } catch (err) {
                console.error('Error clearing cart:', err)
            }
        },

        /**
         * Añade un variant al carrito.
         * Llama a POST /api/cart/add/{userId}/{variantId}.
         */
        async addToCart(userId: number, variantId: number) {
            try {
                await api.post(`/cart/add/${userId}/${variantId}`)
                await this.fetchCart(userId)
            } catch (err) {
                console.error('Error adding to cart:', err)
            }
        },

        // Limpia localmente (no borra en backend)
        clearCart() {
            this.cartItems = []
        }
    }
})
