package com.dmitring.yainterfaceliftdownloader.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * It's a domain class to save/load app variables between app restarts
 */
@Entity
@Data
@NoArgsConstructor
public class ApplicationVariable {
    @Id
    private String variableName;

    @Column
    private String variableValue;

    public ApplicationVariable(String variableName, String variableValue) {
        this.variableName = variableName;
        this.variableValue = variableValue;
    }
}
