<template>
    <main class="form-signin w-100 m-auto">
        <h1 class="h3 mb-3 fw-normal text-center border-bottom pb-2">접속 기록 관리</h1>



        <div class="mb-4 p-3 bg-white rounded border border-primary">
            <form @submit.prevent="applySearch" class="row g-3 align-items-end">
                <div class="col-6 col-md-2">
                    <label class="form-label">사용자 ID</label>
                    <input type="text" v-model="inputUserId" class="form-control form-control-sm"
                        placeholder="ID 입력 (Enter)" />
                </div>
                <div class="col-6 col-md-2">
                    <label class="form-label">작업 유형</label>
                    <select v-model="inputActionType" class="form-select form-select-sm">
                        <option value="ALL">전체</option>
                        <option v-for="type in actionTypes" :key="type.value" :value="type.value">
                            {{ type.text }}
                        </option>
                    </select>
                </div>
                <div class="col-6 col-md-2">
                    <label class="form-label">조회 시작일</label>
                    <input type="date" v-model="inputStartDate" class="form-control form-control-sm" />
                </div>
                <div class="col-6 col-md-2">
                    <label class="form-label">조회 종료일</label>
                    <input type="date" v-model="inputEndDate" class="form-control form-control-sm" />
                </div>
                <div class="col-12 col-md-4 d-flex gap-2">
                    <button type="submit" class="btn btn-primary btn-sm flex-fill">검색</button>
                    <button type="button" @click="resetSearch" class="btn btn-secondary btn-sm flex-fill">초기화</button>
                </div>
            </form>
        </div>




        <div v-if="loading" class="text-center py-10">
            <p class="text-lg text-blue-600">로딩중...</p>
        </div>


        <div v-else class="bg-white shadow-xl rounded-lg overflow-hidden">
            <table class="table table-bordered table-striped align-middle">
                <thead>
                    <tr class="table-light">
                        <th style="width: 40px;" class="text-center">NO</th>
                        <th style="width: 80px;" class="text-center">사용자 ID</th>
                        <th style="width: 120px;" class="text-center">작업 유형</th>
                        <th style="width: 160px;" class="text-center">접속 IP</th>
                        <th style="width: 180px;" class="text-center">기록 일시</th>
                    </tr>
                </thead>


                <tbody>
                    <tr v-for="(log, index) in paginatedUsers" :key="log.id">
                        <td>{{ filteredLogs.length - index }}</td>
                        <td>{{ log.userId }}</td>
                        <td><span :class="getActionClass(log.actionType)">
                                {{ getActionDisplay(log.actionType) }}</span></td>
                        <td>{{ log.ipAddress || 'N/A' }}</td>
                        <td>{{ formatDateTime(log.logTime) }}</td>
                    </tr>
                </tbody>
            </table>
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



.table {
    width: 900px;
    table-layout: fixed;
}

th,
td {
    text-align: center;
}

.cursor-pointer {
    cursor: pointer;
}
</style>

<script>
import axios from 'axios';
import dayjs from 'dayjs';
import 'dayjs/locale/ko';
dayjs.locale('ko');

export default {
    data() {
        return {
            logs: [],
            loading: false,
            API_BASE_URL: 'http://localhost:8080/api/access-logs',

            // 입력값
            inputUserId: '', inputActionType: 'ALL', inputStartDate: '', inputEndDate: '',

            // 실제 필터링용 값 (화면 목록 필터링)
            searchUserId: '', searchActionType: 'ALL', searchStartDate: '', searchEndDate: '',
            currentPage: 1,
            pageSize: 10,
            actionTypes: [
                { value: 'LOGIN', text: '일반 로그인' },
                { value: 'ADMIN_LOGIN', text: '관리자 로그인' },
                { value: 'LOGIN_FAIL', text: '로그인실패' },
            ]
        };
    },
    computed: {
        filteredLogs() {
            // 서버는 전체를 주지만, 화면에서 한번 더 확실하게 필터링
            let filtered = this.logs;
            if (this.searchUserId) filtered = filtered.filter(l => l.userId.toLowerCase().includes(this.searchUserId.toLowerCase()));
            if (this.searchActionType && this.searchActionType !== 'ALL') filtered = filtered.filter(l => l.actionType === this.searchActionType);
            if (this.searchStartDate) filtered = filtered.filter(l => dayjs(l.logTime).isAfter(dayjs(this.searchStartDate).startOf('day')) || dayjs(l.logTime).isSame(dayjs(this.searchStartDate).startOf('day')));
            if (this.searchEndDate) filtered = filtered.filter(l => dayjs(l.logTime).isBefore(dayjs(this.searchEndDate).endOf('day')) || dayjs(l.logTime).isSame(dayjs(this.searchEndDate).endOf('day')));
            return filtered;

        },
        // 필터링된 목록을 기준으로 페이지 계산
        totalPages() {
            if (this.filteredLogs.length === 0) return 1;
            return Math.ceil(this.filteredLogs.length / this.pageSize);
        },
        // 필터링된 목록을 기준으로 데이터 자르기
        paginatedUsers() {
            const start = (this.currentPage - 1) * this.pageSize;
            return this.filteredLogs.slice(start, start + this.pageSize);
        }
    },
    mounted() {
        this.fetchAccessLogs(); // 초기 로딩
    },
    methods: {
        async fetchAccessLogs(params = {}) {
            this.loading = true;
            try {
                const response = await axios.get(this.API_BASE_URL, { params });
                this.logs = response.data;
                this.currentPage = 1;
            } catch (err) {
                console.error(err);
            } finally {
                this.loading = false;
            }
        },
        applySearch() {
            // 1. 화면 필터링용 변수 업데이트
            this.searchUserId = this.inputUserId;
            this.searchActionType = this.inputActionType;
            this.searchStartDate = this.inputStartDate;
            this.searchEndDate = this.inputEndDate;

            // 2. 서버에 검색 조건 전송 (로그 남기기용)
            // [핵심] 여기서 params를 만들어서 보냅니다.
            const params = {};
            if (this.inputUserId) params.searchUserId = this.inputUserId;
            if (this.inputActionType !== 'ALL') params.actionType = this.inputActionType;
            if (this.inputStartDate) params.startDate = this.inputStartDate;
            if (this.inputEndDate) params.endDate = this.inputEndDate;

            console.log(">>> [AccessLog] 검색 요청:", params);
            this.fetchAccessLogs(params);
        },
        resetSearch() {
            this.inputUserId = ''; this.inputActionType = 'ALL'; this.inputStartDate = ''; this.inputEndDate = '';
            this.searchUserId = ''; this.searchActionType = 'ALL'; this.searchStartDate = ''; this.searchEndDate = '';
            this.fetchAccessLogs(); // 파라미터 없이 호출 -> 전체 조회 로그
        },
        formatDateTime(time) {
            if (!time) return 'N/A';
            return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
        },
        getActionDisplay(actionType) {
            if (actionType === 'ALL') return '전체';
            const found = this.actionTypes.find(t => t.value === actionType);
            return found ? found.text : actionType;
        },
        getActionClass(actionType) {
            switch (actionType) {
                case 'LOGIN': return 'text-green-800 bg-green-100 px-2 rounded';
                case 'ADMIN_LOGIN': return 'text-red-800 bg-red-100 px-2 rounded';
                case 'LOGIN_FAIL': return 'text-gray-800 bg-gray-100 px-2 rounded';
                default: return 'text-gray-600';
            }
        }
    }
}
</script>