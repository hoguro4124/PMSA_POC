<template>
    <nav class="navbar navbar-expand-lg bg-body-tertiary">
        <div class="container-fluid">

            <router-link class="navbar-brand" to="/">홈</router-link>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false"
                aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <!-- 관리자 전용 메뉴 -->
                    <li class="nav-item" v-if="accessLevel === 1">
                        <router-link class="nav-link" to="/Admin-List">관리자 목록</router-link>
                    </li>
                    <li class="nav-item" v-if="accessLevel === 1">
                        <router-link class="nav-link" to="/User-List">고객 목록</router-link>
                    </li>
                    <!-- 상담사 이상 접근 가능 -->
                    <li class="nav-item" v-if="accessLevel === 2">
                        <router-link class="nav-link" to="/voc-home">VOC 페이지</router-link>
                    </li>
                    <!-- 사용자 접근 가능 -->
                    <li class="nav-item" v-if="accessLevel === 3">
                        <router-link class="nav-link" to="/user-home">사용자 페이지</router-link>
                    </li>
                    <!-- 마이 페이지 -->
                    <li class="nav-item" v-if="isLoggedIn">
                        <router-link class="nav-link" to="/mypage-auth">마이페이지</router-link>
                    </li>
                    <!-- 로그아웃 -->
                    <li class="nav-item" v-if="isLoggedIn">
                        <button @click="logout" class="btn btn-danger ms-2">로그아웃</button>
                    </li>
                </ul>
            </div>
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