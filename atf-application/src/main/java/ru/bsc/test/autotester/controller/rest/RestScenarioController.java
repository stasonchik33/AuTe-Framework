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

package ru.bsc.test.autotester.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.bsc.test.at.executor.model.Scenario;
import ru.bsc.test.autotester.exception.ResourceNotFoundException;
import ru.bsc.test.autotester.mapper.ScenarioRoMapper;
import ru.bsc.test.autotester.mapper.StepRoMapper;
import ru.bsc.test.autotester.ro.ScenarioRo;
import ru.bsc.test.autotester.ro.StartScenarioInfoRo;
import ru.bsc.test.autotester.ro.StartScenarioRo;
import ru.bsc.test.autotester.ro.StepRo;
import ru.bsc.test.autotester.service.ScenarioService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by sdoroshin on 14.09.2017.
 *
 */

@RestController
@RequestMapping("rest/scenario")
public class RestScenarioController {

    private final ScenarioService scenarioService;
    private final StepRoMapper stepRoMapper;
    private final ScenarioRoMapper scenarioRoMapper;

    @Autowired
    public RestScenarioController(
            ScenarioService scenarioService,
            StepRoMapper stepRoMapper,
            ScenarioRoMapper scenarioRoMapper) {
        this.scenarioService = scenarioService;
        this.stepRoMapper = stepRoMapper;
        this.scenarioRoMapper = scenarioRoMapper;
    }

    @RequestMapping(value = { "{scenarioId}/steps"}, method = RequestMethod.GET)
    public List<StepRo> findSteps(@PathVariable Long scenarioId) {
        Scenario scenario = scenarioService.findOne(scenarioId);
        if (scenario != null) {
            return stepRoMapper.convertStepListToStepRoList(scenario.getStepList());
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping(value = "{scenarioId}", method = RequestMethod.GET)
    public ScenarioRo findOne(@PathVariable Long scenarioId) {
        Scenario scenario = scenarioService.findOne(scenarioId);
        if (scenario != null) {
            return scenarioRoMapper.scenarioToScenarioRo(scenario);
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping(value = "{scenarioId}", method = RequestMethod.PUT)
    public ScenarioRo saveOne(@PathVariable Long scenarioId, @RequestBody ScenarioRo scenarioRo) throws IOException {
        ScenarioRo savedScenario = scenarioService.updateScenarioFormRo(scenarioId, scenarioRo);
        if (savedScenario == null) {
            throw new ResourceNotFoundException();
        }
        return savedScenario;
    }

    @RequestMapping(value = "{scenarioId}", method = RequestMethod.DELETE)
    public void deleteOne(@PathVariable Long scenarioId) {
        scenarioService.deleteOne(scenarioId);
    }

    @RequestMapping(value = "{scenarioId}/steps", method = RequestMethod.PUT)
    public List<StepRo> saveStepList(
            @PathVariable Long scenarioId,
            @RequestBody List<StepRo> stepRoList) throws IOException {
        return scenarioService.updateStepListFromRo(scenarioId, stepRoList);
    }

    @RequestMapping(value = "start", method = RequestMethod.POST)
    public StartScenarioInfoRo executing(@RequestBody StartScenarioRo startScenario) {
        Scenario scenario = scenarioService.findOne(startScenario.getScenarioId());
        if (scenario != null) {
            return scenarioService.startScenarioExecutingList(scenario.getProject(), Collections.singletonList(scenario), startScenario.getExecutionEnvironmentId());
        }
        throw new ResourceNotFoundException();
    }
}
