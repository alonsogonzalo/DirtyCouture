<template>
  <div class="max-w-3xl mx-auto p-6 space-y-6">
    <h1 class="text-3xl font-bold text-center">Seleccionar o añadir dirección de envío</h1>

    <!-- 1) Mostrar mensaje de carga o error -->
    <div v-if="addressStore.loading" class="text-center text-gray-500">
      Cargando direcciones…
    </div>
    <div v-if="addressStore.error" class="text-red-500 text-center">
      {{ addressStore.error }}
    </div>

    <!-- 2) Lista de direcciones existentes (si hay alguna) -->
    <div v-if="!addressStore.loading && addressStore.addresses.length > 0">
      <h2 class="text-xl font-semibold">Tus direcciones guardadas</h2>
      <ul class="space-y-4 mt-4">
        <li
            v-for="addr in addressStore.addresses"
            :key="addr.id"
            class="flex items-center border p-4 rounded-lg"
        >
          <input
              type="radio"
              :value="addr.id"
              v-model="addressStore.selectedAddressId"
              class="mr-4"
          />
          <div>
            <p class="font-semibold">{{ addr.fullName }}</p>
            <p>{{ addr.address }}, {{ addr.city }}, {{ addr.state }}, {{ addr.zipCode }}, {{ addr.country }}</p>
            <p class="text-sm text-gray-500">Tel: {{ addr.phoneNumber }}</p>
          </div>
        </li>
      </ul>
    </div>

    <!-- 3) Formulario para añadir nueva dirección -->
    <div class="border p-6 rounded-lg">
      <h2 class="text-xl font-semibold mb-4">Añadir nueva dirección</h2>
      <form @submit.prevent="handleAddAddress" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700">Nombre completo</label>
          <input
              v-model="form.fullName"
              type="text"
              required
              class="mt-1 block w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">Dirección</label>
          <input
              v-model="form.address"
              type="text"
              required
              class="mt-1 block w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700">Ciudad</label>
            <input
                v-model="form.city"
                type="text"
                required
                class="mt-1 block w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">Provincia/Estado</label>
            <input
                v-model="form.state"
                type="text"
                required
                class="mt-1 block w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700">Código postal</label>
            <input
                v-model="form.zipCode"
                type="text"
                required
                class="mt-1 block w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">País</label>
            <input
                v-model="form.country"
                type="text"
                required
                class="mt-1 block w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">Teléfono</label>
          <input
              v-model="form.phoneNumber"
              type="tel"
              required
              class="mt-1 block w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
        </div>

        <button
            type="submit"
            :disabled="addressStore.loading"
            class="w-full bg-green-600 text-white font-semibold py-2 rounded-lg hover:bg-green-700 disabled:opacity-50 transition"
        >
          {{ addressStore.loading ? 'Guardando...' : 'Añadir dirección' }}
        </button>
      </form>
    </div>

    <!-- 4) Botón para continuar al próximo paso (Checkout/Pago) -->
    <div class="text-right mt-6">
      <button
          @click="proceedToPayment"
          :disabled="!addressStore.selectedAddressId"
          class="px-6 py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 disabled:opacity-50 transition"
      >
        Continuar al pago
      </button>
      <p v-if="!addressStore.selectedAddressId" class="text-sm text-red-500 mt-2">
        Debes seleccionar o añadir una dirección.
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/userStore'
import { useAddressStore, ShippingAddress } from '../stores/addressStore'

const router = useRouter()
const userStore = useUserStore()
const addressStore = useAddressStore()

// Formulario reactive para añadir dirección
const form = ref<Omit<ShippingAddress, 'id' | 'userId' | 'createdAt'>>({
  fullName: '',
  address: '',
  city: '',
  state: '',
  zipCode: '',
  country: '',
  phoneNumber: ''
})

// Al montar el componente, cargamos las direcciones si el usuario está logueado
onMounted(() => {
  if (!userStore.user) {
    // Si no hay sesión, redirigir al login
    router.push('/login')
    return
  }
  // Obtener todas las direcciones del usuario
  addressStore.fetchAddresses(userStore.user.id)
})

/**
 * Al enviar el formulario, llamamos a addressStore.addAddress()
 * que automáticamente vuelve a cargar la lista y marca la recién creada.
 */
async function handleAddAddress() {
  if (!userStore.user) return
  addressStore.addAddress({
    fullName: form.value.fullName,
    address: form.value.address,
    city: form.value.city,
    state: form.value.state,
    zipCode: form.value.zipCode,
    country: form.value.country,
    phoneNumber: form.value.phoneNumber
  })

  // Limpiamos el formulario tras intentar guardar (opcional)
  form.value.fullName = ''
  form.value.address = ''
  form.value.city = ''
  form.value.state = ''
  form.value.zipCode = ''
  form.value.country = ''
  form.value.phoneNumber = ''
}

/**
 * Al pulsar “Continuar al pago”, verificamos que haya una dirección seleccionada
 * y navegamos a la siguiente vista (por ej. /checkout/payment).
 */
function proceedToPayment() {
  if (!addressStore.selectedAddressId || !userStore.user) {
    return
  }
  // Guardamos la dirección seleccionada en el store (puede usarse luego)
  // O bien pasamos como query param: ?shippingId=addressStore.selectedAddressId
  router.push({
    name: 'CheckoutPayment',
    query: { shippingId: addressStore.selectedAddressId.toString() }
  })
}
</script>

<style scoped>
/* Si quieres estilos adicionales, agrégalos aquí */
</style>
