<template>
  <div class="app-container risk-type-page">
    <el-card shadow="never" class="intro-card">
      <div class="intro-title">风险类型设置</div>
      <div class="intro-desc">维护前台上报时可选的风险类型，停用后该类型将不再提供给普通用户选择。</div>
    </el-card>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="76px" class="search-form">
      <el-form-item label="类型名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入类型名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="启用" :value="0" />
          <el-option label="停用" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="toolbar-row">
      <div>
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['system:type:add']">
          新增类型
        </el-button>
      </div>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </div>

    <el-table v-loading="loading" :data="typeList">
      <el-table-column label="编号" align="center" prop="id" width="100" />
      <el-table-column label="类型名称" align="center" prop="name" min-width="220" />
      <el-table-column label="状态" align="center" width="120">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 0 ? 'success' : 'info'" size="small">
            {{ scope.row.status === 0 ? "启用" : "停用" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" min-width="180" />
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleUpdate(scope.row)" v-hasPermi="['system:type:edit']">
            编辑
          </el-button>
          <el-button size="mini" type="text" @click="handleDelete(scope.row)" v-hasPermi="['system:type:remove']">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="dialogTitle" :visible.sync="open" width="520px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="类型名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入类型名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">启用</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listType, getType, delType, addType, updateType } from "@/api/system/type"

export default {
  name: "RiskType",
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      typeList: [],
      open: false,
      dialogTitle: "",
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        status: undefined
      },
      form: {
        id: undefined,
        name: "",
        status: 0
      },
      rules: {
        name: [
          { required: true, message: "请输入类型名称", trigger: "blur" }
        ],
        status: [
          { required: true, message: "请选择状态", trigger: "change" }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listType(this.queryParams).then(response => {
        this.typeList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    resetFormData() {
      this.form = {
        id: undefined,
        name: "",
        status: 0
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleAdd() {
      this.resetFormData()
      this.open = true
      this.dialogTitle = "新增风险类型"
      this.$nextTick(() => {
        if (this.$refs.formRef) {
          this.$refs.formRef.clearValidate()
        }
      })
    },
    handleUpdate(row) {
      getType(row.id).then(response => {
        this.form = response.data || {}
        this.open = true
        this.dialogTitle = "编辑风险类型"
        this.$nextTick(() => {
          if (this.$refs.formRef) {
            this.$refs.formRef.clearValidate()
          }
        })
      })
    },
    cancel() {
      this.open = false
      this.resetFormData()
    },
    submitForm() {
      this.$refs.formRef.validate(valid => {
        if (!valid) {
          return
        }
        const request = this.form.id ? updateType(this.form) : addType(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.id ? "修改成功" : "新增成功")
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      this.$modal.confirm(`确认删除风险类型“${row.name}”吗？`).then(() => {
        return delType(row.id)
      }).then(() => {
        this.$modal.msgSuccess("删除成功")
        this.getList()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.intro-card {
  margin-bottom: 16px;
}

.intro-title {
  margin-bottom: 6px;
  color: #162033;
  font-size: 18px;
  font-weight: 700;
}

.intro-desc {
  color: #606266;
  line-height: 1.7;
}

.search-form {
  margin-top: 16px;
}

.toolbar-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
</style>
