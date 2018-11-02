package ru.bsc.test.at.executor.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table
public class ExecutionEnvironment {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn
    private Project project;
    private String name;

    @OneToMany(mappedBy = "executionEnvironment", orphanRemoval = true, cascade = CascadeType.ALL)
    List<EnvironmentVariable> environmentVariableList;

    public ExecutionEnvironment copy() {
        ExecutionEnvironment newDomain = new ExecutionEnvironment();
        newDomain.setName(getName());
        if (getEnvironmentVariableList() != null) {
            newDomain.setEnvironmentVariableList(getEnvironmentVariableList().stream().map(EnvironmentVariable::copy).collect(Collectors.toList()));
        }
        return newDomain;
    }
}
