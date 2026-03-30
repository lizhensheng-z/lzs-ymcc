<template>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :model="filters" :inline="true">
				<el-form-item>
					<el-input v-model="filters.keywords" size="small"  placeholder="关键字" ></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="warning" v-on:click="getCourses" size="small" icon="el-icon-search">查询课程</el-button>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="addHandler" size="small" icon="el-icon-notebook-1">新增课程</el-button>
				</el-form-item>
				<el-form-item>
					<el-button type="success" @click="onLineCourse" size="small" icon="el-icon-s-promotion">课程发布</el-button>
				</el-form-item>
				<el-form-item>
					<el-button type="danger" @click="offLineCourse" size="small" icon="el-icon-download">课程下架</el-button>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="killCourseModelView" size="small" icon="el-icon-sell">加入秒杀</el-button>
				</el-form-item>
			</el-form>
		</el-col>

		<!--列表v-loading="listLoading"-->
		<el-table @row-click="rowClick" :data="courses" v-loading="listLoading"  @selection-change="selsChange"
				  highlight-current-row  style="width: 100%;">
			<!--多选框-->
			<el-table-column type="selection" width="55e">
			</el-table-column>
			<!--其他都设置值,只有一个不设置值就自动适应了-->
			<el-table-column prop="name" label="课程名称" >
			</el-table-column>
			<el-table-column prop="chapterCount" label="章节数" >
			</el-table-column>
			<!--<el-table-column prop="courseType.name" label="类型">-->
			<!--</el-table-column>-->
			<el-table-column prop="gradeName" label="等级" >
			</el-table-column>
			<el-table-column prop="status" label="状态" :formatter="statusFormatter">
			</el-table-column>
			<el-table-column prop="forUser" label="适用人群" width="220">
			</el-table-column>
			<!--<el-table-column prop="tenantName" label="所属机构">-->
			<!--</el-table-column>-->
			<el-table-column prop="teacherNames" label="讲师" width="140">
			</el-table-column>
			<el-table-column label="操作" width="200">
				<template scope="scope">
					<el-button size="small"  @click="edit(scope.row)" icon="el-icon-edit" type="primary">编辑</el-button>
					<el-button type="danger" size="small" @click="del(scope.row)" icon="el-icon-remove">删除</el-button>
				</template>
			</el-table-column>
		</el-table>
		<!--工具条-->
		<el-col :span="24" class="toolbar">
			<el-button type="danger"  @click="batchRemove" :disabled="this.sels.length===0" icon="el-icon-remove" size="small">批量删除</el-button>
			<el-pagination layout="prev, pager, next" @current-change="handleCurrentChange"  :page-size="10" :total="total" style="float:right;">
			</el-pagination>
		</el-col>

		<!--新增界面-->
		<el-dialog title="新增" :visible.sync="addFormVisible"  :close-on-click-modal="false" width="860px">
			<el-form :inline="true" :model="addForm" label-width="80px"  ref="addForm">
				<el-form-item label="课程名称" prop="name" >
					<el-input v-model="addForm.name" placeholder="课程名称" auto-complete="off" style="width: 300px" />
				</el-form-item>
				<el-form-item label="适用人群" prop="forUser">
					<el-input v-model="addForm.forUser" placeholder="适用人群" auto-complete="off" style="width: 300px"/>
				</el-form-item>
				<el-form-item label="课程类型" prop="courseTypeId" >
					<el-cascader style="width: 300px"
							:props="courseTypeProps"
							v-model="addForm.courseTypeId"
							placeholder="课程类型"
							:options="courseTypes"
							expand-trigger="hover"
							:show-all-levels="false"
							filterable
							change-on-select
					></el-cascader>
				</el-form-item>
				<el-form-item label="添加讲师" prop="teachers">
					<el-select v-model="addForm.teacherIds" multiple placeholder="可选多个讲师" style="width: 300px">
						<el-option
								v-for="item in teachers"
								:key="item.id"
								:label="item.name"
								:value="item.id">
						</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="课程周期" >
					<el-date-picker  style="width: 200px"
									 v-model="addForm.startTime"
									 type="date"
									 value-format="yyyy-MM-dd"
									 size="small"
									 placeholder="课程开始日期">
					</el-date-picker> -
					<el-date-picker   style="width: 200px"
									  v-model="addForm.endTime"
									  type="date"
									  value-format="yyyy-MM-dd"
									  size="small"
									  placeholder="课程结束日期">
					</el-date-picker>
				</el-form-item >

				<el-form-item label="购买可看" >
					<el-input placeholder="可看天数"  type="number" v-model="addForm.validDays" auto-complete="off" style="width: 165px"/>&nbsp;天
				</el-form-item>

				<el-form-item label="课程等级" prop="courseTypeId"  style="width: 700px">
					<el-radio-group v-model="addForm.gradeId">
						<el-radio v-for="grade in grades" :label="grade.id">{{grade.name}}</el-radio>
					</el-radio-group>
				</el-form-item>

				<el-form-item prop="logo" style="width: 400px" >
					<!--<el-input type="text" v-model="employee.logo" auto-complete="off" placeholder="请输入logo！"></el-input>-->
					<el-upload
							class="upload-demo"
							action="http://localhost:10010/ymcc/common/oss/uploadFile"
              :method="'POST'"
              :data="uploadPicData"
              list-type="picture"

              :on-success="handlePicSuccess"
              :limit="1">
            <el-button size="small" type="primary"  icon="el-icon-picture-outline">上传封面</el-button>
            &nbsp;&nbsp;<span slot="tip" class="el-upload__tip">支持500kb，格式jpg</span>
          </el-upload>
        </el-form-item>

        <el-form-item prop="logo"  >
