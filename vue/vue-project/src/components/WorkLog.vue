<template>
    <div class="p-8 bg-gray-50 min-h-screen">
        <h1 class="text-3xl font-extrabold text-gray-800 mb-6 border-b pb-2">
            개인정보취급자 작업 기록 (Work Logs)
        </h1>

        <div class="bg-white shadow rounded-lg p-5 mb-6 border-l-4 border-indigo-500">
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4 items-end">
                <div class="flex flex-col">
                    <label class="text-sm font-medium text-gray-700 mb-1">작업자 ID</label>
                    <input type="text" v-model="inputOperatorId" @keyup.enter="applySearch" placeholder="작업자 ID"
                        class="border border-gray-300 p-2 rounded-md" />
                </div>
                <div class="flex flex-col">
                    <label class="text-sm font-medium text-gray-700 mb-1">작업 유형</label>
                    <select v-model="inputActionType" class="border border-gray-300 p-2 rounded-md bg-white">
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
                <div class="flex flex-col">
                    <label class="text-sm font-medium text-gray-700 mb-1">시작일</label>
                    <input type="date" v-model="inputStartDate" class="border border-gray-300 p-2 rounded-md" />
                </div>
                <div class="flex flex-col">
                    <label class="text-sm font-medium text-gray-700 mb-1">종료일</label>
                    <input type="date" v-model="inputEndDate" class="border border-gray-300 p-2 rounded-md" />
                </div>
                <div class="flex gap-2 h-full">
                    <button @click="applySearch"
                        class="flex-1 bg-indigo-600 text-white font-bold py-2 px-4 rounded hover:bg-indigo-700 h-[42px] self-end">검색</button>
                    <button @click="resetSearch"
                        class="flex-1 bg-gray-500 text-white font-bold py-2 px-4 rounded hover:bg-gray-600 h-[42px] self-end">초기화</button>
                </div>
            </div>
        </div>

        <div v-if="loading" class="text-center py-10">
            <p class="text-lg text-indigo-600">로딩중...</p>
        </div>

        <div v-else class="bg-white shadow-xl rounded-lg overflow-hidden overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-100">
                    <tr>
                        <th class="px-4 py-3 text-left text-xs font-bold text-gray-500 uppercase">NO</th>
                        <th class="px-4 py-3 text-left text-xs font-bold text-gray-500 uppercase">작업자</th>
                        <th class="px-4 py-3 text-left text-xs font-bold text-gray-500 uppercase">메뉴명</th>
                        <th class="px-4 py-3 text-left text-xs font-bold text-gray-500 uppercase">유형</th>
                        <th class="px-4 py-3 text-left text-xs font-bold text-gray-500 uppercase">대상자</th>
                        <th class="px-4 py-3 text-left text-xs font-bold text-gray-500 uppercase">상세 내용</th>
                        <th class="px-4 py-3 text-left text-xs font-bold text-gray-500 uppercase">IP</th>
                        <th class="px-4 py-3 text-left text-xs font-bold text-gray-500 uppercase">일시</th>
                    </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                    <tr v-for="(log, index) in filteredLogs" :key="log.id" class="hover:bg-gray-50">
                        <td class="px-4 py-3 text-sm text-gray-500">{{ filteredLogs.length - index }}</td>
                        <td class="px-4 py-3 text-sm font-bold text-indigo-600">{{ log.operatorId }}</td>
                        <td class="px-4 py-3 text-sm text-gray-700">{{ log.menuName || '-' }}</td>
                        <td class="px-4 py-3 text-sm"><span :class="getActionClass(log.actionType)">{{ log.actionType
                                }}</span></td>
                        <td class="px-4 py-3 text-sm text-gray-600">{{ log.targetId || '-' }}</td>
                        <td class="px-4 py-3 text-sm text-gray-600 max-w-xs truncate" :title="log.details">{{
                            log.details }}</td>
                        <td class="px-4 py-3 text-sm text-gray-500">{{ log.ipAddress }}</td>
                        <td class="px-4 py-3 text-sm text-gray-500">{{ formatDateTime(log.logTime) }}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
import dayjs from 'dayjs';

export default {
    data() {
        return {
            logs: [],
            loading: false,

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
        }
    },
    mounted() {
        this.fetchLogs();
    },
    methods: {
        async fetchLogs(params = {}) {
            this.loading = true;
            try {
                const response = await axios.get('http://localhost:8080/api/work-logs', { params });
                this.logs = response.data;
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
        getActionClass(type) {
            if (type.includes('SEARCH')) return 'text-purple-600 font-bold'; // 검색은 보라색
            if (type.includes('VIEW')) return 'text-blue-600 font-bold';
            if (type.includes('UPDATE')) return 'text-orange-600 font-bold';
            if (type.includes('DELETE')) return 'text-red-600 font-bold';
            return 'text-gray-600';
        }
    }
}
</script>