<template>
    <div class="header">
        <el-form :inline="true" :model="searchForm">
            <el-row>
                <el-col :span="3">
                    <el-button type="info" color="#40485b" @click="queryCopyrightList()">
                        <el-icon size="16">
                            <Refresh />
                        </el-icon>
                    </el-button>
                    <el-button type="success" @click="handleAdd">
                        <el-icon size="16">
                            <Plus />
                        </el-icon>
                        <span>新增</span>
                    </el-button>
                </el-col>
                <el-col :span="21" style="text-align: right;">
                    <el-form-item label="IP名称">
                                                <el-input v-model="searchForm.copyrightName" placeholder="请输入IP名称" clearable />
                    </el-form-item>

                    <el-form-item label="授权方">
                                                <el-input v-model="searchForm.cpName" placeholder="请输入授权方" clearable />
                    </el-form-item>

                    <el-form-item label="状态">
                        <el-select v-model="searchForm.status" placeholder="请选择状态" style="width:168px">
                            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </el-form-item>

                    <el-form-item label="IP海报">
                                                <el-input v-model="searchForm.placardUrl" placeholder="请输入IP海报" clearable />
                    </el-form-item>

                    <el-form-item label="授权到期时间">
                                                <el-input v-model="searchForm.expireTime" placeholder="请输入授权到期时间" clearable />
                    </el-form-item>

                    <el-form-item label="授权类目">
                                                <el-input v-model="searchForm.topCategoryId" placeholder="请输入授权类目" clearable />
                    </el-form-item>

                    <el-form-item label="预留清货天数">
                                                <el-input v-model="searchForm.clearDays" placeholder="请输入预留清货天数" clearable />
                    </el-form-item>

                    <el-form-item label="授权书">
                                                <el-input v-model="searchForm.letterUrl" placeholder="请输入授权书" clearable />
                    </el-form-item>

                    <el-form-item label="状态">
                        <el-select v-model="searchForm.valid" placeholder="请选择状态" style="width:168px">
                            <el-option v-for="item in validOptions" :key="item.value" :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" @click="clickSearch">搜索</el-button>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
    </div>


    <div class="table">
        <el-table :data="copyrightList" style="width: 100%" v-loading="loadStatus" empty-text="没有更多了~" border>
            <el-table-column prop="id" label="ID" />
            <el-table-column prop="copyrightName" label="IP名称" />
            <el-table-column prop="cpName" label="授权方" />
            <el-table-column prop="statusStr" label="状态" />
            <el-table-column prop="placardUrl" label="IP海报" />
            <el-table-column prop="expireTime" label="授权到期时间" />
            <el-table-column prop="topCategoryId" label="授权类目" />
            <el-table-column prop="clearDays" label="预留清货天数" />
            <el-table-column prop="letterUrl" label="授权书" />
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
        <el-pagination background class="pager" layout="prev, pager, next" :total="config.total"
            @current-change="handleClick" />
    </div>

    <el-dialog v-model="dialogVisible" :title="action === 'add' ? '新增IP' : '编辑IP'" draggable="true" width="650">
        <el-form :inline="true" :model="submitForm" ref="form">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="IP名称" prop="copyrightName" :rules="[{ required: true, message: 'IP名称是必填项' }]">
                        <el-input v-model="submitForm.copyrightName" placeholder="请输入IP名称" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="授权方" prop="cpName" :rules="[{ required: true, message: '授权方是必填项' }]">
                        <el-input v-model="submitForm.cpName" placeholder="请输入授权方" />
                    </el-form-item>
                </el-col>            </el-row>

            <el-row>
                <el-col :span="12">
                    <el-form-item label="状态" prop="status" :rules="[{ required: true, message: '状态是必填项' }]">
                        <el-select v-model="submitForm.status" placeholder="请选择状态" style="width:168px">
                            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="IP海报" prop="placardUrl" :rules="[{ required: true, message: 'IP海报是必填项' }]">
                        <el-input v-model="submitForm.placardUrl" placeholder="请输入IP海报" />
                    </el-form-item>
                </el-col>            </el-row>

            <el-row>
                <el-col :span="12">
                    <el-form-item label="授权到期时间" prop="expireTime" :rules="[{ required: true, message: '授权到期时间是必填项' }]">
                        <el-input v-model="submitForm.expireTime" placeholder="请输入授权到期时间" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="授权类目" prop="topCategoryId" :rules="[{ required: true, message: '授权类目是必填项' }]">
                        <el-input v-model="submitForm.topCategoryId" placeholder="请输入授权类目" />
                    </el-form-item>
                </el-col>            </el-row>

            <el-row>
                <el-col :span="12">
                    <el-form-item label="预留清货天数" prop="clearDays" :rules="[{ required: true, message: '预留清货天数是必填项' }]">
                        <el-input v-model="submitForm.clearDays" placeholder="请输入预留清货天数" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="授权书" prop="letterUrl" :rules="[{ required: true, message: '授权书是必填项' }]">
                        <el-input v-model="submitForm.letterUrl" placeholder="请输入授权书" />
                    </el-form-item>
                </el-col>            </el-row>

            <el-row>
                <el-col :span="12">
                    <el-form-item label="状态" prop="valid" :rules="[{ required: true, message: '状态是必填项' }]">
                        <el-select v-model="submitForm.valid" placeholder="请选择状态" style="width:168px">
                            <el-option v-for="item in validOptions" :key="item.value" :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-col>            </el-row>

        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="handleCancel">取消</el-button>
                <el-button type="primary" @click="onSubmit">确定</el-button>
            </div>
        </template>
    </el-dialog>
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
const validOptions = ref([])

