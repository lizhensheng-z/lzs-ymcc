<template>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true" :model="filters">
				<el-form-item>
					<el-input v-model="filters.keyword" placeholder="关键字" size="small"></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" v-on:click="getTableData" icon="el-icon-search" size="small">查询</el-button>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" icon="el-icon-plus" size="small" v-on:click="add">新增</el-button>
				</el-form-item>
			</el-form>
		</el-col>

		<!--列表-->
		<el-table :data="tableData" highlight-current-row v-loading="listLoading" @selection-change="selsChange" style="width: 100%;">
			<el-table-column type="selection" width="55">
			</el-table-column>
			<el-table-column type="index" width="60">
			</el-table-column>
			<el-table-column label="头像">
				<template scope="scope">
					<el-image style="width: 40px; height: 40px" :src="scope.row.headImg" fit="cover">
					</el-image>
				</template>
			</el-table-column>
			<el-table-column prop="name" label="名称" sortable>
			</el-table-column>
			<el-table-column prop="position" label="职位" sortable>
			</el-table-column>
			<el-table-column prop="technology" label="技术栈" sortable>
			</el-table-column>
			<el-table-column prop="tags" label="标签" sortable>
			</el-table-column>
			<el-table-column prop="intro" label="介绍" width="200">
				<template slot-scope="scope">
					<el-popover trigger="hover" placement="top">
						<div style="width: 400px;">{{ scope.row.intro }}</div>
						<div slot="reference" class="name-wrapper">
							<el-tag>{{ scope.row.intro }}</el-tag>
						</div>
					</el-popover>
				</template>
			</el-table-column>
			<el-table-column label="操作" width="200">
				<template scope="scope">
					<el-button size="small" @click="edit(scope.row)" icon="el-icon-edit" type="primary">编辑</el-button>
					<el-button type="danger" size="small" @click="del(scope.row)" icon="el-icon-delete">删除</el-button>
				</template>
			</el-table-column>
		</el-table>

		<!--工具条-->
		<el-col :span="24" class="toolbar">
			<el-button type="danger" @click="batchRemove" :disabled="this.sels.length===0" icon="el-icon-delete">批量删除</el-button>
			<el-pagination layout="prev, pager, next" @current-change="selectPage" :page-size="20" :total="total" style="float:right;">
			</el-pagination>
		</el-col>


		<!--新增/编辑界面-->
		<el-dialog :title="dialogTitle" :visible.sync="addFormVisible" :close-on-click-modal="false">
			<el-form :model="addForm" label-width="80px" :rules="addFormRules" ref="addForm">
				<el-form-item label="名称" prop="name">
					<el-input v-model="addForm.name" auto-complete="off" placeholder="请输入名称"></el-input>
				</el-form-item>
				<el-form-item label="职位" prop="position">
					<el-input v-model="addForm.position" auto-complete="off" placeholder="请输入职位"></el-input>
				</el-form-item>
				<el-form-item label="技术栈" prop="technology">
					<el-input v-model="addForm.technology" auto-complete="off" placeholder="请输入技术栈"></el-input>
				</el-form-item>
				<el-form-item label="标签" prop="tags">
					<el-input v-model="addForm.tags" auto-complete="off" placeholder="请输入标签，用逗号分隔"></el-input>
				</el-form-item>
				<el-form-item label="头像" prop="headImg">
					<el-input v-model="addForm.headImg" auto-complete="off" placeholder="请输入头像URL"></el-input>
				</el-form-item>
				<el-form-item label="介绍" prop="intro">
					<el-input v-model="addForm.intro" type="textarea" :rows="3" auto-complete="off" placeholder="请输入介绍"></el-input>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button @click.native="addFormVisible = false" icon="el-icon-close">取消</el-button>
				<el-button type="primary" @click.native="addSubmit" :loading="addLoading" icon="el-icon-check">提交</el-button>
			</div>
		</el-dialog>
	</section>
</template>

