<template id="courseType">
    <div>
        <el-row style="height: 100%; margin-top: 10px;">
            <div style="border: 1px solid #eeeeee; min-height:600px;width: 20%;float: left;border-radius: 5px">
                <div class="grid-content bg-purple">
                    <el-tree :data="courseTypes" :props="defaultProps"  @node-click="handleNodeClick">
                    </el-tree>
                </div>
            </div>
            <div  style="width: 79%;float: left;margin-left: 5px;border: 1px solid #eeeeee;min-height:600px;border-radius: 5px">
                <!--工具条-->
                <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
                    <el-form :inline="true" :model="filters">
                        <el-form-item>
                            <el-input v-model="filters.keyword" size="small" placeholder="关键字"></el-input>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="warning" v-on:click="getList" size="small" icon="el-icon-search">查询分类</el-button>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="handleAdd" size="small" icon="el-icon-plus">新增分类</el-button>
                        </el-form-item>
                    </el-form>
                </el-col>

                <!--列表-->
                <el-table :data="datas" highlight-current-row  @selection-change="selsChange" style="width: 100%;">
                    <el-table-column type="selection" >
                    </el-table-column>
                    <el-table-column prop="name" label="标题" width="200" sortable>
                    </el-table-column>
                    <el-table-column prop="description" label="描述" width="200" sortable>
                    </el-table-column>
                    <el-table-column prop="totalCount" label="课程数" width="120" sortable>
                    </el-table-column>
                    <el-table-column prop="pName" label="上级分类" width="130" sortable>
                    </el-table-column>

                    <el-table-column label="操作" width="200">
                        <template scope="scope">
                            <el-button size="small" @click="handleEdit(scope.$index, scope.row)"  icon="el-icon-edit" type="primary">编辑</el-button>
                            <el-button type="danger" size="small" @click="handleDel(scope.$index, scope.row)"  icon="el-icon-remove">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>

                <!--工具条-->
                <el-col :span="24" class="toolbar">
                    <el-button type="danger" @click="batchRemove" :disabled="this.sels.length===0" icon="el-icon-remove" size="small">批量删除</el-button>
                    <el-pagination layout="prev, pager, next" @current-change="handleCurrentChange"
                                   :page-size="10" :total="total" style="float:right;">
                    </el-pagination>
                </el-col>

            </div>
        </el-row>

        <!--新增界面-->
        <el-dialog title="新增" :visible.sync="addFormVisible"  :close-on-click-modal="false">
            <el-form :model="addForm" label-width="80px"  ref="addForm">
                <el-form-item label="分类标题" prop="name">
                    <el-input v-model="addForm.name" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="LOGO" prop="logo">
                    <el-input v-model="addForm.logo" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="排序" prop="sortIndex">
                    <el-input v-model="addForm.sortIndex" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="描述" prop="description">
                    <el-input v-model="addForm.description" auto-complete="off"></el-input>
                </el-form-item>

            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click.native="addFormVisible = false">取消</el-button>
                <el-button type="primary" @click.native="addSubmit" >提交</el-button>
            </div>
        </el-dialog>
    </div>
</template>
<style lang="scss">
.el-row {
    margin-bottom: 20px;
    height: 100%;
}
:last-child {
    margin-bottom: 0;
}

// 左侧树形结构样式
.el-tree {
    background: transparent;
    padding: 16px;

    .el-tree-node {
        &:focus > .el-tree-node__content {
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
        }

        .el-tree-node__content {
            height: 40px;
            border-radius: 6px;
            transition: all 0.3s;
            margin-bottom: 4px;

            &:hover {
                background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
            }
        }

        &.is-current > .el-tree-node__content {
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
            color: #667eea;
            font-weight: 500;
        }
    }
}

.grid-content {
    border-radius: 8px;
    min-height: 36px;
}

.toolbar {
    margin: 0px;
    background: #fff;
    border-radius: 8px;
    padding: 16px 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

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
    }
}

