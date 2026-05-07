<template>
  <div class="app-container risk-rule-page">
    <el-card shadow="never" class="intro-card">
      <div class="intro-title">关键词设置</div>
      <div class="intro-desc">为风险关键词配置所属类型和权重，系统会在用户上报后自动参与研判计算。</div>
    </el-card>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="76px" class="search-form">
      <el-form-item label="关键词" prop="keyword">
        <el-input
          v-model="queryParams.keyword"
          placeholder="请输入关键词"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="风险类型" prop="typeId">
        <el-select v-model="queryParams.typeId" placeholder="请选择类型" clearable filterable>
          <el-option
            v-for="item in typeOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
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

    <el-alert
      v-if="!typeOptions.length"
      title="当前还没有可用风险类型，请先到“风险类型设置”中新增并启用类型。"
      type="warning"
      :closable="false"
      class="mb16"
    />

    <div class="toolbar-row">
      <div>
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['system:rule:add']">
          新增规则
        </el-button>
      </div>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </div>

    <el-table v-loading="loading" :data="ruleList">
      <el-table-column label="编号" align="center" prop="id" width="90" />
      <el-table-column label="关键词" align="center" prop="keyword" min-width="180" />
      <el-table-column label="风险类型" align="center" min-width="160">
        <template slot-scope="scope">
          {{ typeName(scope.row.typeId) }}
        </template>
      </el-table-column>
      <el-table-column label="权重" align="center" prop="weight" width="100" />
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
          <el-button size="mini" type="text" @click="handleUpdate(scope.row)" v-hasPermi="['system:rule:edit']">
            编辑
          </el-button>
          <el-button size="mini" type="text" @click="handleDelete(scope.row)" v-hasPermi="['system:rule:remove']">
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

    <el-dialog :title="dialogTitle" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="关键词" prop="keyword">
          <el-input v-model="form.keyword" placeholder="请输入关键词" maxlength="50" />
        </el-form-item>
        <el-form-item label="风险类型" prop="typeId">
          <el-select v-model="form.typeId" placeholder="请选择风险类型" filterable clearable style="width: 100%;">
            <el-option
              v-for="item in typeOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="权重" prop="weight">
          <el-input-number
            v-model="form.weight"
            :min="0"
            :step="1"
            controls-position="right"
            style="width: 100%;"
          />
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
import { listRule, getRule, delRule, addRule, updateRule } from "@/api/system/rule"
import { listType } from "@/api/system/type"

export default {
  name: "RiskRule",
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      ruleList: [],
      typeOptions: [],
      open: false,
      dialogTitle: "",
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        keyword: undefined,
        typeId: undefined,
        status: undefined
      },
      form: {
        id: undefined,
        keyword: "",
        typeId: undefined,
        weight: 0,
        status: 0
      },
      rules: {
        keyword: [
          { required: true, message: "请输入关键词", trigger: "blur" }
        ],
        typeId: [
          { required: true, message: "请选择风险类型", trigger: "change" }
        ],
        weight: [
          { required: true, message: "请输入权重", trigger: "blur" }
        ],
        status: [
          { required: true, message: "请选择状态", trigger: "change" }
        ]
      }
    }
  },
  created() {
    this.loadTypeOptions()
    this.getList()
  },
  methods: {
    loadTypeOptions() {
      listType({ pageNum: 1, pageSize: 500, status: 0 }).then(res => {
        this.typeOptions = res.rows || []
      })
    },
    typeName(typeId) {
      if (typeId == null) {
        return "-"
      }
      const target = this.typeOptions.find(item => String(item.id) === String(typeId))
      return target ? target.name : typeId
    },
    getList() {
      this.loading = true
      listRule(this.queryParams).then(response => {
        this.ruleList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    resetFormData() {
      this.form = {
        id: undefined,
        keyword: "",
        typeId: undefined,
        weight: 0,
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
      this.dialogTitle = "新增关键词规则"
      this.$nextTick(() => {
        if (this.$refs.formRef) {
          this.$refs.formRef.clearValidate()
        }
      })
    },
    handleUpdate(row) {
      getRule(row.id).then(response => {
        this.form = response.data || {}
        this.open = true
        this.dialogTitle = "编辑关键词规则"
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
        const request = this.form.id ? updateRule(this.form) : addRule(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.id ? "修改成功" : "新增成功")
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      this.$modal.confirm(`确认删除关键词“${row.keyword}”吗？`).then(() => {
        return delRule(row.id)
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

.mb16 {
  margin-bottom: 16px;
}
</style>
