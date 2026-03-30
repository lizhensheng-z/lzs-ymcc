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
			<el-table-column prop="appId" label="应用ID" sortable>
			</el-table-column>
			<el-table-column prop="protocol" label="协议" sortable>
			</el-table-column>
			<el-table-column prop="gatewayHost" label="网关" sortable>
			</el-table-column>
			<el-table-column prop="signType" label="签名类型" sortable>
			</el-table-column>
			<el-table-column prop="notifyUrl" label="异步通知地址" width="200" sortable>
			</el-table-column>
			<el-table-column prop="returnUrl" label="同步跳转地址" width="200" sortable>
			</el-table-column>
			<el-table-column prop="createTime" label="创建时间" width="160" sortable>
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
			<el-form :model="addForm" label-width="120px" :rules="addFormRules" ref="addForm">
				<el-form-item label="应用ID" prop="appId">
					<el-input v-model="addForm.appId" auto-complete="off" placeholder="请输入应用ID"></el-input>
				</el-form-item>
				<el-form-item label="商户私钥" prop="merchantPrivateKey">
					<el-input v-model="addForm.merchantPrivateKey" type="textarea" :rows="3" auto-complete="off" placeholder="请输入商户私钥"></el-input>
				</el-form-item>
				<el-form-item label="支付宝公钥" prop="alipayPublicKey">
					<el-input v-model="addForm.alipayPublicKey" type="textarea" :rows="3" auto-complete="off" placeholder="请输入支付宝公钥"></el-input>
				</el-form-item>
				<el-form-item label="协议" prop="protocol">
					<el-input v-model="addForm.protocol" auto-complete="off" placeholder="如：https"></el-input>
				</el-form-item>
				<el-form-item label="网关" prop="gatewayHost">
					<el-input v-model="addForm.gatewayHost" auto-complete="off" placeholder="如：openapi.alipay.com"></el-input>
				</el-form-item>
				<el-form-item label="签名类型" prop="signType">
					<el-input v-model="addForm.signType" auto-complete="off" placeholder="如：RSA2"></el-input>
				</el-form-item>
				<el-form-item label="异步通知地址" prop="notifyUrl">
					<el-input v-model="addForm.notifyUrl" auto-complete="off" placeholder="异步通知地址"></el-input>
				</el-form-item>
				<el-form-item label="同步跳转地址" prop="returnUrl">
					<el-input v-model="addForm.returnUrl" auto-complete="off" placeholder="同步跳转地址"></el-input>
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
					appId: [
						{ required: true, message: '请输入应用ID', trigger: 'blur' }
					]
				},
				// 新增/编辑界面数据
				addForm: {
					id: null,
					appId: '',
					merchantPrivateKey: '',
					alipayPublicKey: '',
					protocol: '',
					gatewayHost: '',
					signType: '',
					notifyUrl: '',
					returnUrl: ''
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
			// 获取支付宝配置列表（分页）
			getTableData() {
				this.listLoading = true;
				let para = {
					page: this.page,
					rows: 20,
					keyword: this.filters.keyword
				};
				this.$http.post("/pay/alipayInfo/pagelist", para).then(result => {
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
					this.$http.delete("/pay/alipayInfo/" + row.id).then(result => {
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
					appId: '',
					merchantPrivateKey: '',
					alipayPublicKey: '',
					protocol: '',
					gatewayHost: '',
					signType: '',
					notifyUrl: '',
					returnUrl: ''
				};
			},
			// 新增/编辑提交
			addSubmit: function() {
				this.$refs.addForm.validate((valid) => {
					if (valid) {
						this.$confirm('确认提交吗？', '提示', {}).then(() => {
							this.addLoading = true;
							this.$http.post("/pay/alipayInfo/save", this.addForm).then(result => {
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
					let deletePromises = ids.map(id => this.$http.delete("/pay/alipayInfo/" + id));
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