<!--          							:before-upload="beforeZipUpload"省略-->
					<el-upload
							class="upload-demo"
              action="http://localhost:10010/ymcc/common/oss/uploadFile"
							:data="uploadZipData"

							:on-success="handleZipSuccess"
							:limit="1">
						<el-button size="small" type="primary" icon="el-icon-upload">上传课件</el-button>
						&nbsp;&nbsp;<span slot="tip" class="el-upload__tip">支持压缩格式</span>
					</el-upload>
				</el-form-item>
				<el-divider></el-divider>

				<el-form-item label="收费规则" prop="gradeId" size="width:100%">
					<el-radio-group v-model="addForm.chargeId">
						<el-radio @change="changeCharge" v-for="charge in charges" :label="charge.id">{{charge.name}}</el-radio>
					</el-radio-group>
				</el-form-item>

				<el-form-item label="课程价格" prop="price">
					<el-input :disabled="priceDisabled" type="number" v-model="addForm.price" auto-complete="off" style="width: 185px"/>
				</el-form-item>
				<el-form-item label="课程原价">
					<el-input :disabled="priceDisabled" type="number" v-model="addForm.priceOld" auto-complete="off" style="width: 185px"/>
				</el-form-item>

				<el-form-item label="咨询QQ" prop="qq">
					<el-input v-model="addForm.qq" auto-complete="off" style="width: 150px"></el-input>
				</el-form-item>


				<el-form-item label="课程简介" prop="description">
					<el-input style="width: 450px"
							:rows="2"
							placeholder="请输入内容"
							v-model="addForm.description">
					</el-input>
				</el-form-item>

				<el-form-item label="课程详情" prop="intro">
					<div class="edit_container">
						<quill-editor
								v-model="addForm.intro"
								ref="myQuillEditor"
								class="editer"
								:options="editorOption"
								@ready="onEditorReady($event)">
						</quill-editor>
					</div>
				</el-form-item>


			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button @click.native="addFormVisible = false" icon="el-icon-remove">取消</el-button>
				<el-button type="primary" @click.native="addSubmit" icon="el-icon-check" >提交</el-button>
			</div>
		</el-dialog>



		<el-dialog title="添加秒杀课程" :visible.sync="killCourseFormVisible"  :close-on-click-modal="false">
			<el-form :model="killCourseForm" label-width="80px"  ref="addForm">
				<el-form-item label="课程名字" prop="price">
					<el-input :disabled="true"  v-model="killCourseForm.courseName" auto-complete="off"></el-input>
				</el-form-item>

				<el-form-item label="秒杀活动" prop="activityId">
					<el-select v-model="killCourseForm.activityId" placeholder="请选择秒杀活动">
						<el-option v-for="item in killActivitys"
								:key="item.id"
								:label="item.name"
								:value="item.id">
							<span style="float: left">{{ item.name }}</span>
							<span style="float: right; color: #8492a6; font-size: 13px">{{ item.timeStr }}</span>
						</el-option>
					</el-select>
					&nbsp;&nbsp;秒杀课程加入秒杀活动，秒杀时间以活动时间为准
				</el-form-item>

				<el-form-item label="秒杀价格" prop="price">
					<el-input  v-model="killCourseForm.killPrice" auto-complete="off"></el-input>
				</el-form-item>

				<el-form-item label="秒杀数量" prop="name">
					<el-input v-model="killCourseForm.killCount" auto-complete="off"></el-input>
				</el-form-item>
				<!--
				<el-form-item label="时间范围" >
					<el-date-picker
							v-model="killCourseForm.startTime"
							type="datetime"
							value-format="yyyy-MM-dd HH:mm:ss"
							size="small"
							placeholder="秒杀开始日期">
					</el-date-picker>

					<el-date-picker
							v-model="killCourseForm.endTime"
							type="datetime"
							value-format="yyyy-MM-dd HH:mm:ss"
							size="small"
							placeholder="秒杀结束日期">
					</el-date-picker>
				</el-form-item>
				-->
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button @click.native="killCourseFormVisible = false" icon="el-icon-remove">取消</el-button>
				<el-button type="primary" @click.native="addKillCourseSubmit"  icon="el-icon-check">提交</el-button>
			</div>
		</el-dialog>
	</section>
