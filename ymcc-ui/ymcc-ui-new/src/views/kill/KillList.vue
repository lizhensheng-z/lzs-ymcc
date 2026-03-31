<template>
  <div class="kill-list-page">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <div class="logo">
          <router-link to="/">云课教育</router-link>
        </div>
        <div class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/course/list">课程列表</router-link>
          <router-link to="/kill" class="active">秒杀专区</router-link>
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

    <!-- 秒杀Banner -->
    <div class="kill-banner">
      <div class="banner-content">
        <h1>限时秒杀</h1>
        <p>超值课程，限时抢购</p>
      </div>
    </div>

    <!-- 秒杀课程列表 -->
    <div class="kill-content">
      <div class="content-wrapper">
        <!-- 筛选条件 -->
        <div class="filter-bar">
          <span class="filter-label">状态筛选：</span>
          <el-radio-group v-model="statusFilter" @change="filterCourses">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="killing">秒杀中</el-radio-button>
            <el-radio-button label="unbegin">即将开始</el-radio-button>
            <el-radio-button label="ended">已结束</el-radio-button>
          </el-radio-group>
        </div>

        <!-- 课程列表 -->
        <div class="course-grid" v-loading="loading">
          <div class="course-card" v-for="course in filteredCourses" :key="course.id" @click="goDetail(course)">
            <div class="card-img">
              <img v-if="course.coursePic" :src="course.coursePic" :alt="course.courseName">
              <div v-else class="img-placeholder">{{ (course.courseName || '课').charAt(0) }}</div>
              <div class="status-tag" :class="getStatusClass(course)">
                {{ course.killStatusName }}
              </div>
            </div>
            <div class="card-body">
              <h3 class="title">{{ course.courseName }}</h3>
              <p class="teacher" v-if="course.teacherNames">
                <i class="el-icon-user"></i> {{ course.teacherNames }}
              </p>
              <div class="price-row">
                <span class="kill-price">¥{{ course.killPrice }}</span>
                <span class="original-price">¥{{ course.price || '--' }}</span>
              </div>
              <div class="info-row">
                <span class="stock">
                  <i class="el-icon-goods"></i> 库存: {{ course.killCount }}
                </span>
                <span class="limit">限购{{ course.killLimit || 1 }}件</span>
              </div>
              <div class="time-row">
                <i class="el-icon-time"></i>
                <span>{{ formatTime(course.startTime) }} - {{ formatTime(course.endTime) }}</span>
              </div>
              <div class="action-row">
                <el-button
                  type="danger"
                  size="small"
                  :disabled="!course.isKilling"
                  @click.stop="handleKill(course)"
                >
                  {{ course.isKilling ? '立即秒杀' : (course.isUnbegin ? '即将开始' : '已结束') }}
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div class="empty-state" v-if="!loading && filteredCourses.length === 0">
          <i class="el-icon-goods"></i>
          <p>暂无秒杀课程</p>
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
  name: 'KillList',
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      loading: false,
      killCourses: [],
      statusFilter: 'all'
    }
  },
  computed: {
    filteredCourses() {
      if (this.statusFilter === 'all') {
        return this.killCourses
      }
      return this.killCourses.filter(course => {
        if (this.statusFilter === 'killing') return course.isKilling
        if (this.statusFilter === 'unbegin') return course.isUnbegin
        if (this.statusFilter === 'ended') return !course.isKilling && !course.isUnbegin
        return true
      })
    }
  },
  mounted() {
    this.checkLogin()
    this.loadKillCourses()
  },
  methods: {
    checkLogin() {
      const token = localStorage.getItem('U-TOKEN')
      this.isLoggedIn = !!token
      if (this.isLoggedIn) {
        const user = localStorage.getItem('user')
        if (user) {
          this.userInfo = JSON.parse(user)
        }
      }
    },
    loadKillCourses() {
      this.loading = true
      this.$http.get('/kill/killCourse/online/all')
        .then(res => {
          if (res.data.success) {
            // 兼容后端字段名
            const courses = res.data.data || []
            courses.forEach(item => {
              item.isKilling = item.isKilling !== undefined ? item.isKilling : item.killing
              item.isUnbegin = item.isUnbegin !== undefined ? item.isUnbegin : item.unbegin
            })
            this.killCourses = courses
          }
        })
        .catch(() => {
          this.$message.error('加载秒杀课程失败')
        })
        .finally(() => {
          this.loading = false
        })
    },
    filterCourses() {
      // 筛选由 computed 自动处理
    },
    getStatusClass(course) {
      if (course.isKilling) return 'killing'
      if (course.isUnbegin) return 'unbegin'
      return 'ended'
    },
    formatTime(time) {
      if (!time) return '--'
      const date = new Date(time)
      if (isNaN(date.getTime())) return '--'
      return `${date.getMonth() + 1}/${date.getDate()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    },
    goDetail(course) {
      this.$router.push(`/kill/detail/${course.id}/${course.activityId}`)
    },
    handleKill(course) {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.goDetail(course)
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
.kill-list-page {
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
    color: #ff4d4f;
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
      padding: 20px 0;

      &.active, &:hover {
        color: #ff4d4f;
        border-bottom: 2px solid #ff4d4f;
      }
    }
  }

  .user-info {
    a {
      margin-left: 20px;
      color: #333;
      text-decoration: none;

      &:hover {
        color: #ff4d4f;
      }
    }

    .username {
      margin-left: 20px;
    }
  }
}

.kill-banner {
  height: 200px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
  display: flex;
  align-items: center;
  justify-content: center;

  .banner-content {
    text-align: center;
    color: #fff;

    h1 {
      font-size: 48px;
      margin: 0 0 10px;
    }

    p {
      font-size: 20px;
      margin: 0;
      opacity: 0.9;
    }
  }
}

.kill-content {
  width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.filter-bar {
  margin-bottom: 20px;
  background: #fff;
  padding: 15px 20px;
  border-radius: 8px;

  .filter-label {
    margin-right: 15px;
    color: #666;
  }
}

.course-grid {
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
    box-shadow: 0 8px 24px rgba(255, 77, 79, 0.15);
  }

  .card-img {
    position: relative;
    height: 150px;
    background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
    display: flex;
    align-items: center;
    justify-content: center;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .img-placeholder {
      font-size: 48px;
      font-weight: bold;
      color: #fff;
      opacity: 0.8;
    }

    .status-tag {
      position: absolute;
      top: 10px;
      right: 10px;
      padding: 4px 10px;
      border-radius: 4px;
      font-size: 12px;
      color: #fff;

      &.killing {
        background: #ff4d4f;
        animation: pulse 1s infinite;
      }

      &.unbegin {
        background: #fa8c16;
      }

      &.ended {
        background: #999;
      }
    }
  }

  .card-body {
    padding: 15px;

    .title {
      margin: 0 0 8px;
      font-size: 16px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .teacher {
      margin: 0 0 10px;
      color: #999;
      font-size: 12px;
    }

    .price-row {
      margin-bottom: 10px;

      .kill-price {
        color: #ff4d4f;
        font-size: 22px;
        font-weight: bold;
      }

      .original-price {
        color: #999;
        font-size: 14px;
        text-decoration: line-through;
        margin-left: 10px;
      }
    }

    .info-row {
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
      color: #666;
      font-size: 12px;
    }

    .time-row {
      color: #999;
      font-size: 12px;
      margin-bottom: 15px;
    }

    .action-row {
      text-align: center;

      .el-button {
        width: 100%;
      }
    }
  }
}

.empty-state {
  text-align: center;
  padding: 100px 0;
  color: #999;

  i {
    font-size: 64px;
    margin-bottom: 20px;
  }

  p {
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

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.6; }
  100% { opacity: 1; }
}
</style>