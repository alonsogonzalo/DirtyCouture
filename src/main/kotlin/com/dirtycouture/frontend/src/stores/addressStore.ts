// src/stores/addressStore.ts
import { defineStore } from 'pinia'
import api from '../services/api' // Axios apuntando a http://localhost:8080

/**
 * Interfaz TS para representar una dirección tal como viene del backend.
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
    createdAt: string // ISO string
}

export const useAddressStore = defineStore('address', {
    state: () => ({
        addresses: [] as ShippingAddress[],
        selectedAddressId: null as number | null,
        loading: false as boolean,
        error: '' as string
    }),
    actions: {
        /**
         * GET /api/shipping/{userId}
         */
        async fetchAddresses(userId: number) {
            this.loading = true
            this.error = ''
            try {
                const response = await api.get<ShippingAddress[]>(`/shipping/${userId}`)
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
         * POST /api/shipping
         */
        async addAddress(newAddress: Omit<ShippingAddress, 'id' | 'userId' | 'createdAt'>) {
            this.loading = true
            this.error = ''
            try {
                const response = await api.post<ShippingAddress>('/shipping', newAddress)
                const created: ShippingAddress = response.data
                this.addresses.push(created)
                this.selectedAddressId = created.id
            } catch (err: any) {
                if (err.response) {
                    console.error('Error adding address:', err.response.status, err.response.data)
                } else {
                    console.error('Error adding address:', err)
                }
                this.error = err.response?.data?.error || 'Error adding address'
            } finally {
                this.loading = false
            }
        },

        /**
         * DELETE /api/shipping/{addressId}
         */
        async deleteAddress(addressId: number, userId: number) {
            this.loading = true
            this.error = ''
            try {
                await api.delete(`/shipping/${addressId}`)
                // Después de borrar, recargamos la lista de direcciones
                await this.fetchAddresses(userId)
            } catch (err: any) {
                console.error('Error deleting address:', err)
                this.error = err.response?.data?.error || 'Error deleting address'
            } finally {
                this.loading = false
            }
        },

        selectAddress(addressId: number) {
            this.selectedAddressId = addressId
        },

        clear() {
            this.addresses = []
            this.selectedAddressId = null
            this.error = ''
            this.loading = false
        }
    }
})
