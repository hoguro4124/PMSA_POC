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

            <!-- 댓글 -->
            <div class="mb-4">
                <label class="form-label fw-semibold">댓글</label>
                <textarea v-model="newComment" class="form-control" rows="6" placeholder="댓글을 작성하세요..."></textarea>
            </div>


            <div class="d-flex justify-content-between align-items-center mb-3">
                <!-- 왼쪽 댓글 관련 버튼들: 가로 일렬 -->
                <div class="d-flex gap-2">
                    <button class="btn btn-primary btn-small" @click="addComment">
                        댓글 작성
                    </button>
                    <button class="btn btn-warning btn-small" v-if="board.comment && board.comment.trim() !== ''"
                        @click="editComment">
                        댓글 수정
                    </button>
                    <button class="btn btn-danger btn-small" v-if="board.comment && board.comment.trim() !== ''"
                        @click="deleteComment">
                        댓글 삭제
                    </button>
                </div>

                <!-- 오른쪽 목록 버튼 -->
                <div>
                    <button type="button" class="btn btn-success btn-small" @click="$router.push('/BoardList')">
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

const newComment = ref(""); // 댓글 입력을 위한 변수

const boardId = route.params.id;


const getBoard = async () => {
    try {
        const response = await axios.get(`http://localhost:8080/board/${boardId}`);
        board.value = response.data;
        newComment.value = board.value.comment || ""; // 댓글이 null일 경우 빈 문자열로 초기화
    } catch (error) {
        console.error(error);
        alert("조회 실패: " + error);
    }
};

const addComment = async () => {
    if (!newComment.value.trim()) {
        alert("댓글을 입력하세요.");
        return;
    }

    try {
        const updatedBoard = { ...board.value, comment: newComment.value };
        // 댓글 추가
        await axios.put(`http://localhost:8080/board/${boardId}`, updatedBoard);
        alert("댓글이 추가되었습니다.");
        board.value.comment = newComment.value; // 댓글 데이터 갱신
        newComment.value = ""; // 댓글 입력란 초기화

        // 댓글 처리 후 페이지 새로고침 (getBoard 재호출)
        getBoard();  // 댓글 업데이트 후 최신 데이터 불러오기

    } catch (error) {
        console.error(error);
        alert("댓글 처리 실패: " + error);
    }
};

const editComment = async () => {
    if (!newComment.value.trim()) {
        alert("댓글을 입력하세요.");
        return;
    }

    try {
        const updatedBoard = { ...board.value, comment: newComment.value };
        // 댓글 수정
        await axios.put(`http://localhost:8080/board/${boardId}`, updatedBoard);
        alert("댓글이 수정되었습니다.");
        board.value.comment = newComment.value; // 댓글 데이터 갱신
        newComment.value = ""; // 댓글 입력란 초기화

        // 댓글 처리 후 페이지 새로고침 (getBoard 재호출)
        getBoard();  // 댓글 업데이트 후 최신 데이터 불러오기

    } catch (error) {
        console.error(error);
        alert("댓글 처리 실패: " + error);
    }
};

const deleteComment = async () => {
    try {
        await axios.put(`http://localhost:8080/board/${boardId}/deleteComment`); // 댓글 삭제
        alert("댓글이 삭제되었습니다.");
        board.value.comment = "";  // 댓글 데이터 갱신
        getBoard();  // 댓글 삭제 후 최신 데이터 불러오기

    } catch (error) {
        console.error(error);
        alert("댓글 삭제 실패: " + error);
    }
};

const formatDate = (dateStr) => {
    if (!dateStr) return "";
    const date = new Date(dateStr);
    return date.toLocaleString();
};

onMounted(() => {
    getBoard();
});
</script>