<script>
	export default {
		data() {
			return {
				// 表格相关
				filters: {
					keyword: ''
				},
				tableData: [],
				total: 0,
				page: 1,
				listLoading: false,
				sels: [],

				// 添加/编辑相关
				addFormVisible: false,
				addLoading: false,
				dialogTitle: '新增',
				addFormRules: {
					name: [
						{ required: true, message: '请输入名称', trigger: 'blur' }
					],
					position: [
						{ required: true, message: '请输入职位', trigger: 'blur' }
					]
				},
				// 新增/编辑界面数据
				addForm: {
					id: null,
					name: '',
					intro: '',
					technology: '',
					position: '',
					headImg: '',
					tags: ''
				}
			}
		},
		methods: {
			// 表格相关
			selsChange: function(sels) {
				this.sels = sels;
			},
			selectPage(val) {
				this.page = val;
				this.getTableData();
			},
			// 获取讲师列表（分页）
			getTableData() {
				this.listLoading = true;
				let para = {
					page: this.page,
					rows: 20,
					keyword: this.filters.keyword
				};
				this.$http.post("/course/teacher/pagelist", para).then(result => {
					let {
						success,
						data,
						message,
						code
					} = result.data;
					if (success) {
						this.total = data.total;
						this.tableData = data.rows;
					} else {
						this.$message({
							message: "数据加载失败[" + message + "]",
							type: 'error'
						});
					}
					this.listLoading = false;
				}).catch(error => {
					this.listLoading = false;
					this.$message({
						message: "数据加载失败[" + error.message + "]",
						type: 'error'
					});
				})
			},

			// 删除
			del: function(row) {
				this.$confirm('确认删除该记录吗?', '提示', {
					type: 'warning'
				}).then(() => {
					this.listLoading = true;
					this.$http.delete("/course/teacher/" + row.id).then(result => {
						let {
							success,
							data,
							message,
							code
						} = result.data;
						if (success) {
							this.$message({
								message: "删除成功",
								type: 'success'
							});
							this.getTableData();
						} else {
							this.$message({
								message: "删除失败[" + message + "]",
								type: 'error'
							});
						}
						this.listLoading = false;
					}).catch(error => {
						this.listLoading = false;
						this.$message({
							message: "删除失败[" + error.message + "]",
							type: 'error'
						});
					})
				});
			},
			// 显示编辑界面
			edit: function(row) {
				this.dialogTitle = '编辑';
				this.addForm = Object.assign({}, row);
				this.addFormVisible = true;
			},
			// 显示新增界面
			add: function() {
				this.dialogTitle = '新增';
				this.addFormVisible = true;
				this.addForm = {
					id: null,
					name: '',
					intro: '',
					technology: '',
					position: '',
					headImg: '',
					tags: ''
				};
			},
			// 新增/编辑提交
			addSubmit: function() {
				this.$refs.addForm.validate((valid) => {
					if (valid) {
						this.$confirm('确认提交吗？', '提示', {}).then(() => {
							this.addLoading = true;
							this.$http.post("/course/teacher/save", this.addForm).then(result => {
								let {
									success,
									data,
									message,
									code
								} = result.data;
								if (success) {
									this.$message({
										message: "提交成功",
										type: 'success'
									});
									this.addFormVisible = false;
									this.getTableData();
								} else {
									this.$message({
										message: "提交失败[" + message + "]",
										type: 'error'
									});
								}
								this.addLoading = false;
							}).catch(error => {
								this.addLoading = false;
								this.$message({
									message: "提交失败[" + error.message + "]",
									type: 'error'
								});
							})
						});
					}
				});
			},

			// 批量删除
			batchRemove: function() {
				var ids = this.sels.map(item => item.id);
				if (ids.length === 0) {
					this.$message({
						message: '请选择要删除的记录',
						type: 'warning'
					});
					return;
				}
				this.$confirm('确认删除选中记录吗？', '提示', {
					type: 'warning'
				}).then(() => {
					this.listLoading = true;
					let deletePromises = ids.map(id => this.$http.delete("/course/teacher/" + id));
					Promise.all(deletePromises).then(results => {
						let allSuccess = results.every(result => result.data.success);
						if (allSuccess) {
							this.$message({
								message: "批量删除成功",
								type: 'success'
							});
							this.getTableData();
						} else {
							this.$message({
								message: "部分删除失败",
								type: 'error'
							});
							this.getTableData();
						}
						this.listLoading = false;
					}).catch(error => {
						this.listLoading = false;
						this.$message({
							message: "批量删除失败[" + error.message + "]",
							type: 'error'
						});
					})
				})
			}
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

	.el-image {
		border-radius: 50%;
		overflow: hidden;
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

// 弹窗表单样式
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

			.el-input__inner,
			.el-textarea__inner {
				border-radius: 6px;
				transition: all 0.3s;

				&:focus {
					border-color: #667eea;
					box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
				}
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

// 标签样式
::v-deep .el-tag {
	border-radius: 4px;
	border: none;
	background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
	color: #667eea;
}
</style>