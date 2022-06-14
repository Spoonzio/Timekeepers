package com.comp4911.backend.access;

import com.comp4911.backend.models.*;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Dependent
@Stateless
public class TimesheetRowManager {

    @PersistenceContext(unitName = "timesheet-jpa")
    private EntityManager em;

    public List<TimesheetrowEntity> find(Date date, String projectId, String workPackageId, String employeeId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TimesheetrowEntity> cq = cb.createQuery(TimesheetrowEntity.class);
        Root<TimesheetrowEntity> root = cq.from(TimesheetrowEntity.class);

        CriteriaQuery<TimesheetrowEntity> select = cq.select(root);
        List<Predicate> predicates = new ArrayList<>();

        if (date != null) {
            predicates.add(cb.equal(root.get("weekEnding"), date));
        }
        if (workPackageId != null) {
            predicates.add(cb.equal(root.get("projectPackageEntity").get("workPackageId"), workPackageId));
        }
        if (projectId != null) {
            predicates.add(cb.equal(root.get("projectPackageEntity").get("projectId"), projectId));
        }
        if (employeeId != null) {
            predicates.add(cb.equal(root.get("employeeEntity").get("employeeId"), employeeId));
        }


        Predicate finalPredicate = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        select.where(finalPredicate);
        TypedQuery<TimesheetrowEntity> typedQuery = em.createQuery(select);

        return typedQuery.getResultList();
    }

    public List<TimesheetrowEntity> find(String projectPacakgeNum) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TimesheetrowEntity> cq = cb.createQuery(TimesheetrowEntity.class);
        Root<TimesheetrowEntity> root = cq.from(TimesheetrowEntity.class);

        CriteriaQuery<TimesheetrowEntity> select = cq.select(root);
        List<Predicate> predicates = new ArrayList<>();

        if (projectPacakgeNum != null) {
            predicates.add(cb.equal(root.get("projectPackageEntity").get("projectPackageNum"), projectPacakgeNum));
        }

        Predicate finalPredicate = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        select.where(finalPredicate);
        TypedQuery<TimesheetrowEntity> typedQuery = em.createQuery(select);

        return typedQuery.getResultList();
    }

    public List<TimesheetrowEntity> find(Date startDate, Date endDate, String employeeId, String projectId, String workPackageId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TimesheetrowEntity> cq = cb.createQuery(TimesheetrowEntity.class);
        Root<TimesheetrowEntity> root = cq.from(TimesheetrowEntity.class);

        CriteriaQuery<TimesheetrowEntity> select = cq.select(root);
        List<Predicate> predicates = new ArrayList<>();

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("weekEnding"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThan(root.get("weekEnding"), endDate));
        }
        if (employeeId != null) {
            predicates.add(cb.equal(root.get("employeeEntity").get("employeeId"), employeeId));
        }
        if (workPackageId != null) {
            predicates.add(cb.equal(root.get("projectPackageEntity").get("workPackageId"), workPackageId));
        }
        if (projectId != null) {
            predicates.add(cb.equal(root.get("projectPackageEntity").get("projectId"), projectId));
        }


        Predicate finalPredicate = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        select.where(finalPredicate);
        TypedQuery<TimesheetrowEntity> typedQuery = em.createQuery(select);

        return typedQuery.getResultList();
    }

    public void persist(TimesheetrowEntity timesheetrow) {
        em.persist(timesheetrow);
    }

    public void remove(TimesheetrowEntity timesheetrow) {
        timesheetrow = find(
                timesheetrow.getWeekEnding(),
                timesheetrow.getProjectPackageEntity().getProjectId(),
                timesheetrow.getProjectPackageEntity().getWorkPackageId(),
                timesheetrow.getEmployeeEntity().getEmployeeId()).get(0);
        em.remove(em.contains(timesheetrow) ? timesheetrow : em.merge(timesheetrow));
    }

    public void merge(TimesheetrowEntity timesheetrow) {
        em.merge(timesheetrow);
    }
}