<template>
    <div class="p-6 max-w-md mx-auto">
        <h2 class="text-xl font-bold mb-4">비밀번호 재확인</h2>
        <input v-model="password" type="password" placeholder="비밀번호를 입력하세요" class="border p-2 w-full mb-4" />
        <button @click="verifyPassword" class="bg-blue-500 text-white px-4 py-2 rounded">
            확인
        </button>
        <p v-if="error" class="text-red-500 mt-2">{{ error }}</p>
    </div>
</template>

<script>
import axios from 'axios'

export default {
    data() {
        return {
            password: '',
            error: ''
        }
    },
    methods: {
        async verifyPassword() {
            const userInfo = localStorage.getItem('userId');
            const id = JSON.parse(localStorage.getItem('user'));

            console.log(userInfo)
            console.log(id)

            try {
                const response = await axios.post('http://localhost:8080/users/login', {
                    userId: userInfo,
                    password: this.password,
                });

                // 로그인 성공 -> 마이페이지 이동
                if (response.data && response.data.token) {
                    this.$router.push(`/My/${response.data.user.id}`);
                } else {
                    this.error = '비밀번호가 일치하지 않습니다.';
                }
            } catch (err) {
                this.error = '비밀번호 확인 실패: ' + (err.response?.data || err.message);
            }
        }
    }
}
</script>
