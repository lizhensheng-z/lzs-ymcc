<template>
  <div class="part-upload">
    <!-- 文件选择区域 -->
    <div class="upload-area" v-if="!uploading">
      <el-upload
        class="upload-demo"
        drag
        action=""
        :auto-upload="false"
        :on-change="handleFileChange"
        :show-file-list="false">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">支持视频文件上传，大文件将自动分片</div>
      </el-upload>
    </div>

    <!-- 上传进度区域 -->
    <div class="upload-progress" v-if="uploading">
      <el-card>
        <div slot="header">
          <span>上传进度</span>
          <el-button
            style="float: right; padding: 3px 0"
            type="text"
            @click="cancelUpload">
            取消上传
          </el-button>
        </div>

        <div class="file-info">
          <p><strong>文件名：</strong>{{ file.name }}</p>
          <p><strong>文件大小：</strong>{{ formatFileSize(file.size) }}</p>
          <p><strong>文件MD5：</strong>{{ fileMd5 }}</p>
        </div>

        <div class="progress-item">
          <span>整体进度：</span>
          <el-progress :percentage="totalProgress" :status="progressStatus"></el-progress>
        </div>

        <div class="progress-item">
          <span>当前分块：</span>
          <el-progress :percentage="chunkProgress" :status="progressStatus"></el-progress>
        </div>

        <div class="upload-status">
          <p>{{ uploadStatusText }}</p>
        </div>
      </el-card>
    </div>

    <!-- 上传完成区域 -->
    <div class="upload-complete" v-if="uploadComplete">
      <el-alert
        :title="completeMessage"
        :type="completeType"
        :closable="false"
        show-icon>
      </el-alert>
      <div style="margin-top: 15px; text-align: center;">
        <el-button type="primary" @click="resetUpload" v-if="uploadSuccess">继续上传</el-button>
        <el-button type="danger" @click="resetUpload" v-else>重新上传</el-button>
      </div>
    </div>
  </div>
</template>

<script>
/**
 * 视频分片上传组件
 * 实现功能：
 * 1. 文件选择和 MD5 计算
 * 2. 文件注册（调用 /media/mediaFile/register）
 * 3. 分块检查（调用 /media/mediaFile/checkchunk）
 * 4. 分块上传（调用 /media/mediaFile/uploadchunk）
 * 5. 分块合并（调用 /media/mediaFile/mergechunks）
 * 6. 上传进度显示
 */

/**
 * 简单的 MD5 实现 - 用于文件分片上传的 MD5 计算
 * 基于 SparkMD5 的简化版本
 */
