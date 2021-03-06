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
 * Created by sdoroshin on 08.09.2017.
 */
@Getter
@Setter
@Entity
@Table(name = "step_parameter_set")
public class StepParameterSet implements AbstractModel {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "step_id")
    private Step step;

    @OneToMany(mappedBy = "stepParameterSet", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<StepParameter> stepParameterList = new LinkedList<>();
    private String description;

    public StepParameterSet copy() {
        StepParameterSet cloned = new StepParameterSet();
        cloned.setDescription(getDescription());

        cloned.setStepParameterList(new LinkedList<>());
        for (StepParameter stepParameter : getStepParameterList()) {
            StepParameter copyStepParameter = stepParameter.copy();
            cloned.getStepParameterList().add(copyStepParameter);
        }

        return cloned;
    }
}
