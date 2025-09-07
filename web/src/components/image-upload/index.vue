<template>
  <div class="component-upload-image">
    <el-upload
      multiple
      :disabled="disabled"
      :action="uploadImgUrl"
      list-type="picture-card"
      :on-success="handleUploadSuccess"
      :before-upload="handleBeforeUpload"
      :data="data"
      :limit="limit"
      :on-error="handleUploadError"
      :on-exceed="handleExceed"
      ref="imageUpload"
      :before-remove="handleDelete"
      :show-file-list="true"
      :headers="headers"
      :file-list="fileList"
      :on-preview="handlePictureCardPreview"
      :class="{ hide: fileList.length >= limit }"
    >
      <i class="el-icon-plus avatar-uploader-icon"></i>
    </el-upload>
    <!-- 上传提示 -->
    <div class="el-upload__tip" v-if="showTip && !disabled">
      请上传
      <template v-if="fileSize">
        大小不超过 <b style="color: #f56c6c">{{ fileSize }}MB</b>
      </template>
      <template v-if="fileType">
        格式为 <b style="color: #f56c6c">{{ fileType.join("/") }}</b>
      </template>
      的文件
    </div>

    <el-dialog
      :visible.sync="dialogVisible"
      title="预览"
      width="800px"
      append-to-body
    >
      <img
        :src="dialogImageUrl"
        style="display: block; max-width: 100%; margin: 0 auto"
      />
    </el-dialog>
  </div>
</template>

<script>
import store from '@/store'
import { Loading, Message } from 'element-ui'
import Sortable from 'sortablejs'
import { adminApiURI }  from '@/utils'
import Cookies from 'js-cookie'

export default {
  name: 'ImageUpload',
  props: {
    value: [String, Object, Array],
    // 上传接口地址
    action: {
      type: String,
      default: '/upload'
    },
    // 上传携带的参数
    data: {
      type: Object
    },
    // 图片数量限制
    limit: {
      type: Number,
      default: 5
    },
    // 大小限制(MB)
    fileSize: {
      type: Number,
      default: 5
    },
    // 文件类型, 例如['png', 'jpg', 'jpeg']
    fileType: {
      type: Array,
      default: () => ['png', 'jpg', 'jpeg']
    },
    // 是否显示提示
    isShowTip: {
      type: Boolean,
      default: true
    },
    // 禁用组件（仅查看图片）
    disabled: {
      type: Boolean,
      default: false
    },
    // 拖动排序
    drag: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      number: 0,
      uploadList: [],
      dialogImageUrl: '',
      dialogVisible: false,
      baseUrl: store.state.site.domain,
      uploadImgUrl: adminApiURI + this.action,
      headers: { 'x-xsrf-token': Cookies.get('XSRF-TOKEN') },
      fileList: []
    }
  },
  computed: {
    showTip() {
      return this.isShowTip && (this.fileType || this.fileSize)
    }
  },
  mounted() {
    // 初始化拖拽排序
    if (this.drag && !this.disabled) {
      this.$nextTick(() => {
        const element = this.$refs.imageUpload.$el.querySelector('.el-upload-list')
        if (element) {
          Sortable.create(element, {
            onEnd: (evt) => {
              const movedItem = this.fileList.splice(evt.oldIndex, 1)[0]
              this.fileList.splice(evt.newIndex, 0, movedItem)
              this.$emit('input', this.listToString(this.fileList))
            }
          })
        }
      })
    }
  },
  methods: {
    // 上传前loading加载
    handleBeforeUpload(file) {
      let isImg = false
      if (this.fileType.length) {
        let fileExtension = ''
        if (file.name.lastIndexOf('.') > -1) {
          fileExtension = file.name.slice(file.name.lastIndexOf('.') + 1)
        }
        isImg = this.fileType.some(type => {
          if (file.type.indexOf(type) > -1) return true
          if (fileExtension && fileExtension.indexOf(type) > -1) return true
          return false
        })
      } else {
        isImg = file.type.indexOf('image') > -1
      }
      if (!isImg) {
        Message.error(`文件格式不正确，请上传${this.fileType.join('/')}图片格式文件!`)
        return false
      }
      if (file.name.includes(',')) {
        Message.error('文件名不正确，不能包含英文逗号!')
        return false
      }
      if (this.fileSize) {
        const isLt = file.size / 1024 / 1024 < this.fileSize
        if (!isLt) {
          Message.error(`上传头像图片大小不能超过 ${this.fileSize} MB!`)
          return false
        }
      }
      Loading.service({ text: '正在上传图片，请稍候...' })
      this.number++
    },
    // 文件个数超出
    handleExceed() {
      Message.error(`上传文件数量不能超过 ${this.limit} 个!`)
    },
    // 上传成功回调
    handleUploadSuccess(res, file) {
      if (res.status === 200) {
        this.uploadList.push({ name: res.result.name, url: res.result.url })
        this.uploadedSuccessfully()
      } else {
        this.number--
        Loading.service().close()
        Message.error(res.message)
        this.$refs.imageUpload.handleRemove(file)
        this.uploadedSuccessfully()
      }
    },
    // 删除图片
    handleDelete(file) {
      const findex = this.fileList.map(f => f.name).indexOf(file.name)
      if (findex > -1 && this.uploadList.length === this.number) {
        this.fileList.splice(findex, 1)
        this.$emit('input', this.listToString(this.fileList))
        return false
      }
    },
    // 上传结束处理
    uploadedSuccessfully() {
      if (this.number > 0 && this.uploadList.length === this.number) {
        this.fileList = this.fileList.filter(f => f.url !== undefined).concat(this.uploadList)
        this.uploadList = []
        this.number = 0
        this.$emit('input', this.listToString(this.fileList))
        Loading.service().close()
      }
    },
    // 上传失败
    handleUploadError() {
      Message.error('上传图片失败')
      Loading.service().close()
    },
    // 预览
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },
    // 对象转成指定字符串分隔
    listToString(list, separator) {
      let strs = ''
      separator = separator || ','
      for (let i in list) {
        if (undefined !== list[i].url && list[i].url.indexOf('blob:') !== 0 && !list[i].url.startsWith('http')) {
          strs += list[i].url.replace(this.baseUrl, '') + separator
        }
      }
      return strs !== '' ? strs.substr(0, strs.length - 1) : ''
    }
  }
}
</script>

<style scoped lang="scss">
// .el-upload--picture-card 控制加号部分
:deep(.hide .el-upload--picture-card) {
    display: none;
}

:deep(.el-upload.el-upload--picture-card.is-disabled) {
  display: none !important;
}
</style>