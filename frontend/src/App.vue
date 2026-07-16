<template>
  <div class="app-container">
    <el-menu
      :default-active="activeMenu"
      mode="horizontal"
      router
      class="app-nav"
    >
      <el-menu-item index="/products">产品管理</el-menu-item>
      <el-menu-item index="/projects">项目管理</el-menu-item>
    </el-menu>
    <div class="app-content">
      <router-view />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => {
  if (route.path.startsWith('/projects')) return '/projects'
  return '/products'
})
</script>

<style>
body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
.app-container {
  min-height: 100vh;
  background: #f0f2f5;
}
.app-nav {
  padding: 0 24px;
}
.app-content {
  padding: 0;
}
</style>
