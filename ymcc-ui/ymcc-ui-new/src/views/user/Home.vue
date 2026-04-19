<template>
  <div class="user-home">
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
      <!-- 欢迎横幅 -->
      <div class="welcome-banner">
        <div class="welcome-content">
          <h1>欢迎回来，{{ userInfo.username || '同学' }}！</h1>
          <p>开启学习之旅，成就更好的自己</p>
          <div class="quick-actions">
            <router-link to="/course/list" class="action-btn primary">开始选课</router-link>
            <router-link to="/user/courses" class="action-btn">继续学习</router-link>
          </div>
        </div>
      </div>

      <!-- 功能入口 -->
      <div class="feature-grid">
        <div class="feature-card">
          <i class="el-icon-reading"></i>
          <h3>我的课程</h3>
          <p>查看已购买的课程</p>
          <router-link to="/user/courses">进入</router-link>
        </div>
        <div class="feature-card">
          <i class="el-icon-s-order"></i>
          <h3>我的订单</h3>
          <p>查看购买记录</p>
          <router-link to="/user/orders">进入</router-link>
        </div>
        <div class="feature-card">
          <i class="el-icon-s-custom"></i>
          <h3>个人资料</h3>
          <p>完善个人信息</p>
          <router-link to="/user/profile">进入</router-link>
        </div>
        <div class="feature-card">
          <i class="el-icon-wallet"></i>
          <h3>我的账户</h3>
          <p>查看账户余额</p>
          <router-link to="/user/account">进入</router-link>
        </div>
      </div>

      <!-- 推荐课程 -->
      <div class="recommend-section">
        <h2>推荐课程</h2>
        <div class="course-list">
          <div class="course-item" v-for="course in recommendCourses" :key="course.id">
            <img :src="course.image || 'https://via.placeholder.com/200x120'">
            <div class="course-info">
              <h4>{{ course.title }}</h4>
              <p class="price">¥{{ course.price }}</p>
              <router-link :to="'/course/detail/' + course.id" class="buy-btn">查看详情</router-link>
            </div>
          </div>
          <div class="empty" v-if="!recommendCourses.length">
            <p>暂无推荐课程</p>
            <router-link to="/course/list">去选课</router-link>
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
export default {
  name: 'UserHome',
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      recommendCourses: []
    }
  },
  mounted() {
    this.checkLogin()
    if (this.isLoggedIn) {
      this.loadUserInfo()
    }
    this.loadRecommendCourses()
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
    loadRecommendCourses() {
      // 使用课程分页接口获取推荐课程
      this.$http.post('/course/course/pagelist', {
        page: 1,
        rows: 4,
        keyword: ''
      })
        .then(res => {
          if (res.data.success) {
            const courses = res.data.data.rows || []
            this.recommendCourses = courses.map(c => ({
              id: c.id,
              title: c.name,
              description: c.forUser || '暂无描述',
              image: c.pic || '',
              price: 0, // Course表没有价格字段
              teacherName: c.teacherNames || ''
            }))
          }
        })
        .catch(() => {
          this.recommendCourses = []
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
.user-home {
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
  padding: 0 20px;
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 60px 40px;
  color: #fff;
  margin-bottom: 30px;

  .welcome-content {
    h1 {
      margin: 0 0 15px;
      font-size: 36px;
    }

    p {
      margin: 0 0 30px;
      font-size: 18px;
      opacity: 0.9;
    }

    .quick-actions {
      display: flex;
      gap: 20px;

      .action-btn {
        padding: 12px 30px;
        border-radius: 25px;
        text-decoration: none;
        font-size: 16px;
        transition: all 0.3s;

        &.primary {
          background: #fff;
          color: #667eea;
        }

        &:not(.primary) {
          border: 2px solid #fff;
          color: #fff;
        }

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
        }
      }
    }
  }
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 40px;

  .feature-card {
    background: #fff;
    border-radius: 8px;
    padding: 30px 20px;
    text-align: center;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    }

    i {
      font-size: 40px;
      color: #667eea;
      margin-bottom: 15px;
    }

    h3 {
      margin: 0 0 10px;
      font-size: 18px;
    }

    p {
      margin: 0 0 15px;
      color: #999;
      font-size: 14px;
    }

    a {
      color: #667eea;
      text-decoration: none;
      font-size: 14px;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}

.recommend-section {
  background: #fff;
  border-radius: 8px;
  padding: 30px;

  h2 {
    margin: 0 0 20px;
    font-size: 24px;
  }

  .course-list {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;

    .course-item {
      border-radius: 8px;
      overflow: hidden;
      background: #f9f9f9;

      img {
        width: 100%;
        height: 140px;
        object-fit: cover;
      }

      .course-info {
        padding: 15px;

        h4 {
          margin: 0 0 10px;
          font-size: 16px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .price {
          color: #ff6b6b;
          font-size: 18px;
          font-weight: bold;
          margin: 0 0 10px;
        }

        .buy-btn {
          display: block;
          text-align: center;
          background: #667eea;
          color: #fff;
          padding: 8px;
          border-radius: 4px;
          text-decoration: none;
          font-size: 14px;

          &:hover {
            background: #5a6fd6;
          }
        }
      }
    }

    .empty {
      grid-column: 1 / -1;
      text-align: center;
      padding: 40px;
      color: #999;

      a {
        color: #667eea;
        text-decoration: none;
      }
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