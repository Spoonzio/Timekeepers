<template>
  <el-row v-loading="loading">
    <el-row>
      <el-breadcrumb separator="/" style="margin-bottom: 5px">
        <el-breadcrumb-item :to="{ path: '/dashboard/employeeList' }"
          >Employee Management</el-breadcrumb-item
        >
        <el-breadcrumb-item>Pay Rates</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>

    <el-row style="text-align: right">
      <el-date-picker v-model="year" type="year" placeholder="Choose a Year">
      </el-date-picker>
    </el-row>

    <el-row>
      <el-col>
        <el-table
          element-loading-text="Loading..."
          element-loading-spinner="el-icon-loading"
          element-loading-background="rgba(0, 0, 0, 0.8)"
          :data="tableData"
          border
          style="width: 100%; margin-top: 20px"
        >
          <el-table-column prop="grade" label="Pay Level"> </el-table-column>

          <el-table-column prop="rate" label="Pay Rate"> </el-table-column>
        </el-table>
      </el-col>
    </el-row>
  </el-row>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      loading: true,
      tableData: [],
      year: new Date(),
    };
  },
  methods: {
    initList: async function () {
      this.loading = true;
      let token = JSON.parse(this.$cookie.get("user")).token;

      try {
        let result = await this.$axios({
          method: "GET",
          url: this.$constants.payRateUrl + "?year=" + this.year.getFullYear(),
          headers: {
            token,
          },
        });

        this.loading = false;

        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        this.tableData = result.data.data;
      } catch (error) {
        this.$message.error(error);
        this.loading = false;
        console.log(error);
      }
    },
  },
  watch: {
    year: function (oldVal, newVal) {
      this.initList();
    },
  },
  mounted() {
    let cookie = this.$cookie.get("user");
    if (!cookie) {
      this.$message.error("Token expired");
      this.$router.push({ name: "Login" });
      return;
    }
    this.initList();
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.el-col {
  min-height: 1px;
}
</style>
