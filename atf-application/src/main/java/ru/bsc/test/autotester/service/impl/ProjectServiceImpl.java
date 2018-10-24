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

package ru.bsc.test.autotester.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bsc.test.at.executor.model.Project;
import ru.bsc.test.autotester.mapper.ProjectRoMapper;
import ru.bsc.test.autotester.repository.ProjectRepository;
import ru.bsc.test.autotester.ro.ProjectRo;
import ru.bsc.test.autotester.service.ProjectService;

import java.util.List;

/**
 * Created by sdoroshin on 21.03.2017.
 *
 */
@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectRoMapper projectRoMapper;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectRoMapper projectRoMapper) {
        this.projectRepository = projectRepository;
        this.projectRoMapper = projectRoMapper;
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Project findOne(Long projectId) {
        return projectRepository.findOne(projectId);
    }

    @Override
    public ProjectRo updateFromRo(Long projectId, ProjectRo projectRo) {
        Project project = findOne(projectId);
        if (project != null) {
            project = projectRepository.save(projectRoMapper.updateProjectFromRo(projectRo));
            return projectRoMapper.projectToProjectRo(project);
        }
        return null;
    }

    @Override
    public ProjectRo createFromRo(ProjectRo projectRo) {
        Project project = projectRoMapper.updateProjectFromRo(projectRo);
        project = projectRepository.save(project);
        return projectRoMapper.projectToProjectRo(project);
    }
}
