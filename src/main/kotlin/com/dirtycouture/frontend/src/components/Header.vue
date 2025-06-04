<template>
  <header class="flex justify-between items-center bg-black text-white px-6 py-4">
    <img src="../assets/logo.png" alt="DirtyCouture Logo" class="h-10" />

    <div class="flex gap-4 items-center">
      <span v-if="user.isLoggedIn">Hola otra vez, {{user.username}}</span>
      <button @click="toggleLogin" class="hover:underline">
        {{user.isLoggedIn ? 'Logout' : 'Login'}}
      </button>
      <div class="relative">
        <i class="fas fa-shopping-cart text-xl"></i>
        <span v-if="cart.itemCount > 0"
              class="absolute -top-2 -right-2 bg-red-600 text-white text-xs px-1 rounded-full">
              {{cart.itemCount}}
        </span>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import {useUserStore} from "../stores/userStore";
import {useCartStore} from "../stores/cartStore";

const user = useUserStore()
const cart = useCartStore()

const toggleLogin = () => {
  user.isLoggedIn ? user.logout() : user.login('Usuario')
}

</script>

<style scoped>
i {
  font-family: "Font Awesome 5 Free";
  font-weight: 900;
}

</style>