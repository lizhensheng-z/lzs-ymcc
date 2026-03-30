<template>
  <div class="login-page">
    <!-- 动态背景 -->
    <div class="animated-bg">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
    </div>

    <!-- 登录容器 -->
    <div class="login-wrapper">
      <!-- 左侧品牌展示 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="logo">
            <i class="el-icon-s-platform"></i>
          </div>
          <h1 class="brand-title">IT学习云平台</h1>
          <p class="brand-subtitle">智能化教育管理系统</p>
          <div class="brand-features">
            <div class="feature-item">
              <i class="el-icon-video-camera"></i>
              <span>在线课程管理</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-s-order"></i>
              <span>订单数据分析</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-s-custom"></i>
              <span>学员服务系统</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="form-section">
        <div class="form-card">
          <div class="form-header">
            <h3 class="title">欢迎登录</h3>
            <p class="subtitle">请使用管理员账号登录系统</p>
          </div>

          <el-form
            :model="ruleForm2"
            :rules="rules2"
            ref="ruleForm2"
            class="login-form"
            @keyup.enter.native="handleSubmit2"
          >
            <el-form-item prop="account">
              <el-input
                type="text"
                v-model="ruleForm2.account"
                auto-complete="off"
                placeholder="请输入管理员账号"
                prefix-icon="el-icon-user"
                size="large"
                class="custom-input"
              ></el-input>
            </el-form-item>

            <el-form-item prop="checkPass">
              <el-input
                type="password"
                v-model="ruleForm2.checkPass"
                auto-complete="off"
                placeholder="请输入登录密码"
                prefix-icon="el-icon-lock"
                size="large"
                show-password
                class="custom-input"
              ></el-input>
            </el-form-item>

            <div class="form-options">
              <el-checkbox v-model="checked" class="remember">记住我</el-checkbox>
              <a href="javascript:void(0)" class="forgot-link" @click="forgotPassword">忘记密码？</a>
            </div>

            <el-form-item>
              <el-button
                type="primary"
                class="login-btn"
                @click.native.prevent="handleSubmit2"
                :loading="logining"
                size="large"
              >
                <span v-if="!logining">登 录</span>
                <span v-else>登录中...</span>
              </el-button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
            <p>© 2026 IT学习云平台 版权所有</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      logining: false,
      ruleForm2: {
        account: '',
        checkPass: ''
      },
      rules2: {
        account: [
          { required: true, message: '请输入管理员账号', trigger: 'blur' },
          { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        checkPass: [
          { required: true, message: '请输入登录密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
        ]
      },
      checked: true
    };
  },
  methods: {
    forgotPassword() {
      this.$message.info('请联系系统管理员重置密码');
    },
    handleSubmit2() {
      this.$refs.ruleForm2.validate((valid) => {
        if (valid) {
          this.logining = true;
          const loginParams = {
            username: this.ruleForm2.account,
            password: this.ruleForm2.checkPass,
            type: 0
          };

          this.$http.post("/uaa/login/common", loginParams)
            .then(res => {
              if (res.data.success) {
                const { access_token, refresh_token, expiresTime } = res.data.data;
                localStorage.setItem("U-TOKEN", access_token);
                localStorage.setItem("R-TOKEN", refresh_token);
                localStorage.setItem("expiresTime", expiresTime);
                localStorage.setItem("user", JSON.stringify({ username: this.ruleForm2.account }));

                this.$message.success('登录成功，欢迎回来！');
                setTimeout(() => {
                  this.$router.push({ path: '/echarts' });
                }, 500);
              } else {
                this.$message.error(res.data.message || '登录失败');
              }
              this.logining = false;
            })
            .catch(error => {
              this.$message.error("登录失败[" + (error.message || '网络错误') + "]");
              this.logining = false;
            });
        } else {
          return false;
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
// 动画关键帧
@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

@keyframes pulse {
  0%, 100% { opacity: 0.4; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.1); }
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

// 页面容器
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  position: relative;
  overflow: hidden;
}

// 动态背景
.animated-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;

  &.orb-1 {
    width: 600px;
    height: 600px;
    background: linear-gradient(135deg, #ff6b6b, #feca57);
    top: -200px;
    right: -100px;
    animation: pulse 8s ease-in-out infinite, float 10s ease-in-out infinite;
  }

  &.orb-2 {
    width: 500px;
    height: 500px;
    background: linear-gradient(135deg, #48dbfb, #0abde3);
    bottom: -150px;
    left: -100px;
    animation: pulse 10s ease-in-out infinite, float 12s ease-in-out infinite reverse;
  }

  &.orb-3 {
    width: 400px;
    height: 400px;
    background: linear-gradient(135deg, #ff9ff3, #f368e0);
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    animation: pulse 12s ease-in-out infinite;
  }
}

// 登录容器
.login-wrapper {
  display: flex;
  width: 1000px;
  height: 600px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(10px);
}

// 左侧品牌区域
.brand-section {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  padding: 60px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 1px, transparent 1px);
    background-size: 20px 20px;
    opacity: 0.3;
  }
}

.brand-content {
  text-align: center;
  position: relative;
  z-index: 1;
}

.logo {
  width: 100px;
  height: 100px;
  margin: 0 auto 30px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);

  i {
    font-size: 50px;
    color: white;
  }
}

.brand-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 10px;
  letter-spacing: 2px;
}

.brand-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 50px;
  letter-spacing: 1px;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-size: 14px;
  opacity: 0.9;

  i {
    font-size: 18px;
  }
}

// 右侧表单区域
.form-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  background: white;
}

.form-card {
  width: 100%;
  max-width: 360px;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;

  .title {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 10px;
  }

  .subtitle {
    font-size: 14px;
    color: #909399;
  }
}

.login-form {
  .custom-input {
    ::v-deep .el-input__inner {
      border-radius: 10px;
      height: 48px;
      line-height: 48px;
      padding-left: 45px;
      font-size: 14px;
      border: 1px solid #dcdfe6;
      transition: all 0.3s;

      &:focus {
        border-color: #667eea;
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
      }
    }

    ::v-deep .el-input__icon {
      font-size: 18px;
      color: #909399;
      line-height: 48px;
    }
  }
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: -10px 0 25px;

  .remember {
    ::v-deep .el-checkbox__label {
      font-size: 13px;
      color: #606266;
    }
  }

  .forgot-link {
    font-size: 13px;
    color: #667eea;
    text-decoration: none;
    transition: color 0.3s;

    &:hover {
      color: #764ba2;
      text-decoration: underline;
    }
  }
}

.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(102, 126, 126, 0.5);
  }

  &:active {
    transform: translateY(0);
  }
}

.form-footer {
  margin-top: 40px;
  text-align: center;

  p {
    font-size: 12px;
    color: #c0c4cc;
  }
}

// 响应式设计
@media (max-width: 992px) {
  .login-wrapper {
    width: 90%;
    max-width: 500px;
    height: auto;
    flex-direction: column;
  }

  .brand-section {
    padding: 40px;
    min-height: 250px;
  }

  .brand-title {
    font-size: 28px;
  }

  .brand-features {
    display: none;
  }

  .form-section {
    padding: 40px;
  }
}

@media (max-width: 576px) {
  .login-page {
    padding: 20px;
  }

  .login-wrapper {
    width: 100%;
    border-radius: 16px;
  }

  .brand-section {
    padding: 30px;
    min-height: 200px;
  }

  .logo {
    width: 70px;
    height: 70px;
    margin-bottom: 20px;

    i {
      font-size: 35px;
    }
  }

  .brand-title {
    font-size: 24px;
  }

  .form-section {
    padding: 30px 25px;
  }

  .form-header .title {
    font-size: 24px;
  }
}
</style>
