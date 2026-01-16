<template>
    <div>
        <el-form :inline="true" :model="searchForm" class="search-form">
        <el-row class="shadow-md p-4 bg-white" >
                <el-col :span="22" >
                                        <el-form-item label="类目名称" label-width="100px">
                                                <el-input v-model="searchForm.categoryName" placeholder="请输入类目名称" clearable />
                    </el-form-item>




                    <el-form-item label="状态" label-width="100px">
                        <el-select v-model="searchForm.status" placeholder="请选择状态" style="width:148px">
                            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </el-form-item>
                     
                </el-col>
                <el-col :span="2">
                    <div class="flex justify-end">
                        <el-button type="primary" @click="clickSearch">搜索</el-button>
                    </div>
                </el-col>
        </el-row>
        </el-form>
    </div>


    <div class="w-full mt-4 bg-white shadow-md px-4 pb-4">
    <div class="mb-2 flex justify-start items-center h-12"> 
               <el-button type="success" @click="handleAdd">
            <el-icon size="16">
                <Plus />
            </el-icon>
            <span>新增</span>
        </el-button>
        
            </div>
        <el-table :data="categoryList" style="width: 100%" v-loading="loadStatus" empty-text="没有更多了~" border>
         <div class="mb-2 flex justify-start items-center h-12"> 
               <el-button type="success" @click="handleAdd">
                   <el-icon size="16">
                    <Plus />
                   </el-icon>
                   <span>新增</span>
               </el-button>
        
            </div>            <el-table-column prop="id" label="ID" />
            <el-table-column prop="categoryName" label="类目名称" />
            <el-table-column prop="topCategoryId" label="一级类目ID" />
            <el-table-column prop="secondCategoryId" label="二级类目ID" />
            <el-table-column prop="thirdCategoryId" label="三级类目ID" />
            <el-table-column prop="statusStr" label="状态" />
            <el-table-column prop="validStr" label="状态" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column prop="updateTime" label="修改时间" />            <el-table-column fixed="right" label="操作">
                <template #default="scope">
                    <el-button type="primary" size="small" @click="clickEdit(scope.row)">
                        编辑
                    </el-button>
                    <el-button type="danger" size="small" @click="clickDelete(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination background class="mt-4 w-full flex justify-end" layout="prev, pager, next" :total="config.total"
            @current-change="handleClick" />
    </div>

<el-drawer v-model="dialogVisible" direction="rtl">
        <template #header>
        <h4>{{ action === 'add' ? '新增类目' : '编辑类目' }}</h4>
        </template>
        <template #default>
        <el-form :model="submitForm" ref="form" label-width="130px" label-position="left">                
                    <el-form-item label="类目名称" prop="categoryName" :rules="[{ required: true, message: '类目名称是必填项' }]">
                        <el-input v-model="submitForm.categoryName" placeholder="请输入类目名称" />
                    </el-form-item>
                
                
                    <el-form-item label="一级类目ID" prop="topCategoryId" :rules="[{ required: true, message: '一级类目ID是必填项' }]">
                        <el-input v-model="submitForm.topCategoryId" placeholder="请输入一级类目ID" />
                    </el-form-item>
                
                
                    <el-form-item label="二级类目ID" prop="secondCategoryId" :rules="[{ required: true, message: '二级类目ID是必填项' }]">
                        <el-input v-model="submitForm.secondCategoryId" placeholder="请输入二级类目ID" />
                    </el-form-item>
                
                
                    <el-form-item label="三级类目ID" prop="thirdCategoryId" :rules="[{ required: true, message: '三级类目ID是必填项' }]">
                        <el-input v-model="submitForm.thirdCategoryId" placeholder="请输入三级类目ID" />
                    </el-form-item>
                
                
                    <el-form-item label="状态" prop="status" :rules="[{ required: true, message: '状态是必填项' }]">
                        <el-select v-model="submitForm.status" placeholder="请选择状态" style="width:148px">
                            <el-option v-for="item in statusSubmitOptions" :key="item.value" :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </el-form-item>
                
        </el-form>
        </template>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="handleCancel">取消</el-button>
                <el-button type="primary" @click="onSubmit">确定</el-button>
            </div>
        </template>
    </el-drawer>
