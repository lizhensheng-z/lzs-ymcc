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
			<el-table-column prop="realName" label="姓名" sortable>
			</el-table-column>
			<el-table-column prop="tel" label="电话" sortable>
			</el-table-column>
			<el-table-column prop="email" label="邮箱" sortable>
			</el-table-column>
			<el-table-column prop="state" label="状态" :formatter="formatState" sortable>
			</el-table-column>
			<el-table-column prop="type" label="员工类型" :formatter="formatType" sortable>
			</el-table-column>
			<el-table-column prop="inputTime" label="创建时间" sortable>
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
			<el-form :model="addForm" label-width="100px" :rules="addFormRules" ref="addForm">
				<el-form-item label="姓名" prop="realName">
					<el-input v-model="addForm.realName" auto-complete="off" placeholder="请输入姓名"></el-input>
				</el-form-item>
				<el-form-item label="电话" prop="tel">
					<el-input v-model="addForm.tel" auto-complete="off" placeholder="请输入电话"></el-input>
				</el-form-item>
				<el-form-item label="邮箱" prop="email">
					<el-input v-model="addForm.email" auto-complete="off" placeholder="请输入邮箱"></el-input>
				</el-form-item>
				<el-form-item label="状态" prop="state">
					<el-select v-model="addForm.state" placeholder="请选择状态">
						<el-option label="正常" :value="0"></el-option>
						<el-option label="锁定" :value="1"></el-option>
						<el-option label="注销" :value="2"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="员工类型" prop="type">
					<el-select v-model="addForm.type" placeholder="请选择员工类型">
						<el-option label="平台普通员工" :value="1"></el-option>
						<el-option label="平台客服人员" :value="2"></el-option>
						<el-option label="平台管理员" :value="3"></el-option>
						<el-option label="机构员工" :value="4"></el-option>
						<el-option label="机构管理员或其他" :value="5"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="部门ID" prop="deptId">
					<el-input v-model.number="addForm.deptId" auto-complete="off" placeholder="请输入部门ID"></el-input>
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
	import util from '../../common/js/util'
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
					realName: [
						{ required: true, message: '请输入姓名', trigger: 'blur' }
					],
					tel: [
						{ required: true, message: '请输入电话', trigger: 'blur' }
					],
					email: [
						{ required: true, message: '请输入邮箱', trigger: 'blur' },
						{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
					]
				},
				// 新增/编辑界面数据
				addForm: {
					id: null,
					realName: '',
					tel: '',
					email: '',
					state: 0,
					type: 1,
					deptId: null,
					loginId: null
				}
			}
		},
		methods: {
			// 表格相关
			selsChange: function(sels) {
				this.sels = sels;
			},
			// 状态显示转换
			formatState: function(row, column) {
				return row.state == 0 ? '正常' : row.state == 1 ? '锁定' : '注销';
			},
			// 员工类型显示转换
			formatType: function(row, column) {
				const typeMap = {
					1: '平台普通员工',
					2: '平台客服人员',
					3: '平台管理员',
					4: '机构员工',
					5: '机构管理员或其他'
				};
				return typeMap[row.type] || '未知';
			},
			selectPage(val) {
				this.page = val;
				this.getTableData();
			},
			// 获取员工列表（分页）
			getTableData() {
				this.listLoading = true;
				let para = {
					page: this.page,
					keyword: this.filters.keyword
				};
				this.$http.post("/system/employee/pagelist", para).then(result => {
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
					this.$http.delete("/system/employee/" + row.id).then(result => {
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
				// 复制数据，避免直接修改表格数据
				this.addForm = Object.assign({}, row);
				this.addFormVisible = true;
			},
			// 显示新增界面
			add: function() {
				this.dialogTitle = '新增';
				this.addFormVisible = true;
				this.addForm = {
					id: null,
					realName: '',
					tel: '',
					email: '',
					state: 0,
					type: 1,
					deptId: null,
					loginId: null
				};
			},
			// 新增/编辑提交
			addSubmit: function() {
				this.$refs.addForm.validate((valid) => {
					if (valid) {
						this.$confirm('确认提交吗？', '提示', {}).then(() => {
							this.addLoading = true;
							this.$http.post("/system/employee/save", this.addForm).then(result => {
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
					// 批量删除需要循环调用或后端支持批量删除接口
					// 这里使用 Promise.all 逐个删除
					let deletePromises = ids.map(id => this.$http.delete("/system/employee/" + id));
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

<style scoped>
</style>