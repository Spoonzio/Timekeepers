import Vue from "vue";
import Router from "vue-router";
import Login from "@/components/login";
import DashBoard from "@/components/dashBoard/Index";
import EmployeeList from "@/components/dashboard/employee/EmployeeList";
import EditEmployee from "@/components/dashboard/employee/EditEmployee";
import EditPayRate from "@/components/dashboard/employee/EditPayRate";
import MyTimesheet from "@/components/dashboard/timesheet/MyTimesheet"
import TimesheetApprove from "@/components/dashboard/timesheet/TimesheetApprove"
import AddTimesheet from "@/components/dashboard/timesheet/AddTimesheet"
import Welcome from "@/components/dashboard/Welcome/"
import EditProject from "@/components/dashboard/project/EditProject";
import ChangePW from '@/components/dashboard/changepw/ChangePW'
import AllReports from '@/components/dashboard/report/Index'
import MonthlyReports from '@/components/dashboard/report/MonthlyReports'
import ViewTimesheet from "@/components/dashboard/timesheet/ViewTimesheet"
import WeeklyReport from "@/components/dashboard/report/WeeklyReport.vue"
import MonthlyList from '@/components/dashboard/report/MonthlyList.vue'
Vue.use(Router);

export default new Router({
  routes: [
    {
      path: "/",
      name: "Login",
      component: Login
    },
    {
      path: "/dashboard",
      name: "DashBoard",
      component: DashBoard,
      redirect: { name: "Welcome" },
      children: [
        {
          name: "Welcome",
          path: "welcome",
          component: Welcome
        },
        {
          name: "Project",
          path: "project",
          component: () => import(/* webpackChunkName: "num" */ '../components/dashboard/project/Index.vue')
        },
        {
          name: "EditProject",
          path: "editProject/:id",
          component: EditProject
        },
        {
          name: "EmployeeList",
          path: "employeeList",
          component: EmployeeList
        },
        {
          name: "EditPayRate",
          path: "editPayRate",
          component: EditPayRate
        },
        {
          name: "EditEmployee",
          path: "editEmployee/:id",
          component: EditEmployee
        },
        {
          name: "TimesheetApprove",
          path: "timesheetApprove",
          component: TimesheetApprove
        },
        {
          name: "MyTimesheet",
          path: "myTimesheet",
          component: MyTimesheet
        },
        {
          name: "AddTimesheet",
          path: "addTimesheet/:id",
          component: AddTimesheet
        },
        {
          name: "ViewTimesheet",
          path: "viewTimesheet/:employeeId/:endDate",
          component: ViewTimesheet
        },
        {
          name: "ChangePW",
          path: "changepw",
          component: ChangePW
        },
        {
          name: "AllReport",
          path: "reports/all",
          component: AllReports
        },
        {
          name: "WeeklyReport",
          path: "reports/weekly",
          component: WeeklyReport
        },
        {
          name: "MonthlyList",
          path: "reports/MonthlyList",
          component: MonthlyList
        },
        {
          name: "MonthlyReports",
          path: "reports/monthlyReports",
          component: MonthlyReports
        },
      ]
    }
  ]
});
