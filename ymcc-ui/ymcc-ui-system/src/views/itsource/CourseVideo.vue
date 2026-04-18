<template>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true" :model="filters">
				<el-form-item>
					<el-input v-model="filters.keyword" placeholder="视频名称/文件名" clearable @keyup.enter.native="getTableData"></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" v-on:click="getTableData"  size="small" icon="el-icon-search">查询视频</el-button>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" size="small" @click="addVideoHandler" icon="el-icon-video-camera">新增视频</el-button>
				</el-form-item>
			</el-form>
		</el-col>

		<!--列表-->
		<el-table :data="tableData" highlight-current-row v-loading="listLoading" @selection-change="selsChange" style="width: 100%;">
			<el-table-column type="selection">
			</el-table-column>

			<el-table-column prop="courseName" label="课程" >
			</el-table-column>
			<el-table-column prop="chapterName" label="章节" >
			</el-table-column>
			<el-table-column prop="name" label="视频" >
			</el-table-column>
			<el-table-column prop="fileOriginalName" label="文件名" >
			</el-table-column>
			<el-table-column prop="fileStatus" label="状态" width="120" :formatter="formatState" sortable>
			</el-table-column>
			<el-table-column label="操作" width="300">
				<template scope="scope">
					<el-button size="small" @click="handleEdit(scope.row)" icon="el-icon-edit" type="primary">编辑</el-button>
					<el-button type="danger" size="small" @click="handleDel(scope.row)" icon="el-icon-remove">删除</el-button>

					<span v-if="scope.row.free">
						<el-button type="danger" size="small" @click="handleFree(scope.row)" icon="el-icon-s-flag">取消试看</el-button>
					</span>
					<span v-else>
						<el-button type="success" size="small" @click="handleFree(scope.row)" icon="el-icon-s-flag">设为试看</el-button>
					</span>
				</template>
			</el-table-column>
		</el-table>

		<!--工具条-->
		<el-col :span="24" class="toolbar">
			<el-button type="danger" @click="batchRemove" :disabled="this.sels.length===0"  icon="el-icon-remove" size="small">批量删除</el-button>
			<el-pagination layout="prev, pager, next" @current-change="handleCurrentChange" :page-size="10" :total="total" style="float:right;">
			</el-pagination>
		</el-col>



		<!--新增视频-->
		<el-dialog title="新增视频" :visible.sync="addVideoFormVisible"  :close-on-click-modal="false" width="800px">
			<el-form :model="addVideoForm" label-width="80px"  ref="addForm">
				<el-form-item label="所属课程" prop="courseId">
					<el-input v-model="addVideoForm.courseName" auto-complete="off"  style="width: 550px" disabled></el-input>
					<el-button type="primary" @click="dialogTableVisible = true" icon="el-icon-search" >选择课程</el-button>
				</el-form-item>

				<el-form-item label="章节名称" prop="name" >
					<el-select @change="selectCourseChapter" v-model="addVideoForm.chapterId" placeholder="请选择" style="width: 680px">
						<el-option
								v-for="item in courseChapters"
								:key="item.id"
								:label="item.name"
								:value="item.id">
						</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="视频名称" prop="name">
					<el-input v-model="addVideoForm.name" auto-complete="off"></el-input>
				</el-form-item>
				<el-form-item label="视频序号" prop="number">
					<el-input v-model="addVideoForm.number" type="number" min="1" auto-complete="off"></el-input>
				</el-form-item>

				<el-form-item label="视频上传" prop="name">
					<PartUpload v-bind="addVideoForm"
                      :video-number="addVideoForm.number"
                      @addVideoFormVisibleClose="addVideoFormVisibleClose"></PartUpload>
				</el-form-item>

			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button @click.native="addVideoFormVisible = false" icon="el-icon-remove">取消</el-button>
				<el-button type="primary" @click.native="addVideo"  icon="el-icon-check">提交</el-button>
			</div>
		</el-dialog>



		<!--选择课程弹窗-->
		<el-dialog title="选择课程" :visible.sync="dialogTableVisible"  :close-on-click-modal="false" width="800px">
			<el-form :model="courseAddForm" label-width="80px"  ref="dialogTableAddForm">
				<!--工具条-->
				<el-form-item label-width="0px" style="border-bottom: 1px solid #eeeeee;padding-bottom: 20px">
					<el-input v-model="courseAddForm.keyword" placeholder="搜索课程名" style="width: 200px" size="small"></el-input>
					<el-button type="warning" v-on:click="getCourses"  icon="el-icon-search" size="small">查询</el-button>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style="color: red">搜索课程后加载数据，双击行进行选择</span>
				</el-form-item>

				<el-form-item label-width="0px">
					<el-table :data="courses" highlight-current-row  @row-dblclick="selectCourse">
						<el-table-column property="id" label="ID"  width="100"></el-table-column>
						<el-table-column property="name" label="课程" ></el-table-column>
						<el-table-column property="gradeName" label="等级"></el-table-column>
					</el-table>
				</el-form-item>

				<el-form-item label-width="0px">
					<el-pagination layout="prev, pager, next" @current-change="handleCoursePageChange" :page-size="10" :total="courseTotal" style="float:right;">
					</el-pagination>
				</el-form-item>
			</el-form>
		</el-dialog>


	</section>
