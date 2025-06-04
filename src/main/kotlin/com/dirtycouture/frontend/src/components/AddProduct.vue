<script setup>
import { ref } from 'vue'
import { supabase } from '../supabaseClient'

const name = ref('')
const price = ref('')
const imageUrl = ref('')
const message = ref('')

async function addProduct() {
  if (!name.value || !price.value) {
    message.value = 'Empty fields'
    return
  }

  const { data, error } = await supabase
      .from('products')
      .insert([{ name: name.value, price: parseFloat(price.value), image_url: imageUrl.value }])

  if (error) {
    message.value = `Error: ${error.message}`
  } else {
    message.value = 'Product add correctly'
    name.value = ''
    price.value = ''
    imageUrl.value = ''
  }
}
</script>

<template>
  <div class="add-product">
    <h2>Añadir producto</h2>
    <input v-model="name" placeholder="Nombre del producto" />
    <input v-model="price" type="number" placeholder="Precio" />
    <input v-model="imageUrl" placeholder="URL imagen (opcional)" />
    <button @click="addProduct">Añadir producto</button>
    <p>{{ message }}</p>
  </div>
</template>

<style scoped>
.add-product {
  max-width: 300px;
  margin: 20px auto;
  display: flex;
  flex-direction: column;
}
.add-product input {
  margin-bottom: 10px;
  padding: 8px;
}
.add-product button {
  background-color: #4caf50;
  color: white;
  padding: 8px;
  border: none;
  cursor: pointer;
}
</style>
