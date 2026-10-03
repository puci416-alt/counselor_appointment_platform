<template>
    <div class="counselor-list">
        <!-- 搜索栏 -->
        <div class="search-bar">
            <el-input v-model="query.specialty" placeholder="输入擅长领域，如：焦虑、抑郁、青少年" clearable style="max-width: 400px"
                @keyup.enter="handleSearch">
                <template #prefix>
                    <el-icon>
                        <Search />
                    </el-icon>
                </template>
            </el-input>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
        </div>

        <!-- 列表 -->
        <div v-loading="loading" class="list-container">
            <el-empty v-if="!loading && list.length === 0" description="暂无咨询师" />

            <el-row v-else :gutter="20">
                <el-col v-for="item in list" :key="item.id" :xs="24" :sm="12" :md="8" :lg="6">
                    <CounselorCard :counselor="item" @click="goDetail(item.id)" />
                </el-col>
            </el-row>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="total > 0">
            <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total"
                :page-sizes="[8, 12, 16]" layout="total, sizes, prev, pager, next, jumper" @current-change="loadData"
                @size-change="loadData" />
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCounselorPage } from '@/api/counselor'
import CounselorCard from '@/components/CounselorCard.vue'

const router = useRouter()

const loading = ref(false)
const list = ref([])
const total = ref(0)

const query = reactive({
    pageNum: 1,
    pageSize: 8,
    specialty: ''
})

async function loadData() {
    loading.value = true
    try {
        const data = await getCounselorPage(query)
        list.value = data.records || []
        total.value = data.total || 0
    } catch (e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

function handleSearch() {
    query.pageNum = 1
    loadData()
}

function handleReset() {
    query.specialty = ''
    query.pageNum = 1
    loadData()
}

function goDetail(id) {
    router.push(`/counselor/${id}`)
}

onMounted(loadData)
</script>

<style scoped>
.search-bar {
    display: flex;
    gap: 12px;
    margin-bottom: 24px;
}

.list-container {
    min-height: 300px;
}

.pagination {
    margin-top: 32px;
    display: flex;
    justify-content: center;
}
</style>