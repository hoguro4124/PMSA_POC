<template>
    <h1>고객 로그인</h1>

    <button @click="goToBoard" class="bg-blue-500 text-white px-4 py-2 rounded">게시글 작성</button>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()

const goToBoard = () => {
    router.push('/Board')
}
</script>