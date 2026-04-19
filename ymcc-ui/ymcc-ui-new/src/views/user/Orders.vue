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
             <img :src="userInfo.headImg || defaultAvatar" @error="handleAvatarError">
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

          <!-- 搜索筛选区域 -->
          <div class="search-area">
            <el-form :model="queryParams" inline ref="queryForm">
              <!-- 日期范围选择 -->
              <el-form-item label="订单日期">
                <el-date-picker
                  v-model="dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="yyyy-MM-dd"
                  :picker-options="pickerOptions"
                  @change="handleDateChange"
                  style="width: 240px"
                />
              </el-form-item>

              <!-- 订单状态筛选 -->
              <el-form-item label="订单状态">
                <el-select
                  v-model="queryParams.statusOrder"
                  placeholder="全部状态"
                  clearable
                  @change="handleSearch"
                  style="width: 120px"
                >
                  <el-option label="全部状态" :value="null" />
                  <el-option label="待支付" :value="0" />
                  <el-option label="已完成" :value="1" />
                  <el-option label="已取消" :value="2" />
                  <el-option label="支付失败" :value="3" />
                  <el-option label="超时取消" :value="4" />
                </el-select>
              </el-form-item>

              <!-- 订单编号搜索 -->
              <el-form-item label="订单编号">
                <el-input
                  v-model="queryParams.orderNo"
                  placeholder="请输入订单编号"
                  clearable
                  @keyup.enter.native="handleSearch"
                  style="width: 180px"
                />
              </el-form-item>

              <el-form-item>
                <el-button type="primary" @click="handleSearch">查询</el-button>
                <el-button @click="handleReset">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 快捷日期筛选 -->
          <div class="quick-date">
            <el-radio-group v-model="quickDate" @change="handleQuickDate">
              <el-radio-button label="">全部</el-radio-button>
              <el-radio-button label="today">今天</el-radio-button>
              <el-radio-button label="week">近一周</el-radio-button>
              <el-radio-button label="month">近一个月</el-radio-button>
              <el-radio-button label="threeMonth">近三个月</el-radio-button>
            </el-radio-group>
          </div>

          <!-- 订单列表 -->
          <div class="order-list" v-loading="loading">
            <div v-if="orders.length === 0 && !loading" class="empty">
              <i class="el-icon-s-order"></i>
              <p>暂无订单记录</p>
              <router-link to="/course/list">去选课</router-link>
            </div>

            <div
              v-for="order in orders"
              :key="order.id"
              class="order-card"
              @click="goToDetail(order.orderNo)"
            >
              <div class="order-header">
                <span class="order-no">订单编号：{{ order.orderNo }}</span>
                <span class="order-time">{{ formatTime(order.createTime) }}</span>
              </div>

              <div class="order-body">
                <div class="order-items">
                  <div
                    v-for="item in order.items"
                    :key="item.id"
                    class="order-item"
                  >
                    <img :src="item.coursePic || defaultCourseImg" class="course-img" alt="课程封面" @error="handleImgError" />
                    <div class="item-info">
                      <div class="course-name">{{ item.courseName }}</div>
                      <div class="item-price">¥{{ item.amount }} × {{ item.count }}</div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="order-footer">
                <div class="order-amount">
                  共{{ order.totalCount }}件商品，实付款：
                  <span class="price">¥{{ order.totalAmount }}</span>
                </div>
                <div class="order-actions">
                  <el-tag :type="getStatusType(order.statusOrder)" size="small">
                    {{ order.statusDesc || getStatusText(order.statusOrder) }}
                  </el-tag>
                  <el-button
                    v-if="order.statusOrder === 0"
                    type="primary"
                    size="mini"
                    @click.stop="payOrder(order)">去支付</el-button>
                  <el-button
                    v-if="order.statusOrder === 1"
                    type="text"
                    size="mini"
                    @click.stop="viewCourse(order)">查看课程</el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div class="pagination-area" v-if="total > 0">
            <el-pagination
              background
              layout="total, prev, pager, next, sizes"
              :total="total"
              :page-size.sync="queryParams.pageSize"
              :current-page.sync="queryParams.pageNum"
              :page-sizes="[10, 20, 50]"
              @current-change="handlePageChange"
              @size-change="handleSizeChange"
            />
          </div>
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
import defaultAvatarImg from '@/assets/java.jpeg'
import { getUserOrderList } from '@/api/order'
import { formatDate, subtractDays } from '@/utils/date'

