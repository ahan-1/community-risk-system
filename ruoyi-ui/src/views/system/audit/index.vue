<template>
  <div class="app-container risk-audit-page">
    <el-card shadow="never" class="intro-card">
      <div class="intro-header">
        <div>
          <div class="intro-title">上报记录审核</div>
          <div class="intro-desc">
            管理员可查看待审核记录，参考系统自动研判结果，并对最终风险分数、风险等级和审核意见进行人工复核。
          </div>
        </div>
        <el-tag type="danger" effect="dark">仅管理员可见</el-tag>
      </div>
      <div class="status-shortcuts">
        <el-button
          v-for="item in statusTabs"
          :key="item.key"
          :type="activeStatusTab === item.key ? 'primary' : 'default'"
          size="mini"
          @click="switchStatusTab(item.key)"
        >
          {{ item.label }}
        </el-button>
      </div>
    </el-card>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="76px" class="search-form">
      <el-form-item label="风险标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入风险标题"
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
      <el-form-item label="风险等级" prop="riskLevel">
        <el-select v-model="queryParams.riskLevel" placeholder="请选择等级" clearable>
          <el-option label="低危" :value="1" />
          <el-option label="中危" :value="2" />
          <el-option label="高危" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已驳回" :value="2" />
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
      <el-table-column label="自动研判分数" align="center" prop="keywordScore" width="120" />
      <el-table-column label="最终风险分数" align="center" prop="finalScore" width="120" />
      <el-table-column label="最终风险等级" align="center" width="120">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.riskLevel" :type="riskLevelTagType(scope.row.riskLevel)" size="small">
            {{ riskLevelLabel(scope.row.riskLevel) }}
          </el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" width="110">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)" size="small">
            {{ statusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核意见" prop="handleResult" min-width="220" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ scope.row.handleResult || "-" }}</span>
        </template>
      </el-table-column>
      <el-table-column label="上报人" align="center" prop="userId" width="100" />
      <el-table-column label="操作" align="center" width="220">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="openAudit(scope.row)">
            {{ scope.row.status === 0 ? "立即审核" : "重新复核" }}
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

    <el-dialog title="风险审核" :visible.sync="auditOpen" width="760px" append-to-body>
      <el-alert
        title="请先查看系统自动研判结果，再结合实际情况填写最终风险分数、最终风险等级和审核意见。"
        type="info"
        :closable="false"
        class="mb16"
      />
      <el-form ref="auditFormRef" :model="auditForm" :rules="auditRules" label-width="110px">
        <el-descriptions :column="2" border class="mb16">
          <el-descriptions-item label="风险标题" :span="2">{{ auditCurrent.title || "-" }}</el-descriptions-item>
          <el-descriptions-item label="风险类型">{{ typeName(auditCurrent.typeId) }}</el-descriptions-item>
          <el-descriptions-item label="自动研判分数">
            {{ auditCurrent.keywordScore == null ? "-" : auditCurrent.keywordScore }}
          </el-descriptions-item>
          <el-descriptions-item label="自动研判等级">
            <span v-if="auditCurrent.riskLevel">{{ riskLevelLabel(auditCurrent.riskLevel) }}</span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="当前最终分数">
            {{ auditCurrent.finalScore == null ? "-" : auditCurrent.finalScore }}
          </el-descriptions-item>
          <el-descriptions-item label="当前最终等级">
            <span v-if="auditCurrent.riskLevel">{{ riskLevelLabel(auditCurrent.riskLevel) }}</span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="上报人">{{ auditCurrent.userId || "-" }}</el-descriptions-item>
          <el-descriptions-item label="风险内容" :span="2">
            <div class="detail-content">{{ auditCurrent.content || "-" }}</div>
          </el-descriptions-item>
        </el-descriptions>

        <el-form-item label="最终风险分数" prop="finalScore">
          <el-input-number
            v-model="auditForm.finalScore"
            :min="0"
            :step="1"
            controls-position="right"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="最终风险等级" prop="riskLevel">
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
        <el-button @click="auditOpen = false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="审核历史" :visible.sync="historyOpen" width="820px" append-to-body>
      <el-table v-loading="historyLoading" :data="historyList">
        <el-table-column label="记录ID" align="center" prop="id" width="90" />
        <el-table-column label="审核结果" align="center" prop="auditResult" width="110">
          <template slot-scope="scope">
            <el-tag :type="scope.row.auditResult === '通过' ? 'success' : 'danger'" size="small">
              {{ scope.row.auditResult || "-" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核意见" prop="auditComment" min-width="280" show-overflow-tooltip />
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
      activeStatusTab: "pending",
      statusTabs: [
        { key: "pending", label: "待审核" },
        { key: "approved", label: "已通过" },
        { key: "rejected", label: "已驳回" },
        { key: "all", label: "全部记录" }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        status: 0,
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
          { required: true, message: "请输入最终风险分数", trigger: "blur" }
        ],
        riskLevel: [
          { required: true, message: "请选择最终风险等级", trigger: "change" }
        ],
        auditResult: [
          { required: true, message: "请选择审核结果", trigger: "change" }
        ],
        auditComment: [
          { required: true, message: "请输入审核意见", trigger: "blur" }
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
    switchStatusTab(tabKey) {
      this.activeStatusTab = tabKey
      if (tabKey === "pending") {
        this.queryParams.status = 0
      } else if (tabKey === "approved") {
        this.queryParams.status = 1
      } else if (tabKey === "rejected") {
        this.queryParams.status = 2
      } else {
        this.queryParams.status = undefined
      }
      this.handleQuery()
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
      this.syncStatusTabByQuery()
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.activeStatusTab = "pending"
      this.queryParams.status = 0
      this.handleQuery()
    },
    syncStatusTabByQuery() {
      if (this.queryParams.status === 0) {
        this.activeStatusTab = "pending"
      } else if (this.queryParams.status === 1) {
        this.activeStatusTab = "approved"
      } else if (this.queryParams.status === 2) {
        this.activeStatusTab = "rejected"
      } else {
        this.activeStatusTab = "all"
      }
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
      this.$nextTick(() => {
        if (this.$refs.auditFormRef) {
          this.$refs.auditFormRef.clearValidate()
        }
      })
    },
    submitAudit() {
      this.$refs.auditFormRef.validate(valid => {
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
.intro-card {
  margin-bottom: 16px;
}

.intro-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
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

.status-shortcuts {
  margin-top: 16px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.search-form {
  margin-top: 16px;
}

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
