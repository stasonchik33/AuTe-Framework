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

import { Injectable } from '@angular/core';
import {Scenario} from '../model/scenario';
import {Headers, Http, ResponseContentType} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import {Step} from '../model/step';
import {Globals} from '../globals';
import {StartScenarioInfo} from '../model/start-scenario-info';
import {ExecutionResult} from '../model/execution-result';
import {StepResult} from '../model/step-result';

@Injectable()
export class ScenarioService {

  public serviceUrl = '/rest/scenario';
  private headers = new Headers({'Content-Type': 'application/json'});

  constructor(
    private globals: Globals,
    private http: Http
  ) { }

  run(scenario: Scenario): Observable<StartScenarioInfo> {
    return this.http.post(
      this.globals.serviceBaseUrl + this.serviceUrl + '/' + scenario.id + '/start',
      {},
      {headers: this.headers}
    ).map(value => value.json() as StartScenarioInfo);
  }

  executionStatus(runningUuid: string): Observable<ExecutionResult> {
    return this.http.get(
      this.globals.serviceBaseUrl + '/rest/execution/' + runningUuid + '/status'
    ).map(value => value.json() as ExecutionResult);
  }

  stop(runningUuid: string): Observable<any> {
    return this.http.post(
      this.globals.serviceBaseUrl + '/rest/execution/' + runningUuid + '/stop',
      {},
      {headers: this.headers}
    );
  }

  findOne(scenarioId: number): Observable<Scenario> {
    return this.http
      .get(this.globals.serviceBaseUrl + this.serviceUrl + '/' + scenarioId)
      .map(value => value.json() as Scenario);
  }

  findScenarioSteps(scenarioId: number): Observable<Step[]> {
    return this.http
      .get(this.globals.serviceBaseUrl + this.serviceUrl + '/' + scenarioId + '/steps')
      .map(value => value.json() as Step[]);
  }

  saveStepList(scenario: Scenario, stepList: Step[]): Observable<Step[]> {
    return this.http.put(
      this.globals.serviceBaseUrl + this.serviceUrl + '/' + scenario.id + '/steps',
      stepList,
      {headers: this.headers}
    ).map(value => value.json() as Step[]);
  }

  saveOne(scenario: Scenario): Observable<Scenario> {
    return this.http.put(
      this.globals.serviceBaseUrl + this.serviceUrl + '/' + scenario.id,
      scenario,
      {headers: this.headers}
    ).map(value => value.json() as Scenario);
  }

  deleteOne(scenario: Scenario): Observable<any> {
    return this.http.delete(this.globals.serviceBaseUrl + this.serviceUrl + '/' + scenario.id);
  }

  getResults(identity: number): Observable<StepResult[]> {
    return this.http.post(this.globals.serviceBaseUrl + '/rest/execution/results', identity)
      .map(data => {
        try {
          return data.json() as StepResult[];
        } catch (e) {
          console.log(e);
          return [];
        }
      });
  }

  downloadReport(identities: number[]): Observable<Blob> {
    return this.http.post(
      this.globals.serviceBaseUrl + '/rest/execution/report',
      identities,
      {responseType: ResponseContentType.Blob }
    ).map(data => data.blob());
  }
}
