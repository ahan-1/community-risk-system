<template>
  <div class="app-container risk-audit-page">
    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="68px">
      <el-form-item label="风险标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入风险标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已驳回" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="风险等级" prop="riskLevel">
        <el-select v-model="queryParams.riskLevel" placeholder="请选择等级" clearable>
          <el-option label="低危" :value="1" />
          <el-option label="中危" :value="2" />
          <el-option label="高危" :value="3" />
        </el-select>
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
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <right-toolbar @queryTable="getList" />
    </div>

    <el-table v-loading="loading" :data="infoList">
      <el-table-column label="编号" align="center" prop="id" width="90" />
      <el-table-column label="风险标题" prop="title" min-width="220" show-overflow-tooltip />
      <el-table-column label="风险类型" align="center" min-width="120">
        <template slot-scope="scope">
          {{ typeName(scope.row.typeId) }}
        </template>
      </el-table-column>
      <el-table-column label="关键词得分" align="center" prop="keywordScore" width="110" />
      <el-table-column label="综合评分" align="center" prop="finalScore" width="110" />
      <el-table-column label="风险等级" align="center" width="110">
        <template slot-scope="scope">
          <el-tag :type="riskLevelTagType(scope.row.riskLevel)" size="small">
            {{ riskLevelLabel(scope.row.riskLevel) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" width="110">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)" size="small">
            {{ statusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="处理结果" prop="handleResult" min-width="180" show-overflow-tooltip />
      <el-table-column label="上报人" align="center" prop="userId" width="100" />
      <el-table-column label="操作" align="center" width="180">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="openAudit(scope.row)">
            {{ scope.row.status === 0 ? "审核" : "重新审核" }}
          </el-button>
          <el-button type="text" size="mini" @click="openHistory(scope.row)">审核历史</el-button>
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

    <el-dialog title="风险审核" :visible.sync="auditOpen" width="720px" append-to-body>
      <el-form ref="auditForm" :model="auditForm" :rules="auditRules" label-width="92px">
        <el-descriptions :column="2" border class="mb16">
          <el-descriptions-item label="风险标题" :span="2">{{ auditCurrent.title || "-" }}</el-descriptions-item>
          <el-descriptions-item label="风险类型">{{ typeName(auditCurrent.typeId) }}</el-descriptions-item>
          <el-descriptions-item label="关键词得分">{{ auditCurrent.keywordScore == null ? "-" : auditCurrent.keywordScore }}</el-descriptions-item>
          <el-descriptions-item label="风险内容" :span="2">
            <div class="detail-content">{{ auditCurrent.content || "-" }}</div>
          </el-descriptions-item>
        </el-descriptions>

        <el-form-item label="综合评分" prop="finalScore">
          <el-input-number v-model="auditForm.finalScore" :min="0" :step="1" controls-position="right" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="风险等级" prop="riskLevel">
          <el-radio-group v-model="auditForm.riskLevel">
            <el-radio :label="1">低危</el-radio>
            <el-radio :label="2">中危</el-radio>
            <el-radio :label="3">高危</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核结果" prop="auditResult">
          <el-radio-group v-model="auditForm.auditResult">
            <el-radio label="通过">通过</el-radio>
            <el-radio label="驳回">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" prop="auditComment">
          <el-input
            v-model="auditForm.auditComment"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
            placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" :loading="submitLoading" @click="submitAudit">提交审核</el-button>
        <el-button @click="auditOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="审核历史" :visible.sync="historyOpen" width="760px" append-to-body>
      <el-table v-loading="historyLoading" :data="historyList">
        <el-table-column label="记录ID" align="center" prop="id" width="90" />
        <el-table-column label="审核结果" align="center" prop="auditResult" width="110" />
        <el-table-column label="审核意见" prop="auditComment" min-width="260" show-overflow-tooltip />
        <el-table-column label="审核人" align="center" prop="auditUserId" width="100" />
        <el-table-column label="审核时间" align="center" prop="auditTime" min-width="180" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { listInfo, listInfoTypeOptions, auditInfo } from "@/api/system/info"
import { listAudit } from "@/api/system/audit"

export default {
  name: "RiskAuditManage",
  data() {
    return {
      loading: false,
      submitLoading: false,
      historyLoading: false,
      total: 0,
      infoList: [],
      typeOptions: [],
      auditOpen: false,
      historyOpen: false,
      historyList: [],
      auditCurrent: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        status: undefined,
        riskLevel: undefined,
        typeId: undefined
      },
      auditForm: {
        riskId: undefined,
        finalScore: 0,
        riskLevel: 1,
        auditResult: "通过",
        auditComment: ""
      },
      auditRules: {
        finalScore: [
          { required: true, message: "请输入综合评分", trigger: "blur" }
        ],
        riskLevel: [
          { required: true, message: "请选择风险等级", trigger: "change" }
        ],
        auditResult: [
          { required: true, message: "请选择审核结果", trigger: "change" }
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
      listInfoTypeOptions().then(res => {
        this.typeOptions = res.data || []
      })
    },
    getList() {
      this.loading = true
      listInfo(this.queryParams).then(res => {
        this.infoList = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    openAudit(row) {
      this.auditCurrent = { ...row }
      this.auditForm = {
        riskId: row.id,
        finalScore: row.finalScore == null ? 0 : row.finalScore,
        riskLevel: row.riskLevel || 1,
        auditResult: row.status === 2 ? "驳回" : "通过",
        auditComment: row.handleResult || ""
      }
      this.auditOpen = true
      this.$nextTick(() => this.resetForm("auditForm"))
    },
    submitAudit() {
      this.$refs.auditForm.validate(valid => {
        if (!valid) {
          return
        }
        this.submitLoading = true
        auditInfo(this.auditForm).then(() => {
          this.$modal.msgSuccess("审核提交成功")
          this.auditOpen = false
          this.getList()
        }).finally(() => {
          this.submitLoading = false
        })
      })
    },
    openHistory(row) {
      this.historyOpen = true
      this.historyLoading = true
      listAudit({
        pageNum: 1,
        pageSize: 100,
        riskId: row.id
      }).then(res => {
        this.historyList = res.rows || []
      }).finally(() => {
        this.historyLoading = false
      })
    },
    typeName(typeId) {
      const match = this.typeOptions.find(item => String(item.id) === String(typeId))
      return match ? match.name : "-"
    },
    statusLabel(status) {
      const map = { 0: "待审核", 1: "已通过", 2: "已驳回" }
      return map[status] || "未知"
    },
    statusTagType(status) {
      const map = { 0: "warning", 1: "success", 2: "danger" }
      return map[status] || "info"
    },
    riskLevelLabel(level) {
      const map = { 1: "低危", 2: "中危", 3: "高危" }
      return map[level] || "未知"
    },
    riskLevelTagType(level) {
      const map = { 1: "success", 2: "warning", 3: "danger" }
      return map[level] || "info"
    }
  }
}
</script>

<style scoped>
.table-toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
}

.mb16 {
  margin-bottom: 16px;
}

.detail-content {
  white-space: pre-wrap;
  line-height: 1.7;
}
</style>
