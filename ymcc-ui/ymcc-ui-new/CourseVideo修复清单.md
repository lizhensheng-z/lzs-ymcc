# CourseVideo.vue 课程视频管理页面修复清单

## 页面功能概述

CourseVideo 页面用于管理课程视频，包括以下功能：
1. 查询视频列表（分页）
2. 新增视频（分片上传）
3. 编辑视频信息
4. 删除视频
5. 设置视频试看/取消试看
6. 批量删除

## 后端服务映射

| 服务 | 网关路径 | 说明 |
|-----|---------|------|
| ymcc-service-media | `/ymcc/media/**` | 媒体文件服务（视频上传、管理） |
| ymcc-service-course | `/ymcc/course/**` | 课程服务（查询课程、章节） |

## 后端 Controller 接口清单

### MediaFileController.java
**路径**: `ymcc-service/ymcc-service-media/src/main/java/cn/lzs/ymcc/web/controller/MediaFileController.java`

| 接口 | 方法 | 说明 | 前端调用 |
|-----|------|------|---------|
| `/mediaFile/pagelist` | POST | 分页查询视频 | ✅ 已调用 |
| `/mediaFile/save` | POST | 保存/修改视频 | ✅ 已调用（通过合并分片） |
| `/mediaFile/{id}` | DELETE | 删除视频 | ❌ 未实现 |
| `/mediaFile/{id}` | GET | 获取视频详情 | ❌ 未实现 |
| `/mediaFile/list` | GET | 获取视频列表 | ❌ 未调用 |
| `/mediaFile/register` | POST | 文件注册（分片上传） | ✅ PartUpload 组件调用 |
| `/mediaFile/checkchunk` | POST | 检查分块 | ✅ PartUpload 组件调用 |
| `/mediaFile/uploadchunk` | POST | 上传分块 | ✅ PartUpload 组件调用 |
| `/mediaFile/mergechunks` | POST | 合并分块 | ✅ PartUpload 组件调用 |
| `/mediaFile/getForUrl/{mediaId}` | GET | 获取视频播放URL | ❌ 未实现 |
| `/mediaFile/update2Free/{id}` | POST | 设置/取消试看 | ❌ **接口缺失** |

### CourseChapterController.java
**路径**: `ymcc-service/ymcc-service-course/src/main/java/cn/lzs/ymcc/web/controller/CourseChapterController.java`

| 接口 | 方法 | 说明 | 前端调用 |
|-----|------|------|---------|
| `/courseChapter/listByCourseId/{id}` | GET | 根据课程ID查询章节 | ✅ 已调用 |

### CourseController.java
**路径**: `ymcc-service/ymcc-service-course/src/main/java/cn/lzs/ymcc/web/controller/CourseController.java`

| 接口 | 方法 | 说明 | 前端调用 |
|-----|------|------|---------|
| `/course/pagelist` | POST | 分页查询课程 | ✅ 已调用 |

---

## 问题列表

### 1. PartUpload 组件缺失
**文件路径**: `src/views/itsource/CourseVideo.vue` (第83-84行)

**问题描述**:
- 页面使用了 `<PartUpload>` 组件进行视频分片上传
- 但在 `main.js` 中该组件被注释掉了（第26-27行）
- 这会导致组件找不到，新增视频功能完全不可用

**相关代码**:
```javascript
// main.js 第26-27行（被注释）
// import PartUpload from 'upload.vue'
// Vue.component("PartUpload", PartUpload);

// CourseVideo.vue 第83-84行（使用组件）
<PartUpload v-bind="addVideoForm"
  @addVideoFormVisibleClose="addVideoFormVisibleClose"></PartUpload>
```

**修复方案**:

**方案A** - 如果 PartUpload 组件存在:
1. 取消 main.js 中的注释
2. 确保 `upload.vue` 文件存在于正确路径

**方案B** - 如果 PartUpload 组件不存在:
1. 需要创建 PartUpload 组件，实现以下功能：
   - 文件分片上传
   - 调用 `/media/mediaFile/register` 注册文件
   - 调用 `/media/mediaFile/checkchunk` 检查分块
   - 调用 `/media/mediaFile/uploadchunk` 上传分块
   - 调用 `/media/mediaFile/mergechunks` 合并分块

