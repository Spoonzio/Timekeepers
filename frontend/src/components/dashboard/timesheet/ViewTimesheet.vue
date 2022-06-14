<template>
  <el-row v-loading="loading">
    <el-breadcrumb separator="/" style="margin-bottom: 5px">
      <el-breadcrumb-item :to="{ path: approverView ? '/dashboard/timesheetapprove' : '/dashboard/mytimesheet' }"
        >Timesheet</el-breadcrumb-item
      >
      <el-breadcrumb-item>View Timesheet</el-breadcrumb-item>
    </el-breadcrumb>

    <el-row  style="margin-top: 25px" :gutter="20" class="title_row" align="middle">
      <el-col :span="4" class="title_text"> Status: </el-col>
      <el-col :span="8" class="title_info">
        {{ form.isApproved }}
      </el-col>
      <el-col :span="4" class="title_text"> Week End: </el-col>
      <el-col :span="8" class="title_info">
        {{ week }}
      </el-col>
    </el-row>

    <el-row style="margin-top: 15px">
      <el-form label-width="10vw" :model="form">
        <el-table :data="rows" border>
          <el-table-column prop="projectId" label="Project">
            <template slot-scope="scope">
              <span>{{ scope.row.projectId }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="workPackageId" label="WP">
            <template slot-scope="scope">
              <span>{{ scope.row.workPackageId }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="mon" label="Mon">
            <template slot-scope="scope">
              <span>{{ scope.row.mon }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="tue" label="Tue">
            <template slot-scope="scope">
              <span>{{ scope.row.tue }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="wed" label="Wed">
            <template slot-scope="scope">
              <span>{{ scope.row.wed }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="thu" label="Thu">
            <template slot-scope="scope">
              <span>{{ scope.row.thu }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="fri" label="Fri">
            <template slot-scope="scope">
              <span>{{ scope.row.fri }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="sat" label="Sat">
            <template slot-scope="scope">
              <span>{{ scope.row.sat }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="sun" label="Sun">
            <template slot-scope="scope">
              <span>{{ scope.row.sun }}</span>
            </template>
          </el-table-column>
        </el-table>
        <el-row style="margin-top: 15px">
          <el-col :span="7">
            <el-form-item label="Over Time:" prop="overTime" size="small">
              <el-input v-model="form.overTime" :disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="Flex Time:" prop="flexTime" size="small">
              <el-input v-model="form.flexTime" :disabled="true"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row v-if="isApproveMode">
          <el-button type="primary" @click="handleSubmit(true)">
            Approve
          </el-button>
          <el-button type="primary" @click="handleSubmit(false)">
            Reject
          </el-button>
        </el-row>
      </el-form>
    </el-row>
  </el-row>
</template>

<script>
export default {
  name: "ViewTimesheet",
  data() {
    return {
      user: null,
      token: null,
      loading: true,
      isApproveMode: false,
      approverView: false,
      rows: [],
      form: {
        isApproved: "",
        flexTime: 0,
        overTime: 0
      },
      week: new Date().toISOString().slice(0, 10),
      employeeId: null,
      pickerOptions: {
        firstDayOfWeek: 1
      }
    };
  },
  methods: {
    async loadData() {
      try {
        this.loading = true;
        this.employeeId = this.$route.params.employeeId;
        this.week = this.$route.params.endDate;

        let result = await this.$axios({
          method: "GET",
          url:
            this.$constants.timesheetUrl +
            `?employeeId=${this.employeeId}&endDate=${this.week}`,
          headers: {
            token: this.token
          }
        });

        this.loading = false;
        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        let rowData = result.data.data;
        if (rowData.length > 0) {
          this.form.isApproved = rowData[0].isApproved;
          this.form.flexTime = rowData[0].flexTimeUsed;
          this.form.overTime = rowData[0].overtime;
          this.form.employeeId = rowData[0].employeeId;

          if (
            this.form.isApproved == "submitted" &&
            this.user.role == "Administrator" && this.approverView
          ) {
            this.isApproveMode = true;
          } else if (
            (this.form.isApproved == "submitted"  &&
            rowData[0].approverId !== this.user.employeeId) ||
              this.form.isApproved == "approved" ||
              this.form.isApproved == "unapproved"
          ) {
            this.isApproveMode = false;
          } else {
            this.isApproveMode = true;
          }
        }

        if (rowData.length > 0 && rowData[0].timesheetrows.length > 0) {
          this.rows = rowData[0].timesheetrows;
        }
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    },
    handleSubmit: async function(approved) {
      try {
        this.loading = true;

        let value = approved
          ? { isApproved: "approved" }
          : { isApproved: "unapproved" };

        let result = await this.$axios({
          method: "PUT",
          url:
            this.$constants.timesheetUrl +
            `/${this.form.employeeId}/${this.week}`,
          headers: {
            token: this.token
          },
          data: value
        });

        this.loading = false;
        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        this.$router.push({ path: "/dashboard/timesheetapprove" });
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    }
  },

  mounted() {
    let cookie = this.$cookie.get("user");
    this.approverView = this.$route.query.approverView == 1 ? true : false;
    if (!cookie) {
      this.$message.error("Token expired");
      this.$router.push({ name: "Login" });
      return;
    }
    this.user = JSON.parse(cookie);
    this.token = this.user.token;
    this.loadData();
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.el-col {
  min-height: 1px;
}

.title_text {
  color: #000000;
  text-align: right;
  vertical-align: bottom;
}

.title_info {
  vertical-align: bottom;
}
</style>
