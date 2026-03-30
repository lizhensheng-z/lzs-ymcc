<template>
  <div class="course-list-page">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="header-content">
        <div class="logo">
          <router-link to="/">云课教育</router-link>
        </div>
        <div class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/course/list" class="active">课程列表</router-link>
          <router-link to="/cart">购物车</router-link>
        </div>
        <div class="user-info">
          <template v-if="isLoggedIn">
            <router-link to="/user">我的课程</router-link>
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
      <div class="filter-bar">
        <div class="filter-item">
          <span class="label">课程分类：</span>
          <el-cascader
            v-model="searchForm.courseTypeId"
            :options="courseTypes"
            :props="{ value: 'id', label: 'name', children: 'children', checkStrictly: true, expandTrigger: 'hover' }"
            placeholder="请选择分类"
            clearable
            @change="loadCourses"
          ></el-cascader>
        </div>
        <div class="filter-item">
          <span class="label">课程等级：</span>
          <el-select v-model="searchForm.level" placeholder="请选择" @change="loadCourses">
            <el-option label="全部" value=""></el-option>
            <el-option label="初级" value="1"></el-option>
            <el-option label="中级" value="2"></el-option>
            <el-option label="高级" value="3"></el-option>
          </el-select>
        </div>
        <div class="filter-item">
          <span class="label">价格：</span>
          <el-select v-model="searchForm.priceType" placeholder="请选择" @change="loadCourses">
            <el-option label="全部" value=""></el-option>
            <el-option label="免费" value="0"></el-option>
            <el-option label="付费" value="1"></el-option>
          </el-select>
        </div>
        <div class="search-box">
          <el-input v-model="searchForm.keyword" placeholder="请输入课程名称" @keyup.enter="loadCourses">
            <el-button slot="append" icon="el-icon-search" @click="loadCourses"></el-button>
          </el-input>
        </div>
      </div>

      <div class="course-grid">
        <div class="course-card" v-for="course in courses" :key="course.id" @click="goDetail(course.id)">
          <div class="course-img" :style="course.image ? {} : { background: course.bg }">
            <img v-if="course.image" :src="course.image" :alt="course.title">
            <div v-else class="course-placeholder">{{ (course.title || '课').charAt(0) }}</div>
          </div>
          <div class="course-info">
            <h3>{{ course.title }}</h3>
            <p class="course-desc">{{ course.description }}</p>
            <div class="course-meta">
              <span class="teacher">{{ course.teacherName }}</span>
              <span class="student-count">{{ course.studentCount }}人</span>
            </div>
            <div class="course-footer">
              <span class="price" v-if="course.price > 0">¥{{ course.price }}</span>
              <span class="price free" v-else>免费</span>
              <el-button type="primary" size="small" @click.stop="addToCart(course)">加入购物车</el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="pagination">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          :page-size="searchForm.pageSize"
          :current-page="searchForm.pageNum"
          @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CourseList',
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      courses: [],
      courseTypes: [],
      total: 0,
      searchForm: {
        courseTypeId: '',
        level: '',
        priceType: '',
        keyword: '',
        pageNum: 1,
        pageSize: 12
      }
    }
  },
  mounted() {
    this.checkLogin()
    this.loadCourseTypes()
    this.loadCourses()
  },
  methods: {
    checkLogin() {
      this.isLoggedIn = !!localStorage.getItem('U-TOKEN')
      if (this.isLoggedIn) {
        // 从 localStorage 获取用户信息
        const user = localStorage.getItem('user')
        if (user) {
          this.userInfo = JSON.parse(user)
        }
      }
    },
    loadCourseTypes() {
      this.$http.get('/course/courseType/treeData')
        .then(res => {
          if (res.data.success) {
            this.courseTypes = res.data.data || []
          }
        })
    },
    loadCourses() {
      // 处理级联选择器的值，取最后一个选中项
      const params = { ...this.searchForm }
      if (params.courseTypeId && Array.isArray(params.courseTypeId)) {
        params.courseTypeId = params.courseTypeId[params.courseTypeId.length - 1]
      }
      this.$http.post('/course/course/pagelist', params)
        .then(res => {
          if (res.data.success) {
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
            this.courses = (res.data.data.rows || []).map((c, i) => ({
              ...c,
              title: c.name,
              description: c.forUser,
              image: c.pic,
              teacherName: c.teacherNames,
              price: 0,
              studentCount: c.chapterCount || 0,
              bg: colors[i % colors.length]
            }))
            this.total = res.data.data.total || 0
          }
        })
        .catch(() => {
          // 使用模拟数据
          this.courses = [
            { id: 1, title: 'Vue.js 实战课程', description: '从入门到精通', price: 99, studentCount: 1234, teacherName: '张老师', image: 'https://via.placeholder.com/300x200' },
            { id: 2, title: 'React 进阶课程', description: '深入理解 React 原理', price: 129, studentCount: 2345, teacherName: '李老师', image: 'https://via.placeholder.com/300x200' },
            { id: 3, title: 'Node.js 全栈开发', description: '打造完整全栈项目', price: 199, studentCount: 3456, teacherName: '王老师', image: 'https://via.placeholder.com/300x200' },
            { id: 4, title: 'Python 入门到实战', description: '零基础学习 Python', price: 0, studentCount: 4567, teacherName: '赵老师', image: 'https://via.placeholder.com/300x200' }
          ]
          this.total = 4
        })
    },
    handlePageChange(page) {
      this.searchForm.pageNum = page
      this.loadCourses()
    },
    goDetail(id) {
      this.$router.push(`/course/detail/${id}`)
    },
    addToCart(course) {
      if (!this.isLoggedIn) {
        this.$router.push('/login')
        return
      }
      this.$http.post('/order/cart/add', {
        courseId: course.id,
        courseName: course.title,
        coursePic: course.image,
        coursePrice: course.price,
        oldCoursePrice: course.price,
        loginId: this.userInfo.id
      }).then(res => {
        if (res.data.success) {
          this.$message.success('已加入购物车')
        } else {
          this.$message.error(res.data.message || '加入失败')
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.course-list-page {
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

  .user-info a {
    margin-left: 20px;
    color: #333;
    text-decoration: none;

    &:hover {
      color: #667eea;
    }
  }
}

.main-content {
  width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.filter-bar {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 20px;

  .filter-item {
    display: flex;
    align-items: center;

    .label {
      white-space: nowrap;
    }
  }

  .search-box {
    margin-left: auto;
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
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  }

  .course-img {
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
      color: #999;
      font-size: 12px;
      margin-bottom: 10px;
    }

    .course-footer {
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
    }
  }
}

.pagination {
  margin-top: 30px;
  text-align: center;
}
</style>