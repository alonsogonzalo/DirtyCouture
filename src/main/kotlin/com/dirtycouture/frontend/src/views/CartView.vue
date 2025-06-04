<template>
  <div class="p-6">
    <h1 class="text-3xl font-bold mb-4">Shopping Cart</h1>

    <!-- Debug: muestra el JSON crudo del carrito -->
    <pre v-if="debug">{{ JSON.stringify(cartItems, null, 2) }}</pre>

    <div v-if="cartItems.length === 0" class="text-gray-500">
      Your cart is empty.
    </div>

    <div v-else class="space-y-4">
      <div
          v-for="item in cartItems"
          :key="item.variantId"
          class="flex items-center gap-4 border p-4 rounded"
      >
        <img
            :src="item.imageUrl"
            class="w-20 h-20 object-cover rounded"
            alt="product image"
        />
        <div class="flex-grow">
          <p class="font-semibold">{{ item.productName }}</p>
          <p class="text-sm text-gray-500">
            Size: {{ item.size }}, Color: {{ item.color }}
          </p>
          <p class="text-sm text-gray-500">Quantity: {{ item.quantity }}</p>
        </div>
        <div class="text-right space-y-2">
          <p class="font-bold">{{ formatPrice(item.price * item.quantity) }}</p>
          <button
              @click="removeItem(item.variantId)"
              class="text-red-600 hover:underline"
          >
            Remove
          </button>
        </div>
      </div>

      <div class="text-right mt-6 space-y-2">
        <p class="text-xl font-bold">Total: {{ formatPrice(totalPrice) }}</p>
        <div class="flex justify-end gap-4">
          <button
              @click="clearCart"
              class="px-6 py-2 bg-red-600 text-white rounded hover:bg-red-700"
          >
            Clear Cart
          </button>
          <router-link to="/checkout/shippingAddress">
            <button class="px-6 py-2 bg-green-600 text-white rounded hover:bg-green-700">
              Proceed to Checkout
            </button>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref, watch } from 'vue'
import { useCartStore } from '../stores/cartStore'
import { useUserStore } from '../stores/userStore'

const cartStore = useCartStore()
const userStore = useUserStore()

// Modo debug para ver en pantalla el JSON crudo
const debug = ref(false)

onMounted(() => {
  console.log('USER:', userStore.user)
  if (userStore.user) {
    cartStore.fetchCart(userStore.user.id)
  } else {
    console.warn('No hay userStore.user, fetchCart no se llama')
  }
})

// Ver en consola cada vez que cambian los items del carrito
watch(
    () => cartStore.cartItems,
    (newVal) => {
      console.log('cartItems actualizado:', newVal)
    }
)

const cartItems = computed(() =>
    Array.isArray(cartStore.cartItems) ? cartStore.cartItems : []
)

const totalPrice = computed(() => {
  const items = cartItems.value
  return items.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

function formatPrice(amount: number): string {
  return `€${amount.toFixed(2)}`
}

function removeItem(variantId: number) {
  if (userStore.user) {
    cartStore.removeFromCart(userStore.user.id, variantId)
  }
}

function clearCart() {
  if (userStore.user) {
    const confirmed = confirm('Are you sure you want to clear the cart?')
    if (confirmed) {
      cartStore.clearCartFromBackend(userStore.user.id)
    }
  }
}
</script>
