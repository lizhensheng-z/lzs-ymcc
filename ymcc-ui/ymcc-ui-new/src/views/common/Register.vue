<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>云课教育</h2>
        <p>用户注册</p>
      </div>
      <el-form :model="registerForm" :rules="rules" ref="registerForm">
        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            prefix-icon="el-icon-phone"
          ></el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-input
            v-model="registerForm.code"
            placeholder="请输入验证码"
            prefix-icon="el-icon-message"
            style="width: 60%"
          ></el-input>
          <el-button
            @click="sendCode"
            :disabled="codeBtnDisabled"
            style="width: 38%; margin-left: 2%"
          >{{ codeBtnText }}</el-button>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
          ></el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="el-icon-lock"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            style="width: 100%"
            @click="handleRegister"
          >注册</el-button>
        </el-form-item>
        <div class="register-footer">
          <a href="#/login">已有账号？立即登录</a>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else {
        if (this.registerForm.confirmPassword !== '') {
          this.$refs.registerForm.validateField('confirmPassword')
        }
        callback()
      }
    }
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    return {
      registerForm: {
        phone: '',
        code: '',
        password: '',
        confirmPassword: ''
      },
      rules: {
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入验证码', trigger: 'blur' }
        ],
        password: [
          { required: true, validator: validatePass, trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validatePass2, trigger: 'blur' }
        ]
      },
      loading: false,
      codeBtnDisabled: false,
      codeBtnText: '获取验证码',
      countdown: 60
    }
  },
  methods: {
    sendCode() {
      if (!this.registerForm.phone) {
        this.$message.warning('请先输入手机号')
        return
      }
      if (!/^1[3-9]\d{9}$/.test(this.registerForm.phone)) {
        this.$message.warning('手机号格式不正确')
        return
      }

      this.$http.get(`/common/verifyCode/sendSmsCode/${this.registerForm.phone}`)
        .then(res => {
          if (res.data.success) {
            this.$message.success('验证码已发送')
            this.codeBtnDisabled = true
            this.countdown = 60
            this.timer = setInterval(() => {
              this.countdown--
              this.codeBtnText = `${this.countdown}秒后重发`
              if (this.countdown <= 0) {
                clearInterval(this.timer)
                this.codeBtnDisabled = false
                this.codeBtnText = '获取验证码'
              }
            }, 1000)
          } else {
            this.$message.error(res.data.message || '发送失败')
          }
        })
    },
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$http.post('/user/user/register', {
            phone: this.registerForm.phone,
            code: this.registerForm.code,
            password: this.registerForm.password
          }).then(res => {
            if (res.data.success) {
              this.$message.success('注册成功')
              this.$router.push('/login')
            } else {
              this.$message.error(res.data.message || '注册失败')
            }
          }).finally(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);

  .register-header {
    text-align: center;
    margin-bottom: 30px;

    h2 {
      margin: 0 0 10px;
      color: #333;
      font-size: 28px;
    }

    p {
      margin: 0;
      color: #999;
      font-size: 14px;
    }
  }

  .register-footer {
    text-align: center;
    font-size: 14px;

    a {
      color: #667eea;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}
</style>