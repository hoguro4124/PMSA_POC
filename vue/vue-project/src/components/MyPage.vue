<template>
    <div class="p-4">
        <h2 class="text-xl font-bold mb-4">사용자 상세 정보</h2>

        <div v-if="user">
            <div>
                <div class="mb-2">
                    <label class="font-semibold">ID: </label>
                    <span>{{ user.userId }}</span>
                </div>

                <div v-if="editMode" class="mb-2">
                    <label class="font-semibold">비밀번호: </label>
                    <input type="password" v-model="user.password" class="border p-1 ml-2" placeholder="새 비밀번호 입력" />
                </div>

                <div v-if="editMode" class="mb-2">
                    <label class="font-semibold">비밀번호 재입력: </label>

                    <input type="password" v-model="password2" class="border p-1 ml-2" placeholder="새 비밀번호 재입력" />
                </div>

                <div class="mb-2">
                    <label class="font-semibold">이름: </label>
                    <span v-if="!editMode">{{ user.name }}</span>
                    <input v-else v-model="user.name" class="border p-1 ml-2" />
                </div>

                <div class="mb-2">
                    <label class="font-semibold">전화번호: </label>
                    <span v-if="!editMode">{{ user.phone }}</span>
                    <input v-else v-model="user.phone" class="border p-1 ml-2" />
                </div>

                <div class="mb-2">
                    <label class="font-semibold">이메일: </label>
                    <span v-if="!editMode">{{ user.email }}</span>
                    <input v-else v-model="user.email" class="border p-1 ml-2" />
                </div>
                <div class="mb-2">
                    <label class="font-semibold">권한: </label>
                    <span>{{ formatAccessLevel(user.accessLevel) }}</span>
                </div>
            </div>

            <div v-if="user.accessLevel == '3'">
                <div>
                    <br>
                    <h3>동의</h3>
                </div>

                <div class="mb-2">
                    <label class="font-semibold">마케팅 동의: </label>
                    <span v-if="!editMode">{{ user.maAgree ? '동의' : '미동의' }}</span>

                    <!-- 수정 모드일 때 스위치 보여줌 -->
                    <div v-else>
                        <label class="inline-flex items-center cursor-pointer">
                            <input type="checkbox" v-model="user.maAgree" class="sr-only peer" />
                            <span class="ml-2 text-sm text-gray-700">
                                {{ user.maAgree ? '동의함' : '미동의' }}
                            </span>
                        </label>
                    </div>
                </div>
            </div>



        </div>

        <div v-else>
            <p>사용자 정보를 불러오는 중...</p>
        </div>


        <!-- 탈퇴 버튼 -->
        <button @click="deleteUser" class="mt-4 bg-blue-500 text-white px-4 py-2 rounded">
            탈퇴
        </button>


        <!-- 수정 모드 버튼 -->
        <button @click="toggleEditMode" class="bg-yellow-500 text-white px-4 py-2 rounded mr-2">
            {{ editMode ? '수정 취소' : '수정하기' }}
        </button>

        <!-- 수정 저장 버튼 -->
        <button v-if="editMode" @click="updateUser" class="bg-blue-500 text-white px-4 py-2 rounded mr-2">
            저장
        </button>

    </div>
</template>

<script>
import axios from 'axios'
export default {
    data() {
        return {
            user: null,
            // 마스킹 적용/해제
            masKed: true,
            // 수정 
            editMode: false,
            // 비밀번호 재입력
            password2: '',
        }
    },

    async created() {
        const userId = this.$route.params.id;
        //userID로 조회 요청
        try {
            const response = await axios.get(`http://localhost:8080/users/${userId}`);
            this.user = response.data;
            console.log(this.user)
        } catch (error) {
            alert('사용자 정보 로딩 실패: ' + error);
        }
    },
    methods: {
        //권한 숫자를 텍스트로 변환
        formatAccessLevel(level) {
            switch (level) {
                case 1: return '최고관리자';
                case 2: return '상담사';
                case 3: return '일반 사용자';
                default: return '알 수 없음';
            }
        },
        toggleEditMode() {
            this.editMode = !this.editMode;
            if (this.editMode) {
                this.user.password = '';
                this.password2 = '';
            }
        },


        // 수정 요청
        async updateUser() {
            const userId = this.$route.params.id;

            // 비밀번호 입력 여부 체크 (공백 포함 제거 후)
            const trimmedPassword = this.user.password?.trim();
            const trimmedPassword2 = this.password2?.trim();

            if (trimmedPassword || trimmedPassword2) {
                // 둘 중 하나라도 공백이면 예외 처리
                if (!trimmedPassword || !trimmedPassword2) {
                    alert('비밀번호 입력란에 공백만 입력할 수 없습니다.');
                    return;
                }

                // 비밀번호와 비밀번호 재입력 일치 확인
                if (trimmedPassword !== trimmedPassword2) {
                    alert('비밀번호가 일치하지 않습니다.');

                    console.log("pw1 :", this.user.password)
                    console.log("pw2 :", this.user.password2)

                    return;
                }
                this.user.password = trimmedPassword;
            } else {
                // 둘 다 비워진 경우는 기존 비밀번호 유지 (전송 제외)
                delete this.user.password;
            }

            console.log(userId)

            try {
                console.log("수정 요청 : " + JSON.stringify(this.user))
                const response = await axios.put(`http://localhost:8080/users/${userId}`, this.user);
                this.user = response.data;
                console.log("수정 완료 : " + JSON.stringify(this.user))
                alert('사용자 정보가 수정되었습니다.');
                this.editMode = false;
                this.password2 = '';
            } catch (error) {
                alert('수정 실패: ' + error);
            }
        },


        // 탈퇴 요청
        async deleteUser() {
            console.log(this.user)

            if (!this.user) return;

            const confirmed = confirm(`탈퇴하시겠습니까?`);
            if (!confirmed) return;
            const userId = this.$route.params.id;
            try {
                await axios.delete(`http://localhost:8080/users/${userId}`);
                console.log(this.user.id)
                alert('탈퇴되었습니다.');
                this.$router.push('/Admin-Home');
            } catch (err) {
                alert('탈퇴 중 오류 발생: ' + error);

            }
        }

    }
}
</script>
