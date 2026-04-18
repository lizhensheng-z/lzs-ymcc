<template>
  <div class="user-message">
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
        <div class="message-container">
          <h2>消息中心</h2>

          <!-- 消息筛选 -->
          <div class="filter-bar">
            <el-radio-group v-model="query.type" @change="loadMessages">
              <el-radio-button :label="null">全部</el-radio-button>
              <el-radio-button :label="1">系统通知</el-radio-button>
              <el-radio-button :label="2">订单消息</el-radio-button>
              <el-radio-button :label="3">课程通知</el-radio-button>
            </el-radio-group>
            <el-button type="text" @click="markAllRead" :disabled="unreadCount === 0">
              全部标为已读
            </el-button>
          </div>

          <!-- 消息列表 -->
          <div class="message-list" v-loading="loading">
            <div
              v-for="msg in messages"
              :key="msg.id"
              class="message-item"
              :class="{ unread: !msg.isRead }"
              @click="viewMessage(msg)">
              <div class="message-icon">
                <i :class="getMessageIcon(msg.type)"></i>
              </div>
              <div class="message-content">
                <div class="message-header">
                  <span class="title">{{ msg.title }}</span>
                  <span class="time">{{ formatTime(msg.createTime) }}</span>
                </div>
                <div class="message-body">{{ msg.content }}</div>
              </div>
              <div class="message-actions" v-if="!msg.isRead">
                <el-tag type="danger" size="mini">未读</el-tag>
              </div>
            </div>

            <!-- 空状态 -->
            <div class="empty" v-if="!loading && messages.length === 0">
              <i class="el-icon-message"></i>
              <p>暂无消息</p>
            </div>
          </div>

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
        </div>
      </div>
    </div>

    <!-- 消息详情弹窗 -->
    <el-dialog :title="currentMessage ? currentMessage.title : ''" :visible.sync="dialogVisible" width="500px">
      <div v-if="currentMessage">
        <p class="dialog-time">时间：{{ formatTime(currentMessage.createTime) }}</p>
        <div class="dialog-content">{{ currentMessage.content }}</div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <!-- 底部 -->
    <div class="footer">
      <p>&copy; 2026 云课教育 版权所有</p>
    </div>
  </div>
</template>

<script>
import defaultAvatarImg from '@/assets/java.jpeg'

export default {
  name: 'UserMessage',
  data() {
    return {
      activeMenu: '/user/msg',
      isLoggedIn: false,
      userInfo: {},
      defaultAvatar: defaultAvatarImg,
      messages: [],
      total: 0,
      loading: false,
      query: {
        page: 1,
        rows: 10,
        type: null
      },
      dialogVisible: false,
      currentMessage: null,
      unreadCount: 0
    }
  },
  mounted() {
    this.checkLogin()
    if (this.isLoggedIn) {
      this.loadUserInfo()
      this.loadMessages()
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
    loadMessages() {
      this.loading = true
      const params = {
        page: this.query.page,
        rows: this.query.rows
      }
      if (this.query.type !== null) {
        params.type = this.query.type
      }

      this.$http.post('/user/stationMessage/pagelist', params)
        .then(res => {
          if (res.data.success) {
            this.messages = res.data.data.rows || []
            this.total = res.data.data.total || 0
          }
        })
        .catch(() => {
          this.messages = []
          this.total = 0
        })
        .finally(() => {
          this.loading = false
        })
    },
    handlePageChange(page) {
      this.query.page = page
      this.loadMessages()
    },
    getMessageIcon(type) {
      const iconMap = {
        1: 'el-icon-bell',
        2: 'el-icon-s-order',
        3: 'el-icon-reading'
      }
      return iconMap[type] || 'el-icon-message'
    },
    formatTime(time) {
      if (!time) return '-'
      return new Date(time).toLocaleString()
    },
    viewMessage(msg) {
      this.currentMessage = msg
      this.dialogVisible = true
      // 标记已读
      if (!msg.isRead) {
        this.$http.put(`/user/stationMessage/${msg.id}`)
          .then(() => {
            msg.isRead = true
            this.unreadCount--
          })
          .catch(() => {})
      }
    },
    markAllRead() {
      this.$http.put('/user/stationMessage/readAll')
        .then(res => {
          if (res.data.success) {
            this.$message.success('已全部标为已读')
             this.loadMessages()
           }
         })
         .catch(() => {})
    },
    handleAvatarError(e) {
      // 头像加载失败时使用默认头像
      e.target.src = this.defaultAvatar
    },
    logout() {
      localStorage.clear()
      this.$router.push('/login')
    }
  }
}
</script>

<style lang="scss" scoped>
.user-message {
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

.message-container {
  background: #fff;
  border-radius: 8px;
  padding: 30px;

  h2 {
    margin: 0 0 20px;
    font-size: 24px;
  }
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.message-list {
  .message-item {
    display: flex;
    align-items: center;
    padding: 15px;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    transition: background 0.3s;

    &:hover {
      background: #f9f9f9;
    }

    &.unread {
      background: #f0f9ff;

      .title {
        font-weight: bold;
      }
    }

    .message-icon {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background: #667eea;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 15px;

      i {
        font-size: 18px;
        color: #fff;
      }
    }

    .message-content {
      flex: 1;

      .message-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 5px;

        .title {
          font-size: 14px;
          color: #333;
        }

        .time {
          font-size: 12px;
          color: #999;
        }
      }

      .message-body {
        font-size: 13px;
        color: #666;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .message-actions {
      margin-left: 10px;
    }
  }
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
    margin: 0;
    font-size: 16px;
  }
}

.dialog-time {
  color: #999;
  font-size: 14px;
  margin-bottom: 15px;
}

.dialog-content {
  line-height: 1.8;
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