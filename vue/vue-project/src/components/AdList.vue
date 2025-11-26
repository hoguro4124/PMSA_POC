<template>
    <main class="form-signin w-100 m-auto">
        <h1 class="h3 mb-3 fw-normal text-center border-bottom pb-2">광고 목록</h1>



        <!-- 테이블 카드 -->

        <table class="table table-bordered table-striped align-middle">

            <thead>
                <tr class="table-light">
                    <th class="text-center" style="width: 60px;">번호</th>
                    <th class="text-start">광고 제목</th>
                    <th class="text-center" style="width: 120px;">광고 시작일</th>
                    <th class="text-center" style="width: 120px;">광고 종료일</th>
                    <th class="text-center" style="width: 100px;">작성자</th>
                    <th class="text-center" style="width: 120px;">첨부파일</th>
                    <th class="text-center" style="width: 140px;">작성일</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(ad, index) in ads" :key="ad.id" class="hover-bg-gray">
                    <td class="text-center">{{ ad.id }}</td>
                    <td class="text-start">{{ ad.adTitle }}</td>
                    <td class="text-center">{{ formatDate(ad.adStartDate) }}</td>
                    <td class="text-center">{{ formatDate(ad.adEndDate) }}</td>
                    <td class="text-center">{{ ad.userId }}</td>
                    <td class="text-center">
                        <a v-if="ad.adAttach" :href="`http://localhost:8080/ads/download/${ad.adAttach}`"
                            target="_blank" class="text-primary text-decoration-underline">
                            다운로드
                        </a>
                        <span v-else>없음</span>
                    </td>
                    <td class="text-center">{{ formatDate(ad.adRegDate) }}</td>
                </tr>

                <tr v-if="ads.length === 0">
                    <td colspan="7" class="text-center py-4 text-muted">
                        등록된 광고가 없습니다.
                    </td>
                </tr>
            </tbody>
        </table>

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

.hover-bg-gray:hover {
    background-color: #f8f9fa;
}
</style>


<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const ads = ref([])

onMounted(() => {
    fetchAds()
})

const fetchAds = async () => {
    try {
        const res = await axios.get('http://localhost:8080/ads')
        ads.value = res.data
    } catch (err) {
        console.error('광고 목록 조회 실패:', err)
    }
}

const formatDate = (dateString) => {
    if (!dateString) return ''
    const date = new Date(dateString)
    return date.toISOString().split('T')[0]
}

const goToRegister = () => {
    router.push('/AdRegi')
}
</script>
