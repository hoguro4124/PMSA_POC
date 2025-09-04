<template>
    <h1>고객 로그인</h1>
    <router-link class="bg-blue-500 text-white px-4 py-2 rounded" to="/mypage-auth">
        마이페이지
    </router-link>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>