<template>
  <el-row v-loading="loading">
    <el-row>
      <el-breadcrumb separator="/" style="margin-bottom: 5px">
        <el-breadcrumb-item :to="{ path: '/dashboard/reports/MonthlyList' }"
          >Report Management</el-breadcrumb-item
        >
        <el-breadcrumb-item>Monthly Reports</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <el-row style="margin-bottom: 20px; text-align: right">
      <el-button type="primary" size="middle" class="btn" @click="addReport()"
        >New Report</el-button
      >
    </el-row>
    <el-row>
      <el-table
        element-loading-text="Loading..."
        element-loading-spinner="el-icon-loading"
        element-loading-background="rgba(0, 0, 0, 0.8)"
        :data="tableData"
        border
        style="width: 100%; text-align: center"
      >
        <el-table-column prop="projectId" label="Project"> </el-table-column>
        <el-table-column prop="workPackageId" label="Work Package">
        </el-table-column>
        <el-table-column prop="date" label="Report Date"> </el-table-column>
        <el-table-column
          label="Action"
          fixed="right"
          width="150"
          align="center"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="viewMonthlyReport(scope.row)"
              >View</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-row>

    <el-dialog
      :title="dialogTitleText"
      :visible.sync="dialogVisible"
      width="800px"
      :before-close="handleClose"
    >
      <el-row class="form">
        <el-row class="form_row">
          <el-col :span="6" class="form_title">Project Name</el-col>
          <el-col :span="6">
            <el-select
              v-model="form.projectId"
              placeholder="Select Project"
              @change="changeProjectName"
              size="small"
              :disabled="dialogState"
            >
              <el-option
                v-for="(item, index) in selectProjectName"
                :key="index"
                :label="item"
                :value="item"
              >
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="5" class="form_title"> WP Name</el-col>
          <el-col :span="6">
            <el-select
              v-model="form.workPackageId"
              placeholder="Select workPackage"
              size="small"
              :disabled="
                selectWPName.length == 0 || dialogState == true ? true : false
              "
            >
              <el-option
                v-for="(item, index) in selectWPName"
                :key="index"
                :label="item"
                :value="item"
              >
              </el-option>
            </el-select>
          </el-col>
        </el-row>
        <el-row class="form_row">
          <el-col :span="6" class="form_title">Report Date</el-col>
          <el-col :span="6">
            <el-date-picker
              v-model="form.reportPeriodFrom"
              type="date"
              placeholder="select Date"
              size="small"
              disabled
            >
            </el-date-picker>
          </el-col>
        </el-row>
        <el-row class="form_row">
          <el-col :span="6" class="form_title">Comments</el-col>
          <el-col :span="17">
            <el-input
              type="textarea"
              :rows="2"
              placeholder=""
              v-model="form.comments"
              :disabled="dialogState"
            >
            </el-input>
          </el-col>
        </el-row>
        <el-row class="form_row">
          <el-col :span="6" class="form_title"
            >Work Accomplished<br />This Period</el-col
          >
          <el-col :span="17">
            <el-input
              type="textarea"
              :rows="2"
              placeholder=""
              v-model="form.workAccomplished"
              :disabled="dialogState"
            >
            </el-input>
          </el-col>
        </el-row>
        <el-row class="form_row">
          <el-col :span="6" class="form_title"
            >Work Planned<br />Next Period</el-col
          >
          <el-col :span="17">
            <el-input
              type="textarea"
              :rows="2"
              placeholder=""
              v-model="form.workPlanned"
              :disabled="dialogState"
            >
            </el-input>
          </el-col>
        </el-row>
        <el-row class="form_row">
          <el-col :span="6" class="form_title">Problems This Period</el-col>
          <el-col :span="17">
            <el-input
              type="textarea"
              :rows="2"
              placeholder=""
              v-model="form.problemsFaced"
              :disabled="dialogState"
            >
            </el-input>
          </el-col>
        </el-row>
        <el-row class="form_row">
          <el-col :span="6" class="form_title">Problems Anticipated</el-col>
          <el-col :span="17">
            <el-input
              type="textarea"
              :rows="2"
              placeholder=""
              v-model="form.problemsAnticipated"
              :disabled="dialogState"
            >
            </el-input>
          </el-col>
        </el-row>

        <el-divider>Estimate</el-divider>
        <el-row
          class="form_row"
          v-for="(item, index) in payRatesEst"
          :key="'EST' + index"
          :item="item"
        >
          <el-col :span="6" class="form_title">{{ item.name }}</el-col>
          <el-col :span="17">
            <el-input-number
              v-model="item.val"
              :precision="1"
              :step="0.1"
              :disabled="dialogState"
              style="width: 100%"
            ></el-input-number>
          </el-col>
        </el-row>

        <el-divider>Engineer Planned</el-divider>
        <el-row
          class="form_row"
          v-for="(item, index) in payRatesRe"
          :key="index"
          :item="item"
        >
          <el-col :span="6" class="form_title">{{ item.name }}</el-col>
          <el-col :span="17">
            <el-input-number
              v-model="item.val"
              :precision="1"
              :step="0.1"
              :disabled="dialogState"
              style="width: 100%"
            ></el-input-number>
          </el-col>
        </el-row>
      </el-row>
      <div slot="footer" class="dialog-footer" v-if="!dialogState">
        <el-button @click="dialogVisible = false">Cancle</el-button>
        <el-button type="primary" @click="submitForm()">OK</el-button>
      </div>
    </el-dialog>
  </el-row>
