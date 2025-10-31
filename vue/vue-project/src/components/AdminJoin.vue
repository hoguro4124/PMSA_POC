<template>
    <div class="p-4">
        <h2 class="text-xl font-bold mb-4">관리자 계정 생성</h2>

        <div class="mb-4">
            * <input v-model="user.userId" placeholder="ID" class="border p-2 mr-2" /><br>
            * <input type="password" v-model="user.password" placeholder="비밀번호" class="border p-2 mr-2" /><br>
            * <input type="password" v-model="password2" placeholder="비밀번호 재입력" class="border p-2 mr-2" /><br>
            * <input v-model="user.name" placeholder="이름" class="border p-2 mr-2" /><br>
            * <input v-model="user.phone" placeholder="전화번호" class="border p-2 mr-2" /><br>
            * <input v-model="user.email" placeholder="이메일" class="border p-2 mr-2" /><br>
            *
            <select v-model="user.accessLevel" class="border p-2 mr-2">
                <option value="1">최고관리자</option>
                <option value="2">상담사</option>
            </select>
            <br>
        </div>

        <!-- 생성  버튼 -->
        <button @click="submitUser" class="bg-blue-500 text-white px-4 py-2 rounded mr-2">
            저장
        </button>

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
                accessLevel: '2',
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
                this.$router.push('/Admin-List');
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
