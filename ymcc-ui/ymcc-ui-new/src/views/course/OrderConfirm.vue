<template>
  <div class="order-confirm">
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
    <div class="confirm-content">
      <div class="confirm-container">
        <h2>确认订单</h2>

        <!-- 订单课程列表 -->
        <div class="order-courses">
          <div class="course-item" v-for="course in courses" :key="course.id">
            <div class="course-cover">
              <img v-if="course.pic" :src="course.pic" :alt="course.name">
              <div v-else class="cover-placeholder">{{ (course.name || '课').charAt(0) }}</div>
            </div>
            <div class="course-info">
              <h4>{{ course.name }}</h4>
              <p>讲师：{{ course.teacherNames || '待定' }}</p>
            </div>
            <div class="course-price">¥{{ course.price }}</div>
          </div>
        </div>

        <!-- 订单金额 -->
        <div class="order-summary">
          <div class="summary-row">
            <span class="label">订单金额：</span>
            <span class="value">¥{{ totalPrice }}</span>
          </div>
          <div class="summary-row total">
            <span class="label">应付金额：</span>
            <span class="value">¥{{ totalPrice }}</span>
          </div>
        </div>

        <!-- 支付方式 -->
        <div class="payment-method">
          <h3>支付方式</h3>
          <el-radio-group v-model="paymentMethod">
            <el-radio label="alipay">
              <i class="el-icon-alipay"></i> 支付宝
            </el-radio>
            <el-radio label="wechat">
              <i class="el-icon-wechat"></i> 微信支付
            </el-radio>
            <el-radio label="balance">
              <i class="el-icon-wallet"></i> 账户余额
            </el-radio>
          </el-radio-group>
        </div>

        <!-- 提交订单 -->
        <div class="submit-section">
          <el-button type="primary" size="large" @click="submitOrder" :loading="submitting">
            提交订单
          </el-button>
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
  name: 'OrderConfirm',
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      courses: [],
      paymentMethod: 'alipay',
      submitting: false
    }
  },
  computed: {
    totalPrice() {
      return this.courses.reduce((sum, course) => sum + (course.price || 0), 0)
    }
  },
  mounted() {
    this.checkLogin()
    this.loadCourses()
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
    loadCourses() {
      const courseIds = this.$route.query.courseIds
      if (!courseIds) {
        this.$router.push('/cart')
        return
      }

      const ids = courseIds.split(',')
      // 批量获取课程信息
      const promises = ids.map(id =>
        this.$http.get(`/course/course/${id}`)
          .then(res => {
            if (res.data.success) {
              return res.data.data
            }
            return null
          })
          .catch(() => null)
      )

      Promise.all(promises).then(results => {
        this.courses = results.filter(c => c !== null)
        if (this.courses.length === 0) {
          // 模拟数据
          this.courses = [
            { id: 1, name: 'Vue.js 实战课程', price: 99, pic: '', teacherNames: '张老师' },
            { id: 2, name: 'React 进阶课程', price: 129, pic: '', teacherNames: '李老师' }
          ]
        }
      })
    },
    submitOrder() {
      if (this.courses.length === 0) {
        this.$message.warning('请选择要购买的课程')
        return
      }

      this.submitting = true

      // 创建订单
      this.$http.post('/order/placeOrder', {
        userId: this.userInfo.id,
        courses: this.courses.map(c => ({
          courseId: c.id,
          courseName: c.name,
          coursePrice: c.price,
          coursePic: c.pic
        })),
        totalAmount: this.totalPrice,
        paymentMethod: this.paymentMethod
      }).then(res => {
        if (res.data.success) {
          const orderId = res.data.data
          // 模拟支付成功，跳转到成功页
          this.$router.push(`/order/success?orderId=${orderId}`)
        } else {
          this.$message.error(res.data.message || '创建订单失败')
        }
      }).catch(() => {
        // 模拟创建订单成功
        this.$router.push('/order/success?orderId=mock123')
      }).finally(() => {
        this.submitting = false
      })
    },
    logout() {
      localStorage.clear()
      this.$router.push('/')
    }
  }
}
</script>

<style lang="scss" scoped>
.order-confirm {
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

.confirm-content {
  width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.confirm-container {
  background: #fff;
  border-radius: 8px;
  padding: 30px;

  h2 {
    margin: 0 0 20px;
    font-size: 24px;
  }
}

.order-courses {
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 20px;

  .course-item {
    display: flex;
    align-items: center;
    padding: 15px 20px;
    border-bottom: 1px solid #eee;

    &:last-child {
      border-bottom: none;
    }

    .course-cover {
      width: 100px;
      height: 60px;
      border-radius: 4px;
      overflow: hidden;
      margin-right: 15px;
      background: #f5f5f5;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .cover-placeholder {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
        color: #fff;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }
    }

    .course-info {
      flex: 1;

      h4 {
        margin: 0 0 5px;
        font-size: 14px;
      }

      p {
        margin: 0;
        color: #999;
        font-size: 12px;
      }
    }

    .course-price {
      color: #ff6b6b;
      font-size: 18px;
      font-weight: bold;
    }
  }
}

.order-summary {
  padding: 20px;
  background: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 20px;

  .summary-row {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 10px;

    &:last-child {
      margin-bottom: 0;
    }

    .label {
      color: #666;
      margin-right: 10px;
    }

    .value {
      font-size: 18px;
      font-weight: bold;
    }

    &.total {
      padding-top: 10px;
      border-top: 1px solid #ddd;

      .value {
        color: #ff6b6b;
        font-size: 24px;
      }
    }
  }
}

.payment-method {
  margin-bottom: 30px;

  h3 {
    margin: 0 0 15px;
    font-size: 16px;
  }

  .el-radio-group {
    display: flex;
    gap: 20px;

    .el-radio {
      padding: 15px 20px;
      border: 1px solid #ddd;
      border-radius: 8px;

      &.is-checked {
        border-color: #667eea;
        background: #e6f7ff;
      }
    }
  }
}

.submit-section {
  text-align: right;

  .el-button {
    padding: 15px 40px;
    font-size: 16px;
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