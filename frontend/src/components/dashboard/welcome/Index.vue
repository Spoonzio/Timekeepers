<template>
  <el-row>
    <el-breadcrumb separator="/" style="margin-bottom: 5px">
      <el-breadcrumb-item :to="{ path: '/dashboard/welcome' }"
        >Welcome</el-breadcrumb-item
      >
      <el-breadcrumb-item>Index</el-breadcrumb-item>
    </el-breadcrumb>

    <el-row style="margin-top: 15vh">
      <center><img :src="images.welcome" width="300"/></center>
    </el-row>
    <center><h2 style="color: #c0c0c0;">Welcome to Project Management System</h2></center>
  </el-row>
</template>

<script>
import WelcomeImage from "@/assets/welcome.png";
export default {
  name: "Login",
  data() {
    return {
      images: {
        welcome: WelcomeImage,
      },
    };
  },
  methods: {
    handleLogin: function () {},
  },
  mounted(){
    let cookie = this.$cookie.get("user");
    if (!cookie) {
      this.$message.error("Token expired");
      this.$router.push({ name: "Login" });
      return;
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.el-col {
  min-height: 1px;
}
</style>
