<template>
  <el-row v-loading="loading">
    <el-breadcrumb separator="/" style="margin-bottom: 5px">
      <el-breadcrumb-item :to="{ path: '/dashboard/mytimesheet' }"
        >Timesheet</el-breadcrumb-item
      >
      <el-breadcrumb-item>Timesheet Approve</el-breadcrumb-item>
    </el-breadcrumb>

    <el-row style="margin-top: 20px;">
      <el-table :data="timesheets" border>
        <el-table-column prop="employeeId" label="Employee ID" width="150">
        </el-table-column>

        <el-table-column prop="employeeFullName" label="Employee Name">
        </el-table-column>

        <el-table-column prop="totalHours" label="Total"> </el-table-column>
        <el-table-column prop="weekEnding" label="Date"> </el-table-column>
        <el-table-column prop="isApproved" label="Status"> </el-table-column>

        <el-table-column fixed="right" width="80" label="Action">
          <template slot-scope="scope">
            <router-link
              :to="{
                name: 'ViewTimesheet',
                params: {
                  employeeId: scope.row.employeeId,
                  endDate: scope.row.weekEnding
                },
                query: { approverView: 1}
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
        style="margin-top: 10px; text-align: center"
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
      size: 20
    };
  },
  methods: {
    async loadData() {
      let cookie = JSON.parse(this.$cookie.get("user"));
      let token = cookie.token;
      console.log(cookie);
      let loggedInId = cookie.employeeId;

      try {
        let result = await this.$axios({
          method: "GET",
          url: `${this.$constants.timesheetUrl}?approverId=${loggedInId}`,
          headers: {
            token
          },
          params: {
            page: this.page,
            size: this.size
          }
        });

        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        this.timesheets = result.data.data;
        // this.timesheets.forEach(x => {
        //   let total = 0;
        //   x.timesheetrows.forEach(y => total += y.total);
        //   x["total"] = total;
        // })

        console.log(this.timesheets);
        this.loading = false;
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
      this.page = val - 1;
      this.loadData();
    }
  },

  mounted() {
    let cookie = this.$cookie.get("user");
    if (!cookie) {
      this.$message.error("Token expired");
      this.$router.push({ name: "Login" });
      return;
    }
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
