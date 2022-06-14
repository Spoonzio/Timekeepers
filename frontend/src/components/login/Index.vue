<template>
  <el-row class="form container" v-loading="loading">
    <el-col :span="8" :offset="8">
      <el-card class="box-card">
        <div slot="header" class="clearfix" style="text-align: center">
          <h2>Time Keeper</h2>
        </div>
        <el-form
          label-width="80px"
          :model="form"
          :rules="rules"
          ref="ruleForm"
          :hide-required-asterisk="true"
        >
          <el-form-item label="Username" prop="username">
            <el-input
              v-model="form.username"
              placeholder="Please Input Your UserName"
            ></el-input>
          </el-form-item>
          <el-form-item label="Password" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="Please Input Your Password"
              show-password
            ></el-input>
          </el-form-item>
          <el-row>
            <el-button
              type="primary"
              @click="submitForm('ruleForm')"
              style="width: 100%"
              >Sign In</el-button
            >
          </el-row>
        </el-form>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      loading: false,
      form: {
        username: "",
        password: "",
      },
      rules: {
        username: [
          {
            required: true,
            message: "Please Input Your UserName",
            trigger: "blur",
          },
        ],
        password: [
          {
            required: true,
            message: "Please Input Your Password",
            trigger: "blur",
          },
        ],
      },
    };
  },
  methods: {
    async Login() {
      try {
        this.loading = true;
        let result = await this.$axios({
          url: this.$constants.loginUrl,
          method: "post",
          data: this.form,
        });

        this.loading = false;

        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        this.$cookie.set("user", JSON.stringify(result.data.data), {
          expires: "30d",
        });
        // console.log(result.data.data);
        this.$router.push({ name: "DashBoard" });
      } catch (error) {
        this.loading = false;
        console.log(error);
      }
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.Login();
        } else {
          return false;
        }
      });
    },
  },
  mounted() {},
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.el-col {
  min-height: 1px;
}

.form {
  padding-top: 25vh;
}

.container {
    min-height: 100vh !important;
    background-size: 100% 100%;
    background-image: url("../../assets/login.jpg");
  }
</style>
