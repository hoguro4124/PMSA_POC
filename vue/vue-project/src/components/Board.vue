<template>
    <main class="form-signin w-100 m-auto">
        <h2 class="h3 fw-bold mb-4 text-start">문의 사항 작성</h2>

        <form @submit.prevent="handleSubmit">
            <!-- 문의 제목 -->
            <div class="mb-4">
                <label class="form-label fw-semibold">문의 제목</label>
                <input type="text" v-model="post.title" class="form-control input-small" placeholder="제목을 입력하세요"
                    required />
            </div>

            <!-- 문의 내용 -->
            <div class="mb-5">
                <label class="form-label fw-semibold">문의 내용</label>
                <textarea v-model="post.content" class="form-control" rows="7" placeholder="내용을 입력하세요"
                    required></textarea>
            </div>

            <!-- 버튼 -->
            <div class="d-flex gap-3 justify-content-center">
                <button type="submit" class="btn btn-dark btn-small flex-fill">작성 완료</button>
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

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

const router = useRouter();

const post = ref({
    title: "",
    content: ""
});


// 등록 처리
const handleSubmit = async () => {
    const user_id = localStorage.getItem("userId"); // 로그인한 사용자 ID

    if (!user_id) {
        alert("로그인 정보가 없습니다. 다시 로그인해주세요.");
        return;
    }
    if (!post.value.title || !post.value.content) {
        alert("제목과 내용을 모두 입력해주세요.");
        return;
    }
    // 보내는 데이터 확인
    const payload = {
        title: post.value.title,
        content: post.value.content,
        userId: user_id
    };
    console.log("보내는 데이터:", payload);


    try {
        const response = await axios.post(
            "http://localhost:8080/board",
            payload,
            {
                headers: {
                    "Content-Type": "application/json"
                }
            }
        );

        console.log("서버 응답:", response);
        router.push("/user-home");
        alert("문의사항이 등록되었습니다.");


    } catch (error) {
        console.error("등록 실패 상세:", error);

        // [핵심] 에러 메시지를 사람이 읽을 수 있게 변환
        let errorMessage = "알 수 없는 오류가 발생했습니다.";

        if (error.response && error.response.data) {
            // 에러 데이터가 객체라면 문자열로 변환
            if (typeof error.response.data === 'object') {
                errorMessage = JSON.stringify(error.response.data, null, 2);
            } else {
                errorMessage = error.response.data;
            }
        } else if (error.message) {
            errorMessage = error.message;
        }

        alert("등록 실패:\n" + errorMessage);
    }
};
</script>