<template>
  <div class="user-center">
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
          <span class="username">{{ userInfo.username }}</span>
          <a href="javascript:;" @click="logout">退出</a>
        </div>
      </div>
    </div>

    <div class="main-content">
      <!-- 左侧菜单 -->
      <div class="sidebar">
        <div class="user-card">
          <div class="avatar">
            <img :src="userInfo.headImg || 'https://via.placeholder.com/80'">
          </div>
          <div class="info">
            <h3>{{ userInfo.username }}</h3>
            <p>等级：{{ userInfo.levelName || 'VIP会员' }}</p>
          </div>
        </div>

        <el-menu :default-active="activeMenu" router>
          <el-menu-item index="/user">
            <i class="el-icon-s-home"></i>
            <span>用户首页</span>
          </el-menu-item>
          <el-menu-item index="/user/home">
            <i class="el-icon-user"></i>
            <span>欢迎页</span>
          </el-menu-item>
          <el-menu-item index="/user/profile">
            <i class="el-icon-s-custom"></i>
            <span>个人资料</span>
          </el-menu-item>
          <el-menu-item index="/user/security">
            <i class="el-icon-safety"></i>
            <span>账户安全</span>
          </el-menu-item>
          <el-menu-item index="/user/orders">
            <i class="el-icon-s-order"></i>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="/user/account">
            <i class="el-icon-wallet"></i>
            <span>资金账户</span>
          </el-menu-item>
          <el-menu-item index="/user/points">
            <i class="el-icon-coin"></i>
            <span>我的积分</span>
          </el-menu-item>
          <el-menu-item index="/user/msg">
            <i class="el-icon-message"></i>
            <span>我的消息</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 右侧内容 -->
      <div class="content">
        <!-- 用户信息卡片 -->
        <div class="user-header">
          <div class="user-info-box">
            <div class="avatar">
              <img :src="userInfo.headImg || 'https://via.placeholder.com/100'">
            </div>
            <div class="info">
              <h2>{{ userInfo.username }}</h2>
              <p>等级：{{ userInfo.levelName || 'VIP会员' }} | 成长值：{{ userInfo.growthValue || 0 }}</p>
              <p>邮箱：{{ userInfo.email || '未绑定' }} | 手机：{{ userInfo.phone || '未绑定' }}</p>
            </div>
          </div>
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-value">{{ stats.orderCount }}</div>
              <div class="stat-label">订单数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.courseCount }}</div>
              <div class="stat-label">课程数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.points }}</div>
              <div class="stat-label">积分</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">¥{{ stats.balance }}</div>
              <div class="stat-label">余额</div>
            </div>
          </div>
        </div>

        <!-- 快捷导航 -->
        <div class="quick-nav">
          <router-link to="/user/orders" class="nav-item">
            <i class="el-icon-s-order"></i>
            <span>我的订单</span>
          </router-link>
          <router-link to="/user/profile" class="nav-item">
            <i class="el-icon-s-custom"></i>
            <span>个人资料</span>
          </router-link>
          <router-link to="/user/account" class="nav-item">
            <i class="el-icon-wallet"></i>
            <span>我的账户</span>
          </router-link>
          <router-link to="/user/msg" class="nav-item">
            <i class="el-icon-message"></i>
            <span>消息中心</span>
          </router-link>
        </div>

        <!-- 最近订单 -->
        <div class="section">
          <div class="section-header">
            <h3>最近订单</h3>
            <router-link to="/user/orders">查看全部</router-link>
          </div>
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
            <el-table-column prop="courseName" label="课程"></el-table-column>
            <el-table-column prop="amount" label="金额" width="100">
              <template slot-scope="scope">
                ¥{{ scope.row.amount }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'">
                  {{ scope.row.status === 1 ? '已完成' : '待支付' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间"></el-table-column>
          </el-table>
        </div>

        <!-- 最近学习 -->
        <div class="section">
          <div class="section-header">
            <h3>最近学习</h3>
            <router-link to="/course/list">去选课</router-link>
          </div>
          <div class="course-list">
            <div class="course-item" v-for="course in recentCourses" :key="course.id">
              <img :src="course.image || 'https://via.placeholder.com/200x120'">
              <div class="course-info">
                <h4>{{ course.title }}</h4>
                <el-progress :percentage="course.progress || 0" :stroke-width="6"></el-progress>
              </div>
            </div>
            <div class="empty" v-if="!recentCourses.length">
              <p>暂无学习记录</p>
              <router-link to="/course/list">去选课</router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserIndex',
  data() {
    return {
      activeMenu: '/user',
      userInfo: {},
      stats: {
        orderCount: 0,
        courseCount: 0,
        points: 0,
        balance: 0
      },
      recentOrders: [],
      recentCourses: []
    }
  },
  mounted() {
    this.loadUserInfo()
    this.loadStats()
    this.loadRecentOrders()
    this.loadRecentCourses()
  },
  methods: {
    loadUserInfo() {
      this.$http.get('/user/user/current')
        .then(res => {
          if (res.data.success) {
            this.userInfo = res.data.data || {}
          }
        })
        .catch(() => {
          this.userInfo = { username: '用户', levelName: 'VIP会员' }
        })
    },
    loadStats() {
      this.$http.get('/user/account/balance')
        .then(res => {
          if (res.data.success) {
            this.stats.balance = res.data.data.balance || 0
          }
        })
      this.$http.get('/user/account/points')
        .then(res => {
          if (res.data.success) {
            this.stats.points = res.data.data.points || 0
          }
        })
    },
    loadRecentOrders() {
      this.$http.post('/order/courseOrder/pagelist', { pageNum: 1, pageSize: 5 })
        .then(res => {
          if (res.data.success) {
            this.recentOrders = res.data.data.rows || []
          }
        })
        .catch(() => {
          this.recentOrders = []
        })
    },
    loadRecentCourses() {
      this.recentCourses = []
    },
    logout() {
      localStorage.clear()
      this.$router.push('/login')
    }
  }
}
</script>

<style lang="scss" scoped>
.user-center {
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
    .username {
      margin-right: 15px;
    }

    a {
      color: #333;
      text-decoration: none;
      margin-left: 15px;

      &:hover {
        color: #667eea;
      }
    }
  }
}

.main-content {
  width: 1200px;
  margin: 20px auto;
  display: flex;
  padding: 0 20px;
}

.sidebar {
  width: 220px;
  margin-right: 20px;

  .user-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    padding: 20px;
    border-radius: 8px;
    text-align: center;
    margin-bottom: 20px;

    .avatar {
      width: 80px;
      height: 80px;
      margin: 0 auto 15px;
      border-radius: 50%;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    h3 {
      margin: 0 0 5px;
    }

    p {
      margin: 0;
      font-size: 12px;
      opacity: 0.8;
    }
  }

  .el-menu {
    border: none;
  }
}

.content {
  flex: 1;
}

.user-header {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;

  .user-info-box {
    display: flex;
    align-items: center;

    .avatar {
      width: 100px;
      height: 100px;
      border-radius: 50%;
      overflow: hidden;
      margin-right: 20px;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .info {
      h2 {
        margin: 0 0 10px;
      }

      p {
        margin: 5px 0;
        color: #999;
        font-size: 14px;
      }
    }
  }

  .user-stats {
    display: flex;
    gap: 40px;

    .stat-item {
      text-align: center;

      .stat-value {
        font-size: 24px;
        font-weight: bold;
        color: #667eea;
      }

      .stat-label {
        color: #999;
        font-size: 14px;
        margin-top: 5px;
      }
    }
  }
}

.quick-nav {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;

  .nav-item {
    flex: 1;
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    text-align: center;
    text-decoration: none;
    color: #333;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    }

    i {
      font-size: 32px;
      color: #667eea;
      display: block;
      margin-bottom: 10px;
    }

    span {
      font-size: 14px;
    }
  }
}

.section {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;

    h3 {
      margin: 0;
    }

    a {
      color: #667eea;
      text-decoration: none;
    }
  }
}

.course-list {
  display: flex;
  gap: 20px;

  .course-item {
    width: 200px;

    img {
      width: 100%;
      height: 120px;
      object-fit: cover;
      border-radius: 8px;
    }

    .course-info {
      padding: 10px 0;

      h4 {
        margin: 0 0 10px;
        font-size: 14px;
      }
    }
  }

  .empty {
    text-align: center;
    padding: 40px;
    color: #999;

    a {
      color: #667eea;
      text-decoration: none;
    }
  }
}
</style>