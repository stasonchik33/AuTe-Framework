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
import javax.persistence.Table;

/**
 * Created by smakarov
 * 21.05.2018 15:34
 */
@Getter
@Setter
@Entity
@Table(name = "mq_mock_response")
public class MqMockResponse {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "mq_mock_id")
    private MqMock mqMock;
    private String responseBody;
    private String destinationQueueName;

    public MqMockResponse copy() {
        MqMockResponse copy = new MqMockResponse();
        copy.setDestinationQueueName(getDestinationQueueName());
        copy.setResponseBody(getResponseBody());
        return copy;
    }
}
