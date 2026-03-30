<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-icon">
        <i class="el-icon-warning-outline"></i>
      </div>
      <h1 class="error-code">404</h1>
      <h2 class="error-title">页面未找到</h2>
      <p class="error-description">抱歉，您访问的页面不存在或已被移除</p>
      <div class="error-actions">
        <el-button type="primary" @click="goHome">
          <i class="el-icon-s-home"></i> 返回首页
        </el-button>
        <el-button @click="goBack">
          <i class="el-icon-back"></i> 返回上页
        </el-button>
      </div>
    </div>
    <div class="error-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script>
export default {
  methods: {
    goHome() {
      this.$router.push('/echarts');
    },
    goBack() {
      this.$router.go(-1);
    }
  }
}
</script>

<style lang="scss" scoped>
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.3;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(1.1);
  }
}

.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  position: relative;
  overflow: hidden;
}

.error-content {
  text-align: center;
  z-index: 1;
  animation: float 6s ease-in-out infinite;
}

.error-icon {
  width: 120px;
  height: 120px;
  margin: 0 auto 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 20px 40px rgba(102, 126, 234, 0.3);

  i {
    font-size: 60px;
    color: #fff;
  }
}

.error-code {
  font-size: 120px;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
}

.error-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 20px 0 10px;
}

.error-description {
  font-size: 16px;
  color: #909399;
  margin-bottom: 40px;
}

.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;

  .el-button {
    padding: 12px 28px;
    font-size: 14px;
    border-radius: 8px;
    font-weight: 500;

    i {
      margin-right: 8px;
    }
  }
}

.error-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0.1;

  &.circle-1 {
    width: 400px;
    height: 400px;
    top: -100px;
    right: -100px;
    animation: pulse 8s ease-in-out infinite;
  }

  &.circle-2 {
    width: 300px;
    height: 300px;
    bottom: -50px;
    left: -50px;
    animation: pulse 10s ease-in-out infinite;
  }

  &.circle-3 {
    width: 200px;
    height: 200px;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    animation: pulse 12s ease-in-out infinite;
  }
}
</style>