import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import UserJoin from '@/components/UserJoin.vue'
import LoginForm from '@/components/LoginForm.vue'
import IdInquiry from '@/components/IdInquiry.vue'

import AdminHome from '@/components/AdminHome.vue'
import AdminList from '@/components/AdminList.vue'
import AdminDetail from '@/components/AdminDetail.vue'
import AdminJoin from '@/components/AdminJoin.vue'

import UserList from '@/components/UserList.vue'
import UserDetail from '@/components/UserDetail.vue'

import UserHome from '@/components/UserHome.vue'
import VocHome from '@/components/VocHome.vue'
import Board from '@/components/Board.vue'

import MyPage from '@/components/MyPage.vue'
import MypageAuth from '@/components/MypageAuth.vue'

import AccessLog from '@/components/AccessLog.vue'
import WorkLog from '@/components/WorkLog.vue'

import BoardDetail from '@/components/BoardDetail.vue'
import BoardList from '@/components/BoardList.vue'
import UserBoardDetail from '@/components/UserBoardDetail.vue'
import UserBoardList from '@/components/UserBoardList.vue'

import AdList from '@/components/AdList.vue'
import AdRegi from '@/components/AdRegi.vue'

import policy from '@/components/policy.vue'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue')
    },
    {
      path: '/policy',
      name: 'policy',
      component: policy
    },
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/login-form',
      name: 'LoginForm',
      component: LoginForm
    },
    {
      path: '/user-join',
      name: 'UserJoin',
      component: UserJoin
    },
    {
      path: '/Id-Inquiry',
      name: 'IdInquiry',
      component: IdInquiry
    },
    {
      path: '/My/:id',
      name: 'MyPage',
      component: MyPage,
    },
    {
      path: '/Mypage-Auth',
      name: 'MypageAuth',
      component: MypageAuth,
    },
    //관리자 접근 메뉴 
    {//1
      path: '/Admin-Home',
      name: 'Admin-Home',
      component: AdminHome,
      meta: { requiresAuth: true, accessLevel: 1 }
    },
    { //1
      path: '/Admin-List',
      name: 'AdminList',
      component: AdminList,
      meta: { requiresAuth: true, accessLevel: 1 }
    },
    { //1
      path: '/Admin/:id',
      name: 'AdminDetail',
      component: AdminDetail,
      meta: { requiresAuth: true, accessLevel: 1 }
    },
    { //1
      path: '/Admin-Join',
      name: 'AdminJoin',
      component: AdminJoin,
      meta: { requiresAuth: true, accessLevel: 1 }
    },
    { //1
      path: '/User/:id',
      name: 'UserDetail',
      component: UserDetail,
      meta: { requiresAuth: true, accessLevel: 1 }
    },
    { //1
      path: '/access-logs',
      name: 'AccessLog',
      component: AccessLog,
      meta: { requiresAuth: true, accessLevel: 1 }
    },
    { //1
      path: '/work-logs',
      name: 'WorkLog',
      component: WorkLog,
      meta: { requiresAuth: true, accessLevel: 1 }
    },
    // 상담원 접근 메뉴
    { //2
      path: '/Voc-Home',
      name: 'VocHome',
      component: VocHome,
      meta: { requiresAuth: true, accessLevel: 2 }
    },
    // 관리자+상담원 접근 메뉴
    { //1, 2
      path: '/User-List',
      name: 'UserList',
      component: UserList,
      meta: { requiresAuth: true, accessLevel: [1, 2] }
    },
    { //1, 2 
      path: '/BoardList',
      name: 'BoardList',
      component: BoardList,
      meta: { requiresAuth: true, accessLevels: [1, 2] }
    },
    {//1, 2
      path: '/Board/:id',
      name: 'BoardDetail',
      component: BoardDetail,
      props: true,  // params를 props로 전달
      meta: { requiresAuth: true, accessLevels: [1, 2] }
    },
    {//1, 2
      path: '/AdList',
      name: 'AdList',
      component: AdList,
      meta: { requiresAuth: true, accessLevels: [1, 2] }
    },
    // 고객 접근 메뉴
    { //3
      path: '/User-Home',
      name: 'UserHome',
      component: UserHome,
      meta: { requiresAuth: true, accessLevel: 3 }
    },
    {//3
      path: '/Board',
      name: 'Board',
      component: Board,
      meta: { requiresAuth: true, accessLevels: 3 }
    },
    {//3
      path: '/AdRegi',
      name: 'AdRegi',
      component: AdRegi,
      meta: { requiresAuth: true, accessLevel: 3 }
    },
    {
      path: '/UserBoardList',
      name: 'UserBoardList',
      component: UserBoardList,
      meta: { requiresAuth: true, accessLevel: 3 }
    },
    {
      path: '/UserBoard/:id',
      name: 'UserBoardDetail',
      component: UserBoardDetail,
      meta: { requiresAuth: true, accessLevel: 3 }, props: true
    },

  ]
})

// 로그인 페이지 접근 제한 + 권한별 접근 제어
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  const accessLevel = parseInt(localStorage.getItem('accessLevel'));
  console.log('토큰, 레벨', token, accessLevel)

  // 로그인 페이지 접근 제한
  if (to.path === '/Login' && token) {
    alert('이미 로그인된 상태입니다.');
    return next('/');
  }

  // 인증이 필요한 페이지 접근
  if (to.meta.requiresAuth) {
    if (!token) {
      alert('로그인이 필요합니다.');
      return next('/');
    }

    const requiredLevel = to.meta.accessLevel || 3;
    console.log("필요 레벨(requiredLevel) : ", requiredLevel);
    console.log("내 권한 레벨(accessLevel) : ", accessLevel);
    if (accessLevel > requiredLevel) {
      alert(`접근 권한이 없습니다. (필요 레벨: ${requiredLevel}, 현재 레벨: ${accessLevel})`);

      if (accessLevel <= 1) return next('/Admin-Home');
      else if (accessLevel === 2) return next('/Voc-Home');
      else if (accessLevel === 3) return next('/User-Home');

      return next('/');
    }
  }

  next();
});

export default router
