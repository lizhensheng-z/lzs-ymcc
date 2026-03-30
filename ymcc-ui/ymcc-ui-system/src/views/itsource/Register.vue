<template>
  <div>
    <!--:model="tenant" 数据双向绑定-->
    <!--ref="tenantForm" id="tenantForm",给form去一个名字-->
    <!--:rules="formRules" 校验规则-->
    <el-form :model="employee" ref="tenantForm" :rules="formRules" label-position="left" label-width="100px" class="demo-ruleForm login-container">
      <h3 class="title">用户注册</h3>
      <el-form-item prop="companyName"label="公司名称">
        <el-input type="text" v-model="employee.companyName" auto-complete="off" placeholder="请输入公司名称！"></el-input>
      </el-form-item>
      <el-form-item prop="companyNum" label="公司执照">
        <el-input type="text" v-model="employee.companyNum" auto-complete="off" placeholder="请输入执照号码！"></el-input>
      </el-form-item>
      <el-form-item prop="address" label="公司地址">
        <el-input type="text" style="width: 350px" v-model="employee.address" auto-complete="off" placeholder="请输入地址！"></el-input>
        <el-button size="small" type="primary" @click="selectAdrress">选择</el-button>
      </el-form-item>
      <el-form-item prop="logo" label="公司Logo">

        <el-upload
                class="upload-demo"
                action="http://itsource-hrm.oss-cn-chengdu.aliyuncs.com"
                :data="uploadData"
                :before-upload="beforeUpload"
                :on-success="handleSuccess"
                :file-list="fileList"
                list-type="picture">
          <el-button size="small" type="primary">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
        </el-upload>

      </el-form-item>
      <el-form-item prop="meal" label="套餐选择">
        <el-select v-model="employee.mealId" placeholder="请选择">
          <el-option
                  v-for="item in meals"
                  :key="item.id"
                  :label="item.mealName"
                  :value="item.id">
            <span style="float: left">{{ item.mealName }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px">{{ item.mealPrice }}</span>
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item prop="meal" label="租户类型">
        <el-select v-model="employee.tenantTypeId" placeholder="请选择">
          <el-option
                  v-for="item in tenantTypes"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item prop="username" label="公司账号">
        <el-input type="text" v-model="employee.username" auto-complete="off" placeholder="请输入账号！"></el-input>
      </el-form-item>
      <el-form-item prop="tel" label="手机号码">
        <el-input type="text" v-model="employee.tel" auto-complete="off" placeholder="请输入电话！"></el-input>
      </el-form-item>
      <el-form-item prop="email" label="电子邮件">
        <el-input type="text" v-model="employee.email" auto-complete="off" placeholder="请输入邮件！"></el-input>
      </el-form-item>
      <el-form-item prop="password" label="密码">
        <el-input type="password" v-model="employee.password" auto-complete="off" placeholder="请输入密码！"></el-input>
      </el-form-item>
      <el-form-item prop="comfirmPassword" label="确认密码">
        <el-input type="password" v-model="employee.comfirmPassword" auto-complete="off" placeholder="请输入确认密码！"></el-input>
      </el-form-item>
      <el-form-item style="width:100%;">
        <el-button type="primary" style="width:100%;" @click.native.prevent="settledIn" >入驻</el-button>
      </el-form-item>
    </el-form>
    <el-dialog
            title="选择地址"
            :visible.sync="dialogVisable"
            width="30%">
      <baidu-map :center="{lng: 116.403765, lat: 39.914850}" :zoom="11">
        <bm-view class="map"></bm-view>
        <bm-control :offset="{width: '10px', height: '10px'}">
          <!--:sugStyle="{zIndex: 2100} 让搜索提示上浮-->
          <bm-auto-complete v-model="keyword" :sugStyle="{zIndex: 2100}">
            <div style="margin-bottom:10px">
              <input id="searchInput" type="text" placeholder="请输入关键字" class="searchinput"/>
              <el-button type="success" @click="selectAdrressConfirm">确定</el-button>
            </div>
          </bm-auto-complete>
        </bm-control>
        <bm-local-search :keyword="keyword" :auto-viewport="true" ></bm-local-search>
      </baidu-map>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisable=false">取 消</el-button>
        <el-button type="primary" @click="dialogVisable=false">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
    export default {
        data() {
            var validatePass2 = (rule, value, callback) => {
                console.log(value); //确认密码
                if (value === '') {
                    callback(new Error('请再次输入密码'))
                } else if (value !== this.employee.password) {
                    callback(new Error('两次输入密码不一致!'))
                } else {
                    callback()
                }
            }
            return {
                //文件上传参数
                uploadData:{},
                meals:[],
                tenantTypes:[],
                keyword:'',
                dialogVisable:false,
                // fileList: [{"name":"xxx","http://localhost/"+this.employee.logo}],
                fileList: [{name:"xxx",url:"http://localhost/uploads/63f18e2b-0717-4d38-b1d8-b29ab463706f.jpg"}],
                //employee:tenant 为了做数据表单校验不要嵌套对象
                employee: {
                    companyName: '源码时代',
                    companyNum: '110',
                    address: '天府新谷',
                    logo:'/xx.jpg',
                    tenantTypeId:'',

                    username:'itsource',
                    tel:'110',
                    email:'itsource@qq.com',
                    password:'123456',
                    comfirmPassword:'123456',

                    mealId:''
                },
                formRules: {
                    companyName: [
                        { required: true, message: '请输入公司名称!', trigger: 'blur' }
                    ],
                    // meal: [
                    //     { required: true, message: '请选择套餐!', trigger: 'blur' }
                    // ],
                    companyNum: [
                        { required: true, message: '请输入公司座机!', trigger: 'blur' }
                    ],
                    address: [
                        { required: true, message: '请输入公司地址!', trigger: 'blur' }
                    ],
                    logo: [
                        { required: true, message: '请输入公司logo!', trigger: 'blur' }
                    ],
                    username: [
                        { required: true, message: '请输入账号!', trigger: 'blur' }
                    ],
                    tel: [
                        { required: true, message: '请输入电话!', trigger: 'blur' }
                    ],
                    email: [
                        { type: 'email',required: true, message: '请输入邮箱!', trigger: 'blur' }
                    ],
                    password: [
                        { required: true, message: '请输入密码!', trigger: 'blur' }
                    ],
                    comfirmPassword: [
                        {required: true,validator: validatePass2, trigger: 'blur' } //自定义校验规则
                    ]
                }
            };
        },
        mounted() {
            this.getMeals();
            this.getTenantTypes();
        },
        methods: {
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
            async beforeUpload(){
                //让这个请求变成同步请求
                await this.$http.get("/file/file/oss/sign").then(response=>{
                    //设置相关的参数
                    console.log(response);
                    var resultObj = response.data.data;
                    this.uploadData.policy = resultObj.policy;
                    this.uploadData.signature = resultObj.signature;
                    this.uploadData.ossaccessKeyId = resultObj.accessid;
                    this.uploadData.dir = resultObj.dir;
                    this.uploadData.host = resultObj.host;

                    //上传的文件名，使用UUID处理一下
                    this.uploadData.key = resultObj.dir + '/'+this.getUUID()+'_${filename}';

                    //http://itsource-hrm.oss-cn-chengdu.aliyuncs.com/itsource/xxxkklojxixox._${filename}.jpg
                });
            },
            handleSuccess(res, file) {
                //this.fileList.pop();
                //上传的完整的文件地址
                var urlPath = this.uploadData.host + '/' + this.uploadData.key.replace("${filename}",file.name) ;
                this.employee.logo = urlPath;
                this.$message({message: '上传成功，图片地址：'+this.employee.logo, type: 'success' });
            },

            getTenantTypes(){
                this.$http.get("/system/tenantType/list") //$.Post(.....)
                    .then(result=>{
                        this.tenantTypes = result.data.data;
                    });
            },
            getMeals(){
              this.$http.get("/auth/meal/list").then(res=>{
                  this.meals = res.data.data;
                  console.log(this.meals);
              });
            },
            selectAdrressConfirm(){
              //获取值搜索框值,设置给地址
                var searchInputV=document.getElementById("searchInput").value;
                this.employee.address = searchInputV;
                //关闭对话框
                this.dialogVisable = false;
            },
            selectAdrress(){
                this.dialogVisable = true;
            },

            handleRemove(file, fileList) {
                var path = file.response.resultObj;
                var param = {
                    path:path
                };
                this.$http.post("/fastdfs/fastdfs/remove",param).then((res) => {
                    if(res.data.success){
                        this.$message({
                            message: '操作成功!',
                            type: 'success'
                        });

                        //
                    }
                    else{
                        this.$message({
                            message: res.data.message,
                            type: 'error'
                        });
                    }

                });

            },
            handlePreview(file) {
                console.log(file);
            },
            //入住提交
            settledIn(){
                this.$refs.tenantForm.validate((valid) => {
                    //校验表单成功后才做一下操作
                    if (valid) {
                        this.$confirm('确认入驻吗？', '提示', {}).then(() => {
                            //拷贝后面对象的值到新对象,防止后面代码改动引起模型变化
                            var param = {
                                "password" :this.employee.password,
                                "username" :this.employee.username,
                                "tenant" : {
                                    "companyName":this.employee.companyName,
                                    "companyNum":this.employee.companyNum,
                                    "address":this.employee.address,
                                    "logo":this.employee.logo,
                                    "tenantTypeId":this.employee.tenantTypeId,
                                },
                                "employee":{
                                    "tel" :this.employee.tel,
                                    "email" :this.employee.email
                                },
                                "mealId":this.employee.mealId
                            };

                            //判断是否有id有就是修改,否则就是添加
                            this.$http.post("/system/tenant/entering",param).then((res) => {
                                if(res.data.success){
                                    this.$message({
                                        message: '操作成功!',
                                        type: 'success'
                                    });
                                    //重置表单
                                    this.$refs['tenantForm'].resetFields();
                                    //跳转登录页面
                                    this.$router.push({ path: '/login' });
                                }
                                else{
                                    this.$message({
                                        message: res.data.message,
                                        type: 'error'
                                    });
                                }

                            });
                        });
                    }
                })
            }
        }
    }

</script>

<style lang="scss" scoped>
@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.login-container {
  margin: 40px auto;
  width: 560px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  backdrop-filter: blur(10px);
  animation: float 6s ease-in-out infinite;

  .title {
    margin: 0px auto 30px auto;
    text-align: center;
    font-size: 28px;
    font-weight: 600;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  ::v-deep .el-form-item {
    margin-bottom: 22px;

    .el-form-item__label {
      font-weight: 500;
      color: #606266;
    }

    .el-input__inner {
      border-radius: 8px;
      height: 42px;
      line-height: 42px;
      transition: all 0.3s;

      &:focus {
        border-color: #667eea;
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
      }
    }
  }

  ::v-deep .el-button--primary {
    width: 100%;
    height: 46px;
    border-radius: 10px;
    font-size: 16px;
    font-weight: 500;
    letter-spacing: 2px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    transition: all 0.3s;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
    }

    &:active {
      transform: translateY(0);
    }
  }

  ::v-deep .el-select {
    width: 100%;

    .el-input__inner {
      border-radius: 8px;
    }
  }

  ::v-deep .upload-demo {
    .el-button {
      border-radius: 8px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-1px);
      }
    }

    .el-upload__tip {
      color: #909399;
      font-size: 12px;
    }
  }
}

.map {
  width: 100%;
  height: 300px;
  border-radius: 8px;
  overflow: hidden;
}

.searchinput {
  width: 300px;
  box-sizing: border-box;
  padding: 10px 15px;
  border: 1px solid #dcdfe6;
  line-height: 20px;
  font-size: 14px;
  height: 42px;
  color: #333;
  position: relative;
  border-radius: 8px;
  transition: all 0.3s;

  &:focus {
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    outline: none;
  }
}

// 弹窗样式
::v-deep .el-dialog {
  border-radius: 12px;
  overflow: hidden;

  .el-dialog__header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 18px 24px;

    .el-dialog__title {
      color: #fff;
      font-weight: 600;
    }

    .el-dialog__headerbtn {
      top: 18px;
      right: 20px;

      .el-dialog__close {
        color: rgba(255, 255, 255, 0.8);

        &:hover {
          color: #fff;
        }
      }
    }
  }

  .el-dialog__body {
    padding: 24px;
  }

  .el-dialog__footer {
    padding: 16px 24px;
    border-top: 1px solid #ebeef5;

    .el-button {
      border-radius: 6px;
      padding: 10px 24px;
    }
  }
}
</style>