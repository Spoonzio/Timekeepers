<template>
  <el-row v-loading="loading">
    <el-breadcrumb separator="/" style="margin-bottom: 20px">
      <el-breadcrumb-item :to="{ path: '/dashboard/changepw' }">Profile</el-breadcrumb-item>
      <el-breadcrumb-item>Change Password</el-breadcrumb-item>
    </el-breadcrumb>

    <el-form :model="ruleForm" status-icon :rules="rules" ref="ruleForm" label-width="120px" class="demo-ruleForm" :hide-required-asterisk="true">
      <el-form-item label="Old Password" prop="oldPass">
        <el-input v-model.number="ruleForm.oldPass" autocomplete="off" type="password"></el-input>
      </el-form-item>
      <el-form-item label="Password" prop="pass">
        <el-input type="password" v-model="ruleForm.pass" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="Confirm" prop="checkPass">
        <el-input type="password" v-model="ruleForm.checkPass" autocomplete="off"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm('ruleForm')">Submit</el-button>
        <el-button @click="resetForm('ruleForm')">Reset</el-button>
      </el-form-item>
    </el-form>
  </el-row>
</template>
<script>
export default {
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("Please input the password"));
      } else {
        if (this.ruleForm.checkPass !== "") {
          this.$refs.ruleForm.validateField("checkPass");
        }
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("Please input the password again"));
      } else if (value !== this.ruleForm.pass) {
        callback(new Error("Two inputs don't match!"));
      } else {
        callback();
      }
    };
    return {
      loading: false,
      user: null,
      token: null,
      ruleForm: {
        pass: "",
        checkPass: ""
      },
      rules: {
        pass: [{ validator: validatePass, trigger: "blur" }],
        checkPass: [{ validator: validatePass2, trigger: "blur" }],
        oldPass: [
          {
            required: true,
            message: "Old password is required",
            trigger: "blur"
          }
        ],
      }
    };
  },
  methods: {
    async handleSubmit() {
      try {
        this.loading = true;

        let data = {
          oldPassword: this.ruleForm.oldPass,
          newPassword: this.ruleForm.pass
        };

        let result = await this.$axios({
          method: "PUT",
          url: this.$constants.usersUrl + "/password/" + this.user.employeeId,
          headers: {
            token: this.token
          },
          data: data
        });

        this.loading = false;
        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        this.resetForm("ruleForm");
        this.$message.success("Your password has been updated");
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.handleSubmit();
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    }
  },
  mounted() {
    let cookie = this.$cookie.get("user");
    if (!cookie) {
      this.$message.error("Token expired");
      this.$router.push({ name: "Login" });
      return;
    }
    this.user = JSON.parse(cookie);
    this.token = this.user.token;
  }
};
</script>
