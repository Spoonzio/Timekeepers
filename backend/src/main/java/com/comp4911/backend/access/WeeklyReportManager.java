package com.comp4911.backend.access;

import com.comp4911.backend.models.WeeklyReportEntity;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Dependent
@Stateless
public class WeeklyReportManager implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Entity manager.
     */
    @PersistenceContext(unitName = "timesheet-jpa")
    private EntityManager em;

    public void persist(WeeklyReportEntity entity) {
        em.persist(entity);
    }

    public List<WeeklyReportEntity> findAll() {
        TypedQuery<WeeklyReportEntity> query = em.createQuery("SELECT m FROM WeeklyReportEntity m", WeeklyReportEntity.class);
        return query.getResultList();
    }

    public WeeklyReportEntity find(int id) {
        return em.find(WeeklyReportEntity.class, id);
    }

    public WeeklyReportEntity find(String projectId, String wpId, Date reportDate){
        TypedQuery<WeeklyReportEntity> query = em.createQuery("SELECT m FROM WeeklyReportEntity m WHERE m.projectPackageEntity.projectId = :projectId AND m.projectPackageEntity.workPackageId = :wpId AND m.reportDate = :reportDate", WeeklyReportEntity.class)
                .setParameter("projectId", projectId)
                .setParameter("wpId", wpId)
                .setParameter("reportDate", reportDate);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<WeeklyReportEntity> findByCreator(String creatorId){
        TypedQuery<WeeklyReportEntity> query = em.createQuery("SELECT m FROM WeeklyReportEntity m WHERE m.employeeEntity.employeeId = :employeeId", WeeklyReportEntity.class)
                .setParameter("employeeId", creatorId);
        return query.getResultList();
    }

    public List<WeeklyReportEntity> findByProjectIds(List<String> projectIds){
        if (projectIds.size() == 0) {
            return new ArrayList<>();
        }
        TypedQuery<WeeklyReportEntity> query = em.createQuery("SELECT m FROM WeeklyReportEntity m WHERE m.projectPackageEntity.projectId IN (:projectIds)", WeeklyReportEntity.class)
                .setParameter("projectIds", projectIds);
        return query.getResultList();
    }

    public List<WeeklyReportEntity> find(String projectId){
        TypedQuery<WeeklyReportEntity> query = em.createQuery("SELECT m FROM WeeklyReportEntity m WHERE m.projectPackageEntity.projectId = :projectId", WeeklyReportEntity.class)
                .setParameter("projectId", projectId);
        return query.getResultList();
    }

    public List<WeeklyReportEntity> find(String projectId, Date reportDate){
        TypedQuery<WeeklyReportEntity> query = em.createQuery("SELECT m FROM WeeklyReportEntity m WHERE m.projectPackageEntity.projectId = :projectId AND m.reportDate = :reportDate", WeeklyReportEntity.class)
                .setParameter("projectId", projectId)
                .setParameter("reportDate", reportDate);
        return query.getResultList();
    }

    public List<WeeklyReportEntity> find(Date startDate, Date endDate, String projectId, String workPackageId){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WeeklyReportEntity> cq = cb.createQuery(WeeklyReportEntity.class);
        Root<WeeklyReportEntity> root = cq.from(WeeklyReportEntity.class);

        CriteriaQuery<WeeklyReportEntity> select = cq.select(root);
        List<Predicate> predicates = new ArrayList<>();

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("reportDate"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThan(root.get("reportDate"), endDate));
        }
        if (projectId != null) {
            predicates.add(cb.equal(root.get("projectPackageEntity").get("projectId"), projectId));
        }
        if (workPackageId != null) {
            predicates.add(cb.equal(root.get("projectPackageEntity").get("workPackageId"), workPackageId));
        }

        Predicate finalPredicate = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        select.where(finalPredicate);
        select.orderBy(cb.desc(root.get("reportDate")));
        TypedQuery<WeeklyReportEntity> typedQuery = em.createQuery(select);

        return typedQuery.getResultList();
    }

    public void merge(WeeklyReportEntity entity) {
        em.merge(entity);
    }
}
