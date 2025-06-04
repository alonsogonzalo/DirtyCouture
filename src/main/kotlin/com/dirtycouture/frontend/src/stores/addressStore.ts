// src/stores/addressStore.ts
import { defineStore } from 'pinia'
import api from '../services/api' // Instancia de Axios con interceptor JWT

/**
 * Tipo TypeScript para una dirección de envío
 */
export interface ShippingAddress {
    id: number
    userId: number
    fullName: string
    address: string
    city: string
    state: string
    zipCode: string
    country: string
    phoneNumber: string
    createdAt: string // ISO timestamp
}

export const useAddressStore = defineStore('address', {
    state: () => ({
        addresses: [] as ShippingAddress[],  // Array de direcciones del usuario
        selectedAddressId: null as number | null, // ID de la dirección escogida (o null)
        loading: false as boolean,
        error: '' as string
    }),
    actions: {
        /**
         * Trae todas las direcciones del usuario desde el backend.
         * GET /api/shipping/{userId}
         */
        async fetchAddresses(userId: number) {
            this.loading = true
            this.error = ''
            try {
                const response = await api.get<ShippingAddress[]>(`/api/shipping/${userId}`)
                // Aseguramos que la respuesta sea un array
                this.addresses = Array.isArray(response.data) ? response.data : []
            } catch (err: any) {
                console.error('Error fetching addresses:', err)
                this.error = err.response?.data?.error || 'Error fetching addresses'
                this.addresses = []
            } finally {
                this.loading = false
            }
        },

        /**
         * Crea una nueva dirección en el backend y, luego,
         * vuelve a recargar todas las direcciones para actualizar la lista.
         * POST /api/shipping
         */
        async addAddress(newAddress: Omit<ShippingAddress, 'id' | 'userId' | 'createdAt'>) {
            this.loading = true
            this.error = ''
            try {
                // La ruta espera un body JSON con: { fullName, address, city, state, zipCode, country, phoneNumber }
                const response = await api.post<ShippingAddress>('/api/shipping', newAddress)
                // La respuesta es el objeto recién creado con su ID, userId y createdAt
                const created: ShippingAddress = response.data
                // Insertamos esa dirección en el array para no volver a cargar todo, aunque opcionalmente podrías llamar fetch
                this.addresses.push(created)
                // Seleccionamos automáticamente la dirección recién creada
                this.selectedAddressId = created.id
            } catch (err: any) {
                console.error('Error adding address:', err)
                this.error = err.response?.data?.error || 'Error adding address'
            } finally {
                this.loading = false
            }
        },

        /**
         * Establece la dirección seleccionada por su ID
         */
        selectAddress(addressId: number) {
            this.selectedAddressId = addressId
        },

        /**
         * Reinicia el store (por ejemplo, al hacer logout)
         */
        clear() {
            this.addresses = []
            this.selectedAddressId = null
            this.error = ''
            this.loading = false
        }
    }
})
