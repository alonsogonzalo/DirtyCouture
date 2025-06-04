// stores/cartStore.js
import { defineStore } from 'pinia'
import axios from 'axios'

export const useCartStore = defineStore('cart', {
    state: () => ({
        cartItems: [],
    }),
    actions: {
        async fetchCart(userId) {
            try {
                const response = await axios.get(`/api/cart/${userId}`)
                this.cartItems = response.data
            } catch (error) {
                console.error('Error fetching cart:', error)
            }
        },
        async addToCart(userId, variantId, quantity = 1) {
            try {
                await axios.post('/api/cart', {
                    userId,
                    variantId,
                    quantity,
                })
                await this.fetchCart(userId)
            } catch (error) {
                console.error('Error adding to cart:', error)
            }
        },
        async removeFromCart(userId, variantId) {
            try {
                await axios.delete(`/api/cart/${userId}/${variantId}`)
                await this.fetchCart(userId)
            } catch (error) {
                console.error('Error removing from cart:', error)
            }
        },
        clearCart() {
            this.cartItems = []
        },
    },
})
