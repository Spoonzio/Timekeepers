<template>
  <el-row v-loading="loading">
    <el-row>
      <el-breadcrumb separator="/" style="margin-bottom: 5px">
        <el-breadcrumb-item :to="{ path: '/dashboard/reports/all' }">Report Management</el-breadcrumb-item>
        <el-breadcrumb-item>All Reports</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>

    <el-row>
      <el-col>
        <el-table element-loading-text="Loading..." element-loading-spinner="el-icon-loading" element-loading-background="rgba(0, 0, 0, 0.8)" :data="allProjectData" border style="width: 100%; margin-top: 20px">
          <el-table-column prop="projectId" label="Project Name"> </el-table-column>
          <el-table-column prop="rate" label="Weekly Report" fixed="right" width="150">
            <template slot-scope="scope">
              <el-button type="text" @click="viewReport(scope.row,1)">View</el-button>
            </template>
          </el-table-column>
          <el-table-column prop="rate" label="Monthly Report" fixed="right" width="150">
            <template slot-scope="scope">
              <el-button type="text" @click="viewReport(scope.row,2)">View</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="pagination.currentPage" :page-sizes="pagination.sizes" :page-size="pagination.currentSize" layout="total, sizes, prev, pager, next, jumper" :total="pagination.total" style="margin-top: 10px; text-align: center">
    </el-pagination>
    <el-dialog  v-loading="loading" :title="dialogTitleText" :visible.sync="dialogVisible" width="40%" :before-close="handleClose">
      <el-date-picker v-model="dialogReportDateSelect" format="yyyy-MM-dd" value-format="yyyy-MM-dd" type="date" size="small" placeholder="Select Date" v-if="!loading" align="right" @change="selectDateChange()">
      </el-date-picker>
      <el-table height="300" element-loading-text="Loading..." element-loading-spinner="el-icon-loading" element-loading-background="rgba(0, 0, 0, 0.8)" :data="dialogData" border style="width: 100%; margin-top: 20px">
        <el-table-column prop="reportDate" label="Date"> </el-table-column>
        <el-table-column prop="rate" label="Detail" fixed="right" width="150">
          <template slot-scope="scope">
            <el-button type="text" @click="viewWeeklyDetail(scope.row)">View</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </el-row>
</template>

<script>
export default {
  name: "AllReports",
  data() {
    return {
      dialogTitleText: "",

      //查看所有报告的表格 加载图标控制变量
      loading: false,

      //所有项目数据
      allProjectData: [],

      //周报数据 --用于页面渲染
      dialogData: [],

      //周报临时数据 -- 用于选择日期筛选
      dialogDataCopy: [],

      //选择周报日期变量
      dialogReportDateSelect: null,

      //周报的项目ID
      dialogProjecId: null,

      //分页控制变快了
      pagination: {
        currentPage: 1,
        currentSize: 20,
        sizes: [20, 40, 60, 80, 100],
        total: 0,
      },
      //当前登录用户信息
      userInfo: null,

      //控制模态框弹出变量
      dialogVisible: false
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
    this.userInfo = JSON.parse(cookie)

    // 获取所有项目数据
    this.getAllProject();
  },
  methods: {
    // 获取所有项目数据
    getAllProject: async function () {
      try {
        this.loading = true;
        let res = await this.$axios({
          method: "GET",
          url: "api/projectpackage",
          headers: {
            token: this.userInfo.token,
          },
          data: {
            employeeId: this.userInfo.employeeId,
            page: this.pagination.currentPage,
            size: this.pagination.currentSize
          }
        });

        if (res.data.code != 0) {
          this.$message.error(res.data.message);
          return;
        }
        this.allProjectData = res.data.data;

        this.loading = false;
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    },

    //查看项目的周报
    viewReport: async function (row, index) {
      this.dialogVisible = true;
      this.loading = true;
      let baseUrl = "";
      switch (parseInt(index)) {
        case 1:
          this.dialogTitleText = "WeeklyReport"
          baseUrl = "api/report/weekly?projectId=";
          break;
        case 2:
          this.dialogTitleText = "MonthlyReport"
          baseUrl = "api/report/monthly?projectId=";
          break;
        default:
          break;
      }
      try {
        this.loading = true;
        this.dialogProjecId = row.projectId
        let res = await this.$axios({
          method: "GET",
          url: baseUrl + row.projectId,
          headers: {
            token: this.userInfo.token,
          },
        });

        if (res.data.code != 0) {
          this.$message.error(res.data.message);
          this.dialogVisible = false;
          return;
        }
        switch (parseInt(index)) {
          case 1:
            this.dialogData = this.disposeData(res.data.data.reports)
            this.dialogDataCopy = this.disposeData(res.data.data.reports)
            break;
          case 2:
            this.dialogData = this.disposeData(res.data.data.reports)
            this.dialogDataCopy = this.disposeData(res.data.data.reports)
            break;
          default:
            break;
        }
        this.loading = false;
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.dialogVisible = false;
        this.loading = false;
      }
    },

    //选择日期 
    selectDateChange: async function (row) {
      // 如果选择的日期为空，也就是清空选择框内容，重新显示项目的所有报表
      // 否则循环比较，匹配到就显示那一条数据，没匹配到就显示没数据
      if (this.dialogReportDateSelect == null) {
        this.dialogData = this.dialogDataCopy
      } else {
        for (let i = 0; i < this.dialogDataCopy.length; i++) {
          if (this.dialogReportDateSelect == this.dialogDataCopy[i].reportDate) {
            this.dialogData = [{ reportDate: this.dialogReportDateSelect }]
            return
          }
        }
        this.dialogData = []
      }
    },
    // 查看周报详情（跳转）
    viewWeeklyDetail(row) {
      switch (this.dialogTitleText) {
        case "WeeklyReport":
          this.$router.push({ name: "WeeklyReport", query: { projectId: this.dialogProjecId, date: row.reportDate } },);
          break;
        case "MonthlyReport":
          this.$router.push({ name: "MonthlyReports", query: { projectId: this.dialogProjecId, date: row.reportDate } },);
          break;
        default:
          break;
      }
    },
    // 分页每页展示数量变化
    handleSizeChange(val) {
      this.pagination.currentSize = val;
      this.pagination.currentPage = 1;
      this.getAllProject();
    },

    //分页页数变化
    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.getAllProject();
    },

    //周报模态框关闭事件
    handleClose(done) {
      done();
      this.dialogData = [];
      this.dialogProjecId = null;
      this.dialogReportDateSelect = null;
    },
    //处理数据，报表列表搞成json数组好渲染[{reportDate:"data"}]
    disposeData(data) {
      let retData = [];
      if (data.length == 0) {
        return []
      } else {
        switch (this.dialogTitleText) {
          case "WeeklyReport":
            for (const key in data) {
              if (Object.hasOwnProperty.call(data, key)) {
                if (data[key].length == 0) {
                  return []
                } else {
                  retData.push({ reportDate: data[key] })
                }

              }
            }
            break;
          case "MonthlyReport":
            for (const key in data) {
              if (Object.hasOwnProperty.call(data, key)) {
                if (data[key].length == 0) {
                  return []
                } else {
                  retData.push({ reportDate: data[key].date })
                }
              }
            }
            break;
          default:
            break;
        }

        return retData
      }
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.el-col {
  min-height: 1px;
}
.el-date-editor {
  position: absolute;
  top: 55px;
  right: 20px;
}
</style>
