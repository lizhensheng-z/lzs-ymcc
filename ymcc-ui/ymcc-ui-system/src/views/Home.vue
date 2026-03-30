<template>
	<el-row class="container">
		<el-col :span="24" class="header">
			<el-col :span="10" class="logo" :class="collapsed?'logo-collapse-width':'logo-width'">
				{{collapsed?'':sysName}}
			</el-col>
			<el-col :span="10">
				<div class="tools" @click.prevent="collapse">
					<i class="fa fa-align-justify"></i>
				</div>
			</el-col>
			<el-col :span="4" class="userinfo">
				<el-dropdown trigger="hover">
					<span class="el-dropdown-link userinfo-inner"><img :src="this.sysUserAvatar" /> {{sysUserName}}</span>
					<el-dropdown-menu slot="dropdown">
						<el-dropdown-item>我的消息</el-dropdown-item>
						<el-dropdown-item>设置</el-dropdown-item>
						<el-dropdown-item divided @click.native="logout">退出登录</el-dropdown-item>
					</el-dropdown-menu>
				</el-dropdown>
			</el-col>
		</el-col>
		<el-col :span="24" class="main">
			<aside :class="collapsed?'menu-collapsed':'menu-expanded'">
				<!--导航菜单-->
				<el-menu :default-active="$route.path" class="el-menu-vertical-demo" @open="handleopen" @close="handleclose" @select="handleselect"
					 unique-opened router v-show="!collapsed">
					<template v-for="(item,index) in $router.options.routes" v-if="!item.hidden">
						<el-submenu :index="index+''" v-if="!item.leaf">
							<template slot="title"><i :class="item.iconCls"></i>{{item.name}}</template>
							<el-menu-item v-for="child in item.children" :index="child.path" :key="child.path" v-if="!child.hidden">{{child.name}}</el-menu-item>
						</el-submenu>
						<el-menu-item v-if="item.leaf&&item.children.length>0" :index="item.children[0].path"><i :class="item.iconCls"></i>{{item.children[0].name}}</el-menu-item>
					</template>
				</el-menu>
				<!--导航菜单-折叠后-->
				<ul class="el-menu el-menu-vertical-demo collapsed" v-show="collapsed" ref="menuCollapsed">
					<li v-for="(item,index) in $router.options.routes" v-if="!item.hidden" class="el-submenu item">
						<template v-if="!item.leaf">
							<div class="el-submenu__title" style="padding-left: 20px;" @mouseover="showMenu(index,true)" @mouseout="showMenu(index,false)"><i :class="item.iconCls"></i></div>
							<ul class="el-menu submenu" :class="'submenu-hook-'+index" @mouseover="showMenu(index,true)" @mouseout="showMenu(index,false)"> 
								<li v-for="child in item.children" v-if="!child.hidden" :key="child.path" class="el-menu-item" style="padding-left: 40px;" :class="$route.path==child.path?'is-active':''" @click="$router.push(child.path)">{{child.name}}</li>
							</ul>
						</template>
						<template v-else>
							<li class="el-submenu">
								<div class="el-submenu__title el-menu-item" style="padding-left: 20px;height: 56px;line-height: 56px;padding: 0 20px;" :class="$route.path==item.children[0].path?'is-active':''" @click="$router.push(item.children[0].path)"><i :class="item.iconCls"></i></div>
							</li>
						</template>
					</li>
				</ul>
			</aside>
			<section class="content-container">
				<div class="grid-content bg-purple-light" >
					<el-col :span="24" class="breadcrumb-container" style="margin-bottom: 10px">
						<strong class="title" style="font-size: 16px">{{$route.name}}</strong>
						<el-breadcrumb separator="/" class="breadcrumb-inner">
							<el-breadcrumb-item v-for="item in $route.matched" :key="item.path">
								{{ item.name }}
							</el-breadcrumb-item>
						</el-breadcrumb>
					</el-col>
					<el-col :span="24" class="content-wrapper">
						<transition name="fade" mode="out-in">
							<router-view></router-view>
						</transition>
					</el-col>
				</div>
			</section>
		</el-col>
	</el-row>
