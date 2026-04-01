<template>
  <div class="order-success">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <div class="logo">
          <router-link to="/">云课教育</router-link>
        </div>
        <div class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/course/list">课程列表</router-link>
          <router-link to="/cart">购物车</router-link>
        </div>
        <div class="user-info">
          <template v-if="isLoggedIn">
            <router-link to="/user">个人主页</router-link>
            <span class="username">{{ userInfo.username }}</span>
            <a href="javascript:;" @click="logout">退出</a>
          </template>
          <template v-else>
            <router-link to="/login">登录</router-link>
          </template>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="success-content">
      <div class="success-container">
        <div class="success-icon">
          <i class="el-icon-circle-check"></i>
        </div>
        <h2>订单支付成功</h2>
        <p class="order-info">订单号：{{ orderId }}</p>
        <p class="tips">您可以开始学习已购买的课程了</p>

        <div class="action-buttons">
          <el-button type="primary" size="large" @click="goMyCourses">
            去学习
          </el-button>
          <el-button size="large" @click="$router.push('/course/list')">
            继续选课
          </el-button>
        </div>
      </div>
    </div>

    <!-- 底部 -->
    <div class="footer">
      <p>&copy; 2026 云课教育 版权所有</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OrderSuccess',
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      orderId: ''
    }
  },
  mounted() {
    this.orderId = this.$route.query.orderNo || this.$route.query.orderId || '未知'
    this.checkLogin()
  },
  methods: {
    checkLogin() {
      this.isLoggedIn = !!localStorage.getItem('U-TOKEN')
      if (this.isLoggedIn) {
        const user = localStorage.getItem('user')
        if (user) {
          this.userInfo = JSON.parse(user)
        }
      }
    },
    goMyCourses() {
      this.$router.push('/user')
    },
    logout() {
      localStorage.clear()
      this.$router.push('/')
    }
  }
}
</script>

<style lang="scss" scoped>
.order-success {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  .header-content {
    width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    height: 60px;
    padding: 0 20px;
  }

  .logo a {
    font-size: 24px;
    color: #667eea;
    text-decoration: none;
    font-weight: bold;
  }

  .nav {
    flex: 1;
    margin-left: 50px;

    a {
      margin: 0 20px;
      color: #333;
      text-decoration: none;

      &:hover {
        color: #667eea;
      }
    }
  }

  .user-info {
    a {
      margin-left: 20px;
      color: #333;
      text-decoration: none;

      &:hover {
        color: #667eea;
      }
    }

    .username {
      margin-left: 20px;
    }
  }
}

.success-content {
  width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
}

.success-container {
  background: #fff;
  border-radius: 8px;
  padding: 60px;
  text-align: center;

  .success-icon {
    i {
      font-size: 80px;
      color: #52c41a;
    }
  }

  h2 {
    margin: 20px 0 10px;
    font-size: 28px;
  }

  .order-info {
    color: #999;
    margin: 0 0 10px;
  }

  .tips {
    color: #666;
    margin: 0 0 30px;
  }

  .action-buttons {
    display: flex;
    justify-content: center;
    gap: 20px;

    .el-button {
      padding: 12px 30px;
      font-size: 16px;
    }
  }
}

.footer {
  background: #333;
  color: #fff;
  text-align: center;
  padding: 30px;
  margin-top: 40px;

  p {
    margin: 0;
  }
}
</style>