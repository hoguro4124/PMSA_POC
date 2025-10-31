<template>
    <div class="p-4">
        <h1 class="text-2xl font-bold mb-4">최고관리자 - 관리자 목록</h1>

        <!-- 검색창 -->
        <input v-model="searchInput" type="text" placeholder="이름, 전화번호, 이메일 검색" class="border p-2 w-full mr-2" />
        <button @click="performSearch" class="bg-blue-500 text-white px-4 py-2 rounded">
            검색
        </button>

        <!-- 조회 목록 -->
        <table class="table-auto w-full border-collapse border border-gray-400">
            <thead>
                <tr class="bg-gray-200">
                    <th class="border px-2 py-1">
                        <input type="checkbox" v-model="selectAll" @change="toggleAll" />
                    </th>
                    <th class="border px-2 py-1">ID</th>
                    <th class="border px-2 py-1">이름</th>
                    <th class="border px-2 py-1">전화번호</th>
                    <th class="border px-2 py-1">이메일</th>
                    <th class="border px-2 py-1">권한</th>
                </tr>
            </thead>

            <tbody>
                <tr v-for="user in paginatedUsers" class="cursor-pointer hover:bg-gray-100">
                    <td class="border px-2 py-1">
                        <input type="checkbox" v-model="selectedUsers" :value="user" />
                    </td>
                    <td class="border px-2 py-1" :key="user.id" @click="goToDetail(user.id)"> {{ user.userId }}</td>
                    <td class="border px-2 py-1"> {{ masKed ? maskName(user.name) : user.name }} </td>
                    <td class="border px-2 py-1">{{ masKed ? maskPhone(user.phone) : user.phone }}</td>
                    <td class="border px-2 py-1">{{ masKed ? maskEmail(user.email) : user.email }}</td>
                    <td class="border px-2 py-1">{{ formatAccessLevel(user.accessLevel) }}</td>
                </tr>
            </tbody>
        </table>

        <!-- 다운로드 버튼 -->
        <button @click="downloadCSV" class="bg-green-500 text-white px-4 py-2 rounded mb-4">
            선택 사용자 다운로드
        </button>

        <!-- 사용자 등록 버튼 -->
        <button @click="$router.push('/Admin-Join')" class="bg-green-500 text-white px-4 py-2 rounded mb-4">
            관리자 생성
        </button>

        <!-- 페이지네이션 -->
        <div class="flex justify-center mt-4 space-x-2">
            <button v-for="page in totalPages" :key="page" @click="currentPage = page" :class="[
                'px-3 py-1 border rounded',
                currentPage === page ? 'bg-blue-500 text-white' : 'bg-white text-black'
            ]">
                {{ page }}
            </button>
        </div>
    </div>
</template>


<script>
import axios from 'axios'
import { mapWritableState } from 'pinia';

export default {
    data() {
        return {
            // 전체 사용자 데이터
            users: [],
            // 선택된 사용자 목록
            selectedUsers: [],
            // 현재 페이지 번호
            currentPage: 1,
            // 한 페이지당 사용자 수
            pageSize: 10,
            // 사용자가 입력한 검색어 (버튼 클릭 전 상태)
            searchInput: '',
            // 실제 필터링에 사용되는 검색어 (버튼 클릭 시 적용)
            search: '',
            // 전체 선택 여부
            selectAll: false,
            // 마스킹 적용
            masKed: true,
        }
    },
    computed: {
        // 검색어에 따라 필터링된 사용자 목록
        filteredUsers() {
            const keyword = this.search.toLowerCase();
            return this.users
                .filter(user => user.accessLevel < 3)
                .filter(user =>
                    user.name?.toLowerCase().includes(keyword) ||
                    user.phone?.includes(keyword) ||
                    user.email?.toLowerCase().includes(keyword)
                );
        },
        // 전체 페이지 수 계산
        totalPages() {
            return Math.ceil(this.filteredUsers.length / this.pageSize);
        },
        // 현재 페이지에 표시할 사용자 목록
        paginatedUsers() {
            console.log("계정 수 : ", this.filteredUsers.length);
            console.log("페이지 사이즈 : ", this.pageSize);
            const start = (this.currentPage - 1) * this.pageSize;
            return this.filteredUsers.slice(start, start + this.pageSize);
        }
    },
    methods: {
        // 사용자 목록 API 호출
        async getUsers() {
            try {
                const response = await axios.get('http://localhost:8080/users');
                this.users = response.data;
            } catch (error) {
                alert('조회 실패: ' + error);
            }
        },

        // 권한 숫자를 텍스트로 변환
        formatAccessLevel(level) {
            switch (level) {
                case 1: return '최고관리자';
                case 2: return '상담사';
                case 3: return '일반 사용자';
                default: return '알 수 없음';
            }
        },
        // 상세 페이지 이동
        goToDetail(id) {
            this.$router.push(`/Admin/${id}`);
        },

        // 검색 버튼 클릭 시 실행
        performSearch() {
            this.search = this.searchInput;
            this.currentPage = 1;
        },

        // 전체 체크박스 토글
        toggleAll() {
            if (this.selectAll) {
                this.selectedUsers = [...this.paginatedUsers];
            } else {
                this.selectedUsers = [];
            }
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


        // 선택된 사용자 CSV 다운로드
        downloadCSV() {
            if (this.selectedUsers.length === 0) {
                alert('다운로드할 사용자를 선택하세요.');
                return;
            }

            const headers = ['이름', '전화번호', '이메일', '권한'];
            const rows = this.selectedUsers.map(u => [
                u.name,
                u.phone,
                u.email,
                this.formatAccessLevel(u.accessLevel)
            ]);

            // UTF-8 BOM 추가 (\uFEFF) → Excel에서 한글 안 깨지게
            let csvContent = '\uFEFF' + headers.join(',') + '\n';
            rows.forEach(row => {
                csvContent += row.join(',') + '\n';
            });

            const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
            const url = URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.setAttribute('href', url);
            link.setAttribute('download', '선택사용자.csv');
            link.click();
            URL.revokeObjectURL(url);
        }
    },
    // 컴포넌트 마운트 시 사용자 목록 가져오기
    mounted() {
        this.getUsers();
    }
}
</script>
