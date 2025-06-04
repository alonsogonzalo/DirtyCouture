<!-- src/views/RegisterView.vue -->
<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-100 to-gray-200 px-4">
    <div class="bg-white p-8 rounded-xl shadow-xl w-full max-w-sm space-y-6">
      <h1 class="text-3xl font-extrabold text-center text-gray-900">Crear cuenta</h1>

      <form @submit.prevent="handleRegister" class="space-y-4">
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
            class="w-full bg-blue-600 text-white font-semibold py-2 rounded-lg hover:bg-blue-700 transition"
        >
          Registrarse
        </button>
        <div>
        <router-link to="/" class="w-full bg-blue-600 text-white font-semibold py-2 rounded-lg hover:bg-blue-700 transition">Volver al incio </router-link>
          </div>
      </form>

      <p class="text-sm text-center text-gray-600">
        ¿Ya tienes cuenta?
        <router-link to="/login" class="text-blue-600 hover:underline">Inicia sesión aquí</router-link>
      </p>

      <p v-if="error" class="text-red-500 text-center text-sm">{{ error }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const email = ref('')
const password = ref('')
const error = ref('')
const router = useRouter()

async function handleRegister() {
  error.value = ''
  try {
    const response = await fetch('http://localhost:8080/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email.value, password: password.value })
    })

    if (!response.ok) {
      const res = await response.json()
      error.value = res.error || 'Error en el registro'
      return
    }

    // Registro exitoso, redirige al login
    router.push('/login')
  } catch (err) {
    error.value = 'Error de red o servidor'
  }
}
</script>

<style scoped>
.register {
  max-width: 400px;
  margin: 2rem auto;
  padding: 1rem;
}
</style>
