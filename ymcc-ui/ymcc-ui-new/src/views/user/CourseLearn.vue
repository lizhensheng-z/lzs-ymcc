<template>
  <div class="course-learn">
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
        </div>
      </div>
    </div>

    <!-- 学习主体 -->
    <div class="learn-content">
      <!-- 视频区域 -->
      <div class="video-section">
        <div class="video-container">
          <div v-if="currentVideo" class="video-player">
            <video
              ref="videoPlayer"
              :src="currentVideoUrl"
              controls
              controlsList="nodownload"
              @ended="onVideoEnded"
              @timeupdate="onTimeUpdate"
            ></video>
            <div v-if="!currentVideoUrl" class="video-placeholder">
              <i class="el-icon-video-play"></i>
              <p>暂无视频</p>
            </div>
          </div>
          <div v-else class="video-placeholder">
            <i class="el-icon-video-play"></i>
            <p>请选择要学习的视频</p>
          </div>
        </div>
        <div class="video-info">
          <h2>{{ currentVideo ? currentVideo.name : '请选择视频' }}</h2>
          <p v-if="course.name">课程：{{ course.name }}</p>
        </div>
      </div>

      <!-- 章节列表 -->
      <div class="chapter-section">
        <div class="chapter-header">
          <h3>课程目录</h3>
          <span class="progress-text">已学习 {{ learnedCount }}/{{ totalCount }}</span>
        </div>
        <el-collapse v-model="activeChapter" accordion>
          <el-collapse-item v-for="chapter in chapters" :key="chapter.id" :name="chapter.id">
            <template slot="title">
              <div class="chapter-title">
                <span class="chapter-name">{{ chapter.name }}</span>
                <span class="chapter-duration" v-if="chapter.totalTime">{{ chapter.totalTime }}分钟</span>
              </div>
            </template>
            <div class="video-list">
              <div
                class="video-item"
                :class="{ active: currentVideo && currentVideo.id === video.id, learned: video.learned }"
                v-for="video in chapter.videos"
                :key="video.id"
                @click="selectVideo(video)"
              >
                <i :class="video.learned ? 'el-icon-circle-check' : 'el-icon-video-play'"></i>
                <span class="video-name">{{ video.name }}</span>
                <span class="video-duration" v-if="video.duration">{{ video.duration }}分钟</span>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </div>

    <!-- 返回按钮 -->
    <div class="back-btn">
      <el-button @click="$router.push('/user/courses')">返回我的课程</el-button>
    </div>
  </div>
</template>

<script>
import Hls from 'hls.js'

