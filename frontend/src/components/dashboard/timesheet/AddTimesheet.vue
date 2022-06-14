<template>
  <el-row v-loading="loading">
    <el-breadcrumb separator="/" style="margin-bottom: 20px">
      <el-breadcrumb-item :to="{ path: '/dashboard/mytimesheet' }"
        >Timesheet</el-breadcrumb-item
      >
      <el-breadcrumb-item>Add or Edit Timesheet</el-breadcrumb-item>
    </el-breadcrumb>

    <el-row>
      <el-col :span="18">
        <el-date-picker
          v-model="week"
          disabled
          type="week"
          :picker-options="pickerOptions"
          value-format="yyyy-MM-dd"
          placeholder="Choose a Year"
          :change="
            () => {
              onDateChange();
            }
          "
        >
        </el-date-picker>
      </el-col>
      <el-col :span="6" style="text-align: right"
        ><el-button type="primary" @click="addRow()">New Row</el-button></el-col
      >
    </el-row>
    <el-row style="margin-top: 15px" v-if="showTable">
      <el-form
        :inline="true"
        :model="form"
        :rules="rules"
        ref="ruleForm"
        :hide-required-asterisk="true"
      >
        <el-table :data="rows" border>
          <el-table-column prop="projectId" label="Project">
            <template slot-scope="scope">
              {{ scope.row.projectId }}
            </template>
          </el-table-column>

          <el-table-column prop="workPackageId" label="WP">
            <template slot-scope="scope">
              {{ scope.row.workPackageId }}
            </template>
          </el-table-column>

          <el-table-column prop="mon" label="Mon">
            <template slot-scope="scope">
              {{ scope.row.mon }}
            </template>
          </el-table-column>

          <el-table-column prop="tue" label="Tue">
            <template slot-scope="scope">
              {{ scope.row.tue }}
            </template>
          </el-table-column>

          <el-table-column prop="wed" label="Wed">
            <template slot-scope="scope">
              {{ scope.row.wed }}
            </template>
          </el-table-column>

          <el-table-column prop="thu" label="Thu">
            <template slot-scope="scope"> {{ scope.row.thu }}</template>
          </el-table-column>

          <el-table-column prop="fri" label="Fri">
            <template slot-scope="scope"> {{ scope.row.fri }}</template>
          </el-table-column>

          <el-table-column prop="sat" label="Sat">
            <template slot-scope="scope"> {{ scope.row.sat }}</template>
          </el-table-column>

          <el-table-column prop="sun" label="Sun">
            <template slot-scope="scope"> {{ scope.row.sun }}</template>
          </el-table-column>

          <el-table-column fixed="right" width="115" label="Action">
            <template slot-scope="scope">
              <el-button type="text" @click="editOnRow(scope.row)"
                >Edit</el-button
              >
              <el-button type="text" @click="deleteRow(scope.$index)"
                >Delete</el-button
              >
            </template>
          </el-table-column>
        </el-table>
        <el-row style="margin-top: 15px">
          <el-form-item label="Over Time:" prop="overTime" size="small">
            <el-input v-model="form.overTime"></el-input>
          </el-form-item>
          <el-form-item label="Flex Time:" prop="flexTime" size="small">
            <el-input v-model="form.flexTime"></el-input>
          </el-form-item>
        </el-row>

        <el-button type="primary" @click="submitForm('ruleForm')">
          Submit
        </el-button>
      </el-form>
    </el-row>
    <el-row v-else style="margin-top: 15px">
      The timesheet of this week is already existed and is forbidden to edit.
      Please choose another date.
    </el-row>

    <!--添加一个row-->
    <el-dialog
      title="Add/Edit Timesheet Row"
      :visible.sync="dialogVisible"
      width="500px"
    >
      <el-row style="max-height: 300px; overflow: auto; padding: 10px">
        <el-form :model="form" label-width="100px">
          <el-form-item label="Project">
            <el-select
              v-model="rowForm.projectId"
              placeholder="Please select a project"
              style="width: 100%"
              :change="onProjectChange(rowForm.projectId)"
            >
              <el-option
                v-for="item in rootProjects"
                :key="item"
                :label="item"
                :value="item"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="WP">
            <el-select
              v-model="rowForm.workPackageId"
              placeholder="Please select a work package"
              style="width: 100%"
            >
              <el-option
                v-for="item in currentWPs"
                :key="item"
                :label="item"
                :value="item"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="Monday">
            <el-input-number
              v-model="rowForm.mon"
              :precision="1"
              :step="0.1"
              :max="10"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="Tuesday">
            <el-input-number
              v-model="rowForm.tue"
              :precision="1"
              :step="0.1"
              :max="10"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="Wednesday">
            <el-input-number
              v-model="rowForm.wed"
              :precision="1"
              :step="0.1"
              :max="10"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="Thursday">
            <el-input-number
              v-model="rowForm.thu"
              :precision="1"
              :step="0.1"
              :max="10"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="Friday">
            <el-input-number
              v-model="rowForm.fri"
              :precision="1"
              :step="0.1"
              :max="10"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="Saturday">
            <el-input-number
              v-model="rowForm.sat"
              :precision="1"
              :step="0.1"
              :max="10"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="Sunday">
            <el-input-number
              v-model="rowForm.sun"
              :precision="1"
              :step="0.1"
              :max="10"
            ></el-input-number>
          </el-form-item>
        </el-form>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancle</el-button>
        <el-button type="primary" @click="onAddNewRow()">OK</el-button>
      </div>
    </el-dialog>
  </el-row>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      user: null,
      token: null,
      loading: true,
      showTable: true,
      rows: [],
      form: {
        isApproved: "submitted",
        flexTime: 0,
        overTime: 0,
      },
      rowForm: {
        projectId: "",
        workPackageId: "",
        mon: 0.0,
        tue: 0.0,
        wed: 0.0,
        thu: 0.0,
        fri: 0.0,
        sat: 0.0,
        sun: 0.0,
      },
      currentWPs: [],
      dialogVisible: false,
      week: new Date().toISOString().slice(0, 10),
      rootProjects: [],
      rawProjects: {},
      previousProjectId: null,
      previousWPId: null,
      pickerOptions: {
        firstDayOfWeek: 1,
      },
      rules: {
        projectId: [
          {
            required: true,
            message: "Project Name is required",
            trigger: "blur",
          },
        ],
        workPackageId: [
          {
            required: true,
            message: "Work package is required",
            trigger: "blur",
          },
        ],
        flexTime: [
          {
            required: true,
            message: "flex time is required",
            trigger: "blur",
          },
        ],
        overTime: [
          {
            required: true,
            message: "Over time is required",
            trigger: "blur",
          },
        ],
        markupCost: [
          {
            required: true,
            message: "Markup cost is required",
            trigger: "blur",
          },
        ],
      },
    };
  },
  methods: {
    async loadData() {
      try {
        this.loading = true;

        let date = this.$route.params.endDate;

        if (date) {
          this.week = date;
        }

        let result = await this.$axios({
          method: "GET",
          url:
            this.$constants.timesheetUrl +
            `?employeeId=${this.user.employeeId}&startDate=${this.week}`,
          headers: {
            token: this.token,
          },
        });

        this.loading = false;
        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        let rowData = result.data.data;
        console.log(rowData);
        if (rowData.length > 0) {
          this.form.isApproved = rowData[0].isApproved;
          this.form.flexTime = rowData[0].flexTimeUsed;
          this.form.overTime = rowData[0].overtime;

          if (
            this.form.isApproved == "submitted" ||
            this.form.isApproved == "approved"
          ) {
            this.showTable = false;
          } else {
            this.showTable = true;
          }
        }

        if (rowData.length > 0 && rowData[0].timesheetrows.length > 0) {
          this.rows = rowData[0].timesheetrows.map((r) => ({
            ...r,
            edit: false,
          }));
        }
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    },
    onDateChange() {
      this.loadData();
    },
    editOnRow(row) {
      this.bAdd = false;
      this.rowForm = row;
      this.dialogVisible = true;
    },
    addRow() {
      this.bAdd = true;
      this.dialogVisible = true;
    },
    deleteRow: async function (id) {
      try {
        this.loading = true;
        let formValue = {
          projectId: this.rows[id].projectId,
          workPackageId: this.rows[id].workPackageId,
          employeeId: this.user.employeeId,
          weekEnding: this.week,
        };
        console.log(formValue);
        let result = await this.$axios({
          method: "DELETE",
          url: this.$constants.timesheetUrl + "/row",
          headers: {
            token: this.token,
          },
          data: formValue,
        });

        this.loading = false;
        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        this.rows = this.rows.filter((r, i) => i != id);
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    },
    handleSubmit: async function () {
      try {
        this.loading = true;
        const min = 40;
        if (this.getSummary() - this.form.flexTime - this.form.overTime < min) {
          this.$message.error("Total hours must be at least 40 hours");
          return;
        }

        this.form.isApproved = "submitted";

        let result = await this.$axios({
          method: "PUT",
          url:
            this.$constants.timesheetUrl +
            `/${this.user.employeeId}/${this.week}`,
          headers: {
            token: this.token,
          },
          data: this.form,
        });

        this.loading = false;
        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        this.$router.push({ path: "/dashboard/mytimesheet" });
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.handleSubmit();
        } else {
          return false;
        }
      });
    },
    getSummary: function () {
      let sum = 0;
      this.rows.forEach((r) => {
        sum += r.mon + r.tue + r.wed + r.thu + r.fri + r.sat + r.sun;
      });
      return sum;
    },
    getAllProjects: async function () {
      try {
        this.loading = true;

        let result = await this.$axios({
          method: "GET",
          url: "api/projectpackage",
          headers: {
            token: this.token,
          },
        });

        this.loading = false;
        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        function traverse(root, currentRoot) {
          if (!root.workPackageArray) {
            root.workPackageArray = [];
          }
          currentRoot.forEach((element) => {
            root.workPackageArray.push(element.workPackageId);
            if (element.childWorkPackages.length) {
              traverse(root, element.childWorkPackages);
            }
          });
        }

        result.data.data.forEach((element) => {
          this.rootProjects.push(element.projectId);
          if (element.childWorkPackages.length) {
            traverse(element, element.childWorkPackages);
          }
        });

        this.rawProjects = result.data.data;
        console.log(result.data.data);
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    },
    onProjectChange(projectId) {
      for (let i = 0; i < this.rawProjects.length; ++i) {
        if (projectId == this.rawProjects[i].projectId) {
          this.currentWPs = this.rawProjects[i].workPackageArray;
        }
      }
    },
    async onAddNewRow() {
      try {
        this.loading = true;
        this.dialogVisible = false;

        this.rowForm.weekEnding = this.week;
        let result = null;

        if (this.bAdd) {
          result = await this.$axios({
            method: "POST",
            url: "api/timesheet/row",
            headers: {
              token: this.token,
            },
            data: this.rowForm,
          });
        } else {
          result = await this.$axios({
            method: "PUT",
            url:
              "api/timesheet/row/" +
              this.user.employeeId +
              "/" +
              this.week +
              "/" +
              this.rowForm.projectId +
              "/" +
              this.rowForm.workPackageId,
            headers: {
              token: this.token,
            },
            data: this.rowForm,
          });
        }

        this.loading = false;
        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        this.dialogVisible = false;
        this.getAllProjects();
      } catch (error) {
        this.$message.error(error);
        console.log(error);
        this.loading = false;
      }
    },
  },

  async mounted() {
    let cookie = this.$cookie.get("user");
    if (!cookie) {
      this.$message.error("Token expired");
      this.$router.push({ name: "Login" });
      return;
    }
    this.user = JSON.parse(cookie);
    this.token = this.user.token;
    await this.getAllProjects();
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
