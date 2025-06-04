
import axios from 'axios'
import { useUserStore } from '../stores/userStore'

const api = axios.create({
    baseURL: '/',
})

api.interceptors.request.use(config => {
    const userStore = useUserStore()
    if (userStore.token) {
        config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
})

export default api
