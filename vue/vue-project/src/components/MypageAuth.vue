<template>
    <main class="form-signin w-100 m-auto">
        <h1 class="h3 fw-bold mb-4 text-center">비밀번호 재확인</h1>
        <input v-model="password" type="password" placeholder="비밀번호를 입력하세요" class="form-control input-small mb-3" />
        <button @click="verifyPassword" class="btn btn-primary btn-small w-100">
            확인
        </button>
    </main>
</template>


<style scoped>
.form-signin {
    max-width: 950px;
    padding: 2rem;
    margin: 40px auto;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 0 10px #eee;
}

.input-small {
    height: 38px;
    padding: 0.375rem 0.75rem;
    font-size: 1rem;
    border-radius: 6px;
}

.btn-small {
    height: 38px;
    padding: 0.375rem 0.75rem;
    font-size: 0.875rem;
    white-space: nowrap;
}
</style>

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
                    alert('비밀번호가 일치하지 않습니다.');
                }
            } catch (err) {
                alert('비밀번호가 일치하지 않습니다.');
            }
        }
    }
}
</script>
