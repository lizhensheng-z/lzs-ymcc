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

    <!-- 秒杀专区入口 -->
    <div class="kill-section" v-if="killCourses.length > 0">
      <div class="section-header">
        <h2>限时秒杀</h2>
        <router-link to="/kill">查看全部</router-link>
      </div>
      <div class="kill-list">
       <div class="kill-card" v-for="course in killCourses" :key="course.id" @click="goKillDetail(course)">
         <div class="kill-img">
           <img v-if="course.coursePic" :src="course.coursePic" :alt="course.courseName" @error="handleKillImageError">
           <img v-else :src="defaultCover" :alt="course.courseName">
           <div class="kill-badge">秒杀</div>
         </div>
         <div class="kill-info">
           <h3>{{ course.courseName }}</h3>
           <div class="kill-price">
             <span class="current-price">¥{{ course.killPrice }}</span>
             <span class="original-price">¥{{ course.price }}</span>
           </div>
           <div class="kill-status">
             <span v-if="course.isUnbegin" class="status unbegin">即将开始</span>
             <span v-else-if="course.isKilling" class="status killing">秒杀中</span>
             <span v-else class="status ended">已结束</span>
             <span class="count">库存: {{ course.killCount }}</span>
           </div>
         </div>
       </div>
     </div>
    </div>

    <!-- 课程列表 -->
     <div class="course-section">
       <div class="section-header">
         <h2>热门课程</h2>
         <router-link to="/course/list">查看更多</router-link>
       </div>
       <div class="course-list" v-if="hotCourses.length > 0">
         <div class="course-card" v-for="course in hotCourses" :key="course.id" @click="goDetail(course.id)">
           <div class="course-img" :style="course.image ? {} : { background: course.bg }">
             <img v-if="course.image" :src="course.image" :alt="course.title" @error="handleCourseImageError">
             <img v-else :src="defaultCover" :alt="course.title">
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
       <!-- 空状态 -->
       <div class="empty-courses" v-else>
         <i class="el-icon-reading"></i>
         <p>暂无热门课程</p>
         <router-link to="/course/list">查看全部课程</router-link>
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
      <p>&copy; 2026 云课教育 版权所有</p>
    </div>
  </div>
</template>

<script>
import defaultCoverImg from '@/assets/java.jpeg'

export default {
  name: 'CourseIndex',
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      killCourses: [],  // 秒杀课程列表
      defaultCover: defaultCoverImg,
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
    this.loadKillCourses()
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
      // 调用课程分页接口，获取热门课程（取第一页，8条数据）
      this.$http.post('/course/course/pagelist', {
        page: 1,
        rows: 8,
        keyword: ''
      })
        .then(res => {
          if (res.data.success) {
            const courses = res.data.data.rows || []
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
              id: c.id,
              title: c.name,
              description: c.forUser || '暂无描述',
              image: c.pic || '',
              price: 0, // Course表没有价格字段，价格在CourseMarket表
              studentCount: c.chapterCount || 0,
              teacherName: c.teacherNames || '',
              bg: colors[i % colors.length]
            }))
          } else {
            this.$message.error('加载课程失败：' + (res.data.message || '未知错误'))
          }
        })
        .catch(error => {
          console.error('加载热门课程失败：', error)
          this.$message.error('加载课程失败，请稍后重试')
          this.hotCourses = []
        })
    },
    goDetail(id) {
       this.$router.push(`/course/detail/${id}`)
     },
     // 处理课程图片加载失败
     handleCourseImageError(e) {
       // 课程图片加载失败时使用默认图片
       e.target.src = this.defaultCover
     },
     // 处理秒杀课程图片加载失败
     handleKillImageError(e) {
       // 秒杀课程图片加载失败时使用默认图片
       e.target.src = this.defaultCover
     },
     // 加载秒杀课程
    loadKillCourses() {
      this.$http.get('/kill/killCourse/online/all')
        .then(res => {
          if (res.data.success) {
            // 只取前4个展示在首页
            const courses = (res.data.data || []).slice(0, 4)
            // 兼容后端字段名
            courses.forEach(item => {
              item.isKilling = item.isKilling !== undefined ? item.isKilling : item.killing
              item.isUnbegin = item.isUnbegin !== undefined ? item.isUnbegin : item.unbegin
            })
            this.killCourses = courses
          }
        })
        .catch(() => {
          // 加载失败不显示秒杀专区
          this.killCourses = []
        })
    },
    // 跳转秒杀详情
    goKillDetail(course) {
      this.$router.push(`/kill/detail/${course.id}/${course.activityId}`)
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

// 秒杀专区样式
.kill-section {
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
      color: #ff4d4f;
    }

    a {
      color: #667eea;
      text-decoration: none;
    }
  }
}

.kill-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.kill-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 16px rgba(255, 77, 79, 0.2);
    border-color: #ff4d4f;
  }

  .kill-img {
    position: relative;
    height: 140px;
    background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
    display: flex;
    align-items: center;
    justify-content: center;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .kill-placeholder {
      font-size: 48px;
      font-weight: bold;
      color: #fff;
      opacity: 0.8;
    }

    .kill-badge {
      position: absolute;
      top: 0;
      right: 0;
      background: #ff4d4f;
      color: #fff;
      padding: 4px 12px;
      font-size: 12px;
      border-radius: 0 0 0 8px;
    }
  }

  .kill-info {
    padding: 15px;

    h3 {
      margin: 0 0 10px;
      font-size: 16px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .kill-price {
      margin-bottom: 10px;

      .current-price {
        color: #ff4d4f;
        font-size: 20px;
        font-weight: bold;
      }

      .original-price {
        color: #999;
        font-size: 14px;
        text-decoration: line-through;
        margin-left: 10px;
      }
    }

    .kill-status {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .status {
        padding: 2px 8px;
        border-radius: 4px;
        font-size: 12px;

        &.unbegin {
          background: #fff7e6;
          color: #fa8c16;
        }

        &.killing {
          background: #fff1f0;
          color: #ff4d4f;
          animation: pulse 1s infinite;
        }

        &.ended {
          background: #f5f5f5;
          color: #999;
        }
      }

      .count {
        color: #999;
        font-size: 12px;
      }
    }
  }
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.6; }
  100% { opacity: 1; }
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

 .empty-courses {
   text-align: center;
   padding: 80px 20px;
   background: #fff;
   border-radius: 8px;

   i {
     font-size: 64px;
     color: #ddd;
     margin-bottom: 20px;
   }

   p {
     color: #999;
     font-size: 16px;
     margin: 0 0 20px;
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