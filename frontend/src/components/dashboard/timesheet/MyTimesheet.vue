<template>
  <el-row v-loading="loading">
    <el-breadcrumb separator="/" style="margin-bottom: 5px">
      <el-breadcrumb-item :to="{ path: '/dashboard/mytimesheet' }"
        >My Timesheet</el-breadcrumb-item
      >
    </el-breadcrumb>

    <el-row style="margin-bottom: 5px; text-align: right">
      <router-link to="addTimesheet/0">
        <el-button type="primary">Add Timesheet</el-button>
      </router-link>
    </el-row>
    <el-row style="margin-top: 20px;">
      <el-table :data="timesheets" border>
        <el-table-column prop="weekEnding" label="Date"> </el-table-column>

        <el-table-column prop="totalHours" label="Total">
        </el-table-column>

        <el-table-column prop="isApproved" label="Status"> </el-table-column>

        <el-table-column fixed="right" width="80" label="Action">
          <template slot-scope="scope">
            <router-link
              v-if="checkTimesheetSubmit(scope.row.isApproved)"
              :to="{
                name: 'AddTimesheet',
                params: { id: scope.row.employeeId },
                query: { endDate: scope.row.weekEnding }
              }"
              style="text-decoration: none; color: blue"
            >
              <el-button type="text" size="small">Edit</el-button>
            </router-link>
            <router-link
              v-else
              :to="{
                name: 'ViewTimesheet',
                params: {
                  employeeId: scope.row.employeeId,
                  endDate: scope.row.weekEnding
                }
              }"
              style="text-decoration: none; color: blue"
            >
              <el-button type="text" size="small">View</el-button>
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
      timesheets: [],
      total: 0,
      page: 0,
      size: 20,
      user: null
    };
  },
  methods: {
    async loadData() {
      let token = JSON.parse(this.$cookie.get("user")).token;

      if (!token) {
        this.$message.error("Token expired");
        this.$router.push("/");
      }

      try {
        let result = await this.$axios({
          method: "GET",
          url:
            this.$constants.timesheetUrl +
            `?employeeId=${this.user.employeeId}`,
          headers: {
            token
          },
          params: {
            page: this.page,
            size: this.size
          }
        });

        this.loading = false;
        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        this.timesheets = result.data.data;
        this.total = result.data.data.total;
      } catch (error) {
        this.$message.error(error);
        console.log(error);
      }
    },
    handleSizeChange(val) {
      this.size = val;
      this.loadData();
      //console.log(`${val} Record for each page`);
    },
    handleCurrentChange(val) {
      this.page = val - 1;
      this.loadData();
      //console.log(`current page: ${val}`);
    },
    checkTimesheetSubmit(isApproved) {
      if (isApproved == "submitted" || isApproved == "approved") {
        return false;
      } else {
        return true;
      }
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
    this.loadData();
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.el-col {
  min-height: 1px;
}
</style>
