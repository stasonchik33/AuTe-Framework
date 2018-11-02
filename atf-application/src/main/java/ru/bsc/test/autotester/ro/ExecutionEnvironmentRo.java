package ru.bsc.test.autotester.ro;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExecutionEnvironmentRo {

    private Long id;
    private String name;
    List<EnvironmentVariableRo> environmentVariableList;
}
