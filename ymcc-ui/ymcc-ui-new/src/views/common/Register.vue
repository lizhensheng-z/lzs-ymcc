<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>云课教育</h2>
        <p>用户注册</p>
      </div>
      <el-form :model="registerForm" :rules="rules" ref="registerForm">
        <el-form-item prop="mobile">
          <el-input
            v-model="registerForm.mobile"
            placeholder="请输入手机号"
            prefix-icon="el-icon-phone"
          ></el-input>
        </el-form-item>
        <el-form-item prop="smsCode">
          <el-input
            v-model="registerForm.smsCode"
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
        mobile: '',
        smsCode: '',
        password: '',
        confirmPassword: '',
        regChannel: 1
      },
      rules: {
        mobile: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        smsCode: [
          { required: true, message: '请输入验证码', trigger: 'blur' }
        ],
        password: [
          { required: true, validator: validatePass, trigger: 'blur' },
          { pattern: /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$/, message: '密码必须包含字母和数字，6-20位', trigger: 'blur' }
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
      if (!this.registerForm.mobile) {
        this.$message.warning('请先输入手机号')
        return
      }
      if (!/^1[3-9]\d{9}$/.test(this.registerForm.mobile)) {
        this.$message.warning('手机号格式不正确')
        return
      }

      this.$http.get(`/common/verifyCode/sendSmsCode/${this.registerForm.mobile}`)
        .then(res => {
          if (res.data.success) {
            // 弹窗显示验证码
            this.$alert(`您的验证码是：${res.data.data}`, '验证码', {
              confirmButtonText: '确定',
              callback: action => {
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
              }
            })
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
            mobile: this.registerForm.mobile,
            smsCode: this.registerForm.smsCode,
            password: this.registerForm.password,
            regChannel: this.registerForm.regChannel
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