// dialog展示控制
const dialogVisible = ref(false)

// 搜索参数
const searchForm = reactive({
    copyrightName: '',
    cpName: '',
    status: '',
    placardUrl: '',
    expireTime: '',
    topCategoryId: '',
    clearDays: '',
    letterUrl: '',
    valid: ''})

// table加载状态
const loadStatus = ref(true)

// list列表
const copyrightList = ref([])

// 弹窗 - 状态
const action = ref('add');

onMounted: {
    queryCopyrightList()
}

// 查询
async function queryCopyrightList() {
    loadStatus.value = true;
    const { data: data } = await proxy.$api.queryCopyrightList(config)
    loadStatus.value = false;
    copyrightList.value = data.list
    config.total = data.total
statusOptions.value = data.statuss
validOptions.value = data.valids}

// 分页点击
function handleClick(pageNo) {
    config.pageNo = pageNo
    queryCopyrightList()
}

// 搜索点击
function clickSearch() {
    config.copyrightName = searchForm.copyrightName
    config.cpName = searchForm.cpName
    if (searchForm.status.toString().length > 0) {
        config.status = searchForm.status
    }
    config.placardUrl = searchForm.placardUrl
    config.expireTime = searchForm.expireTime
    if (searchForm.topCategoryId.toString().length > 0) {
        config.topCategoryId = searchForm.topCategoryId
    }
    if (searchForm.clearDays.toString().length > 0) {
        config.clearDays = searchForm.clearDays
    }
    config.letterUrl = searchForm.letterUrl
    if (searchForm.valid.toString().length > 0) {
        config.valid = searchForm.valid
    }    queryCopyrightList()
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
        submitForm.copyrightName = item.copyrightName

        submitForm.cpName = item.cpName

        submitForm.status = item.status

        submitForm.placardUrl = item.placardUrl

        submitForm.expireTime = item.expireTime

        submitForm.topCategoryId = item.topCategoryId

        submitForm.clearDays = item.clearDays

        submitForm.letterUrl = item.letterUrl

        submitForm.valid = item.valid
    });
}

// form - 新增/修改
let submitForm = reactive({
    copyrightName: '',
    cpName: '',
    status: '',
    placardUrl: '',
    expireTime: '',
    topCategoryId: '',
    clearDays: '',
    letterUrl: '',
    valid: ''})

// 提交 - 新增/修改
async function onSubmit() {
    proxy.$refs.form.validate(async (valid, fields) => {
        if (valid) {
            if (action.value === 'add') {
                submitForm.id = null
                const res = await proxy.$api.addCopyright(submitForm)
                resetAndToast(res)
            } else {
                const res = await proxy.$api.modifyCopyright(submitForm)
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
        queryCopyrightList()
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
        const res = await proxy.$api.removeCopyright({ id: item.id })
        queryCopyrightList()
        if (res) {
            proxy.$toast.success()
        }
    })
}

</script>

<style lang="less" scoped>
.table {
    position: relative;

    .pager {
        position: absolute;
        bottom: -50px;
        right: 0;
    }
}

.header {
    display: flex;

    form {
        width: 100%;
    }
}
</style>