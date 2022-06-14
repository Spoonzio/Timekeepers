<template>
  <el-row v-loading="loading">
    <el-row style="margin-bottom: 30px">
      <el-breadcrumb separator="/" style="margin-bottom: 5px">
        <el-breadcrumb-item :to="{ path: '/dashboard/reports/all' }"
          >Report Management</el-breadcrumb-item
        >
        <el-breadcrumb-item :to="{ path: '/dashboard/reports/all' }"
          >All Reports</el-breadcrumb-item
        >
        <el-breadcrumb-item>Monthly Reports</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>

    <el-card class="box-card">
      <el-row :gutter="20" class="title_row">
        <el-col :span="4" class="title_text"> Project ID: </el-col>
        <el-col :span="8" class="title_info">
          {{ projectId }}
        </el-col>
        <el-col :span="4" class="title_text"> Project Name: </el-col>
        <el-col :span="8" class="title_info">
          {{ projectName }}
        </el-col>
      </el-row>
      <el-row :gutter="20" class="title_row">
        <el-col :span="4" class="title_text"> Report Date: </el-col>
        <el-col :span="8" class="title_info">
          {{ reportDate }}
        </el-col>
        <el-col :span="4" class="title_text"> Report Period: </el-col>
        <el-col :span="8" class="title_info">
          {{ reportPeriod }}
        </el-col>
      </el-row>
    </el-card>

    <el-card class="box-card" v-for="(report, index) in tableData" :key="index">
      <!--Desc-->
      <el-descriptions class="margin-top" :column="2" border>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-user"></i>
            Work Package
          </template>
          {{ report.WorkPackageName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-mobile-phone"></i>
            Completion
          </template>
          {{ report.Completion }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-location-outline"></i>
            Comment
          </template>
          <span v-html="report.Comments"></span>
        </el-descriptions-item>
      </el-descriptions>

      <!--table-->
      <el-table :data="report.Rows" border style="width: 100%">
        <el-table-column prop="Name" label="Name"> </el-table-column>
        <el-table-column prop="BCWS" label="BCWS"> </el-table-column>
        <el-table-column prop="EngPlanned" label="EngPlanned">
        </el-table-column>
        <el-table-column prop="ACWP" label="ACWP"> </el-table-column>
        <el-table-column prop="EAC" label="EAC"> </el-table-column>
        <el-table-column prop="Variance" label="Variance"> </el-table-column>
      </el-table>
    </el-card>
  </el-row>
</template>

<script>
export default {
  name: "AllReports",
  data() {
    return {
      //查看所有报告的表格 加载图标控制变量
      loading: false,

      //表数据
      tableData: [],

      projectId: "",
      projectName: "",
      reportDate: "",
      reportPeriod: "",
    };
  },
  watch: {},

  mounted() {
    let cookie = this.$cookie.get("user");
    if (!cookie) {
      this.$message.error("Token expired");
      this.$router.push({ name: "Login" });
      return;
    }

    // 获取用户信息
    this.userInfo = JSON.parse(cookie);

    this.projectId = this.$route.query.projectId;
    this.reportDate = this.$route.query.date.toString();
    this.projectName = this.projectId;

    // 获取所有项目数据
    this.getMouthlyReport();
  },
  methods: {
    // 获取所有项目数据
    getMouthlyReport: async function () {
      try {
        this.loading = true;
        let res = await this.$axios({
          method: "GET",
          url:
            "api/report/monthly?date=" +
            this.reportDate +
            "&projectId=" +
            this.projectId,
          headers: {
            token: this.userInfo.token,
          },
        });
        if (res.data.code != 0) {
          this.$message.error(res.data.message);
          return;
        }

        console.log(res.data);

        this.projectId = res.data.data.projectId;
        this.reportDate = res.data.data.reportDate;
        this.reportPeriod = res.data.data.reportPeriod;
        this.tableData = res.data.data.reports;
        this.loading = false;
      } catch (error) {
        this.$message.error(error);

        this.loading = false;
      }
    },

    //动态表头渲染
    labelHead(h, { column, index }) {
      return h("span", { class: "table-head", style: { width: "100%" } }, [
        column.label,
      ]);
    },

    //表格合并单元格
    objectSpanMethod({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 0) {
        if (rowIndex % 3 === 0) {
          return {
            rowspan: 3,
            colspan: 1,
          };
        } else {
          return {
            rowspan: 0,
            colspan: 0,
          };
        }
      }
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.el-col {
  min-height: 1px;
}
.title_row {
  margin: 10px 0;
}
.title_text {
  color: #000000;
  text-align: right;
  vertical-align: bottom;
}
.title_info {
  vertical-align: bottom;
}
.report_box {
  border: 1px solid #cacaca;
  background-color: #5f5f5e;
}
.report_title {
  width: 100%;
  height: 50px;
  line-height: 50px;
  /* border-bottom: 1px solid #eeeeee; */
  padding: 0 15px;
  background-color: #f2f6fc;
}

.box-card{
  margin-bottom: 15px;
}
</style>
