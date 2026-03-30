<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>云课教育</h2>
        <p>用户登录</p>
      </div>
      <el-form :model="loginForm" :rules="rules" ref="loginForm">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
          ></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            @keyup.enter.native="handleLogin"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            style="width: 100%"
            @click="handleLogin"
          >登录</el-button>
        </el-form-item>
        <div class="login-footer">
          <a href="#/register">立即注册</a>
          <a href="#/forget">忘记密码？</a>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        type: 1
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$http.post('/uaa/login/common', {
            username: this.loginForm.username,
            password: this.loginForm.password,
            type: this.loginForm.type
          }).then(res => {
            if (res.data.success) {
              // 保存 token
              const data = res.data.data
              localStorage.setItem('U-TOKEN', data.access_token)
              localStorage.setItem('R-TOKEN', data.refresh_token)
              // 将秒数转换为过期时间戳
              const expiresTime = new Date().getTime() + (data.expiresTime || 7200) * 1000
              localStorage.setItem('expiresTime', expiresTime)
              // 从 token 中解析用户信息 (JWT payload 是第二部分)
              try {
                const payload = JSON.parse(atob(data.access_token.split('.')[1]))
                const userData = JSON.parse(payload.user_name)
                localStorage.setItem('user', JSON.stringify(userData))
              } catch (e) {
                // 解析失败则只保存用户名
                localStorage.setItem('user', JSON.stringify({ username: this.loginForm.username }))
              }
              // 跳转首页
              this.$router.push('/')
            } else {
              this.$message.error(res.data.message || '登录失败')
            }
          }).catch(() => {
            this.$message.error('登录失败，请检查用户名和密码')
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
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);

  .login-header {
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

  .login-footer {
    display: flex;
    justify-content: space-between;
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