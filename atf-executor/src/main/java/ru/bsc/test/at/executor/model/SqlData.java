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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "sql_data")
public class SqlData implements Serializable, AbstractModel {
    private static final long serialVersionUID = -5297373310164570345L;

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "step_id")
    private Step step;
    private String sql;
    private String sqlSavedParameter;
    private SqlResultType sqlReturnType = SqlResultType.MAP;

    public SqlData copy() {
        SqlData sqlData = new SqlData();
        sqlData.setSql(getSql());
        sqlData.setSqlSavedParameter(getSqlSavedParameter());
        sqlData.setSqlReturnType(getSqlReturnType());
        return sqlData;
    }
}
