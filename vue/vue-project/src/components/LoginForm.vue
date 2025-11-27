<template>
  <main class="form-signin w-100 m-auto">
    <h1 class="h3 mb-3 fw-normal text-center">로그인</h1>

    <form @submit.prevent="submitLogin">
      <div class="form-floating mb-3">
        <input v-model="user.userId" type="text" class="form-control" id="floatingUserId" placeholder="아이디" required />
        <label for="floatingUserId">아이디</label>
      </div>
      <div class="form-floating mb-3">
        <input v-model="user.password" type="password" class="form-control" id="floatingPassword" placeholder="비밀번호"
          required />
        <label for="floatingPassword">비밀번호</label>
      </div>

      <button type="submit" class="w-100 btn btn-lg btn-primary">
        로그인
      </button>

      <div class="d-flex justify-content-between mt-3">
        <button type="button" class="btn btn-link p-0" @click="goToUserJoin">회원가입</button>

      </div>
    </form>
  </main>
</template>

<style scoped>
.form-signin {
  max-width: 330px;
  padding: 15px;
  margin: auto;
  margin-top: 50px;

}
</style>
<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
const goToUserJoin = () => {
  router.push('/user-join')
}
const goToIdInquiry = () => {
  router.push('/Id-Inquiry')
}
</script>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      user: {
        userId: '',
        password: '',
      },
    };
  },
  methods: {
    goToUserJoin() {
      this.$router.push('/user-join');
    },
    goToIdInquiry() {
      this.$router.push('/Id-Inquiry');
    },
    async submitLogin() {
      if (!this.user.userId || !this.user.password) {
        alert('모든 입력값을 입력해주세요.');
        return;
      }

      try {
        const response = await axios.post('http://localhost:8080/users/login', this.user);

        const userData = response.data.user;
        const token = response.data.token;
        const accessLevel = parseInt(userData.accessLevel);

        // 1. 정보 저장
        localStorage.setItem('token', token);
        localStorage.setItem('accessLevel', accessLevel);
        localStorage.setItem('user', JSON.stringify(userData.id));
        localStorage.setItem('userId', userData.userId);

        alert('로그인 성공');


        if (accessLevel === 1) {
          window.location.href = '/Admin-Home';
        } else if (accessLevel === 2) {
          window.location.href = '/Voc-Home';
        } else if (accessLevel === 3) {
          window.location.href = '/User-Home';
        } else {
          window.location.href = '/';
        }


      } catch (error) {
        console.error(error);
        const msg = error.response?.data || '로그인 실패';
        alert(msg);
      }
    }
  }
}
</script>
