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
import ru.bsc.test.at.executor.model.Project;
import ru.bsc.test.autotester.exception.ResourceNotFoundException;
import ru.bsc.test.autotester.mapper.ProjectRoMapper;
import ru.bsc.test.autotester.mapper.ScenarioRoMapper;
import ru.bsc.test.autotester.ro.ProjectRo;
import ru.bsc.test.autotester.ro.ProjectSearchRo;
import ru.bsc.test.autotester.ro.ScenarioRo;
import ru.bsc.test.autotester.service.ProjectService;
import ru.bsc.test.autotester.service.ScenarioService;

import java.io.IOException;
import java.util.List;

/**
 * Created by sdoroshin on 12.09.2017.
 *
 */

@RestController
@RequestMapping("/rest/projects")
public class RestProjectController {
    private final ProjectService projectService;
    private final ScenarioService scenarioService;
    private final ProjectRoMapper projectRoMapper;
    private final ScenarioRoMapper scenarioRoMapper;

    @Autowired
    public RestProjectController(
            ProjectService projectService,
            ScenarioService scenarioService,
            ProjectRoMapper projectRoMapper,
            ScenarioRoMapper scenarioRoMapper) {
        this.projectService = projectService;
        this.scenarioService = scenarioService;
        this.projectRoMapper = projectRoMapper;
        this.scenarioRoMapper = scenarioRoMapper;
    }

    @RequestMapping
    public List<ProjectRo> findAll() {
        return projectRoMapper.convertProjectListToProjectRoList(projectService.findAll());
    }

    @RequestMapping(value = "{projectId}", method = RequestMethod.GET)
    public ProjectRo findOne(@PathVariable Long projectId) {
        Project project = projectService.findOne(projectId);
        if (project != null) {
            return projectRoMapper.projectToProjectRo(project);
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping(value = "{projectId}", method = RequestMethod.PUT)
    public ProjectRo saveOne(@PathVariable Long projectId, @RequestBody ProjectRo projectRo) {
        return projectService.updateFromRo(projectId, projectRo);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ProjectRo create(@RequestBody ProjectRo projectRo) throws IOException {
        return projectService.createFromRo(projectRo);
    }

    @RequestMapping(value = "{projectId}/scenarios", method = RequestMethod.GET)
    public List<ScenarioRo> getScenarios(@PathVariable Long projectId) {
        return scenarioRoMapper.convertScenarioListToScenarioRoList(scenarioService.findAllByProject(projectId));
    }

    @RequestMapping(value = "{projectId}/scenarios", method = RequestMethod.POST)
    public ScenarioRo newScenario(@PathVariable Long projectId, @RequestBody ScenarioRo scenarioRo) throws IOException {
        ScenarioRo savedScenario = scenarioService.addScenarioToProject(scenarioRo);
        if (savedScenario == null) {
            throw new ResourceNotFoundException();
        }
        return savedScenario;
    }

    @RequestMapping(value = "search", method = RequestMethod.POST)
    public List<ScenarioRo> searchByMethod(@RequestBody ProjectSearchRo projectSearchRo) {
        return scenarioService.findScenarioByStepRelativeUrl(projectSearchRo);
    }

}
