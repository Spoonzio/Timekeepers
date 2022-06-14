<template>
  <el-container>
    <el-aside width="260px">
      <el-header class="logo">Project Management System</el-header>
      <el-menu
        default-active="1"
        class="aside"
        @open="handleOpen"
        @close="handleClose"
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b"
      >
        <router-link to="/dashboard/welcome" style="text-decoration: none">
          <el-menu-item index="1">
            <i class="el-icon-s-home"></i>
            <span>Welcome</span>
          </el-menu-item>
        </router-link>

        <router-link to="/dashboard/project" style="text-decoration: none">
          <el-menu-item index="2"
            ><template slot="title">
              <i class="el-icon-s-order"></i>
              <span>Project Management</span>
            </template></el-menu-item
          >
        </router-link>

        <el-submenu
          index="3"
          v-if="
            user.role == 'Administrator' ||
              user.role == 'Human Resource' ||
              isSupervisor
          "
        >
          <template slot="title">
            <i class="el-icon-s-custom"></i>
            <span>Employee Management</span>
          </template>

          <router-link
            to="/dashboard/employeeList"
            style="text-decoration: none"
          >
            <el-menu-item index="3-1">Employees</el-menu-item>
          </router-link>

          <router-link
            to="/dashboard/editPayRate"
            style="text-decoration: none"
            v-if="user.role == 'Administrator' || user.role == 'Human Resource'"
          >
            <el-menu-item index="3-2">Pay Rates</el-menu-item>
          </router-link>
        </el-submenu>

        <el-submenu index="4">
          <template slot="title">
            <i class="el-icon-s-claim"></i>
            <span>Timesheet Management</span>
          </template>

          <router-link
            to="/dashboard/mytimesheet"
            style="text-decoration: none"
          >
            <el-menu-item index="4-2">My Timesheets</el-menu-item>
          </router-link>

          <router-link
            to="/dashboard/timesheetapprove"
            style="text-decoration: none"
            v-if="isApprover"
          >
            <el-menu-item index="4-1">Approve Timesheets</el-menu-item>
          </router-link>
        </el-submenu>

        <el-submenu
          index="6"
          v-if="user.role == 'Administrator' || showReportManagement"
        >
          <template slot="title">
            <i class="el-icon-document"></i>
            <span>Report Management</span>
          </template>

          <router-link
            to="/dashboard/reports/all"
            style="text-decoration: none"
            v-if="user.role == 'Administrator' ||  isPM"
          >
            <el-menu-item index="6-1">All Report List</el-menu-item>
          </router-link>

          <router-link
            to="/dashboard/reports/MonthlyList"
            style="text-decoration: none"
          >
            <el-menu-item index="6-2">Monthly Report List</el-menu-item>
          </router-link>
        </el-submenu>

        <router-link to="/dashboard/changepw" style="text-decoration: none">
          <el-menu-item index="5"
            ><template slot="title">
              <i class="el-icon-key"></i>
              <span>Change Password</span>
            </template></el-menu-item
          >
        </router-link>

        <el-menu-item index="99" @click="handleLogout">
          <i class="el-icon-magic-stick"></i>
          <span>Logout</span>
        </el-menu-item>
      </el-menu></el-aside
    >
    <el-main class="main"><router-view /></el-main>
  </el-container>
</template>

<script>
export default {
  name: "DashBoard",
  data() {
    return {
      active: "1",
      user: {},
      isSupervisor: false,
      isApprover: false,
      showReportManagement: false,
      isPM: false
    };
  },
  methods: {
    handleOpen(key, keyPath) {
      console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      console.log(key, keyPath);
    },
    handleLogout() {
      this.$cookie.delete("user");
      this.$router.push({ name: "Login" });
    }
  },
  mounted() {
    let cookie = this.$cookie.get("user");
    console.log(JSON.parse(cookie));
    if (!cookie) {
      this.$message.error("Token expired");
      this.$router.push({ name: "Login" });
      return;
    }
    this.user = JSON.parse(cookie);
    this.isSupervisor = this.user.roles.includes("Supervisor");
    this.isApprover = this.user.roles.includes("Timesheet Approver");
    this.isPM = this.user.roles.includes("Project Manager");
    if (
      this.user.role == "Employee" &&
      (this.user.roles.includes("Responsible Engineer") || this.isPM)
    ) {
      this.showReportManagement = true;
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.logo {
  line-height: 50px;
  padding-left: 15px;
  height: 50px;
  color: white;
  text-align: center;
  background-color: #545c64;
}

.aside {
  min-height: calc(100vh - 60px);
  height: calc(100% - 60px);
}
</style>
