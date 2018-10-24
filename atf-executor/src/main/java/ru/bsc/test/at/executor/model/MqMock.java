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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sdoroshin on 27.07.2017.
 */
@Getter
@Setter
@Entity
@Table(name = "mq_mock")
public class MqMock implements AbstractModel {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "step_id")
    private Step step;
    private String sourceQueueName;
    @OneToMany(mappedBy = "mqMock")
    private List<MqMockResponse> responses = new LinkedList<>();
    private String httpUrl;
    private String xpath;

    protected MqMock copy() {
        MqMock response = new MqMock();
        response.setSourceQueueName(getSourceQueueName());
        response.setHttpUrl(getHttpUrl());
        getResponses().forEach(r -> response.getResponses().add(r.copy()));
        response.setXpath(getXpath());
        return response;
    }
}