</template>

<script>

	export default {
		data() {
			return {
				filters: {
					keyword: ''
				},
				tableData: [],
				total: 0,
				page: 1,
				listLoading: false,
				sels:[],
				//添加章节视频的数据
				addVideoForm:{
					chapterId:"",
					chapterName:'',
					courseId:'',
					number:'',
					videoUrl:'',
					name:'',
					courseName:''
				},
				addVideoFormVisible:false,
				//选择课程=========================
				dialogTableVisible:false,
				courseAddForm:{
					keyword:''
				},
				courses:[],
				//选择章节===================
				courseChapters:[],
				//课程弹窗分页
				courseTotal: 0,
				coursePage: 1
			}

		},
		methods: {
			addVideoFormVisibleClose(){
				this.addVideoFormVisible = false;
			},
			addVideoHandler(chapterRow){
				// 重置表单
				this.addVideoForm = {
					chapterId:"",
					chapterName:'',
					courseId:'',
					number:'',
					videoUrl:'',
					name:'',
					courseName:''
				};
				this.courseChapters = [];
				this.addVideoFormVisible = true;
			},
			//加载课程==============================
			getCourses(){
				let para = {
					page: this.coursePage,
					rows: 10,
					keyword: this.courseAddForm.keyword || ''
				};
				this.$http.post("/course/course/pagelist",para).then(result=>{
					this.courses = result.data.data.rows;
					this.courseTotal = result.data.data.total;
				});
			},
			//课程分页切换
			handleCoursePageChange(val){
				this.coursePage = val;
				this.getCourses();
			},
			selectCourse(row){
				this.courseAddForm.keyword = "";
				this.addVideoForm.courseId = row.id;
				this.addVideoForm.courseName = row.name;
				this.dialogTableVisible = false;
				this.getCourseChapter();
			},

			//加载章节
			getCourseChapter(){
				if(this.addVideoForm.courseId){
					this.$http.get("/course/courseChapter/listByCourseId/"+this.addVideoForm.courseId).then(result=>{
						console.log('====== 加载章节列表 ======');
						console.log('课程ID:', this.addVideoForm.courseId);
						console.log('返回的章节列表:', result.data.data);
						
						// 确保所有章节的ID都转为字符串，避免精度丢失
						this.courseChapters = result.data.data.map(chapter => {
							console.log(`章节: ${chapter.name}, ID: ${chapter.id}, ID类型: ${typeof chapter.id}`);
							return {
								...chapter,
								id: String(chapter.id),  // 转为字符串
								courseId: String(chapter.courseId)
							};
						});
						
						console.log('处理后的章节列表:', this.courseChapters);
						console.log('========================');
					});
				}
			},
			//选择章节
			selectCourseChapter(courseChapterId){
				// 添加更详细的调试日志
				console.log('====== 选择章节调试信息 ======');
				console.log('传入的章节ID:', courseChapterId, '类型:', typeof courseChapterId);
				console.log('当前 addVideoForm.chapterId:', this.addVideoForm.chapterId);
				console.log('章节列表:', this.courseChapters);
				console.log('============================');
				
				if(courseChapterId){
					// el-select 的 v-model 会自动更新 addVideoForm.chapterId
					// 我们需要再验证一次是否正确
					for(let i = 0 ; i < this.courseChapters.length ; i++){
						let courseChapter = this.courseChapters[i];
						// 统一转为字符串比较，避免类型不一致问题
						if(String(courseChapter.id) === String(courseChapterId)){
							console.log('✅ 匹配成功！');
							console.log('章节名称:', courseChapter.name);
							console.log('章节ID:', courseChapter.id);
							
							this.addVideoForm.chapterName = courseChapter.name;
							// 强制更新 chapterId 确保一致性
							this.$set(this.addVideoForm, 'chapterId', String(courseChapter.id));
							
							console.log('更新后 addVideoForm.chapterId:', this.addVideoForm.chapterId);
							console.log('更新后 addVideoForm:', JSON.stringify(this.addVideoForm));
							break;
						}
					}
				}
			},
			//试看start==============================
			handleFree(row){
				// 设置免费试看：修改free字段后调用save接口
				let updateData = {...row};
				updateData.free = !row.free;
				this.$http.post("/media/mediaFile/save",updateData).then(result=>{
					let {success,message} = result.data;
					if(success){
						this.$message({
							message: '设置成功',
							type: 'success'
						});
						this.getTableData();
					}else{
						this.$message({
							message: message,
							type: 'error'
						});
					}
				}).catch(error =>{
					this.$message({
						message: '设置失败['+error+']',
						type: 'error'
					});
				});
			},
			//试看end==============================
			//性别显示转换
			formatState: function (row, column) {
				//1未处理 2处理成功 3处理失败 4无需处理
				if(row.fileStatus == 1){
					return '处理中...';
				}
				if(row.fileStatus == 2){
					return '上传成功';
				}
				if(row.fileStatus == 3){
					return '上传失败';
				}
				if(row.fileStatus == 4){
					return '无需处理';
				}
				return  '未知';
			},
			handleCurrentChange(val) {
				this.page = val;
				this.getTableData();
			},
			//获取视频列表（支持按视频名称模糊查询）
			getTableData() {
				let para = {
					page: this.page,
					rows: 10,
					name: this.filters.keyword,  // 视频名称模糊查询
					keyword: this.filters.keyword  // 兼容后端的 keyword 字段
				};
				this.listLoading = true; //显示加载圈
				this.$http.post("/media/mediaFile/pagelist",para).then(result=>{
					this.total = result.data.data.total;
					this.tableData = result.data.data.rows;
					this.listLoading = false;  //关闭加载圈
				}).catch(error => {
					this.listLoading = false;
					this.$message({
						message: '查询失败',
						type: 'error'
					});
				});
			},
			selsChange(sels){
				this.sels = sels;
			},
			//编辑视频
			handleEdit(row){
				this.addVideoForm = Object.assign({}, row);
				this.addVideoFormVisible = true;
				//加载章节列表
				this.getCourseChapter();
			},
			//删除视频
			handleDel(row){
				this.$confirm('确认删除该视频吗?', '提示', {
					type: 'warning'
				}).then(() => {
					this.listLoading = true;
					this.$http.delete("/media/mediaFile/" + row.id).then((res) => {
						this.listLoading = false;
						let ajaxResult = res.data;
						if (ajaxResult.success) {
							this.$message({
								message: '删除成功',
								type: 'success'
							});
							this.getTableData();
						} else {
							this.$message({
								message: ajaxResult.message,
								type: 'error'
							});
						}
					});
				}).catch(() => {
					this.$message({
						type: 'info',
						message: '已取消删除'
					});
				});
			},
			//新增视频提交
			addVideo(){
				this.$refs.addForm.validate((valid) => {
					if (valid) {
						this.$confirm('确认提交吗？', '提示', {}).then(() => {
							this.$http.post("/media/mediaFile/save",this.addVideoForm).then(result=>{
								let {success,message} = result.data;
								if(success){
									this.$message({
										message: '提交成功',
										type: 'success'
									});
									this.addVideoFormVisible = false;
									this.getTableData();
									//重置表单
									this.addVideoForm = {
										chapterId:"",
										chapterName:'',
										courseId:'',
										number:'',
										videoUrl:'',
										name:'',
										courseName:''
									};
								}else{
									this.$message({
										message: message,
										type: 'error'
									});
								}
							});
						});
					}
				});
			},
			//批量删除
			batchRemove(){
				let ids = this.sels.map(item => item.id).join(",");
				this.$confirm('确认删除选中记录吗？', '提示', {
					type: 'warning'
				}).then(() => {
					this.listLoading = true;
					this.$http.post("/media/mediaFile/batchRemove", { ids: ids }).then((res) => {
						this.listLoading = false;
						let ajaxResult = res.data;
						if (ajaxResult.success) {
							this.$message({
								message: '删除成功',
								type: 'success'
							});
							this.getTableData();
						} else {
							this.$message({
								message: ajaxResult.message,
								type: 'error'
							});
						}
					});
				}).catch(() => {
					this.$message({
						type: 'info',
						message: '已取消删除'
					});
				});
			},
		},
		mounted() {
			this.getTableData();
		}
	}

