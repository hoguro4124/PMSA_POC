<script setup>
import LoginForm from '../components/LoginForm.vue';

import { useRouter } from 'vue-router'
import { ref, computed, onMounted } from 'vue'

const router = useRouter()
const token = ref(localStorage.getItem('token'))
const accessLevel = ref(parseInt(localStorage.getItem('accessLevel')) || 99) // 99 = 비로그인 사용자

onMounted(() => {
  if (token.value) {
    if (accessLevel.value === 1) {
      router.replace('/admin-home')
    } else if (accessLevel.value === 2) {
      router.replace('/voc-home')
    } else if (accessLevel.value === 3) {
      router.replace('/user-home')
    }
  }
})
</script>

<template>
  <main>
    <div>
      <h1>개인정보처리시스템 진단 역량강화 POC 홈페이지</h1>
      <br>
      <br>
      <br>
      <h1>개인정보취급방침</h1>
    </div>
    <div>
      <LoginForm />
    </div>
  </main>
</template>
