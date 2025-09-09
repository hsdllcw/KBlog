<template>
  <el-container>
    <el-form :model="pageData" :rules="rules" ref="pageData" label-width="100px" class="el-col-20">
      <el-form-item label="文档标题" prop="title">
         <el-input v-model="pageData.title"></el-input>
      </el-form-item>
      <el-form-item label="文档描述" prop="desription">
        <el-input v-model="pageData.desription"></el-input>
      </el-form-item>
      <el-form-item label="所属栏目" prop="categoryId">
        <el-select v-model="pageData.categoryId" placeholder="请选择所属栏目">
          <el-option
            v-for="item in categoryData"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所属标签" prop="tagIds">
        <el-select v-model="pageData.tagIds" multiple placeholder="请选择所属标签">
          <el-option
            v-for="item in tagData"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="文章封面" prop="poster">
        <image-upload v-model="pageData.poster" @on-success="handleImageUpload" action="/upload"></image-upload>
      </el-form-item>
      <el-form-item label="文章外链" prop="linkIs">
        <el-switch
          v-model="pageData.linkIs"
          active-text="外链文章"
          @change="toggleLink"
        >
        </el-switch>
      </el-form-item>
      <el-form-item v-if="pageData.linkIs" label="文章地址" prop="outlink">
        <el-input v-model="pageData.outlink"></el-input>
      </el-form-item>
      <el-form-item label="正文" v-show="!pageData.linkIs">
        <image-upload class="quill-image-upload" @on-success="richUploadSuccess" action="/upload" v-show="false"></image-upload>
        <div id="quill-editor" ref="quill-editor"></div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm('pageData')">{{pageData.id?'保存修改':'立即创建'}}</el-button>
        <el-button @click="resetForm('pageData')">重置</el-button>
      </el-form-item>
    </el-form>
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
  import ImageUpload from '@/components/image-upload'

  Quill.register('modules/imageResize', ImageResize)
  Quill.register('modules/imageDrop', ImageDrop)

  export default {
    components: { ImageUpload },
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
          ],
          outlink: [
            { required: true, message: '请输入文章外链', trigger: 'blur' },
            {validator: (rule, value, callback) => {
              if (value === '') {
                callback(new Error('请输入文章外链'))
              } else {
                if (!value.startsWith('http://') && !value.startsWith('https://')) {
                  callback(new Error('请输入正确的外链地址'))
                } else {
                  callback()
                }
              }
            }, trigger: 'blur'}
          ]
        },
        loading: false,
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
      ['clean'],   // 清除字体样式
      ['link', 'image']  // 链接，图片，视频
      // ['custom']  // 添加一个自定义功能
    ],
    // 自定义富文本的图片上传
    imageFunction(val) {
      if(val) {
        document.querySelector('.quill-image-upload input').click()
      } else {
        this.quill.format('image', false)
      }
    },
    methods: {
      handleImageUpload(response) {
        this.pageData.poster = response.result.url
      },
      // 富文本中的图片上传
      richUploadSuccess(response) {
        /**
         * 如果上传成功
         * ps：不同的上传接口，判断是否成功的标志也不一样，需要看后端的返回
         * 通常情况下，应该有返回上传的结果状态（是否上传成功）
         * 以及，图片上传成功后的路径
         * 将路径赋值给 imgUrl
         */
        if(response.result) {
          let imgUrl = response.result.url
          // 获取光标所在位置
          let length = this.quill.getSelection().index
          // 插入图片，res为服务器返回的图片链接地址
          this.quill.insertEmbed(length, 'image', imgUrl)
          // 调整光标到最后
          this.quill.setSelection(length + 1)
        } else {
          // 提示信息，需引入Message
          this.$message.error('图片插入失败')
        }
      },
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
              container: this.$options.toolbarOptions,
              handlers: {  // 自定义功能
                'image': this.$options.imageFunction
                // 'custom': this.quillCustomFunction
              }
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
        this.$refs[formName].validate((valid) => {
          if (valid) {
            if(this.pageData.linkIs) {
              this.pageData.content = `<p><a href="${this.pageData.outlink}">点击阅读</a></p>`
            }
            this.page[`${this.pageData.id ? 'update' : 'create'}`](this.pageData).then(() => {
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
        clearTimeout(this.timer)
      },
      toggleLink(linkIs) {
        if(!linkIs) {
          this.pageData.content = undefined
        }
      }
    },
    mounted() {
      if(this.$route.name === `UpdatePage`) {
        if(this.$route.query.id) {
          this.page.get(this.$route.query.id).then(result => {
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