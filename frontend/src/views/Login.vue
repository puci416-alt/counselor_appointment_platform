<template>
    <div class="login-page">
        <el-card class="login-card">
            <template #header>
                <div class="login-header">
                    <el-icon :size="28" color="#409eff">
                        <Sunny />
                    </el-icon>
                    <h2>心理咨询预约平台</h2>
                </div>
            </template>

            <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="handleLogin">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="form.username" placeholder="请输入用户名" size="large">
                        <template #prefix>
                            <el-icon>
                                <User />
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <el-form-item label="密码" prop="password">
                    <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password>
                        <template #prefix>
                            <el-icon>
                                <Lock />
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleLogin">
                        登录
                    </el-button>
                </el-form-item>

                <div class="tips">
                    <span>测试账号：test / 123456</span>
                </div>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/user'

const router = useRouter()
const route = useRoute()

const formRef = ref()
const loading = ref(false)

const form = reactive({
    username: '',
    password: ''
})

const rules = {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
    await formRef.value.validate()

    loading.value = true
    try {
        const token = await login(form)
        localStorage.setItem('token', token)
        localStorage.setItem('username', form.username)
        ElMessage.success('登录成功')
        // 如果有重定向参数，跳回去；否则去咨询师列表
        const redirect = route.query.redirect || '/counselor'
        router.push(redirect)
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.login-page {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #e0f2fe 0%, #f0f9ff 100%);
}

.login-card {
    width: 400px;
    padding: 8px;
}

.login-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
}

.login-header h2 {
    margin: 0;
    color: #303133;
    font-size: 20px;
}

.tips {
    text-align: center;
    color: #909399;
    font-size: 13px;
}
</style>