<template>
  <div class="page-wrapper">
    <el-card shadow="never">
      <div class="search-bar">
        <el-form :inline="true" :model="queryForm" class="search-form">
          <el-form-item label="物料编码">
            <el-input v-model="queryForm.code" placeholder="请输入物料编码" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="物料名称">
            <el-input v-model="queryForm.name" placeholder="请输入物料名称" clearable style="width: 180px" @keyup.enter="loadData" />
          </el-form-item>
          <el-form-item label="仓库">
            <el-select v-model="queryForm.warehouse" placeholder="请选择" clearable style="width: 160px">
              <el-option v-for="w in warehouseOptions" :key="w" :label="w" :value="w" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData">
              <el-icon><Search /></el-icon>查询
            </el-button>
            <el-button @click="resetQuery">
              <el-icon><RefreshLeft /></el-icon>重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="pageList" border stripe style="width: 100%; margin-top: 12px">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="物料编码" prop="code" width="130" />
        <el-table-column label="物料名称" prop="name" width="160" />
        <el-table-column label="规格" prop="spec" width="140" />
        <el-table-column label="仓库" prop="warehouse" width="140" />
        <el-table-column label="库区" prop="area" width="120" />
        <el-table-column label="库位" prop="location" width="120" />
        <el-table-column label="库存数量" prop="stock" width="110" align="center" />
        <el-table-column label="安全库存" prop="safeStock" width="110" align="center" />
        <el-table-column label="单位" prop="unit" width="90" align="center" />
        <el-table-column label="状态" prop="status" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.stock < row.safeStock ? 'danger' : 'success'" size="small">
              {{ row.stock < row.safeStock ? '低于安全库存' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="库存详情" width="680px" :close-on-click-modal="false">
      <el-form label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="物料编码">{{ form.code }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="物料名称">{{ form.name }}</el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="规格">{{ form.spec }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位">{{ form.unit }}</el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="仓库">{{ form.warehouse }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库区">{{ form.area }}</el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="库位">{{ form.location }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存数量">{{ form.stock }}</el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="安全库存">{{ form.safeStock }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-tag :type="form.stock < form.safeStock ? 'danger' : 'success'" size="small">
                {{ form.stock < form.safeStock ? '低于安全库存' : '正常' }}
              </el-tag>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

const dialogVisible = ref(false)

const warehouseOptions = ['一号原料仓', '二号原料仓', '成品仓 A', '半成品仓', '辅料仓']

const dataList = ref([
  { id: 1, code: 'M00001', name: '不锈钢板', spec: '1220*2440*2mm', warehouse: '一号原料仓', area: '原料A区', location: 'L01-A01', stock: 200, safeStock: 50, unit: '张' },
  { id: 2, code: 'M00002', name: '铝合金型材', spec: '6063-T5 2m', warehouse: '一号原料仓', area: '原料A区', location: 'L01-A02', stock: 30, safeStock: 80, unit: '根' },
  { id: 3, code: 'M00003', name: '铜线圈', spec: '0.8mm', warehouse: '二号原料仓', area: '原料B区', location: 'L02-A01', stock: 150, safeStock: 60, unit: 'kg' },
  { id: 4, code: 'B00001', name: '半成品装配A', spec: 'A100', warehouse: '半成品仓', area: '半成品区', location: 'L04-A01', stock: 45, safeStock: 30, unit: '件' },
  { id: 5, code: 'F00001', name: '工控机箱', spec: 'IPC-610L', warehouse: '成品仓 A', area: '成品A区', location: 'L03-A01', stock: 8, safeStock: 10, unit: '台' },
  { id: 6, code: 'A00001', name: '内六角螺丝', spec: 'M4x8', warehouse: '辅料仓', area: '辅料区', location: 'L05-A01', stock: 320, safeStock: 500, unit: '包' }
])

const queryForm = reactive({
  code: '',
  name: '',
  warehouse: '',
  page: 1,
  pageSize: 10
})

const total = ref(dataList.value.length)

const filterList = computed(() => {
  const codeKw = normalize(queryForm.code)
  const nameKw = normalize(queryForm.name)
  const whKw = normalize(queryForm.warehouse)
  const list = dataList.value.filter((r) => {
    const ok1 = codeKw ? normalize(r.code).includes(codeKw) : true
    const ok2 = nameKw ? normalize(r.name).includes(nameKw) : true
    const ok3 = whKw ? normalize(r.warehouse) === whKw : true
    return ok1 && ok2 && ok3
  })
  total.value = list.length
  return list
})

const pageList = computed(() => {
  const start = (queryForm.page - 1) * queryForm.pageSize
  return filterList.value.slice(start, start + queryForm.pageSize)
})

function normalize(val) {
  return (val == null ? '' : String(val)).trim().toLowerCase()
}

function loadData() {
  queryForm.page = 1
  ElMessage.success('查询完成')
}

function resetQuery() {
  queryForm.code = ''
  queryForm.name = ''
  queryForm.warehouse = ''
  queryForm.page = 1
}

const form = reactive({
  id: null,
  code: '',
  name: '',
  spec: '',
  warehouse: '',
  area: '',
  location: '',
  stock: 0,
  safeStock: 0,
  unit: ''
})

function handleDetail(row) {
  Object.assign(form, JSON.parse(JSON.stringify(row)))
  dialogVisible.value = true
}

onMounted(() => {
  total.value = dataList.value.length
})
</script>

<style lang="scss" scoped>
.page-wrapper {
  padding: 12px;
}
.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #fafbfc;
  border: 1px solid #ebeef5;
  border-radius: 6px;
}
.search-form :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 12px;
}
.search-form :deep(.el-form-item__label) {
  color: #606266;
  font-weight: 500;
}
.pagination-bar {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
