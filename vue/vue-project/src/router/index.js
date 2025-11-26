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

import AdList from '@/components/AdList.vue'
import AdRegi from '@/components/AdRegi.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
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
      path: '/Admin-Home',
      name: 'Admin-Home',
      component: AdminHome
    },
    {
      path: '/Admin-List',
      name: 'AdminList',
      component: AdminList,
      meta: { requiresAuth: true, accessLevel: 1 }
    },
    {
      path: '/Admin/:id',
      name: 'AdminDetail',
      component: AdminDetail,
      meta: { requiresAuth: true, accessLevel: 1 }
    },
    {
      path: '/Admin-Join',
      name: 'AdminJoin',
      component: AdminJoin,
      meta: { requiresAuth: true, accessLevel: 1 }
    },
    {
      path: '/User-List',
      name: 'UserList',
      component: UserList,
      meta: { requiresAuth: true, accessLevel: 1 }
    },
    {
      path: '/User/:id',
      name: 'UserDetail',
      component: UserDetail,
      meta: { requiresAuth: true, accessLevel: 1 }
    },

    {
      path: '/Voc-Home',
      name: 'VocHome',
      component: VocHome,
      meta: { requiresAuth: true, accessLevel: 2 }
    },
    {
      path: '/User-Home',
      name: 'UserHome',
      component: UserHome,
      meta: { requiresAuth: true, accessLevel: 3 }
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

    //추가
    {
      path: '/access-logs',
      name: 'AccessLog',
      component: AccessLog,
      meta: { requiresAuth: true, accessLevel: 1 }
    },
    {
      path: '/work-logs',
      name: 'WorkLog',
      component: WorkLog,
      meta: { requiresAuth: true, accessLevel: 1 }
    },

    // Board 관련
    {//3
      path: '/Board',
      name: 'Board',
      component: Board,
      meta: { requiresAuth: true, accessLevel: 3 }

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
      props: true  // params를 props로 전달
    },
    {//3
      path: '/AdRegi',
      name: 'AdRegi',
      component: AdRegi,
      meta: { requiresAuth: true, accessLevel: 3 }
    },
    {//1,2
      path: '/AdList',
      name: 'AdList',
      component: AdList,
      meta: { requiresAuth: true, accessLevels: [1, 2] }
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue')
    }
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
      return next('/loginForm');
    }

    const requiredLevel = to.meta.accessLevel || 3;
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
