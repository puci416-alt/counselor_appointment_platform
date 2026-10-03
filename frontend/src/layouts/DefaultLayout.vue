<template>
    <el-container class="layout">
        <el-header class="header">
            <div class="header-inner">
                <div class="logo" @click="router.push('/')">
                    <el-icon>
                        <Sunny />
                    </el-icon>
                    <span>心理咨询预约平台</span>
                </div>
                <el-menu mode="horizontal" :default-active="activeMenu" router class="menu">
                    <el-menu-item index="/counselor">咨询师</el-menu-item>
                    <el-menu-item index="/my-appointments">我的预约</el-menu-item>
                </el-menu>
                <div class="user-area">
                    <template v-if="!token">
                        <el-button type="primary" size="small" @click="router.push('/login')">
                            登录
                        </el-button>
                    </template>
                    <template v-else>
                        <el-dropdown @command="handleCommand">
                            <span class="user-info">
                                <el-icon>
                                    <User />
                                </el-icon>
                                {{ username }}
                                <el-icon>
                                    <ArrowDown />
                                </el-icon>
                            </span>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </template>
                </div>
            </div>
        </el-header>

        <el-main class="main">
            <router-view />
        </el-main>

        <el-footer class="footer">
            © 2026 心理咨询预约平台 · 演示项目
        </el-footer>
    </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()

const token = ref(localStorage.getItem('token') || '')
const username = ref(localStorage.getItem('username') || '用户')

const activeMenu = computed(() => {
    if (route.path.startsWith('/counselor')) return '/counselor'
    return route.path
})

function handleCommand(cmd) {
    if (cmd === 'logout') {
        ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
            .then(() => {
                localStorage.removeItem('token')
                localStorage.removeItem('username')
                token.value = ''
                ElMessage.success('已退出登录')
                router.push('/counselor')
            })
            .catch(() => { })
    }
}
</script>

<style scoped>
.layout {
    min-height: 100vh;
}

.header {
    background: #fff;
    border-bottom: 1px solid #e4e7ed;
    padding: 0;
    position: sticky;
    top: 0;
    z-index: 100;
}

.header-inner {
    display: flex;
    align-items: center;
    max-width: 1200px;
    margin: 0 auto;
    height: 60px;
    padding: 0 20px;
}

.logo {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    font-weight: 600;
    color: #409eff;
    cursor: pointer;
    margin-right: 40px;
}

.menu {
    flex: 1;
    border-bottom: none !important;
}

.user-area {
    display: flex;
    align-items: center;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 4px;
    cursor: pointer;
    color: #333;
}

.main {
    max-width: 1200px;
    margin: 0 auto;
    width: 100%;
    padding: 24px 20px;
}

.footer {
    text-align: center;
    color: #909399;
    font-size: 13px;
    line-height: 60px;
}
</style>