// 表格样式
.el-table {
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
.el-pagination {
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
.el-dialog {
    border-radius: 12px;
    overflow: hidden;

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

<script>
    export default {
        data() {
            return {
                addForm:{
                    name:"",
                    logo:"",
                    sortIndex:"",
                    description:"",
                    pid:""
                },
                addFormVisible:false,
                sels:[],
                filters:{
                    keyword:""
                },
                datas:[],
                page:1,//当前页,要传递到后台的
                total:0, //分页总数
                courseTypes:[],
                defaultProps: {
                    children: 'children',
                    label: 'name'
                }
            }
        },
        methods:{
            handleAdd(){
              this.addFormVisible = true;
            },
            addSubmit(){
                //提交
                this.$http.post("/course/courseType/save",this.addForm).then(res=>{
                    //{success: true, ..
                    var ajaxResult = res.data;
                    if(ajaxResult.success){
                        this.addFormVisible = false;
                        this.$message({
                            message: '提交成功',
                            type: 'success'
                        });
                        this.getTreeData();
                    }else{
                        this.$message({
                            message: ajaxResult.message,
                            type: 'error'
                        });
                    }
                });
            },
            getChildrenByPid(pid){
                this.$http.get("/course/courseType/selectChildrenById/"+pid).then(res=>{
                    this.datas = res.data.data;
                });
            },
            handleCurrentChange(page){
                this.page = page;
                this.getList();
            },
            batchRemove(){
                var ids = this.sels.map(item => item.id);
                if(ids.length === 0){
                    this.$message({ message: "请选择要删除的数据", type: 'warning' });
                    return;
                }
                this.$confirm('确认删除选中分类吗？', '提示', { type: 'warning' }).then(() => {
                    this.listLoading = true;
                    let deletePromises = ids.map(id => this.$http.delete("/course/courseType/" + id));
                    Promise.all(deletePromises).then(results => {
                        let allSuccess = results.every(result => result.data.success);
                        if(allSuccess){
                            this.$message({ message: "批量删除成功", type: 'success' });
                        }else{
                            this.$message({ message: "部分删除失败", type: 'error' });
                        }
                        this.getTreeData();
                        this.getList();
                        this.listLoading = false;
                    }).catch(error => {
                        this.listLoading = false;
                        this.$message({ message: "批量删除失败["+error.message+"]", type: 'error' });
                    })
                })
            },
            handleEdit(index, row){
                this.addForm = {
                    id: row.id,
                    name: row.name,
                    logo: row.logo,
                    sortIndex: row.sortIndex,
                    description: row.description,
                    pid: row.pid
                };
                this.addFormVisible = true;
            },
          handleDel(index, row) {
            this.$confirm('确认删除该记录吗?', '提示', {
              type: 'warning'
            }).then(() => {
              this.listLoading = true;
              let para = { id: row.id };
              this.$http.delete(`/course/courseType/${para.id}`).then((res) => {
                this.listLoading = false;
                let ajaxResult = res.data;
                if (ajaxResult.success) {
                  this.$message({
                    message: '删除成功',
                    type: 'success'
                  });
                  this.getTreeData();
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

          selsChange(){

            },
            getList(){
                this.listLoading = true; //显示加载圈
                let para = {
                    "page":this.page,
					"rows":10,
                    "keyword":this.filters.keyword
                };
                //分页查询
                this.$http.post("/course/courseType/pagelist",para).then(result=>{
                    this.total = result.data.data.total;
                    this.datas = result.data.data.rows;
                    this.listLoading = false;  //关闭加载圈
                }).catch(error => {
                    this.$message({ message: error.message,type: 'error'});
                    this.listLoading = false;  //关闭加载圈
                });
            },
            handleNodeClick(row){
                this.datas = row.children;
                this.addForm.pid = row.id;
            },
            getTreeData(){
                // 发送一个异步请求: get请求 /product/courseType/treeData
                this.$http.get("/course/courseType/treeData").then(res=>{
                    this.courseTypes = res.data.data;
                });
            }
        },
        mounted(){
            this.getList();
            //对courseTypes数据赋值
           this.getTreeData();
        }
    };
</script>