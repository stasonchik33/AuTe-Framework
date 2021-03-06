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
import ru.bsc.test.at.executor.model.Scenario;
import ru.bsc.test.autotester.ro.ScenarioRo;

import java.util.List;

@Mapper(config = Config.class)
public abstract class ScenarioRoMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "project", ignore = true),
            @Mapping(target = "stepList", ignore = true)
    })
    abstract void updateScenarioFromRo(ScenarioRo scenarioRo, @MappingTarget Scenario scenario);

    public Scenario updateScenario(ScenarioRo scenarioRo, @MappingTarget Scenario scenario) {
        updateScenarioFromRo(scenarioRo, scenario);

        return scenario;
    }

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "projectId", source = "project.id"),
            @Mapping(target = "projectName", source = "project.name"),
            @Mapping(target = "scenarioGroup", source = "scenarioGroup"),
            @Mapping(target = "stepList", ignore = true),
            @Mapping(target = "beforeScenarioIgnore", source = "beforeScenarioIgnore"),
            @Mapping(target = "afterScenarioIgnore", source = "afterScenarioIgnore")
    })
    public abstract ScenarioRo scenarioToScenarioRo(Scenario scenario);

    public abstract List<ScenarioRo> convertScenarioListToScenarioRoList(List<Scenario> scenarioList);
}
