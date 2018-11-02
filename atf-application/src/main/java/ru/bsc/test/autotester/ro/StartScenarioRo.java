package ru.bsc.test.autotester.ro;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartScenarioRo implements AbstractRo {
    private Long scenarioId;
    private Long executionEnvironmentId;
}
