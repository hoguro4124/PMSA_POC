<template>
  <main class="form-signin w-100 m-auto">
    <h2 class="h3 fw-bold mb-4 text-center">회원 가입</h2>
    <form @submit.prevent="submitUser" class="mb-4">
      <div class="mb-3">
        <input v-model="user.userId" placeholder="ID" class="form-control input-small" />
      </div>
      <div class="mb-3">
        <input type="password" v-model="user.password" placeholder="비밀번호" class="form-control input-small" />
      </div>
      <div class="mb-3">
        <input type="password" v-model="password2" placeholder="비밀번호 재입력" class="form-control input-small" />
      </div>
      <div class="mb-3">
        <input v-model="user.name" placeholder="이름" class="form-control input-small" />
      </div>
      <div class="mb-3">
        <input v-model="user.phone" placeholder="전화번호" class="form-control input-small" />
      </div>
      <div class="mb-3">
        <input v-model="user.email" placeholder="이메일" class="form-control input-small" />
      </div>

      <div class="form-check mb-2">
        <input type="checkbox" v-model="agree" id="agreePrivacy" class="form-check-input" />
        <label for="agreePrivacy" class="form-check-label fw-semibold">
          [필수] 개인정보 수집 동의
          <span @click="requiredtoggle" style="cursor:pointer;">
            {{ requiredAgree ? '🔺' : '🔻' }}
          </span>
        </label>
        <div v-if="requiredAgree" class="ms-3 mt-2 text-secondary small">
          1. 개인정보 수집 목적<br />
          1) 회원 관리<br />
          이용자 식별 및 본인 확인, 가입의사 확인, 불만처리 등 민원처리, 공지사항 전달, 불량회원 부정이용 방지<br />
          2) 서비스 이용 및 요금 정산<br />
          광고상품의 청약, 요금 결제, SMS 발신번호 등록/관리<br />
          2. 개인정보 수집 항목<br />
          이름, ID, 비밀번호, 이메일<br />
          3. 개인정보 보유 및 이용 기간<br />
          회원 탈퇴 시 지체없이 파기(관련 법령의 규정에 의하여 보존할 필요가 있는 경우에는 해당 기간까지 보관 후 파기)
        </div>
      </div>

      <div class="form-check mb-2">
        <input type="checkbox" v-model="user.ageAgree" id="agreeAge" class="form-check-input" />
        <label for="agreeAge" class="form-check-label fw-semibold">
          [필수] 만 14세 미만 아동 수집 동의
        </label>
      </div>

      <div class="form-check mb-2">
        <input type="checkbox" v-model="user.maAgree" id="agreeMarketing" class="form-check-input" />
        <label for="agreeMarketing" class="form-check-label fw-semibold">
          [선택] 마케팅 동의
          <span @click="thirdtoggle" style="cursor:pointer;">
            {{ thirdAgree ? '🔺' : '🔻' }}
          </span>
        </label>
        <div v-if="thirdAgree" class="ms-3 mt-2 text-secondary small">
          1. 수집이용목적<br />
          1) 상품안내, 이벤트 행사 관련 정보 안내<br />
          2. 수집 항목<br />
          1) 휴대전화번호<br />
          3. 보유 및 이용기간<br />
          1) 회원탈퇴 또는 고객 삭제 요청시 까지
        </div>
      </div>

      <button type="submit" class="btn btn-primary btn-small w-100 mt-3">
        가입
      </button>
    </form>
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
  padding: 0 12px;
  font-size: 0.875rem;
  white-space: nowrap;
}
</style>


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
      agree: false,

      requiredAgree: false,
      fourteenAgree: false,
      thirdAgree: false
    };
  },
  methods: {
    requiredtoggle() {
      this.requiredAgree = !this.requiredAgree
    },
    fourteentoggle() {
      this.fourteenAgree = !this.fourteenAgree
    },
    thirdtoggle() {
      this.thirdAgree = !this.thirdAgree
    },

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