<template>
  <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
    <div
        v-for="product in products"
        :key="product.id"
        class="relative border rounded-lg p-4 shadow hover:shadow-lg transition"
        :class="{ 'bg-gray-200 opacity-70 pointer-events-none': product.stock === 0 }"
    >
      <div class="absolute top-4 right-4 text-xl cursor-pointer" @click="fav(product)">
        <i :class="['fas', product.fav ? 'fa-heart text-red-500' : 'fa-heart text-gray-400']"></i>
      </div>

      <img
          :src="product.image_url"
          alt="Imagen del producto"
          class="w-full h-48 object-cover mb-4 rounded-md"
      />
      <h3 class="text-xl font-bold mb-2">{{ product.name }}</h3>
      <p class="text-gray-700 mb-2">Precio: €{{ product.price }}</p>

      <button
          v-if="product.stock > 0"
          class="w-full bg-black text-white font-semibold py-2 rounded-lg hover:bg-gray-800 transition"
          @click="openVariantSelector(product)"
      >
        Añadir al carrito
      </button>
      <button
          v-else
          class="w-full bg-red-600 text-white font-semibold py-2 rounded-lg cursor-not-allowed"
          disabled
      >
        Agotado
      </button>
    </div>

    <div
        v-if="showSelector"
        class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <div class="bg-white rounded-lg p-6 w-full max-w-md">
        <h2 class="text-2xl font-bold mb-4">Selecciona talla y color</h2>

        <div class="space-y-4">
          <div v-if="currentProduct.product_variants.length === 0">
            <p class="text-red-500">Este producto no tiene variantes disponibles.</p>
          </div>

          <div
              v-for="variant in currentProduct.product_variants"
              :key="variant.id"
              class="flex items-center justify-between border rounded p-3"
              :class="{ 'bg-gray-100': selectedVariant?.id === variant.id }"
              @click="selectVariant(variant)"
          >
            <div>
              <p class="font-semibold">
                Talla: {{ variant.size }} | Color: {{ variant.color }}
              </p>
              <p class="text-sm text-gray-500">Stock: {{ variant.stock }}</p>
            </div>
            <div v-if="selectedVariant?.id === variant.id">
              <i class="fas fa-check text-green-600"></i>
            </div>
          </div>
        </div>

        <div class="flex justify-end gap-4 mt-6">
          <button
              @click="cancelVariant"
              class="px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400"
          >
            Cancelar
          </button>
          <button
              :disabled="!selectedVariant || selectedVariant.stock === 0"
              @click="confirmAddToCart"
              class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 disabled:opacity-50"
          >
            Confirmar
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { supabase } from '../supabaseClient.js'
import { useUserStore } from '../stores/userStore'
import { useCartStore } from '../stores/cartStore'

const userStore = useUserStore()
const cartStore = useCartStore()

const products = ref([])
const showSelector = ref(false)
const currentProduct = ref(null)
const selectedVariant = ref(null)

const openVariantSelector = (product) => {
  currentProduct.value = product
  if (!product.product_variants || product.product_variants.length === 0) {
    alert('No hay variantes disponibles para este producto.')
    return
  }
  selectedVariant.value = null
  showSelector.value = true
}

const selectVariant = (variant) => {
  selectedVariant.value = variant
}

const cancelVariant = () => {
  currentProduct.value = null
  selectedVariant.value = null
  showSelector.value = false
}

const confirmAddToCart = async () => {
  const userId = userStore.user?.id
  const variantId = selectedVariant.value?.id

  if (!userId || !variantId) {
    console.error('Faltan datos para añadir al carrito')
    return
  }

  try {
    const response = await fetch(`http://localhost:8080/api/cart/add/${userId}/${variantId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${userStore.token}`,
      },
    })

    if (!response.ok) throw new Error('Error al añadir al carrito')

    await cartStore.fetchCart(userId)
    cancelVariant()
  } catch (error) {
    console.error('Error al añadir al carrito:', error)
  }
}

const fetchProducts = async () => {
  const { data, error } = await supabase.from('products').select(`
    *,
    product_variants (id, size, color, stock, price, image_url)
  `)

  if (error) {
    console.error('Error fetching products:', error)
  } else {
    products.value = data.map((p) => ({
      ...p,
      stock: p.product_variants.reduce((acc, v) => acc + (v.stock || 0), 0),
      fav: false,
    }))
  }
}

const fav = (product) => {
  product.fav = !product.fav
}

onMounted(fetchProducts)
</script>

<style scoped>
@import "@fortawesome/fontawesome-free/css/all.min.css";
</style>
