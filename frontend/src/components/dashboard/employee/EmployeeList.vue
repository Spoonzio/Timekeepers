<template>
  <el-row v-loading="loading">
    <el-breadcrumb separator="/" style="margin-bottom: 5px">
      <el-breadcrumb-item :to="{ path: '/dashboard/employeeList' }"
        >Employee Management</el-breadcrumb-item
      >
      <el-breadcrumb-item>Employees</el-breadcrumb-item>
    </el-breadcrumb>

    <el-row v-if="showAll" style="margin-bottom: 5px; text-align: right">
      <router-link to="editEmployee/0">
        <el-button type="primary">New Employee</el-button>
      </router-link>
    </el-row>
    <el-row v-bind:style=" showAll ? '' : 'margin-top: 40px;' ">
      <el-table :data="employees" border>
        <el-table-column prop="employeeId" label="ID"> </el-table-column>

        <el-table-column prop="lastName" label="Last Name"> </el-table-column>

        <el-table-column prop="firstName" label="First Name"> </el-table-column>

        <el-table-column prop="role" label="Title"> </el-table-column>

        <el-table-column prop="supervisor.firstName" label="Supervisor">
        </el-table-column>

        <el-table-column prop="approver.firstName" label="Approver">
        </el-table-column>

        <el-table-column v-if="showAll" prop="paygradeEntity.grade" label="Pay Level">
        </el-table-column>

        <el-table-column v-if="showAll" prop="paygradeEntity.rate" label="Pay Rate">
        </el-table-column>

        <el-table-column fixed="right" width="80" label="Action">
          <template slot-scope="scope">
            <router-link
              :to="{
                name: 'EditEmployee',
                params: { id: scope.row.employeeId },
              }"
              style="text-decoration: none; color: blue"
            >
              <el-button type="text" size="small">Edit</el-button>
            </router-link>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page.sync="currentPage"
        :page-size="20"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        style="margin-top:10px; text-align: center"
      >
      </el-pagination>
    </el-row>
  </el-row>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      loading: true,
      currentPage: 1,
      employees: [],
      total: 0,
      page: 0,
      size: 20,
      user: null,
      showAll: false
    };
  },
  methods: {
    
    async loadData() {
      let token = JSON.parse(this.$cookie.get("user")).token;
      
      if(!token){
        this.$message.error("Token expired");
        this.$router.push("/");
      }

      let paramsData = {
        page: this.page,
        size: this.size,
      };

      if (this.user.roles.includes("Administrator") || this.user.roles.includes("Human Resource")){
        this.showAll = true;
      } else {
        this.showAll = false;
        paramsData.supervisorId = this.user.employeeId;
      };

      try {
        let result = await this.$axios({
          method: "GET",
          url: this.$constants.usersUrl,
          headers: {
            token,
          },
          params: paramsData,
        });

        this.loading = false;
        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        this.employees = result.data.data.list;
        this.total = result.data.data.total;

        console.log(this.employees);
      } catch (error) {
        this.$message.error(error);
        console.log(error);
      }
    },
    handleSizeChange(val) {
      this.size = val;
      this.loadData();
    },
    handleCurrentChange(val) {
      this.page = val -1;
      this.loadData();
    },
  },

  mounted() {
    let cookie = this.$cookie.get("user");
    if (!cookie) {
      this.$message.error("Token expired");
      this.$router.push({ name: "Login" });
      return;
    }
    this.user = JSON.parse(cookie);
    this.loadData();
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.el-col {
  min-height: 1px;
}
</style>
