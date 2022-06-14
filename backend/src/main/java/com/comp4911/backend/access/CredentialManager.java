package com.comp4911.backend.access;

import com.comp4911.backend.models.CredentialEntity;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Dependent
@Stateless
public class CredentialManager implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Entity manager.
     */
    @PersistenceContext(unitName = "timesheet-jpa")
    private EntityManager em;

    /**
     * finds a credential by its username.
     */
    public CredentialEntity find(String username) {
        return em.find(CredentialEntity.class, username);
    }

    /**
     * finds a credential by its username.
     */
    public CredentialEntity findByEmpID(String employeeID) {
        if (employeeID == null) {
            return null;
        }

        TypedQuery<CredentialEntity> tq = em.createQuery("SELECT p FROM CredentialEntity p WHERE p.employeeEntity.employeeId = :eId", CredentialEntity.class);
        tq.setParameter("eId", employeeID);
        return tq.getSingleResult();
    }

    /**
     * persists a credential.
     */
    public void persist(CredentialEntity credential) {
        em.persist(credential);
    }

    /**
     * merges a credential.
     */
    public void merge(CredentialEntity credential) {
        em.merge(credential);
    }

    /**
     * removes a credential.
     */
    public void remove(CredentialEntity credential) {
        credential = find(credential.getUserName());
        em.remove(credential);
    }

    public CredentialEntity findByToken(String token) {
        if (token == null) {
            return null;
        }
        TypedQuery<CredentialEntity> tq = em.createQuery("SELECT p FROM CredentialEntity p WHERE p.token = :token", CredentialEntity.class);
        tq.setParameter("token", token);


        List<CredentialEntity> ce = tq.getResultList();
        if (ce.size() != 0) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (ce.get(0).getTimestamp().before(now)) {
                return null;
            }
        } else {
            return null;
        }

        return ce.get(0);
    }
}
