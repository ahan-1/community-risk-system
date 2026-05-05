<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="风险标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入风险标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="风险类型ID(关联risk_type.id)" prop="typeId">
        <el-input
          v-model="queryParams.typeId"
          placeholder="请输入风险类型ID(关联risk_type.id)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关键词匹配得分" prop="keywordScore">
        <el-input
          v-model="queryParams.keywordScore"
          placeholder="请输入关键词匹配得分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="综合风险评分" prop="finalScore">
        <el-input
          v-model="queryParams.finalScore"
          placeholder="请输入综合风险评分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="风险等级(1低 2中 3高)" prop="riskLevel">
        <el-input
          v-model="queryParams.riskLevel"
          placeholder="请输入风险等级(1低 2中 3高)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="上报人ID(关联sys_user.user_id)" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入上报人ID(关联sys_user.user_id)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="风险图片" prop="imageUrl">
        <el-input
          v-model="queryParams.imageUrl"
          placeholder="请输入风险图片"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:info:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:info:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:info:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:info:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="infoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="风险标题" align="center" prop="title" />
      <el-table-column label="风险详细描述" align="center" prop="content" />
      <el-table-column label="风险类型ID(关联risk_type.id)" align="center" prop="typeId" />
      <el-table-column label="关键词匹配得分" align="center" prop="keywordScore" />
      <el-table-column label="综合风险评分" align="center" prop="finalScore" />
      <el-table-column label="风险等级(1低 2中 3高)" align="center" prop="riskLevel" />
      <el-table-column label="状态(0待审核 1已通过 2已驳回)" align="center" prop="status" />
      <el-table-column label="上报人ID(关联sys_user.user_id)" align="center" prop="userId" />
      <el-table-column label="风险图片" align="center" prop="imageUrl" />
      <el-table-column label="处理结果" align="center" prop="handleResult" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:info:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:info:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改风险信息主对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="风险标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入风险标题" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="风险详细描述">
              <editor v-model="form.content" :min-height="192"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="风险类型ID(关联risk_type.id)" prop="typeId">
              <el-input v-model="form.typeId" placeholder="请输入风险类型ID(关联risk_type.id)" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关键词匹配得分" prop="keywordScore">
              <el-input v-model="form.keywordScore" placeholder="请输入关键词匹配得分" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="综合风险评分" prop="finalScore">
              <el-input v-model="form.finalScore" placeholder="请输入综合风险评分" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="风险等级(1低 2中 3高)" prop="riskLevel">
              <el-input v-model="form.riskLevel" placeholder="请输入风险等级(1低 2中 3高)" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="上报人ID(关联sys_user.user_id)" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入上报人ID(关联sys_user.user_id)" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="风险图片" prop="imageUrl">
              <el-input v-model="form.imageUrl" placeholder="请输入风险图片" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="处理结果" prop="handleResult">
              <el-input v-model="form.handleResult" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInfo, getInfo, delInfo, addInfo, updateInfo } from "@/api/system/info"

export default {
  name: "RiskInfo",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 风险信息主表格数据
      infoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        content: null,
        typeId: null,
        keywordScore: null,
        finalScore: null,
        riskLevel: null,
        status: null,
        userId: null,
        imageUrl: null,
        handleResult: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        title: [
          { required: true, message: "风险标题不能为空", trigger: "blur" }
        ],
        userId: [
          { required: true, message: "上报人ID(关联sys_user.user_id)不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询风险信息主列表 */
    getList() {
      this.loading = true
      listInfo(this.queryParams).then(response => {
        this.infoList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        title: null,
        content: null,
        typeId: null,
        keywordScore: null,
        finalScore: null,
        riskLevel: null,
        status: null,
        userId: null,
        imageUrl: null,
        handleResult: null,
        createTime: null,
        updateTime: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加风险信息主"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getInfo(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改风险信息主"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateInfo(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addInfo(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除风险信息主编号为"' + ids + '"的数据项？').then(function() {
        return delInfo(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/info/export', {
        ...this.queryParams
      }, `info_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
