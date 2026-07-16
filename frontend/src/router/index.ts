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
  ],
})

export default router
