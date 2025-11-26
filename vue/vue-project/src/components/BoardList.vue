<template>
    <main class="form-signin w-100 m-auto">
        <h1 class="h3 mb-3 fw-normal text-center">문의사항 목록</h1>

        <!-- 검색창 -->
        <form class="mb-3 d-flex" @submit.prevent="performSearch">
            <input v-model="searchInput" type="text" class="form-control me-2 input-small" placeholder="제목 검색" />
            <button type="submit" class="btn btn-primary btn-small">검색</button>
        </form>

        <!-- 조회 목록 -->
        <table class="table table-bordered table-striped align-middle">
            <thead>
                <tr class="table-light">
                    <th style="width: 40px;" class="text-center">번호</th>
                    <th style="width: 200px;" class="text-center">제목</th>
                    <th style="width: 120px;" class="text-center">작성일</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="board in paginatedBoards" :key="board.id">
                    <td class="text-center">{{ board.id }}</td>
                    <td class="text-center " @click.prevent="goToBoardDetail(board.id)" style="cursor: pointer;">
                        {{ board.title }}</td>
                    <td class="text-center">{{ formatDate(board.createdAt) }}</td>
                </tr>
            </tbody>
        </table>

        <!-- 페이지네이션 -->
        <nav>
            <ul class="pagination justify-content-center">
                <li class="page-item" v-for="page in totalPages" :key="page" :class="{ active: currentPage === page }">
                    <button class="page-link" @click="currentPage = page">{{ page }}</button>
                </li>
            </ul>
        </nav>
    </main>
</template>

<script>
import axios from 'axios'

export default {
    data() {
        return {
            boards: [],
            searchInput: '',
            search: '',
            currentPage: 1,
            pageSize: 10,
        }
    },
    computed: {
        filteredBoards() {
            const keyword = this.search.toLowerCase()
            return this.boards.filter(board =>
                board.title?.toLowerCase().includes(keyword)
            )
        },
        totalPages() {
            return Math.ceil(this.filteredBoards.length / this.pageSize)
        },
        paginatedBoards() {
            const start = (this.currentPage - 1) * this.pageSize
            return this.filteredBoards.slice(start, start + this.pageSize)
        }
    },
    methods: {
        async getBoards() {
            try {
                const response = await axios.get('http://localhost:8080/board')
                this.boards = response.data
            } catch (error) {
                alert('조회 실패: ' + error)
            }
        },
        performSearch() {
            this.search = this.searchInput
            this.currentPage = 1
        },
        formatDate(dateStr) {
            const date = new Date(dateStr)
            return date.toLocaleString() // YYYY-MM-DD HH:MM:SS 형식
        },
        goToBoardDetail(id) {
            this.$router.push(`/Board/${id}`)
        }
    },
    mounted() {
        this.getBoards()
    }
}
</script>

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
    height: 30px;
    padding: 0.25rem 0.5rem;
    font-size: 0.875rem;
}

.btn-small {
    height: 30px;
    padding: 0.25rem 0.5rem;
    font-size: 0.7rem;
    white-space: nowrap;
}

.table {
    width: 100%;
    table-layout: fixed;
    border-collapse: collapse;
}

th {
    text-align: center;
}

td {
    text-align: center;
    vertical-align: middle;
    padding: 0.75rem 0.5rem;
}

/* 제목 컬럼 왼쪽 정렬 */
td.text-start {
    text-align: left;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

/* 페이지네이션 중앙 정렬 보장 */
.pagination {
    margin-top: 1rem;
    justify-content: center !important;
    padding-left: 0;
}
</style>
