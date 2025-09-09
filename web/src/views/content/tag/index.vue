<template>
  <el-container>
    <el-header>
      <el-row>
        <el-col :span="6">
          <el-form :model="searchFormData" ref="searchForm" label-width="80px">
            <el-form-item label="标签名" prop="search_LIKE_name">
              <el-input v-model="searchFormData[`search_LIKE_name`]"></el-input>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" icon="el-icon-search" @click="search"></el-button>
        </el-col>
        <el-col :span="12" style="text-align: right">
          <el-button type="primary" icon="el-icon-document-add" @click="createTag"></el-button>
          <el-button-group>
            <el-button type="primary" icon="el-icon-edit" @click="handleEdit"></el-button>
            <!-- <el-button type="primary" icon="el-icon-share" @click="search"></el-button> -->
            <el-button type="primary" icon="el-icon-delete" @click="handleDelete"></el-button>
          </el-button-group>
        </el-col>
      </el-row>
    </el-header>
    <el-main>
      <el-table
        :data="tagData['content']"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column label="ID" prop="id" width="55"></el-table-column>
        <el-table-column label="名称" prop="name"></el-table-column>
        <el-table-column label="标识" prop="sign"></el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-main>
    <el-footer>
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="tagData['number']"
        :page-sizes="[10, 50, 100, 500]"
        :page-size="tagData['size']"
        layout="prev, pager, next, sizes, total"
        :total="tagData['totalElements']"
      ></el-pagination>
    </el-footer>
  </el-container>
</template>

<script>
import Tag from '@/api/tag'

export default {
  data() {
    return {
      tag: new Tag(),
      searchFormData: {},
      selectData: [],
      tagData: [],
      defaultProps: {
        children: `children`,
        label: `name`
      },
      number: 0,
      size: 10
    }
  },
  methods: {
    search() {
      this._getList(this.searchFormData)
    },
    _getList(searchData) {
      this.tag.getList(searchData, this.number, this.size).then(result => {
        this.tagData = result[`data`][`result`]
      })
    },
    handleEdit(index, row) {
      if (
        (row !== null && row) ||
        ((row === null || !row) && this.selectData.length === 1)
      ) {
        this.$router.push({
          name: `UpdateTag`,
          query: { id: (row || this.selectData[0]).id }
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
      this.tag.deleteById(row.id).then(() => {
        this._getList()
      })
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
    createTag() {
      this.$router.push({ name: `CreateTag` })
    }
  },
  mounted() {
    this.tag.getList({ search_EQ_sign: [`root`] }).then(result => {
      this.tagTreeData = result[`data`][`result`][`content`]
    })
    this._getList()
  }
}
</script>