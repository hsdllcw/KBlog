<template>
  <div class="homepage-container">
    <h1 class="eva-h1">
      <i class="u2"></i>工作台
    </h1>
    <div class="index-tip">
      <p class="user eva-p">
        <i class="u8"></i>
        <span class="welcome-user">Admin</span>
      </p>
      <p class="eva-p">您好，欢迎来到Kblog后台</p>
    </div>
    <div class="index-cotent">
      <h2 class="eva-h2">
        <i class="u14"></i>系统信息
      </h2>
      <div class="row cl">
        <el-col :span="12">
          <p class="eva-p">系统名称: Kblog</p>
          <p class="eva-p">系统版本: 1.0</p>
          <p class="eva-p">JAVA版本: 1.9.0_91</p>
          <p class="eva-p">操作系统名称: CentOS 7.4</p>
        </el-col>
        <el-col :span="12">
          <p class="eva-p">Servlet信息: Apache Tomcat/9.0.12</p>
          <p class="eva-p">Servlet版本: 2.5</p>
          <p class="eva-p">JAVA路径: /home/root/local/jdk/jre</p>
        </el-col>
      </div>
    </div>
  </div>
</template>
<script>
import('@/assets/custom-theme/back-index.scss')
export default {
  data() {
    return {}
  },
  methods: {},
  created() {},
  mounted() {},
  updated() {}
}
</script>