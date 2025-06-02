import {defineStore} from "pinia";

export const useCartStore = defineStore('cart', {
    state: () => ({
        items: [] as Array<{id: number; name: String; quantity:number}>,
    }),
    getters: {
        itemCount: (state) => state.items.reduce((acc, item) => acc + item.quantity, 0),
    },
})