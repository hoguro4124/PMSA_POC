<template>
    <main class="form-signin w-100 m-auto">
        <h1 class="h3 mb-3 fw-normal text-center">최고관리자 - 사용자 목록</h1>

        <div class="mb-3 d-flex">
            <input v-model="searchInput" @keyup.enter="getUsers" type="text" class="form-control me-2 input-small"
                placeholder="이름, 전화번호, 이메일 검색" />
            <button @click="getUsers" class="btn btn-primary btn-small">검색</button>
        </div>

        <table class="table table-bordered table-striped align-middle">
            <thead>
                <tr class="table-light">
                    <th style="width: 20px;" class="text-center"> <input type="checkbox" v-model="selectAll"
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
                    <td><input type="checkbox" v-model="selectedUsers" :value="user" @click.stop /></td>
                    <td>{{ user.userId }}</td>
                    <td>{{ masKed ? maskName(user.name) : user.name }}</td>
                    <td>{{ masKed ? maskPhone(user.phone) : user.phone }}</td>
                    <td>{{ masKed ? maskEmail(user.email) : user.email }}</td>
                    <td>{{ formatAccessLevel(user.accessLevel) }}</td>
                </tr>
                <tr v-if="users.length === 0">
                    <td colspan="6" class="text-center py-4 text-muted">
                        {{ loading ? '로딩중...' : '데이터가 없습니다.' }}
                    </td>
                </tr>
            </tbody>
        </table>

        <div class="mb-3 d-flex gap-2">
            <button @click="downloadCSV" class="btn btn-success">선택 사용자 다운로드</button>
        </div>

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
    padding: 0.25rem 0.5rem;
    font-size: 0.875rem;
}

.btn-small {
    height: 30px;
    padding: 0.25rem 0.5rem;
    font-size: 0.5rem;
}

.table {
    width: 900px;
    table-layout: fixed;
}

.th,
td {
    text-align: center;
}
</style>

<script>
import axios from 'axios'

export default {
    data() {
        return {
            users: [],
            selectedUsers: [],
            currentPage: 1,
            pageSize: 10,
            searchInput: '',
            selectAll: false,
            masKed: true,
            loading: false
        }
    },
    computed: {
        // 프론트엔드 필터링 로직 제거
        totalPages() {
            return Math.ceil(this.users.length / this.pageSize);
        },
        paginatedUsers() {
            const start = (this.currentPage - 1) * this.pageSize;
            return this.users.slice(start, start + this.pageSize);
        }
    },
    methods: {
        // [수정] 서버로 검색어 전송
        async getUsers() {
            this.loading = true;
            try {
                const token = localStorage.getItem('token');

                // 검색어 파라미터 구성
                const params = {};
                if (this.searchInput) {
                    params.keyword = this.searchInput;
                }

                console.log(">>> [UserList] 서버 요청:", params);

                const response = await axios.get('http://localhost:8080/users', {
                    headers: { 'Authorization': `Bearer ${token}` },
                    params: params
                });

                this.users = response.data;
                this.currentPage = 1;

            } catch (error) {
                console.error(error);
                alert('조회 실패: ' + error);
            } finally {
                this.loading = false;
            }
        },

        formatAccessLevel(level) {
            switch (level) {
                case 1: return '최고관리자';
                case 2: return '상담사';
                case 3: return '일반 사용자';
                default: return '알 수 없음';
            }
        },
        goToDetail(id) { this.$router.push(`/User/${id}`); },
        toggleAll() {
            if (this.selectAll) { this.selectedUsers = [...this.paginatedUsers]; }
            else { this.selectedUsers = []; }
        },
        maskName(name) {
            if (!name) return ''
            if (name.length === 2) return name[0] + '*'
            if (name.length >= 3) return name[0] + '*'.repeat(name.length - 2) + name[name.length - 1]
            return name
        },
        maskPhone(phone) {
            if (!phone || phone.length < 7) return phone
            return phone.substring(0, 3) + '****' + phone.substring(phone.length - 4)
        },
        maskEmail(email) {
            if (!email.includes('@')) return email
            const [userPart, domain] = email.split('@')
            const maskedUser = userPart[0] + '*'.repeat(Math.max(userPart.length - 1, 1))
            return maskedUser + '@' + domain
        },
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
            let csvContent = '\uFEFF' + headers.join(',') + '\n';
            rows.forEach(row => { csvContent += row.join(',') + '\n'; });
            const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
            const url = URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.setAttribute('href', url);
            link.setAttribute('download', '선택사용자.csv');
            link.click();
            URL.revokeObjectURL(url);
        }
    },
    mounted() {
        this.getUsers();
    }
}
</script>