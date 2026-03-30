<template>
  <div class="user-orders">
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
        <div class="orders-container">
          <h2>我的订单</h2>

          <!-- 搜索筛选 -->
          <div class="filter-bar">
            <el-select v-model="query.status" placeholder="订单状态" clearable @change="loadOrders">
              <el-option label="全部" :value="null"></el-option>
              <el-option label="待支付" :value="1"></el-option>
              <el-option label="已完成" :value="4"></el-option>
              <el-option label="已取消" :value="5"></el-option>
            </el-select>
          </div>

          <!-- 订单列表 -->
          <el-table :data="orders" v-loading="loading" style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="200"></el-table-column>
            <el-table-column prop="title" label="购买记录"></el-table-column>
            <el-table-column prop="totalAmount" label="金额" width="100">
              <template slot-scope="scope">
                <span class="price">¥{{ scope.row.totalAmount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="statusOrder" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.statusOrder)">
                  {{ getStatusText(scope.row.statusOrder) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="下单时间" width="180">
              <template slot-scope="scope">
                {{ formatTime(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template slot-scope="scope">
                <el-button
                  v-if="scope.row.statusOrder === 1"
                  type="primary"
                  size="mini"
                  @click="payOrder(scope.row)">去支付</el-button>
                <el-button
                  v-if="scope.row.statusOrder === 4"
                  type="text"
                  size="mini"
                  @click="viewCourse(scope.row)">查看课程</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <el-pagination
            v-if="total > 0"
            class="pagination"
            @current-change="handlePageChange"
            :current-page="query.page"
            :page-size="query.rows"
            layout="total, prev, pager, next"
            :total="total">
          </el-pagination>

          <!-- 空状态 -->
          <div class="empty" v-if="!loading && orders.length === 0">
            <i class="el-icon-s-order"></i>
            <p>暂无订单记录</p>
            <router-link to="/course/list">去选课</router-link>
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
  name: 'UserOrders',
  data() {
    return {
      activeMenu: '/user/orders',
      isLoggedIn: false,
      userInfo: {},
      loading: false,
      orders: [],
      total: 0,
      query: {
        page: 1,
        rows: 10,
        status: null
      }
    }
  },
  mounted() {
    this.checkLogin()
    if (this.isLoggedIn) {
      this.loadUserInfo()
      this.loadOrders()
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
    loadOrders() {
      this.loading = true
      const params = {
        page: this.query.page,
        rows: this.query.rows
      }
      if (this.query.status !== null && this.query.status !== '') {
        params.status = this.query.status
      }

      this.$http.post('/order/courseOrder/pagelist', params)
        .then(res => {
          if (res.data.success) {
            this.orders = res.data.data.rows || []
            this.total = res.data.data.total || 0
          }
        })
        .catch(() => {
          this.orders = []
          this.total = 0
        })
        .finally(() => {
          this.loading = false
        })
    },
    handlePageChange(page) {
      this.query.page = page
      this.loadOrders()
    },
    getStatusType(status) {
      const map = {
        1: 'warning',
        2: 'info',
        3: 'info',
        4: 'success',
        5: 'info'
      }
      return map[status] || 'info'
    },
    getStatusText(status) {
      const map = {
        1: '待支付',
        2: '已取消',
        3: '已取消',
        4: '已完成',
        5: '已取消'
      }
      return map[status] || '未知'
    },
    formatTime(time) {
      if (!time) return '-'
      return new Date(time).toLocaleString()
    },
    payOrder(order) {
      // 跳转到支付确认页
      this.$router.push({
        path: '/order/confirm',
        query: { orderId: order.id }
      })
    },
    viewCourse(order) {
      // 跳转到课程学习页
      this.$router.push(`/user/course/learn/${order.courseId}`)
    },
    logout() {
      localStorage.clear()
      this.$router.push('/login')
    }
  }
}
</script>

<style lang="scss" scoped>
.user-orders {
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

.orders-container {
  background: #fff;
  border-radius: 8px;
  padding: 30px;

  h2 {
    margin: 0 0 20px;
    font-size: 24px;
  }
}

.filter-bar {
  margin-bottom: 20px;
}

.price {
  color: #ff6b6b;
  font-weight: bold;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.empty {
  text-align: center;
  padding: 60px 20px;
  color: #999;

  i {
    font-size: 60px;
    color: #ddd;
    display: block;
    margin-bottom: 20px;
  }

  p {
    margin: 0 0 20px;
    font-size: 16px;
  }

  a {
    color: #667eea;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
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