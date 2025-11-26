<template>
    <main class="form-signin w-100 m-auto">
        <h2 class="h3 fw-bold mb-4 text-start">사용자 상세 정보</h2>

        <div v-if="user">
            <div class="mb-3">
                <label class="form-label fw-semibold">ID:</label>
                <span class="d-block">{{ user.userId }}</span>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">이름:</label>
                <span v-if="!editMode" class="d-block">
                    {{ masKed ? maskName(user.name) : user.name }}
                </span>
                <input v-else v-model="user.name" class="form-control input-small" />
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">전화번호:</label>
                <span class="d-block">{{ user.phone }}</span>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">이메일:</label>
                <span v-if="!editMode" class="d-block">
                    {{ masKed ? maskEmail(user.email) : user.email }}
                </span>
                <input v-else v-model="user.email" class="form-control input-small" />
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">권한:</label>
                <span v-if="!editMode" class="d-block">
                    {{ formatAccessLevel(user.accessLevel) }}
                </span>
                <select v-else v-model.number="user.accessLevel" class="form-select input-small">
                    <option :value="1">최고관리자</option>
                    <option :value="2">상담사</option>
                </select>
            </div>
        </div>

        <div v-else>
            <p>사용자 정보를 불러오는 중...</p>
        </div>

        <div class="d-flex gap-2 mt-4 flex-wrap">
            <button @click="toggleMask" class="btn btn-primary btn-small flex-fill">
                {{ masKed ? '마스킹 해제' : '마스킹 적용' }}
            </button>

            <button @click="editMode = !editMode" class="btn btn-warning btn-small flex-fill">
                {{ editMode ? '수정 취소' : '수정하기' }}
            </button>

            <button v-if="editMode" @click="updateUser" class="btn btn-primary btn-small flex-fill">
                저장
            </button>

            <button v-if="editMode" @click="deleteUser" class="btn btn-danger btn-small flex-fill">
                삭제
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

.btn-small {
    height: 38px;
    padding: 0 12px;
    font-size: 0.875rem;
    white-space: nowrap;
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
            editMode: false
        }
    },

    async created() {
        const userId = this.$route.params.id;
        //userID로 조회 요청
        try {
            const response = await axios.get(`http://localhost:8080/users/${userId}`);
            this.user = response.data;
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
        // 마스킹 적용/해제
        toggleMask() {
            console.log(this.masKed)
            this.masKed = !this.masKed
            console.log(this.masKed)
        },
        // 이름 마스킹
        maskName(name) {
            if (!name) return ''
            if (name.length === 2) return name[0] + '*'
            if (name.length >= 3) return name[0] + '*'.repeat(name.length - 2) + name[name.length - 1]
            return name
        },
        // 전화번호 마스킹
        maskPhone(phone) {
            if (!phone || phone.length < 7) return phone
            return phone.substring(0, 3) + '****' + phone.substring(phone.length - 4)
        },
        // 이메일 마스킹
        maskEmail(email) {
            if (!email.includes('@')) return email
            const [userPart, domain] = email.split('@')
            const maskedUser = userPart[0] + '*'.repeat(Math.max(userPart.length - 1, 1))
            return maskedUser + '@' + domain
        },
        // 수정 요청
        async updateUser() {
            const userId = this.$route.params.id;
            try {
                const response = await axios.put(`http://localhost:8080/users/${userId}`, this.user);
                this.user = response.data;
                alert('사용자 정보가 수정되었습니다.');
                this.editMode = false;
            } catch (error) {
                alert('수정 실패: ' + error);
            }
        },

        //삭제 요청
        async deleteUser() {
            console.log(this.user)

            if (!this.user) return;

            const confirmed = confirm(`${this.user.name}을(를) 삭제할까요?`);
            if (!confirmed) return;
            const userId = this.$route.params.id;
            try {
                await axios.delete(`http://localhost:8080/users/${userId}`);
                console.log(this.user.id)
                alert('삭제되었습니다.');
                this.$router.push('/Admin-Home');
            } catch (err) {
                alert('삭제 중 오류 발생: ' + error);

            }
        }

    }
}
</script>
