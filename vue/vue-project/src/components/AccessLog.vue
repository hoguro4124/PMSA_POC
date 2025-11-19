<template>
    <div class="p-8 bg-gray-50 min-h-screen">
        <h1 class="text-3xl font-extrabold text-gray-800 mb-6 border-b pb-2">
            접속 기록 관리 (Access Logs)
        </h1>

        <!-- 검색 필터 영역 -->
        <div class="bg-white shadow rounded-lg p-5 mb-6 border-l-4 border-blue-400">
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4 items-center">
                <div class="flex flex-col">
                    <label for="searchId" class="text-sm font-medium text-gray-700 mb-1">사용자 ID 검색</label>
                    <input type="text" id="searchId" v-model="searchUserId" placeholder="검색할 사용자 ID 입력"
                        class="border border-gray-300 p-2 rounded-md focus:ring-blue-500 focus:border-blue-500" />
                </div>

                <div class="flex flex-col">
                    <label for="actionType" class="text-sm font-medium text-gray-700 mb-1">작업 유형</label>
                    <select id="actionType" v-model="searchActionType"
                        class="border border-gray-300 p-2 rounded-md bg-white focus:ring-blue-500 focus:border-blue-500">
                        <option v-for="type in actionTypes" :key="type.value" :value="type.value">
                            {{ type.text }}
                        </option>
                    </select>
                </div>

                <div class="flex items-end h-full pt-4 md:pt-0">
                    <button @click="resetSearch"
                        class="w-full bg-gray-500 text-white font-semibold py-2 px-4 rounded-md hover:bg-gray-600 transition duration-150 shadow-md">
                        검색 초기화
                    </button>
                </div>
            </div>
        </div>

        <div v-if="loading" class="text-center py-10">
            <p class="text-lg text-blue-600">데이터를 불러오는 중입니다...</p>
            <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto mt-4" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeke-width="4"></circle>
                <path class="opacity-75" fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z">
                </path>
            </svg>
        </div>

        <div v-else-if="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative"
            role="alert">
            <strong class="font-bold">오류 발생:</strong>
            <span class="block sm:inline">{{ error }}</span>
            <p class="text-sm mt-2">서버 상태와 API 경로(http://localhost:8080)를 확인해 주세요.</p>
        </div>

        <div v-else class="bg-white shadow-xl rounded-lg overflow-hidden">
            <div v-if="filteredLogs.length === 0" class="p-6 text-center text-gray-500">
                {{ logs.length > 0 ? '검색 조건과 일치하는 기록이 없습니다.' : '접속 기록이 존재하지 않습니다.' }}
            </div>

            <table v-else class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-100">
                    <tr>
                        <th
                            class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-1/12">
                            NO</th>
                        <th
                            class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-2/12">
                            사용자 ID</th>
                        <th
                            class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-2/12">
                            작업 유형</th>
                        <th
                            class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-3/12">
                            접속 IP</th>
                        <th
                            class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-4/12">
                            기록 일시</th>
                    </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                    <tr v-for="(log, index) in filteredLogs" :key="log.id" :class="{ 'bg-gray-50': index % 2 === 1 }">
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ filteredLogs.length - index }}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-blue-600">{{ log.userId }}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm">
                            <span :class="getActionClass(log.actionType)">
                                {{ getActionDisplay(log.actionType) }}
                            </span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ log.ipAddress || 'N/A' }}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDateTime(log.logTime) }}
                        </td>
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
            error: null,
            API_BASE_URL: 'http://localhost:8080/api/access-logs',

            searchUserId: '',
            searchActionType: 'ALL',
            actionTypes: [
                { value: 'ALL', text: '전체' },
                { value: 'LOGIN', text: '일반 로그인' },
                { value: 'ADMIN_LOGIN', text: '관리자 로그인' },
                { value: 'LOGOUT', text: '로그아웃' },
            ]
        };
    },
    computed: {
        filteredLogs() {
            let filtered = this.logs;

            if (this.searchUserId) {
                const lowerCaseSearch = this.searchUserId.toLowerCase();
                filtered = filtered.filter(log =>
                    log.userId.toLowerCase().includes(lowerCaseSearch)
                );
            }

            if (this.searchActionType && this.searchActionType !== 'ALL') {
                filtered = filtered.filter(log =>
                    log.actionType === this.searchActionType
                );
            }

            return filtered;
        }
    },
    mounted() {
        this.fetchAccessLogs();
    },
    methods: {
        async fetchAccessLogs() {
            this.loading = true;
            this.error = null;
            try {
                const response = await axios.get(this.API_BASE_URL);
                this.logs = response.data;
            } catch (err) {
                console.error("접속 기록 조회 오류:", err);
                this.error = '접속 기록을 불러오는 데 실패했습니다. 서버 상태와 API 경로를 확인하세요.';
            } finally {
                this.loading = false;
            }
        },
        resetSearch() {
            this.searchUserId = '';
            this.searchActionType = 'ALL';
        },
        formatDateTime(dateTime) {
            if (!dateTime) return 'N/A';
            return dayjs(dateTime).format('YYYY년 MM월 DD일 HH:mm');
        },
        getActionDisplay(actionType) {
            const found = this.actionTypes.find(type => type.value === actionType);
            return found ? found.text : actionType;
        },
        getActionClass(actionType) {
            switch (actionType) {
                case 'LOGIN': return 'inline-flex items-center px-3 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800';
                case 'ADMIN_LOGIN': return 'inline-flex items-center px-3 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800';
                case 'LOGOUT': return 'inline-flex items-center px-3 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800';
                default: return 'inline-flex items-center px-3 py-0.5 rounded-full text-xs font-medium bg-indigo-100 text-indigo-800';
            }
        }
    }
}
</script>

<style scoped>
/* Tailwind CSS 사용 */
</style>
