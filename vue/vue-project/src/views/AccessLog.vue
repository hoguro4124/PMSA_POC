<template>
    <div class="p-8 bg-gray-50 min-h-screen">
        <h1 class="text-3xl font-extrabold text-gray-800 mb-6 border-b pb-2">
            접속 기록 관리 (Access Logs)
        </h1>

        <div v-if="loading" class="text-center py-10">
            <p class="text-lg text-blue-600">데이터를 불러오는 중입니다...</p>
            <!-- 로딩 스피너 -->
            <svg class="animate-spin h-8 w-8 text-blue-500 mx-auto mt-4" xmlns="http://www.w3.org/2000/svg" fill="none"
                viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
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
            <div v-if="logs.length === 0" class="p-6 text-center text-gray-500">
                접속 기록이 존재하지 않습니다.
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
                    <tr v-for="(log, index) in logs" :key="log.id" :class="{ 'bg-gray-50': index % 2 === 1 }">
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ log.id }}</td>
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
import dayjs from 'dayjs'; // 날짜/시간 포맷팅을 위해 dayjs 라이브러리 사용 가정

// 한국어 로케일이 필요하다면 아래 주석 해제 (프로젝트에 dayjs가 설치되어 있어야 함)
import 'dayjs/locale/ko';
dayjs.locale('ko');

export default {
    data() {
        return {
            logs: [],
            loading: false,
            error: null,
            // API 기본 경로 설정 (백엔드 포트에 맞게 수정하세요)
            API_BASE_URL: 'http://localhost:8080/api/access-logs',
        };
    },
    mounted() {
        this.fetchAccessLogs();
    },
    methods: {
        // 접속 기록 데이터를 백엔드로부터 가져오는 함수
        async fetchAccessLogs() {
            this.loading = true;
            this.error = null;
            try {
                const response = await axios.get(this.API_BASE_URL);
                // API에서 최신순으로 정렬되어 오므로 그대로 사용
                this.logs = response.data;
            } catch (err) {
                console.error("접속 기록 조회 오류:", err);
                this.error = '접속 기록을 불러오는 데 실패했습니다.';
            } finally {
                this.loading = false;
            }
        },

        // 날짜/시간 포맷팅 함수
        formatDateTime(dateTime) {
            if (!dateTime) return 'N/A';
            // 'YYYY-MM-DDTHH:mm:ss.SSS' 형태의 문자열을 포맷팅
            // Dayjs 라이브러리가 필요합니다. (npm install dayjs)
            return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss');
        },

        // 작업 유형을 사용자에게 친숙한 이름으로 변환
        getActionDisplay(actionType) {
            switch (actionType) {
                case 'LOGIN': return '일반 로그인';
                case 'ADMIN_LOGIN': return '관리자 로그인';
                case 'LOGOUT': return '로그아웃';
                default: return actionType;
            }
        },

        // 작업 유형에 따라 스타일 클래스 지정
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
/* Tailwind CSS 사용으로 별도의 style은 최소화 */
</style>3