**推荐方案**: 先检查项目是否有现成的分片上传组件，如果没有需要创建。

---

### 2. 前端方法缺失
**文件路径**: `src/views/itsource/CourseVideo.vue`

**问题描述**: 模板中调用了多个方法，但在 `methods` 中未定义

#### 2.1 handleEdit 方法缺失
**模板位置**: 第35行
```html
<el-button size="small" @click="handleEdit(scope.row)" icon="el-icon-edit" type="primary">编辑</el-button>
```

**应实现功能**:
- 打开编辑对话框
- 回填视频信息
- 调用 `/media/mediaFile/save` 保存修改

**参考实现**:
```javascript
handleEdit: function(row) {
    this.dialogTitle = '编辑视频';
    this.addVideoForm = Object.assign({}, row);
    this.addVideoFormVisible = true;
}
```

#### 2.2 handleDel 方法缺失
**模板位置**: 第36行
```html
<el-button type="danger" size="small" @click="handleDel(scope.row)" icon="el-icon-remove">删除</el-button>
```

**应实现功能**:
- 确认删除
- 调用 `/media/mediaFile/{id}` DELETE 接口
- 刷新列表

**参考实现**:
```javascript
handleDel: function(row) {
    this.$confirm('确认删除该视频吗?', '提示', {
        type: 'warning'
    }).then(() => {
        this.listLoading = true;
        this.$http.delete("/media/mediaFile/" + row.id).then(result => {
            let { success, message } = result.data;
            if (success) {
                this.$message({ message: "删除成功", type: 'success' });
                this.getTableData();
            } else {
                this.$message({ message: "删除失败[" + message + "]", type: 'error' });
            }
            this.listLoading = false;
        });
    });
}
```

#### 2.3 addVideo 方法缺失
**模板位置**: 第90行
```html
<el-button type="primary" @click.native="addVideo" icon="el-icon-check">提交</el-button>
```

**应实现功能**:
- 验证表单
- 调用保存接口（或通过 PartUpload 组件处理）
- 关闭对话框
- 刷新列表

**参考实现**:
```javascript
addVideo: function() {
    // 如果 PartUpload 组件处理了上传，这里只需要关闭对话框
    // 否则需要在这里处理保存逻辑
    this.addVideoFormVisible = false;
    this.getTableData();
}
```

---

### 3. 后端接口缺失 - update2Free
**后端文件**: `ymcc-service/ymcc-service-media/src/main/java/cn/lzs/ymcc/web/controller/MediaFileController.java`

**问题描述**:
前端调用 `/media/mediaFile/update2Free/{id}` 接口设置试看，但后端没有实现

**前端调用** (CourseVideo.vue 第208行):
```javascript
this.$http.post("/media/mediaFile/update2Free/"+row.id).then(result=>{
```

**后端需要添加的接口**:
```java
/**
 * 设置/取消视频试看
 * @param id 视频ID
 * @return
 */
@PostMapping("/update2Free/{id}")
public JSONResult update2Free(@PathVariable("id") Long id){
    MediaFile mediaFile = mediaFileService.selectById(id);
    if(mediaFile != null){
        // 切换 free 状态
        mediaFile.setFree(!mediaFile.getFree());
        mediaFileService.updateById(mediaFile);
    }
    return JSONResult.success();
}
```

**或者使用通用 save 接口**:
如果不需要单独接口，前端可以修改为调用 save 接口：
```javascript
handleFree(row){
    // 切换 free 状态
    row.free = !row.free;
    this.$http.post("/media/mediaFile/save", row).then(result=>{
        let {success,message} = result.data;
        if(success){
            this.$message({ message: '设置成功', type: 'success' });
            this.getTableData();
        }else{
            this.$message({ message: message, type: 'error' });
        }
    });
}
```

**推荐方案**: 前端修改为调用 save 接口，避免后端改动。

---

### 4. 分页查询参数缺失 rows
**文件路径**: `src/views/itsource/CourseVideo.vue`

#### 4.1 视频列表分页查询
**代码位置**: 第252-256行
```javascript
getTableData() {
    let para = {
        page: this.page,
        keyword: this.filters.keyword
        // 缺少 rows
    };
```

