<template>
    <main class="form-signin w-100 m-auto">
        <h1 class="h3 mb-3 fw-normal text-center">최고관리자 - 관리자 목록</h1>

        <!-- 검색창 -->
        <form class="mb-3 d-flex" @submit.prevent="performSearch">
            <input v-model="searchInput" type="text" class="form-control me-2 input-small"
                placeholder="이름, 전화번호, 이메일 검색" />
            <button type="submit" class="btn btn-primary btn-small">검색</button>
        </form>

        <!-- 조회 목록 -->
        <table class="table table-bordered table-striped align-middle">
            <thead>
                <tr class="table-light">
                    <th style="width: 20px;" class="text-center"><input type="checkbox" v-model="selectAll"
                            @change="toggleAll" /></th>
                    <th style="width: 40px;" class="text-center">ID</th>
                    <th style="width: 40px;" class="text-center">이름</th>
                    <th style="width: 60px;" class="text-center">전화번호</th>
                    <th style="width: 80px;" class="text-center">이메일</th>
                    <th style="width: 40px;" class="text-center">권한</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="user in paginatedUsers" :key="user.id" class="cursor-pointer" @click="goToDetail(user.id)">
                    <td>
                        <input type="checkbox" v-model="selectedUsers" :value="user" @click.stop />
                    </td>
                    <td>{{ user.userId }}</td>
                    <td>{{ masKed ? maskName(user.name) : user.name }}</td>
                    <td>{{ masKed ? maskPhone(user.phone) : user.phone }}</td>
                    <td>{{ masKed ? maskEmail(user.email) : user.email }}</td>
                    <td>{{ formatAccessLevel(user.accessLevel) }}</td>
                </tr>
            </tbody>
        </table>

        <!-- 하단 버튼 -->
        <div class="mb-3 d-flex gap-2">
            <button @click="downloadCSV" class="btn btn-success">선택 사용자 다운로드</button>
            <button @click="$router.push('/Admin-Join')" class="btn btn-success">관리자 생성</button>
        </div>

        <!-- 페이지네이션 -->
        <nav>
            <ul class="pagination justify-content-center">
                <li class="page-item" v-for="page in totalPages" :key="page" :class="{ active: currentPage === page }">
                    <button class="page-link" @click="currentPage = page">{{ page }}</button>
                </li>
            </ul>
        </nav>
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

.cursor-pointer {
    cursor: pointer;
}

.input-small {
    height: 30px;
    /* 원하는 높이로 조절 */
    padding: 0.25rem 0.5rem;
    /* 상하/좌우 패딩 조절 */
    font-size: 0.875rem;
    /* 텍스트 크기 조절 */
}

.btn-small {
    height: 30px;
    /* 원하는 높이로 조절 */
    padding: 0.25rem 0.5rem;
    /* 상하/좌우 패딩 조절 */
    font-size: 0.5rem;
    /* 텍스트 크기 조절 */
}

.table {
    width: 900px;
    /* 표 전체 너비 고정 */
    table-layout: fixed;
    /* 고정 레이아웃 사용 */
}

.th,
td {
    text-align: center;
}
</style>

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
