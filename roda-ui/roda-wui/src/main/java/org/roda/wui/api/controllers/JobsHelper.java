/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.wui.api.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.roda.core.RodaCoreFactory;
import org.roda.core.data.adapter.sublist.Sublist;
import org.roda.core.data.common.NotFoundException;
import org.roda.core.data.common.Pair;
import org.roda.core.data.common.RodaConstants.JOB_TYPE;
import org.roda.core.data.common.RodaConstants.RESOURCE_TYPE;
import org.roda.core.data.v2.IndexResult;
import org.roda.core.data.v2.Job;
import org.roda.core.data.v2.RodaUser;
import org.roda.core.data.v2.TransferredResource;
import org.roda.core.index.IndexServiceException;
import org.roda.wui.api.exceptions.ApiException;
import org.roda.wui.api.exceptions.RequestNotValidException;
import org.roda.wui.api.v1.utils.ApiUtils;
import org.roda.wui.common.client.GenericException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.pattern.Patterns;

public class JobsHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(JobsHelper.class);

  private static final List<String> ORCHESTRATOR_METHODS = Arrays.asList("runPluginOnTransferredResources");
  private static final List<RESOURCE_TYPE> RESOURCE_TYPES = Arrays.asList(RESOURCE_TYPE.BAGIT);
  private static final List<JOB_TYPE> JOB_TYPES = Arrays.asList(JOB_TYPE.INGEST);

  protected static void validateCreateJob(Job job) throws RequestNotValidException {
    if (!ORCHESTRATOR_METHODS.contains(job.getOrchestratorMethod())) {
      throw new RequestNotValidException(ApiException.INVALID_PARAMETER_VALUE,
        "Invalid orchestrator method '" + job.getOrchestratorMethod() + "'");
    }
    if (!RESOURCE_TYPES.contains(job.getResourceType())) {
      throw new RequestNotValidException(ApiException.INVALID_PARAMETER_VALUE,
        "Invalid resource type '" + job.getResourceType() + "'");
    }
    if (!JOB_TYPES.contains(job.getType())) {
      throw new RequestNotValidException(ApiException.INVALID_PARAMETER_VALUE,
        "Invalid plugin type '" + job.getType() + "'");
    }

  }

  protected static Job createJob(RodaUser user, Job job) throws NotFoundException {
    Job updatedJob = new Job(job);
    updatedJob.setUsername(user.getName());

    // serialize job to file & index it
    RodaCoreFactory.getModelService().createJob(updatedJob, null);

    Patterns.ask(RodaCoreFactory.getPluginOrchestrator().getCoordinator(), updatedJob, 5);

    return updatedJob;
  }

  public static Job getJob(String jobId) throws NotFoundException, GenericException {
    try {
      return RodaCoreFactory.getIndexService().retrieve(Job.class, jobId);
    } catch (IndexServiceException e) {
      if (e.getCode() == IndexServiceException.NOT_FOUND) {
        throw new NotFoundException("Job with id '" + jobId + "' was not found.");
      } else {
        throw new GenericException("Error getting Job with id '" + jobId + "'.");
      }
    }
  }

  // FIXME see if this method is being used
  private static List<TransferredResource> getTransferredResourcesFromObjectIds(RodaUser user, List<String> objectIds)
    throws NotFoundException {
    List<TransferredResource> res = new ArrayList<TransferredResource>();
    for (String objectId : objectIds) {
      try {
        res.add(BrowserHelper.retrieveTransferredResource(user.getId() + "/" + objectId));
      } catch (GenericException e) {
        LOGGER.error("Error retrieving transferred resource {}", objectId, e);
      }
    }
    return res;
  }

  // FIXME see if this method is being used
  public static List<TransferredResource> getTransferredResourcesFromObjectIds(String username, List<String> objectIds)
    throws NotFoundException {
    List<TransferredResource> res = new ArrayList<TransferredResource>();
    for (String objectId : objectIds) {
      try {
        res.add(BrowserHelper.retrieveTransferredResource(username + "/" + objectId));
      } catch (GenericException e) {
        LOGGER.error("Error retrieving transferred resource {}", objectId, e);
      }
    }
    return res;
  }

  // FIXME do better error handling
  public static org.roda.core.data.v2.Jobs listJobs(String start, String limit) {
    org.roda.core.data.v2.Jobs jobs = null;
    Pair<Integer, Integer> pagingParams = ApiUtils.processPagingParams(start, limit);

    try {
      IndexResult<Job> find = RodaCoreFactory.getIndexService().find(Job.class, null, null,
        new Sublist(new Sublist(pagingParams.getFirst(), pagingParams.getSecond())));
      jobs = getJobsFromIndexResult(find);
    } catch (IndexServiceException e) {
      LOGGER.error("Error retrieving Jobs list", e);
    }

    return jobs;
  }

  private static org.roda.core.data.v2.Jobs getJobsFromIndexResult(IndexResult<Job> jobsFromIndexResult) {
    org.roda.core.data.v2.Jobs jobs = new org.roda.core.data.v2.Jobs();

    for (Job job : jobsFromIndexResult.getResults()) {
      jobs.addJob(job);
    }

    return jobs;
  }

}
