<script setup>
import { ref, onMounted } from 'vue'
import { supabase } from '../supabaseClient.js'

const products = ref([])

const fetchProducts = async () => {
  const { data, error } = await supabase
      .from('products')
      .select(`
      *,
      product_variants (
        stock
      )
    `)

  if (error) {
    console.error('Error fetching products with stock:', error)
  } else {
    products.value = data.map(p => {
      const stock = p.product_variants?.[0]?.stock ?? 0
      console.log(`Producto: ${p.name} | Stock: ${stock}`)
      return {
        ...p,
        stock
      }
    })
  }
}

onMounted(fetchProducts)
</script>

<template>
  <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
    <div
        v-for="product in products"
        :key="product.id"
        class="relative border rounded-lg p-4 shadow hover:shadow-lg transition"
        :class="{ 'bg-gray-200 opacity-70 pointer-events-none': product.stock === 0 }"
    >
      <img :src="product.image_url" alt="Imagen del producto" class="w-full h-48 object-cover mb-4 rounded-md" />
      <h3 class="text-xl font-bold mb-2">{{ product.name }}</h3>
      <p class="text-gray-700 mb-2">Precio: €{{ product.price }}</p>

      <button
          v-if="product.stock > 0"
          class="w-full bg-black text-white font-semibold py-2 rounded-lg hover:bg-gray-800 transition"
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
  </div>
</template>
