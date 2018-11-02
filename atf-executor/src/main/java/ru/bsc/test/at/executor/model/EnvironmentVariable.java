package ru.bsc.test.at.executor.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table
public class EnvironmentVariable {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn
    private ExecutionEnvironment executionEnvironment;
    private String key;
    private String value;

    public EnvironmentVariable copy() {
        EnvironmentVariable newDomain = new EnvironmentVariable();
        newDomain.setKey(getKey());
        newDomain.setValue(getValue());
        return newDomain;
    }
}
