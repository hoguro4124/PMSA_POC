<template>
    <main class="form-signin w-100 m-auto">
        <h1 class="h3 mb-3 fw-normal text-center border-bottom pb-2">개인정보취급자 작업 기록</h1>

        <div class="mb-4 p-3 bg-white rounded border border-primary">
            <form @submit.prevent="applySearch" class="row g-3 align-items-end">
                <div class="col-6 col-md-2">
                    <label class="form-label">작업자 ID</label>
                    <input type="text" v-model="inputOperatorId" class="form-control form-control-sm"
                        placeholder="작업자 ID" />
                </div>
                <div class="col-6 col-md-2">
                    <label class="form-label">작업 유형</label>
                    <select v-model="inputActionType" class="form-select form-select-sm">
                        <option value="ALL">전체</option>
                        <option value="USER_LIST_VIEW">목록 조회</option>
                        <option value="USER_DETAIL_VIEW">상세 조회</option>
                        <option value="USER_UPDATE">정보 수정</option>
                        <option value="USER_DELETE">삭제/탈퇴</option>
                        <option value="USER_CREATE">신규 등록</option>
                        <option value="ADMIN_SEARCH">관리자 검색</option>
                        <option value="USER_SEARCH">고객 검색</option>
                    </select>
                </div>
                <div class="col-6 col-md-2">
                    <label class="form-label">시작일</label>
                    <input type="date" v-model="inputStartDate" class="form-control form-control-sm" />
                </div>
                <div class="col-6 col-md-2">
                    <label class="form-label">종료일</label>
                    <input type="date" v-model="inputEndDate" class="form-control form-control-sm" />
                </div>
                <div class="col-12 col-md-4 d-flex gap-2">
                    <button type="submit" class="btn btn-primary btn-sm flex-fill">검색</button>
                    <button type="button" @click="resetSearch" class="btn btn-secondary btn-sm flex-fill">초기화</button>
                </div>
            </form>
        </div>

        <div v-if="loading" class="text-center py-5">
        </div>


        <div v-else class="bg-white shadow-xl rounded-lg overflow-hidden overflow-x-auto">
            <table class="table table-bordered table-striped align-middle">
                <thead>
                    <tr class="table-light">
                        <th style="width: 40px;" class="text-center">NO</th>
                        <th style="width: 80px;" class="text-center">작업자</th>
                        <th style="width: 120px;" class="text-center">메뉴명</th>

                        <th style="width: 80px;" class="text-center">대상자</th>
                        <th style="width: 160px;" class="text-center">상세 내용</th>
                        <th style="width: 120px;" class="text-center">IP</th>
                        <th style="width: 180px;" class="text-center">일시</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="(log, index) in paginatedUsers" :key="log.id">
                        <td>{{ filteredLogs.length - index }}</td>
                        <td>{{ log.operatorId }}</td>
                        <td>{{ log.menuName || '-' }}</td>
                        <td>{{ log.targetId || '-' }}</td>
                        <td :title="log.details">{{
                            log.details }}</td>
                        <td>{{ log.ipAddress }}</td>
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

.table td {
    text-align: center;
}

.text-truncate {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
</style>

<script>
import axios from 'axios';
import dayjs from 'dayjs';

export default {
    data() {
        return {
            logs: [],
            loading: false,
            currentPage: 1,
            pageSize: 100,
            inputOperatorId: '', inputActionType: 'ALL', inputStartDate: '', inputEndDate: '',
            searchOperatorId: '', searchActionType: 'ALL', searchStartDate: '', searchEndDate: ''
        }
    },
    computed: {
        filteredLogs() {
            let filtered = this.logs;
            if (this.searchOperatorId) filtered = filtered.filter(l => l.operatorId.includes(this.searchOperatorId));
            if (this.searchActionType !== 'ALL') filtered = filtered.filter(l => l.actionType === this.searchActionType);
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
        this.fetchLogs();
    },
    methods: {
        async fetchLogs(params = {}) {
            this.loading = true;
            try {
                const response = await axios.get('http://localhost:8080/work_logs', { params });
                this.logs = response.data;
                this.currentPage = 1;
            } catch (error) {
                console.error(error);
            } finally {
                this.loading = false;
            }
        },
        applySearch() {
            this.searchOperatorId = this.inputOperatorId;
            this.searchActionType = this.inputActionType;
            this.searchStartDate = this.inputStartDate;
            this.searchEndDate = this.inputEndDate;

            // [핵심] 서버로 파라미터 전송 (로그용)
            const params = {};
            if (this.inputOperatorId) params.searchOperatorId = this.inputOperatorId;
            if (this.inputActionType !== 'ALL') params.actionType = this.inputActionType;
            if (this.inputStartDate) params.startDate = this.inputStartDate;
            if (this.inputEndDate) params.endDate = this.inputEndDate;

            console.log(">>> [WorkLog] 검색 요청:", params);
            this.fetchLogs(params);
        },
        resetSearch() {
            this.inputOperatorId = ''; this.inputActionType = 'ALL'; this.inputStartDate = ''; this.inputEndDate = '';
            this.searchOperatorId = ''; this.searchActionType = 'ALL'; this.searchStartDate = ''; this.searchEndDate = '';
            this.fetchLogs();
        },
        formatDateTime(time) {
            return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
        },
    }
}
</script>