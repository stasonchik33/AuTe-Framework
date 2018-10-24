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

package ru.bsc.test.autotester.launcher.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bsc.test.at.executor.model.Project;
import ru.bsc.test.at.executor.model.Scenario;
import ru.bsc.test.at.executor.model.ScenarioResult;
import ru.bsc.test.at.executor.service.AtProjectExecutor;
import ru.bsc.test.at.executor.service.IExecutingFinishObserver;
import ru.bsc.test.at.executor.service.IStopObserver;
import ru.bsc.test.at.executor.service.api.ProjectExecutorRequest;
import ru.bsc.test.autotester.launcher.api.ScenarioLauncher;
import ru.bsc.test.autotester.model.ExecutionResult;
import ru.bsc.test.autotester.repository.ScenarioRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

/**
 * @author Pavel Golovkin
 */
@Component
@Slf4j
public class ScenarioLauncherImpl implements ScenarioLauncher {

    private static final ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    private final ScenarioRepository scenarioRepository;

    @Autowired
    public ScenarioLauncherImpl(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    public void launchScenarioFromUI(
            List<Scenario> scenarioToExecute,
            Project project,
            ExecutionResult executionResult,
            Set<String> stopExecutingSet,
            String runningUuid
    ) {
        log.info("Launch scenario from UI {} {}", scenarioToExecute, project);
        AtProjectExecutor atExecutor = new AtProjectExecutor(Paths.get(project.getDirectoryPath(), project.getId().toString())
                .toString());

        new Thread(() -> {
            IStopObserver stopObserver = () -> stopExecutingSet.remove(runningUuid);
            IExecutingFinishObserver finishObserver = scenarioResults -> {
                executionResult.setFinished(true);
                synchronized (scenarioRepository) {
                    processResults(scenarioResults);
                }
            };
            atExecutor.execute(new ProjectExecutorRequest(project, scenarioToExecute, executionResult.getScenarioResults(), stopObserver, finishObserver));
        }).start();
    }


    private void processResults(List<ScenarioResult> scenarioResults) {
        for (ScenarioResult scenarioResult : scenarioResults) {
            try {
                Scenario scenario = scenarioResult.getScenario();
                scenario.setFailed(scenarioResult.isFailed());
                scenario = scenarioRepository.save(scenario);

                Path path = Paths.get("tmp", "results", scenario.getId().toString());
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                Path resultFile = path.resolve("results.json");
                Files.deleteIfExists(resultFile);
                objectMapper.writeValue(resultFile.toFile(), scenarioResult.getStepResultList());
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }
}