</template>

<script>
	export default {
		data() {
			return {
				sysName:'IT学习云平台管理',
				collapsed:false,
				sysUserName: '',
				sysUserAvatar: 'https://img2.baidu.com/it/u=3309676100,432266439&fm=26&fmt=auto&gp=0.jpg',
				form: {
					name: '',
					region: '',
					date1: '',
					date2: '',
					delivery: false,
					type: [],
					resource: '',
					desc: ''
				}
			}
		},
		methods: {
			onSubmit() {
				console.log('submit!');
			},
			handleopen() {
				//console.log('handleopen');
			},
			handleclose() {
				//console.log('handleclose');
			},
			handleselect: function (a, b) {
			},
			//退出登录
			logout: function () {
				var _this = this;
				this.$confirm('确认退出吗?', '提示', {}).then(() => {
					localStorage.removeItem('user');
					localStorage.removeItem('U-TOKEN');
					localStorage.removeItem('R-TOKEN');
					localStorage.removeItem('expiresTime');
					_this.$router.push('/login');
				});

			},
			//折叠导航栏
			collapse:function(){
				this.collapsed=!this.collapsed;
			},
			showMenu(i,status){
				this.$refs.menuCollapsed.getElementsByClassName('submenu-hook-'+i)[0].style.display=status?'block':'none';
			}
		},
		mounted() {
			var user = localStorage.getItem('user');
			if (user) {
				user = JSON.parse(user);
				this.sysUserName = user.username || '';
				this.sysUserAvatar = user.avatar || 'https://img2.baidu.com/it/u=3309676100,432266439&fm=26&fmt=auto&gp=0.jpg';
			}

		}
	}

</script>

