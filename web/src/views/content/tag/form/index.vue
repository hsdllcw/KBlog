<template>
  <el-container>
    <el-form :model="tagData" :rules="rules" ref="tagData" label-width="100px">
      <el-form-item label="标签名称" prop="name">
         <el-input v-model="tagData.name"></el-input>
      </el-form-item>
      <el-form-item label="标签标识" prop="sign">
         <el-input v-model="tagData.sign"></el-input>
      </el-form-item>
      <el-form-item label="标签顺序" prop="sort">
         <el-input v-model="tagData.sort"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm('tagData')">{{tagData.id?'保存修改':'立即创建'}}</el-button>
        <el-button @click="resetForm('tagData')">重置</el-button>
      </el-form-item>
    </el-form>
  </el-container>
</template>
<script>
  import Tag from '@/api/tag'
  export default {
    data() {
      return {
        tag: new Tag(),
        tagData: {},
        rules: {
          name: [
            { required: true, message: '请输入栏目名称', trigger: 'blur' }
          ],
          sign: [
            { required: true, message: '请输入栏目标识', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.tag[`${this.tagData.id ? 'update' : 'create'}`](this.tagData).then(() => {
              this.$message({
                message: '保存成功！',
                type: 'success'
              })
              if(!this.tagData.id) {
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
      if(this.$route.query.id) {
        this.tag.get(this.$route.query.id).then(result => {
          let tagData = result[`data`][`result`]
          if(!this.tagData || this.tagData.id !== tagData.id) {
            this.tagData = tagData
          }
        })
      }
    }
  }
</script>