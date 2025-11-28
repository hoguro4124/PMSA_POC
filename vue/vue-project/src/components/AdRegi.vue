<template>
    <main class="form-signin w-100 m-auto">
        <h1 class="h3 mb-3 fw-normal text-center border-bottom pb-2">광고 등록</h1>

        <form @submit.prevent="submitAd">
            <!-- 광고 제목 -->
            <div class="mb-3">
                <label for="adTitle" class="form-label fw-semibold">광고 제목</label>
                <input v-model="ad.adTitle" type="text" id="adTitle" class="form-control input-small"
                    placeholder="광고 제목을 입력하세요" required />
            </div>

            <!-- 광고 시작일 -->
            <div class="mb-3">
                <label for="adStartDate" class="form-label fw-semibold">광고 시작일</label>
                <input v-model="ad.adStartDate" type="date" id="adStartDate" class="form-control input-small"
                    required />
            </div>

            <!-- 광고 종료일 -->
            <div class="mb-3">
                <label for="adEndDate" class="form-label fw-semibold">광고 종료일</label>
                <input v-model="ad.adEndDate" type="date" id="adEndDate" class="form-control input-small"
                    :min="ad.adStartDate" required />
            </div>

            <!-- 광고 파일 업로드 -->
            <div class="mb-3">
                <label for="adFile" class="form-label fw-semibold">광고 파일</label>
                <input type="file" id="adFile" ref="adFileInput" @change="handleFileUpload" class="form-control"
                    accept=".pdf,.jpg,.jpeg,.png,.txt" required />
                <div v-if="fileError" class="text-danger small mt-1">
                    허용되지 않는 파일 형식입니다. (.pdf, .jpg, .jpeg, .png, .txt만 허용)
                </div>
            </div>

            <!-- 등록 버튼 -->
            <button type="submit" class="btn btn-primary btn-small w-100" :disabled="isSubmitting">
                광고 등록
            </button>
        </form>
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

.input-small {
    height: 38px;
    padding: 0.375rem 0.75rem;
    font-size: 1rem;
    border-radius: 6px;
}

.btn-small {
    height: 38px;
    padding: 0 12px;
    font-size: 0.875rem;
    white-space: nowrap;
}
</style>

<script>
import axios from 'axios'

export default {
    data() {
        return {
            ad: {
                adTitle: '',
                adStartDate: '',
                adEndDate: '',
                adAttach: '',
                userId: '', // 로그인한 사용자의 ID
                adRegDate: '', // 현재 일시
            },
            selectedFile: null, // 업로드한 파일 객체
            fileError: false, // 파일 확장자 오류 여부
            isSubmitting: false, // 광고 등록 버튼 비활성화 여부
        }
    },
    created() {
        // 로그인한 사용자 정보 가져오기 (로컬 스토리지나 API에서)
        this.ad.userId = localStorage.getItem('userId') || 'defaultUser'; // 예시로 로컬 스토리지에서 가져옴
    },
    methods: {
        // 폼 리셋
        resetForm() {
            // ad 객체 초기화
            this.ad.adTitle = '';
            this.ad.adStartDate = '';
            this.ad.adEndDate = '';
            this.ad.adAttach = '';
            this.ad.adRegDate = '';

            // 파일/에러 상태 초기화
            this.selectedFile = null;
            this.fileError = false;

            // 파일 input 비우기
            if (this.$refs.adFileInput) {
                this.$refs.adFileInput.value = '';
            }
        },

        // 파일 업로드 처리
        handleFileUpload(event) {
            const file = event.target.files[0]; // 파일 객체
            if (file) {
                const fileExtension = file.name.split('.').pop().toLowerCase();
                const allowedExtensions = ['pdf', 'jpg', 'jpeg', 'png', 'txt']; // 허용된 파일 확장자

                // 확장자 검사
                if (!allowedExtensions.includes(fileExtension)) {
                    this.fileError = true; // 오류 표시
                    this.selectedFile = null; // 파일 초기화
                    event.target.value = ''; // 잘못된 파일 선택 시 input도 비우기
                } else {
                    this.fileError = false; // 오류 없으면
                    this.selectedFile = file;
                    this.ad.adAttach = file.name; // 파일 이름을 adAttach에 저장
                    console.log('파일명 : ', file.name);
                }
            }
        },

        // 광고 등록 처리
        async submitAd() {
            // 필수 값 체크
            if (!this.ad.adTitle || !this.ad.adStartDate || !this.ad.adEndDate || !this.selectedFile) {
                alert('모든 필드를 작성해주세요.');
                return;
            }

            this.isSubmitting = true; // 버튼 비활성화

            try {
                // 날짜 변환 (날짜만 입력되므로 시간은 00:00:00으로 설정)
                const startDate = this.ad.adStartDate + "T00:00:00"; // adStartDate가 "2025-11-25"라면 "2025-11-25T00:00:00"로 변환
                const endDate = this.ad.adEndDate + "T00:00:00"; // adEndDate가 "2025-11-30"이라면 "2025-11-30T00:00:00"로 변환

                const formData = new FormData();
                formData.append('adTitle', this.ad.adTitle);
                formData.append('adStartDate', startDate); // 변환된 날짜 전송
                formData.append('adEndDate', endDate); // 변환된 날짜 전송
                formData.append('adAttach', this.selectedFile); // 실제 파일 객체를 전송
                formData.append('userId', this.ad.userId);
                formData.append('adRegDate', new Date().toISOString()); // 현재 시간

                // 보내는 데이터 확인 (로그 추가)
                console.log("보내는 데이터: ", {
                    adTitle: this.ad.adTitle,
                    adStartDate: startDate,
                    adEndDate: endDate,
                    adAttach: this.selectedFile.name,
                    userId: this.ad.userId,
                });

                // 백엔드 API 호출
                const response = await axios.post('http://localhost:8080/ads', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });

                // 응답 확인
                console.log("광고 등록 성공:", response);
                alert('광고 등록이 완료되었습니다.');

                this.resetForm(); // 알럿 후 폼 초기화

            } catch (error) {
                console.error("광고 등록 오류:", error); // 오류가 발생하면 콘솔에 출력
                if (error.response) {
                    console.error("응답 오류:", error.response.data); // 서버 응답 오류
                    alert(`서버 오류: ${error.response.data}`);
                } else if (error.request) {
                    console.error("요청 오류:", error.request); // 요청 오류
                    alert('서버에 요청을 보냈으나 응답이 없습니다.');
                } else {
                    console.error("설정 오류:", error.message); // 설정 오류
                    alert('설정 오류가 발생했습니다.');
                }
            } finally {
                this.isSubmitting = false; // 버튼 다시 활성화
            }
        }
    }
}
</script>