package com.comp4911.backend.access;

import com.comp4911.backend.models.TimesheetEntity;
import com.comp4911.backend.models.TimesheetrowEntity;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Dependent
@Stateless
public class TimesheetManager {

    @PersistenceContext(unitName = "timesheet-jpa")
    private EntityManager em;

    public List<TimesheetEntity> find(Date startDate, Date endDate, String employeeId, Byte isApproved, String approverId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TimesheetEntity> cq = cb.createQuery(TimesheetEntity.class);
        Root<TimesheetEntity> root = cq.from(TimesheetEntity.class);

        CriteriaQuery<TimesheetEntity> select = cq.select(root);
        List<Predicate> predicates = new ArrayList<>();

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("weekEnding"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("weekEnding"), endDate));
        }
        if (employeeId != null) {
            predicates.add(cb.equal(root.get("employeeEntity").get("employeeId"), employeeId));
        }
        if (isApproved != null) {
            predicates.add(cb.equal(root.get("isApproved"), isApproved));
        }
        if (approverId != null) {
            //include if approverId is timesheetApprover or supervisor
            predicates.add(
                    cb.or(
                            cb.equal(root.get("employeeEntity").get("supervisor").get("employeeId"), approverId),
                            cb.equal(root.get("employeeEntity").get("timesheetApprover").get("employeeId"), approverId)
                    )
            );
        }

        Predicate finalPredicate = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        select.where(finalPredicate);
        select.orderBy(cb.desc(root.get("weekEnding")));
        TypedQuery<TimesheetEntity> typedQuery = em.createQuery(select);

        return typedQuery.getResultList();
    }

    public TimesheetEntity find(String employeeId, Date weekEnding) {
        TypedQuery<TimesheetEntity> query = em.createQuery("SELECT t FROM TimesheetEntity t WHERE t.employeeEntity.employeeId = :employeeId AND t.weekEnding = :weekEnding", TimesheetEntity.class)
                .setParameter("employeeId", employeeId)
                .setParameter("weekEnding", weekEnding);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public void create(TimesheetEntity timesheet) {
        em.persist(timesheet);
    }

    public void merge(TimesheetEntity timesheet) {
        em.merge(timesheet);
    }

    public void remove(TimesheetEntity timesheet) {
        em.remove(timesheet);
    }
}