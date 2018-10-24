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

package ru.bsc.test.autotester.service.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bsc.test.at.executor.model.*;
import ru.bsc.test.autotester.exception.ResourceNotFoundException;
import ru.bsc.test.autotester.launcher.api.ScenarioLauncher;
import ru.bsc.test.autotester.mapper.ProjectRoMapper;
import ru.bsc.test.autotester.mapper.ScenarioRoMapper;
import ru.bsc.test.autotester.mapper.StepRoMapper;
import ru.bsc.test.autotester.model.ExecutionResult;
import ru.bsc.test.autotester.properties.EnvironmentProperties;
import ru.bsc.test.autotester.report.AbstractReportGenerator;
import ru.bsc.test.autotester.repository.ProjectRepository;
import ru.bsc.test.autotester.repository.ScenarioRepository;
import ru.bsc.test.autotester.repository.StepRepository;
import ru.bsc.test.autotester.ro.*;
import ru.bsc.test.autotester.service.ProjectService;
import ru.bsc.test.autotester.service.ScenarioService;
import ru.bsc.test.autotester.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.ZipOutputStream;

/**
 * Created by sdoroshin on 21.03.2017.
 */
@Service
@Slf4j
public class ScenarioServiceImpl implements ScenarioService {
    //@formatter:off
    private static final TypeReference<List<StepResult>> RESULT_LIST_TYPE = new TypeReference<List<StepResult>>(){};
    //@formatter:on
    private final ConcurrentMap<String, ExecutionResult> runningScriptsMap = new ConcurrentHashMap<>();
    private final Set<String> stopExecutingSet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private final ObjectMapper objectMapper = new ObjectMapper().setVisibility(
            PropertyAccessor.FIELD,
            JsonAutoDetect.Visibility.ANY
    );
    private final StepRoMapper stepRoMapper;
    private final ScenarioRoMapper scenarioRoMapper;
    private final ProjectRoMapper projectRoMapper;
    private final ScenarioRepository scenarioRepository;
    private final StepRepository stepRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final EnvironmentProperties environmentProperties;
    private final AbstractReportGenerator reportGenerator;
    private final ScenarioLauncher scenarioLauncher;

    @Autowired
    public ScenarioServiceImpl(
            StepRoMapper stepRoMapper,
            ScenarioRoMapper scenarioRoMapper,
            ProjectRoMapper projectRoMapper,
            ScenarioRepository scenarioRepository,
            StepRepository stepRepository, ProjectRepository projectRepository, ProjectService projectService,
            EnvironmentProperties environmentProperties,
            AbstractReportGenerator reportGenerator,
            ScenarioLauncher scenarioLauncher
    ) {
        this.stepRoMapper = stepRoMapper;
        this.scenarioRoMapper = scenarioRoMapper;
        this.projectRoMapper = projectRoMapper;
        this.scenarioRepository = scenarioRepository;
        this.stepRepository = stepRepository;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.environmentProperties = environmentProperties;
        this.reportGenerator = reportGenerator;
        this.scenarioLauncher = scenarioLauncher;
    }

    @Override
    public StartScenarioInfoRo startScenarioExecutingList(Project project, List<Scenario> scenarioList) {
        loadBeforeAndAfterScenarios(project, scenarioList);
        StartScenarioInfoRo startScenarioInfoRo = new StartScenarioInfoRo();

        ExecutionResult executionResult = new ExecutionResult();
        final List<ScenarioResult> scenarioResultList = new ArrayList<>();
        executionResult.setScenarioResults(scenarioResultList);
        final String runningUuid = UUID.randomUUID().toString();
        startScenarioInfoRo.setRunningUuid(runningUuid);
        runningScriptsMap.put(runningUuid, executionResult);

        scenarioLauncher.launchScenarioFromUI(scenarioList, project, environmentProperties, executionResult, stopExecutingSet, projectService, runningUuid );
        return startScenarioInfoRo;
    }



    @Override
    public void stopExecuting(String executingUuid) {
        stopExecutingSet.add(executingUuid);
    }

    @Override
    public List<String> getExecutingList() {
        return new LinkedList<>(runningScriptsMap.keySet());
    }

    @Override
    public ExecutionResult getResult(String executingUuid) {
        return runningScriptsMap.get(executingUuid);
    }

    @Override
    public List<StepResult> getResult(Long identity) {
        try {
            return loadResults(identity);
        } catch (IOException e) {
            log.error("Error while loading scenario", e);
        }
        return null;
    }

