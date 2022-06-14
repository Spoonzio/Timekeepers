package com.comp4911.backend.access;

import com.comp4911.backend.models.EmployeeEntity;
import com.comp4911.backend.models.ProjectPackageEntity;
import com.comp4911.backend.models.TimesheetEntity;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Dependent
@Stateless
public class ProjectPackageManager {

    @PersistenceContext(unitName = "timesheet-jpa")
    private EntityManager em;

    public ProjectPackageEntity find(String projectId, String workPackageId) {
        TypedQuery<ProjectPackageEntity> q = em.createQuery("SELECT p FROM ProjectPackageEntity p WHERE p.projectId = :projectId AND p.workPackageId = :workPackageId", ProjectPackageEntity.class)
                .setParameter("projectId", projectId)
                .setParameter("workPackageId", workPackageId);
        try {
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<ProjectPackageEntity> findAllLowestLevel() {
        TypedQuery<ProjectPackageEntity> q = em.createQuery("SELECT p FROM ProjectPackageEntity p WHERE p.childWorkPackages.size = 0 ORDER BY p.projectId ASC, p.workPackageId ASC, p.createdOn ASC", ProjectPackageEntity.class);
        return q.getResultList();
    }

    public ProjectPackageEntity find(String projectId) {
        TypedQuery<ProjectPackageEntity> q = em.createQuery("SELECT p FROM ProjectPackageEntity p WHERE p.projectId = :projectId AND p.parentProjectPackageEntity IS NULL", ProjectPackageEntity.class)
                .setParameter("projectId", projectId);
        try {
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<ProjectPackageEntity> findAll(String projectId) {
        TypedQuery<ProjectPackageEntity> q = em.createQuery("SELECT p FROM ProjectPackageEntity p WHERE p.projectId = :projectId", ProjectPackageEntity.class)
                .setParameter("projectId", projectId);
        return q.getResultList();
    }

    public List<ProjectPackageEntity> findByResponsibleEngineer(String responsibleEngineerId) {
        TypedQuery<ProjectPackageEntity> q = em.createQuery(
                "SELECT p FROM ProjectPackageEntity p " +
                        "WHERE p.managerEntity.employeeId = :responsibleEngineerId " +
                        "AND p.parentProjectPackageEntity IS NOT NULL", ProjectPackageEntity.class)
                .setParameter("responsibleEngineerId", responsibleEngineerId);
        return q.getResultList();
    }

    public List<ProjectPackageEntity> findByManagerId(String managerId) {
        TypedQuery<ProjectPackageEntity> q = em.createQuery("SELECT p FROM ProjectPackageEntity p WHERE p.managerEntity.employeeId = :managerId AND p.parentProjectPackageEntity IS NULL", ProjectPackageEntity.class)
                .setParameter("managerId", managerId);
        return q.getResultList();
    }

    public List<ProjectPackageEntity> findByAssistantId(String assistantId) {
        TypedQuery<ProjectPackageEntity> q = em.createQuery("SELECT p FROM ProjectPackageEntity p WHERE p.assistantEntity.employeeId = :assistantId AND p.parentProjectPackageEntity IS NULL", ProjectPackageEntity.class)
                .setParameter("assistantId", assistantId);
        return q.getResultList();
    }

    public List<ProjectPackageEntity> findByParticipantId(String participantId) {
        TypedQuery<ProjectPackageEntity> q = em.createQuery(
                "SELECT DISTINCT p FROM ProjectPackageEntity p " +
                        "JOIN p.employeeEntities emps " +
                        "WHERE emps.employeeId = :participantId AND p.parentProjectPackageEntity IS NULL", ProjectPackageEntity.class)
                .setParameter("participantId", participantId);
        return q.getResultList();
    }


    public void merge(ProjectPackageEntity projectPackageEntity) {
        em.merge(projectPackageEntity);
    }

    public void persist(ProjectPackageEntity projectPackageEntity) {
        em.persist(projectPackageEntity);
    }

    public void remove(ProjectPackageEntity projectPackageEntity) {
        projectPackageEntity = find(projectPackageEntity.getProjectId(), projectPackageEntity.getWorkPackageId());
        em.remove(projectPackageEntity);
    }

    public List<ProjectPackageEntity> findAllRoot() {
        TypedQuery<ProjectPackageEntity> q = em.createQuery("SELECT p FROM ProjectPackageEntity p WHERE p.parentProjectPackageEntity IS NULL", ProjectPackageEntity.class);
        try {
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }
}
