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
import ru.bsc.test.at.executor.model.*;
import ru.bsc.test.autotester.component.JsonDiffCalculator;
import ru.bsc.test.autotester.ro.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sdoroshin on 14.09.2017.
 */

@SuppressWarnings("unused")
@Mapper(config = Config.class)
public abstract class StepRoMapper {

    @Autowired
    private JsonDiffCalculator diffCalculator;

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "scenario", ignore = true),
            @Mapping(target = "expectedServiceRequestList", source = "expectedServiceRequestList"),
    })
    public abstract Step updateStep(StepRo stepRo, @MappingTarget Step step);

    public Step convertStepRoToStep(StepRo stepRo) {
        return updateStep(stepRo, new Step());
    }

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "step", ignore = true),
    })
    abstract SqlData to(SqlDataRo ro);

    abstract SqlDataRo to(SqlData ro);

    abstract List<ExpectedServiceRequest> requestListRoToRequest(List<ExpectedServiceRequestRo> expectedServiceRequestRoList);

    abstract List<MockServiceResponse> responseListRoToResponse(List<MockServiceResponseRo> mockServiceResponseRoList);

    abstract List<FormData> formDataRoListToFormData(List<FormDataRo> formDataRoList);

    public abstract StepRo stepToStepRo(Step step);

    public abstract List<StepRo> convertStepListToStepRoList(List<Step> stepList);

    abstract ExpectedServiceRequestRo expectedServiceRequestToRo(ExpectedServiceRequest expectedServiceRequest);

    abstract List<ExpectedServiceRequestRo> convertExpectedServiceRequestListToRoList(List<ExpectedServiceRequest> expectedServiceRequests);

    abstract List<StepParameterSetRo> convertStepParameterSetListToStepParameterSetRoList(List<StepParameterSet> stepParameterSetList);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "step", ignore = true),
    })
    abstract StepParameterSet updateStepParameterSetFromRo(StepParameterSetRo stepParameterSetRo);

    abstract List<StepParameter> stepParameterRoListToStepParameter(List<StepParameterRo> stepParameterRoList);

    abstract StepParameterSetRo stepParameterSetToStepParameterSetRo(StepParameterSet stepParameterSet);

    private StepParameterSet updateStepParameterSet(StepParameterSetRo stepParameterSetRo) {
        return updateStepParameterSetFromRo(stepParameterSetRo);
    }

    abstract List<StepParameterRo> convertStepParameterToStepParameterRo(List<StepParameter> stepParameterList);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "stepParameterSet", ignore = true),
    })
    abstract StepParameter updateStepParameterFromRo(StepParameterRo stepParameterRo);

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
    abstract MockServiceResponse updateMockServiceResponseFromRo(MockServiceResponseRo mockServiceResponseRo);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "mockServiceResponse", ignore = true),
    })
    abstract HeaderItem updateHeaderFromRo(HeaderItemRo headerItemRo);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "step", ignore = true),
    })
    abstract ExpectedServiceRequest updateExpectedServiceRequestFromRo(ExpectedServiceRequestRo expectedServiceRequestRo);

    private ExpectedServiceRequest updateExpectedServiceRequest(ExpectedServiceRequestRo expectedServiceRequestRo) {
        return updateExpectedServiceRequestFromRo(expectedServiceRequestRo);
    }

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "step", ignore = true),
    })
    abstract FormData updateFormDataFromRo(FormDataRo formDataRo);

    abstract List<FormDataRo> convertFormDataListToRo(List<FormData> formDataList);

    abstract FormDataRo formDataToRo(FormData formData);

    public void updateScenarioStepList(List<StepRo> stepRoList, Scenario scenario) {
        scenario.setStepList(stepRoList.stream().map(this::convertStepRoToStep).collect(Collectors.toList()));
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
}
