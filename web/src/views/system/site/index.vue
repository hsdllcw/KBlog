<template>
  <el-container>
    <el-form :model="siteData" :rules="rules" ref="siteData" label-width="100px">
      <el-form-item label="站点名称" prop="name">
         <el-input v-model="siteData.name"></el-input>
      </el-form-item>
      <el-form-item label="站点标识" prop="sign">
         <el-input v-model="siteData.sign"></el-input>
      </el-form-item>
      <el-form-item label="域名" prop="domain">
         <el-input v-model="siteData.domain"></el-input>
      </el-form-item>
      <el-form-item label="主题" prop="templateTheme">
         <el-input v-model="siteData.templateTheme"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm('siteData')">{{siteData.id?'保存修改':'立即创建'}}</el-button>
        <el-button @click="resetForm('siteData')">重置</el-button>
      </el-form-item>
    </el-form>
  </el-container>
</template>
<script>
  import Site from '@/api/site'
  export default {
    data() {
      return {
        site: new Site(),
        siteData: {},
        rules: {
          name: [
            { required: true, message: '请输入站点名称', trigger: 'blur' }
          ],
          sign: [
            { required: true, message: '请输入站点标识', trigger: 'blur' }
          ],
          domain: [
            { required: true, message: '请输入域名', trigger: 'blur' }
          ],
          templateTheme: [
            { required: true, message: '请输入主题', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.site[`${this.siteData.id ? 'update' : 'create'}`](this.siteData).then(() => {
              this.$message({
                message: '保存成功！',
                type: 'success'
              })
              this.mounted()
            })
          }
        })
      },
      resetForm(formName) {
        this.$refs[formName].resetFields()
      }
    },
    mounted() {
      this.site.getList().then(result => {
        let siteList = result[`data`][`result`][`content`]
        let siteData = null
        if(siteList.length > 0) {
          siteData = siteList[0]
        }
        if(siteData) {
          if(!this.siteData || this.siteData.id !== siteData.id) {
            this.siteData = siteData
          }
        }
      })
    }
  }
</script>