</template>

<script>
export default {
  name: "AllReports",
  data() {
    return {
      //表格加载状态
      loading: false,

      //表格数据
      tableData: [],

      //下拉选择框的数据
      selectProjectName: [],

      //下拉选择框的数据 级联 WP
      selectWPName: [],

      //项目信息  用于下拉框索引数据
      projectInfo: [],

      //添加report表单
      payRatesEst: [],
      payRatesRe: [],
      form: {
        projectId: "",
        workPackageId: "",
        reportPeriodFrom: "",
        comments: "",
        workAccomplished: "",
        workPlanned: "",
        problemsFaced: "",
        problemsAnticipated: "",
        etc: {},
        engineerPlanned: {},
      },

      //控制模态框是否显示
      dialogVisible: false,

      //模态框标题
      dialogTitleText: "Edit Report",

      //控制模态框里的组件书否禁用 （false不禁用  true禁用）
      dialogState: false,
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

    // 获取所有项目数据
    this.getMonthlyList();
    this.getAllproject();
    this.loadPayRate();

    var myDate = new Date();
    this.form.reportPeriodFrom = myDate
      .toLocaleDateString()
      .split("/")
      .join("-");
  },
  methods: {
    getAllproject: async function () {
      try {
        this.loading = true;
        let res = await this.$axios({
          method: "GET",
          url: "api/projectpackage",
          headers: {
            token: this.userInfo.token,
          },
        });
        if (res.data.code != 0) {
          this.$message.error(res.data.message);
          return;
        }

        this.disposeData(res.data.data);
        this.disposeData2(res.data.data);

        this.loading = false;
      } catch (error) {
        this.$message.error(error);

        this.loading = false;
      }
    },

    // 获取月报列表
    getMonthlyList: async function () {
      try {
        this.loading = true;
        let res = await this.$axios({
          method: "GET",
          url: "api/report/RE",
          headers: {
            token: this.userInfo.token,
          },
        });
        if (res.data.code != 0) {
          this.$message.error(res.data.message);
          return;
        }

        this.tableData = res.data.data.reports;
        this.loading = false;
      } catch (error) {
        this.$message.error(error);

        this.loading = false;
      }
    },

    //处理数据 递归出所有的WPName
    disposeData(data) {
      let DataInfo = {
        projectName: "",
        wpName: "",
        reportDate: "",
      };
      for (let i = 0; i < data.length; i++) {
        DataInfo.projectName = data[i].projectId;
        DataInfo.wpName = data[i].workPackageId;
        this.projectInfo.push(DataInfo);
        DataInfo = {
          projectName: "",
          wpName: "",
        };
        if (data[i].childWorkPackages.length > 0) {
          this.disposeData(data[i].childWorkPackages);
        } else {
          continue;
        }
      }
      return;
    },

    //处理数据 生成project下拉框里需要的数据数组
    disposeData2(data) {
      for (let i = 0; i < data.length; i++) {
        this.selectProjectName.push(data[i].projectId);
      }
    },

    //点击查看月报
    viewMonthlyReport: async function (row) {
      this.loading = true;
      this.dialogTitleText = "View Report";

      try {
        let res = await this.$axios({
          method: "GET",
          url: "api/report/RE/" + row.projectId + "/" + row.workPackageId,
          params: {
            date: row.date,
          },
          headers: {
            token: this.userInfo.token,
          },
        });

        this.loading = false;
        if (res.data.code != 0) {
          this.$message.error(res.data.message);
          return;
        }
        this.form.projectId = res.data.data.projectId;
        this.form.workPackageId = res.data.data.workPackageId;
        this.form.reportPeriodFrom = res.data.data.reportDate;
        this.form.comments = res.data.data.comment;
        this.form.workAccomplished = res.data.data.workAccomplished;
        this.form.workPlanned = res.data.data.workPlanned;
        this.form.problemsFaced = res.data.data.problemFaced;
        this.form.problemsAnticipated = res.data.data.problemAnticipated;
        //this.form.etc = res.data.data.etc;
        //this.form.etc = res.data.data.etc;

        this.payRatesEst = [];
        for (let key in res.data.data.etc) {
          let obj = {
            name: key,
            val : res.data.data.etc[key]
          }
          this.payRatesEst.push(obj);
        } 
        
        this.payRatesRe = [];
        for (let key in res.data.data.engineerPlanned) {
          let obj = {
            name: key,
            val : res.data.data.engineerPlanned[key]
          }
          this.payRatesRe.push(obj);
        }  

        this.dialogVisible = true;
        this.dialogState = true;
      } catch (error) {
        this.loading = false;
        this.$message.error(error);
      }
    },

    //模态框关闭事件
    handleClose(done) {
      done();
      this.form = {
        projectId: "",
        workPackageId: "",
        reportPeriodFrom: new Date().myDate
          .toLocaleDateString()
          .split("/")
          .join("-"),
        comments: "",
        workAccomplished: "",
        workPlanned: "",
        problemsFaced: "",
        problemsAnticipated: "",
      };
    },

    //点击添加按钮 弹框
    addReport() {
      this.dialogVisible = true;
      this.dialogState = false;
      this.dialogTitleText = "Add Report";
    },

    //上传表单
    submitForm: async function () {
      for (let i = 0; i < this.payRatesEst.length; i++) {
        this.form.etc[this.payRatesEst[i].name] = this.payRatesEst[i].val;
      }

      for (let i = 0; i < this.payRatesRe.length; i++) {
        this.form.engineerPlanned[this.payRatesRe[i].name] =
          this.payRatesRe[i].val;
      }

      try {
        let res = await this.$axios({
          method: "POST",
          url: "api/report/RE",
          headers: {
            token: this.userInfo.token,
          },
          data: this.form,
        });
        if (res.data.code != 0) {
          this.$message.error(res.data.message);
          return;
        } else {
          this.$message.success(res.data.data);
          this.getMonthlyList();
          this.dialogVisible = false;
        }
      } catch (error) {
        this.$message.error(error);
        console.log(error);
      }
    },

    //下拉框选中 projectName 索引出WPName 数组
    changeProjectName() {
      //console.log(this.projectInfo);
      this.selectWPName = [];
      this.form.workPackageId = "";
      for (let i = 0; i < this.projectInfo.length; i++) {
        if (this.form.projectId == this.projectInfo[i].projectName) {
          this.selectWPName.push(this.projectInfo[i].wpName);
        }
      }
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
          this.payRatesEst.push(obj);
        });
        this.payRatesEst.forEach((payRate) => {
          let obj = {
            name: payRate.name,
            val: payRate.val,
          };
          this.payRatesRe.push(obj);
        });
      } catch (error) {
        this.$message.error(error);
        this.loading = false;
        console.log(error);
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

.form_title {
  text-align: right;
  padding-right: 20px;
  margin-top: 3px;
}

.form_row {
  margin-bottom: 15px;
}

.form {
  max-height: 400px;
  overflow: auto;
  padding: 10px;
}
</style>