**修复方案**:
```javascript
getTableData() {
    let para = {
        page: this.page,
        rows: 10,  // 与 el-pagination 的 :page-size="10" 一致
        keyword: this.filters.keyword
    };
```

#### 4.2 课程列表分页查询
**代码位置**: 第172-175行
```javascript
getCourses(){
    this.$http.post("/course/course/pagelist",this.courseAddForm).then(result=>{
        this.courses = result.data.data.rows;
    });
}
```

**问题**: `courseAddForm` 只有 `keyword`，缺少 `page` 和 `rows`

**修复方案**:
```javascript
getCourses(){
    let para = {
        page: 1,
        rows: 10,
        keyword: this.courseAddForm.keyword
    };
    this.$http.post("/course/course/pagelist", para).then(result=>{
        this.courses = result.data.data.rows;
    });
}
```

---

### 5. 批量删除功能未实现
**文件路径**: `src/views/itsource/CourseVideo.vue` (第267-269行)

**当前代码**:
```javascript
batchRemove(){
    this.$message({ message: "功能未开放", type: 'error' });
}
```

**应实现功能**:
- 获取选中的视频ID列表
- 循环调用删除接口或调用批量删除接口
- 刷新列表

**参考实现**:
```javascript
batchRemove: function() {
    var ids = this.sels.map(item => item.id);
    if (ids.length === 0) {
        this.$message({ message: '请选择要删除的记录', type: 'warning' });
        return;
    }
    this.$confirm('确认删除选中记录吗？', '提示', {
        type: 'warning'
    }).then(() => {
        this.listLoading = true;
        let deletePromises = ids.map(id => this.$http.delete("/media/mediaFile/" + id));
        Promise.all(deletePromises).then(results => {
            let allSuccess = results.every(result => result.data.success);
            if (allSuccess) {
                this.$message({ message: "批量删除成功", type: 'success' });
                this.getTableData();
            } else {
                this.$message({ message: "部分删除失败", type: 'error' });
            }
            this.listLoading = false;
        });
    });
}
```

---

### 6. 选择课程弹窗分页问题
**文件路径**: `src/views/itsource/CourseVideo.vue` (第115-116行)

**问题描述**: 选择课程弹窗中的分页组件 `:total="0"` 是固定的，不会根据实际数据更新

**当前代码**:
```html
<el-pagination layout="prev, pager, next"  :page-size="10" :total="0" style="float:right;">
</el-pagination>
```

**修复方案**:
需要添加一个变量来存储课程总数，并在获取课程列表时更新：

```javascript
data() {
    return {
        // ... 其他数据
        courseTotal: 0,  // 添加课程总数
    }
},

methods: {
    getCourses(){
        let para = {
            page: 1,
            rows: 10,
            keyword: this.courseAddForm.keyword
        };
        this.$http.post("/course/course/pagelist", para).then(result=>{
            this.courses = result.data.data.rows;
            this.courseTotal = result.data.data.total;  // 更新总数
        });
    }
}
```

```html
<el-pagination layout="prev, pager, next"  :page-size="10" :total="courseTotal" style="float:right;">
</el-pagination>
```

---

## 修复优先级

### 🔴 高优先级（功能完全不可用）
1. **PartUpload 组件缺失** - 新增视频功能完全不可用
2. **handleDel 方法缺失** - 删除功能不可用
3. **update2Free 接口缺失** - 试看功能不可用

### 🟡 中优先级（功能不完整）
4. **handleEdit 方法缺失** - 编辑功能不可用
5. **addVideo 方法缺失** - 提交按钮无响应
6. **分页参数缺失 rows** - 可能导致分页异常
7. **批量删除未实现** - 批量操作不可用

### 🟢 低优先级（优化）
8. **选择课程弹窗分页** - 分页显示不正确

---

## 修复完成记录

### 2024-XX-XX 修复完成

