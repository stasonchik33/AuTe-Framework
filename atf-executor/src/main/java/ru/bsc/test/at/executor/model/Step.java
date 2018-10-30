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

import lombok.Data;
import ru.bsc.test.at.executor.helper.client.impl.http.HTTPMethod;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by sdoroshin on 10.05.2017.
 */
@Data
@Entity
@Table(name = "step")
public class Step implements Serializable, AbstractModel {
    private static final long serialVersionUID = 1670286319126044952L;

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    @OneToMany(mappedBy = "step", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ExpectedServiceRequest> expectedServiceRequestList = new LinkedList<>();
    private String relativeUrl;
    private HTTPMethod requestMethod;
    @Column(columnDefinition="TEXT")
    private String request;
    @Column(columnDefinition="TEXT")
    private String requestHeaders;
    @Column(columnDefinition="TEXT")
    private String expectedResponse;
    private Boolean expectedResponseIgnore;
    private Integer expectedStatusCode;
    private String jsonXPath;
    private RequestBodyType requestBodyType = RequestBodyType.JSON;
    private Boolean usePolling;
    private String pollingJsonXPath;
    @OneToMany(mappedBy = "step", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<MockServiceResponse> mockServiceResponseList = new LinkedList<>();
    private Boolean disabled;
    private String stepComment;
    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "saved_values_check")
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> savedValuesCheck = new HashMap<>();
    private ResponseCompareMode responseCompareMode = ResponseCompareMode.JSON;
    @OneToMany(mappedBy = "step", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<StepParameterSet> stepParameterSetList = new LinkedList<>();
    @OneToMany(mappedBy = "step", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<MqMessage> mqMessages = new LinkedList<>();
    private Boolean multipartFormData;
    @OneToMany(mappedBy = "step", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<FormData> formDataList = new LinkedList<>();
    private String jsonCompareMode = "NON_EXTENSIBLE";
    private String script;
    private String numberRepetitions;
    private String parseMockRequestUrl;
    private String parseMockRequestXPath;
    private String parseMockRequestScenarioVariable;
    private String timeoutMs;
    @OneToMany(mappedBy = "step", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<MqMock> mqMockResponseList = new LinkedList<>();
    @OneToMany(mappedBy = "step", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ExpectedMqRequest> expectedMqRequestList;
    @OneToMany(mappedBy = "step", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<SqlData> sqlDataList = new LinkedList<>();
    @OneToMany(mappedBy = "step", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ScenarioVariableFromMqRequest> scenarioVariableFromMqRequestList;
    private StepMode stepMode;

    // JMS step mode
    private String mqOutputQueueName;
    private String mqInputQueueName;
    private String mqTimeoutMs;

    public Step copy() {
        Step step = new Step();
        step.setRelativeUrl(getRelativeUrl());
        step.setRequestMethod(getRequestMethod());
        step.setRequestHeaders(getRequestHeaders());
        step.setRequest(getRequest());
        step.setExpectedResponse(getExpectedResponse());
        step.setExpectedStatusCode(getExpectedStatusCode());
        step.setJsonXPath(getJsonXPath());
        step.setRequestBodyType(getRequestBodyType());
        step.setExpectedResponseIgnore(getExpectedResponseIgnore());
        step.setUsePolling(getUsePolling());
        step.setPollingJsonXPath(getPollingJsonXPath());
        step.setDisabled(getDisabled());
        step.setStepComment(getStepComment());
        step.setScript(getScript());
        step.setSavedValuesCheck(new HashMap<>(getSavedValuesCheck()));
        step.setResponseCompareMode(getResponseCompareMode());
        if (getExpectedServiceRequestList() != null) {
            step.setExpectedServiceRequestList(new LinkedList<>());
            for (ExpectedServiceRequest expectedServiceRequest : getExpectedServiceRequestList()) {
                step.getExpectedServiceRequestList().add(expectedServiceRequest.copy());
            }
        }
        if (getMockServiceResponseList() != null) {
            step.setMockServiceResponseList(new LinkedList<>());
            for (MockServiceResponse mockServiceResponse : getMockServiceResponseList()) {
                step.getMockServiceResponseList().add(mockServiceResponse.copy());
            }
        }
        if (getStepParameterSetList() != null) {
            step.setStepParameterSetList(new LinkedList<>());
            for (StepParameterSet stepParameterSet : getStepParameterSetList()) {
                step.getStepParameterSetList().add(stepParameterSet.copy());
            }
        }
        if (getFormDataList() != null) {
            step.setFormDataList(new LinkedList<>());
            for (FormData formData : getFormDataList()) {
                step.getFormDataList().add(formData.copy());
            }
        }
        if (getMqMessages() != null) {
            step.setMqMessages(new LinkedList<>());
            getMqMessages().forEach(info -> step.getMqMessages().add(info.copy()));
        }
        if (getSqlDataList() != null) {
            step.setSqlDataList(new LinkedList<>());
            for (SqlData sqlData : getSqlDataList()) {
                step.getSqlDataList().add(sqlData.copy());
            }
        }

        step.setMultipartFormData(getMultipartFormData());
        step.setJsonCompareMode(getJsonCompareMode());
        step.setNumberRepetitions(getNumberRepetitions());
        step.setParseMockRequestUrl(getParseMockRequestUrl());
        step.setParseMockRequestXPath(getParseMockRequestXPath());
        step.setParseMockRequestScenarioVariable(getParseMockRequestScenarioVariable());
        step.setTimeoutMs(getTimeoutMs());
        step.setStepMode(getStepMode());

        if (this.getMqMockResponseList() == null) {
            step.setMqMockResponseList(new LinkedList<>());
        }
        this.getMqMockResponseList().clear();
        if (this.getMqMockResponseList() != null) {
            for (MqMock mqMock : this.getMqMockResponseList()) {
                this.getMqMockResponseList().add(mqMock.copy());
            }
        }

        if (getExpectedMqRequestList() != null) {
            step.setExpectedMqRequestList(new LinkedList<>());
            for (ExpectedMqRequest expectedMqRequest : getExpectedMqRequestList()) {
                step.getExpectedMqRequestList().add(expectedMqRequest.copy());
            }
        }

        if (getSqlDataList() != null) {
            step.setSqlDataList(new LinkedList<>());
            for (SqlData sqlData : getSqlDataList()) {
                step.getSqlDataList().add(sqlData.copy());
            }
        }

        if (getScenarioVariableFromMqRequestList() != null) {
            step.setScenarioVariableFromMqRequestList(new LinkedList<>());
            for (ScenarioVariableFromMqRequest variable : getScenarioVariableFromMqRequestList()) {
                step.getScenarioVariableFromMqRequestList().add(variable.copy());
            }
        }

        return step;
    }

    public Boolean getExpectedResponseIgnore() {
        return expectedResponseIgnore != null && expectedResponseIgnore;
    }

    public Boolean getUsePolling() {
        return usePolling != null && usePolling;
    }

    public Boolean getDisabled() {
        return disabled != null && disabled;
    }

    public Boolean getMultipartFormData() {
        return multipartFormData != null && multipartFormData;
    }
}
