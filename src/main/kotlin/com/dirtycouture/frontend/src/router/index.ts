// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import LoginView from '../views/LoginView.vue'
import DashboardView from '../views/DashboardView.vue'
import RegisterView from '../views/RegisterView.vue'
import { useUserStore } from '../stores/userStore'
import SettingsView from "../views/SettingsView.vue";
import CartView from "../views/CartView.vue";
import ShippingAddressView from "../views/ShippingAddressView.vue";

const routes = [
    {
        path: '/',
        name: 'home',
        component: Home
    },
    {
        path: '/login',
        name: 'login',
        component: LoginView
    },
    {
        path: '/dashboard',
        name: 'dashboard',
        component: DashboardView,
        meta: { requiresAuth: true }
    },
    {
        path: '/register',
        name: 'register',
        component: RegisterView
    },
    {
        path: '/settings',
        name: 'settings',
        component: SettingsView
    },
    {
        path: '/cart',
        name: 'cart',
        component: CartView
    },
    {
        path: '/checkout/shippingAddress',
        name: 'shippingAddress',
        component: ShippingAddressView
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// Protege rutas con meta.requiresAuth
router.beforeEach((to, from, next) => {
    const userStore = useUserStore()

    // Cargar desde localStorage si es necesario
    if (!userStore.token) {
        userStore.initializeFromStorage()
    }

    if (to.meta.requiresAuth && !userStore.token) {
        next('/login')
    } else {
        next()
    }
})

export default router
