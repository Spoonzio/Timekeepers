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
        <el-breadcrumb-item>Weekly Reports</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>

    <el-card class="box-card">
      <el-row :gutter="20" class="title_row">
        <el-col :span="4" class="title_text">Project ID:</el-col>
        <el-col :span="8" class="title_info">
          {{ projectId }}
        </el-col>
        <el-col :span="4" class="title_text">Project Name:</el-col>
        <el-col :span="8" class="title_info">
          {{ projectId }}
        </el-col>
      </el-row>
      <el-row :gutter="20" class="title_row">
        <el-col :span="4" class="title_text">Report Date:</el-col>
        <el-col :span="8" class="title_info">
          {{ reportDate }}
        </el-col>
        <el-col :span="4" class="title_text">Report Period:</el-col>
        <el-col :span="8" class="title_info">
          {{ reportPeriod }}
        </el-col>
      </el-row>
    </el-card>

    <el-card class="box-card" v-for="(item, index) in pageData" :key="index">
      <!--Desc-->
      <el-descriptions class="margin-top" :column="2" border>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-user"></i>
            Work Package ID
          </template>
          {{ item.WPName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-user"></i>
            Work Package Name
          </template>
          {{ item.WPName }}
        </el-descriptions-item>
      </el-descriptions>

      <el-table
          :span-method="objectSpanMethod"
          v-loading="loading"
          :render-header="labelHead"
          element-loading-text="Loading..."
          element-loading-spinner="el-icon-loading"
          element-loading-background="rgba(0, 0, 0, 0.8)"
          :data="item.detail"
          border
          style="width: 100%"
        >
          <el-table-column prop="EmployeeId" label="Name" width="100">
          </el-table-column>
          <el-table-column
            :label="item2"
            v-for="(item2, index2) in item.header"
            :key="index2"
            align="center"
            width="150"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.WeekSpent[index2].spent }}</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="thisWeekSpent"
            label="This Week Total Hours"
          ></el-table-column>
          <el-table-column
            prop="total"
            label=" Total Spent Hours"
          ></el-table-column>
          <el-table-column
            prop="Planned"
            label="Planed Hours"
            width="120"
            align="center"
            fixed="right"
          >
            <template>
              <span style="text-align: center">{{ item.Planned }}</span>
            </template>
          </el-table-column>
        </el-table>
    </el-card>

  </el-row>
</template>

<script>
export default {
  name: "AllReports",
  data() {
    return {
      loading: false,
      //页面数据 多表格
      pageData: [],

      projectId: "",
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
    this.reportDate = this.$route.query.date;

    // 获取周报数据
    this.getWeeklyReport();
  },
  methods: {
    // 获取周报数据
    getWeeklyReport: async function () {
      try {
        this.loading = true;
        let res = await this.$axios({
          method: "GET",
          url:
            "api/report/weekly?date=" +
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
        this.projectId = res.data.data.projectId;
        this.reportDate = res.data.data.reportDate;
        this.reportPeriod = res.data.data.reportPeriod;
        console.log(res.data.data);
        this.pageData = this.disposeData(res.data.data.reports);

        this.loading = false;
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    },
    //动态表头渲染
    labelHead(h, { column, index }) {
      return h("span", { class: "table-head", style: { width: "100%" } }, [
        column.label,
      ]);
    },
    // 表格合并
    objectSpanMethod({ row, column, rowIndex, columnIndex }) {
      if (column.label === "Planed Hours") {
        if (rowIndex % row.rowSpan === 0) {
          return {
            rowspan: row.rowSpan,
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
    // 处理数据
    disposeData(data) {
      let retData = [];
      let dataInfo = {
        WPName: "",
        WPID: "",
        header: [],
        detail: [],
        Planned: 0,
      };
      let detailInfo = {
        EmployeeId: "",
        WeekSpent: [],
        thisWeekSpent: 0,
        Planned: 0,
        total: 0,
        rowSpan: 0,
        colSpan: 0,
        tableIndex: 0,
      };
      if (data.length == 0) {
        return [];
      } else {
        for (const key in data) {
          if (Object.hasOwnProperty.call(data, key)) {
            for (let i = 0; i < data[key].length; i++) {
              if (i == 0) {
                dataInfo.WPName = key;
                dataInfo.WPID = key;
                for (let k = 0; k < data[key][i].length; k++) {
                  if (data[key][i][k].indexOf("-") != -1) {
                    dataInfo.header.push(data[key][i][k]);
                  }
                }
              } else {
                for (let j = 0; j < data[key][i].length; j++) {
                  if (j == 0) {
                    detailInfo.EmployeeId = data[key][i][j];
                  } else {
                    if (data[key][0][j].indexOf("-") != -1) {
                      if (data[key][i][j] == "") {
                        detailInfo.WeekSpent.push({ spent: 0 });
                      } else {
                        detailInfo.WeekSpent.push({
                          spent: parseFloat(data[key][i][j]),
                        });
                      }
                    } else {
                      if (data[key][0][j] == "Total") {
                        if (data[key][i][j] == "") {
                          detailInfo.total = 0;
                        } else {
                          detailInfo.total = parseFloat(data[key][i][j]);
                        }
                      } else if (data[key][0][j] == "Planned Hours") {
                        if (data[key][i][j] == "") {
                          detailInfo.Planned = 0;
                          dataInfo.Planned += 0;
                        } else {
                          detailInfo.Planned = parseFloat(data[key][i][j]);
                          dataInfo.Planned += parseFloat(data[key][i][j]);
                        }
                      }
                    }
                  }
                }
                detailInfo.thisWeekSpent =
                  detailInfo.WeekSpent[detailInfo.WeekSpent.length - 1].spent;
                detailInfo.rowSpan = data[key].length - 1;
                detailInfo.colSpan = data[key][0].length;
                detailInfo.tableIndex = i - 1;
                dataInfo.detail.push(detailInfo);

                detailInfo = {
                  EmployeeId: "",
                  WeekSpent: [],
                  thisWeekSpent: 0,
                  Planned: 0,
                  total: 0,
                  rowSpan: 0,
                  colSpan: 0,
                  tableIndex: 0,
                };
              }
            }
          }
          retData.push(dataInfo);

          dataInfo = {
            WPName: "",
            WPID: "",
            detail: [],
            header: [],
            Planned: 0,
          };
        }
        return retData;
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
