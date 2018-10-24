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
import java.io.Serializable;

/**
 * Created by sdoroshin on 10.05.2017.
 */
@Getter
@Setter
@Entity
@Table(name = "expected_service_request")
public class ExpectedServiceRequest implements Serializable, AbstractModel {
    private static final long serialVersionUID = 2437656620029851514L;

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "step_id")
    private Step step;
    private String serviceName;
    private String expectedServiceRequest;
    private String ignoredTags;
    private String count;

    protected ExpectedServiceRequest copy() {
        ExpectedServiceRequest request = new ExpectedServiceRequest();
        request.setServiceName(getServiceName());
        request.setExpectedServiceRequest(getExpectedServiceRequest());
        request.setIgnoredTags(getIgnoredTags());
        request.setCount(getCount());
        return request;
    }
}
