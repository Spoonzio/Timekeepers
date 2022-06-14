package com.comp4911.backend.lib;

import com.comp4911.backend.access.ProjectPackageManager;
import com.comp4911.backend.models.ProjectPackageEntity;
import com.comp4911.backend.models.TimesheetrowEntity;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Util {
    public static <T> List<T> getPage(List<T> sourceList, int page, int pageSize) {
        page++;
        if(pageSize <= 0 || page <= 0) {
            return Collections.emptyList();
        }

        int fromIndex = (page - 1) * pageSize;
        if(sourceList == null || sourceList.size() <= fromIndex){
            return Collections.emptyList();
        }

        // toIndex exclusive
        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
    }

    public static  Set<TimesheetrowEntity> travelTree(ProjectPackageEntity projectPackageEntity) {
        Set<TimesheetrowEntity> rows = projectPackageEntity.getTimesheetrowEntities();
        Set<ProjectPackageEntity> child = projectPackageEntity.getChildWorkPackages();
        for (ProjectPackageEntity childWorkPackage : child) {
            rows.addAll(travelTree(childWorkPackage));
        }
        return rows;
    }

    public static boolean isProjectLowestLevel(ProjectPackageEntity projectPackageEntity) {
        return projectPackageEntity != null && (projectPackageEntity.getChildWorkPackages() == null || projectPackageEntity.getChildWorkPackages().size() == 0);
    }

    public static void recursivelySetStatus (ProjectPackageEntity projectPackageEntity, boolean status, ProjectPackageManager projectPackageManager) {
        if (projectPackageEntity == null) {
            return;
        }
        projectPackageEntity.setIsOpen(status);
        projectPackageManager.merge(projectPackageEntity);
        Set<ProjectPackageEntity> child = projectPackageEntity.getChildWorkPackages();
        for (ProjectPackageEntity childWorkPackage : child) {
            recursivelySetStatus(childWorkPackage, status, projectPackageManager);
        }
    }
}
