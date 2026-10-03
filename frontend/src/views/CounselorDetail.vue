<template>
    <div class="detail-page" v-loading="loading">
        <!-- 咨询师信息 -->
        <el-card v-if="counselor" class="info-card">
            <div class="info-header">
                <el-avatar :size="100" :src="counselor.avatar">
                    {{ counselor.name?.charAt(0) }}
                </el-avatar>
                <div class="info-main">
                    <h2>{{ counselor.name }}</h2>
                    <p class="title">{{ counselor.title }}</p>
                    <div class="specialties">
                        <el-tag v-for="(tag, idx) in specialtyList" :key="idx" size="small" type="info" effect="plain">
                            {{ tag }}
                        </el-tag>
                    </div>
                    <div class="price">
                        <span class="num">¥{{ counselor.price }}</span>
                        <span class="unit">/ 次</span>
                    </div>
                </div>
            </div>

            <el-divider />

            <div class="section">
                <h4>简介</h4>
                <p>{{ counselor.introduction || '暂无简介' }}</p>
            </div>

            <div class="section">
                <h4>资质</h4>
                <p>{{ counselor.qualification || '暂无资质信息' }}</p>
            </div>
        </el-card>

        <!-- 可预约时段 -->
        <el-card class="schedule-card">
            <template #header>
                <div class="card-header">
                    <span>可预约时段</span>
                    <el-button link type="primary" @click="loadSchedules">
                        <el-icon>
                            <Refresh />
                        </el-icon>
                        刷新
                    </el-button>
                </div>
            </template>

            <el-empty v-if="!loading && groupedSchedules.length === 0" description="暂无可预约时段" />

            <div v-else class="schedule-list">
                <div v-for="group in groupedSchedules" :key="group.date" class="schedule-day">
                    <div class="date-label">
                        <el-icon>
                            <Calendar />
                        </el-icon>
                        {{ formatDate(group.date) }}
                    </div>
                    <div class="time-slots">
                        <el-button v-for="item in group.items" :key="item.id"
                            :type="item.status === 0 ? 'primary' : 'info'" :disabled="item.status !== 0" plain
                            @click="goBook(item)">
                            {{ formatTime(item.startTime) }} - {{ formatTime(item.endTime) }}
                            <span v-if="item.status === 1" class="booked">（已约）</span>
                            <span v-if="item.status === 2" class="booked">（不可约）</span>
                        </el-button>
                    </div>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCounselorDetail, getSchedules } from '@/api/counselor'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const counselor = ref(null)
const schedules = ref([])

const specialtyList = computed(() => {
    const s = counselor.value?.specialties
    if (!s) return []
    return s.split(',')
})

const groupedSchedules = computed(() => {
    const map = {}
    schedules.value.forEach(item => {
        const date = item.scheduleDate
        if (!map[date]) map[date] = []
        map[date].push(item)
    })
    return Object.keys(map)
        .sort()
        .map(date => ({ date, items: map[date] }))
})

function formatDate(dateStr) {
    if (!dateStr) return ''
    const d = new Date(dateStr)
    const week = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][d.getDay()]
    return `${dateStr} ${week}`
}

function formatTime(timeStr) {
    if (!timeStr) return ''
    return timeStr.substring(0, 5)
}

async function loadData() {
    loading.value = true
    try {
        const id = route.params.id
        counselor.value = await getCounselorDetail(id)
        await loadSchedules()
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

async function loadSchedules() {
    try {
        const id = route.params.id
        schedules.value = await getSchedules(id)
    } catch (e) {
        console.error(e)
    }
}

function goBook(schedule) {
    router.push({
        path: '/appointment/create',
        query: {
            counselorId: route.params.id,
            scheduleId: schedule.id,
            date: schedule.scheduleDate,
            startTime: schedule.startTime,
            endTime: schedule.endTime
        }
    })
}

onMounted(loadData)
</script>

<style scoped>
.detail-page {
    max-width: 900px;
    margin: 0 auto;
}

.info-card {
    margin-bottom: 20px;
}

.info-header {
    display: flex;
    gap: 24px;
    align-items: center;
}

.info-main h2 {
    margin: 0 0 8px;
}

.info-main .title {
    color: #909399;
    margin: 0 0 12px;
}

.specialties {
    display: flex;
    gap: 6px;
    margin-bottom: 12px;
}

.price {
    color: #f56c6c;
    font-weight: 600;
}

.price .num {
    font-size: 22px;
}

.price .unit {
    font-size: 13px;
    color: #909399;
    font-weight: normal;
}

.section {
    margin-top: 16px;
}

.section h4 {
    margin: 0 0 8px;
    color: #303133;
}

.section p {
    color: #606266;
    line-height: 1.6;
    margin: 0;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.schedule-day {
    margin-bottom: 20px;
}

.date-label {
    display: flex;
    align-items: center;
    gap: 6px;
    font-weight: 600;
    margin-bottom: 10px;
    color: #303133;
}

.time-slots {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
}

.time-slots .el-button {
    min-width: 160px;
}

.booked {
    color: #909399;
    font-size: 12px;
}
</style>