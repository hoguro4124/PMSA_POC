<template>
  <div class="p-4">
    <h2 class="text-xl font-bold mb-4">사용자 입력</h2>
    <div class="mb-4">
      <input v-model="user.name" placeholder="이름" class="border p-2 mr-2" />
      <input v-model="user.phone" placeholder="전화번호" class="border p-2" />
    </div>
    <button @click="addUser" class="bg-blue-500 text-white px-4 py-2 rounded">저장</button>
    <button @click="getUsers" class="bg-green-500 text-white px-4 py-2 rounded ml-2">조회</button>
    <button @click="delUsers" class="bg-green-500 text-white px-4 py-2 rounded ml-2">삭제</button>

    <div v-if="users.length" class="mt-4">
      <h3 class="text-lg font-semibold">사용자 목록</h3>
      <ul>
        <li v-for="u in users" :key="u.phone">
          {{ u.name }} ({{ u.phone }})
        </li>
      </ul>
    </div>
  </div>
<div>
    <h2>사용자 목록</h2>
    <ul>
      <li 
        v-for="u in users" 
        :key="u.phone" 
        @click="selectUser(u)"
        :style="{ cursor: 'pointer', fontWeight: selectedUser?.phone === u.phone ? 'bold' : 'normal' }"
      >
        {{ u.name }} ({{ u.phone }})
      </li>
    </ul>

    <button v-if="selectedUser" @click="deleteUser">삭제</button>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      user: {
        name: '',
        phone: ''
      },
      users: [],
      selectedUser: null
    };
  },
  methods: {
    async addUser() {
      try {
        await axios.post('http://localhost:8080/users', this.user);
        alert('저장 완료!');
        this.user.name = '';
        this.user.phone = '';
      } catch (error) {
        alert('저장 실패: ' + error);
      }
    },
    async getUsers() {
      try {
        const response = await axios.get('http://localhost:8080/users');
        this.users = response.data;
      } catch (error) {
        alert('조회 실패: ' + error);
      }
    },
    selectUser(user) {
    this.selectedUser = user;
  },

  async deleteUser() {
    if (!this.selectedUser) return;

    const confirmed = confirm(`${this.selectedUser.name}을(를) 삭제할까요?`);
    if (!confirmed) return;

    await axios.delete(`http://localhost:8080/users/${this.selectedUser.phone}`);
    this.selectedUser = null;
    await this.getUsers(); // 삭제 후 목록 다시 불러오기
  }

  }
};
</script>

<style scoped>
input {
  width: 200px;
}
</style>
