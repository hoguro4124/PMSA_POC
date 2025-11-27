<template>
    <main class="form-signin w-100 m-auto">
        <h1 class="h3 mb-3 fw-normal text-center border-bottom pb-2">개인정보취급자 작업 기록</h1>

        <div class="mb-4 p-3 bg-white rounded border border-primary">
            <div class="mb-3 border-bottom pb-2">
                <label class="me-3 fw-bold">조회 대상:</label>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" id="adminRadio" value="ADMIN" v-model="userLevelType"
                        @change="applySearch">
                    <label class="form-check-label" for="adminRadio">관리자</label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" id="generalRadio" value="GENERAL"
                        v-model="userLevelType" @change="applySearch">
                    <label class="form-check-label" for="generalRadio">일반 사용자</label>
                </div>
            </div>

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
                        <option value="BOARD_LIST_VIEW">게시판 목록</option>
                        <option value="BOARD_DETAIL_VIEW">게시판 상세</option>
                        <option value="COMMENT_ACTION">댓글 작업</option>
                        <option value="AD_LIST_VIEW">광고 목록</option>
                        <option value="AD_CREATE">광고 등록</option>
                        <option value="ADMIN_SEARCH">관리자 검색</option>
                        <option value="USER_SEARCH">고객 검색</option>
                        <option value="WORK_LOG_SEARCH">작업기록 검색</option>
                        <option value="WORK_LOG_VIEW">작업기록 조회</option>
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
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>

        <div v-else class="bg-white shadow-xl rounded-lg overflow-hidden overflow-x-auto">
            <table class="table table-bordered table-striped align-middle">
                <thead>
                    <tr class="table-light">
                        <th style="width: 40px;" class="text-center">NO</th>
                        <th style="width: 80px;" class="text-center">작업자</th>
                        <th style="width: 120px;" class="text-center">메뉴명</th>

                        <th style="width: 80px;" class="text-center">대상자</th>
                        <th style="width: 180px;" class="text-center">상세 내용</th>
                        <th style="width: 120px;" class="text-center">IP</th>
                        <th style="width: 160px;" class="text-center">일시</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="(log, index) in paginatedLogs" :key="log.id">
                        <td class="text-center">{{ filteredLogs.length - ((currentPage - 1) * pageSize) - index }}</td>
                        <td class="text-center">{{ log.operatorId }}</td>
                        <td class="text-center">{{ log.menuName || '-' }}</td>


                        <td class="text-center">{{ log.targetId || '-' }}</td>

                        <td class="text-start" style="max-width: 180px;">
                            <div class="text-truncate-wrapper" :title="log.details">
                                {{ log.details }}
                            </div>
                        </td>

                        <td class="text-center">{{ log.ipAddress }}</td>
                        <td class="text-center">{{ formatDateTime(log.logTime) }}</td>
                    </tr>
                    <tr v-if="filteredLogs.length === 0">
                        <td colspan="8" class="text-center py-4 text-muted">데이터가 없습니다.</td>
                    </tr>
                </tbody>
            </table>
        </div>

        <nav v-if="totalPages > 1">
            <ul class="pagination justify-content-center mt-3">
                <li class="page-item" v-for="page in totalPages" :key="page" :class="{ active: currentPage === page }">
                    <button class="page-link" @click="currentPage = page">{{ page }}</button>
                </li>
            </ul>
        </nav>
    </main>
</template>

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

            userLevelType: 'ADMIN',
            inputOperatorId: '', inputActionType: 'ALL', inputStartDate: '', inputEndDate: '',
            searchOperatorId: '', searchActionType: 'ALL', searchStartDate: '', searchEndDate: ''
        }
    },
    computed: {
        filteredLogs() {
            let filtered = this.logs;
            if (this.searchOperatorId) filtered = filtered.filter(l => l.operatorId.toLowerCase().includes(this.searchOperatorId.toLowerCase()));
            if (this.searchActionType !== 'ALL') filtered = filtered.filter(l => l.actionType === this.searchActionType);
            if (this.searchStartDate) filtered = filtered.filter(l => dayjs(l.logTime).isAfter(dayjs(this.searchStartDate).startOf('day')) || dayjs(l.logTime).isSame(dayjs(this.searchStartDate).startOf('day')));
            if (this.searchEndDate) filtered = filtered.filter(l => dayjs(l.logTime).isBefore(dayjs(this.searchEndDate).endOf('day')) || dayjs(l.logTime).isSame(dayjs(this.searchEndDate).endOf('day')));
            return filtered;
        },
        totalPages() {
            if (this.filteredLogs.length === 0) return 1;
            return Math.ceil(this.filteredLogs.length / this.pageSize);
        },
        paginatedLogs() {
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
                const token = localStorage.getItem('token');
                params.userLevelType = this.userLevelType;

                const response = await axios.get('http://localhost:8080/api/work-logs', {
                    headers: { 'Authorization': `Bearer ${token}` },
                    params: params
                });

                this.logs = response.data;
                this.currentPage = 1;
            } catch (error) {
                console.error(error);
                alert("로그 조회 실패: " + error.message);
            } finally {
                this.loading = false;
            }
        },
        applySearch() {
            this.searchOperatorId = this.inputOperatorId;
            this.searchActionType = this.inputActionType;
            this.searchStartDate = this.inputStartDate;
            this.searchEndDate = this.inputEndDate;

            const params = {};
            if (this.inputOperatorId) params.searchOperatorId = this.inputOperatorId;
            if (this.inputActionType !== 'ALL') params.actionType = this.inputActionType;
            if (this.inputStartDate) params.startDate = this.inputStartDate;
            if (this.inputEndDate) params.endDate = this.inputEndDate;

            this.fetchLogs(params);
        },
        resetSearch() {
            this.userLevelType = 'ADMIN';
            this.inputOperatorId = ''; this.inputActionType = 'ALL'; this.inputStartDate = ''; this.inputEndDate = '';
            this.searchOperatorId = ''; this.searchActionType = 'ALL'; this.searchStartDate = ''; this.searchEndDate = '';
            this.fetchLogs();
        },
        formatDateTime(time) {
            return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
        },
        getActionClass(type) {
            const base = 'text-xs text-nowrap ';

            if (type.includes('SEARCH')) return base + 'text-purple-600';
            if (type.includes('VIEW')) return base + 'text-blue-600';
            if (type.includes('UPDATE') || type.includes('COMMENT')) return base + 'text-warning';
            if (type.includes('DELETE')) return base + 'text-danger';
            if (type.includes('CREATE')) return base + 'text-success';
            return base + 'text-secondary';
        }
    }
}
</script>

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
    table-layout: fixed;
}

th,
td {
    text-align: center;
    vertical-align: middle;
}

.text-truncate-wrapper {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    width: 100%;
}
</style>
