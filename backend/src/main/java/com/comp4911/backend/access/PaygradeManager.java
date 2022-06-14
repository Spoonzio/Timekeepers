package com.comp4911.backend.access;

import com.comp4911.backend.models.PaygradeEntity;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Dependent
@Stateless
public class PaygradeManager implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Entity manager.
     */
    @PersistenceContext(unitName = "timesheet-jpa")
    private EntityManager em;

    /**
     * find paygrade by grade
     */
    public PaygradeEntity find(String grade, String year) {
        TypedQuery<PaygradeEntity> query = em.createQuery("SELECT p FROM PaygradeEntity p WHERE p.grade = :grade AND p.year = :year", PaygradeEntity.class)
                .setParameter("grade", grade)
                .setParameter("year", year);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * find paygrade by grade
     */
    public List<PaygradeEntity> find(String grade) {
        TypedQuery<PaygradeEntity> query = em.createQuery("SELECT p FROM PaygradeEntity p WHERE p.grade = :grade", PaygradeEntity.class)
                .setParameter("grade", grade);
        return query.getResultList();
    }

    /**
     * persist paygrade.
     */
    public void persist(PaygradeEntity paygrade) {
        em.persist(paygrade);
    }

    /**
     * merge paygrade.
     */
    public PaygradeEntity merge(PaygradeEntity paygrade) {
        return em.merge(paygrade);
    }

    /**
     * remove paygrade.
     */
    public void remove(PaygradeEntity paygrade) {
        paygrade = find(paygrade.getGrade(), paygrade.getYear());
        em.remove(paygrade);
    }

    public List<PaygradeEntity> findAll() {
        return em.createQuery("SELECT p FROM PaygradeEntity p", PaygradeEntity.class).getResultList();
    }
}
