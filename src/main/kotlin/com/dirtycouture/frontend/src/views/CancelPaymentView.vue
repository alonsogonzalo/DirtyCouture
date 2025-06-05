<template>
  <div class="max-w-2xl mx-auto p-6 text-center">
    <h1 class="text-3xl font-bold text-red-600 mb-4">Pago cancelado</h1>
    <p class="text-lg mb-4">Parece que no se completó el proceso de pago.</p>
    <p class="text-gray-600">Puedes volver a intentarlo desde tu carrito o modificar tu pedido.</p>

    <router-link to="/cart" class="mt-6 inline-block text-blue-600 hover:underline">
      Volver al carrito
    </router-link>
  </div>
</template>

<script setup lang="ts">
</script>
