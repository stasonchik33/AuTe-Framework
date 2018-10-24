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

import {Component, OnInit, ViewChild} from '@angular/core';
import {Scenario} from '../model/scenario';
import {Step} from '../model/step';
import {ScenarioService} from '../service/scenario.service';
import {Router, ActivatedRoute, ParamMap} from '@angular/router';
import 'rxjs/add/operator/switchMap';
import {CustomToastyService} from '../service/custom-toasty.service';
import {TranslateService} from '@ngx-translate/core';
import {ScenarioTitleItemComponent} from '../scenario-list-item/scenario-title-item.component';

@Component({
  selector: 'app-scenario-detail',
  templateUrl: './scenario-detail.component.html',
  styleUrls: ['./scenario-detail.component.css']
})
export class ScenarioDetailComponent implements OnInit {

  scenario: Scenario;
  stepList: Step[];

  @ViewChild(ScenarioTitleItemComponent) scenarioListItemComponent: ScenarioTitleItemComponent;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private scenarioService: ScenarioService,
    private customToastyService: CustomToastyService,
    private translate: TranslateService
  ) { }

  ngOnInit() {
    this.route.params.subscribe((params: ParamMap) => {
      this.scenarioService
        .findOne(params['scenarioId'])
        .subscribe(value => this.scenario = value, this.handleError);

      this.scenarioService
        .findScenarioSteps(params['scenarioId'])
        .subscribe(value => this.stepList = value);
    });
  }

  saveSteps() {
    if (this.scenario && this.stepList) {
      const toasty = this.customToastyService.saving();
      this.scenarioService.saveStepList(this.scenario, this.stepList)
        .subscribe(savedStepList => {
          this.customToastyService.success('Сохранено', 'Шаги сохранены');
          this.stepList = savedStepList;
        }, error => {
          const message = JSON.parse(error._body).message;
          this.translate.get(message).subscribe(value => {
            this.customToastyService.error('Ошибка', value);
          });
        }, () => this.customToastyService.clear(toasty));
    }
  }

  addStep() {
    if (!this.stepList) {
      this.stepList = [];
    }
    this.stepList.push(new Step());
  }

  onDeleteClick(step: Step) {
    if (confirm('Confirm: delete step')) {
      const indexToRemove = this.stepList.indexOf(step);
      if (indexToRemove > -1) {
        this.stepList.splice(indexToRemove, 1);
      }
    }
  }

  addStepBefore(step: Step) {
    const addAfterIndex = this.stepList.indexOf(step);
    if (addAfterIndex > -1) {
      this.stepList.splice(addAfterIndex, 0, new Step());
    }
  }

  onUpClick(step: Step) {
    const index = this.stepList.indexOf(step);
    if (index > 0) {
      const tmpStep = this.stepList[index];
      this.stepList[index] = this.stepList[index - 1];
      this.stepList[index - 1] = tmpStep;
    }
  }

  onDownClick(step: Step) {
    const index = this.stepList.indexOf(step);
    if (index > -1 && index < this.stepList.length - 1) {
      const tmpStep = this.stepList[index];
      this.stepList[index] = this.stepList[index + 1];
      this.stepList[index + 1] = tmpStep;
    }
  }

  onCloneClick(step: Step) {
    if (confirm('Confirm: Clone step')) {
      const index = this.stepList.indexOf(step);
      const clonedStep = Object.assign({}, step);
      this.stepList.splice(index + 1, 0, clonedStep);
      this.customToastyService.success('Сохранено', 'Шаг склонирован');
    }
  }

  deleteScenario() {
    if (confirm('Confirm: Delete scenario')) {
      const toasty = this.customToastyService.deletion('Удаление сценария...');
      this.scenarioService.deleteOne(this.scenario)
        .subscribe(() => {
          this.router.navigate(['/project', this.scenario.projectId], {replaceUrl: true});
          this.customToastyService.success('Удалено', 'Сценарий удалён');
        }, error => this.customToastyService.error('Ошибка', error), () => this.customToastyService.clear(toasty));
    }
  }

  handleError() {
    this.router.navigate(['/'], {replaceUrl: true});
  }

  onChangeItem(step: Step) {
    this.scenarioListItemComponent.updateExecutedResults(step);
  }
}