const SimpleMD5 = (function() {
  function md5cycle(x, k) {
    let a = x[0], b = x[1], c = x[2], d = x[3];
    a = ff(a, b, c, d, k[0], 7, -680876936);
    d = ff(d, a, b, c, k[1], 12, -389564586);
    c = ff(c, d, a, b, k[2], 17, 606105819);
    b = ff(b, c, d, a, k[3], 22, -1044525330);
    a = ff(a, b, c, d, k[4], 7, -176418897);
    d = ff(d, a, b, c, k[5], 12, 1200080426);
    c = ff(c, d, a, b, k[6], 17, -1473231341);
    b = ff(b, c, d, a, k[7], 22, -45705983);
    a = ff(a, b, c, d, k[8], 7, 1770035416);
    d = ff(d, a, b, c, k[9], 12, -1958414417);
    c = ff(c, d, a, b, k[10], 17, -42063);
    b = ff(b, c, d, a, k[11], 22, -1990404162);
    a = ff(a, b, c, d, k[12], 7, 1804603682);
    d = ff(d, a, b, c, k[13], 12, -40341101);
    c = ff(c, d, a, b, k[14], 17, -1502002290);
    b = ff(b, c, d, a, k[15], 22, 1236535329);
    a = gg(a, b, c, d, k[1], 5, -165796510);
    d = gg(d, a, b, c, k[6], 9, -1069501632);
    c = gg(c, d, a, b, k[11], 14, 643717713);
    b = gg(b, c, d, a, k[0], 20, -373897302);
    a = gg(a, b, c, d, k[5], 5, -701558691);
    d = gg(d, a, b, c, k[10], 9, 38016083);
    c = gg(c, d, a, b, k[15], 14, -660478335);
    b = gg(b, c, d, a, k[4], 20, -405537848);
    a = gg(a, b, c, d, k[9], 5, 568446438);
    d = gg(d, a, b, c, k[14], 9, -1019803690);
    c = gg(c, d, a, b, k[3], 14, -187363961);
    b = gg(b, c, d, a, k[8], 20, 1163531501);
    a = gg(a, b, c, d, k[13], 5, -1444681467);
    d = gg(d, a, b, c, k[2], 9, -51403784);
    c = gg(c, d, a, b, k[7], 14, 1735328473);
    b = gg(b, c, d, a, k[12], 20, -1926607734);
    a = hh(a, b, c, d, k[5], 4, -378558);
    d = hh(d, a, b, c, k[8], 11, -2022574463);
    c = hh(c, d, a, b, k[11], 16, 1839030562);
    b = hh(b, c, d, a, k[14], 23, -35309556);
    a = hh(a, b, c, d, k[1], 4, -1530992060);
    d = hh(d, a, b, c, k[4], 11, 1272893353);
    c = hh(c, d, a, b, k[7], 16, -155497632);
    b = hh(b, c, d, a, k[10], 23, -1094730640);
    a = hh(a, b, c, d, k[13], 4, 681279174);
    d = hh(d, a, b, c, k[0], 11, -358537222);
    c = hh(c, d, a, b, k[3], 16, -722521979);
    b = hh(b, c, d, a, k[6], 23, 76029189);
    a = hh(a, b, c, d, k[9], 4, -640364487);
    d = hh(d, a, b, c, k[12], 11, -421815835);
    c = hh(c, d, a, b, k[15], 16, 530742520);
    b = hh(b, c, d, a, k[2], 23, -995338651);
    a = ii(a, b, c, d, k[0], 6, -198630844);
    d = ii(d, a, b, c, k[7], 10, 1126891415);
    c = ii(c, d, a, b, k[14], 15, -1416354905);
    b = ii(b, c, d, a, k[5], 21, -57434055);
    a = ii(a, b, c, d, k[12], 6, 1700485571);
    d = ii(d, a, b, c, k[3], 10, -1894986606);
    c = ii(c, d, a, b, k[10], 15, -1051523);
    b = ii(b, c, d, a, k[1], 21, -2054922799);
    a = ii(a, b, c, d, k[8], 6, 1873313359);
    d = ii(d, a, b, c, k[15], 10, -30611744);
    c = ii(c, d, a, b, k[6], 15, -1560198380);
    b = ii(b, c, d, a, k[13], 21, 1309151649);
    a = ii(a, b, c, d, k[4], 6, -145523070);
    d = ii(d, a, b, c, k[11], 10, -1120210379);
    c = ii(c, d, a, b, k[2], 15, 718787259);
    b = ii(b, c, d, a, k[9], 21, -343485551);
    x[0] = add32(a, x[0]);
    x[1] = add32(b, x[1]);
    x[2] = add32(c, x[2]);
    x[3] = add32(d, x[3]);
  }

  function cmn(q, a, b, x, s, t) {
    a = add32(add32(a, q), add32(x, t));
    return add32((a << s) | (a >>> (32 - s)), b);
  }

  function ff(a, b, c, d, x, s, t) {
    return cmn((b & c) | ((~b) & d), a, b, x, s, t);
  }

  function gg(a, b, c, d, x, s, t) {
    return cmn((b & d) | (c & (~d)), a, b, x, s, t);
  }

  function hh(a, b, c, d, x, s, t) {
    return cmn(b ^ c ^ d, a, b, x, s, t);
  }

  function ii(a, b, c, d, x, s, t) {
    return cmn(c ^ (b | (~d)), a, b, x, s, t);
  }

  function md51(s) {
    const n = s.length;
    const state = [1732584193, -271733879, -1732584194, 271733878];
    let i;
    for (i = 64; i <= s.length; i += 64) {
      md5cycle(state, bytesToWords(s.substring(i - 64, i)));
    }
    s = s.substring(i - 64);
    const tail = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
    for (i = 0; i < s.length; i++) {
      tail[i >> 2] |= s.charCodeAt(i) << ((i % 4) << 3);
    }
    tail[i >> 2] |= 0x80 << ((i % 4) << 3);
    if (i > 55) {
      md5cycle(state, tail);
      for (i = 0; i < 16; i++) tail[i] = 0;
    }
    tail[14] = n * 8;
    md5cycle(state, tail);
    return state;
  }

  function bytesToWords(b) {
    const words = [];
    for (let i = 0; i < 64; i += 4) {
      words[i >> 2] = b.charCodeAt(i) | (b.charCodeAt(i + 1) << 8) | (b.charCodeAt(i + 2) << 16) | (b.charCodeAt(i + 3) << 24);
    }
    return words;
  }

  function add32(a, b) {
    return (a + b) & 0xFFFFFFFF;
  }

  function hex(x) {
    let str = '';
    for (let i = 0; i < 4; i++) {
      const v = (x >>> (i * 8)) & 0xFF;
      str += (v < 16 ? '0' : '') + v.toString(16);
    }
    return str;
  }

  function hexHash(hash) {
    return hex(hash[0]) + hex(hash[1]) + hex(hash[2]) + hex(hash[3]);
  }

  // ArrayBuffer 版本的 MD5 计算
  class ArrayBufferMD5 {
    constructor() {
      this.reset();
    }

    reset() {
      this.buffer = '';
    }

    append(arrayBuffer) {
      const uint8Array = new Uint8Array(arrayBuffer);
      for (let i = 0; i < uint8Array.length; i++) {
        this.buffer += String.fromCharCode(uint8Array[i]);
      }
    }

    end() {
      const hash = md51(this.buffer);
      return hexHash(hash);
    }
  }

  return {
    ArrayBuffer: ArrayBufferMD5
  };
})();