<style scoped lang="scss">
	@import '~scss_vars';

	// 动画关键帧
	@keyframes slideIn {
		from {
			opacity: 0;
			transform: translateX(-20px);
		}
		to {
			opacity: 1;
			transform: translateX(0);
		}
	}

	@keyframes fadeIn {
		from {
			opacity: 0;
		}
		to {
			opacity: 1;
		}
	}

	.container {
		position: absolute;
		top: 0px;
		bottom: 0px;
		width: 100%;
		background-color: #f0f2f5;

		// 头部样式优化
		.header {
			height: 60px;
			line-height: 60px;
			background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
			color: #fff;
			box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
			position: relative;
			z-index: 100;

			.userinfo {
				text-align: right;
				padding-right: 35px;
				float: right;

				.userinfo-inner {
					cursor: pointer;
					color: #fff;
					display: flex;
					align-items: center;
					justify-content: flex-end;
					transition: all 0.3s;

					&:hover {
						opacity: 0.85;
					}

					img {
						width: 36px;
						height: 36px;
						border-radius: 50%;
						margin-left: 10px;
						border: 2px solid rgba(255, 255, 255, 0.3);
						transition: all 0.3s;

						&:hover {
							border-color: rgba(255, 255, 255, 0.6);
							transform: scale(1.05);
						}
					}
				}
			}

			.logo {
				height: 60px;
				font-size: 20px;
				font-weight: 600;
				padding-left: 20px;
				padding-right: 20px;
				border-color: rgba(255, 255, 255, 0.1);
				border-right-width: 1px;
				border-right-style: solid;
				display: flex;
				align-items: center;
				letter-spacing: 1px;

				img {
					width: 40px;
					float: left;
					margin: 10px 10px 10px 18px;
				}

				.txt {
					color: #fff;
				}
			}

			.logo-width {
				width: 230px;
			}

			.logo-collapse-width {
				width: 60px;
			}

			.tools {
				padding: 0px 23px;
				width: 14px;
				height: 60px;
				line-height: 60px;
				cursor: pointer;
				transition: all 0.3s;

				&:hover {
					background: rgba(255, 255, 255, 0.1);
				}

				i {
					transition: transform 0.3s;
				}

				&:hover i {
					transform: scale(1.1);
				}
			}
		}

		// 主体区域样式优化
		.main {
			display: flex;
			position: absolute;
			top: 60px;
			bottom: 0px;
			overflow: hidden;

			// 侧边栏样式优化
			aside {
				flex: 0 0 230px;
				width: 230px;
				background: #fff;
				box-shadow: 2px 0 8px rgba(0, 0, 0, 0.06);

				.el-menu {
					height: 100%;
					border-right: none;
					background: #fff;

					.el-submenu {
						.el-submenu__title {
							height: 50px;
							line-height: 50px;
							transition: all 0.3s;

							&:hover {
								background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
								color: #667eea;
							}
						}

						&.is-active {
							.el-submenu__title {
								color: #667eea;
								font-weight: 500;
							}
						}
					}

					.el-menu-item {
						height: 46px;
						line-height: 46px;
						padding-left: 50px !important;
						transition: all 0.3s;

						&:hover {
							background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
							color: #667eea;
						}

						&.is-active {
							background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
							color: #667eea;
							font-weight: 500;
							border-right: 3px solid #667eea;
						}
					}
				}

				.collapsed {
					width: 60px;

					.item {
						position: relative;
					}

					.submenu {
						position: absolute;
						top: 0px;
						left: 60px;
						z-index: 99999;
						height: auto;
						display: none;
						background: #fff;
						border-radius: 0 8px 8px 0;
						box-shadow: 4px 4px 12px rgba(0, 0, 0, 0.1);
						min-width: 150px;
						padding: 8px 0;

						.el-menu-item {
							padding-left: 20px !important;
							height: 40px;
							line-height: 40px;

							&:hover {
								background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
							}

							&.is-active {
								background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
								color: #667eea;
							}
						}
					}
				}
			}

			.menu-collapsed {
				flex: 0 0 60px;
				width: 60px;
			}

			.menu-expanded {
				flex: 0 0 230px;
				width: 230px;
			}

			// 内容区域样式优化
			.content-container {
				flex: 1;
				overflow-y: scroll;
				padding: 20px;
				background-color: #f0f2f5;

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

				.breadcrumb-container {
					margin-bottom: 16px;
					padding: 16px 20px;
					background: #fff;
					border-radius: 8px;
					box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
					animation: fadeIn 0.3s ease;

					.title {
						width: 200px;
						float: left;
						color: #303133;
						font-size: 16px;
						font-weight: 600;
					}

					.breadcrumb-inner {
						float: right;
						line-height: 22px;

						::v-deep .el-breadcrumb__item {
							.el-breadcrumb__inner {
								color: #909399;
								font-weight: 400;

								&:hover {
									color: #667eea;
								}
							}

							&:last-child .el-breadcrumb__inner {
								color: #667eea;
								font-weight: 500;
							}
						}
					}
				}

				.content-wrapper {
					background-color: #fff;
					box-sizing: border-box;
					border-radius: 8px;
					padding: 20px;
					box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
					animation: slideIn 0.3s ease;
					min-height: calc(100vh - 180px);
				}
			}
		}
	}

	// 响应式设计
	@media (max-width: 768px) {
		.container {
			.header {
				.logo-width {
					width: 60px;
					font-size: 14px;
				}

				.userinfo {
					padding-right: 15px;

					.userinfo-inner {
						font-size: 12px;
					}
				}
			}

			.main {
				aside {
					position: absolute;
					z-index: 99;
					left: -230px;
					transition: left 0.3s;

					&.menu-expanded {
						left: 0;
					}
				}

				.content-container {
					padding: 10px;

					.breadcrumb-container {
						padding: 12px 15px;

						.title {
							font-size: 14px;
						}
					}

					.content-wrapper {
						padding: 15px;
					}
				}
			}
		}
	}
</style>