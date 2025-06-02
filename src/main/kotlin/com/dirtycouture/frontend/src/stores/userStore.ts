import {defineStore} from "pinia";

export const useUserStore = defineStore ('user', {
    state: () => ({
        username: 'Invitado',
        isLoggedIn: false,
    }),
    actions: {
        login(name: string) {
            this.username = name;
            this.isLoggedIn = true;
        },
        logout() {
            this.username = 'Invitado';
            this.isLoggedIn = false;
        },
    },
});