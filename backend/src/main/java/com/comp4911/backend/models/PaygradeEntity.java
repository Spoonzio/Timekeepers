package com.comp4911.backend.models;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;

@Entity
@Table(name = "paygrade", schema = "timesheet")
public class PaygradeEntity {
    private int paygradeId;
    private String grade;
    private BigDecimal rate;
    private String year;
    private Collection<EmployeeEntity> employeeEntities;

    @Id
    @Column(name = "paygradeId")
    public int getPaygradeId() {
        return paygradeId;
    }

    public void setPaygradeId(int paygradeId) {
        this.paygradeId = paygradeId;
    }

    @Column(name = "grade")
    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Column(name = "year")
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }


    @Basic
    @Column(name = "rate")
    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaygradeEntity that = (PaygradeEntity) o;

        if (grade != null ? !grade.equals(that.grade) : that.grade != null) return false;
        if (rate != null ? !rate.equals(that.rate) : that.rate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = grade != null ? grade.hashCode() : 0;
        result = 31 * result + (rate != null ? rate.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "paygradeEntity", fetch = FetchType.EAGER)
    public Collection<EmployeeEntity> getEmployeeEntities() {
        return employeeEntities;
    }

    public void setEmployeeEntities(Collection<EmployeeEntity> employeeEntities) {
        this.employeeEntities = employeeEntities;
    }
}
