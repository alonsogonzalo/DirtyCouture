<!-- views/CartView.vue -->
<template>
  <div class="p-6">
    <h1 class="text-3xl font-bold mb-4">Shopping Cart</h1>

    <div v-if="cartItems.length === 0" class="text-gray-500">
      Your cart is empty.
    </div>

    <div v-else class="space-y-4">
      <div
          v-for="item in cartItems"
          :key="item.variantId"
          class="flex justify-between items-center border p-4 rounded"
      >
        <div>
          <p class="font-semibold">{{ item.productName }}</p>
          <p class="text-sm text-gray-500">Size: {{ item.size }}, Color: {{ item.color }}</p>
          <p class="text-sm text-gray-500">Quantity: {{ item.quantity }}</p>
        </div>
        <div class="flex items-center space-x-4">
          <p class="font-bold">{{ formatPrice(item.price * item.quantity) }}</p>
          <button @click="removeItem(item.variantId)" class="text-red-600 hover:underline">
            Remove
          </button>
        </div>
      </div>

      <div class="text-right mt-6">
        <p class="text-xl font-bold">Total: {{ formatPrice(totalPrice) }}</p>
        <router-link to="/checkout">
          <button class="mt-4 px-6 py-2 bg-green-600 text-white rounded hover:bg-green-700">
            Proceed to Checkout
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue'
import { useCartStore } from '../stores/cartStore'
import { useUserStore } from '../stores/userStore'

const cartStore = useCartStore()
const userStore = useUserStore()

onMounted(() => {
  if (userStore.user) {
    cartStore.fetchCart(userStore.user.id)
  }
})

const cartItems = computed(() => cartStore.cartItems)

const totalPrice = computed(() =>
    cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
)

function formatPrice(amount) {
  return `$${(amount / 100).toFixed(2)}`
}

function removeItem(variantId) {
  if (userStore.user) {
    cartStore.removeFromCart(userStore.user.id, variantId)
  }
}
</script>
