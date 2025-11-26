<template>
    <main class="form-signin w-100 m-auto">
        <h2 class="hh3 mb-3 fw-normal text-center border-bottom pb-2">사용자 상세 정보</h2>

        <div v-if="user" class="d-flex flex-column gap-3"
            style="grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));">
            <div>
                <label class="form-label fw-semibold">ID:</label>
                <div class="border rounded p-2 text-muted">{{ user.userId }}</div>
            </div>

            <div v-if="editMode">
                <label class="form-label fw-semibold" :class="{ 'text-danger': editMode }">비밀번호:</label>
                <input type="password" v-model="user.password" class="form-control" placeholder="새 비밀번호 입력" />
            </div>

            <div v-if="editMode">
                <label class="form-label fw-semibold" :class="{ 'text-danger': editMode }">비밀번호 재입력:</label>
                <input type="password" v-model="password2" class="form-control" placeholder="새 비밀번호 재입력" />
            </div>

            <div>
                <label class="form-label fw-semibold " :class="{ 'text-danger': editMode }">이름:</label>
                <div v-if="!editMode" class="border rounded p-2">{{ user.name }}</div>
                <input v-else v-model="user.name" class="form-control" />
            </div>

            <div>
                <label class="form-label fw-semibold" :class="{ 'text-danger': editMode }">전화번호:</label>
                <div v-if="!editMode" class="border rounded p-2">{{ user.phone }}</div>
                <input v-else v-model="user.phone" class="form-control" />
            </div>

            <div>
                <label class="form-label fw-semibold" :class="{ 'text-danger': editMode }">이메일:</label>
                <div v-if="!editMode" class="border rounded p-2">{{ user.email }}
                </div>
                <input v-else v-model="user.email" class="form-control" />
            </div>

            <div>
                <label class="form-label fw-semibold">권한:</label>
                <div class="border rounded p-2 text-muted">{{ formatAccessLevel(user.accessLevel) }}</div>
            </div>

            <div v-if="user.accessLevel == '3'">
                <h3>마케팅 동의</h3>
                <label class="form-check-label d-flex align-items-center">
                    <input type="checkbox" v-model="user.maAgree" class="form-check-input me-2" :disabled="!editMode" />
                    <span>{{ user.maAgree ? '동의함' : '미동의' }}</span>
                </label>
            </div>
        </div>

        <div v-else>
            <p>사용자 정보를 불러오는 중...</p>
        </div>

        <div class="d-flex gap-2 mt-4">
            <button @click="deleteUser" class="btn btn-danger flex-fill">
                탈퇴
            </button>

            <button @click="toggleEditMode" class="btn btn-warning text-white flex-fill">
                {{ editMode ? '수정 취소' : '수정하기' }}
            </button>

            <button v-if="editMode" @click="updateUser" class="btn btn-primary flex-fill">
                저장
            </button>
        </div>
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

.btn {
    min-width: 100px;
}
</style>



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
