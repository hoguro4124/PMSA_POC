<template>
    <div class="p-4">
        <h2 class="text-xl font-bold mb-4">사용자 상세 정보</h2>

        <div v-if="user">
            <p><strong>ID:</strong> {{ user.userId }}</p>
            <p><strong>이름:</strong> {{ masKed ? maskName(user.name) : user.name }}</p>
            <p><strong>전화번호:</strong> {{ masKed ? maskPhone(user.phone) : user.phone }}</p>
            <p><strong>이메일:</strong> {{ masKed ? maskEmail(user.email) : user.email }}</p>
            <p><strong>권한:</strong> {{ formatAccessLevel(user.accessLevel) }}</p>
        </div>

        <div v-else>
            <p>사용자 정보를 불러오는 중...</p>
        </div>

        <!-- 마스킹 적용/해제 버튼 -->
        <button @click="toggleMask" class="mt-4 bg-blue-500 text-white px-4 py-2 rounded">
            {{ masKed ? '마스킹 해제' : '마스킹 적용' }}
        </button>

        <!-- 삭제 버튼 -->
        <button @click="deleteUser" class="mt-4 bg-blue-500 text-white px-4 py-2 rounded">
            삭제
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
            masKed: true
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
