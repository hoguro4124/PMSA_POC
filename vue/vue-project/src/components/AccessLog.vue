<template>
    <div class="p-8 bg-gray-50 min-h-screen">
        <h1 class="text-3xl font-extrabold text-gray-800 mb-6 border-b pb-2">
            접속 기록 관리 (Access Logs)
        </h1>

        <div class="bg-white shadow rounded-lg p-5 mb-6 border-l-4 border-blue-400">
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4 items-end">
                <div class="flex flex-col">
                    <label class="text-sm font-medium text-gray-700 mb-1">사용자 ID</label>
                    <input type="text" v-model="inputUserId" @keyup.enter="applySearch" placeholder="ID 입력 (Enter)"
                        class="border border-gray-300 p-2 rounded-md" />
                </div>
                <div class="flex flex-col">
                    <label class="text-sm font-medium text-gray-700 mb-1">작업 유형</label>
                    <select v-model="inputActionType" class="border border-gray-300 p-2 rounded-md bg-white">
                        <option value="ALL">전체</option>
                        <option v-for="type in actionTypes" :key="type.value" :value="type.value">{{ type.text }}
                        </option>
                    </select>
                </div>
                <div class="flex flex-col">
                    <label class="text-sm font-medium text-gray-700 mb-1">조회 시작일</label>
                    <input type="date" v-model="inputStartDate" class="border border-gray-300 p-2 rounded-md" />
                </div>
                <div class="flex flex-col">
                    <label class="text-sm font-medium text-gray-700 mb-1">조회 종료일</label>
                    <input type="date" v-model="inputEndDate" class="border border-gray-300 p-2 rounded-md" />
                </div>
                <div class="flex gap-2 h-full">
                    <button @click="applySearch"
                        class="flex-1 bg-blue-600 text-white font-bold py-2 px-4 rounded hover:bg-blue-700 h-[42px] self-end">검색</button>
                    <button @click="resetSearch"
                        class="flex-1 bg-gray-500 text-white font-bold py-2 px-4 rounded hover:bg-gray-600 h-[42px] self-end">초기화</button>
                </div>
            </div>
        </div>

        <div v-if="loading" class="text-center py-10">
            <p class="text-lg text-blue-600">로딩중...</p>
        </div>

        <div v-else class="bg-white shadow-xl rounded-lg overflow-hidden">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-100">
                    <tr>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase w-1/12">NO</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase w-2/12">사용자 ID</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase w-2/12">작업 유형</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase w-3/12">접속 IP</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase w-4/12">기록 일시</th>
                    </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                    <tr v-for="(log, index) in filteredLogs" :key="log.id" class="hover:bg-gray-50">
                        <td class="px-6 py-4 text-sm text-gray-900">{{ filteredLogs.length - index }}</td>
                        <td class="px-6 py-4 text-sm font-medium text-blue-600">{{ log.userId }}</td>
                        <td class="px-6 py-4 text-sm"><span :class="getActionClass(log.actionType)">{{
                                getActionDisplay(log.actionType) }}</span></td>
                        <td class="px-6 py-4 text-sm text-gray-500">{{ log.ipAddress || 'N/A' }}</td>
                        <td class="px-6 py-4 text-sm text-gray-500">{{ formatDateTime(log.logTime) }}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

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