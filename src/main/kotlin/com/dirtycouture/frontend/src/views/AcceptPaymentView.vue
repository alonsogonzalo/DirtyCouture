<template>
  <div class="max-w-2xl mx-auto p-6 text-center">
    <h1 class="text-3xl font-bold text-green-600 mb-4">¡Pago completado!</h1>
    <p class="text-lg mb-4">Gracias por tu compra. Tu pedido se ha registrado correctamente.</p>
    <p class="text-gray-600">Número de pedido: {{ orderId }}</p>

    <router-link to="/" class="mt-6 inline-block text-blue-600 hover:underline">
      Volver al inicio
    </router-link>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
const orderId = route.query.orderId
</script>
