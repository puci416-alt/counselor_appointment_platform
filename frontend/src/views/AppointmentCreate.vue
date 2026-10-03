<template>
    <div class="create-page">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>预约确认</span>
                </div>
            </template>

            <!-- 预约信息 -->
            <el-descriptions :column="1" border class="info-block">
                <el-descriptions-item label="咨询师">
                    {{ counselorName || `ID: ${form.counselorId}` }}
                </el-descriptions-item>
                <el-descriptions-item label="预约日期">
                    {{ form.date }}
                </el-descriptions-item>
                <el-descriptions-item label="预约时间">
                    {{ form.startTime }} - {{ form.endTime }}
                </el-descriptions-item>
            </el-descriptions>

            <!-- 备注 -->
            <el-form label-position="top" class="remark-form">
                <el-form-item label="备注（可选）">
                    <el-input v-model="form.remark" type="textarea" :rows="4" placeholder="简单描述您想咨询的问题，方便咨询师提前了解"
                        maxlength="200" show-word-limit />
                </el-form-item>
            </el-form>

            <!-- 操作 -->
            <div class="actions">
                <el-button @click="router.back()">返回</el-button>
                <el-button type="primary" :loading="submitting" @click="handleSubmit">
                    确认预约
                </el-button>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createAppointment } from '@/api/appointment'
import { getCounselorDetail } from '@/api/counselor'

const route = useRoute()
const router = useRouter()

const submitting = ref(false)
const counselorName = ref('')

const form = reactive({
    counselorId: null,
    scheduleId: null,
    date: '',
    startTime: '',
    endTime: '',
    remark: ''
})

onMounted(async () => {
    // 从 query 拿数据
    form.counselorId = Number(route.query.counselorId)
    form.scheduleId = Number(route.query.scheduleId)
    form.date = route.query.date
    form.startTime = route.query.startTime
    form.endTime = route.query.endTime

    // 检查登录
    const token = localStorage.getItem('token')
    if (!token) {
        ElMessage.warning('请先登录')
        router.push({
            path: '/login',
            query: { redirect: route.fullPath }
        })
        return
    }

    // 查咨询师名字（可选，为了显示更好看）
    if (form.counselorId) {
        try {
            const c = await getCounselorDetail(form.counselorId)
            counselorName.value = c.name
        } catch (e) {
            console.error(e)
        }
    }
})

async function handleSubmit() {
    if (!form.scheduleId) {
        ElMessage.error('时段信息缺失')
        return
    }
    if (submitting.value) return    // 防止重复提交
    submitting.value = true
    try {
        await createAppointment({
            scheduleId: form.scheduleId,
            counselorId: form.counselorId,
            remark: form.remark
        })

        ElMessageBox.alert('预约成功！可在"我的预约"里查看', '提示', {
            confirmButtonText: '去看看',
            type: 'success'
        }).then(() => {
            router.push('/my-appointments')
        })
    } catch (e) {
        console.error(e)
    } finally {
        submitting.value = false
    }
}
</script>

<style scoped>
.create-page {
    max-width: 640px;
    margin: 0 auto;
}

.card-header {
    font-weight: 600;
}

.info-block {
    margin-bottom: 24px;
}

.remark-form {
    margin-bottom: 16px;
}

.actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
}
</style>