<template>
  <el-container>
    <el-form :model="collocatedData" :rules="rules" ref="collocatedData" label-width="100px">
      <el-form-item label="功能名称" prop="name">
         <el-input v-model="collocatedData.name"></el-input>
      </el-form-item>
      <el-form-item label="功能标识" prop="sign">
         <el-input v-model="collocatedData.sign"></el-input>
      </el-form-item>
      <el-form-item label="功能路径" prop="path">
         <el-input v-model="collocatedData.path"></el-input>
      </el-form-item>
      <el-form-item label="树码" prop="treeCode">
         <el-input v-model="collocatedData.treeCode"></el-input>
      </el-form-item>
      <el-form-item label="功能图标" prop="icon">
         <el-input v-model="collocatedData.icon"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm('collocatedData')">{{collocatedData.id?'保存修改':'立即创建'}}</el-button>
        <el-button @click="resetForm('collocatedData')">重置</el-button>
      </el-form-item>
    </el-form>
  </el-container>
</template>
<script>
  import Collocated from '@/api/collocated'
  export default {
    data() {
      return {
        collocated: new Collocated(),
        collocatedData: {},
        rules: {
          name: [
            { required: true, message: '请输入功能名称', trigger: 'blur' }
          ],
          sign: [
            { required: true, message: '请输入功能标识', trigger: 'blur' }
          ],
          path: [
            { required: true, message: '请输入功能路径', trigger: 'blur' }
          ],
          treeCode: [
            { required: true, message: '请输入树码', trigger: 'blur' }
          ],
          icon: [
            { required: true, message: '请输入功能图标', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.collocated[`${this.collocatedData.id ? 'update' : 'create'}`](this.collocatedData).then(() => {
              this.$message({
                message: '保存成功！',
                type: 'success'
              })
              if(!this.collocatedData.id) {
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
      if(this.$route.params.collocatedData) {
        this.collocated.get(this.$route.params.collocatedData.id).then(result => {
          let collocatedData = result[`data`][`result`]
          if(!this.collocatedData || this.collocatedData.id !== collocatedData.id) {
            this.collocatedData = collocatedData
          }
        })
      }
    }
  }
</script>