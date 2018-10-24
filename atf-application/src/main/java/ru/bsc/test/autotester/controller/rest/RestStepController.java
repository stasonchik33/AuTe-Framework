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
import ru.bsc.test.autotester.exception.ResourceNotFoundException;
import ru.bsc.test.autotester.ro.StepRo;
import ru.bsc.test.autotester.service.ScenarioService;

import java.io.IOException;

@RestController
@RequestMapping("/rest/step")
public class RestStepController {

    private final ScenarioService scenarioService;

    @Autowired
    public RestStepController(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }

    @RequestMapping(value = "{scenarioId}/{stepId}", method = RequestMethod.PUT)
    public StepRo updateOne(
            @PathVariable Long scenarioId,
            @PathVariable Long stepId,
            @RequestBody StepRo stepRo) throws IOException {
        StepRo updatedStep = scenarioService.updateStepFromRo(scenarioId, stepId, stepRo);
        if (updatedStep == null) {
            throw new ResourceNotFoundException();
        }
        return updatedStep;
    }
}
