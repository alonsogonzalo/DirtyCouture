<!-- src/views/RegisterView.vue -->
<template>
  <div class="register">
    <h1>Crear cuenta</h1>
    <form @submit.prevent="handleRegister">
      <label>Email:</label>
      <input v-model="email" type="email" required />

      <label>Contraseña:</label>
      <input v-model="password" type="password" required />

      <button type="submit">Registrarse</button>

      <p v-if="error" style="color: red">{{ error }}</p>
    </form>
    <p>
      ¿Ya tienes cuenta?
      <router-link to="/login">Inicia sesión aquí</router-link>
    </p>
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
