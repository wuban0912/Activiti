/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.rest.service.api.management;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ManagementService;
import org.activiti.engine.runtime.DeadLetterJobQuery;
import org.activiti.rest.common.api.DataResponse;
import org.activiti.rest.common.api.RequestUtil;
import org.activiti.rest.service.api.RestResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Joram Barrez
 */
@RestController
public class DeadLetterJobCollectionResource {

  @Autowired
  protected RestResponseFactory restResponseFactory;

  @Autowired
  protected ManagementService managementService;

  @RequestMapping(value = "/management/deadletter-jobs", method = RequestMethod.GET, produces = "application/json")
  public DataResponse getJobs(@RequestParam Map<String, String> allRequestParams, HttpServletRequest request) {
    DeadLetterJobQuery query = managementService.createDeadLetterJobQuery();

    if (allRequestParams.containsKey("id")) {
      query.jobId(allRequestParams.get("id"));
    }
    if (allRequestParams.containsKey("processInstanceId")) {
      query.processInstanceId(allRequestParams.get("processInstanceId"));
    }
    if (allRequestParams.containsKey("executionId")) {
      query.executionId(allRequestParams.get("executionId"));
    }
    if (allRequestParams.containsKey("processDefinitionId")) {
      query.processDefinitionId(allRequestParams.get("processDefinitionId"));
    }
    if (allRequestParams.containsKey("executable")) {
      query.executable();
    }
    if (allRequestParams.containsKey("timersOnly")) {
      if (allRequestParams.containsKey("messagesOnly")) {
        throw new ActivitiIllegalArgumentException("Only one of 'timersOnly' or 'messagesOnly' can be provided.");
      }
      if (Boolean.valueOf(allRequestParams.get("timersOnly"))) {
        query.timers();
      }
    }
    if (allRequestParams.containsKey("messagesOnly")) {
      if (Boolean.valueOf(allRequestParams.get("messagesOnly"))) {
        query.messages();
      }
    }
    if (allRequestParams.containsKey("dueBefore")) {
      query.duedateLowerThan(RequestUtil.getDate(allRequestParams, "dueBefore"));
    }
    if (allRequestParams.containsKey("dueAfter")) {
      query.duedateHigherThan(RequestUtil.getDate(allRequestParams, "dueAfter"));
    }
    if (allRequestParams.containsKey("withException")) {
      if (Boolean.valueOf(allRequestParams.get("withException"))) {
        query.withException();
      }
    }
    if (allRequestParams.containsKey("exceptionMessage")) {
      query.exceptionMessage(allRequestParams.get("exceptionMessage"));
    }
    if (allRequestParams.containsKey("tenantId")) {
      query.jobTenantId(allRequestParams.get("tenantId"));
    }
    if (allRequestParams.containsKey("tenantIdLike")) {
      query.jobTenantIdLike(allRequestParams.get("tenantIdLike"));
    }
    if (allRequestParams.containsKey("withoutTenantId")) {
      if (Boolean.valueOf(allRequestParams.get("withoutTenantId"))) {
        query.jobWithoutTenantId();
      }
    }

    return new JobPaginateList(restResponseFactory).paginateList(allRequestParams, query, "id", JobQueryProperties.PROPERTIES);
  }
}