export default {
  name: 'PartUpload',
  props: {
    // 课程 ID
    courseId: {
      type: [String, Number],
      default: ''
    },
    // 章节 ID
    chapterId: {
      type: [String, Number],
      default: ''
    },
    // 课程名称
    courseName: {
      type: String,
      default: ''
    },
    // 章节名称
    chapterName: {
      type: String,
      default: ''
    },
    // 视频名称
    name: {
      type: String,
      default: ''
    },
    // 视频序号（课程序列号）
    videoNumber: {
      type: [String, Number],
      default: 1
    }
  },
  data() {
    return {
      // 文件对象
      file: null,
      // 文件MD5值
      fileMd5: '',
      // 是否正在上传
      uploading: false,
      // 上传完成
      uploadComplete: false,
      // 上传成功
      uploadSuccess: false,
      // 完成消息
      completeMessage: '',
      // 完成类型
      completeType: 'success',
      // 整体进度
      totalProgress: 0,
      // 分块进度
      chunkProgress: 0,
      // 进度状态 - 不传表示没有状态
      progressStatus: undefined,
      // 上传状态文本
      uploadStatusText: '准备上传...',
      // 分块大小（2MB）
      chunkSize: 2 * 1024 * 1024,
      // 总分块数
      totalChunks: 0,
      // 当前分块索引
      currentChunk: 0,
      // 已上传的分块列表
      uploadedChunks: [],
      // 取消上传标志
      cancelFlag: false
    }
  },
  methods: {
    /**
     * 处理文件选择
     */
    handleFileChange(file) {
      this.file = file.raw;
      this.startUpload();
    },

    /**
     * 开始上传流程
     */
    async startUpload() {
      if (!this.file) {
        this.$message.error('请选择文件');
        return;
      }

      // 验证必填字段
      if (!this.courseId || !this.chapterId) {
        this.$message.error('请先选择课程和章节');
        return;
      }

      this.uploading = true;
      this.uploadComplete = false;
      this.cancelFlag = false;
      this.totalProgress = 0;
      this.chunkProgress = 0;
      this.uploadStatusText = '正在计算文件MD5...';

      try {
        // 1. 计算文件MD5
        this.fileMd5 = await this.calculateMd5(this.file);
        console.log('文件MD5:', this.fileMd5);

        // 2. 注册文件
        this.uploadStatusText = '正在注册文件...';
        const registerResult = await this.registerFile();
        if (!registerResult.success) {
          throw new Error(registerResult.message || '文件注册失败');
        }

        // 3. 检查已上传分块
        this.uploadStatusText = '正在检查已上传分块...';
        await this.checkChunks();

        // 4. 上传分块
        this.uploadStatusText = '正在上传分块...';
        await this.uploadChunks();

        // 5. 合并分块
        if (!this.cancelFlag) {
          this.uploadStatusText = '正在合并分块...';
          await this.mergeChunks();
        }

      } catch (error) {
        console.error('上传失败:', error);
        this.uploadSuccess = false;
        this.completeMessage = '上传失败：' + error.message;
        this.completeType = 'error';
        this.uploadComplete = true;
        this.uploading = false;
      }
    },

    /**
     * 计算文件MD5
     */
    calculateMd5(file) {
      return new Promise((resolve, reject) => {
        const chunkSize = 2 * 1024 * 1024; // 2MB
        const chunks = Math.ceil(file.size / chunkSize);
        let currentChunk = 0;
        const spark = new SimpleMD5.ArrayBuffer();
        const fileReader = new FileReader();

        fileReader.onload = (e) => {
          spark.append(e.target.result);
          currentChunk++;

          // 更新进度
          const progress = Math.round((currentChunk / chunks) * 100);
          this.chunkProgress = progress;

          if (currentChunk < chunks) {
            loadNext();
          } else {
            const md5 = spark.end();
            resolve(md5);
          }
        };

        fileReader.onerror = (e) => {
          reject(new Error('MD5计算失败'));
        };

        const loadNext = () => {
          const start = currentChunk * chunkSize;
          const end = Math.min(start + chunkSize, file.size);
          fileReader.readAsArrayBuffer(file.slice(start, end));
        };

        loadNext();
      });
    },

    /**
     * 注册文件
     */
    async registerFile() {
      const params = new URLSearchParams();
      params.append('fileMd5', this.fileMd5);
      params.append('fileName', this.file.name);
      params.append('fileSize', this.file.size);
      params.append('mimetype', this.file.type);
      params.append('fileExt', this.getFileExt(this.file.name));
      params.append('courseId', this.courseId);
      params.append('chapterId', this.chapterId);
      params.append('courseName', this.courseName);
      params.append('chapterName', this.chapterName);
      params.append('name', this.name || this.file.name);

      const result = await this.$http.post('/media/mediaFile/register', params, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      });
      return result.data;
    },

    /**
     * 检查已上传分块
     */
    async checkChunks() {
      this.totalChunks = Math.ceil(this.file.size / this.chunkSize);
      this.uploadedChunks = [];

      for (let i = 0; i < this.totalChunks; i++) {
        const params = new URLSearchParams();
        params.append('fileMd5', this.fileMd5);
        params.append('chunk', i);
        params.append('chunkSize', this.chunkSize);

        const result = await this.$http.post('/media/mediaFile/checkchunk', params, {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        });
        if (result.data.success && result.data.data) {
          this.uploadedChunks.push(i);
        }
      }

      // 更新整体进度
      this.totalProgress = Math.round((this.uploadedChunks.length / this.totalChunks) * 100);
    },

    /**
     * 上传分块
     */
    async uploadChunks() {
      for (let i = 0; i < this.totalChunks; i++) {
        if (this.cancelFlag) {
          break;
        }

        // 跳过已上传的分块
        if (this.uploadedChunks.includes(i)) {
          continue;
        }

        this.currentChunk = i;
        const start = i * this.chunkSize;
        const end = Math.min(start + this.chunkSize, this.file.size);
        const chunk = this.file.slice(start, end);

        const formData = new FormData();
        formData.append('file', chunk);
        formData.append('fileMd5', this.fileMd5);
        formData.append('chunk', i);

        try {
          const result = await this.$http.post('/media/mediaFile/uploadchunk', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            },
            onUploadProgress: (progressEvent) => {
              if (progressEvent.total > 0) {
                this.chunkProgress = Math.round((progressEvent.loaded / progressEvent.total) * 100);
              }
            }
          });

          if (result.data.success) {
            this.uploadedChunks.push(i);
            this.totalProgress = Math.round((this.uploadedChunks.length / this.totalChunks) * 100);
            this.uploadStatusText = `正在上传分块... (${i + 1}/${this.totalChunks})`;
          } else {
            throw new Error(result.data.message || '分块上传失败');
          }
        } catch (error) {
          throw new Error('分块 ' + i + ' 上传失败: ' + error.message);
        }
      }
    },

    /**
     * 合并分块
     */
    async mergeChunks() {
      const params = new URLSearchParams();
      params.append('fileMd5', this.fileMd5);
      params.append('fileName', this.file.name);
      params.append('fileSize', this.file.size);
      params.append('mimetype', this.file.type);
      params.append('fileExt', this.getFileExt(this.file.name));
      params.append('chunkTotal', this.totalChunks);
      params.append('courseId', this.courseId);
      params.append('chapterId', this.chapterId);
      params.append('courseName', this.courseName);
      params.append('chapterName', this.chapterName);
      params.append('name', this.name || this.file.name);
      // 添加 videoNumber 参数（课程序列号）
      params.append('videoNumber', this.videoNumber);
    
      const result = await this.$http.post('/media/mediaFile/mergechunks', params, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      });
    
      if (result.data.success) {
        this.uploadSuccess = true;
        this.completeMessage = '上传成功!';
        this.completeType = 'success';
        this.totalProgress = 100;
        this.chunkProgress = 100;
        this.uploadStatusText = '上传完成';
    
        // 通知父组件上传完成
        this.$emit('upload-success', result.data.data);
        this.$emit('addVideoFormVisibleClose');
      } else {
        throw new Error(result.data.message || '分块合并失败');
      }
    
      this.uploadComplete = true;
      this.uploading = false;
    },

    /**
     * 取消上传
     */
    cancelUpload() {
      this.$confirm('确认取消上传吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.cancelFlag = true;
        this.uploading = false;
        this.uploadComplete = false;
        this.file = null;
        this.fileMd5 = '';
        this.totalProgress = 0;
        this.chunkProgress = 0;
        this.uploadStatusText = '已取消上传';
        this.$message.info('已取消上传');
      });
    },

    /**
     * 重置上传状态
     */
    resetUpload() {
      this.uploading = false;
      this.uploadComplete = false;
      this.uploadSuccess = false;
      this.file = null;
      this.fileMd5 = '';
      this.totalProgress = 0;
      this.chunkProgress = 0;
      this.uploadStatusText = '准备上传...';
      this.uploadedChunks = [];
      this.currentChunk = 0;
      this.totalChunks = 0;
    },

    /**
     * 获取文件扩展名
     */
    getFileExt(filename) {
      const pos = filename.lastIndexOf('.');
      return pos > -1 ? filename.substring(pos + 1) : '';
    },

    /**
     * 格式化文件大小
     */
    formatFileSize(size) {
      if (size < 1024) {
        return size + ' B';
      } else if (size < 1024 * 1024) {
        return (size / 1024).toFixed(2) + ' KB';
      } else if (size < 1024 * 1024 * 1024) {
        return (size / (1024 * 1024)).toFixed(2) + ' MB';
      } else {
        return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB';
      }
    }
  }
}
</script>

<style scoped>
.part-upload {
  width: 100%;
}

.upload-area {
  text-align: center;
}

.upload-progress {
  margin-top: 20px;
}

.file-info {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.file-info p {
  margin: 5px 0;
  font-size: 14px;
}

.progress-item {
  margin: 15px 0;
}

.progress-item span {
  display: inline-block;
  width: 100px;
  font-size: 14px;
  color: #606266;
}

.upload-status {
  margin-top: 15px;
  text-align: center;
  color: #409EFF;
  font-size: 14px;
}

.upload-complete {
  margin-top: 20px;
}
</style>
