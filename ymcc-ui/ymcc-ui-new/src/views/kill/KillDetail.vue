<template>
  <div class="kill-detail-page">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <div class="logo">
          <router-link to="/">云课教育</router-link>
        </div>
        <div class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/course/list">课程列表</router-link>
          <router-link to="/kill">秒杀专区</router-link>
        </div>
        <div class="user-info">
          <template v-if="isLoggedIn">
            <router-link to="/user">个人主页</router-link>
            <span class="username">{{ userInfo.username }}</span>
            <a href="javascript:;" @click="logout">退出</a>
          </template>
          <template v-else>
            <router-link to="/login">登录</router-link>
          </template>
        </div>
      </div>
    </div>

    <!-- 课程详情 -->
    <div class="detail-content" v-loading="loading">
      <div class="content-wrapper">
        <div class="course-main">
          <!-- 课程信息 -->
          <div class="course-info-card">
            <div class="course-cover">
              <img v-if="course.coursePic" :src="course.coursePic" :alt="course.courseName" @error="handleImgError">
              <img v-else src="../../assets/java.jpeg" :alt="course.courseName">
              <div class="kill-badge">秒杀</div>
            </div>
            <div class="course-meta">
              <h1 class="course-title">{{ course.courseName }}</h1>
              <p class="course-teacher" v-if="course.teacherNames">
                <i class="el-icon-user"></i> {{ course.teacherNames }}
              </p>
              <div class="price-section">
                <span class="kill-price">¥{{ course.killPrice }}</span>
                <span class="original-price">原价 ¥{{ course.price || '--' }}</span>
              </div>
              <div class="info-items">
                <div class="info-item">
                  <span class="label">库存</span>
                  <span class="value">{{ course.killCount }} 件</span>
                </div>
                <div class="info-item">
                  <span class="label">限购</span>
                  <span class="value">每人限购 {{ course.killLimit || 1 }} 件</span>
                </div>
                <div class="info-item">
                  <span class="label">活动时间</span>
                  <span class="value">{{ formatTime(course.startTime) }} - {{ formatTime(course.endTime) }}</span>
                </div>
              </div>
              <div class="status-section">
                <div class="status-tag" :class="getStatusClass()">
                  {{ course.killStatusName }}
                </div>
                <div class="countdown" v-if="course.isKilling">
                  <span>距离结束：</span>
                  <span class="time">{{ countdown }}</span>
                </div>
                <div class="countdown" v-else-if="course.isUnbegin">
                  <span>距离开始：</span>
                  <span class="time">{{ countdown }}</span>
                </div>
              </div>
              <div class="action-section">
                <el-button
                  type="danger"
                  size="large"
                  :loading="killing"
                  :disabled="!course.isKilling"
                  @click="handleKill"
                >
                  {{ course.isKilling ? '立即秒杀' : (course.isUnbegin ? '即将开始' : '活动已结束') }}
                </el-button>
                <p class="tips" v-if="course.isKilling">抢购成功后请在30分钟内完成支付</p>
              </div>
            </div>
          </div>

          <!-- 课程详情 -->
          <div class="course-detail">
            <el-tabs v-model="activeTab">
              <el-tab-pane label="课程介绍" name="intro">
                <div class="intro-content">
                  <p v-if="course.description">{{ course.description }}</p>
                  <p v-else class="empty-text">暂无课程介绍</p>
                </div>
              </el-tab-pane>
              <el-tab-pane label="购买须知" name="notice">
                <div class="notice-content">
                  <h4>秒杀活动规则</h4>
                  <ul>
                    <li>秒杀商品数量有限，先到先得</li>
                    <li>每个用户限购 {{ course.killLimit || 1 }} 件</li>
                    <li>秒杀成功后请在30分钟内完成支付，超时订单将自动取消</li>
                    <li>秒杀商品不支持退款</li>
                    <li>本活动最终解释权归平台所有</li>
                  </ul>
                </div>
              </el-tab-pane>
            </el-tabs>
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
  name: 'KillDetail',
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      loading: false,
      killing: false,
      course: {},
      activeTab: 'intro',
      countdown: '00:00:00',
      timer: null
    }
  },
  mounted() {
    this.checkLogin()
    this.loadCourseDetail()
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
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
    loadCourseDetail() {
      const killId = this.$route.params.id
      const activityId = this.$route.params.activityId

      if (!killId || !activityId) {
        this.$message.error('参数错误')
        this.$router.push('/kill')
        return
      }

      this.loading = true
      this.$http.get(`/kill/killCourse/online/one/${killId}/${activityId}`)
        .then(res => {
          if (res.data.success) {
            const data = res.data.data || {}
            // 兼容后端字段名
            data.isKilling = data.isKilling !== undefined ? data.isKilling : data.killing
            data.isUnbegin = data.isUnbegin !== undefined ? data.isUnbegin : data.unbegin
            this.course = data
            this.startCountdown()
          } else {
            this.$message.error(res.data.message || '加载失败')
          }
        })
        .catch(() => {
          this.$message.error('加载秒杀课程失败')
        })
        .finally(() => {
          this.loading = false
        })
    },
    startCountdown() {
      this.updateCountdown()
      this.timer = setInterval(() => {
        this.updateCountdown()
      }, 1000)
    },
    updateCountdown() {
      const now = new Date().getTime()
      let targetTime

      if (this.course.isKilling) {
        targetTime = new Date(this.course.endTime).getTime()
      } else if (this.course.isUnbegin) {
        targetTime = new Date(this.course.startTime).getTime()
      } else {
        this.countdown = '00:00:00'
        return
      }

      const diff = targetTime - now
      if (diff <= 0) {
        this.countdown = '00:00:00'
        // 刷新课程状态
        this.loadCourseDetail()
        return
      }

      const hours = Math.floor(diff / (1000 * 60 * 60))
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
      const seconds = Math.floor((diff % (1000 * 60)) / 1000)

      this.countdown = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
    },
    getStatusClass() {
      if (this.course.isKilling) return 'killing'
      if (this.course.isUnbegin) return 'unbegin'
      return 'ended'
    },
    formatTime(time) {
      if (!time) return '--'
      const date = new Date(time)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}`
    },
    async handleKill() {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }

      if (!this.course.isKilling) {
        this.$message.warning('秒杀活动未开始或已结束')
        return
      }

      this.killing = true
      try {
        const res = await this.$http.post('/kill/killCourse/kill', {
          killCourseId: this.course.id,
          activityId: this.course.activityId
        })

        if (res.data.success) {
          const orderNo = res.data.data
          this.$message.success('秒杀成功！请尽快完成支付')

          // 跳转到订单确认页
          this.$router.push({
            path: '/order/confirm',
            query: {
              orderNo: orderNo,
              type: 'kill'  // 标识为秒杀订单
            }
          })
        } else {
          this.$message.error(res.data.message || '秒杀失败')
          // 刷新库存
          this.loadCourseDetail()
        }
      } catch (err) {
        this.$message.error('秒杀失败，请重试')
      } finally {
        this.killing = false
      }
    },
    handleImgError(e) {
      e.target.src = require('../../assets/java.jpeg')
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
.kill-detail-page {
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

      &:hover {
        color: #ff4d4f;
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

.detail-content {
  width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
}

.course-info-card {
  display: flex;
  background: #fff;
  border-radius: 8px;
  padding: 30px;
  margin-bottom: 20px;

  .course-cover {
    width: 400px;
    height: 250px;
    border-radius: 8px;
    overflow: hidden;
    position: relative;
    background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .cover-placeholder {
      font-size: 80px;
      font-weight: bold;
      color: #fff;
      opacity: 0.8;
    }

    .kill-badge {
      position: absolute;
      top: 10px;
      left: 10px;
      background: #ff4d4f;
      color: #fff;
      padding: 5px 15px;
      border-radius: 4px;
      font-size: 14px;
    }
  }

  .course-meta {
    flex: 1;
    margin-left: 30px;

    .course-title {
      margin: 0 0 15px;
      font-size: 24px;
    }

    .course-teacher {
      color: #666;
      margin: 0 0 20px;
    }

    .price-section {
      margin-bottom: 20px;

      .kill-price {
        color: #ff4d4f;
        font-size: 32px;
        font-weight: bold;
      }

      .original-price {
        color: #999;
        font-size: 16px;
        text-decoration: line-through;
        margin-left: 15px;
      }
    }

    .info-items {
      margin-bottom: 20px;

      .info-item {
        display: flex;
        margin-bottom: 10px;

        .label {
          width: 80px;
          color: #999;
        }

        .value {
          color: #333;
        }
      }
    }

    .status-section {
      display: flex;
      align-items: center;
      margin-bottom: 20px;

      .status-tag {
        padding: 5px 15px;
        border-radius: 4px;
        font-size: 14px;
        margin-right: 20px;

        &.killing {
          background: #ff4d4f;
          color: #fff;
        }

        &.unbegin {
          background: #fa8c16;
          color: #fff;
        }

        &.ended {
          background: #999;
          color: #fff;
        }
      }

      .countdown {
        color: #ff4d4f;
        font-size: 16px;

        .time {
          font-weight: bold;
          font-size: 20px;
        }
      }
    }

    .action-section {
      .el-button {
        width: 200px;
        height: 50px;
        font-size: 18px;
      }

      .tips {
        color: #999;
        font-size: 12px;
        margin: 10px 0 0;
      }
    }
  }
}

.course-detail {
  background: #fff;
  border-radius: 8px;
  padding: 20px 30px;

  .intro-content, .notice-content {
    padding: 20px 0;
    line-height: 1.8;
    color: #666;
  }

  .notice-content {
    h4 {
      color: #333;
      margin: 0 0 15px;
    }

    ul {
      padding-left: 20px;
      margin: 0;

      li {
        margin-bottom: 10px;
      }
    }
  }

  .empty-text {
    color: #999;
    text-align: center;
    padding: 50px 0;
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