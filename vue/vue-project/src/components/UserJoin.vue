<template>
  <div>
    <h1> 회원가입 </h1>
  </div>
  <div class="p-4">
    <h2 class="text-xl font-bold mb-4">회원 가입</h2>
    <div class="mb-4">
      <input v-model="user.userId" placeholder="ID" class="border p-2 mr-2" /><br>
      <input type="password" v-model="user.password" placeholder="비밀번호" class="border p-2 mr-2" /><br>
      <input type="password" v-model="password2" placeholder="비밀번호 재입력" class="border p-2 mr-2" /><br>
      <input v-model="user.name" placeholder="이름" class="border p-2 mr-2" /><br>
      <input v-model="user.phone" placeholder="전화번호" class="border p-2 mr-2" /><br>
      <input v-model="user.email" placeholder="이메일" class="border p-2 mr-2" /><br>
      <input type="checkbox" v-model="agree" /> [필수] 개인정보 수집 동의<br>
      <input type="checkbox" v-model="user.maAgree" /> [선택] 마케팅 수집 동의<br>
      <input type="checkbox" v-model="user.ageAgree" /> [필수] 만 14세 미만 아동 수집 동의
    </div>
    <button @click="submitUser" class="bg-blue-500 text-white px-4 py-2 rounded">가입</button>



  </div>

</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      user: {
        userId: '',
        password: '',
        name: '',
        phone: '',
        email: '',
        maAgree: false,
        ageAgree: false,
        accessLevel: '3',
      },
      users: [],
      password2: '',
      selectedUser: null,
      agree: false
    };
  },
  methods: {
    async submitUser() {
      // 모두 입력 했는지 확인
      if (!this.user.userId || !this.user.password || !this.password2 ||
        !this.user.name || !this.user.phone || !this.user.email) {
        alert('모든 입력값을 입력해주세요.');
        return;
      }

      if (!this.user.ageAgree || !this.agree) {
        alert('필수 동의를 체크하세요.');
        return
      }

      // 비밀번호와 비밀번호 재입력 일치 확인
      if (this.user.password !== this.password2) {
        console.log("pw1 :", this.user.password)
        console.log("pw2 :", this.user.password2)
        alert('비밀번호가 일치하지 않습니다.');
        return;
      }

      // 이메일 형식 검사 (정규표현식)
      const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailPattern.test(this.user.email)) {
        alert('올바른 이메일 형식을 입력해주세요.');
        return;
      }

      // 전화번호 숫자 검사 (정규표현식)
      const phonePattern = /^[0-9]{9,15}$/; // 최소 9자리 ~ 최대 15자리 숫자
      if (!phonePattern.test(this.user.phone)) {
        alert('전화번호는 숫자만 입력해주세요. (예: 01012345678)');
        return;
      }

      try {
        // 중복 가입 확인
        const exists = await axios.get(`http://localhost:8080/users/check-id/${this.user.userId}`);
        if (exists.data.exists) {
          alert('이미 존재하는 아이디입니다.');

          return;
        }

        console.log('보내는 값:', JSON.stringify(this.user));

        //성공 시 회원가입 수행
        const response = await axios.post('http://localhost:8080/users', this.user);

        console.log('중복 계정 확인: ', JSON.stringify(exists.data.exists))

        alert('회원가입 성공');
        this.$router.push('/Login-Form');
        this.user = { // 값 초기화
          userId: '',
          password: '',
          name: '',
          phone: '',
          email: '',
          maAgree: false,
          ageAgree: false,
          accessLevel: '3'
        };
        this.password2 = '';
        this.users.push(response.data); // 목록 갱신
      } catch (error) {
        // 오류
        alert('회원가입 실패: ' + (error.response?.data?.message || error.message));
      }
    }
  }
}
</script>