</script>

<style scoped lang="scss">
// 页面容器
section {
	background: transparent;
}

// 工具栏样式
.toolbar {
	background: #fff;
	border-radius: 8px;
	padding: 16px 20px;
	margin-bottom: 16px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

	.el-form-item {
		margin-bottom: 0;
	}

	.el-input {
		::v-deep .el-input__inner {
			border-radius: 6px;
			transition: all 0.3s;

			&:focus {
				border-color: #667eea;
				box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
			}
		}
	}

	.el-button {
		border-radius: 6px;
		font-weight: 500;
		transition: all 0.3s;

		&:hover {
			transform: translateY(-1px);
		}
	}
}

// 表格样式
::v-deep .el-table {
	border-radius: 8px;
	overflow: hidden;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

	&::before {
		display: none;
	}

	.el-table__header-wrapper {
		.el-table__header {
			th {
				background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
				color: #fff !important;
				font-weight: 500;
				padding: 14px 0;
				border-bottom: none !important;

				.cell {
					font-size: 14px;
				}
			}
		}
	}

	.el-table__body-wrapper {
		.el-table__row {
			transition: all 0.3s;

			&:hover {
				background-color: #f5f7fa !important;
			}

			td {
				padding: 12px 0;
				border-bottom: 1px solid #ebeef5;
			}
		}
	}
}

