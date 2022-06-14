<template>
  <el-row justify="center" align="middle">
    <!-- <el-alert
      title="Reuse the variables/code as much as possible, try not to copy them from here to there and then overthere. Improve your code quality, make it clean. Check all components in Element-UI library to see if there is an existing example for you before you create a page. There is no need to use your customized components for this project. If you want to use a form and validate it, the login page will be an example. If you want to use a data table, the employee list page will be another example."
      type="error"
      effect="dark"
    >
    </el-alert> -->
    <router-view />
  </el-row>
</template>

<script>
export default {
  name: "App",
  mounted() {
    if (!JSON.parse(this.$cookie.get("user"))) {
      //this.$message.error("Token expired");
      this.$router.push("/");
    }
  }
};
</script>

<style>
#app {
  font-family: "Helvetica Neue",Helvetica,"PingFang SC","Hiragino Sans GB","Microsoft YaHei","微软雅黑",Arial,sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  width: 100vw;
  min-height: 100vh;
}

html,
body {
  margin: 0;
  background-color: #f5f7fa;
}

.el-menu {
  border-right: 0;
}
.el-textarea .el-textarea__inner {
  resize: none;
}
</style>
