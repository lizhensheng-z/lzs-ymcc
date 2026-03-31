<template>
	<div id="app"
		 style="">
		<transition name="fade"
		            mode="out-in">
			<router-view></router-view>
		</transition>
	</div>
</template>

<script>
export default {
	name: 'app',
	components: {
	}
}

</script>

<style lang="scss">
body {
	margin: 0px;
	padding: 0px;
	font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
	font-size: 14px;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
	background-color: #f0f2f5;
}
#app {
	position: absolute;
	top: 0px;
	bottom: 0px;
	width: 100%;
}

.el-submenu [class^=fa] {
	vertical-align: baseline;
	margin-right: 10px;
}

.el-menu-item [class^=fa] {
	vertical-align: baseline;
	margin-right: 10px;
}

// 工具栏样式优化
.toolbar {
	border-radius: 8px;
	background: #fff;
	padding: 16px 20px;
	margin: 0 0 16px 0;
	box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
	border: 1px solid #ebeef5;

	.el-form-item {
		margin-bottom: 0;
	}

	.el-input__inner {
		border-radius: 6px;
		transition: all 0.3s;

		&:focus {
			border-color: #667eea;
			box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
		}
	}

	.el-button {
		border-radius: 6px;
		font-weight: 500;
		transition: all 0.3s;

		&:hover {
			transform: translateY(-1px);
		}

		&:active {
			transform: translateY(0);
		}
	}
}

// 表格样式优化
.el-table {
	border-radius: 8px;
	overflow: hidden;
	box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);

	&::before {
		display: none;
	}

	.el-table__header-wrapper {
		.el-table__header {
			th {
				background: #f5f7fa !important;
				color: #004adf30 !important;
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

	.el-table__empty-block {
		background: #fff;
	}
}

// 分页样式优化
.el-pagination {
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

// 弹窗样式优化
.el-dialog {
	border-radius: 12px;
	overflow: hidden;
	box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);

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
		max-height: 60vh;
		overflow-y: auto;

		&::-webkit-scrollbar {
			width: 6px;
		}

		&::-webkit-scrollbar-track {
			background: #f1f1f1;
			border-radius: 3px;
		}

		&::-webkit-scrollbar-thumb {
			background: #c1c1c1;
			border-radius: 3px;

			&:hover {
				background: #a1a1a1;
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

// 表单样式优化
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

	.el-select {
		width: 100%;
	}

	.el-cascader {
		width: 100%;
	}
}

// 按钮样式优化
.el-button {
	border-radius: 6px;
	font-weight: 500;
	transition: all 0.3s;

	&--primary {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border: none;

		&:hover,
		&:focus {
			background: linear-gradient(135deg, #5a6fd6 0%, #6a4190 100%);
			transform: translateY(-1px);
			box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
		}
	}

	&--success {
		background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
		border: none;

		&:hover,
		&:focus {
			background: linear-gradient(135deg, #49aa17 0%, #348d0c 100%);
			transform: translateY(-1px);
		}
	}

	&--warning {
		background: linear-gradient(135deg, #faad14 0%, #d48806 100%);
		border: none;

		&:hover,
		&:focus {
			background: linear-gradient(135deg, #e09c12 0%, #c07d05 100%);
			transform: translateY(-1px);
		}
	}

	&--danger {
		background: linear-gradient(135deg, #ff6b6b 0%, #ff4d4f 100%);
		border: none;

		&:hover,
		&:focus {
			background: linear-gradient(135deg, #f55a5a 0%, #f03d3f 100%);
			transform: translateY(-1px);
		}
	}

	&--info {
		background: linear-gradient(135deg, #909399 0%, #73767a 100%);
		border: none;

		&:hover,
		&:focus {
			background: linear-gradient(135deg, #82858a 0%, #65676b 100%);
			transform: translateY(-1px);
		}
	}
}

// 消息提示样式优化
.el-message {
	border-radius: 8px;
	padding: 12px 20px;
	box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
	border: none;

	&--success {
		background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%);
	}

	&--warning {
		background: linear-gradient(135deg, #fffbe6 0%, #fff1b8 100%);
	}

	&--error {
		background: linear-gradient(135deg, #fff2f0 0%, #ffccc7 100%);
	}

	&--info {
		background: linear-gradient(135deg, #f0f5ff 0%, #d6e4ff 100%);
	}
}

// 过渡动画
.fade-enter-active,
.fade-leave-active {
	transition: all .3s ease;
}

.fade-enter,
.fade-leave-active {
	opacity: 0;
}

// 下拉菜单样式优化
.el-dropdown-menu {
	border-radius: 8px;
	border: none;
	box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
	padding: 8px 0;

	.el-dropdown-menu__item {
		padding: 10px 20px;
		transition: all 0.3s;

		&:hover {
			background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
			color: #667eea;
		}
	}
}

// 选择器下拉样式优化
.el-select-dropdown {
	border-radius: 8px;
	border: none;
	box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);

	.el-select-dropdown__item {
		transition: all 0.3s;

		&.selected {
			color: #667eea;
			font-weight: 500;
		}

		&:hover {
			background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
		}
	}
}

// 复选框样式优化
.el-checkbox {
	.el-checkbox__input.is-checked .el-checkbox__inner {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border-color: #667eea;
	}

	.el-checkbox__input.is-focus .el-checkbox__inner {
		border-color: #667eea;
	}

	.el-checkbox__inner:hover {
		border-color: #667eea;
	}
}

// 单选框样式优化
.el-radio {
	.el-radio__input.is-checked .el-radio__inner {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border-color: #667eea;
	}

	.el-radio__input.is-focus .el-radio__inner {
		border-color: #667eea;
	}

	.el-radio__inner:hover {
		border-color: #667eea;
	}
}

// 标签样式优化
.el-tag {
	border-radius: 4px;
	border: none;
}

// 卡片样式优化
.el-card {
	border-radius: 12px;
	border: none;
	box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
	transition: all 0.3s;

	&:hover {
		box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
	}
}

// 上传组件样式优化
.el-upload-dragger {
	border-radius: 8px;
	transition: all 0.3s;

	&:hover {
		border-color: #667eea;
	}
}

// 日期选择器样式优化
.el-date-editor {
	.el-input__inner {
		border-radius: 6px;
	}

	&.is-active .el-input__inner {
		border-color: #667eea;
	}
}

// 级联选择器样式优化
.el-cascader {
	.el-input__inner {
		border-radius: 6px;
	}

	&.is-focus .el-input__inner {
		border-color: #667eea;
	}
}
</style>