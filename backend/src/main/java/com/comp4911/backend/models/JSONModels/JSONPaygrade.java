package com.comp4911.backend.models.JSONModels;

import com.comp4911.backend.models.PaygradeEntity;

import java.math.BigDecimal;

public class JSONPaygrade {

    private String year;
    private String grade;
    private BigDecimal rate;

    public JSONPaygrade() {}

    public JSONPaygrade(PaygradeEntity paygradeEntity) {
        this.grade = paygradeEntity.getGrade();
        this.rate = paygradeEntity.getRate();
        this.year = paygradeEntity.getYear();
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
