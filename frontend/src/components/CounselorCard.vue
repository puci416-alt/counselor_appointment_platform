<template>
    <el-card class="counselor-card" shadow="hover" @click="$emit('click')">
        <div class="card-body">
            <el-avatar :size="80" :src="counselor.avatar">
                {{ counselor.name?.charAt(0) }}
            </el-avatar>
            <div class="info">
                <h3 class="name">{{ counselor.name }}</h3>
                <p class="title">{{ counselor.title || '心理咨询师' }}</p>
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
    </el-card>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
    counselor: {
        type: Object,
        required: true
    }
})

defineEmits(['click'])

const specialtyList = computed(() => {
    const s = props.counselor.specialties
    if (!s) return []
    return s.split(',').slice(0, 3)
})
</script>

<style scoped>
.counselor-card {
    cursor: pointer;
    margin-bottom: 20px;
    transition: transform 0.2s;
}

.counselor-card:hover {
    transform: translateY(-4px);
}

.card-body {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 8px 0;
}

.info {
    margin-top: 12px;
    width: 100%;
}

.name {
    font-size: 17px;
    font-weight: 600;
    margin: 0 0 4px;
    color: #303133;
}

.title {
    font-size: 12px;
    color: #909399;
    margin: 0 0 12px;
}

.specialties {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 6px;
    margin-bottom: 12px;
    min-height: 24px;
}

.price {
    color: #f56c6c;
    font-weight: 600;
}

.price .num {
    font-size: 20px;
}

.price .unit {
    font-size: 12px;
    color: #909399;
    font-weight: normal;
}
</style>