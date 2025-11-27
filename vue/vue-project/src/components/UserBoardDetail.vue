<template>
    <main class="form-signin w-100 m-auto">
        <h1 class="h3 fw-bold mb-4 text-start">문의사항 상세</h1>

        <form>
            <!-- 제목 -->
            <div class="mb-4">
                <label class="form-label fw-semibold">문의 제목</label>
                <input type="text" class="form-control bg-light" :value="board.title" readonly />
            </div>

            <!-- 작성자 -->
            <div class="mb-4">
                <label class="form-label fw-semibold">작성자</label>
                <input type="text" class="form-control bg-light" :value="board.userId" readonly />
            </div>

            <!-- 내용 -->
            <div class="mb-4">
                <label class="form-label fw-semibold">문의 내용</label>
                <textarea class="form-control bg-light" rows="8" :value="board.content" readonly></textarea>
            </div>

            <!-- 작성일 -->
            <div class="mb-4">
                <label class="form-label fw-semibold">작성일</label>
                <input type="text" class="form-control bg-light" :value="formatDate(board.createdAt)" readonly />
            </div>

            <!-- 댓글 조회 (읽기 전용) -->
            <div class="mb-4" v-if="board.comment && board.comment.trim() !== ''">
                <label class="form-label fw-semibold">댓글</label>
                <textarea class="form-control bg-light" rows="6" :value="board.comment" readonly></textarea>
            </div>

            <div class="d-flex justify-content-between align-items-center mb-3">
                <div class="d-flex gap-2"></div>
                <!-- 목록 버튼 -->
                <div class="d-flex justify-content-center">
                    <button type="button" class="btn btn-success btn-small" @click="$router.push('/UserBoardList')">
                        목록
                    </button>
                </div>
            </div>
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

.btn-small {
    height: 38px;
    padding: 0 12px;
    font-size: 0.875rem;
    white-space: nowrap;
}
</style>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

const route = useRoute();
const router = useRouter();

const board = ref({
    title: "",
    content: "",
    userId: "",
    createdAt: "",
    comment: ""  // 댓글 정보
});

const boardId = route.params.id;



// 상세 게시글 조회
const getBoard = async () => {
    try {
        const response = await axios.get(`http://localhost:8080/board/${boardId}`);
        board.value = response.data;
    } catch (error) {
        console.error(error);
        alert("조회 실패: " + error);
    }
};

// 날짜 포맷
const formatDate = (dateStr) => {
    if (!dateStr) return "";
    const date = new Date(dateStr);
    return date.toLocaleString();
};

onMounted(() => {
    getBoard();
});
</script>
