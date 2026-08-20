<template>
  <div class="tags-view-container">
    <el-scrollbar class="tags-view-wrapper">
      <div class="tags-view-content flex items-center px-4 space-x-2">
        <el-tag
          v-for="tag in visitedViews"
          :key="tag.path"
          :closable="!tag.meta?.affix"
          :type="isActive(tag) ? '' : 'info'"
          :effect="isActive(tag) ? 'dark' : 'plain'"
          size="small"
          class="tag-item cursor-pointer"
          @click="toView(tag)"
          @close="closeView(tag)"
        >
          {{ tag.meta?.title }}
        </el-tag>
      </div>
    </el-scrollbar>
    
    <!-- 右键菜单 -->
    <ul
      v-show="visible"
      :style="{ left: left + 'px', top: top + 'px' }"
      class="contextmenu"
    >
      <li @click="refreshSelectedTag">刷新页面</li>
      <li v-if="!isAffix(selectedTag)" @click="closeSelectedTag">关闭当前</li>
      <li @click="closeOthersTags">关闭其他</li>
      <li @click="closeAllTags">关闭所有</li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

interface TagView {
  path: string
  name?: string
  meta?: {
    title: string
    affix?: boolean
    icon?: string
  }
  query?: Record<string, any>
  params?: Record<string, any>
}

const route = useRoute()
const router = useRouter()

const visitedViews = ref<TagView[]>([])
const selectedTag = ref<TagView>({} as TagView)
const visible = ref(false)
const left = ref(0)
const top = ref(0)

// 当前激活的视图
const isActive = (tag: TagView) => {
  return tag.path === route.path
}

// 是否为固定标签
const isAffix = (tag: TagView) => {
  return tag.meta?.affix
}

// 初始化固定标签
const initTags = () => {
  const affixTags = [
    {
      path: '/dashboard',
      name: 'Dashboard',
      meta: {
        title: '首页',
        affix: true
      }
    }
  ]
  
  for (const tag of affixTags) {
    if (tag.name) {
      visitedViews.value.push(tag)
    }
  }
}

// 添加标签
const addView = (view: TagView) => {
  const isDuplicate = visitedViews.value.some(v => v.path === view.path)
  if (!isDuplicate) {
    visitedViews.value.push(view)
  }
}

// 删除标签
const delView = (view: TagView) => {
  const index = visitedViews.value.findIndex(v => v.path === view.path)
  if (index > -1) {
    visitedViews.value.splice(index, 1)
  }
}

// 跳转到指定视图
const toView = (view: TagView) => {
  if (view.path === route.path) return
  router.push({
    path: view.path,
    query: view.query,
    params: view.params
  })
}

// 关闭标签
const closeView = (view: TagView) => {
  delView(view)
  if (isActive(view)) {
    toLastView()
  }
}

// 跳转到最后一个视图
const toLastView = () => {
  const latestView = visitedViews.value.slice(-1)[0]
  if (latestView && latestView.path !== route.path) {
    router.push(latestView.path)
  } else {
    router.push('/')
  }
}

// 右键菜单
const openMenu = (tag: TagView, e: MouseEvent) => {
  const menuMinWidth = 105
  const offsetLeft = 240 // 侧边栏宽度
  const offsetWidth = menuMinWidth
  const maxLeft = offsetLeft + offsetWidth - 15

  if (e.clientX > maxLeft) {
    left.value = maxLeft
  } else {
    left.value = e.clientX + 15
  }

  top.value = e.clientY
  visible.value = true
  selectedTag.value = tag
}

const closeMenu = () => {
  visible.value = false
}

// 右键菜单操作
const refreshSelectedTag = () => {
  router.replace({
    path: '/redirect' + selectedTag.value.path,
    query: selectedTag.value.query
  })
}

const closeSelectedTag = () => {
  closeView(selectedTag.value)
}

const closeOthersTags = () => {
  visitedViews.value = visitedViews.value.filter(v => 
    v.path === selectedTag.value.path || isAffix(v)
  )
}

const closeAllTags = () => {
  visitedViews.value = visitedViews.value.filter(v => isAffix(v))
  router.push('/')
}

// 监听路由变化
watch(route, () => {
  addView({
    path: route.path,
    name: route.name as string,
    meta: route.meta,
    query: route.query,
    params: route.params
  })
})

// 监听点击事件关闭右键菜单
watch(visible, (value) => {
  if (value) {
    document.body.addEventListener('click', closeMenu)
  } else {
    document.body.removeEventListener('click', closeMenu)
  }
})

onMounted(() => {
  initTags()
  addView({
    path: route.path,
    name: route.name as string,
    meta: route.meta,
    query: route.query,
    params: route.params
  })
})
</script>

<style scoped>
.tags-view-container {
  @apply h-10 w-full bg-white border-b border-gray-200;
}

.tags-view-wrapper {
  height: 40px;
}

.tags-view-content {
  height: 40px;
}

.tag-item {
  @apply transition-all duration-200;
}

.tag-item:hover {
  @apply shadow-sm;
}

.contextmenu {
  @apply absolute bg-white shadow-lg border border-gray-200 rounded-md py-1 z-50 text-sm;
  min-width: 100px;
}

.contextmenu li {
  @apply px-3 py-2 cursor-pointer hover:bg-gray-50 transition-colors;
}
</style>
