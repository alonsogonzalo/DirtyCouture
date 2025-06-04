<!-- src/views/DashboardView.vue -->
<template>
  <div class="dashboard">
    <h1>Welcome, {{ user?.email }}!</h1>
    <p>You are logged in as <strong>{{ user?.role }}</strong>.</p>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '../stores/userStore'

const userStore = useUserStore()
const user = userStore.user
</script>

<style scoped>
.dashboard {
  text-align: center;
  padding: 2rem;
}
</style>
