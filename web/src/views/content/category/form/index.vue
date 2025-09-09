<template>
  <el-container>
    <el-form :model="categoryData" :rules="rules" ref="categoryData" label-width="100px">
      <el-form-item label="栏目名称" prop="name">
         <el-input v-model="categoryData.name"></el-input>
      </el-form-item>
      <el-form-item label="栏目标识" prop="sign">
         <el-input v-model="categoryData.sign"></el-input>
      </el-form-item>
      <!-- <el-form-item label="树码" prop="treeCode">
         <el-input v-model="categoryData.treeCode"></el-input>
      </el-form-item> -->
      <el-form-item label="父栏目" prop="parentId">
        <el-select v-model="categoryData.parentId" placeholder="请选择所属栏目" class="el-col-24">
          <el-option
            v-for="item in categoryListData"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm('categoryData')">{{categoryData.id?'保存修改':'立即创建'}}</el-button>
        <el-button @click="resetForm('categoryData')">重置</el-button>
      </el-form-item>
    </el-form>
  </el-container>
</template>
<script>
  import Category from '@/api/category'
  export default {
    data() {
      return {
        category: new Category(),
        categoryData: {},
        categoryListData: [],
        rules: {
          name: [
            { required: true, message: '请输入栏目名称', trigger: 'blur' }
          ],
          sign: [
            { required: true, message: '请输入栏目标识', trigger: 'blur' }
          ]
          // treeCode: [
          //   { required: true, message: '请输入树码', trigger: 'blur' }
          // ]
        }
      }
    },
    methods: {
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.category[`${this.categoryData.id ? 'update' : 'create'}`](this.categoryData).then(() => {
              this.$message({
                message: '保存成功！',
                type: 'success'
              })
              if(!this.categoryData.id) {
                this.resetForm(formName)
              }
            })
          }
        })
      },
      resetForm(formName) {
        this.$refs[formName].resetFields()
      }
    },
    mounted() {
      Promise.all([
        new Promise((resolve, reject) => {
          if(this.$route.query.id) {
            this.category.get(this.$route.query.id).then(result => {
              let categoryData = result[`data`][`result`]
              if(!this.categoryData || this.categoryData.id !== categoryData.id) {
                this.categoryData = categoryData
                resolve()
              }else {
                resolve()
              }
            })
          }
        }),
        new Promise((resolve, reject) => {
          this.category.getList().then(result => {
            this.categoryListData = result[`data`][`result`][`content`]
            resolve()
          })
        })]
      ).then(() => {
        if(this.categoryData.id) {
          this.categoryListData = this.categoryListData.filter(item => item.id !== this.categoryData.id)
        }
      })
    }
  }
</script>