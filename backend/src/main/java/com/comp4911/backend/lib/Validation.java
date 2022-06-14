package com.comp4911.backend.lib;

import com.comp4911.backend.models.EmployeeEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Validation {
    public static boolean canBeSupervisor(EmployeeEntity attemptedSupervisor, EmployeeEntity employee) {
        if (attemptedSupervisor == null) {
            return true;
        }
        if (employee == null) {
            return false;
        }
        if (employee.getEmployeeId().equals(attemptedSupervisor.getEmployeeId())) {
            return false;
        }
        String attemptedSupervisorId = attemptedSupervisor.getEmployeeId();
        List<EmployeeEntity> childrenEmps = new ArrayList<>();

        Set<EmployeeEntity> currentChildrenEmps = employee.getSupervising();
        childrenEmps.addAll(currentChildrenEmps);

        while (!childrenEmps.isEmpty()){
            // Has id == is circular reference
            for (EmployeeEntity childEmp : childrenEmps) {
                // Prevent circular relationship ie A03->A05->A03
                if(childEmp.getEmployeeId().equals(attemptedSupervisorId)){
                    return false;
                }
            }

            // Add grandchildren to list
            if(childrenEmps.size()>0){
                EmployeeEntity child = childrenEmps.get(0);
                childrenEmps.remove(0);

                Set<EmployeeEntity> grandChildrenEmps = child.getSupervising();
                childrenEmps.addAll(grandChildrenEmps);
            }
        }
        return true;
    }

    public static boolean canBeTimesheetApprover(EmployeeEntity attemptedApprover, EmployeeEntity employee) {
        if (attemptedApprover == null) {
            return true;
        }
        if (employee == null) {
            return false;
        }
        if (employee.getEmployeeId().equals(attemptedApprover.getEmployeeId())) {
            return false;
        }
        String attemptedApproverId = attemptedApprover.getEmployeeId();
        List<EmployeeEntity> childrenEmps = new ArrayList<>();

        Set<EmployeeEntity> currentChildrenEmps = employee.getTimesheetApproving();
        childrenEmps.addAll(currentChildrenEmps);

        while (!childrenEmps.isEmpty()){
            // Has id == is circular reference
            for (EmployeeEntity childEmp : childrenEmps) {
                // Prevent circular relationship ie A03->A05->A03
                if(childEmp.getEmployeeId().equals(attemptedApproverId)){
                    return false;
                }
            }

            // Add grandchildren to list
            if(childrenEmps.size()>0){
                EmployeeEntity child = childrenEmps.get(0);
                childrenEmps.remove(0);

                Set<EmployeeEntity> grandChildrenEmps = child.getTimesheetApproving();
                childrenEmps.addAll(grandChildrenEmps);
            }
        }
        return true;
    }
}
