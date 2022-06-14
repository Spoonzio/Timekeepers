<template>
  <el-row v-loading="loading">
    <el-breadcrumb
      separator-class="el-icon-arrow-right"
      style="margin-bottom: 20px"
    >
      <el-breadcrumb-item :to="{ path: '/dashboard/project' }"
        >Project</el-breadcrumb-item
      >
      <el-breadcrumb-item v-if="isNewProject">New Project</el-breadcrumb-item>
      <el-breadcrumb-item v-else-if="isNewProject === false">{{
        isWorkPackage ? "Work Package Details" : "Project Details"
      }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-form
      label-width="20vw"
      :model="form"
      :rules="rules"
      ref="ruleForm"
      :hide-required-asterisk="true"
    >
      <el-form-item
        v-if="isWorkPackage === false"
        label="Project Name"
        prop="projectId"
      >
        <el-input v-model="form.projectId"></el-input>
      </el-form-item>

      <el-form-item
        v-else-if="isWorkPackage"
        label="Work Package Name"
        prop="workPackageId"
      >
        <el-input v-model="form.workPackageId"></el-input>
      </el-form-item>

      <el-form-item :label="projectManagerLabel" prop="projectManager">
        <el-select v-model="form.managerId" filterable clearable>
          <el-option
            v-for="item in employees"
            :key="item.employeeId"
            :label="employeesLabel(item)"
            :value="item.employeeId"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item
        v-if="isWorkPackage === false"
        label="Project Assistant"
        prop="projectAssistant"
      >
        <el-select v-model="form.assistantId" filterable clearable>
          <el-option
            v-for="item in employees"
            :key="item.employeeId"
            :label="employeesLabel(item)"
            :value="item.employeeId"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item
        v-if="isNewProject === false && isWorkPackage === false"
        label="Status"
        prop="status"
      >
        <el-select v-model="form.isOpen" filterable>
          <el-option
            v-for="(item, index) in statusOptions"
            :key="index"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>

       <el-divider>Cost (Est) in Person Day</el-divider>

      <el-form-item
        v-for="(item, index) in payRates"
        :label="item.name"
        prop=""
        :key="index"
        :item="item"
      >
        <el-input-number
          v-model="item.val"
          :precision="1"
          :step="0.1"
        ></el-input-number>
      </el-form-item>

      <el-button
        v-if="isNewProject"
        type="primary"
        style="width: 100%"
        @click="submitForm('ruleForm')"
        >Create
      </el-button>

      <el-button
        v-else-if="isNewProject === false"
        type="primary"
        style="width: 100%"
        @click="submitForm('ruleForm')"
        >{{ isWorkPackage ? "Create" : "Save" }}
      </el-button>
    </el-form>
  </el-row>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      currentWorkPackageId: null,
      payRates: [],
      form: {
        projectId: null,
        workPackageId: null,
        managerId: null,
        isOpen: true,
        employees: [],
        parentWorkPackageId: null,
        assistantId: null,
        personDays: {},
      },
      loading: false,
      statusOptions: [
        {
          label: "In progress",
          value: true,
        },
        {
          label: "Closed",
          value: false,
        },
      ],
      employees: [],
      isWorkPackage: false,
      isNewProject: true,
      isCreateForm: true,
      token: "",
      projectManagerLabel: "Responsible Engineer",
      rules: {
        projectId: [
          {
            required: true,
            message: "Project Name is required",
            trigger: "blur",
          },
        ],
        workPackageId: [
          {
            required: true,
            message: "Work package is required",
            trigger: "blur",
          },
        ],
        managerId: [
          {
            required: true,
            message: "Manager is required",
            trigger: "blur",
          },
        ],
        isOpen: [
          {
            required: true,
            message: "Status is required",
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
    getProjectIdFromUrl: function () {
      let createWP = this.$route.query.createWP;
      this.currentProjectId = this.$route.params.id;
      this.currentWorkPackageId = this.$route.query.workPackageId;

      if (createWP == 1) {
        this.form.projectId = this.$route.params.id;
        this.form.parentWorkPackageId = this.$route.query.workPackageId;
        this.isNewProject = false;
        this.isWorkPackage = true;
        this.projectManagerLabel = "Responsible Engineer";
        this.isCreateForm = true;
      } else if (this.currentProjectId != 0) {
        this.isNewProject = false;
        this.isCreateForm = false;
      }
    },
    getCurrentProject: async function () {
      try {
        this.loading = true;
        let result = await this.$axios({
          method: "GET",
          url:
            this.$constants.projectUrl +
            "/" +
            this.currentProjectId +
            "/" +
            this.currentWorkPackageId,
          headers: {
            token: this.token,
          },
        });

        for (let key in result.data.data.personDays) {
          for (let i = 0; i < this.payRates.length; i++) {
            if(key == this.payRates[i].name){
              this.payRates[i].val = result.data.data.personDays[key]
            }
          }
        }

        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        let project = result.data.data;
        this.form = project;

        this.form.isOpen = project.open;
        this.form.employees = project.employeeEntities.map((e) => e.employeeId);
        this.form.parentWorkPackageId = project.parentWorkPackageId;

        if (project.projectId != project.workPackageId) {
          this.isNewProject = false;
          this.isWorkPackage = true;
        }

        this.loading = false;
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    },
    getEmployees: async function () {
      try {
        this.loading = true;
        let url = this.$constants.usersUrl;

        if (
          this.isWorkPackage ||
          (this.currentProjectId != this.currentWorkPackageId &&
            this.currentProjectId != 0)
        ) {
          url = this.$constants.projectUrl + "/" + this.currentProjectId;
          this.isNewProject = false;
          this.isWorkPackage = true;
        }

        let result = await this.$axios({
          method: "GET",
          url: url,
          headers: {
            token: this.token,
          },
        });

        this.loading = false;

        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        if (this.isWorkPackage) {
          this.employees = result.data.data.employeeEntities;
        } else {
          this.employees = result.data.data.list;
        }
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    },
    handleSave: async function () {
      try {
        this.loading = true;

        for (let i = 0; i < this.payRates.length; i++) {
          this.form.personDays[this.payRates[i].name] = this.payRates[i].val;
        }

        //check if it is not a work package
        if (!this.isWorkPackage) {
          this.form.workPackageId = this.form.projectId;
        }

        let url = this.$constants.projectUrl;
        if (!this.isCreateForm && this.currentProjectId != 0) {
          url += "/" + this.currentProjectId + "/" + this.currentWorkPackageId;
        }

        if (
          this.isCreateForm ||
          !this.employees.includes(this.form.managerId)
        ) {
          this.form.employees.push(this.form.managerId);
        }

        if (
          this.form.assistantId != null &&
          !this.employees.includes(this.form.assistantId)
        ) {
          this.form.employees.push(this.form.assistantId);
        }

        let result = await this.$axios({
          method: this.isCreateForm ? "POST" : "PUT",
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

        this.$router.push({ path: "/dashboard/project" });
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
    loadPayRate: async function () {
      this.loading = true;
      let token = JSON.parse(this.$cookie.get("user")).token;

      try {
        let result = await this.$axios({
          method: "GET",
          url: this.$constants.payRateUrl + "?year=" + new Date().getFullYear(),
          headers: {
            token,
          },
        });

        this.loading = false;

        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        let payRates = result.data.data;
        payRates.forEach((payrate) => {
          let obj = {
            name: payrate.grade,
            val: 0,
          };
          this.payRates.push(obj);
        });
      } catch (error) {
        this.$message.error(error);
        this.loading = false;
        console.log(error);
      }
    },
  },
  async mounted() {
    let cookie = this.$cookie.get("user");
    if (!cookie) {
      this.$message.error("Token expired");
      this.$router.push({ name: "Login" });
      return;
    }
    this.token = JSON.parse(this.$cookie.get("user")).token;
    this.getProjectIdFromUrl();
    await this.loadPayRate();
    if (!this.isCreateForm) {
      this.getCurrentProject();
    }
    this.getEmployees();
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.el-select {
  width: 100%;
}
</style>
