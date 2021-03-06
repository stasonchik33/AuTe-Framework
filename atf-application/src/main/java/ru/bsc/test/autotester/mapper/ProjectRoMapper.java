/*
 * Copyright 2018 BSC Msc, LLC
 *
 * This file is part of the AuTe Framework project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.bsc.test.autotester.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import ru.bsc.test.at.executor.model.AmqpBroker;
import ru.bsc.test.at.executor.model.EnvironmentVariable;
import ru.bsc.test.at.executor.model.ExecutionEnvironment;
import ru.bsc.test.at.executor.model.Project;
import ru.bsc.test.at.executor.model.Stand;
import ru.bsc.test.autotester.ro.AmqpBrokerRo;
import ru.bsc.test.autotester.ro.EnvironmentVariableRo;
import ru.bsc.test.autotester.ro.ExecutionEnvironmentRo;
import ru.bsc.test.autotester.ro.ProjectRo;
import ru.bsc.test.autotester.ro.StandRo;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sdoroshin on 15.09.2017.
 *
 */

@Mapper(config = Config.class)
public abstract class ProjectRoMapper {

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "beforeScenario", source = "beforeScenario.id"),
            @Mapping(target = "afterScenario", source = "afterScenario.id"),
            @Mapping(target = "stand", source = "stand"),
            @Mapping(target = "useRandomTestId", source = "useRandomTestId"),
            @Mapping(target = "testIdHeaderName", source = "testIdHeaderName"),
    })
    abstract public ProjectRo projectToProjectRo(Project project);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "beforeScenario", ignore = true),
            @Mapping(target = "afterScenario", ignore = true),
            @Mapping(target = "stand", source = "stand"),
            @Mapping(target = "amqpBroker", source = "amqpBroker"),
            @Mapping(target = "useRandomTestId", source = "useRandomTestId"),
            @Mapping(target = "testIdHeaderName", source = "testIdHeaderName"),
            @Mapping(target = "scenarioList", ignore = true),
            @Mapping(target = "mqCheckInterval", ignore = true),
            @Mapping(target = "mqCheckCount", ignore = true),
            @Mapping(target = "executionEnvironmentList", ignore = true),
    })
    abstract void updateProjectFromRo(ProjectRo projectRo, @MappingTarget Project project);

    public void updateProject(ProjectRo ro, Project domain) {
        updateProjectFromRo(ro, domain);

        if (domain.getExecutionEnvironmentList() == null) {
            domain.setExecutionEnvironmentList(new LinkedList<>());
        }
        if (ro.getExecutionEnvironmentList() == null) {
            ro.setExecutionEnvironmentList(new LinkedList<>());
        }
        List<ExecutionEnvironment> setStepParameterList = new LinkedList<>(domain.getExecutionEnvironmentList());
        domain.getExecutionEnvironmentList().clear();
        domain.getExecutionEnvironmentList().addAll(ro.getExecutionEnvironmentList().stream()
                .map(executionEnvironmentRo -> setStepParameterList.stream()
                        .filter(stepParameter -> Objects.equals(stepParameter.getId(), executionEnvironmentRo.getId()))
                        .map(executionEnvironment -> updateExecutionEnvironment(executionEnvironmentRo, executionEnvironment))
                        .findAny()
                        .orElseGet(() -> {
                            ExecutionEnvironment newExecutionEnvironment = new ExecutionEnvironment();
                            updateExecutionEnvironment(executionEnvironmentRo, newExecutionEnvironment);
                            newExecutionEnvironment.setProject(domain);
                            return newExecutionEnvironment;
                        })
                )
                .collect(Collectors.toList())
        );

        domain.setBeforeScenario(
                ro.getBeforeScenario() == null ? null :
                        domain.getScenarioList().stream()
                                .filter(scenario -> Objects.equals(scenario.getId(), ro.getBeforeScenario()))
                                .findAny().orElse(null)
        );

        domain.setAfterScenario(
                ro.getAfterScenario() == null ? null :
                        domain.getScenarioList().stream()
                                .filter(scenario -> Objects.equals(scenario.getId(), ro.getAfterScenario()))
                                .findAny().orElse(null)
        );
    }

    abstract public List<ProjectRo> convertProjectListToProjectRoList(List<Project> list);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "project", ignore = true),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "environmentVariableList", ignore = true)
    })
    abstract void updateExecutionEnvironmentFromRo(ExecutionEnvironmentRo ro, @MappingTarget ExecutionEnvironment domain);

    ExecutionEnvironment updateExecutionEnvironment(ExecutionEnvironmentRo ro, @MappingTarget ExecutionEnvironment domain) {
        updateExecutionEnvironmentFromRo(ro, domain);

        if (domain.getEnvironmentVariableList() == null) {
            domain.setEnvironmentVariableList(new LinkedList<>());
        }
        if (ro.getEnvironmentVariableList() == null) {
            ro.setEnvironmentVariableList(new LinkedList<>());
        }
        List<EnvironmentVariable> environmentVariableList = new LinkedList<>(domain.getEnvironmentVariableList());
        domain.getEnvironmentVariableList().clear();
        domain.getEnvironmentVariableList().addAll(ro.getEnvironmentVariableList().stream()
                .map(roItem -> environmentVariableList.stream()
                        .filter(domainItem -> Objects.equals(domainItem.getId(), roItem.getId()))
                        .map(domainItem -> updateEnvironmentVariable(roItem, domainItem))
                        .findAny()
                        .orElseGet(() -> {
                            EnvironmentVariable newDomainItem = new EnvironmentVariable();
                            updateEnvironmentVariable(roItem, newDomainItem);
                            newDomainItem.setExecutionEnvironment(domain);
                            return newDomainItem;
                        })
                )
                .collect(Collectors.toList())
        );

        return domain;
    }

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "executionEnvironment", ignore = true),
            @Mapping(target = "key", source = "key"),
            @Mapping(target = "value", source = "value")
    })
    abstract EnvironmentVariable updateEnvironmentVariable(EnvironmentVariableRo ro, @MappingTarget EnvironmentVariable domain);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "serviceUrl", source = "serviceUrl"),
            @Mapping(target = "dbUrl", source = "dbUrl"),
            @Mapping(target = "dbUser", source = "dbUser"),
            @Mapping(target = "dbPassword", source = "dbPassword"),
            @Mapping(target = "wireMockUrl", source = "wireMockUrl")
    })
    abstract StandRo standToStandRo(Stand stand);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "project", ignore = true),
            @Mapping(target = "serviceUrl", source = "serviceUrl"),
            @Mapping(target = "dbUrl", source = "dbUrl"),
            @Mapping(target = "dbUser", source = "dbUser"),
            @Mapping(target = "dbPassword", source = "dbPassword"),
            @Mapping(target = "wireMockUrl", source = "wireMockUrl")
    })
    abstract Stand updateStandFromRo(StandRo standRo);

    @Mappings({
            @Mapping(target = "mqService", source = "mqService"),
            @Mapping(target = "host", source = "host"),
            @Mapping(target = "port", source = "port"),
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "password", source = "password"),

    })
    public abstract AmqpBroker updateAmqpBrokerFromRo(AmqpBrokerRo amqpBrokerRo);

    @Mappings({
            @Mapping(target = "mqService", source = "mqService"),
            @Mapping(target = "host", source = "host"),
            @Mapping(target = "port", source = "port"),
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "password", source = "password")
    })
    public abstract AmqpBrokerRo convertAmqpBrokerToRo(AmqpBroker amqpBroker);
}
