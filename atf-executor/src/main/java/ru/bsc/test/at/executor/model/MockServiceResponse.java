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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sdoroshin on 27.07.2017.
 */
@Getter
@Setter
@Entity
@Table(name = "mock_service_response")
public class MockServiceResponse implements AbstractModel {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "step_id")
    private Step step;
    private String serviceUrl;
    @Column(columnDefinition="TEXT")
    private String responseBody;
    private Integer httpStatus;
    private String contentType;
    private String userName;
    private String password;
    private String pathFilter;
    @OneToMany(mappedBy = "mockServiceResponse", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<HeaderItem> headers;

    protected MockServiceResponse copy() {
        MockServiceResponse response = new MockServiceResponse();
        response.setServiceUrl(getServiceUrl());
        response.setResponseBody(getResponseBody());
        // TODO: cloned.setResponseBodyFile(getResponseBodyFile());
        response.setHttpStatus(getHttpStatus());
        response.setContentType(getContentType());
        response.setUserName(getUserName());
        response.setPassword(getPassword());
        response.setPathFilter(getPathFilter());
        if(headers != null) {
            response.setHeaders(headers.stream().map(HeaderItem::copy).collect(Collectors.toList()));
        }
        return response;
    }
}
