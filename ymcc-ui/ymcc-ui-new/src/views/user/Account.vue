<template>
  <div class="user-account">
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
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
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
          <el-menu-item index="/user/courses">
            <i class="el-icon-reading"></i>
            <span>我的课程</span>
          </el-menu-item>
          <el-menu-item index="/user/home">
            <i class="el-icon-user"></i>
            <span>欢迎页</span>
          </el-menu-item>
          <el-menu-item index="/user/profile">
            <i class="el-icon-s-custom"></i>
            <span>个人资料</span>
          </el-menu-item>
          <el-menu-item index="/user/orders">
            <i class="el-icon-s-order"></i>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="/user/account">
            <i class="el-icon-wallet"></i>
            <span>我的账户</span>
          </el-menu-item>
          <el-menu-item index="/user/msg">
            <i class="el-icon-message"></i>
            <span>消息中心</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 右侧内容 -->
      <div class="content">
        <div class="account-container">
          <h2>我的账户</h2>

          <!-- 账户概览 -->
          <div class="account-overview">
            <div class="overview-card">
              <div class="icon wallet">
                <i class="el-icon-wallet"></i>
              </div>
              <div class="info">
                <div class="label">账户余额</div>
                <div class="value">¥{{ accountInfo.usableAmount || 0 }}</div>
              </div>
            </div>
            <div class="overview-card">
              <div class="icon frozen">
                <i class="el-icon-lock"></i>
              </div>
              <div class="info">
                <div class="label">冻结金额</div>
                <div class="value">¥{{ accountInfo.frozenAmount || 0 }}</div>
              </div>
            </div>
            <div class="overview-card">
              <div class="icon points">
                <i class="el-icon-star-on"></i>
              </div>
              <div class="info">
                <div class="label">积分</div>
                <div class="value">{{ userInfo.points || 0 }}</div>
              </div>
            </div>
          </div>

          <!-- 账户明细 -->
          <div class="section">
            <h3>账户明细</h3>
            <el-table :data="accountLogs" v-loading="loading" style="width: 100%">
              <el-table-column prop="type" label="类型" width="100">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.type === 1 ? 'success' : 'warning'">
                    {{ scope.row.type === 1 ? '收入' : '支出' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="amount" label="金额" width="120">
                <template slot-scope="scope">
                  <span :class="scope.row.type === 1 ? 'income' : 'expense'">
                    {{ scope.row.type === 1 ? '+' : '-' }}¥{{ scope.row.amount }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="描述"></el-table-column>
              <el-table-column prop="createTime" label="时间" width="180">
                <template slot-scope="scope">
                  {{ formatTime(scope.row.createTime) }}
                </template>
              </el-table-column>
            </el-table>

            <!-- 空状态 -->
            <div class="empty" v-if="!loading && accountLogs.length === 0">
              <p>暂无账户明细</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部 -->
    <div class="footer">
      <p>&copy; 2024 云课教育 版权所有</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserAccount',
  data() {
    return {
      activeMenu: '/user/account',
      isLoggedIn: false,
      userInfo: {},
      accountInfo: {
        usableAmount: 0,
        frozenAmount: 0
      },
      accountLogs: [],
      loading: false
    }
  },
  mounted() {
    this.checkLogin()
    if (this.isLoggedIn) {
      this.loadUserInfo()
      this.loadAccountInfo()
    }
  },
  methods: {
    checkLogin() {
      this.isLoggedIn = !!localStorage.getItem('U-TOKEN')
      if (this.isLoggedIn) {
        const user = localStorage.getItem('user')
        if (user) {
          this.userInfo = JSON.parse(user)
        }
      } else {
        this.$router.push('/login')
      }
    },
    loadUserInfo() {
      this.$http.get('/user/user/current')
        .then(res => {
          if (res.data.success) {
            this.userInfo = res.data.data || {}
          }
        })
        .catch(() => {})
    },
    loadAccountInfo() {
      this.$http.get('/user/account/balance')
        .then(res => {
          if (res.data.success) {
            this.accountInfo = res.data.data || {}
          }
        })
        .catch(() => {})
    },
    formatTime(time) {
      if (!time) return '-'
      return new Date(time).toLocaleString()
    },
    logout() {
      localStorage.clear()
      this.$router.push('/login')
    }
  }
}
</script>

<style lang="scss" scoped>
.user-account {
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

.account-container {
  background: #fff;
  border-radius: 8px;
  padding: 30px;

  h2 {
    margin: 0 0 20px;
    font-size: 24px;
  }
}

.account-overview {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;

  .overview-card {
    background: #f9f9f9;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;

    .icon {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 15px;

      i {
        font-size: 28px;
        color: #fff;
      }

      &.wallet {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }

      &.frozen {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }

      &.points {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }
    }

    .info {
      .label {
        color: #999;
        font-size: 14px;
        margin-bottom: 5px;
      }

      .value {
        font-size: 24px;
        font-weight: bold;
        color: #333;
      }
    }
  }
}

.section {
  h3 {
    margin: 0 0 15px;
    font-size: 18px;
  }
}

.income {
  color: #67c23a;
  font-weight: bold;
}

.expense {
  color: #f56c6c;
  font-weight: bold;
}

.empty {
  text-align: center;
  padding: 40px;
  color: #999;
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