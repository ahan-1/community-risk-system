<template>
  <div class="auth-page">
    <div class="auth-shell">
      <div class="brand-panel">
        <div class="brand-badge">普通用户自助注册</div>
        <h1>风险管控平台</h1>
        <p>
          注册后将自动分配为普通用户角色，可进入风险上报和我的记录模块。
          管理员菜单不会向普通用户展示。
        </p>
        <div class="brand-notice">
          <div class="notice-title">注册说明</div>
          <div class="notice-item">账号长度 2 到 20 位，密码长度 6 到 20 位。</div>
          <div class="notice-item">注册成功后可直接返回登录页，用新账号登录系统。</div>
          <div class="notice-item">如系统关闭了注册功能，页面会提示当前不允许注册。</div>
        </div>
      </div>

      <div class="form-panel">
        <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="register-form">
          <div class="form-title-wrap">
            <div class="form-subtitle">创建普通用户账号</div>
            <h2 class="form-title">{{ title }}</h2>
          </div>

          <el-form-item prop="username">
            <el-input v-model="registerForm.username" type="text" auto-complete="off" placeholder="请输入账号">
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-form-item prop="password" :rules="registerPwdValidator">
            <el-input
              v-model="registerForm.password"
              type="password"
              auto-complete="off"
              placeholder="请输入密码"
              @keyup.enter.native="handleRegister"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              auto-complete="off"
              placeholder="请再次输入密码"
              @keyup.enter.native="handleRegister"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-form-item prop="code" v-if="captchaEnabled">
            <div class="captcha-row">
              <el-input
                v-model="registerForm.code"
                auto-complete="off"
                placeholder="请输入验证码"
                @keyup.enter.native="handleRegister"
              >
                <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
              </el-input>
              <img :src="codeUrl" @click="getCode" class="captcha-img" />
            </div>
          </el-form-item>

          <el-button
            :loading="loading"
            type="primary"
            class="submit-btn"
            @click.native.prevent="handleRegister"
          >
            <span v-if="!loading">完成注册</span>
            <span v-else>正在提交...</span>
          </el-button>

          <div class="form-tools">
            <router-link class="link-type" to="/login">返回登录</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { getCodeImg, register } from "@/api/login"
import passwordRule from "@/utils/passwordRule"

export default {
  name: "Register",
  mixins: [passwordRule],
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      codeUrl: "",
      registerForm: {
        username: "",
        password: "",
        confirmPassword: "",
        code: "",
        uuid: ""
      },
      loading: false,
      captchaEnabled: true
    }
  },
  computed: {
    registerRules() {
      return {
        username: [
          { required: true, trigger: "blur", message: "请输入账号" },
          { min: 2, max: 20, message: "账号长度必须在 2 到 20 个字符之间", trigger: "blur" }
        ],
        confirmPassword: [
          { required: true, message: "请再次输入密码", trigger: "blur" },
          {
            validator: (rule, value, callback) => {
              if (this.registerForm.password !== value) {
                callback(new Error("两次输入的密码不一致"))
              } else {
                callback()
              }
            },
            trigger: "blur"
          }
        ],
        code: [
          { required: true, trigger: "change", message: "请输入验证码" }
        ]
      }
    }
  },
  created() {
    this.getCode()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.registerForm.uuid = res.uuid
        }
      })
    },
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (!valid) {
          return
        }
        this.loading = true
        register(this.registerForm).then(() => {
          const username = this.registerForm.username
          this.$alert(`账号 ${username} 注册成功，请返回登录页继续使用。`, "注册成功", {
            type: "success"
          }).then(() => {
            this.$router.push("/login")
          }).catch(() => {})
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
    radial-gradient(circle at top right, rgba(14, 116, 144, 0.18), transparent 26%),
    radial-gradient(circle at bottom left, rgba(37, 99, 235, 0.18), transparent 30%),
    linear-gradient(135deg, #eef5fb 0%, #f8fbff 55%, #d7e4ef 100%);
}

.auth-shell {
  width: 100%;
  max-width: 1080px;
  min-height: 640px;
  display: grid;
  grid-template-columns: 1fr 0.92fr;
  overflow: hidden;
  border-radius: 24px;
  box-shadow: 0 24px 80px rgba(15, 23, 42, 0.18);
  background: #fff;
}

.brand-panel {
  padding: 52px 46px;
  color: #fff;
  background: linear-gradient(155deg, #12314f 0%, #0e7490 100%);
}

.brand-badge {
  display: inline-flex;
  padding: 8px 14px;
  margin-bottom: 18px;
  border-radius: 999px;
  font-size: 12px;
  letter-spacing: 1px;
  background: rgba(255, 255, 255, 0.16);
}

.brand-panel h1 {
  margin: 0 0 16px;
  font-size: 38px;
  font-weight: 700;
}

.brand-panel p {
  margin: 0 0 28px;
  line-height: 1.9;
  color: rgba(255, 255, 255, 0.88);
}

.brand-notice {
  padding: 22px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.14);
}

.notice-title {
  margin-bottom: 12px;
  font-size: 18px;
  font-weight: 600;
}

.notice-item {
  line-height: 1.9;
  color: rgba(255, 255, 255, 0.82);
}

.form-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 36px 28px;
  background: linear-gradient(180deg, #f9fbfd 0%, #ffffff 100%);
}

.register-form {
  width: 100%;
  max-width: 390px;
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

.submit-btn {
  width: 100%;
  height: 46px;
  border: none;
  font-size: 15px;
  border-radius: 12px;
  background: linear-gradient(135deg, #0e7490 0%, #2563eb 100%);
}

.form-tools {
  margin-top: 18px;
  text-align: right;
}

.link-type {
  color: #0e7490;
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
    padding: 34px 26px;
  }

  .brand-panel h1 {
    font-size: 30px;
  }

  .form-panel {
    padding: 32px 24px 36px;
  }
}
</style>
