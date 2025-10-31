<template>
    <div>
        <h2>게시글 작성</h2>
        <input class="border rounded px-2 py-1 w-full mb-3" type="text" v-model="post.title"
            placeholder="제목을 입력하세요" /><br><br>
        <textarea class="border rounded px-2 py-1 w-full mb-3" rows="5" v-model="post.content"
            placeholder="내용을 입력하세요"></textarea><br><br>
        <button class="bg-blue-500 text-white px-4 py-2 rounded" @click="handleSubmit">
            작성 완료
        </button>
        <button @click="goToUserHome" class="bg-blue-500 text-white px-4 py-2 rounded">목록</button>
    </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()

const goToUserHome = () => {
    router.push('/user-home')
}
</script>

<script>
import axios from 'axios';

export default {
    data() {
        return {
            post: {
                title: '',
                content: '',
            }
        }
    },
    methods: {
        async handleSubmit() {
            // 모두 입력 했는지 확인
            if (!this.post.title || !this.post.content) {
                alert('모든 입력값을 입력해주세요.');
                return;
            }
            console.log('보내는 값:', JSON.stringify(this.post));
        }

    }
}
</script>