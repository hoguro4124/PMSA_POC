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
      <h1 class="text-center">개인정보처리시스템 진단 역량강화 POC 홈페이지</h1>
      <br>
    </div>
    <div>
      <LoginForm />
    </div>
  </main>
</template>

<style scoped>
/* 부트스트랩 기본 스타일이 적용되므로 추가 커스터마이징은 선택 */
.form-signin {
  max-width: 330px;
  padding: 15px;
  margin: auto;
}
</style>