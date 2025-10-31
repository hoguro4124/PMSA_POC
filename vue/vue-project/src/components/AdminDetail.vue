<template>
    <div class="p-4">
        <h2 class="text-xl font-bold mb-4">사용자 상세 정보</h2>


        <div v-if="user">
            <div class="mb-2">
                <label class="font-semibold">ID: </label>
                <span>{{ user.userId }}</span>
            </div>

            <div class="mb-2">
                <label class="font-semibold">이름: </label>
                <span v-if="!editMode">{{ masKed ? maskName(user.name) : user.name }}</span>
                <input v-else v-model="user.name" class="border p-1 ml-2" />
            </div>

            <div class="mb-2">
                <label class="font-semibold">전화번호: </label>
                <!-- <span v-if="!editMode">{{ masKed ? maskPhone(user.phone) : user.phone }}</span>
                <input v-else v-model="user.phone" class="border p-1 ml-2" /> -->
                <span>{{ user.phone }}</span>
            </div>

            <div class="mb-2">
                <label class="font-semibold">이메일: </label>
                <span v-if="!editMode">{{ masKed ? maskEmail(user.email) : user.email }}</span>
                <input v-else v-model="user.email" class="border p-1 ml-2" />
            </div>


            <div class="mb-2">
                <label class="font-semibold">권한: </label>

                <span v-if="!editMode">
                    {{ formatAccessLevel(user.accessLevel) }}
                </span>

                <select v-else v-model.number="user.accessLevel"
                    @change="console.log('권한선택:', user.accessLevel, typeof user.accessLevel)" class="border p-2 ml-2">
                    <option :value="1">최고관리자</option>
                    <option :value="2">상담사</option>
                </select>
            </div>

        </div>


        <div v-else>
            <p>사용자 정보를 불러오는 중...</p>
        </div>

        <!-- 마스킹 적용/해제 버튼 -->
        <button @click="toggleMask" class="mt-4 bg-blue-500 text-white px-4 py-2 rounded">
            {{ masKed ? '마스킹 해제' : '마스킹 적용' }}
        </button>


        <!-- 수정 모드 토글 -->
        <button @click="editMode = !editMode" class="bg-yellow-500 text-white px-4 py-2 rounded mr-2">
            {{ editMode ? '수정 취소' : '수정하기' }}
        </button>

        <!-- 수정 저장 버튼 -->
        <button v-if="editMode" @click="updateUser" class="bg-blue-500 text-white px-4 py-2 rounded mr-2">
            저장
        </button>
        <!-- 삭제 버튼 -->
        <button v-if="editMode" @click="deleteUser" class="mt-4 bg-blue-500 text-white px-4 py-2 rounded">
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
