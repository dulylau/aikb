<template>
  <div class="component">
    <div class="header"><h3>项目成员</h3><el-button type="primary" size="small" @click="showAddDialog = true">添加成员</el-button></div>
    <el-table :data="localMembers" style="width:100%">
      <el-table-column prop="userId" label="用户ID" />
      <el-table-column prop="role" label="角色" width="120">
        <template #default="{ row }">
          <el-select v-model="row.role" size="small" @change="(v:string)=>handleRoleChange(row.userId, v)">
            <el-option label="OWNER" value="OWNER" /><el-option label="DEVELOPER" value="DEVELOPER" /><el-option label="OBSERVER" value="OBSERVER" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="80">
        <template #default="{ row }"><el-button type="danger" size="small" @click="handleRemove(row.userId)">移除</el-button></template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="showAddDialog" title="添加成员" width="400px">
      <el-form><el-form-item label="用户ID"><el-input v-model.number="newUserId" type="number" placeholder="请输入用户ID" /></el-form-item>
        <el-form-item label="角色"><el-select v-model="newRole" style="width:100%"><el-option label="OWNER" value="OWNER" /><el-option label="DEVELOPER" value="DEVELOPER" /><el-option label="OBSERVER" value="OBSERVER" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="showAddDialog = false">取消</el-button><el-button type="primary" @click="handleAdd">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useProjectStore } from '../stores/project'
import type { ProjectMember } from '../types'

const props = defineProps<{ projectId: number; members: ProjectMember[] }>()
const store = useProjectStore()
const localMembers = ref<ProjectMember[]>([...props.members])
const showAddDialog = ref(false); const newUserId = ref<number | null>(null); const newRole = ref('DEVELOPER')

watch(() => props.members, (v) => { localMembers.value = [...v] })

async function handleRoleChange(userId: number, role: string) {
  await store.updateMemberRole(props.projectId, userId, role)
  ElMessage.success('角色已更新')
}
async function handleRemove(userId: number) {
  await store.removeMember(props.projectId, userId)
  localMembers.value = localMembers.value.filter(m => m.userId !== userId)
  ElMessage.success('成员已移除')
}
async function handleAdd() {
  if (!newUserId.value) return
  const m = await store.addMember(props.projectId, newUserId.value, newRole.value)
  localMembers.value.push(m)
  showAddDialog.value = false
  newUserId.value = null
  ElMessage.success('成员添加成功')
}
</script>

<style scoped>
.component { margin-bottom: 24px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.header h3 { margin: 0; }
</style>
