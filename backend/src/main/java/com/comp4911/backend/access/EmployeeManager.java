package com.comp4911.backend.access;


import com.comp4911.backend.models.EmployeeEntity;
import com.comp4911.backend.models.TimesheetEntity;

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
import java.util.ArrayList;
import java.util.List;

@Dependent
@Stateless
public class EmployeeManager implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * Entity manager.
     */
    @PersistenceContext(unitName = "timesheet-jpa")
    private EntityManager em;

    /**
     * find employee by id.
     */
    public EmployeeEntity find(String employeeId) {
        try {
            EmployeeEntity employee = em.createQuery("SELECT e FROM EmployeeEntity e WHERE e.employeeId = :employeeId", EmployeeEntity.class).setParameter("employeeId", employeeId).getSingleResult();
            return employee;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * find employee by id.
     */
    public EmployeeEntity findByNum(Integer employeeNum) {
        return em.find(EmployeeEntity.class, employeeNum);
    }

    /**
     * persist employee.
     */
    public void persist(EmployeeEntity employee) {
        em.persist(employee);
    }

    /**
     * merge employee.
     */
    public EmployeeEntity merge(EmployeeEntity employee) {
        return em.merge(employee);
    }

    /**
     * remove employee.
     */
    public void remove(EmployeeEntity employee) {
        employee = find(employee.getEmployeeId());
        em.remove(employee);
    }

    /**
     * Get all employees
     * @return List of employeeEntities
     */
    public List<EmployeeEntity> findAll() {
        return em.createQuery("SELECT e FROM EmployeeEntity e", EmployeeEntity.class).getResultList();
    }

    /**
     * Get all employees with pagination
     * @return List of employeeEntities
     */
    public List<EmployeeEntity> findAll(String supervisorId, String approverId, String role, Integer page, Integer size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EmployeeEntity> cq = cb.createQuery(EmployeeEntity.class);
        Root<EmployeeEntity> root = cq.from(EmployeeEntity.class);

        CriteriaQuery<EmployeeEntity> select = cq.select(root);
        List<Predicate> predicates = new ArrayList<>();

        if (supervisorId != null) {
            predicates.add(cb.equal(root.get("supervisor").get("employeeId"), supervisorId));
        }
        if (approverId != null) {
            predicates.add(cb.equal(root.get("timesheetApprover").get("employeeId"), approverId));
        }
        if (role != null) {
            predicates.add(cb.equal(root.get("role"), role));
        }

        Predicate finalPredicate = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        select.where(finalPredicate);
        select.orderBy(cb.asc(root.get("employeeId")));

        TypedQuery<EmployeeEntity> typedQuery = em.createQuery(select);
        if (page != null && size != null) {
            typedQuery.setFirstResult(page * size);
            typedQuery.setMaxResults(size);
        }

        return typedQuery.getResultList();
    }

    /**
     * Get all employees by role
     * @return List of employeeEntities
     */
    public List<EmployeeEntity> findAllByRole(String role) {
        return em.createQuery("SELECT e FROM EmployeeEntity e WHERE e.role = :role", EmployeeEntity.class).setParameter("role", role).getResultList();
    }
    public void flush() {
        em.flush();
    }
}