| 序号 | 问题 | 修复内容 | 状态 |
|-----|------|---------|------|
| 1 | 分页参数缺失 rows | getTableData() 添加 `rows: 10` | ✅ 已修复 |
| 2 | 课程分页参数缺失 | getCourses() 添加 `page`, `rows` 参数 | ✅ 已修复 |
| 3 | 课程弹窗分页问题 | 添加 `courseTotal` 数据并更新分页 | ✅ 已修复 |
| 4 | handleEdit 方法缺失 | 添加编辑方法，支持回填数据和保存 | ✅ 已修复 |
| 5 | handleDel 方法缺失 | 添加删除方法，调用 DELETE 接口 | ✅ 已修复 |
| 6 | addVideo 方法缺失 | 添加新增提交方法，调用 save 接口 | ✅ 已修复 |
| 7 | update2Free 接口缺失 | 修改 handleFree 调用 save 接口切换状态 | ✅ 已修复 |
| 8 | 批量删除未实现 | 实现 batchRemove 方法，支持批量删除 | ✅ 已修复 |
| 9 | PartUpload 组件缺失 | 创建 PartUpload.vue 组件，实现分片上传功能 | ✅ 已修复 |

**PartUpload 组件功能说明**:
- 文件选择和 MD5 计算（使用内嵌 MD5 实现，无需额外依赖）
- 文件注册（调用 `/media/mediaFile/register`）
- 分块检查（调用 `/media/mediaFile/checkchunk`）
- 分块上传（调用 `/media/mediaFile/uploadchunk`）
- 分块合并（调用 `/media/mediaFile/mergechunks`）
- 上传进度显示（整体进度 + 当前分块进度）
- 支持取消上传

---

## 修复验证清单

修复完成后，请按以下顺序验证：

- [ ] 1. 页面能正常加载视频列表
- [ ] 2. 分页功能正常（切换页码、每页显示数量正确）
- [ ] 3. 查询功能正常（根据关键字搜索）
- [ ] 4. 点击"新增视频"按钮能打开对话框
- [ ] 5. 点击"选择课程"按钮能弹出课程选择弹窗
- [ ] 6. 选择课程后能加载对应章节列表
- [ ] 7. 视频分片上传功能正常
- [ ] 8. 上传完成后能保存视频信息
- [ ] 9. 点击"编辑"按钮能打开编辑对话框并回填数据
- [ ] 10. 编辑后能保存修改
- [ ] 11. 点击"删除"按钮能删除视频
- [ ] 12. 点击"设为试看/取消试看"按钮能切换试看状态
- [ ] 13. 批量删除功能正常

---

## 附录：MediaFile 实体类字段说明

**文件路径**: `ymcc-pojo/ymcc-pojo-media/src/main/java/cn/lzs/ymcc/domain/MediaFile.java`

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | Long | 主键ID |
| fileId | String | 文件唯一标识（MD5） |
| fileName | String | 文件名 |
| fileOriginalName | String | 源文件名 |
| fileUrl | String | 文件云存储地址（m3u8格式） |
| filePath | String | 本地临时存储路径 |
| fileType | String | 文件类型 |
| fileStatus | Integer | 1未处理 2处理成功 3处理失败 4无需处理 |
| fileSize | Long | 文件大小 |
| mimeType | String | MIME类型 |
| uploadTime | Date | 上传时间 |
| chapterId | Long | 所属章节ID |
| courseId | Long | 所属课程ID |
| number | Integer | 视频序号 |
| name | String | 视频名称 |
| courseName | String | 课程名称 |
| chapterName | String | 章节名称 |
| timeMinute | Integer | 视频时长（分钟） |
| **free** | **Boolean** | **是否免费试看** |

---

## 附录：前端组件依赖

CourseVideo.vue 依赖以下自定义组件：

| 组件名 | 用途 | 状态 |
|-------|------|------|
| PartUpload | 视频分片上传 | ⚠️ 被注释，需要恢复或重新实现 |

如果 PartUpload 组件不存在，需要实现以下功能：
1. 文件选择和 MD5 计算
2. 文件注册（调用 `/media/mediaFile/register`）
3. 分块检查（调用 `/media/mediaFile/checkchunk`）
4. 分块上传（调用 `/media/mediaFile/uploadchunk`）
5. 分块合并（调用 `/media/mediaFile/mergechunks`）
6. 上传进度显示
7. 
