<script setup>
import { RouterView } from 'vue-router'
import Navbar from './components/Navbar.vue'
import NavbarBottom from './components/NavbarBottom.vue';

</script>

<template>

  <div>
    <Navbar />
    <main class="min-h-[calc(100vh-34rem)] p-4">
      <Router-view />
    </main>
    <NavbarBottom />
  </div>
</template>

<style scoped>
main {
  min-height: calc(100vh - 34rem);
  padding-bottom: 4rem;
  /* 하단 네브바 높이 확보 */
}
</style>
