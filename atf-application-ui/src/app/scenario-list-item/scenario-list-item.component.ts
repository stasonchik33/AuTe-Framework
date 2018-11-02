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

import {Component, EventEmitter, Input, OnInit, Output, QueryList, ViewChildren} from '@angular/core';
import {Scenario} from '../model/scenario';
import {ScenarioService} from '../service/scenario.service';
import {StepResult} from '../model/step-result';
import {StartScenarioInfo} from '../model/start-scenario-info';
import {Step} from '../model/step';
import { saveAs } from 'file-saver/FileSaver';
import {StepResultItemComponent} from '../step-result-item/step-result-item.component';

export const PROJECT_ENV_TOKEN = 'bsc_autotester_project_env';

@Component({
  selector: 'app-scenario-list-item',
  templateUrl: './scenario-list-item.component.html',
  styleUrls: ['./scenario-list-item.component.css']
})
export class ScenarioListItemComponent implements OnInit {

  @Input()
  scenario: Scenario;
  @Input()
  projectId: string;
  @Input()
  stepList: Step[];

  @Output() onStateChange = new EventEmitter<any>();
  @Output() cbStateChange = new EventEmitter<any>();

  @ViewChildren(StepResultItemComponent) childrenComponents: QueryList<StepResultItemComponent>;

  stepResultList: StepResult[];

  state = 'none';
  showResultDetails = false;
  startScenarioInfo: StartScenarioInfo;
  resultCheckTimeout = 500;
  executedSteps: number;
  totalSteps: number;

  constructor(
    private scenarioService: ScenarioService
  ) { }

  ngOnInit() {
    this.scenario._selected = false;
  }

  stateChanged() {
    this.onStateChange.emit({state: this.state, executedSteps: this.executedSteps, totalSteps: this.totalSteps});
  }

  runScenario() {
    if (this.state !== 'executing') {
      this.executedSteps = 0;
      this.stepResultList = [];
      this.state = 'starting';
      this.stateChanged();
      const projectEnvironmentId = parseInt(localStorage.getItem(PROJECT_ENV_TOKEN + this.projectId), 10);
      this.scenarioService.run(this.scenario, projectEnvironmentId)
        .subscribe(startScenarioInfo => {
          this.startScenarioInfo = startScenarioInfo;
          this.state = 'executing';
          this.stateChanged();
          this.checkState();
        });
    }
  }

  checkState() {
    if (this.startScenarioInfo) {
      this.scenarioService.executionStatus(this.startScenarioInfo.runningUuid)
        .subscribe(executionResult => {
          if (executionResult.scenarioResultList && executionResult.scenarioResultList[0]) {
            this.scenario.hasResults = true;
            const scenarioResult = executionResult.scenarioResultList[0];
            const allSteps: StepResult [] = scenarioResult.stepResultList;
            if (allSteps.length > 0) {
              if (this.stepResultList.length === 0) {
                this.stepResultList.push(allSteps[0]);
              }
              this.stepResultList[this.stepResultList.length - 1] = allSteps[this.stepResultList.length - 1];
              for (let i = this.stepResultList.length; i < allSteps.length; i++) {
                this.stepResultList.push(allSteps[i]);
              }
            }

            this.scenario.failed = allSteps != null && allSteps.filter(result => result.result === 'Fail').length > 0;

            this.executedSteps = allSteps.filter(stepResult => stepResult.editable).length;
            this.totalSteps = scenarioResult.totalSteps;

            if (executionResult.finished) {
              this.state = 'finished';
            } else {
              setTimeout(() => {
                this.checkState();
              }, this.resultCheckTimeout);
            }
            this.stateChanged();
          } else {
            setTimeout(() => {
              this.checkState();
            }, this.resultCheckTimeout);
          }
        }, error => {
          if (error.message && !error.message.contains('Unexpected end of JSON input')) {
            setTimeout(() => {
              this.checkState();
            }, this.resultCheckTimeout);
          }
        });
    }
  }

  resultDetailsToggle() {
    if (!this.stepResultList) {
      this.scenarioService.getResults(this.scenario.id).subscribe(data => {
        this.stepResultList = data;
        this.totalSteps = this.stepResultList.length;
        this.executedSteps = this.totalSteps;
        this.showResultDetails = !this.showResultDetails;
      });
    } else {
      this.showResultDetails = !this.showResultDetails;
    }
  }

  stop(): void {
    if (this.startScenarioInfo) {
      this.scenarioService
        .stop(this.startScenarioInfo.runningUuid)
        .subscribe();
    }
  }

  getMapStyleForScenario(): string {
    if (this.state !== 'executing' && this.state !== 'starting') {
      if (this.scenario.failed === true) { return 'failedScenario'; }
      if (this.scenario.failed === false) { return 'passedScenario'; }
    }
    return '';
  }

  getReport() {
    const scenarioIdentities = [];
    scenarioIdentities.push(this.scenario.id);
    this.scenarioService
      .downloadReport(scenarioIdentities)
      .subscribe(blobReport => {
        saveAs(blobReport, 'report.zip')
      });
    return false;
  }

  onClick(event: any) {
    this.cbStateChange.emit(event);
  }

  updateExecutedResults(step: Step) {
    this.childrenComponents.forEach(comp => {
      if (comp.stepItem) {
        const s = comp.step;
        if (s.id === step.id && this.stepList.indexOf(s) === -1) {
          // setTimeout need remove exception in dev mode https://blog.angular-university.io/angular-debugging/
          setTimeout(() => {
            comp.changed = true;
          });
        }
      }
    });
  }
}
