<template>
  <div class="user-profile">
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
      <div class="profile-container">
        <h2>个人资料</h2>

        <el-form :model="profileForm" :rules="rules" ref="profileForm" label-width="100px" class="profile-form">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="profileForm.username" disabled></el-input>
          </el-form-item>

          <el-form-item label="昵称" prop="nickName">
            <el-input v-model="profileForm.nickName" placeholder="请输入昵称"></el-input>
          </el-form-item>

          <el-form-item label="手机号" prop="phone">
            <el-input v-model="profileForm.phone" placeholder="请输入手机号"></el-input>
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input v-model="profileForm.email" placeholder="请输入邮箱"></el-input>
          </el-form-item>

          <el-form-item label="性别" prop="sex">
            <el-radio-group v-model="profileForm.sex">
              <el-radio :label="0">男</el-radio>
              <el-radio :label="1">女</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="生日">
            <el-date-picker
              v-model="profileForm.birthday"
              type="date"
              placeholder="选择生日"
              value-format="yyyy-MM-dd">
            </el-date-picker>
          </el-form-item>

          <el-form-item label="头像">
            <el-upload
              class="avatar-uploader"
              :action="uploadAction"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :on-error="handleAvatarError"
              :before-upload="beforeAvatarUpload"
              :headers="uploadHeaders"
              name="file">
              <img v-if="profileForm.headImg" :src="profileForm.headImg" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>

          <el-form-item label="签名">
            <el-input
              type="textarea"
              v-model="profileForm.sign"
              :rows="3"
              placeholder="请输入个性签名"></el-input>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitForm" :loading="saving">保存</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
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
  name: 'UserProfile',
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      saving: false,
      uploadAction: 'http://localhost:10010/ymcc/common/oss/uploadFile', // 后端上传接口地址（网关端口 10010）
      uploadHeaders: {},
      profileForm: {
        username: '',
        nickName: '',
        phone: '',
        email: '',
        sex: 0,
        birthday: '',
        headImg: '',
        sign: ''
      },
      rules: {
        nickName: [
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.checkLogin()
    if (this.isLoggedIn) {
      this.loadUserInfo()
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
        // 设置上传请求的认证头
        const token = localStorage.getItem('U-TOKEN')
        this.uploadHeaders = {
          'Authorization': 'Bearer ' + token
        }
      } else {
        this.$router.push('/login')
      }
    },
    loadUserInfo() {
      this.$http.get('/user/user/current')
        .then(res => {
          if (res.data.success) {
            const data = res.data.data
            this.profileForm.username = data.username || ''
            this.profileForm.nickName = data.nickName || ''
            this.profileForm.phone = data.phone || ''
            this.profileForm.email = data.email || ''
            this.profileForm.headImg = data.avatar || data.headImg || ''
          }
        })
        .catch(() => {})
    },
    submitForm() {
      this.$refs.profileForm.validate(valid => {
        if (valid) {
          this.saving = true
          this.$http.post('/user/user/save', {
            id: this.userInfo.id,
            nickName: this.profileForm.nickName,
            phone: this.profileForm.phone,
            email: this.profileForm.email,
            sex: this.profileForm.sex,
            birthday: this.profileForm.birthday,
            headImg: this.profileForm.headImg,
            sign: this.profileForm.sign
          }).then(res => {
            if (res.data.success) {
              this.$message.success('保存成功')
            } else {
              this.$message.error(res.data.message || '保存失败')
            }
          }).catch(() => {
            this.$message.error('保存失败')
          }).finally(() => {
            this.saving = false
          })
        }
      })
    },
    resetForm() {
      this.$refs.profileForm.resetFields()
      this.loadUserInfo()
    },
    handleAvatarSuccess(res, file) {
      // 使用后端返回的真实图片 URL
      if (res.success && res.data) {
        this.profileForm.headImg = res.data
        this.$message.success('头像上传成功')
      } else {
        this.$message.error(res.message || '上传失败')
      }
    },
    handleAvatarError() {
      this.$message.error('头像上传失败，请重试')
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG/PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    },
    logout() {
      localStorage.clear()
      this.$router.push('/')
    }
  }
}
</script>

<style lang="scss" scoped>
.user-profile {
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

.profile-container {
  background: #fff;
  border-radius: 8px;
  padding: 30px;

  h2 {
    margin: 0 0 20px;
    font-size: 24px;
  }
}

.profile-form {
  max-width: 600px;
}

.avatar-uploader {
  .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;

    &:hover {
      border-color: #409EFF;
    }
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    line-height: 100px;
    text-align: center;
  }

  .avatar {
    width: 100px;
    height: 100px;
    display: block;
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