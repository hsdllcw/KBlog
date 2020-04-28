<template>
  <el-container>
    <el-form :model="pageData" :rules="rules" ref="pageData" label-width="100px" class="el-col-20">
      <el-form-item label="文档标题" prop="title">
         <el-input v-model="pageData.title"></el-input>
      </el-form-item>
      <!-- <el-form-item label="状态" prop="status">
        <el-select v-model="pageData.status" placeholder="请选择状态">
          <el-option label="正常" value="NORMAL"></el-option>
          <el-option label="草稿" value="DRAFT"></el-option>
        </el-select>
      </el-form-item> -->
      <el-form-item label="正文">
        <div id="quill-editor" ref="quill-editor"></div>
      </el-form-item>
      <!-- <el-form-item label="活动时间" required>
        <el-col :span="11">
          <el-form-item prop="date1">
            <el-date-picker type="date" placeholder="选择日期" v-model="page.date1" style="width: 100%;"></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col class="line" :span="2">-</el-col>
        <el-col :span="11">
          <el-form-item prop="date2">
            <el-time-picker placeholder="选择时间" v-model="page.date2" style="width: 100%;"></el-time-picker>
          </el-form-item>
        </el-col>
      </el-form-item> -->
      <!-- <el-form-item label="即时配送" prop="delivery">
        <el-switch v-model="page.delivery"></el-switch>
      </el-form-item> -->
      <!-- <el-form-item label="活动性质" prop="type">
        <el-checkbox-group v-model="page.type">
          <el-checkbox label="美食/餐厅线上活动" name="type"></el-checkbox>
          <el-checkbox label="地推活动" name="type"></el-checkbox>
          <el-checkbox label="线下主题活动" name="type"></el-checkbox>
          <el-checkbox label="单纯品牌曝光" name="type"></el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="特殊资源" prop="resource">
        <el-radio-group v-model="page.resource">
          <el-radio label="线上品牌商赞助"></el-radio>
          <el-radio label="线下场地免费"></el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="活动形式" prop="desc">
        <el-input type="textarea" v-model="page.desc"></el-input>
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" @click="submitForm('pageData')">{{pageData.id?'保存修改':'立即创建'}}</el-button>
        <el-button type="primary" @click="dialog = true">高级设置</el-button>
        <el-button @click="resetForm('pageData')">重置</el-button>
      </el-form-item>
    </el-form>
    <el-drawer
      :before-close="handleClose"
      :visible.sync="dialog"
      direction="rtl"
      custom-class="demo-drawer"
      ref="drawer"
      >
      <el-container>
        <el-main>
          <el-form :model="pageData">
            <el-form-item label="栏目" prop="categoryId" :label-width="formLabelWidth">
              <el-select v-model="pageData.categoryId" placeholder="请选择所属栏目" class="el-col-24">
                <el-option
                  v-for="item in categoryData"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="标签" prop="tagIds" :label-width="formLabelWidth">
              <el-select v-model="pageData.tagIds" multiple placeholder="请选择所属标签" class="el-col-24">
                <el-option
                  v-for="item in tagData"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-form>
        </el-main>
        <el-footer style="text-align: right;">
          <el-button @click="cancelForm">取 消</el-button>
          <el-button type="primary" @click="$refs.drawer.closeDrawer()">{{ loading ? '提交中 ...' : '确 定' }}</el-button>
        </el-footer>
      </el-container>
    </el-drawer>
  </el-container>
