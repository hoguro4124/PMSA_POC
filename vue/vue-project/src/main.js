
import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import 'bootstrap/dist/css/bootstrap.min.css'

// ▼▼▼ [추가 1] axios 라이브러리 가져오기 ▼▼▼
import axios from 'axios'

// ▼▼▼ [추가 2] 모든 요청을 가로채서 토큰을 심는 설정 (여기가 핵심!) ▼▼▼
axios.interceptors.request.use(config => {
    // 1. 브라우저 저장소에서 토큰 꺼내기
    const token = localStorage.getItem('token');

    if (token) {
        // 2. 토큰이 있으면 헤더에 'Bearer 토큰' 형식으로 추가
        config.headers.Authorization = `Bearer ${token}`;

        // [디버깅용] F12 콘솔에서 이 로그가 보이면 성공입니다.
        console.log(">>> [Global] 토큰 자동 탑재 완료:", token.substring(0, 10) + "...");
    } else {
        console.log(">>> [Global] 토큰 없음 (비로그인 상태)");
    }

    return config;
}, error => {
    return Promise.reject(error);
});
// ▲▲▲ [설정 끝] ▲▲▲


const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')