    @Override
    public void getReport(List<Long> identities, ZipOutputStream outputStream) throws Exception {
        log.info("Scenario identities to generate report: {}", identities.size());
        reportGenerator.clear();
        identities.forEach(identity -> {
            try {
                List<StepResult> results = loadResults(identity);
                if (results != null) {
                    Scenario scenario = scenarioRepository.getOne(identity);
                    reportGenerator.add(scenario, results);
                }
            } catch (IOException e) {
                log.error("Error while loading scenario", e);
            }
        });
        if (reportGenerator.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        File tmpDirectory = new File("tmp", UUID.randomUUID().toString());
        if (tmpDirectory.mkdirs()) {
            reportGenerator.generate(tmpDirectory);
            ZipUtils.pack(tmpDirectory, outputStream);
        } else {
            throw new FileAlreadyExistsException(tmpDirectory.getAbsolutePath());
        }
    }

    @Override
    public StepRo addStepToScenario(Long scenarioId, StepRo stepRo) throws IOException {
        Scenario scenario = scenarioRepository.findOne(scenarioId);
        if (scenario != null) {
            Step newStep = stepRoMapper.convertStepRoToStep(stepRo);
            scenario.getStepList().add(newStep);
            scenarioRepository.save(scenario);
            return stepRoMapper.stepToStepRo(newStep);
        }
        return null;
    }

    @Override
    public Scenario saveScenario(String projectCode, String scenarioPath, Scenario scenario) throws IOException {
        return scenarioRepository.save(scenario);
    }

    @Override
    public void deleteOne(Long id) throws IOException {
        scenarioRepository.delete(id);
    }

    @Override
    public ScenarioRo updateScenarioFormRo(Long id, ScenarioRo scenarioRo) {
        Scenario scenario = scenarioRepository.findOne(id);
        if (scenario != null) {
            scenario = scenarioRoMapper.updateScenario(scenarioRo, scenario);
            scenario = scenarioRepository.save(scenario);
            return projectRoMapper.scenarioToScenarioRo(scenario);
        }
        return null;
    }

    @Override
    public Scenario findOne(Long id) {
        return scenarioRepository.findOne(id);
    }

    @Override
    public Step cloneStep(Long id, String stepCode) {
        Scenario scenario = scenarioRepository.findOne(id);
        Step existsStep = scenario.getStepList().stream()
                .filter(step -> Objects.equals(step.getCode(), stepCode))
                .findAny()
                .orElse(null);
        if (existsStep != null) {
            Step newStep = existsStep.copy();
            scenario.getStepList().add(newStep);
            return newStep;
        }
        return null;
    }

    @Override
    public List<StepRo> updateStepListFromRo(Long id, List<StepRo> stepRoList) {
        Scenario scenario = findOne(id);
        if (scenario != null) {
            stepRoMapper.updateScenarioStepList(stepRoList, scenario);
            scenario = scenarioRepository.save(scenario);
            return stepRoMapper.convertStepListToStepRoList(scenario.getStepList());
        }
        throw new ResourceNotFoundException();
    }

    @Override
    public List<ScenarioRo> findScenarioByStepRelativeUrl(String projectCode, ProjectSearchRo projectSearchRo) {
        List<Scenario> scenarios = new ArrayList<>();
        if (!StringUtils.isEmpty(projectSearchRo.getRelativeUrl())) {
            HashSet<Scenario> scenarioSet = new HashSet<>();
            stepRepository.findByRelativeUrl(projectSearchRo.getRelativeUrl()).forEach(step -> scenarioSet.add(step.getScenario()));
            scenarios = new ArrayList<>(scenarioSet);
        }
        return projectRoMapper.convertScenarioListToScenarioRoList(scenarios);
    }

    @Override
    public StepRo updateStepFromRo(Long scenarioId, String stepCode, StepRo stepRo) {
        Scenario scenario = scenarioRepository.findOne(scenarioId);
        Step existsStep = scenario.getStepList().stream()
                .filter(step -> Objects.equals(step.getCode(), stepCode))
                .findAny()
                .orElse(null);
        stepRoMapper.updateStep(stepRo, existsStep);
        scenarioRepository.save(scenario);
        return stepRoMapper.stepToStepRo(existsStep);
    }

    @Override
    public List<Scenario> findAllByProject(Long projectId) {
        return projectRepository.getOne(projectId).getScenarioList();
    }

    @Override
    public ScenarioRo addScenarioToProject(String projectCode, ScenarioRo scenarioRo) throws IOException {
        Scenario newScenario = scenarioRoMapper.updateScenario(scenarioRo, new Scenario());
        newScenario = scenarioRepository.save(newScenario);
        return projectRoMapper.scenarioToScenarioRo(newScenario);
    }

    private void loadBeforeAndAfterScenarios(Project project, List<Scenario> scenarioList) {
        for (Scenario scenario : scenarioList) {
            if (!scenario.getBeforeScenarioIgnore() && project.getBeforeScenario() != null) {
                project.getScenarioList().add(project.getBeforeScenario());
            }
            if (!scenario.getAfterScenarioIgnore() && project.getAfterScenario() != null) {
                project.getScenarioList().add(project.getAfterScenario());
            }
        }
    }

    private List<StepResult> loadResults(Long identity) throws IOException {
        Path resultsFile = Paths.get(
                "tmp",
                "results",
                identity.toString(),
                "results.json"
        );
        List<StepResult> results = null;
        if (Files.exists(resultsFile)) {
            results = objectMapper.readValue(resultsFile.toFile(), RESULT_LIST_TYPE);
        }
        return results;
    }
}
