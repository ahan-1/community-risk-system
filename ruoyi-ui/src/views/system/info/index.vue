<template>
  <div class="app-container risk-info-page">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="风险上报" name="report">
        <el-row :gutter="20">
          <el-col :lg="15" :md="24">
            <el-card shadow="never" class="panel-card">
              <div slot="header" class="card-header">
                <span>填写上报信息</span>
              </div>
              <el-alert
                title="提交后系统会自动完成研判并进入待审核状态，普通用户不可查看分数和风险等级。"
                type="info"
                :closable="false"
                class="mb16"
              />
              <el-form ref="reportForm" :model="reportForm" :rules="reportRules" label-width="88px">
                <el-form-item label="风险标题" prop="title">
                  <el-input
                    v-model="reportForm.title"
                    maxlength="100"
                    show-word-limit
                    placeholder="请输入风险标题"
                  />
                </el-form-item>
                <el-form-item label="风险类型" prop="typeId">
                  <el-select
                    v-model="reportForm.typeId"
                    placeholder="请选择风险类型"
                    filterable
                    clearable
                    style="width: 100%;"
                  >
                    <el-option
                      v-for="item in typeOptions"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="风险内容" prop="content">
                  <el-input
                    v-model="reportForm.content"
                    type="textarea"
                    :rows="8"
                    maxlength="1000"
                    show-word-limit
                    placeholder="请详细描述风险情况、涉及人员、时间地点等关键信息"
                  />
                </el-form-item>
                <el-form-item label="图片链接" prop="imageUrl">
                  <el-input
                    v-model="reportForm.imageUrl"
                    placeholder="如有图片，可填写图片地址"
                  />
                </el-form-item>
                <el-form-item class="form-actions">
                  <el-button type="primary" :loading="submitLoading" @click="submitReport">提交上报</el-button>
                  <el-button @click="resetReportForm">重置</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-col>

          <el-col :lg="9" :md="24">
            <el-card shadow="never" class="panel-card">
              <div slot="header" class="card-header">
                <span>流程说明</span>
              </div>
              <div class="flow-list">
                <div class="flow-item">
                  <span class="flow-index">1</span>
                  <div>
                    <div class="flow-title">用户填写并提交</div>
                    <div class="flow-desc">填写标题、内容、风险类型后提交，系统创建待审核记录。</div>
                  </div>
                </div>
                <div class="flow-item">
                  <span class="flow-index">2</span>
                  <div>
                    <div class="flow-title">系统自动研判</div>
                    <div class="flow-desc">系统根据规则自动计算分数和风险等级，这些结果仅管理员可见。</div>
                  </div>
                </div>
                <div class="flow-item">
                  <span class="flow-index">3</span>
                  <div>
                    <div class="flow-title">管理员审核处理</div>
                    <div class="flow-desc">管理员查看待审核记录，可调整分数、等级并填写审核意见。</div>
                  </div>
                </div>
                <div class="flow-item">
                  <span class="flow-index">4</span>
                  <div>
                    <div class="flow-title">查看审核状态</div>
                    <div class="flow-desc">你可以在“我的记录”中查看当前状态和处理结果。</div>
                  </div>
                </div>
              </div>
              <el-alert
                v-if="!typeOptions.length"
                title="当前未查询到可用风险类型，请先确认后台已启用类型数据。"
                type="warning"
                :closable="false"
              />
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane name="records">
        <span slot="label">{{ isAdmin ? "风险记录" : "我的记录" }}</span>

        <el-form
          ref="queryForm"
          :model="queryParams"
          size="small"
          :inline="true"
          v-show="showSearch"
          label-width="68px"
        >
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
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <div class="table-toolbar">
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
        </div>

        <el-table v-loading="loading" :data="infoList">
          <el-table-column label="编号" align="center" prop="id" width="90" />
          <el-table-column label="风险标题" prop="title" min-width="220" show-overflow-tooltip>
            <template slot-scope="scope">
              <el-button type="text" @click="openDetail(scope.row)">{{ scope.row.title }}</el-button>
            </template>
          </el-table-column>
          <el-table-column label="风险类型" align="center" min-width="140">
            <template slot-scope="scope">
              <span>{{ typeName(scope.row.typeId) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="审核状态" align="center" width="120">
            <template slot-scope="scope">
              <el-tag :type="statusTagType(scope.row.status)" size="small">
                {{ statusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            v-if="isAdmin"
            label="关键词得分"
            align="center"
            prop="keywordScore"
            width="120"
          />
          <el-table-column
            v-if="isAdmin"
            label="综合评分"
            align="center"
            prop="finalScore"
            width="120"
          />
          <el-table-column v-if="isAdmin" label="风险等级" align="center" width="120">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.riskLevel" :type="riskLevelTagType(scope.row.riskLevel)" size="small">
                {{ riskLevelLabel(scope.row.riskLevel) }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="处理结果" prop="handleResult" min-width="200" show-overflow-tooltip>
            <template slot-scope="scope">
              <span>{{ scope.row.handleResult || "-" }}</span>
            </template>
          </el-table-column>
          <el-table-column v-if="isAdmin" label="上报人" align="center" prop="userId" width="100" />
          <el-table-column label="操作" align="center" width="120" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="openDetail(scope.row)">查看详情</el-button>
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
      </el-tab-pane>
    </el-tabs>

    <el-dialog title="风险详情" :visible.sync="detailOpen" width="680px" append-to-body>
      <div v-loading="detailLoading" class="detail-body">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="风险标题" :span="2">
            {{ detailData.title || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="风险类型">
            {{ typeName(detailData.typeId) }}
          </el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <el-tag :type="statusTagType(detailData.status)" size="small">
              {{ statusLabel(detailData.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="isAdmin" label="关键词得分">
            {{ detailData.keywordScore == null ? "-" : detailData.keywordScore }}
          </el-descriptions-item>
          <el-descriptions-item v-if="isAdmin" label="综合评分">
            {{ detailData.finalScore == null ? "-" : detailData.finalScore }}
          </el-descriptions-item>
          <el-descriptions-item v-if="isAdmin" label="风险等级">
            <span v-if="detailData.riskLevel">{{ riskLevelLabel(detailData.riskLevel) }}</span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item v-if="isAdmin" label="上报人">
            {{ detailData.userId || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="图片链接" :span="2">
            <span v-if="detailData.imageUrl">{{ detailData.imageUrl }}</span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="处理结果" :span="2">
            {{ detailData.handleResult || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="风险内容" :span="2">
            <div class="detail-content">{{ detailData.content || "-" }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addInfo, getInfo, listInfo, listInfoTypeOptions } from "@/api/system/info"
import { checkRole } from "@/utils/permission"

export default {
  name: "RiskInfo",
  data() {
    return {
      activeTab: "report",
      submitLoading: false,
      loading: false,
      detailLoading: false,
      showSearch: true,
      total: 0,
      infoList: [],
      typeOptions: [],
      detailOpen: false,
      detailData: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        status: undefined
      },
      reportForm: {
        title: "",
        content: "",
        typeId: undefined,
        imageUrl: ""
      },
      reportRules: {
        title: [
          { required: true, message: "请输入风险标题", trigger: "blur" }
        ],
        typeId: [
          { required: true, message: "请选择风险类型", trigger: "change" }
        ],
        content: [
          { required: true, message: "请输入风险内容", trigger: "blur" }
        ]
      }
    }
  },
  computed: {
    isAdmin() {
      return checkRole(["admin"])
    }
  },
  created() {
    this.syncActiveTabByRoute()
    this.loadTypeOptions()
    this.getList()
  },
  watch: {
    $route() {
      this.syncActiveTabByRoute()
    },
    activeTab(value) {
      const expectedTab = this.resolveTabByRoute()
      if (value !== expectedTab) {
        this.pushRouteByTab(value)
      }
    }
  },
  methods: {
    resolveTabByRoute() {
      const currentPath = this.$route.path || ""
      if (currentPath.indexOf("/my-report") === 0) {
        return "records"
      }
      return "report"
    },
    syncActiveTabByRoute() {
      this.activeTab = this.resolveTabByRoute()
    },
    pushRouteByTab(tabName) {
      const targetPath = tabName === "records" ? "/my-report" : "/risk-report"
      if (this.$route.path !== targetPath) {
        this.$router.push(targetPath)
      }
    },
    loadTypeOptions() {
      listInfoTypeOptions().then(response => {
        this.typeOptions = response.data || []
      })
    },
    getList() {
      this.loading = true
      listInfo(this.queryParams).then(response => {
        this.infoList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    submitReport() {
      this.$refs.reportForm.validate(valid => {
        if (!valid) {
          return
        }
        this.$modal.confirm("确认提交该风险信息吗？提交后将进入待审核状态。").then(() => {
          this.submitLoading = true
          return addInfo(this.reportForm)
        }).then(() => {
          this.$modal.msgSuccess("风险上报成功")
          this.resetReportForm()
          this.activeTab = "records"
          this.getList()
        }).finally(() => {
          this.submitLoading = false
        })
      })
    },
    resetReportForm() {
      this.reportForm = {
        title: "",
        content: "",
        typeId: undefined,
        imageUrl: ""
      }
      this.resetForm("reportForm")
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    openDetail(row) {
      this.detailLoading = true
      this.detailOpen = true
      getInfo(row.id).then(response => {
        this.detailData = response.data || {}
      }).finally(() => {
        this.detailLoading = false
      })
    },
    typeName(typeId) {
      if (typeId == null) {
        return "-"
      }
      const match = this.typeOptions.find(item => String(item.id) === String(typeId))
      return match ? match.name : typeId
    },
    statusLabel(status) {
      const statusMap = {
        0: "待审核",
        1: "已通过",
        2: "已驳回"
      }
      return statusMap[status] || "未知"
    },
    statusTagType(status) {
      const tagMap = {
        0: "warning",
        1: "success",
        2: "danger"
      }
      return tagMap[status] || "info"
    },
    riskLevelLabel(level) {
      const levelMap = {
        1: "低危",
        2: "中危",
        3: "高危"
      }
      return levelMap[level] || "未知"
    },
    riskLevelTagType(level) {
      const tagMap = {
        1: "success",
        2: "warning",
        3: "danger"
      }
      return tagMap[level] || "info"
    }
  }
}
</script>

<style scoped>
.panel-card {
  min-height: 100%;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
}

.mb16 {
  margin-bottom: 16px;
}

.table-toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
}

.form-actions {
  margin-bottom: 0;
}

.flow-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.flow-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  padding: 14px;
  background: #f8fafc;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.flow-index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #e8f3ff;
  color: #2f6bff;
  font-weight: 600;
  flex-shrink: 0;
}

.flow-title {
  margin-bottom: 4px;
  color: #303133;
  font-weight: 600;
}

.flow-desc {
  color: #606266;
  line-height: 1.6;
}

.detail-body {
  min-height: 120px;
}

.detail-content {
  white-space: pre-wrap;
  line-height: 1.7;
  color: #303133;
}
</style>
