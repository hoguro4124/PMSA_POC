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
        <button type="button" class="btn btn-link p-0" @click="goToIdInquiry">ID 찾기</button>
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
      users: [],
    };
  },
  methods: {
    async submitLogin() {
      // 모두 입력 했는지 확인
      if (!this.user.userId || !this.user.password) {
        alert('모든 입력값을 입력해주세요.');
        return;
      }

      try {
        // 성공 시 로그인 수행
        const response = await axios.post('http://localhost:8080/users/login', this.user);

        const userData = response.data.user;
        const token = response.data.token;
        console.log(JSON.stringify(userData))

        // 토큰과 권한을 localStorage에 저장
        localStorage.setItem('token', token);
        localStorage.setItem('accessLevel', userData.accessLevel);
        localStorage.setItem('user', userData.id);
        localStorage.setItem('userId', userData.userId);




        console.log("입력값 : ", this.user)
        console.log("회신값 : ", response.data.user)
        console.log("토큰 : ", response.data.token)
        alert('로그인 성공');
        this.users.push(response.data);
        window.location.reload();


        if (response.data.user.accessLevel === 1) {
          this.$router.push('/Admin-Home');
        } else if (response.data.user.accessLevel === 2) {
          this.$router.push('/Voc-Home');
        } else if (response.data.user.accessLevel === 3) {
          this.$router.push('/User-Home');
        } else {
          console.log('오류');
        }

      } catch (error) {
        // 오류
        alert('로그인 실패: ' + error);
      }
    }
  }
}
</script>