</template>

<script>
    import { quillEditor } from "vue-quill-editor"; //调用编辑器
    import "quill/dist/quill.core.css"
	import "quill/dist/quill.snow.css"
	import "quill/dist/quill.bubble.css"

	export default {
        computed: {
			editor() {
                return this.$refs.myQuillEditor.quill
            }
		},
        components: {//使用编辑器
            quillEditor
        },
		data() {
			return {
				uploadPicData:{},
				uploadZipData:{},
				uploadResource:{
			      	courseId:1,
					paths:[]
				},
                row:"",
                courseTypeProps:{
                    value:"id",
                    label:"name"
				},
                priceDisabled:true,
                editorOption: {},//富文本编辑框配置
			    grades:[
					{id:1,name:"青铜"},{id:2,name:"白银"},{id:3,name:"黄金"},{id:4,name:"白金"},{id:5,name:"钻石"}
				],
                charges:[
					{"id":1 , "name":"免费"},
					{"id":2 , "name":"收费"}
				],
				teachers:[],
				courseTypes:[],
        addFormVisible:false,
				//images:[xxx.jgp,xxxx,jpg,xxxx.jpg],
				killActivitys:[],
				addForm:{
          startTime:'',
          endTime:'',
          validDays:'',
          name:'',
          forUser:'',
          gradeId:'',
					teacherIds:'',
          courseTypeId:'',
          description:'',
          intro:'',
          chargeId:'',
          price:'',
          priceOld:'',
          qq:'',
          pic:'',
					zipResources:''
				},
        listLoading:false,
				//查询对象
				filters:{
					keywords:''
				},
				page:1,//当前页,要传递到后台的
				total:0, //分页总数
			    courses:[], //当前页数据
				sels:'',
				//秒杀相关================
				killCourseFormVisible:false,
				killCourseForm:{
					courseId:"",
					killCount:"",
					startTime:"",
					endTime:"",
					killPrice:"",
					courseName:"",
					coursePic:"",
					teacherNames:"",
					activityId:"",
				},
			}
		},
		methods: {
        	//秒杀相关
			getKillActivitys(){
				this.$http.get("/kill/killActivity/list").then(result=>{
					let {data,success,message} = result.data;
					if(success){
						this.killActivitys = data;
					}else{
						this.$message({ message: message,type: 'error'});
					}
				}).catch(error => {
					this.$message({ message: error.message,type: 'error'});
				});
			},
			killCourseModelView(){
				//获取选中的行
				if(!this.row || this.row  === ""){
					this.$message({ message: '老铁，请选择数据',type: 'error'});
					return;
				}
				this.killCourseForm.killCount = "";
				this.killCourseForm.killPrice = "";
				this.killCourseForm.startTime = "";
				this.killCourseForm.endTime = "";

				this.killCourseForm.courseId = this.row.id;
				this.killCourseForm.courseName = this.row.name;
				this.killCourseForm.coursePic = this.row.pic;
				this.killCourseForm.teacherNames = this.row.teacherNames;
				this.killCourseFormVisible = true;
			},
			addKillCourseSubmit(){
				this.$http.post("/kill/killCourse/save",this.killCourseForm).then(res=>{
					var ajaxResult = res.data;
					if(ajaxResult.success){
						this.$message({
							message: '加入秒杀成功，请在秒杀中心查看!',
							type: 'success'
						});
						this.killCourseFormVisible = false;
						//this.getCourses();
					}else{
						this.$message({ message: ajaxResult.message,type: 'error'});
					}
				}).catch(error=>{
					this.$message({ message: '保存失败!',type: 'error'});
				})
			},
			async getSign(data){
				//让这个请求变成同步请求
				await this.$http.get("/common/oss/sign").then(response=>{
					//设置相关的参数
					var resultObj = response.data.data;
					data.policy = resultObj.policy;
					data.signature = resultObj.signature;
					data.ossaccessKeyId = resultObj.accessid;
					data.dir = resultObj.dir;
					data.host = resultObj.host;
					//上传的文件名，使用UUID处理一下
					data.key = resultObj.dir + '/'+this.getUUID()+'_${filename}';
				});
			},
        	//文件上传===============================================================
			getUUID() {
				var s = [];
				var hexDigits = "0123456789abcdef";
				for (var i = 0; i < 36; i++) {
					s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
				}
				s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
				s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
				s[8] = s[13] = s[18] = s[23] = "-";
				var uuid = s.join("");
				return uuid;
			},
			async beforePicUpload(){
				await this.getSign(this.uploadPicData);
			},
			async beforeZipUpload(){
				await this.getSign(this.uploadZipData);
			},
			handlePicSuccess(res, file) {
				//上传的完整的文件地址 ： https://java1030.oss-cn-chengdu.aliyuncs.com/ymcc393olajewfjaolfja.jpg
				var urlPath = this.uploadPicData.host + '/' + this.uploadPicData.key.replace("${filename}",file.name) ;
				this.addForm.pic = res.data;
				this.$message({message: '上传成功', type: 'success' });
			},
			handleZipSuccess(res, file) {
				//上传的完整的文件地址
				//var urlPath = this.uploadZipData.host + '/' + this.uploadZipData.key.replace("${filename}",file.name) ;
        this.addForm.zipResources = res.data;
				this.$message({message: '上传成功', type: 'success' });
			},
            addSubmit(){
                this.addForm.courseTypeId = this.addForm.courseTypeId[this.addForm.courseTypeId.length - 1];
				var gradeName;
				for(var i = 0 ; i < this.grades.length ; i++){
                    var grade = this.grades[i];
                    if(grade.id === this.addForm.gradeId){
                        gradeName = grade.name;
                        break;
					}
				}

                var param = {
                    course:{
                        courseTypeId:this.addForm.courseTypeId,
                        name:this.addForm.name,
                        forUser:this.addForm.forUser,
                        gradeId:this.addForm.gradeId,
                        gradeName:gradeName,
                        pic:this.addForm.pic,
                        startTime:this.addForm.startTime,
                        endTime:this.addForm.endTime
					},
                	courseDetail:{
                        description:this.addForm.description,
                        intro:this.addForm.intro
					},
                	courseMarket:{
                        charge:this.addForm.chargeId,
                        qq:this.addForm.qq,
                        price:this.addForm.price,
                        priceOld:this.addForm.priceOld,
                        validDays:this.addForm.validDays
					},
					courseResource:{
                    	resources:this.addForm.zipResources,
						type:0	//课件
					},
					teacherIds:this.addForm.teacherIds
				};

                this.$http.post("/course/course/save",param).then(res=>{
                    var ajaxResult = res.data;
                    if(ajaxResult.success){
                        this.$message({
                            message: '保存成功!',
                            type: 'success'
                        });
                        this.addFormVisible = false;
                        this.getCourses();
                    }else{
                        this.$message({
                            message: '提交失败['+res.data.message+"]",
                            type: 'error'
                        });
                    }
				});
			},
			getGrades(){
              this.$http.get("/system/systemdictionaryitem/listBySn/dj").then(result=>{
                  this.grades = result.data.data;
              });
			},
      getCourseTypes(){
        this.$http.get("/course/courseType/treeData").then(result=>{
            this.courseTypes = result.data.data;
        });
			},
        changeCharge(chargeId){
            if(chargeId === 1){
                this.priceDisabled = true;
                this.addForm.price = "";
                this.addForm.priceOld = "";
            }else{
                this.priceDisabled = false;
            }
			},
            onEditorReady(editor) {
                //当富文本编辑框初始化好执行
            },
            addHandler(){
				this.addFormVisible = true;
			},
            handleCurrentChange(curentPage){
                this.page = curentPage;
                this.getCourses();
			},
            getCourses(){
                //发送Ajax请求后台获取数据  axios
				//添加分页条件及高级查询条件
				let para = {
				    "page":this.page,
					"rows":10,
					"keyword":this.filters.keywords
				};
				this.listLoading = true; //显示加载圈
				//分页查询
                this.$http.post("/course/course/pagelist",para).then(result=>{
                        this.total = result.data.data.total;
                        this.courses = result.data.data.rows;
                        this.listLoading = false;  //关闭加载圈
				}).catch(error => {
					this.$message({ message: error.message,type: 'error'});
				});
			},
            onLineCourse(){
                //获取选中的行
				if(!this.row || this.row  === ""){
                    this.$message({ message: '老铁，你不选中数据，臣妾上不了啊....',type: 'error'});
				    return;
				}

				this.$http.post("/course/course/onLineCourse/"+this.row.id).then(res=>{
				    var ajaxResult = res.data;
				    if(ajaxResult.success){
                        this.$message({ message: '老铁，上线成功.',type: 'success'});
                        this.getCourses();
					}else{
                        this.$message({ message: ajaxResult.message,type: 'error'});
					}
				}).catch(error => {
					this.$message({ message: error.message,type: 'error'});
				});
              // this.$http.post("/course/course/onLineCourse/" + this.row.id, {}, {
              //   headers: {
              //     "Content-Type": "application/json;charset=UTF-8"
              //   }
              // }).then(res => {
              //   var ajaxResult = res.data;
              //   if (ajaxResult.success) {
              //     this.$message({ message: '老铁，上线成功.', type: 'success' });
              //     this.getCourses();
              //   } else {
              //     this.$message({ message: ajaxResult.message, type: 'error' });
              //   }
              // }).catch(error => {
              //   this.$message({ message: error.message, type: 'error' });
              // });
			},
            offLineCourse(){
                //获取选中的行
                if(!this.row || this.row  === ""){
                    this.$message({ message: '老铁，你不选中数据，臣妾下不了啊....',type: 'error'});
                    return;
                }

                this.$http.post("/course/course/offLineCourse/"+this.row.id).then(res=>{
                    var ajaxResult = res.data;
                    if(ajaxResult.success){
                        this.$message({ message: '老铁，下线成功.',type: 'success'});
                        this.getCourses();
                    }else{
                        this.$message({ message: ajaxResult.message,type: 'error'});
                    }
                })
			},
            rowClick(row){
				this.row = row;
			},
            statusFormatter: function (row, column) {
                return row.status == 1 ? '已上线' : '未上线';
            },

			//讲师
			getTeachers(){
				this.$http.get("/course/teacher/list")
						.then(result=>{
							this.teachers = result.data.data;
							console.log(this.teachers)
						}).catch(error => {
							this.$message({ message: error.message,type: 'error'});
						});
			},
			edit(row){
				// 复用新增弹窗，传入数据
				this.addForm = {
					id: row.id,
					name: row.name,
					forUser: row.forUser,
					courseTypeId: row.courseTypeId,
					gradeId: row.gradeId,
					startTime: row.startTime,
					endTime: row.endTime,
					validDays: row.validDays,
					description: row.description,
					intro: row.intro,
					chargeId: row.chargeId,
					price: row.price,
					priceOld: row.priceOld,
					qq: row.qq,
					pic: row.pic,
					teacherIds: row.teacherIds
				};
				this.addFormVisible = true;
			},
			del(row){
				this.$confirm('确认删除该课程吗?', '提示', { type: 'warning' }).then(() => {
					this.listLoading = true;
					this.$http.delete("/course/course/" + row.id).then(result => {
						let { success, message, data } = result.data;
						if (success) {
							this.$message({ message: "删除成功", type: 'success' });
							this.getCourses();
						} else {
							this.$message({ message: "删除失败[" + message + "]", type: 'error' });
						}
						this.listLoading = false;
					}).catch(error => {
						this.listLoading = false;
						this.$message({ message: "删除失败[" + error.message + "]", type: 'error' });
					})
				});
			},
			batchRemove(){
				var ids = this.sels.map(item => item.id);
				if(ids.length === 0){
					this.$message({ message: "请选择要删除的数据", type: 'warning' });
					return;
				}
				this.$confirm('确认删除选中课程吗？', '提示', { type: 'warning' }).then(() => {
					this.listLoading = true;
					let deletePromises = ids.map(id => this.$http.delete("/course/course/" + id));
					Promise.all(deletePromises).then(results => {
						let allSuccess = results.every(result => result.data.success);
						if(allSuccess){
							this.$message({ message: "批量删除成功", type: 'success' });
						}else{
							this.$message({ message: "部分删除失败", type: 'error' });
						}
						this.getCourses();
						this.listLoading = false;
					}).catch(error => {
						this.listLoading = false;
						this.$message({ message: "批量删除失败["+error.message+"]", type: 'error' });
					})
				})
			},
			selsChange(sels){
				this.sels = sels;
			},
		},

		mounted() {
		    this.getCourses();
		    //this.getGrades();
		    this.getCourseTypes();
		    this.getTeachers();
		    this.getKillActivitys();
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

	.el-table__empty-block {
		background: #fff;
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

			.el-select,
			.el-cascader {
				width: 100%;
			}
		}

		.el-divider {
			margin: 20px 0;
			background-color: #ebeef5;
		}

		// 富文本编辑器样式
		.edit_container {
			width: 100%;

			.ql-container {
				min-height: 200px;
				border-radius: 0 0 6px 6px;
			}

			.ql-toolbar {
				border-radius: 6px 6px 0 0;
				border-color: #dcdfe6;
			}
		}

		// 上传组件样式
		.upload-demo {
			.el-upload-list {
				.el-upload-list__item {
					transition: all 0.3s;

					&:hover {
						background-color: #f5f7fa;
					}
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

// 单选框组样式
::v-deep .el-radio-group {
	.el-radio {
		margin-right: 20px;

		.el-radio__input.is-checked .el-radio__inner {
			background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
			border-color: #667eea;
		}

		.el-radio__input.is-checked + .el-radio__label {
			color: #667eea;
		}
	}
}
</style>