export default {
  name: 'UserOrders',
  data() {
    return {
      activeMenu: '/user/orders',
      isLoggedIn: false,
      userInfo: {},
      defaultAvatar: defaultAvatarImg,
      defaultCourseImg: 'https://via.placeholder.com/80x60?text=Course',
      loading: false,
      orders: [],
      total: 0,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        startDate: null,
        endDate: null,
        statusOrder: null,
        orderNo: ''
      },
      // 日期范围
      dateRange: [],
      // 快捷日期选择
      quickDate: '',
      // 日期选择器配置
      pickerOptions: {
        disabledDate(time) {
          // 不能选择未来日期
          return time.getTime() > Date.now()
        }
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
    // 加载订单列表
    async loadOrders() {
      this.loading = true
      try {
        const res = await getUserOrderList(this.queryParams)
        if (res.data && res.data.success) {
          this.orders = res.data.data.records || []
          this.total = res.data.data.total || 0
        } else {
          this.$message.error(res.data.message || '查询失败')
        }
      } catch (error) {
        console.error('查询订单失败:', error)
        // 如果新接口失败，降级使用旧接口
        this.loadOrdersFallback()
      } finally {
        this.loading = false
      }
    },
    // 旧接口降级方案
    loadOrdersFallback() {
      const params = {
        page: this.queryParams.pageNum,
        rows: this.queryParams.pageSize
      }
      if (this.queryParams.statusOrder !== null && this.queryParams.statusOrder !== '') {
        params.status = this.queryParams.statusOrder
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
    },
    // 日期范围变化
    handleDateChange(val) {
      if (val && val.length === 2) {
        this.queryParams.startDate = val[0]
        this.queryParams.endDate = val[1]
      } else {
        this.queryParams.startDate = null
        this.queryParams.endDate = null
      }
      this.quickDate = '' // 清除快捷选择
      this.handleSearch()
    },
    // 快捷日期选择
    handleQuickDate(val) {
      if (!val) {
        this.dateRange = []
        this.queryParams.startDate = null
        this.queryParams.endDate = null
      } else {
        const today = new Date()
        let startDate
        const endDate = formatDate(today, 'yyyy-MM-dd')

        switch (val) {
          case 'today':
            startDate = endDate
            break
          case 'week':
            startDate = formatDate(subtractDays(today, 7), 'yyyy-MM-dd')
            break
          case 'month':
            startDate = formatDate(subtractDays(today, 30), 'yyyy-MM-dd')
            break
          case 'threeMonth':
            startDate = formatDate(subtractDays(today, 90), 'yyyy-MM-dd')
            break
        }

        this.dateRange = [startDate, endDate]
        this.queryParams.startDate = startDate
        this.queryParams.endDate = endDate
      }
      this.handleSearch()
    },
    // 查询
    handleSearch() {
      this.queryParams.pageNum = 1
      this.loadOrders()
    },
    // 重置
    handleReset() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        startDate: null,
        endDate: null,
        statusOrder: null,
        orderNo: ''
      }
      this.dateRange = []
      this.quickDate = ''
      this.loadOrders()
    },
    // 页码变化
    handlePageChange(page) {
      this.queryParams.pageNum = page
      this.loadOrders()
    },
    // 每页条数变化
    handleSizeChange(size) {
      this.queryParams.pageSize = size
      this.queryParams.pageNum = 1
      this.loadOrders()
    },
    // 获取状态标签类型
    getStatusType(status) {
      const typeMap = {
        0: 'warning',  // 待支付
        1: 'success',  // 已完成
        2: 'info',     // 已取消
        3: 'danger',   // 支付失败
        4: 'info'      // 超时取消
      }
      return typeMap[status] || 'info'
    },
    // 获取状态文本
    getStatusText(status) {
      const textMap = {
        0: '待支付',
        1: '已完成',
        2: '已取消',
        3: '支付失败',
        4: '超时取消'
      }
      return textMap[status] || '未知'
    },
    // 格式化时间
    formatTime(time) {
      if (!time) return '-'
      return formatDate(new Date(time), 'yyyy-MM-dd hh:mm:ss')
    },
    // 跳转订单详情
    goToDetail(orderNo) {
      this.$router.push(`/user/order/detail/${orderNo}`)
    },
    // 去支付
    payOrder(order) {
      // 跳转到支付确认页
      this.$router.push({
        path: '/order/confirm',
        query: { orderId: order.id }
      })
    },
    // 查看课程
    viewCourse(order) {
      // 跳转到课程学习页
      if (order.items && order.items.length > 0) {
        this.$router.push(`/user/course/learn/${order.items[0].courseId}`)
      }
    },
    // 头像加载失败
    handleAvatarError(e) {
      e.target.src = this.defaultAvatar
    },
    // 图片加载失败
    handleImgError(e) {
      e.target.src = this.defaultCourseImg
    },
    // 退出登录
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

.search-area {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 15px;

  .el-form-item {
    margin-bottom: 0;
  }
}

.quick-date {
  margin-bottom: 20px;

  .el-radio-group {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
}

.order-list {
  min-height: 300px;
}

.order-card {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 15px;
  padding: 15px 20px;
  cursor: pointer;
  transition: box-shadow 0.3s;

  &:hover {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
}

.order-header {
  display: flex;
  justify-content: space-between;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.order-no {
  font-weight: 500;
  color: #333;
}

.order-time {
  color: #999;
  font-size: 13px;
}

.order-body {
  padding: 15px 0;
}

.order-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;

  &:last-child {
    margin-bottom: 0;
  }
}

.course-img {
  width: 80px;
  height: 60px;
  border-radius: 4px;
  object-fit: cover;
  margin-right: 15px;
  background: #f5f5f5;
}

.item-info {
  flex: 1;
}

.course-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
}

.item-price {
  color: #666;
  font-size: 13px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.order-amount {
  color: #666;

  .price {
    color: #ff4d4f;
    font-size: 18px;
    font-weight: 500;
  }
}

.order-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pagination-area {
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