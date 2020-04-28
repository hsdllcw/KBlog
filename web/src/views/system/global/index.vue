<template>
  <el-container>
    <el-form :model="globalData" :rules="rules" ref="globalData" label-width="100px">
      <el-form-item label="协议" prop="protocol">
         <el-input v-model="globalData.protocol"></el-input>
      </el-form-item>
      <el-form-item label="端口号" prop="port">
         <el-input v-model="globalData.port"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm('globalData')">{{globalData.id?'保存修改':'立即创建'}}</el-button>
        <el-button @click="resetForm('globalData')">重置</el-button>
      </el-form-item>
    </el-form>
  </el-container>
</template>
<script>
  import Global from '@/api/global'
  export default {
    data() {
      return {
        global: new Global(),
        globalData: {},
        rules: {
          protocol: [
            { required: true, message: '请输入协议', trigger: 'blur' }
          ],
          port: [
            { required: true, message: '请输入端口号', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.global[`${this.globalData.id ? 'update' : 'create'}`](this.globalData).then(() => {
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
      this.global.getList().then(result => {
        let globalList = result[`data`][`result`][`content`]
        let globalData = null
        if(globalList.length > 0) {
          globalData = globalList[0]
        }
        if(globalData) {
          if(!this.globalData || this.globalData.id !== globalData.id) {
            this.globalData = globalData
          }
        }
      })
    }
  }
</script>