<template>
	<section class="handle-callback">
		<el-card class="box-card">
			<div slot="header">
				<span>回调处理</span>
			</div>
			<div class="callback-content">
				<el-alert
					title="回调处理页面"
					type="info"
					description="此页面用于处理第三方回调（如支付回调、登录回调等）"
					show-icon
					:closable="false">
				</el-alert>
				<div class="callback-params" v-if="hasParams">
					<h4>回调参数：</h4>
					<pre>{{ callbackParams }}</pre>
				</div>
				<div class="no-params" v-else>
					<el-empty description="暂无回调参数"></el-empty>
				</div>
			</div>
		</el-card>
	</section>
</template>

<script>
	export default {
		data() {
			return {
				callbackParams: {},
				hasParams: false
			}
		},
		mounted() {
			this.handleCallback();
		},
		methods: {
			handleCallback() {
				// 获取 URL 查询参数
				const query = this.$route.query;
				if (Object.keys(query).length > 0) {
					this.callbackParams = query;
					this.hasParams = true;
					console.log('收到回调参数:', query);
				}
			}
		}
	}
</script>

<style scoped>
.handle-callback {
	padding: 20px;
}
.callback-content {
	margin-top: 20px;
}
.callback-params {
	margin-top: 20px;
}
.callback-params pre {
	background-color: #f5f7fa;
	padding: 15px;
	border-radius: 4px;
	overflow-x: auto;
}
.no-params {
	margin-top: 40px;
}
</style>

<style scoped>

</style>