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

      <div v-if="addressStore.loading" class="text-center text-gray-500 mb-4">
        Cargando direcciones…
      </div>
      <div v-if="addressStore.error" class="text-red-500 text-center mb-4">
        {{ addressStore.error }}
      </div>

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

      <div v-if="!addressStore.loading && addressStore.addresses.length === 0" class="text-gray-500">
        No tienes direcciones guardadas.
      </div>
    </div>

    <!-- Bloque de pedidos -->
    <div class="bg-white shadow-lg rounded-lg p-8 w-full max-w-3xl">
      <h2 class="text-xl font-semibold mb-4">Tus pedidos</h2>

      <div v-if="orders.length === 0" class="text-gray-500 text-center">No tienes pedidos realizados.</div>

      <div v-for="order in orders" :key="order.id" class="border rounded-lg mb-4">
        <div class="p-4 flex justify-between items-center cursor-pointer" @click="toggleOrder(order.id)">
          <div>
            <p><strong>Pedido #{{ order.id }}</strong> – Total: €{{ order.total.toFixed(2) }}</p>
            <p class="text-sm">Estado: {{ mapStatus(order.status) }} – Pago: {{ mapPayment(order.paymentStatus) }}</p>
            <p class="text-xs text-gray-400">Fecha: {{ formatDate(order.createdAt) }}</p>
          </div>
          <span>{{ expandedOrderId === order.id ? '▲' : '▼' }}</span>
        </div>

        <div v-if="expandedOrderId === order.id" class="px-4 pb-4">
          <div v-for="item in order.items" :key="item.variantId" class="flex items-center py-2 border-t">
            <img :src="item.imageUrl" alt="item" class="w-16 h-16 object-cover rounded mr-4" />
            <div>
              <p class="font-semibold">{{ item.productName }}</p>
              <p class="text-sm">Talla: {{ item.size }}, Color: {{ item.color }}</p>
              <p class="text-sm">Cantidad: {{ item.quantity }} – Precio: €{{ item.price.toFixed(2) }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/userStore'
import { useAddressStore } from '../stores/addressStore'
import axios from 'axios'

const router = useRouter()
const userStore = useUserStore()
const addressStore = useAddressStore()

const email = ref('')
const role = ref('User')
const updatingUser = ref(false)
const successMessage = ref('')

const orders = ref<any[]>([])
const expandedOrderId = ref<number | null>(null)

const user = computed(() => userStore.user)

onMounted(async () => {
  if (!userStore.user) {
    router.push('/login')
    return
  }
  email.value = userStore.user.email
  role.value = userStore.user.role

  addressStore.fetchAddresses(userStore.user.id)
  await fetchOrders()
})

async function handleUpdateUser() {
  if (!userStore.user) return
  updatingUser.value = true
  successMessage.value = ''

  try {
    const response = await fetch(`http://localhost:8080/api/users/${userStore.user.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${userStore.token}`
      },
      body: JSON.stringify({ email: email.value, role: role.value })
    })

    const result = await response.json()
    if (!response.ok) throw new Error(result.error || 'Error al actualizar usuario')

    userStore.setUser({ id: userStore.user.id, email: result.email, role: result.role })
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

async function fetchOrders() {
  try {
    const res = await axios.get('http://localhost:8080/api/orders/user', {
      headers: {
        Authorization: `Bearer ${userStore.token}`
      }
    })
    orders.value = res.data
  } catch (err) {
    console.error('Error al cargar pedidos:', err)
  }
}

function toggleOrder(id: number) {
  expandedOrderId.value = expandedOrderId.value === id ? null : id
}

function formatDate(iso: string): string {
  try {
    const d = new Date(iso)
    return `${d.getDate().toString().padStart(2, '0')}/${(d.getMonth() + 1).toString().padStart(2, '0')}/${d.getFullYear()} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
  } catch {
    return iso
  }
}

function confirmDelete(addressId: number) {
  if (!userStore.user) return
  const ok = confirm('¿Estás seguro de que deseas borrar esta dirección?')
  if (!ok) return
  addressStore.deleteAddress(addressId, userStore.user.id)
}

function mapStatus(code: number): string {
  return ['Pendiente', 'Pagado', 'Enviado', 'Cancelado'][code] ?? 'Desconocido'
}

function mapPayment(status: string): string {
  return status === 'completed' ? 'Completado' : 'Pendiente'
}
</script>

<style scoped>
.min-h-screen {
  min-height: calc(100vh - 2rem);
}
</style>