// 分页样式
::v-deep .el-pagination {
	margin-top: 16px;

	.btn-prev, .btn-next {
		border-radius: 6px;
	}

	.el-pager li {
		border-radius: 6px;
		font-weight: 500;
		transition: all 0.3s;

		&.active {
			background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		}

		&:hover {
			color: #667eea;
		}
	}
}

// 弹窗样式
::v-deep .el-dialog {
	.el-dialog__header {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		padding: 18px 24px;
		margin-right: 0;

		.el-dialog__title {
			color: #fff;
			font-weight: 600;
			font-size: 18px;
		}

		.el-dialog__headerbtn {
			top: 18px;
			right: 20px;

			.el-dialog__close {
				color: rgba(255, 255, 255, 0.8);
				font-size: 18px;
				transition: all 0.3s;

				&:hover {
					color: #fff;
					transform: rotate(90deg);
				}
			}
		}
	}

	.el-dialog__body {
		padding: 24px;

		.el-form-item {
			.el-form-item__label {
				font-weight: 500;
				color: #606266;
			}

			.el-input__inner {
				border-radius: 6px;
				transition: all 0.3s;

				&:focus {
					border-color: #667eea;
					box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
				}
			}

			.el-select {
				width: 100%;
			}
		}
	}

	.el-dialog__footer {
		padding: 16px 24px;
		border-top: 1px solid #ebeef5;
		background: #fafafa;

		.el-button {
			border-radius: 6px;
			padding: 10px 24px;
			font-weight: 500;
		}
	}
}
</style>