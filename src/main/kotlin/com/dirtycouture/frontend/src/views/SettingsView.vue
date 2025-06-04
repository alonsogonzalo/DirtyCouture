<template>
  <div class="min-h-screen flex flex-col items-center bg-gray-100 p-6 space-y-8">
    <!-- Contenedor principal -->
    <div class="bg-white shadow-lg rounded-lg p-8 w-full max-w-md">
      <!-- Título y formulario de usuario -->
      <h1 class="text-2xl font-bold mb-6 text-center">Configuración de usuario</h1>
      <form @submit.prevent="handleUpdateUser" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Correo electrónico</label>
          <input
              v-model="email"
              type="email"
              required
              class="w-full border px-3 py-2 rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Rol</label>
          <select
              v-model="role"
              required
              class="w-full border px-3 py-2 rounded focus:outline-none focus:ring-2 focus:ring-blue-400"
          >
            <option value="User">User</option>
            <option value="Admin">Admin</option>
          </select>
        </div>

        <button
            type="submit"
            :disabled="updatingUser"
            class="w-full bg-green-600 text-white font-semibold py-2 rounded-lg hover:bg-green-700 disabled:opacity-50 transition"
        >
          {{ updatingUser ? 'Guardando...' : 'Guardar cambios' }}
        </button>

        <p v-if="successMessage" class="text-green-600 text-sm text-center mt-2">
          {{ successMessage }}
        </p>
      </form>
    </div>

    <!-- Bloque de direcciones -->
    <div class="bg-white shadow-lg rounded-lg p-8 w-full max-w-3xl">
      <h2 class="text-xl font-semibold mb-4">Tus direcciones guardadas</h2>

      <!-- Estado de carga / error -->
      <div v-if="addressStore.loading" class="text-center text-gray-500 mb-4">
        Cargando direcciones…
      </div>
      <div v-if="addressStore.error" class="text-red-500 text-center mb-4">
        {{ addressStore.error }}
      </div>

      <!-- Lista de direcciones -->
      <ul v-if="!addressStore.loading && addressStore.addresses.length > 0" class="space-y-4">
        <li
            v-for="addr in addressStore.addresses"
            :key="addr.id"
            class="flex items-center justify-between border p-4 rounded-lg"
        >
          <div>
            <p class="font-semibold">{{ addr.fullName }}</p>
            <p>{{ addr.address }}, {{ addr.city }}, {{ addr.state }}, {{ addr.zipCode }}, {{ addr.country }}</p>
            <p class="text-sm text-gray-500">Tel: {{ addr.phoneNumber }}</p>
            <p class="text-xs text-gray-400 mt-1">Guardada: {{ formatDate(addr.createdAt) }}</p>
          </div>
          <button
              @click="confirmDelete(addr.id)"
              class="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"
          >
            Borrar
          </button>
        </li>
      </ul>

      <!-- Mensaje si no hay direcciones -->
      <div v-if="!addressStore.loading && addressStore.addresses.length === 0" class="text-gray-500">
        No tienes direcciones guardadas.
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/userStore'
import { useAddressStore } from '../stores/addressStore'

// Instanciamos los stores
const router = useRouter()
const userStore = useUserStore()
const addressStore = useAddressStore()

// Campos para editar usuario
const email = ref('')
const role = ref('User')
const updatingUser = ref(false)
const successMessage = ref('')

// Obtenemos el usuario actual de userStore
const user = computed(() => userStore.user)

// Al montar, rellenamos los campos de usuario y cargamos direcciones
onMounted(() => {
  if (!userStore.user) {
    // Si no hay usuario logueado, redirigimos al login
    router.push('/login')
    return
  }
  // Inicializamos los campos de usuario
  email.value = userStore.user.email
  role.value = userStore.user.role

  // Cargamos direcciones para este usuario
  addressStore.fetchAddresses(userStore.user.id)
})

/**
 * Función que se ejecuta al enviar el formulario de usuario:
 * Llama a PUT /api/users/{userId} para actualizar email/role.
 * Luego actualiza userStore y muestra un mensaje de éxito.
 */
async function handleUpdateUser() {
  if (!userStore.user) return
  updatingUser.value = true
  successMessage.value = ''

  try {
    // Suponemos que existe en el backend un endpoint:
    // PUT /api/users/{userId} { email, role }
    const response = await fetch(`http://localhost:8080/api/users/${userStore.user.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${userStore.token}`
      },
      body: JSON.stringify({
        email: email.value,
        role: role.value
      })
    })

    const result = await response.json()
    if (!response.ok) {
      throw new Error(result.error || 'Error al actualizar usuario')
    }

    // Actualizamos el store con los datos nuevos
    userStore.setUser({
      id: userStore.user.id,
      email: result.email,
      role: result.role
    })

    successMessage.value = 'Datos actualizados correctamente.'
    setTimeout(() => (successMessage.value = ''), 3000)
  } catch (err: any) {
    console.error(err)
    successMessage.value = 'Error al actualizar los datos.'
    setTimeout(() => (successMessage.value = ''), 3000)
  } finally {
    updatingUser.value = false
  }
}

/**
 * Función que formatea una fecha ISO para mostrar día/mes/año y hora.
 */
function formatDate(isoString: string): string {
  try {
    const date = new Date(isoString)
    const d = date.getDate().toString().padStart(2, '0')
    const m = (date.getMonth() + 1).toString().padStart(2, '0')
    const y = date.getFullYear()
    const hh = date.getHours().toString().padStart(2, '0')
    const mm = date.getMinutes().toString().padStart(2, '0')
    return `${d}/${m}/${y} ${hh}:${mm}`
  } catch {
    return isoString
  }
}

/**
 * Pide confirmación y luego invoca a addressStore.deleteAddress.
 */
async function confirmDelete(addressId: number) {
  if (!userStore.user) return
  const ok = confirm('¿Estás seguro de que deseas borrar esta dirección?')
  if (!ok) return

  await addressStore.deleteAddress(addressId, userStore.user.id)
}
</script>

<style scoped>
/* Ajustes menores para alinear y espaciar */
.min-h-screen {
  min-height: calc(100vh - 2rem);
}
</style>