export default {
  name: 'CourseLearn',
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      courseId: null,
      course: {},
      chapters: [],
      activeChapter: null,
      currentVideo: null,
      currentVideoUrl: null,
      learnedCount: 0,
      totalCount: 0,
      hls: null
    }
  },
  watch: {
    currentVideo: {
      handler(newVideo) {
        if (newVideo && newVideo.videoUrl) {
          this.initVideoPlayer(newVideo.videoUrl)
        }
      },
      immediate: false
    }
  },
  beforeDestroy() {
    // 销毁 HLS 实例
    if (this.hls) {
      this.hls.destroy()
      this.hls = null
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
      } else {
        this.$router.push('/login')
      }
    },
    initVideoPlayer(videoUrl) {
      const video = this.$refs.videoPlayer
      if (!video) return

      // 销毁之前的 HLS 实例
      if (this.hls) {
        this.hls.destroy()
        this.hls = null
      }

      // 判断是否是 HLS (m3u8) 格式
      if (videoUrl.indexOf('.m3u8') > -1) {
        if (Hls.isSupported()) {
          // 浏览器支持 HLS
          const hls = new Hls()
          hls.loadSource(videoUrl)
          hls.attachMedia(video)
          hls.on(Hls.Events.MANIFEST_PARSED, () => {
            video.play().catch(() => {})
          })
          this.hls = hls
        } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
          // Safari 原生支持 HLS
          video.src = videoUrl
        }
      } else {
        // 普通视频直接设置 src
        video.src = videoUrl
      }
    },
    loadCourseDetail() {
      this.$http.get(`/course/course/${this.courseId}`)
        .then(res => {
          if (res.data.success) {
            this.course = res.data.data || {}
          }
        })
    },
    loadChapters() {
      // 加载章节和视频（后端已返回包含视频的章节列表）
      this.$http.get(`/course/courseChapter/listByCourseId/${this.courseId}`)
        .then(res => {
          if (res.data.success) {
            const chapters = res.data.data || []
            // 将 mediaFiles 映射为 videos
            this.chapters = chapters.map(chapter => ({
              ...chapter,
              videos: chapter.mediaFiles || []
            }))
            if (this.chapters.length > 0) {
              this.activeChapter = this.chapters[0].id
              // 计算总数
              this.totalCount = this.chapters.reduce((sum, ch) => sum + (ch.videos ? ch.videos.length : 0), 0)
              // 如果有视频ID参数，选择对应视频
              const videoId = this.$route.query.videoId
              if (videoId) {
                this.selectVideoById(videoId)
              } else if (this.chapters[0].videos && this.chapters[0].videos.length > 0) {
                this.selectVideo(this.chapters[0].videos[0])
              }
            }
          }
        })
        .catch(() => {
          // 模拟数据
          this.chapters = [
            {
              id: 1,
              name: '第一章：入门基础',
              totalTime: 60,
              videos: [
                { id: 1, name: '1.1 课程介绍', duration: 10, learned: true },
                { id: 2, name: '1.2 环境搭建', duration: 20, learned: false },
                { id: 3, name: '1.3 第一个程序', duration: 30, learned: false }
              ]
            },
            {
              id: 2,
              name: '第二章：核心技术',
              totalTime: 90,
              videos: [
                { id: 4, name: '2.1 核心概念', duration: 30, learned: false },
                { id: 5, name: '2.2 高级用法', duration: 30, learned: false },
                { id: 6, name: '2.3 实战项目', duration: 30, learned: false }
              ]
            }
          ]
          this.totalCount = 6
          if (this.chapters.length > 0 && this.chapters[0].videos) {
            this.activeChapter = this.chapters[0].id
            this.selectVideo(this.chapters[0].videos[0])
          }
        })
    },
    selectVideoById(videoId) {
      for (const chapter of this.chapters) {
        const video = chapter.videos.find(v => v.id == videoId)
        if (video) {
          this.selectVideo(video)
          break
        }
      }
    },
    selectVideo(video) {
      // 获取视频播放地址
      this.$http.get(`/media/mediaFile/getForUrl/${video.id}`)
        .then(res => {
          if (res.data.success && res.data.data) {
            // 设置视频URL，支持HLS(m3u8)格式
            this.$set(video, 'videoUrl', res.data.data)
            this.currentVideoUrl = res.data.data
          }
        })
        .catch(() => {
          // 如果获取失败，尝试使用其他字段
          const url = video.fileUrl || video.url
          this.$set(video, 'videoUrl', url)
          this.currentVideoUrl = url
        })

      this.currentVideo = video

      // 更新学习状态
      if (!video.learned) {
        video.learned = true
        this.learnedCount = this.chapters.reduce((sum, ch) =>
          sum + (ch.videos ? ch.videos.filter(v => v.learned).length : 0), 0)
        // 可以调用API保存学习进度
        this.saveProgress(video.id)
      }
    },
    saveProgress(videoId) {
      // 保存学习进度到后端（可选功能，静默失败）
      this.$http.post('/user/userCourse/saveProgress', {
        courseId: this.courseId,
        videoId: videoId,
        userId: this.userInfo.id
      }).catch(() => {
        // 静默处理，不显示错误
      })
    },
    onVideoEnded() {
      this.$message.success('视频播放完成')
    },
    onTimeUpdate() {
      // 可以记录播放进度
    },
    logout() {
      localStorage.clear()
      this.$router.push('/')
    }
  }
}
</script>

<style lang="scss" scoped>
.course-learn {
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

.learn-content {
  display: flex;
  width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
  gap: 20px;
}

.video-section {
  flex: 1;

  .video-container {
    background: #000;
    border-radius: 8px;
    overflow: hidden;
    aspect-ratio: 16 / 9;

    .video-player {
      width: 100%;
      height: 100%;

      video {
        width: 100%;
        height: 100%;
        object-fit: contain;
      }
    }

    .video-placeholder {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #fff;
      background: #333;

      i {
        font-size: 64px;
        margin-bottom: 20px;
      }

      p {
        font-size: 18px;
      }
    }
  }

  .video-info {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    margin-top: 20px;

    h2 {
      margin: 0 0 10px;
      font-size: 20px;
    }

    p {
      margin: 0;
      color: #999;
    }
  }
}

.chapter-section {
  width: 350px;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  max-height: 600px;
  overflow-y: auto;

  .chapter-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;

    h3 {
      margin: 0;
      font-size: 18px;
    }

    .progress-text {
      color: #667eea;
      font-size: 14px;
    }
  }

  .chapter-title {
    width: 100%;
    display: flex;
    justify-content: space-between;

    .chapter-name {
      font-weight: bold;
    }

    .chapter-duration {
      color: #999;
      font-size: 12px;
    }
  }

  .video-list {
    .video-item {
      display: flex;
      align-items: center;
      padding: 12px 15px;
      cursor: pointer;
      border-radius: 4px;
      transition: all 0.3s;

      &:hover {
        background: #f5f5f5;
      }

      &.active {
        background: #e6f7ff;
        color: #667eea;
      }

      &.learned {
        color: #52c41a;
      }

      i {
        margin-right: 10px;
        font-size: 16px;
      }

      .video-name {
        flex: 1;
        font-size: 14px;
      }

      .video-duration {
        color: #999;
        font-size: 12px;
      }
    }
  }
}

.back-btn {
  width: 1200px;
  margin: 0 auto;
  padding: 0 20px 20px;
}

.footer {
  background: #333;
  color: #fff;
  text-align: center;
  padding: 30px;

  p {
    margin: 0;
  }
}
</style>