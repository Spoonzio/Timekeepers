package com.comp4911.backend.models;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "credential", schema = "timesheet")
public class CredentialEntity {
    private String userName;
    private String password;
    private String token;
    private Timestamp timestamp;
    private EmployeeEntity employeeEntity;

    @Id
    @Column(name = "userName")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Basic
    @Column(name = "timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CredentialEntity that = (CredentialEntity) o;

        if (userName != null ? !userName.equals(that.userName) : that.userName != null)
            return false;
        if (password != null ? !password.equals(that.password) : that.password != null)
            return false;
        if (token != null ? !token.equals(that.token) : that.token != null)
            return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @OneToOne
    @JoinColumn(name = "employeeNum", referencedColumnName = "employeeNum", nullable = false)
    public EmployeeEntity getEmployeeEntity() {
        return employeeEntity;
    }

    public void setEmployeeEntity(EmployeeEntity employeeEntity) {
        this.employeeEntity = employeeEntity;
    }
}