</template>
<script>
  import Page from '@/api/page'
  import Category from '@/api/category'
  import Tag from '@/api/tag'
  import 'quill/dist/quill.core.css'
  import 'quill/dist/quill.snow.css'
  import Quill from 'quill'
  import ImageResize from 'quill-image-resize-module'
  import { ImageDrop } from 'quill-image-drop-module'
  Quill.register('modules/imageResize', ImageResize)
  Quill.register('modules/imageDrop', ImageDrop)

  export default {
    data() {
      return {
        page: new Page(),
        category: new Category(),
        tag: new Tag(),
        pageData: {
          category: {},
          tagIds: []
        },
        categoryData: [],
        tagData: [],
        rules: {
          title: [
            { required: true, message: '请输入文档标题', trigger: 'blur' }
          ]
          // status: [
          //   { required: true, message: '请选择文档状态', trigger: 'change' },
          //   { min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
          // ],
          // date1: [
          //   { type: 'date', required: true, message: '请选择日期', trigger: 'change' }
          // ],
          // date2: [
          //   { type: 'date', required: true, message: '请选择时间', trigger: 'change' }
          // ],
          // type: [
          //   { type: 'array', required: true, message: '请至少选择一个活动性质', trigger: 'change' }
          // ],
          // resource: [
          //   { required: true, message: '请选择活动资源', trigger: 'change' }
          // ],
          // desc: [
          //   { required: true, message: '请填写活动形式', trigger: 'blur' }
          // ]
        },
        loading: false,
        dialog: false,
        formLabelWidth: '80px',
        timer: null
      }
    },
    // 富文本工具栏配置
    toolbarOptions: [
      [{ 'size': ['small', false, 'large', 'huge'] }], // 字体大小
      [{ 'header': [1, 2, 3, 4, 5, 6, false] }],     // 几级标题
      ['bold', 'italic', 'underline', 'strike'],    // 加粗，斜体，下划线，删除线
      [{ 'indent': '-1' }, { 'indent': '+1' }],     // 缩进
      [{ 'color': [] }, { 'background': [] }],     // 字体颜色，字体背景颜色
      [{ 'align': [] }],    // 对齐方式
      ['clean']    // 清除字体样式
      // ['image'],
      // ['custom']  // 添加一个自定义功能
    ],
    // // 自定义富文本的图片上传
    // imageFunction(val) {
    //   if(val) {
    //     document.querySelector('#img-upload input').click()
    //   } else {
    //     this.quill.format('image', false)
    //   }
    // },
    methods: {
      // // 富文本中的图片上传
      // richUploadSuccess(response, file, fileList) {
      //   /**
      //    * 如果上传成功
      //    * ps：不同的上传接口，判断是否成功的标志也不一样，需要看后端的返回
      //    * 通常情况下，应该有返回上传的结果状态（是否上传成功）
      //    * 以及，图片上传成功后的路径
      //    * 将路径赋值给 imgUrl
      //    */
      //   if(response.files.file) {
      //     let imgUrl = response.files.file
      //     // 获取光标所在位置
      //     let length = this.quill.getSelection().index
      //     // 插入图片，res为服务器返回的图片链接地址
      //     this.quill.insertEmbed(length, 'image', imgUrl)
      //     // 调整光标到最后
      //     this.quill.setSelection(length + 1)
      //   } else {
      //     // 提示信息，需引入Message
      //     this.$message.error('图片插入失败')
      //   }
      // },
      onEditorChange(eventName, ...args) {
        if(eventName === 'text-change') {
          // args[0] will be delta
          // 获取富文本内容
          this.pageData.content = document.querySelector('#quill-editor').children[0].innerHTML
        } else if (eventName === 'selection-change') {
          // args[0] will be old range
        }
      },
      // 初始化自定义的quill工具栏
      // 拿到quill实例以后，在执行自定义toolbar的操作
      initCustomQullToolbar() {
        // const timeButton = document.querySelector('.ql-custom')
        // timeButton.style.cssText = 'width: 80px; outline: none;'
        // timeButton.innerText = '自定义'
      },
      // // 给自定义的按钮功能加上方法
      // quillCustomFunction() {
      //   const h = this.$createElement
      //   this.$notify({
      //     type: 'success',
      //     title: '自定义一个quill功能',
      //     message: h('i', {style: 'color: teal'}, '可不可以让我自定义一个 Quill 的功能？可不可以让我自定义一个 Quill 的功能？')
      //   })
      // },
      initQuill() {
        const quill = new Quill('#quill-editor', {
          // 编辑器配置选项
          theme: 'snow',
          placeholder: 'Compose an epic...',
          debug: 'error',
          modules: {
            toolbar: {
              container: this.$options.toolbarOptions
              // handlers: {  // 自定义功能
              //   'image': this.$options.imageFunction,
              //   'custom': this.quillCustomFunction
              // }
            },
            imageDrop: true,
            imageResize: {
              modules: ['Resize', 'DisplaySize', 'Toolbar'],
              handleStyles: {
                backgroundColor: 'black',
                border: 'none',
                color: 'white'
              },
              displayStyles: {
                backgroundColor: 'black',
                border: 'none',
                color: 'white'
              },
              toolbarStyles: {
                backgroundColor: 'black',
                border: 'none',
                color: 'white'
              },
              toolbarButtonStyles: {},
              toolbarButtonSvgStyles: {}
            }
          }
        })
        this.quill = quill
        // // 拿到quill实例以后，在执行自定义toolbar的操作
        // this.initCustomQullToolbar()
        /**
         * 监听富文本变化
         * editor-change 包括 text-change、selection-change
         * 你也可以分别监听 text-change 和 selection-change
         * 文档：https://quilljs.com/docs/api/#text-change
         */
        quill.on('editor-change', this.onEditorChange)
      },
      submitForm(formName) {
        const loading = this.$loading({
          lock: true,
          text: 'Loading',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        })
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.page[`${this.pageData.id ? 'update' : 'create'}`](this.pageData).then(() => {
              loading.close()
              this.$message({
                message: '保存成功！',
                type: 'success'
              })
              if(!this.pageData.id) {
                this.resetForm(formName)
              }
            })
          }
        })
      },
      resetForm(formName) {
        this.quill.setText(String())
        this.$refs[formName].resetFields()
      },
      handleClose(done) {
        this.$confirm('确定要提交表单吗？')
          .then(_ => {
            this.timer = setTimeout(() => {
              done()
              this.submitForm('pageData')
              // 动画关闭需要一定的时间
            }, 2000)
          })
          .catch(_ => {})
      },
      cancelForm() {
        this.dialog = false
        clearTimeout(this.timer)
      }
    },
    mounted() {
      if(this.$route.name === `UpdatePage`) {
        if(this.$route.params.pageData) {
          this.page.get(this.$route.params.pageData.id).then(result => {
            let pageData = result[`data`][`result`]
            if(!this.pageData || this.pageData.id !== pageData.id) {
              pageData.tagIds = pageData.tags.map(item => item.id)
              pageData.categoryId = pageData.category.id
              this.pageData = pageData
              document.querySelector('#quill-editor').children[0].innerHTML = pageData.content
            }
          })
        }
      }
      this.category.getList().then(result => {
        this.categoryData = result[`data`][`result`][`content`]
      })
      this.tag.getList().then(result => {
        this.tagData = result[`data`][`result`][`content`]
      })
      this.initQuill()
    },
    beforeDestroy() {
      this.quill = null
      delete this.quill
    }
  }
</script>
<style scoped>
.main-container {
  max-width: 80%;
  min-width: 800px;
  margin-left: auto;
  margin-right: auto;
}
.tips {
  width: 600px;
  padding: 15px 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  line-height: 2;
}
.font {
  font-size: 18px;
}
#quill-editor {
  /*width: 80%;*/
  height: 300px;
}
/*字数统计*/
.quill-count {
  border: 1px solid #ccc;
  border-top: none;
  height: 30px;
  line-height: 30px;
  text-align: right;
  padding-right: 10px;
  font-size: 14px;
  color: #666;
}
/* 内容返显 */
.ql-editor {
  margin-bottom: 50px;
}
</style>