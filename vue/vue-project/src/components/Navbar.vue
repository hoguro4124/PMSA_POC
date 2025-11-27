<template>
    <nav class="custom-navbar navbar-expand-lg navbar-light bg-light shadow-sm">
        <div class="container-fluid">


            <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false"
                aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <router-link class="nav-link" to="/">홈</router-link>
                    </li>

                    <template v-if="accessLevel === 1 || accessLevel === 2">
                        <li class="nav-item">
                            <router-link class="nav-link" to="/AdList">광고 목록</router-link>
                        </li>
                        <li class="nav-item">
                            <router-link class="nav-link" to="/BoardList">문의 사항 목록</router-link>
                        </li>
                        <li class="nav-item">
                            <router-link class="nav-link" to="/User-List">고객 목록</router-link>
                        </li>
                    </template>

                    <template v-if="accessLevel === 1">
                        <li class="nav-item">
                            <router-link class="nav-link" to="/Admin-List">관리자 목록</router-link>
                        </li>
                        <li class="nav-item">
                            <router-link class="nav-link" to="/Access-Logs">접속 기록</router-link>
                        </li>
                        <li class="nav-item">
                            <router-link class="nav-link" to="/work-logs">작업 기록</router-link>
                        </li>
                    </template>

                    <template v-if="accessLevel === 3">
                        <li class="nav-item">
                            <router-link class="nav-link" to="/AdRegi">광고 등록</router-link>
                        </li>
                        <li class="nav-item">
                            <router-link class="nav-link" to="/board">문의 사항 등록</router-link>
                        </li>
                        <li class="nav-item">
                            <router-link class="nav-link" to="/UserBoardList">나의 문의 사항</router-link>
                        </li>
                    </template>

                    <template v-if="isLoggedIn">
                        <li class="nav-item">
                            <router-link class="nav-link" to="/mypage-auth">마이페이지</router-link>
                        </li>
                        <li class="nav-item">
                            <router-link to="#" class="nav-link text-danger fw-bold" @click.prevent="logout"
                                style="cursor: pointer;">
                                로그아웃
                            </router-link>
                        </li>



                    </template>
                </ul>
            </div>
        </div>
    </nav>
</template>

<style scoped>
.custom-navbar {
    max-width: 950px;
    margin-left: auto;
    margin-right: auto;
    width: 100%;
    border-radius: 12px;
    /* 둥글기 조절 (원하는 px값으로 변경 가능) */
}

.custom-navbar .navbar-nav .nav-link {
    padding-left: 1rem;
    /* 기본값보다 더 넉넉한 좌측 패딩 */
    padding-right: 1rem;
    /* 기본값보다 더 넉넉한 우측 패딩 */
}

.custom-navbar .navbar-brand {
    margin-right: 2rem;
    /* 홈 메뉴와 이후 메뉴 사이의 좌측 여백 */
}

.custom-navbar {
    margin-bottom: 2rem;
    /* 원하는 여백 크기 조절 */
}
</style>

<script setup>
import { useRouter } from 'vue-router'
import { ref, computed, onMounted, onUnmounted } from 'vue'

const router = useRouter()

// 상태 변수 선언
const token = ref(localStorage.getItem('token'))
const accessLevel = ref(99) // 기본값 비로그인

const isLoggedIn = computed(() => !!token.value)

// [핵심] 권한 상태를 강제로 업데이트하는 함수
const checkAuthStatus = () => {
    const storedToken = localStorage.getItem('token')
    const storedLevel = localStorage.getItem('accessLevel')

    token.value = storedToken
    // 숫자로 변환하여 저장 (없으면 99)
    accessLevel.value = storedLevel ? parseInt(storedLevel) : 99
}

let intervalId = null;

onMounted(() => {
    checkAuthStatus(); // 1. 켜지자마자 확인

    // 2. 0.5초마다 계속 확인 (반응성 강제 적용)
    intervalId = setInterval(checkAuthStatus, 500);
})

onUnmounted(() => {
    if (intervalId) clearInterval(intervalId);
})

const logout = () => {
    localStorage.clear();
    checkAuthStatus();
    router.push('/');
    // 확실한 초기화를 위해 새로고침
    setTimeout(() => window.location.reload(), 100);
}
</script>
