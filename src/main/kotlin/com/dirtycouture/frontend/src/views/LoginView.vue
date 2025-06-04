<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-100 to-gray-200 px-4">
    <div class="bg-white p-8 rounded-xl shadow-xl w-full max-w-sm space-y-6">
      <h1 class="text-3xl font-extrabold text-center text-gray-900">Iniciar sesión</h1>

      <form @submit.prevent="handleLogin" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700">Email</label>
          <input
              v-model="email"
              type="email"
              required
              class="mt-1 block w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">Contraseña</label>
          <input
              v-model="password"
              type="password"
              required
              class="mt-1 block w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
        </div>

        <button
            type="submit"
            :disabled="loading"
            class="w-full bg-blue-600 text-white font-semibold py-2 rounded-lg hover:bg-blue-700 transition disabled:opacity-50"
        >
          {{ loading ? 'Entrando...' : 'Entrar' }}
        </button>

        <router-link
            to="/"
            class="block w-full text-center bg-gray-300 text-gray-800 font-semibold py-2 rounded-lg hover:bg-gray-400 transition"
        >
          Volver al inicio
        </router-link>
      </form>

      <p class="text-sm text-center text-gray-600">
        ¿No tienes cuenta?
        <router-link to="/register" class="text-blue-600 hover:underline">Regístrate aquí</router-link>
      </p>

      <p v-if="error" class="text-red-500 text-center text-sm">{{ error }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/userStore'

const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)
const router = useRouter()
const userStore = useUserStore()

const handleLogin = async () => {
  error.value = ''
  loading.value = true

  try {
    const response = await fetch('http://localhost:8080/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email.value, password: password.value })
    })

    const result = await response.json()
    console.log('Login response:', result)

    if (!response.ok) {
      throw new Error(result.error || 'Login fallido')
    }

    userStore.setUser(result.user)
    userStore.setToken(result.token)

    router.push('/') // Redirigir al home, o a /dashboard si lo prefieres
  } catch (err: any) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
input {
  border: 1px solid #ccc;
}
</style>
