<template>
  <div class="min-h-screen flex flex-col items-center justify-center bg-gray-100 p-6">
    <div class="bg-white shadow-lg rounded-lg p-8 w-full max-w-md">
      <h1 class="text-2xl font-bold mb-6 text-center">Configuración de usuario</h1>

      <div class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-1">Correo electrónico</label>
        <input v-model="email" type="email" class="w-full border px-3 py-2 rounded" />
      </div>

      <div class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-1">Rol</label>
        <select v-model="role" class="w-full border px-3 py-2 rounded">
          <option value="User">User</option>
          <option value="Admin">Admin</option>
        </select>
      </div>

      <button @click="saveChanges" class="w-full bg-blue-500 hover:bg-blue-600 text-white py-2 rounded">
        Guardar cambios
      </button>
    <div>
  </div>

        <router-link to="/" class="w-full bg-blue-600 text-white font-semibold py-2 rounded-lg hover:bg-blue-700 transition">Volver al incio </router-link>

      <p v-if="successMessage" class="text-green-500 mt-4 text-center">{{ successMessage }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '../stores/userStore'

const userStore = useUserStore()

const email = ref(userStore.user?.email || '')
const role = ref(userStore.user?.role || '')

const successMessage = ref('')

async function saveChanges() {
  if (!userStore.token) return

  try {
    const response = await fetch('http://localhost:8080/auth/update', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${userStore.token}`
      },
      body: JSON.stringify({
        email: email.value,
        role: role.value
      })
    })

    if (!response.ok) {
      throw new Error('Error al actualizar el usuario')
    }

    const data = await response.json()
    userStore.setUser({
      id: data.id,
      email: data.email,
      role: data.role
    })
    successMessage.value = 'Datos actualizados correctamente.'
    setTimeout(() => (successMessage.value = ''), 3000)

  } catch (error) {
    console.error(error)
    successMessage.value = 'Error al actualizar los datos.'
  }
}

</script>
