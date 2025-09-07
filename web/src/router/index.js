import Vue from 'vue'
import Router from 'vue-router'

import Login from '@/views/login'
import Layout from '@/layout/layout'

Vue.use(Router)

/**
 * alwaysShow: true           if set true, will always show the root menu, whatever its child routes length
 *                            if not set alwaysShow, only more than ont route under the children
 *                            it will becomes nested mode, otherwise not show the root menu
 * alwaysShow: true           如果设置为true,它将总是现在在根目录。如果不设置的话，当它只有1个子路由的时候，会把
 *                            它的唯一子路由放到跟目录上来，而不显示它自己本身。
 */

export const constantRouterMap = [
  {
    path: '/login',
    name: 'login',
    hidden: true,
    component: Login,
    meta: {
      title: '登录'
    }
  },
  {
    path: '/',
    meta: {
      icon: 'location-outline',
      title: '工作台'
    },
    component: Layout,
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'home',
        component: () => import('@/views/homepage'),
        meta: {icon: 'location-outline', title: '工作台'}
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    meta: {
      icon: 'tickets',
      title: '个人中心'
    },
    children: [
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/profile'),
        meta: {icon: 'warning', title: '个人中心'}
      },
      {
        path: 'avatar',
        name: 'Avatar',
        component: () => import('@/views/user/profile'),
        meta: {icon: 'warning', title: '修改头像'}
      }
    ]
  },
  {
    path: '/content',
    component: Layout,
    alwaysShow: true,
    meta: {
      icon: 'location-outline',
      title: '内容管理'
    },
    children: [
      {
        path: '/page',
        name: 'Page',
        component: () => import('@/views/content/page/index'),
        meta: {icon: 'star-on', title: '文档管理'}
      },
      {
        path: '/page/create',
        name: 'CreatePage',
        hidden: true,
        component: () => import('@/views/content/page/form/index'),
        meta: {icon: 'star-on', title: '新建文档'}
      },
      {
        path: '/page/update',
        name: 'UpdatePage',
        hidden: true,
        component: () => import('@/views/content/page/form/index'),
        meta: {icon: 'star-on', title: '编辑文档'}
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/content/category/index'),
        meta: {icon: 'star-on', title: '栏目管理'}
      },
      {
        path: '/category/create',
        name: 'CreateCategory',
        hidden: true,
        component: () => import('@/views/content/category/form/index'),
        meta: {icon: 'star-on', title: '新建栏目'}
      },
      {
        path: '/category/update',
        name: 'UpdateCategory',
        hidden: true,
        component: () => import('@/views/content/category/form/index'),
        meta: {icon: 'star-on', title: '修改栏目'}
      },
      {
        path: '/tag/create',
        name: 'CreateTag',
        hidden: true,
        component: () => import('@/views/content/tag/form/index'),
        meta: {icon: 'star-on', title: '新建标签'}
      },
      {
        path: '/tag/update',
        name: 'UpdateTag',
        hidden: true,
        component: () => import('@/views/content/tag/form/index'),
        meta: {icon: 'star-on', title: '修改标签'}
      },
      {
        path: 'tag',
        name: 'Tag',
        component: () => import('@/views/content/tag/index'),
        meta: {icon: 'star-on', title: '标签管理'}
      }
    ]
  },
  // {
  //   path: '/collocated',
  //   component: Layout,
  //   redirect: '/collocated/index',
  //   // alwaysShow: true,
  //   meta: {
  //     icon: 'location-outline',
  //     title: '功能管理'
  //   },
  //   children: [
  //     {
  //       path: 'index',
  //       name: 'Collocated',
  //       component: () => import('@/views/collocated/index'),
  //       meta: {icon: 'location-outline', title: '功能管理'}
  //     },
  //     {
  //       path: '/collocated/create',
  //       name: 'CreateCollocated',
  //       hidden: true,
  //       component: () => import('@/views/collocated/form/index'),
  //       meta: {icon: 'star-on', title: '新建功能'}
  //     },
  //     {
  //       path: '/collocated/update',
  //       name: 'UpdateCollocated',
  //       hidden: true,
  //       component: () => import('@/views/collocated/form/index'),
  //       meta: {icon: 'star-on', title: '修改功能'}
  //     }
  //   ]
  // },
  {
    path: '/system',
    component: Layout,
    alwaysShow: true,
    meta: {
      icon: 'location-outline',
      title: '系统管理'
    },
    children: [
      {
        path: 'site',
        name: 'Site',
        component: () => import('@/views/system/site/index'),
        meta: {icon: 'star-on', title: '网站管理'}
      },
      {
        path: 'global',
        name: 'Global',
        component: () => import('@/views/system/global/index'),
        meta: {icon: 'star-on', title: '系统设置'}
      }
    ]
  },
  {
    path: '*',
    component: () => import('@/views/error-page/404'),
    hidden: true
  }
]

export default new Router({
  // mode: 'history', // require service support
  base: '/admin/',
  // scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
})

export const asyncRouterMap = [
  { path: '*', redirect: '/404', hidden: true }
]