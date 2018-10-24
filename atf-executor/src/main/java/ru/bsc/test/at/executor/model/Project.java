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

package ru.bsc.test.at.executor.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Created by sdoroshin on 10.05.2017.
 */
@Getter
@Setter
@Entity
@Table(name = "project")
public class Project implements Serializable, AbstractModel {
    private static final long serialVersionUID = 7331632683933716938L;

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @OneToMany(mappedBy = "project")
    private List<Scenario> scenarioList = new LinkedList<>();
    @ManyToOne
    @JoinColumn(name = "before_scenario_path")
    private Scenario beforeScenario;
    @ManyToOne
    @JoinColumn(name = "after_scenario_path")
    private Scenario afterScenario;
    private Stand stand;
    private Boolean useRandomTestId;
    private String testIdHeaderName;
    private AmqpBroker amqpBroker;
    private Long mqCheckInterval;
    private Integer mqCheckCount;
    private String directoryPath;

    public Project copy() {
        Project project = new Project();
        project.setName(getName());
        project.setUseRandomTestId(getUseRandomTestId());
        project.setTestIdHeaderName(getTestIdHeaderName());
        project.setStand(getStand().copy());
        if (getScenarioList() != null) {
            project.setScenarioList(new LinkedList<>());
            for (Scenario scenario : getScenarioList()) {
                Scenario projectScenario = scenario.copy();
                projectScenario.setScenarioGroup(scenario.getScenarioGroup());
                project.getScenarioList().add(projectScenario);
            }
        }

        project.setAmqpBroker(getAmqpBroker().copy());
        return project;
    }

    @Override
    public String toString() {
        return "Project{" + id + '}';
    }

    public Boolean getUseRandomTestId() {
        return useRandomTestId != null && useRandomTestId;
    }
}
