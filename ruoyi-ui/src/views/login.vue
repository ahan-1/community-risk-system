<template>
  <div class="auth-page">
    <div class="auth-shell">
      <div class="brand-panel">
        <div class="brand-badge">社区风险治理毕业设计</div>
        <h1>风险管控平台</h1>
        <p>
          面向社区风险上报、自动研判、人工审核与统计分析的一体化平台。
          普通用户完成上报，管理员负责审核与研判治理。
        </p>
        <div class="brand-points">
          <div class="point-item">
            <span class="point-index">01</span>
            <div>
              <div class="point-title">快速上报</div>
              <div class="point-desc">填写标题、内容与风险类型后即可提交待审核记录。</div>
            </div>
          </div>
          <div class="point-item">
            <span class="point-index">02</span>
            <div>
              <div class="point-title">自动研判</div>
              <div class="point-desc">系统自动计算分数和风险等级，结果仅管理员可见。</div>
            </div>
          </div>
          <div class="point-item">
            <span class="point-index">03</span>
            <div>
              <div class="point-title">审核闭环</div>
              <div class="point-desc">管理员可调整最终分数、等级，并留下审核意见与历史记录。</div>
            </div>
          </div>
        </div>
      </div>

      <div class="form-panel">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
          <div class="form-title-wrap">
            <div class="form-subtitle">账号登录</div>
            <h2 class="form-title">{{ title }}</h2>
          </div>

          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="请输入账号"
            >
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="请输入密码"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-form-item prop="code" v-if="captchaEnabled">
            <div class="captcha-row">
              <el-input
                v-model="loginForm.code"
                auto-complete="off"
                placeholder="请输入验证码"
                @keyup.enter.native="handleLogin"
              >
                <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
              </el-input>
              <img :src="codeUrl" @click="getCode" class="captcha-img" />
            </div>
          </el-form-item>

          <div class="form-tools">
            <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
            <router-link v-if="register" class="link-type" to="/register">没有账号？立即注册</router-link>
          </div>

          <el-button
            :loading="loading"
            type="primary"
            class="submit-btn"
            @click.native.prevent="handleLogin"
          >
            <span v-if="!loading">登录系统</span>
            <span v-else>正在登录...</span>
          </el-button>

          <div class="login-tip">
            普通用户登录后可看到“风险上报、我的记录”；管理员登录后可进入规则设置、上报记录与统计分析。
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from "@/utils/jsencrypt"

export default {
  name: "Login",
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入密码" }
        ],
        code: [
          { required: true, trigger: "change", message: "请输入验证码" }
        ]
      },
      loading: false,
      captchaEnabled: true,
      register: true,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get("rememberMe")
      this.loginForm = {
        ...this.loginForm,
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) {
          return
        }
        this.loading = true
        if (this.loginForm.rememberMe) {
          Cookies.set("username", this.loginForm.username, { expires: 30 })
          Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
          Cookies.set("rememberMe", this.loginForm.rememberMe, { expires: 30 })
        } else {
          Cookies.remove("username")
          Cookies.remove("password")
          Cookies.remove("rememberMe")
        }
        this.$store.dispatch("Login", this.loginForm).then(() => {
          this.$router.push({ path: this.redirect || "/" }).catch(() => {})
        }).catch(() => {
          this.loading = false
          if (this.captchaEnabled) {
            this.getCode()
          }
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.auth-page {
  min-height: 100vh;
  padding: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(circle at top left, rgba(34, 107, 255, 0.2), transparent 30%),
    radial-gradient(circle at bottom right, rgba(17, 153, 142, 0.18), transparent 28%),
    linear-gradient(135deg, #0f172a 0%, #12314f 48%, #f2f6fb 48%, #f7fafc 100%);
}

.auth-shell {
  width: 100%;
  max-width: 1180px;
  min-height: 680px;
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  overflow: hidden;
  border-radius: 24px;
  box-shadow: 0 24px 80px rgba(15, 23, 42, 0.22);
  background: rgba(255, 255, 255, 0.9);
}

.brand-panel {
  padding: 56px 52px;
  color: #fff;
  background:
    linear-gradient(160deg, rgba(8, 37, 72, 0.94), rgba(14, 83, 129, 0.92)),
    url("../assets/images/login-background.jpg") center/cover no-repeat;
}

.brand-badge {
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  margin-bottom: 18px;
  border-radius: 999px;
  font-size: 12px;
  letter-spacing: 1px;
  background: rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(8px);
}

.brand-panel h1 {
  margin: 0 0 18px;
  font-size: 42px;
  line-height: 1.15;
  font-weight: 700;
}

.brand-panel p {
  margin: 0 0 32px;
  max-width: 520px;
  line-height: 1.9;
  color: rgba(255, 255, 255, 0.86);
}

.brand-points {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.point-item {
  display: flex;
  gap: 14px;
  align-items: flex-start;
  padding: 18px 20px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.point-index {
  width: 34px;
  height: 34px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  color: #0f172a;
  font-weight: 700;
  background: #f8d76a;
  flex-shrink: 0;
}

.point-title {
  margin-bottom: 4px;
  font-size: 16px;
  font-weight: 600;
}

.point-desc {
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.8);
}

.form-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 32px;
  background: linear-gradient(180deg, #f9fbfd 0%, #ffffff 100%);
}

.login-form {
  width: 100%;
  max-width: 400px;
}

.form-title-wrap {
  margin-bottom: 28px;
}

.form-subtitle {
  margin-bottom: 8px;
  color: #0e7490;
  font-size: 13px;
  letter-spacing: 1px;
}

.form-title {
  margin: 0;
  color: #162033;
  font-size: 30px;
  font-weight: 700;
}

.input-icon {
  height: 39px;
  width: 14px;
  margin-left: 2px;
}

.captcha-row {
  display: grid;
  grid-template-columns: 1fr 120px;
  gap: 12px;
}

.captcha-img {
  width: 120px;
  height: 40px;
  border-radius: 10px;
  cursor: pointer;
  object-fit: cover;
  border: 1px solid #d8e3ee;
}

.form-tools {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 22px;
  color: #606266;
}

.link-type {
  color: #0e7490;
}

.submit-btn {
  width: 100%;
  height: 46px;
  border: none;
  font-size: 15px;
  border-radius: 12px;
  background: linear-gradient(135deg, #0e7490 0%, #2563eb 100%);
}

.login-tip {
  margin-top: 18px;
  line-height: 1.8;
  color: #7c8798;
  font-size: 13px;
}

::v-deep .el-input__inner {
  height: 44px;
  border-radius: 12px;
  border-color: #d6dfeb;
}

@media screen and (max-width: 992px) {
  .auth-page {
    padding: 16px;
  }

  .auth-shell {
    min-height: auto;
    grid-template-columns: 1fr;
  }

  .brand-panel {
    padding: 36px 28px;
  }

  .brand-panel h1 {
    font-size: 32px;
  }

  .form-panel {
    padding: 32px 24px 36px;
  }
}
</style>
