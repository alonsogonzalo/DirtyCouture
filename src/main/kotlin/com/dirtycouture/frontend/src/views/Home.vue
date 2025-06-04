<script setup>
import Logo from "../assets/logo.png";
import AddProduct  from "../components/AddProduct.vue";
import ProductList from "../components/ProductList.vue";
</script>

<template>
  <div class="flex flex-col min-h-screen bg-white">

    <!-- Header -->
    <header class="bg-black text-white px-6 py-2 flex items-center justify-between max-h-20 overflow-hidden">
      <div class="w-1/3"></div>
      <div class="w-1/3 flex justify-center">
        <!--<img src="/src/assets/logo.png" alt="DirtyCouture Logo" class="h-10 object-contain" />-->
        <img :src="Logo" alt="DirtyCouture Logo" class="w-60 h-auto object-contain"/>
      </div>
      <router-link to="/login" class="bg-black text-white px-6 py-3 rounded-2xl shadow-md hover:bg-gray-800 transition">
        Iniciar sesión
      </router-link>
      <div class="w-1/3 flex justify-end space-x-4 text-2xl">
        <router-link to="/settings">
          <i class="fas fa-user cursor-pointer hover:text-gray-300"></i>
        </router-link>
        <i class="fas fa-shopping-cart"></i>
      </div>
    </header>

    <!-- Main content -->
    <main class="p-6 max-w-6xl mx-auto flex-grow">
      <h1 class="text-3xl font-extrabold text-gray-900 mb-6 text-center">Nuestros productos</h1>

      <!-- <AddProduct />-->
      <ProductList />

    </main>

    <!-- Footer -->
    <footer class="bg-red-600 text-white h-24 w-full rounded-t-xl flex items-center justify-center space-x-12">
      <span>Diego Alcoba Arias prueba</span>
      <span>Gonzalo Alonso Olaiz</span>
      <span>Diego Cisneros Morales</span>
    </footer>
  </div>
</template>

<style scoped>
@import "@fortawesome/fontawesome-free/css/all.min.css";
</style>
