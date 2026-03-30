<template>
  <div class="user-courses">
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
            <router-link to="/user" class="active">个人主页</router-link>
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

    <!-- 主体内容 -->
    <div class="main-content">
      <div class="courses-container">
        <div class="page-header">
          <h2>我的课程</h2>
        </div>

        <!-- 课程列表 -->
        <div class="courses-list" v-if="courses.length > 0">
          <div class="course-card" v-for="course in courses" :key="course.id" @click="goLearn(course)">
            <div class="course-cover">
              <img v-if="course.pic" :src="course.pic" :alt="course.name">
              <div v-else class="cover-placeholder">{{ (course.name || '课').charAt(0) }}</div>
              <div class="course-progress" v-if="course.progress !== undefined">
                <el-progress :percentage="course.progress" :stroke-width="6" :show-text="false"></el-progress>
              </div>
            </div>
            <div class="course-info">
              <h3>{{ course.name }}</h3>
              <p class="course-desc">{{ course.description }}</p>
              <div class="course-meta">
                <span class="teacher" v-if="course.teacherNames">讲师：{{ course.teacherNames }}</span>
                <span class="progress" v-if="course.progress !== undefined">学习进度：{{ course.progress }}%</span>
              </div>
              <el-button type="primary" size="small" @click.stop="goLearn(course)">继续学习</el-button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div class="empty" v-else>
          <i class="el-icon-reading"></i>
          <p>您还没有购买任何课程</p>
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
  name: 'UserCourses',
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      courses: []
    }
  },
  mounted() {
    this.checkLogin()
    if (this.isLoggedIn) {
      this.loadMyCourses()
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
    loadMyCourses() {
      this.$http.get('/user/user/courses')
        .then(res => {
          if (res.data.success) {
            const courses = res.data.data || []
            const colors = [
              'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
              'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
              'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
              'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
              'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)'
            ]
            this.courses = courses.map((c, i) => ({
              ...c,
              name: c.name || c.title || '课程',
              description: c.description || '',
              pic: c.pic || c.image || '',
              bg: colors[i % colors.length]
            }))
          }
        })
        .catch(() => {
          // 使用模拟数据
          this.courses = [
            { id: 1, name: 'Vue.js 实战课程', description: '从入门到精通', teacherNames: '张老师', progress: 35, pic: '', bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
            { id: 2, name: 'React 进阶课程', description: '深入理解 React 原理', teacherNames: '李老师', progress: 60, pic: '', bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' }
          ]
        })
    },
    goLearn(course) {
      this.$router.push(`/user/course/learn/${course.id}`)
    },
    logout() {
      localStorage.clear()
      this.$router.push('/')
    }
  }
}
</script>

<style lang="scss" scoped>
.user-courses {
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

      &.active, &:hover {
        color: #667eea;
      }
    }
  }

  .user-info {
    a {
      margin-left: 20px;
      color: #333;
      text-decoration: none;

      &:hover, &.active {
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
  margin: 0 auto;
  padding: 20px;
}

.courses-container {
  .page-header {
    margin-bottom: 20px;

    h2 {
      margin: 0;
      font-size: 24px;
    }
  }
}

.courses-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
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

  .course-cover {
    position: relative;
    height: 160px;

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
      font-size: 48px;
      font-weight: bold;
      color: #fff;
    }

    .course-progress {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background: rgba(0, 0, 0, 0.5);
      padding: 5px 10px;
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
      margin-bottom: 10px;
      font-size: 12px;
      color: #999;

      span {
        display: block;
        margin-bottom: 5px;
      }
    }
  }
}

.empty {
  text-align: center;
  padding: 100px 0;
  background: #fff;
  border-radius: 8px;

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