package com.comp4911.backend.models.JSONModels;

import com.comp4911.backend.models.CredentialEntity;

public class JSONCredential {
    private String userName;
    private Integer employeeNum;

    public JSONCredential() {}

    public JSONCredential(CredentialEntity credential) {
        this.userName = credential.getUserName();
        this.employeeNum = credential.getEmployeeEntity().getEmployeeNum();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getEmployeeNum() {
        return employeeNum;
    }

    public void setEmployeeNum(Integer employeeNum) {
        this.employeeNum = employeeNum;
    }
}
