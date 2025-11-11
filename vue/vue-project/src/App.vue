<script setup>
import { RouterView } from 'vue-router'
import Navbar from './components/Navbar.vue'
import NavbarBottom from './components/NavbarBottom.vue';

</script>

<template>
  <main class="min-vh-100 d-flex align-items-center justify-content-center">
    <div class="form-signin">
      <Navbar />
      <Router-view />
      <NavbarBottom />
    </div>
  </main>
</template>

<style scoped>
.form-signin {
  max-width: auto;
  width: 100%;
  margin: auto;
}

main {
  padding: 1rem;
  background-color: #f0f2f5;
  min-height: 100vh;
}
</style>
