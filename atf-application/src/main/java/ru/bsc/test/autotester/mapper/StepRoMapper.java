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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bsc.test.at.executor.model.*;
import ru.bsc.test.autotester.component.JsonDiffCalculator;
import ru.bsc.test.autotester.ro.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sdoroshin on 14.09.2017.
 */

@Component
@SuppressWarnings({"unused", "WeakerAccess"})
@Mapper(config = Config.class)
public abstract class StepRoMapper {

    private JsonDiffCalculator diffCalculator;

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "scenario", ignore = true),
            @Mapping(target = "stepParameterSetList", ignore = true),
            @Mapping(target = "expectedServiceRequestList", ignore = true),
            @Mapping(target = "mockServiceResponseList", ignore = true),
            @Mapping(target = "sqlDataList", ignore = true),
    })
    public abstract void updateStepFromRo(StepRo stepRo, @MappingTarget Step step);

    public Step updateStep(StepRo stepRo, @MappingTarget Step step) {
        updateStepFromRo(stepRo, step);

        if (step.getMockServiceResponseList() == null) {
            step.setMockServiceResponseList(new LinkedList<>());
        }
        if (stepRo.getMockServiceResponseList() == null) {
            stepRo.setMockServiceResponseList(new LinkedList<>());
        }
        List<MockServiceResponse> stepMockServiceResponseList = new LinkedList<>(step.getMockServiceResponseList());
        step.getMockServiceResponseList().clear();
        step.getMockServiceResponseList().addAll(stepRo.getMockServiceResponseList().stream()
                .map(mockServiceResponseRo -> stepMockServiceResponseList.stream()
                        .filter(mockServiceResponse -> Objects.equals(mockServiceResponse.getId(), mockServiceResponseRo.getId()))
                        .map(mockServiceResponse -> updateMockServiceResponseFromRo(mockServiceResponseRo, mockServiceResponse))
                        .findAny()
                        .orElseGet(() -> {
                            MockServiceResponse newResponse = new MockServiceResponse();
                            updateMockServiceResponseFromRo(mockServiceResponseRo, newResponse);
                            newResponse.setStep(step);
                            return newResponse;
                        }))
                .collect(Collectors.toList())
        );

        if (step.getSqlDataList() == null) {
            step.setSqlDataList(new LinkedList<>());
        }
        if (stepRo.getSqlDataList() == null) {
            stepRo.setSqlDataList(new LinkedList<>());
        }
        List<SqlData> stepSqlDataList = new LinkedList<>(step.getSqlDataList());
        step.getSqlDataList().clear();
        step.getSqlDataList().addAll(stepRo.getSqlDataList().stream()
                .map(sqlDataRo -> stepSqlDataList.stream()
                        .filter(sqlData -> Objects.equals(sqlData.getId(), sqlDataRo.getId()))
                        .map(sqlData -> updateSqlDataFromRo(sqlDataRo, sqlData))
                        .findAny()
                        .orElseGet(() -> {
                            SqlData newSqlData = new SqlData();
                            updateSqlDataFromRo(sqlDataRo, newSqlData);
                            newSqlData.setStep(step);
                            return newSqlData;
                        })
                )
                .collect(Collectors.toList())
        );

        if (step.getStepParameterSetList() == null) {
            step.setStepParameterSetList(new LinkedList<>());
        }
        if (stepRo.getStepParameterSetList() == null) {
            stepRo.setStepParameterSetList(new LinkedList<>());
        }
        List<StepParameterSet> stepParameterSetList = new LinkedList<>(step.getStepParameterSetList());
        step.getStepParameterSetList().clear();
        step.getStepParameterSetList().addAll(stepRo.getStepParameterSetList().stream()
                .map(stepParameterSetRo -> stepParameterSetList.stream()
                        .filter(stepParameterSet -> Objects.equals(stepParameterSet.getId(), stepParameterSetRo.getId()))
                        .map(stepParameterSet -> updateStepParameterSet(stepParameterSetRo, stepParameterSet))
                        .findAny()
                        .orElseGet(() -> {
                            StepParameterSet newSet = new StepParameterSet();
                            updateStepParameterSet(stepParameterSetRo, newSet);
                            newSet.setStep(step);
                            return newSet;
                        }))
                .collect(Collectors.toList())
        );

        if (step.getExpectedServiceRequestList() == null) {
            step.setExpectedServiceRequestList(new LinkedList<>());
        }
        if (stepRo.getExpectedServiceRequestList() == null) {
            stepRo.setExpectedServiceRequestList(new LinkedList<>());
        }
        List<ExpectedServiceRequest> stepExpectedServiceRequestList = new LinkedList<>(step.getExpectedServiceRequestList());
        step.getExpectedServiceRequestList().clear();
        step.getExpectedServiceRequestList().addAll(stepRo.getExpectedServiceRequestList().stream()
                .map(expectedServiceRequestRo -> stepExpectedServiceRequestList.stream()
                        .filter(expectedServiceRequest -> Objects.equals(expectedServiceRequest.getId(), expectedServiceRequestRo.getId()))
                        .map(expectedServiceRequest -> updateExpectedServiceRequest(expectedServiceRequestRo, expectedServiceRequest))
                        .findAny()
                        .orElseGet(() -> {
                            ExpectedServiceRequest newRequest = new ExpectedServiceRequest();
                            updateExpectedServiceRequest(expectedServiceRequestRo, newRequest);
                            newRequest.setStep(step);
                            return newRequest;
                        }))
                .collect(Collectors.toList())
        );

        return step;
    }

    public Step convertStepRoToStep(StepRo stepRo) {
        return updateStep(stepRo, new Step());
    }

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "step", ignore = true),
    })
    abstract SqlData to(SqlDataRo ro);

    abstract SqlDataRo to(SqlData ro);

    abstract List<FormData> formDataRoListToFormData(List<FormDataRo> formDataRoList);

    public abstract StepRo stepToStepRo(Step step);

    public abstract List<StepRo> convertStepListToStepRoList(List<Step> stepList);

    abstract ExpectedServiceRequestRo expectedServiceRequestToRo(ExpectedServiceRequest expectedServiceRequest);

    abstract List<ExpectedServiceRequestRo> convertExpectedServiceRequestListToRoList(List<ExpectedServiceRequest> expectedServiceRequests);

    abstract List<StepParameterSetRo> convertStepParameterSetListToStepParameterSetRoList(List<StepParameterSet> stepParameterSetList);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "step", ignore = true),
            @Mapping(target = "stepParameterList", ignore = true),
    })
    abstract void updateStepParameterSetFromRo(StepParameterSetRo stepParameterSetRo, @MappingTarget StepParameterSet s);

    abstract StepParameterSetRo stepParameterSetToStepParameterSetRo(StepParameterSet stepParameterSet);

    private StepParameterSet updateStepParameterSet(StepParameterSetRo stepParameterSetRo, @MappingTarget StepParameterSet stepParameterSet) {
        updateStepParameterSetFromRo(stepParameterSetRo, stepParameterSet);

        if (stepParameterSet.getStepParameterList() == null) {
            stepParameterSet.setStepParameterList(new LinkedList<>());
        }
        if (stepParameterSetRo.getStepParameterList() == null) {
            stepParameterSetRo.setStepParameterList(new LinkedList<>());
        }
        List<StepParameter> setStepParameterList = new LinkedList<>(stepParameterSet.getStepParameterList());
        stepParameterSet.getStepParameterList().clear();
        stepParameterSet.getStepParameterList().addAll(stepParameterSetRo.getStepParameterList().stream()
                .map(stepParameterRo -> setStepParameterList.stream()
                        .filter(stepParameter -> Objects.equals(stepParameter.getId(), stepParameterRo.getId()))
                        .map(stepParameter -> updateStepParameterFromRo(stepParameterRo, stepParameter))
                        .findAny()
                        .orElseGet(() -> {
                            StepParameter newParameter = new StepParameter();
                            updateStepParameterFromRo(stepParameterRo, newParameter);
                            newParameter.setStepParameterSet(stepParameterSet);
                            return newParameter;
                        })
                )
                .collect(Collectors.toList())
        );

        return stepParameterSet;
    }

    abstract List<StepParameterRo> convertStepParameterToStepParameterRo(List<StepParameter> stepParameterList);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "stepParameterSet", ignore = true),
    })
    abstract StepParameter updateStepParameterFromRo(StepParameterRo stepParameterRo, @MappingTarget StepParameter stepParameter);

    abstract StepParameterRo stepParameterToStepParameterRo(StepParameter stepParameter);

    public abstract List<StepResultRo> convertStepResultListToStepResultRo(List<StepResult> stepResultList);

    public List<StepResultRo> convertStepResultListToStepResultRoWithDiff(List<StepResult> stepResultList) {
        List<StepResultRo> stepResultRos = convertStepResultListToStepResultRo(stepResultList);
        if (stepResultRos != null) {
            stepResultRos.stream()
                    .filter(Objects::nonNull)
                    .forEach(s -> s.setDiff(diffCalculator.calculate(s.getActual(), s.getExpected())));
        }
        return stepResultRos;
    }

    @Mappings({
            @Mapping(target = "diff", ignore = true),
            @Mapping(target = "result", source = "result.text")
    })
    abstract StepResultRo stepResultToStepResultRo(StepResult stepResult);

    abstract List<MockServiceResponseRo> convertMockServiceResponseListToMockServiceResponseRoList(List<MockServiceResponse> mockServiceResponseList);

    abstract MockServiceResponseRo mockServiceResponseToMockServiceResponseRo(MockServiceResponse mockServiceResponse);

    abstract HeaderItemRo headerItemToHeaderItemRo(HeaderItem headerItem);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "step", ignore = true),
    })
    abstract MockServiceResponse updateMockServiceResponseFromRo(MockServiceResponseRo mockServiceResponseRo, @MappingTarget MockServiceResponse r);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "step", ignore = true),
    })
    abstract SqlData updateSqlDataFromRo(SqlDataRo sqlDataRo, @MappingTarget SqlData sqlData);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "mockServiceResponse", ignore = true),
    })
    abstract HeaderItem updateHeaderFromRo(HeaderItemRo headerItemRo);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "step", ignore = true),
    })
    abstract ExpectedServiceRequest updateExpectedServiceRequestFromRo(ExpectedServiceRequestRo expectedServiceRequestRo, @MappingTarget ExpectedServiceRequest r);

    private ExpectedServiceRequest updateExpectedServiceRequest(ExpectedServiceRequestRo expectedServiceRequestRo, @MappingTarget ExpectedServiceRequest r) {
        return updateExpectedServiceRequestFromRo(expectedServiceRequestRo, r);
    }

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "step", ignore = true),
    })
    abstract FormData updateFormDataFromRo(FormDataRo formDataRo);

    abstract List<FormDataRo> convertFormDataListToRo(List<FormData> formDataList);

    abstract FormDataRo formDataToRo(FormData formData);

    public void updateScenarioStepList(List<StepRo> stepRoList, Scenario scenario) {
        List<Step> scenarioSteps = new LinkedList<>(scenario.getStepList());
        scenario.getStepList().clear();
        scenario.getStepList().addAll(stepRoList.stream()
                .map(stepRo -> scenarioSteps.stream()
                        .filter(step -> Objects.equals(step.getId(), stepRo.getId()))
                        .map(step -> updateStep(stepRo, step))
                        .findAny()
                        .orElseGet(() -> {
                            Step newStep = new Step();
                            updateStep(stepRo, newStep);
                            newStep.setScenario(scenario);
                            return newStep;
                        }))
                .collect(Collectors.toList()));
    }

    abstract ScenarioVariableFromMqRequestRo scenarioVariableFromMqRequestToRo(ScenarioVariableFromMqRequest variable);

    abstract List<ScenarioVariableFromMqRequestRo> convertMqVar(List<ScenarioVariableFromMqRequest> scenarioVariableFromMqRequestList);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "step", ignore = true),
    })
    abstract ScenarioVariableFromMqRequest updateVariableFromMqRequestFromRo(ScenarioVariableFromMqRequestRo variableRo);

    abstract List<ScenarioVariableFromMqRequest> convertMqVarListToRo(List<ScenarioVariableFromMqRequestRo> scenarioVariableFromMqRequestRoList);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "step", ignore = true),
    })
    abstract MqMessage convertMqMessageRoTo(MqMessageRo ro);

    abstract MqMessageRo convertMqMessageToRo(MqMessage ro);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "mqMock", ignore = true),
    })
    abstract MqMockResponse convertMqMockResponsePartRoTo(MqMockResponseRo ro);

    abstract MqMockResponseRo convertMqMockResponsePartToRo(MqMockResponse response);

    abstract MqMockRo convertMqMockToRo(MqMock mqMock);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "step", ignore = true),
    })
    abstract MqMock convertMqMockRoTo(MqMockRo mqMock);

    abstract ExpectedMqRequestRo convertMqMockToRo(ExpectedMqRequest mqMock);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "step", ignore = true),
    })
    abstract ExpectedMqRequest convertMqMockRoTo(ExpectedMqRequestRo mqMock);

    @Autowired
    public void setDiffCalculator(JsonDiffCalculator diffCalculator) {
        this.diffCalculator = diffCalculator;
    }
}
