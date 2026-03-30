<template>
  <div class="course-detail">
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
            <router-link to="/user/courses">我的课程</router-link>
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

    <!-- 课程信息 -->
    <div class="course-info-section">
      <div class="course-info-content">
        <div class="course-left">
          <div class="course-cover">
            <img v-if="course.pic" :src="course.pic" :alt="course.name">
            <div v-else class="cover-placeholder">{{ (course.name || '课').charAt(0) }}</div>
          </div>
        </div>
        <div class="course-right">
          <h1 class="course-title">{{ course.name }}</h1>
          <div class="course-desc">{{ course.description }}</div>
          <div class="course-meta">
            <span class="teacher" v-if="course.teacherNames">讲师：{{ course.teacherNames }}</span>
            <span class="students" v-if="course.totalMinute">时长：{{ Math.floor(course.totalMinute / 60) }}小时</span>
          </div>
          <div class="course-price">
            <span class="price" v-if="course.price > 0">¥{{ course.price }}</span>
            <span class="price free" v-else>免费</span>
          </div>
          <div class="course-actions">
            <el-button type="primary" size="large" @click="buyNow">立即购买</el-button>
            <el-button size="large" @click="addToCart">加入购物车</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 课程章节 -->
    <div class="course-chapters">
      <div class="chapter-title">课程章节</div>
      <el-collapse v-model="activeChapter">
        <el-collapse-item v-for="chapter in chapters" :key="chapter.id" :name="chapter.id">
          <template slot="title">
            <div class="chapter-header">
              <span class="chapter-name">{{ chapter.name }}</span>
              <span class="chapter-duration" v-if="chapter.totalTime">{{ chapter.totalTime }}分钟</span>
            </div>
          </template>
          <div class="chapter-content">
            <div
              class="video-item"
              v-for="video in chapter.videos"
              :key="video.id"
              @click="playVideo(video)"
            >
              <i class="el-icon-video-play"></i>
              <span class="video-name">{{ video.name }}</span>
              <span class="video-duration" v-if="video.duration">{{ video.duration }}分钟</span>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>

    <!-- 底部 -->
    <div class="footer">
      <p>&copy; 2024 云课教育 版权所有</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CourseDetail',
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      courseId: null,
      course: {},
      chapters: [],
      activeChapter: []
    }
  },
  mounted() {
    this.courseId = this.$route.params.id
    this.checkLogin()
    this.loadCourseDetail()
    this.loadChapters()
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
    loadCourseDetail() {
      this.$http.get(`/course/course/detail/data/${this.courseId}`)
        .then(res => {
          if (res.data.success) {
            this.course = res.data.data || {}
          }
        })
        .catch(() => {
          // 使用基本信息
          this.$http.get(`/course/course/${this.courseId}`)
            .then(res => {
              if (res.data.success) {
                this.course = res.data.data || {}
              }
            })
        })
    },
    loadChapters() {
      this.$http.get(`/course/courseChapter/listByCourseId/${this.courseId}`)
        .then(res => {
          if (res.data.success) {
            this.chapters = res.data.data || []
            if (this.chapters.length > 0) {
              this.activeChapter = [this.chapters[0].id]
            }
          }
        })
    },
    buyNow() {
      if (!this.isLoggedIn) {
        this.$router.push('/login')
        return
      }
      this.addToCart()
      this.$router.push('/cart')
    },
    addToCart() {
      if (!this.isLoggedIn) {
        this.$router.push('/login')
        return
      }
      this.$http.post('/order/cart/add', {
        courseId: this.courseId,
        courseName: this.course.name,
        coursePic: this.course.pic,
        coursePrice: this.course.price || 0,
        oldCoursePrice: this.course.price || 0,
        loginId: this.userInfo.id
      }).then(res => {
        if (res.data.success) {
          this.$message.success('已加入购物车')
        } else {
          this.$message.error(res.data.message || '加入失败')
        }
      })
    },
    playVideo(video) {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        return
      }
      // 跳转学习页面
      this.$router.push(`/user/course/learn/${this.courseId}?videoId=${video.id}`)
    },
    logout() {
      localStorage.clear()
      this.$router.push('/')
    }
  }
}
</script>

<style lang="scss" scoped>
.course-detail {
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

      &:hover {
        color: #667eea;
      }
    }

    .username {
      margin-left: 20px;
    }
  }
}

.course-info-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.course-info-content {
  width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 40px;
}

.course-left {
  .course-cover {
    width: 400px;
    height: 250px;
    border-radius: 8px;
    overflow: hidden;
    background: rgba(0, 0, 0, 0.2);

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
      font-size: 80px;
      color: #fff;
      opacity: 0.5;
    }
  }
}

.course-right {
  flex: 1;
  color: #fff;

  .course-title {
    font-size: 28px;
    margin: 0 0 20px;
  }

  .course-desc {
    font-size: 14px;
    margin-bottom: 20px;
    opacity: 0.9;
  }

  .course-meta {
    margin-bottom: 20px;

    span {
      margin-right: 30px;
    }
  }

  .course-price {
    margin-bottom: 30px;

    .price {
      font-size: 32px;
      font-weight: bold;

      &.free {
        color: #52c41a;
      }
    }
  }

  .course-actions {
    display: flex;
    gap: 20px;
  }
}

.course-chapters {
  width: 1200px;
  margin: 40px auto;
  padding: 0 20px;

  .chapter-title {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 20px;
  }

  .chapter-header {
    width: 100%;
    display: flex;
    justify-content: space-between;
  }

  .chapter-content {
    padding: 10px 0;
  }

  .video-item {
    display: flex;
    align-items: center;
    padding: 12px 20px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background: #f5f5f5;
    }

    i {
      margin-right: 10px;
      font-size: 18px;
    }

    .video-name {
      flex: 1;
    }

    .video-duration {
      color: #999;
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