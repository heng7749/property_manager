<template>
    <div style="padding:16px;">
      <el-row style="width: 100%;" :gutter="6">
        <el-col>
            <!-- 部门 -->
            <el-card class="box-card" style="margin-top: 8px;">
                <el-form inline class="searchBar">
                    <el-form-item label="部门名称:">
                        <el-input size="small" v-model="searchValue" maxlength="10" placeholder="请输入部门名" style="width:180px"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button size="small" type="primary" icon="el-icon-search" @click="search">搜索</el-button>
                    </el-form-item>
                </el-form>
                <el-table border :data="tableData" tooltip-effect="dark" size="mini" v-loading="loading"
                    :header-cell-style="{textAlign:'center'}" :cell-style="{textAlign:'center'}">
                    <el-table-column prop="id" label="编号" min-width="120"></el-table-column>
                    <el-table-column prop="name" label="部门" min-width="120"></el-table-column>
                    <el-table-column prop="createTime" label="创建时间" min-width="120"></el-table-column>
                    <el-table-column prop="creator" label="创建人" min-width="120"></el-table-column>
                    <!-- 操作 -->
                    <el-table-column label="操作" fixed="right" min-width="150">
                        <template slot-scope="scope">
                            <el-button size="mini" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
                            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table> 
                <el-button @click="openAddView" type="primary" size="mini" icon="el-icon-plus" style="margin-top: 20px;margin-left: 10px;">新建</el-button>
                <!-- 分页 -->
                <Pagination class="pagination" v-bind:child-msg="pageMsg" @pageChange="pageChangeHandle"></Pagination>
            </el-card>
        </el-col>
      </el-row>
        <!-- 新增部门 -->
        <el-dialog title="部门" :visible.sync="addViewVisible" width="40%" center>
            <el-form ref="addForm" :model="addForm" label-width="90px" size="mini">
                <el-form-item label="部门名:" required>
                    <el-input v-model="addForm.name" style="width:80%"></el-input>
                </el-form-item>
            </el-form>
            <div style="width: 150px;margin-left: auto;margin-right: auto;">
                <el-button type="primary" size="small" @click="addDepartment">提交</el-button>
                <el-button size="small" @click="addViewVisible=false">取消</el-button>
            </div>
        </el-dialog>
        <!-- 部门编辑 -->
        <el-dialog title="部门编辑" :visible.sync="departmentInfoVisible" width="40%" center>
            <el-form ref="editForm" :model="editForm" label-width="90px" size="mini">
                <el-form-item label="部门名:" required>
                    <el-input v-model="editForm.name" style="width:80%"></el-input>
                </el-form-item>
            </el-form>
            <div style="width: 150px;margin-left: auto;margin-right: auto;">
                <el-button type="primary" size="small" @click="editDepartment">提交</el-button>
                <el-button size="small" @click="departmentInfoVisible=false">取消</el-button>
            </div>
        </el-dialog>
    </div>
  </template>
  
  <script>
  import Pagination from '../../components/Pagination.vue'
  export default {
      components: {Pagination},
      name:"DepartmentManager",
      data(){
          return {
              searchValue:'',
              tableData:[],
              pageMsg: {
                currentPage: 1,
                pageSize: 5,
                total: 1
              },
              departmentInfoVisible:false,
              addViewVisible:false,
              addForm:{
                name:''
              },
              editForm:{
                id:'',
                name:''
              }
          }
      },
      methods:{
          handleEdit(row) {
              this.departmentInfoVisible = true;
              Object.assign(this.editForm,row);
          },
          handleDelete(row) {
            this.$confirm('是否删除此部门信息?', '删除部门提示', {
                  confirmButtonText: '确定',
                  cancelButtonText: '取消',
                  type: 'warning'
              }).then(() => {
                this.deleteDepartment(row.id)
              }).catch(() => {
                  this.$message({
                      type: 'info',
                      message: '已取消删除'
                  });          
              });
          },
          pageChangeHandle(param) {
            let currentPage = param.currentPage
            let searchValue = this.searchValue
            this.searchDepartments(currentPage,searchValue)
          },
          openAddView() {
              this.addViewVisible = true;
          },
          search(){
            let currentPage = this.pageMsg.currentPage
            let searchValue = this.searchValue
            this.searchDepartments(currentPage,searchValue)
          },
          searchDepartments(currentPage,searchValue){
            let page = this
            let token = this.$store.getters.GET_TOKEN
            let url = "/heng/department/searchDepartment"
            let params = {
                "name":searchValue,
                "params":{
                    "currentPage": currentPage,
                }
            }
            this.loading = true
            this.$http.post(url, params, {headers:{'token':token}}).then(function(res){
                let result = res.data
                if(result.status === 0){
                    if(result.data.list.length == 0){
                        page.$message('搜索结果为空');
                    } else {
                        page.tableData = result.data.list
                        page.pageMsg.total = result.data.total
                        page.pageMsg.pageSize = result.data.pageSize
                        page.pageMsg.currentPage = result.data.pageNum
                    }
                    } else {
                        page.$message.error(result.msg);
                    }
                    page.loading = false
            }).catch(err=>{
                console.log(err);
                page.loading = false
            })
          },
          addDepartment(){
            let page = this
            let token = this.$store.getters.GET_TOKEN
            let url = "/heng/department/addDepartment"
            let params = {
                "name": this.addForm.name
            }
            this.$http.post(url, params, {headers:{'token':token}}).then(function(res){
                let result = res.data
                if(result.status === 0){
                    page.$message({
                        message: '添加成功',
                        type: 'success'
                    });
                    page.addViewVisible = false
                    page.search()
                } else {
                    page.$message.error(result.msg);
                }
            }).catch(err=>{
                console.log(err);
            })
          },
          editDepartment(){
            let page = this
            let token = this.$store.getters.GET_TOKEN
            let url = "/heng/department/editDepartment"
            let params = {
                "id": this.editForm.id,
                "name": this.editForm.name
            }
            this.$http.post(url, params, {headers:{'token':token}}).then(function(res){
                let result = res.data
                if(result.status === 0){
                    page.$message({
                        message: '修改成功',
                        type: 'success'
                    });
                    page.departmentInfoVisible = false
                    page.search()
                } else {
                    page.$message.error(result.msg);
                }
            }).catch(err=>{
                console.log(err);
            })
          },
          deleteDepartment(id){
            let page = this
            let token = this.$store.getters.GET_TOKEN
            let url = '/heng/department/deleteDepartment'
            let params = {
                'id': id
            }
            this.$http.post(url, params, {headers:{'token':token}}).then(function(res){
                let result = res.data
                if(result.status === 0){
                    page.$message({
                        message: result.msg,
                        type: 'success'
                    });
                    page.search()
                } else {
                    page.$message.error(result.msg);
                }
            }).catch(err=>{
                console.log(err);
            })
          }
      },
      created(){
        this.searchDepartments(1,'')
      }
  }
  </script>
  
  <style scoped>
  .searchBar{
      align-content: center;
      align-items: center;
  }
  .el-dropdown-link {
        cursor: pointer;
        color: #409EFF;
        font-size: 14px;
        padding: 0 12px 0 0;
    }
    .dropdownTag{
    width: 110px;
    height: 40px;
    text-align: right;
    line-height: 40px;
}
  </style>