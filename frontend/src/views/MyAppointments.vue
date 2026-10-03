<template>
    <div class="my-appointments">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>我的预约</span>
                    <el-button link type="primary" @click="loadData">
                        <el-icon>
                            <Refresh />
                        </el-icon>
                        刷新
                    </el-button>
                </div>
            </template>

            <div v-loading="loading">
                <el-empty v-if="!loading && list.length === 0" description="暂无预约记录" />

                <el-table v-else :data="list" stripe>
                    <el-table-column prop="orderNo" label="订单号" width="200" show-overflow-tooltip />
                    <el-table-column label="预约时间" width="180">
                        <template #default="{ row }">
                            {{ formatDateTime(row.appointmentTime) }}
                        </template>
                    </el-table-column>
                    <el-table-column label="状态" width="120">
                        <template #default="{ row }">
                            <el-tag :type="statusType(row.status)" size="small">
                                {{ statusText(row.status) }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="remark" label="备注" show-overflow-tooltip />
                    <el-table-column label="操作" width="120" fixed="right">
                        <template #default="{ row }">
                            <el-button v-if="canCancel(row.status)" type="danger" size="small" link
                                @click="handleCancel(row)">
                                取消预约
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>

                <div class="pagination" v-if="total > 0">
                    <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total"
                        layout="total, prev, pager, next" @current-change="loadData" />
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyAppointments, cancelAppointment } from '@/api/appointment'

const loading = ref(false)
const list = ref([])
const total = ref(0)

const query = reactive({
    page: 1,
    size: 10
})

async function loadData() {
    loading.value = true
    try {
        const data = await getMyAppointments(query)
        list.value = data.records || []
        total.value = data.total || 0
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

async function handleCancel(row) {
    try {
        await ElMessageBox.confirm(
            `确定取消订单 ${row.orderNo} 吗？`,
            '取消预约',
            { type: 'warning' }
        )
        await cancelAppointment(row.id)
        ElMessage.success('已取消')
        loadData()
    } catch (e) {
        if (e !== 'cancel') console.error(e)
    }
}

function statusText(status) {
    return {
        0: '待确认',
        1: '已确认',
        2: '已完成',
        3: '已取消'
    }[status] || '未知'
}

function statusType(status) {
    return {
        0: 'warning',
        1: 'success',
        2: 'info',
        3: 'danger'
    }[status] || ''
}

function canCancel(status) {
    return status === 0 || status === 1
}

function formatDateTime(dt) {
    if (!dt) return ''
    return dt.replace('T', ' ').substring(0, 16)
}

onMounted(loadData)
</script>

<style scoped>
.my-appointments {
    max-width: 1100px;
    margin: 0 auto;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}
</style>