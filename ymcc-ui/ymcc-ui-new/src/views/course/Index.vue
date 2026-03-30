<template>
  <div class="course-index">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <div class="logo">
          <h1>云课教育</h1>
        </div>
        <div class="nav">
          <router-link to="/" class="active">首页</router-link>
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
            <router-link to="/register">注册</router-link>
          </template>
        </div>
      </div>
    </div>

    <!-- 轮播图 -->
    <div class="banner">
      <el-carousel height="400px" :interval="5000" arrow="always">
        <el-carousel-item v-for="(banner, index) in banners" :key="index">
          <div class="banner-item" :style="{ background: banner.bg }">
            <div class="banner-content">
              <h2>{{ banner.text }}</h2>
              <p>立即开始学习，提升技能</p>
              <el-button type="primary" size="large" @click="$router.push('/course/list')">开始学习</el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 课程列表 -->
    <div class="course-section">
      <div class="section-header">
        <h2>热门课程</h2>
        <router-link to="/course/list">查看更多</router-link>
      </div>
      <div class="course-list">
        <div class="course-card" v-for="course in hotCourses" :key="course.id" @click="goDetail(course.id)">
          <div class="course-img" :style="course.image ? {} : { background: course.bg }">
            <img v-if="course.image" :src="course.image" :alt="course.title">
            <div v-else class="course-placeholder">{{ (course.title || '课').charAt(0) }}</div>
            <div class="course-tag" v-if="course.tag">{{ course.tag }}</div>
          </div>
          <div class="course-info">
            <h3>{{ course.title }}</h3>
            <p class="course-desc">{{ course.description }}</p>
            <div class="course-meta">
              <span class="price" v-if="course.price > 0">¥{{ course.price }}</span>
              <span class="price free" v-else>免费</span>
              <span class="student-count">{{ course.studentCount }}人学习</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 课程分类 -->
    <div class="course-section">
      <div class="section-header">
        <h2>课程分类</h2>
      </div>
      <div class="category-list">
        <div class="category-item" v-for="category in categories" :key="category.id" @click="goCategory(category.id)">
          <div class="category-icon">{{ category.icon }}</div>
          <div class="category-name">{{ category.name }}</div>
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
  name: 'CourseIndex',
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      banners: [
        { image: '', title: '首页轮播1', bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', text: '云课教育 - 开启学习之旅' },
        { image: '', title: '首页轮播2', bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', text: '精品课程 等你来学' },
        { image: '', title: '首页轮播3', bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', text: '名师授课 品质保证' }
      ],
      hotCourses: [],
      categories: [
        { id: 1, name: '前端开发', icon: '🎨' },
        { id: 2, name: '后端开发', icon: '💻' },
        { id: 3, name: '移动开发', icon: '📱' },
        { id: 4, name: '数据科学', icon: '📊' },
        { id: 5, name: '人工智能', icon: '🤖' },
        { id: 6, name: '云计算', icon: '☁️' }
      ]
    }
  },
  mounted() {
    this.checkLogin()
    this.loadHotCourses()
  },
  methods: {
    checkLogin() {
      const token = localStorage.getItem('U-TOKEN')
      this.isLoggedIn = !!token
      if (this.isLoggedIn) {
        this.loadUserInfo()
      }
    },
    loadUserInfo() {
      this.$http.get('/user/user/current')
        .then(res => {
          if (res.data.success) {
            this.userInfo = res.data.data || {}
          }
        })
    },
    loadHotCourses() {
      this.$http.get('/course/course/listForUser')
        .then(res => {
          if (res.data.success) {
            const courses = (res.data.data || []).slice(0, 8)
            // 添加背景色
            const colors = [
              'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
              'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
              'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
              'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
              'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
              'linear-gradient(135deg, #d299c2 0%, #fef9d7 100%)',
              'linear-gradient(135deg, #89f7fe 0%, #66a6ff 100%)'
            ]
            this.hotCourses = courses.map((c, i) => ({
              ...c,
              title: c.name || c.title || '课程',
              description: c.description || '',
              image: c.pic || c.image || '',
              price: c.price || 0,
              studentCount: c.studentCount || 0,
              bg: colors[i % colors.length]
            }))
          }
        })
        .catch(() => {
          // 使用模拟数据
          this.hotCourses = [
            { id: 1, title: 'Vue.js 实战课程', description: '从入门到精通', price: 99, studentCount: 1234, image: '', bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
            { id: 2, title: 'React 进阶课程', description: '深入理解 React 原理', price: 129, studentCount: 2345, image: '', bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
            { id: 3, title: 'Node.js 全栈开发', description: '打造完整全栈项目', price: 199, studentCount: 3456, image: '', bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
            { id: 4, title: 'Python 入门到实战', description: '零基础学习 Python', price: 0, studentCount: 4567, image: '', bg: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
          ]
        })
    },
    goDetail(id) {
      this.$router.push(`/course/detail/${id}`)
    },
    goCategory(id) {
      this.$router.push(`/course/list?type=${id}`)
    },
    logout() {
      localStorage.clear()
      this.isLoggedIn = false
      this.$router.push('/')
    }
  }
}
</script>

<style lang="scss" scoped>
.course-index {
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

  .logo h1 {
    margin: 0;
    font-size: 24px;
    color: #667eea;
  }

  .nav {
    flex: 1;
    margin-left: 50px;

    a {
      margin: 0 20px;
      color: #333;
      text-decoration: none;
      padding: 20px 0;

      &.active, &:hover {
        color: #667eea;
        border-bottom: 2px solid #667eea;
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

.banner {
  .banner-item {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .banner-content {
    text-align: center;
    color: #fff;

    h2 {
      font-size: 48px;
      margin: 0 0 20px;
      font-weight: bold;
    }

    p {
      font-size: 24px;
      margin: 0 0 30px;
      opacity: 0.9;
    }
  }
}

.course-section {
  width: 1200px;
  margin: 40px auto;
  padding: 0 20px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      margin: 0;
      font-size: 24px;
    }

    a {
      color: #667eea;
      text-decoration: none;
    }
  }
}

.course-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.course-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  }

  .course-img {
    position: relative;
    height: 160px;
    display: flex;
    align-items: center;
    justify-content: center;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .course-placeholder {
      font-size: 48px;
      font-weight: bold;
      color: #fff;
      opacity: 0.8;
    }

    .course-tag {
      position: absolute;
      top: 10px;
      left: 10px;
      background: #667eea;
      color: #fff;
      padding: 4px 10px;
      border-radius: 4px;
      font-size: 12px;
    }
  }

  .course-info {
    padding: 15px;

    h3 {
      margin: 0 0 10px;
      font-size: 16px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .course-desc {
      margin: 0 0 10px;
      color: #999;
      font-size: 12px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .course-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .price {
        color: #ff6b6b;
        font-size: 18px;
        font-weight: bold;

        &.free {
          color: #52c41a;
        }
      }

      .student-count {
        color: #999;
        font-size: 12px;
      }
    }
  }
}

.category-list {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 20px;
}

.category-item {
  background: #fff;
  padding: 30px;
  text-align: center;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  }

  .category-icon {
    font-size: 40px;
    margin-bottom: 10px;
  }

  .category-name {
    font-size: 14px;
    color: #333;
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