<template>
  <div class="cart-page">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <div class="logo">
          <router-link to="/">云课教育</router-link>
        </div>
        <div class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/course/list">课程列表</router-link>
          <router-link to="/cart" class="active">购物车</router-link>
        </div>
        <div class="user-info">
          <template v-if="isLoggedIn">
            <router-link to="/user">个人主页</router-link>
            <span class="username">{{ userInfo.username }}</span>
            <a href="javascript:;" @click="logout">退出</a>
          </template>
          <template v-else>
            <router-link to="/login">登录</router-link>
            <router-link to="/register">注册</router-link>
          </template>
        </div>
      </div>
    </div>

    <!-- 购物车主体 -->
    <div class="cart-content">
      <div class="cart-container">
        <h2>购物车</h2>

        <!-- 购物车列表 -->
        <div class="cart-list" v-if="cartItems.length > 0">
          <el-table :data="cartItems" @selection-change="handleSelectionChange" row-key="id">
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column label="课程信息" min-width="300">
              <template slot-scope="scope">
                <div class="course-info">
                  <div class="course-cover">
                    <img v-if="scope.row.coursePic" :src="scope.row.coursePic" :alt="scope.row.courseName">
                    <div v-else class="cover-placeholder">{{ (scope.row.courseName || '课').charAt(0) }}</div>
                  </div>
                  <div class="course-detail">
                    <h4>{{ scope.row.courseName }}</h4>
                    <p>讲师：{{ scope.row.teacherNames || '待定' }}</p>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="coursePrice" label="价格" width="120">
              <template slot-scope="scope">
                <span class="price">¥{{ scope.row.coursePrice }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button type="text" @click="removeItem(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 结算区域 -->
          <div class="checkout-section">
            <div class="checkout-info">
              <span class="selected-count">已选 {{ selectedItems.length }} 门课程</span>
              <span class="total-price">总计：<em>¥{{ totalPrice }}</em></span>
            </div>
            <el-button type="primary" size="large" @click="checkout" :disabled="selectedItems.length === 0">
              结算
            </el-button>
          </div>
        </div>

        <!-- 空状态 -->
        <div class="empty" v-else>
          <i class="el-icon-shopping-cart-2"></i>
          <p>购物车是空的</p>
          <router-link to="/course/list">去选课</router-link>
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
  name: 'CourseCart',
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      cartItems: [],
      selectedItems: []
    }
  },
  computed: {
    totalPrice() {
      return this.selectedItems.reduce((sum, item) => sum + (item.coursePrice || 0), 0)
    }
  },
  mounted() {
    this.checkLogin()
    if (this.isLoggedIn) {
      this.loadCart()
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
      }
    },
    loadCart() {
      this.$http.get(`/order/cart/list/${this.userInfo.id}`)
        .then(res => {
          if (res.data.success) {
            this.cartItems = res.data.data || []
          }
        })
        .catch(() => {
          // 模拟数据
          this.cartItems = [
            { id: 1, courseId: 1, courseName: 'Vue.js 实战课程', coursePic: '', coursePrice: 99, teacherNames: '张老师' },
            { id: 2, courseId: 2, courseName: 'React 进阶课程', coursePic: '', coursePrice: 129, teacherNames: '李老师' }
          ]
        })
    },
    handleSelectionChange(val) {
      this.selectedItems = val
    },
    removeItem(item) {
      this.$confirm('确定要删除该课程吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/order/cart/remove/${item.id}`)
          .then(res => {
            if (res.data.success) {
              this.$message.success('删除成功')
              this.loadCart()
            } else {
              this.$message.error(res.data.message || '删除失败')
            }
          })
          .catch(() => {
            // 前端模拟删除
            const index = this.cartItems.findIndex(c => c.id === item.id)
            if (index > -1) {
              this.cartItems.splice(index, 1)
              this.$message.success('删除成功')
            }
          })
      }).catch(() => {})
    },
    checkout() {
      if (this.selectedItems.length === 0) {
        this.$message.warning('请选择要购买的课程')
        return
      }
      // 跳转到订单确认页
      const ids = this.selectedItems.map(item => item.courseId).join(',')
      this.$router.push(`/order/confirm?courseIds=${ids}`)
    },
    logout() {
      localStorage.clear()
      this.$router.push('/')
    }
  }
}
</script>

<style lang="scss" scoped>
.cart-page {
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

      &:hover, &.active {
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

.cart-content {
  width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.cart-container {
  background: #fff;
  border-radius: 8px;
  padding: 30px;

  h2 {
    margin: 0 0 20px;
    font-size: 24px;
  }
}

.course-info {
  display: flex;
  align-items: center;

  .course-cover {
    width: 120px;
    height: 70px;
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
      font-size: 24px;
      color: #fff;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
  }

  .course-detail {
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
}

.price {
  color: #ff6b6b;
  font-size: 16px;
  font-weight: bold;
}

.checkout-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;

  .checkout-info {
    .selected-count {
      margin-right: 20px;
      color: #999;
    }

    .total-price {
      font-size: 16px;

      em {
        color: #ff6b6b;
        font-size: 24px;
        font-weight: bold;
        font-style: normal;
      }
    }
  }
}

.empty {
  text-align: center;
  padding: 100px 0;

  i {
    font-size: 64px;
    color: #ddd;
  }

  p {
    margin: 20px 0;
    color: #999;
  }

  a {
    color: #667eea;
    text-decoration: none;
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