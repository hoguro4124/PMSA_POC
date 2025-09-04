<template>
    <nav class="bg-gray-800 text-white p-4 flex justify-between items-center">
        <div class="flex space-x-4">
            <router-link to="/" class="hover:underline">홈</router-link>

            <!-- 관리자 전용 메뉴 -->
            <router-link v-if="accessLevel === 1" to="/admin-List" class="hover:underline">
                관리자 목록
            </router-link>
            <router-link v-if="accessLevel === 1" to="/User-List" class="hover:underline">
                사용자 목록
            </router-link>

            <!-- 상담사 이상 접근 가능 -->
            <router-link v-if="accessLevel === 2" to="/voc-home" class="hover:underline">
                VOC 페이지
            </router-link>

            <!-- 사용자 접근 가능 -->
            <router-link v-if="accessLevel === 3" to="/user-home" class="hover:underline">
                사용자 페이지
            </router-link>

            <!-- 마이 페이지 -->
            <router-link v-if="isLoggedIn" to="/mypage-auth" class="hover:underline">
                마이페이지
            </router-link>


            <!-- 로그아웃 -->
            <button v-if="isLoggedIn" @click="logout" class="bg-red-500 hover:bg-red-600 px-4 py-1 rounded">
                로그아웃
            </button>

        </div>
    </nav>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref, computed, onMounted } from 'vue'

const router = useRouter()

const token = ref(localStorage.getItem('token'))
const accessLevel = ref(parseInt(localStorage.getItem('accessLevel')) || 99) // 99 = 비로그인 사용자

const isLoggedIn = computed(() => !!token.value)

const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('accessLevel')
    token.value = null
    accessLevel.value = 99
    router.push('/')
}





</script>

<style scoped>
nav a {
    margin-right: 1rem;
}
</style>