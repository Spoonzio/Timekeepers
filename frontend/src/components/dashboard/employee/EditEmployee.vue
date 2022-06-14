<template>
  <el-row v-loading="loading">
    <el-breadcrumb
      separator-class="el-icon-arrow-right"
      style="margin-bottom: 20px"
    >
      <el-breadcrumb-item :to="{ path: '/dashboard/employeeList' }"
        >Employee</el-breadcrumb-item
      >
      <el-breadcrumb-item :to="{ path: '/dashboard/employeeList' }"
        >List</el-breadcrumb-item
      >
      <el-breadcrumb-item>Employee Detail</el-breadcrumb-item>
    </el-breadcrumb>

    <el-form
      label-width="10vw"
      :model="form"
      :rules="rules"
      ref="ruleForm"
      :hide-required-asterisk="true"
    >
      <el-form-item label="ID" prop="employeeId">
        <el-input
          v-model="form.employeeId"
          placeholder="Please Input the Employee ID"
          :disabled="! showAll"
        ></el-input>
      </el-form-item>

      <el-form-item v-if="showAll" label="Username" prop="userName">
        <el-input
          v-model="form.userName"
          placeholder="Please Input the UserName"
        ></el-input>
      </el-form-item>

      <el-form-item v-if="showAll" label="Password" prop="password">
        <el-input
          v-model="form.password"
          type="password"
          :placeholder="passwordPlaceholder"
          show-password
        ></el-input>
      </el-form-item>

      <el-form-item label="Last Name" prop="lastName">
        <el-input
          v-model="form.lastName"
          placeholder="Please Input the Last Name"
          :disabled="! showAll"
        ></el-input>
      </el-form-item>

      <el-form-item label="First Name" prop="firstName">
        <el-input
          v-model="form.firstName"
          placeholder="Please Input the First Name"
          :disabled="! showAll"
        ></el-input>
      </el-form-item>

      <el-form-item v-if="showAll" label="Role" prop="role">
        <el-select
          v-model="form.role"
          filterable
          placeholder="Please Choose the Role"
        >
          <el-option
            v-for="(item, index) in roleOptions"
            :key="index"
            :label="item"
            :value="item"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item v-if="showAll" label="Pay Rate" prop="paygrade">
        <el-select
          v-model="form.paygrade"
          filterable
          placeholder="Please Choose the Pay Rate"
        >
          <el-option
            v-for="(item, index) in payGradeOptions"
            :key="index"
            :label="item.grade"
            :value="item.grade"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="Supervisor" prop="supervisor">
        <el-select
          v-model="form.supervisor"
          filterable
          clearable
          placeholder="Please Choose the Supervisor"
          :disabled="! showAll"
        >
          <el-option
            v-for="item in employees"
            :key="item.employeeId"
            :label="employeesLabel(item)"
            :value="item.employeeId"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="Approver" prop="approver">
        <el-select
          v-model="form.approver"
          filterable
          placeholder="Please Choose the timesheet approver"
        >
          <el-option
            v-for="item in employees"
            :key="item.employeeId"
            :label="employeesLabel(item)"
            :value="item.employeeId"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="Disabled" prop="disabled">
        <el-switch
          v-model="form.disabled"
          :active-value="true"
          :inactive-value="false"
          :disabled="! showAll"
        >
        </el-switch>
      </el-form-item>

      <el-button
        type="primary"
        style="width: 100%"
        @click="submitForm('ruleForm')"
      >
        Save
      </el-button>
    </el-form>
  </el-row>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      currentEmployeeId: 1,
      user: null,
      showAll: false,
      passwordPlaceholder: "Please Input The Password",
      form: {
        employeeId: null,
        userName: "",
        password: null,
        lastName: "",
        firstName: "",
        paygrade: null,
        supervisor: null,
        role: null,
        approver: null,
        disabled: false,
      },
      loading: false,
      roleOptions: [],
      employees: [],
      payGradeOptions: [],
      token: "",
      rules: {
        employeeId: [
          {
            required: true,
            message: "Employee ID is required",
            trigger: "blur",
          },
        ],
        userName: [
          {
            required: true,
            message: "Username is required",
            trigger: "blur",
          },
        ],
        password: [
          {
            required: true,
            message: "Password is required",
            trigger: "blur",
          },
        ],
        lastName: [
          {
            required: true,
            message: "Last name is required",
            trigger: "blur",
          },
        ],
        firstName: [
          {
            required: true,
            message: "First name is required",
            trigger: "blur",
          },
        ],
        paygrade: [
          {
            required: true,
            message: "Pay Level is required",
            trigger: "blur",
          },
        ],
        role: [
          {
            required: true,
            message: "Role is required",
            trigger: "blur",
          },
        ],
        approver: [
          {
            required: true,
            message: "Timesheet Approver is required",
            trigger: "blur",
          },
        ],
      },
    };
  },
  methods: {
    employeesLabel: function (item) {
      return item.firstName + " " + item.lastName;
    },
    getEmployeeIdFromUrl: function () {
      this.currentEmployeeId = this.$route.params.id;
    },
    getCurrentEmployee: async function () {
      try {
        this.loading = true;
        let result = await this.$axios({
          method: "GET",
          url: this.$constants.usersUrl + "/" + this.currentEmployeeId,
          headers: {
            token: this.token,
          },
        });

        this.loading = false;
        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        let employee = result.data.data;

        this.form.employeeId = employee.employeeId;
        this.form.firstName = employee.firstName;
        this.form.lastName = employee.lastName;
        this.form.userName = employee.credentialEntity.userName;
        this.form.role = employee.role;
        this.form.supervisor = employee.supervisor ? employee.supervisor.employeeId : null;
        this.form.paygrade = employee.paygradeEntity.grade;
        this.form.approver = employee.approver ? employee.approver.employeeId : null;
        this.form.disabled = employee.disabled;
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    },
    getEmployees: async function () {
      try {
        this.loading = true;
        let result = await this.$axios({
          method: "GET",
          url: this.$constants.usersUrl,
          headers: {
            token: this.token,
          },
        });

        this.loading = false;

        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }
        this.employees = result.data.data.list.filter(
          (emp) => emp.employeeId != this.currentEmployeeId
        );
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    },
    getPayGrade: async function () {
      try {
        this.loading = true;
        let result = await this.$axios({
          method: "GET",
          url: this.$constants.payRateUrl,
          headers: {
            token: this.token,
          },
        });

        this.loading = false;
        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        this.payGradeOptions = result.data.data;
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    },
    getRoles: async function () {
      try {
        this.loading = true;
        let result = await this.$axios({
          method: "GET",
          url: this.$constants.usersUrl + "/role",
          headers: {
            token: this.token,
          },
        });

        this.loading = false;
        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        this.roleOptions = result.data.data;
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    },
    handleSave: async function () {
      try {
        this.loading = true;
        let url = this.$constants.usersUrl;
        if (this.currentEmployeeId != 0) {
          url += "/" + this.currentEmployeeId;
        }

        if (!this.form.password || this.form.password == " " || this.form.password.length < 1){
          this.form.password = null;
        }

        let result = await this.$axios({
          method: this.currentEmployeeId == 0 ? "POST" : "PUT",
          url: url,
          headers: {
            token: this.token,
          },
          data: this.form,
        });

        this.loading = false;
        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        this.$router.push({ path: "/dashboard/employeeList" });
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.handleSave();
        } else {
          return false;
        }
      });
    },
  },
  mounted() {
    let cookie = this.$cookie.get("user");
    if (!cookie) {
      this.$message.error("Token expired");
      this.$router.push({ name: "Login" });
      return;
    }

    this.user = JSON.parse(this.$cookie.get("user"));
    this.token = this.user.token;

    if (this.user.roles.includes("Administrator") || this.user.roles.includes("Human Resource")){
      this.showAll = true;
    } else {
      this.showAll = false;
    };

    this.getEmployeeIdFromUrl();
    if (this.currentEmployeeId != 0) {
      this.passwordPlaceholder = "Please input the new password if needed";
      this.rules.password = [
          {
            required: false,
          },
        ],
      this.getCurrentEmployee();
    }

    this.getEmployees();

    if (this.showAll){
      this.getPayGrade();
      this.getRoles();
    }
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.el-select {
  width: 100%;
}
</style>