</template>

<script setup>
import { onMounted, getCurrentInstance, ref, reactive } from 'vue';

const { proxy } = getCurrentInstance()

// 请求参数
const config = reactive({
    pageNo: 1,
    pageSize: 10,
    total: 0,
})

// 枚举
const statusOptions = ref([])
const statusSubmitOptions = ref([])

// dialog展示控制
const dialogVisible = ref(false)

// 搜索参数
const searchForm = reactive({
    categoryName: '',
    topCategoryId: '',
    secondCategoryId: '',
    thirdCategoryId: '',
    status: ''})

// table加载状态
const loadStatus = ref(true)

// list列表
const categoryList = ref([])

// 弹窗 - 状态
const action = ref('add');

onMounted: {
    queryCategoryList()
}

// 查询
async function queryCategoryList() {
    loadStatus.value = true;
    const { data: data } = await proxy.$api.queryCategoryList(config)
    loadStatus.value = false;
    categoryList.value = data.list
    config.total = data.total
statusOptions.value = data.statuss
statusSubmitOptions.value = data.statuss.filter(x=>x.value !== 0)}

// 分页点击
function handleClick(pageNo) {
    config.pageNo = pageNo
    queryCategoryList()
}

// 搜索点击
function clickSearch() {
    config.categoryName = searchForm.categoryName

    if (searchForm.topCategoryId.toString().length > 0) {
        config.topCategoryId = searchForm.topCategoryId
    }
    if (searchForm.secondCategoryId.toString().length > 0) {
        config.secondCategoryId = searchForm.secondCategoryId
    }
    if (searchForm.thirdCategoryId.toString().length > 0) {
        config.thirdCategoryId = searchForm.thirdCategoryId
    }
    if (searchForm.status.toString().length > 0) {
        config.status = searchForm.status
    }    queryCategoryList()
}

// 弹窗 - 新增
function handleAdd() {
    dialogVisible.value = true
    action.value = 'add'
}

// 弹窗 - 编辑
async function clickEdit(item) {
    action.value = 'edit'
    dialogVisible.value = true

    proxy.$nextTick(() => {
        submitForm.id = item.id
        submitForm.categoryName = item.categoryName

        submitForm.topCategoryId = item.topCategoryId

        submitForm.secondCategoryId = item.secondCategoryId

        submitForm.thirdCategoryId = item.thirdCategoryId

        submitForm.status = item.status
    });
}

// form - 新增/修改
let submitForm = reactive({
    categoryName: '',
    topCategoryId: '',
    secondCategoryId: '',
    thirdCategoryId: '',
    status: ''})

// 提交 - 新增/修改
async function onSubmit() {
    proxy.$refs.form.validate(async (valid, fields) => {
        if (valid) {
            if (action.value === 'add') {
                submitForm.id = null
                const res = await proxy.$api.addCategory(submitForm)
                resetAndToast(res)
            } else {
                const res = await proxy.$api.modifyCategory(submitForm)
                resetAndToast(res)
            }
        } else {
            proxy.$toast.validerr()
        }
    })
}

// 成功提示
function resetAndToast(res) {
    if (res) {
        dialogVisible.value = false
        proxy.$refs.form.resetFields()
        queryCategoryList()
        proxy.$toast.success()
    }
}

// 按钮 - 取消
function handleCancel() {
    dialogVisible.value = false
    proxy.$nextTick(() => {
        proxy.$refs.form.resetFields();
    });

}

// 按钮 - 删除
function clickDelete(item) {
    proxy.$toast.confirm(async () => {
        const res = await proxy.$api.removeCategory({ id: item.id })
        queryCategoryList()
        if (res) {
            proxy.$toast.success()
        }
    })
}

</script>

<style lang="less" scoped>

.search-form :deep(.el-form-item) {
    margin-bottom: 0;
    display: inline-flex;
    align-items: center;
}

.search-form :deep(.el-form-item__content) {
    display: flex;
    align-items: center;
}
</style>
