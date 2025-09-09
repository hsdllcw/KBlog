<template>
  <el-container>
    <el-aside width="200px">
      <el-tree
        :data="categoryData"
        :props="defaultProps"
        @node-click="handleNodeClick"
        default-expand-all
      ></el-tree>
    </el-aside>
    <el-container>
      <el-header>
        <el-row>
          <el-col :span="6">
            <el-form
              :model="searchFormData"
              ref="searchForm"
              label-width="80px"
            >
              <el-form-item label="标题" prop="search_LIKE_title">
                <el-input
                  v-model="searchFormData[`search_LIKE_title`]"
                ></el-input>
              </el-form-item>
            </el-form>
          </el-col>
          <el-col :span="6">
            <el-button
              type="primary"
              icon="el-icon-search"
              @click="search"
            ></el-button>
          </el-col>
          <el-col :span="12" style="text-align: right">
            <el-button
              type="primary"
              icon="el-icon-document-add"
              @click="createPage"
            ></el-button>
            <el-button-group>
              <el-button
                type="primary"
                icon="el-icon-edit"
                @click="handleEdit"
              ></el-button>
              <!-- <el-button type="primary" icon="el-icon-share" @click="search"></el-button> -->
              <el-button
                type="primary"
                icon="el-icon-delete"
                @click="handleDelete"
              ></el-button>
            </el-button-group>
          </el-col>
        </el-row>
      </el-header>
      <el-main>
        <el-table
          :data="pageData['content']"
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column label="ID" prop="id" width="55"></el-table-column>
          <el-table-column label="标题" prop="title">
            <template slot-scope="scope">
              <el-link :href="baseUrl + scope.row.uri" type="primary" target="_blank">{{ scope.row.title }}</el-link>
            </template>
          </el-table-column>
          <el-table-column label="作者" prop="author" width="80"></el-table-column>
          <el-table-column
            label="发布时间"
            prop="publishDate"
          ></el-table-column>
          <el-table-column
            label="状态"
            prop="status"
            width="55"
          ></el-table-column>
          <el-table-column label="操作" align="center" width="350">
            <template slot-scope="scope">
              <el-popover
                placement="top"
                title="博客页面地址"
                width="200"
                trigger="click"
                :content="baseUrl + scope.row.uri">
                <el-button icon="el-icon-paperclip" size="mini" slot="reference" @click="copyLink(baseUrl + scope.row.uri)">复制地址</el-button>
              </el-popover>
              <el-button
                icon="el-icon-edit"
                size="mini"
                @click="handleEdit(scope.$index, scope.row)"
                >编辑文章</el-button
              >
              <el-button
                icon="el-icon-delete"
                size="mini"
                type="danger"
                @click="handleDelete(scope.$index, scope.row)"
                >删除文章</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-main>
      <el-footer>
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageData['number']"
          :page-sizes="[10, 50, 100, 500]"
          :page-size="pageData['size']"
          layout="prev, pager, next, sizes, total"
          :total="pageData['totalElements']"
        ></el-pagination>
      </el-footer>
    </el-container>
  </el-container>
</template>

<script>
import store from '@/store'
import Page from '@/api/page'
import Category from '@/api/category'

export default {
  data() {
    return {
      page: new Page(),
      category: new Category(),
      searchFormData: {},
      selectData: [],
      categoryData: [],
      defaultProps: {
        children: `children`,
        label: `name`
      },
      pageData: [],
      number: 0,
      size: 10,
      baseUrl: store.getters['site/baseUrl']
    }
  },
  methods: {
    search() {
      this._getList(Object.assign(this.searchFormData))
    },
    _getList(searchData) {
      this.page.getList(searchData, this.number, this.size).then((result) => {
        this.pageData = result[`data`][`result`]
      })
    },
    handleEdit(index, row) {
      if (
        (row !== null && row) ||
        ((row === null || !row) && this.selectData.length === 1)
      ) {
        this.$router.push({
          name: `UpdatePage`,
          params: { pageData: row || this.selectData[0] }
        })
      } else if (this.selectData.length > 0) {
        this.$message({
          showClose: true,
          message: `最多选择一项`,
          type: `error`
        })
      }
    },
    handleDelete(index, row) {
      var ids = []
      if (
        (row !== null && row) ||
        ((row === null || !row) && this.selectData.length === 1)
      ) {
        ids.push(row.id)
      } else if (this.selectData.length === 0) {
        this.$message({
          showClose: true,
          message: `最少选择一项`,
          type: `error`
        })
      } else {
        ids.push.apply(
          ids,
          this.selectData.map((item) => item.id)
        )
      }
      this.page.deleteById(ids).then(() => {
        this._getList()
      })
    },
    handleNodeClick(data) {
      let getCategoryIds = (tempCategoryDate) => {
        let categoryIdArray = []
        if (tempCategoryDate.children && tempCategoryDate.children.length > 0) {
          for (const children of tempCategoryDate.children) {
            categoryIdArray.push.apply(
              categoryIdArray,
              getCategoryIds(children)
            )
          }
        }
        categoryIdArray.push(tempCategoryDate.id)
        return categoryIdArray
      }
      this.searchFormData['search_IN_category.id'] = getCategoryIds(data)
      this._getList(this.searchFormData)
    },
    handleSelectionChange(data) {
      this.selectData = data
    },
    handleSizeChange(data) {
      this.size = data
      this._getList()
    },
    handleCurrentChange(data) {
      this.number = data
      this._getList()
    },
    createPage() {
      this.$router.push({ name: `CreatePage` })
    },
    copyLink(link) {
      navigator.clipboard.writeText(link)
      this.$message({
        message: '链接已复制到剪贴板',
        type: 'success'
      })
    }
  },
  mounted() {
    this.category.getList({ search_EQ_sign: [`root`] }).then((result) => {
      this.categoryData = result[`data`][`result`][`content`]
    })
    this._getList()
  }
}
</script>