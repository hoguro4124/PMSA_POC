
import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import 'bootstrap/dist/css/bootstrap.min.css'

import axios from 'axios'

axios.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
        console.log(">>> [Global] 토큰 자동 탑재 완료:", token.substring(0, 10) + "...");
    } else {
        console.log(">>> [Global] 토큰 없음 (비로그인 상태)");
    }

    return config;
}, error => {
    return Promise.reject(error);
});



const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')