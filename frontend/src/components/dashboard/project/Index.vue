<template>
  <el-row v-loading="loading">
    <el-breadcrumb separator="/" style="margin-bottom: 5px">
      <el-breadcrumb-item :to="{ path: '/dashboard/project' }">Project</el-breadcrumb-item>
      <el-breadcrumb-item>Project Management</el-breadcrumb-item>
    </el-breadcrumb>
    <el-row style="margin-bottom: 5px; text-align: right">
      <router-link to="editProject/0">
        <el-button v-if=" user.role == 'Administrator'" type="primary">New Project</el-button>
      </router-link>
    </el-row>

    <el-row>
      <el-table :data="projectData" row-key="workPackageId" border lazy :load="load" :tree-props="{
          children: 'childWorkPackages',
          hasChildren: 'hasChildren',
        }">
        <el-table-column prop="workPackageId" label="Name" width="200"/>
        <el-table-column prop="manager" label="Manager">
          <template slot-scope="scope">
            <span v-if="scope.row.manager">{{ scope.row.manager }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if=" user.role != 'Employee'" prop="hourSpent" label="Hour (Spent)" />
        <el-table-column prop="isOpen" label="Status" />
        <el-table-column v-if=" user.role != 'Employee'" prop="costEstimate" label="Cost (Est)" />

        <el-table-column prop="employeeEntities" label="Employee">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEmployeeList(scope.row.employeeEntities, scope.row)">
              {{ scope.row.employeeEntities.length }}
            </el-button>
          </template>
        </el-table-column>

        <el-table-column v-if=" user.role != 'Employee'" fixed="right" label="Action">
          <template slot-scope="scope">
            <router-link :to="{
                name: 'EditProject',
                params: { id: scope.row.projectId },
                query: { createWP: 1, workPackageId: scope.row.workPackageId},
              }" style="text-decoration: none; color: blue">
              <el-button type="text" size="small">New</el-button>
            </router-link>
            <router-link :to="{
                name: 'EditProject',
                params: { id: scope.row.projectId },
                query: {workPackageId: scope.row.workPackageId}
              }" style="text-decoration: none; color: blue">
              <el-button type="text" size="small" style="margin-left:5px;">Edit</el-button>
            </router-link>
            <!-- <el-button type="text" size="small">Edit</el-button> -->
          </template>
        </el-table-column>
      </el-table>
    </el-row>
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page.sync="pagination.currentPage" :page-sizes="pagination.sizes" :page-size="pagination.currentSize" layout="total, sizes, prev, pager, next, jumper" :total="pagination.total" style="margin-top: 10px; text-align: center">
    </el-pagination>

    <el-dialog
      title="Employee List"
      :visible.sync="showEmployeeList"
      destroy-on-close
      close-on-press-escape
      lock-scroll
      append-to-body
      v-loading="dialogLoading"
    >
      <el-input
        v-model="search"
        size="normal"
        placeholder="Type the first Name to search an employee"
        style="margin-bottom: 5px; width: 100%"
      />
      <el-table
        border
        max-height="300"
        @selection-change="handleSelectionChange"
        :data="
          selectedEmployees.filter(
            (data) =>
              !search ||
              data.firstName.toLowerCase().includes(search.toLowerCase())
          )
        ">
        <el-table-column property="employeeId" label="Employee ID"></el-table-column>

        <el-table-column property="lastName" label="Last Name"></el-table-column>

        <el-table-column property="firstName" label="First Name">
        </el-table-column>

        <el-table-column property="role" label="Role"> </el-table-column>

        <el-table-column property="" label="Assigned" current-change>
          <template slot-scope="scope">
            <el-checkbox
              v-if="scope.row.isSupervised"
              v-model="scope.row.inProject"
            ></el-checkbox>
            <el-checkbox
              v-else
              v-model="scope.row.inProject"
              disabled
            ></el-checkbox>
          </template>
        </el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showEmployeeList = false">Cancel</el-button>
        <el-button type="primary" @click="handleAssign">Save</el-button>
      </span>
    </el-dialog>
  </el-row>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      user: {},
      search: "",
      token: "",
      dialogLoading: false,
      loading: true,
      employees: [],
      selectedEmployees: [],
      checkedEmployeeObj: {},
      selectedProject: {},

      showEmployeeList: false,
      pagination: {
        currentPage: 1,
        currentSize: 20,
        sizes: [20, 40, 60, 80, 100],
        total: 0,
      },
      projectData: [],
      multipleSelection: [],
    };
  },
  methods: {
    toggleSelection(rows) {
      if (rows) {
        rows.forEach((row) => {
          this.$refs.multipleTable.toggleRowSelection(row);
        });
      } else {
        this.$refs.multipleTable.clearSelection();
      }
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },

    handleSizeChange(val) {
      this.size = val;
      console.log(`${val} items per page`);
    },

    handleCurrentChange(val) {
      console.log(`current page: ${val}`);
    },

    async handleEmployeeList(employeeEntities, row) {
      this.loading = true;
      try {
        let result = await this.$axios({
          method: "GET",
          url:
            this.$constants.projectUrl +
            "/" +
            row.projectId +
            "/" +
            row.workPackageId +
            "/parentEmployees",
          headers: {
            token: this.token,
          },
          params: {
            page: this.page,
            size: this.size,
          },
        });

        this.loading = false;
        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        // this.employees = result.data.data.list;
        // this.total = result.data.data.total;
        console.log(row);
        this.selectedEmployees = result.data.data;
        for (let i in this.selectedEmployees) {
          this.$set(this.selectedEmployees[i], "inProject", false);
          if(!row.parentWorkPackageId && this.user.role != "Administrator"){
            this.$set(this.selectedEmployees[i], "isSupervised", false);
          }
          else if (this.user.role == "Administrator" || row.managerId == this.user.employeeId) {
            this.$set(this.selectedEmployees[i], "isSupervised", true);

          } else {
            if (!this.selectedEmployees[i].supervisor) {
              this.$set(this.selectedEmployees[i], "isSupervised", false);
            } else if (
              this.selectedEmployees[i].supervisor.employeeId ==
              this.user.employeeId
            ) {
              this.$set(this.selectedEmployees[i], "isSupervised", true);
            } else {
              this.$set(this.selectedEmployees[i], "isSupervised", false);
            }
          }

          for (let j in employeeEntities) {
            if (
              this.selectedEmployees[i].employeeId ==
              employeeEntities[j].employeeId
            ) {
              this.$set(this.selectedEmployees[i], "inProject", true);
              break;
            }
          }
        }
        console.log(this.selectedEmployees);
        this.selectedProject = row;
        this.showEmployeeList = true;
      } catch (error) {
        this.$message.error(error);
        console.log(error);
      }
    },

    // handleChangeChecked(emp) {
    //   this.handleEmployeeList(emp);
    // },
    async handleAssign() {
      try {
        this.dialogLoading = true;
        let subUrl =
          "/" +
          this.selectedProject.projectId +
          "/" +
          this.selectedProject.workPackageId +
          "/swap";
        let newEmployees = [];

        this.selectedEmployees.forEach((x) => {
          if (x.inProject) {
            newEmployees.push(x.employeeId);
          }
        });
        if (newEmployees.length == 0) {
          this.dialogLoading = false;
          this.$message.error("Need at least one employee for a project");
          return;
        }

        let result = await this.$axios({
          method: "PUT",
          url: this.$constants.projectUrl + subUrl,
          headers: {
            token: this.token,
          },
          data: { employees: newEmployees },
        });
        this.initList();
        this.dialogLoading = false;
        this.showEmployeeList = false;
      } catch (error) {
        this.$message.error(error);
        this.dialogLoading = false;
        console.log(error);
      }
    },

    load(tree, treeNode, resolve) {
      console.log(tree, treeNode);
      setTimeout(() => {
        resolve([
          {
            id: 31,
            date: "2016-05-01",
            name: "wangxiaohu",
          },
          {
            id: 32,
            date: "2016-05-01",
            name: "wangxiaohu",
          },
        ]);
      }, 1000);
    },

    initList: async function () {
      if (!this.token) {
        this.$message.error("Token expired");
        this.$router.push("/");
      }

      try {
        let result = await this.$axios({
          method: "GET",
          url: this.$constants.projectUrl,
          headers: {
            token: this.token,
          },
        });

        this.loading = false;
        if (result.data.code != 0) {
          this.$message.error(result.data.message);
          return;
        }

        result.data.data.forEach((project) => {
          if (project.isOpen) {
            project.isOpen = "In Progress";
          } else {
            project.isOpen = "Close";
          }
        });

        console.log(result.data.data);

        this.projectData = result.data.data;
        this.DFS(this.projectData);

        result = await this.$axios({
          method: "GET",
          url: this.$constants.usersUrl,
          headers: {
            token: this.token,
          },
        });

        this.employees = result.data.data.list;
      } catch (error) {
        this.$message.error(error);
        console.log(error);
      }
    },

    DFS: function (node) {
      if (node == null) {
        return;
      }

      for (let i = 0; i < node.length; i++) {
        node[i].isOpen = node[i].open ? "In Progress" : "Closed";
        if (node[i].childWorkPackages) {
          this.DFS(node[i].childWorkPackages);
        }
      }
    },
  },

  mounted() {
    let cookie = this.$cookie.get("user");
    if (!cookie) {
      this.$message.error("Token expired");
      this.$router.push({ name: "Login" });
      return;
    }
    this.user = JSON.parse(cookie);
    console.log(this.user);
    this.token = JSON.parse(this.$cookie.get("user")).token;
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
