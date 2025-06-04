<script setup>
import { ref, onMounted } from 'vue'
import { supabase } from '../supabaseClient.js'

const products = ref([])

const fetchProducts = async () => {
  const { data, error } = await supabase
      .from('products')      // Cambia 'products' por el nombre de tu tabla
      .select('*')

  if (error) {
    console.error('Error fetching products:', error)
  } else {
    products.value = data
  }
}

onMounted(() => {
  fetchProducts()
})
</script>

<template>
  <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
    <div v-for="product in products" :key="product.id" class="bg-white rounded-2xl shadow-md p-4 flex flex-col items-center">
      <img :src="product.image_url || 'https://via.placeholder.com/150'" class="mb-4 rounded-md" />
      <h2 class="text-lg font-semibold">{{ product.name }}</h2>
      <p class="text-sm text-gray-500">€{{ product.price }}</p>
    </div>
  </div>
</template>
