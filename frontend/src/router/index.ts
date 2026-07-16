import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/products',
    },
    {
      path: '/products',
      name: 'ProductList',
      component: () => import('../views/ProductList.vue'),
    },
    {
      path: '/products/create',
      name: 'ProductCreate',
      component: () => import('../views/ProductCreate.vue'),
    },
    {
      path: '/products/:id',
      name: 'ProductDetail',
      component: () => import('../views/ProductDetail.vue'),
    },
    {
      path: '/products/:id/edit',
      name: 'ProductEdit',
      component: () => import('../views/ProductEdit.vue'),
    },
    {
      path: '/projects',
      name: 'ProjectList',
      component: () => import('../views/ProjectList.vue'),
    },
    {
      path: '/projects/create',
      name: 'ProjectCreate',
      component: () => import('../views/ProjectCreate.vue'),
    },
    {
      path: '/projects/:id',
      name: 'ProjectDetail',
      component: () => import('../views/ProjectDetail.vue'),
    },
    {
      path: '/projects/:id/edit',
      name: 'ProjectEdit',
      component: () => import('../views/ProjectEdit.vue'),
    },
  ],
})

export default router
