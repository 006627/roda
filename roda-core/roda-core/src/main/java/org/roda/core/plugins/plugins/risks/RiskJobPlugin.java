/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.core.plugins.plugins.risks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roda.core.data.common.RodaConstants;
import org.roda.core.data.common.RodaConstants.PreservationEventType;
import org.roda.core.data.exceptions.AlreadyExistsException;
import org.roda.core.data.exceptions.AuthorizationDeniedException;
import org.roda.core.data.exceptions.GenericException;
import org.roda.core.data.exceptions.InvalidParameterException;
import org.roda.core.data.exceptions.NotFoundException;
import org.roda.core.data.exceptions.RequestNotValidException;
import org.roda.core.data.v2.ip.AIP;
import org.roda.core.data.v2.ip.File;
import org.roda.core.data.v2.ip.Representation;
import org.roda.core.data.v2.jobs.PluginParameter;
import org.roda.core.data.v2.jobs.PluginParameter.PluginParameterType;
import org.roda.core.data.v2.jobs.PluginType;
import org.roda.core.data.v2.jobs.Report;
import org.roda.core.data.v2.jobs.Report.PluginState;
import org.roda.core.data.v2.notifications.Notification;
import org.roda.core.data.v2.validation.ValidationException;
import org.roda.core.index.IndexService;
import org.roda.core.model.ModelService;
import org.roda.core.plugins.AbstractPlugin;
import org.roda.core.plugins.Plugin;
import org.roda.core.plugins.PluginException;
import org.roda.core.plugins.orchestrate.JobException;
import org.roda.core.plugins.orchestrate.SimpleJobPluginInfo;
import org.roda.core.plugins.plugins.PluginHelper;
import org.roda.core.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * https://docs.google.com/spreadsheets/d/
 * 1Ncu0My6tf19umSClIA6iXeYlJ4_FP6MygRwFCe0EzyM
 * 
 * FIXME 20160323 hsilva: after each task (i.e. plugin), the AIP should be
 * obtained again from model (as it might have changed)
 * 
 * @author Hélder Silva <hsilva@keep.pt>
 */
public class RiskJobPlugin extends AbstractPlugin<Serializable> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RiskJobPlugin.class);

  private static String riskIds = null;
  private static String OTHER_METADATA_TYPE = "RiskIncidence";

  private static Map<String, PluginParameter> pluginParameters = new HashMap<>();
  static {
    pluginParameters.put(RodaConstants.PLUGIN_PARAMS_RISK_ID,
      new PluginParameter(RodaConstants.PLUGIN_PARAMS_RISK_ID, "Risks", PluginParameterType.RISK_ID, "", false, false,
        "Add the risks that will be associated with the objects above."));

    pluginParameters.put(RodaConstants.PLUGIN_PARAMS_EMAIL_NOTIFICATION,
      new PluginParameter(RodaConstants.PLUGIN_PARAMS_EMAIL_NOTIFICATION, "Job finished notification",
        PluginParameterType.STRING, "", false, false,
        "Send a notification, after finishing the process, to one or more e-mail addresses (comma separated)"));
  }

  @Override
  public void setParameterValues(Map<String, String> parameters) throws InvalidParameterException {
    super.setParameterValues(parameters);
  }

  @Override
  public List<PluginParameter> getParameters() {
    ArrayList<PluginParameter> parameters = new ArrayList<PluginParameter>();
    parameters.add(pluginParameters.get(RodaConstants.PLUGIN_PARAMS_RISK_ID));
    parameters.add(pluginParameters.get(RodaConstants.PLUGIN_PARAMS_EMAIL_NOTIFICATION));
    return parameters;
  }

  @Override
  public void init() throws PluginException {
    // do nothing
  }

  @Override
  public void shutdown() {
    // do nothing
  }

  @Override
  public Report beforeAllExecute(IndexService index, ModelService model, StorageService storage)
    throws PluginException {
    // do nothing
    return null;
  }

  @Override
  public Report beforeBlockExecute(IndexService index, ModelService model, StorageService storage)
    throws PluginException {
    // do nothing
    return null;
  }

  @Override
  public Report execute(IndexService index, ModelService model, StorageService storage, List<Serializable> resources)
    throws PluginException {
    Report report = executePlugin(index, model, storage, resources);
    return report;
  }

  @Override
  public Report afterBlockExecute(IndexService index, ModelService model, StorageService storage)
    throws PluginException {
    // do nothing
    return null;
  }

  @Override
  public Report afterAllExecute(IndexService index, ModelService model, StorageService storage) throws PluginException {

    try {
      String emails = PluginHelper.getStringFromParameters(this,
        pluginParameters.get(RodaConstants.PLUGIN_PARAMS_EMAIL_NOTIFICATION));
      if (!"".equals(emails)) {
        List<String> emailList = new ArrayList<String>(Arrays.asList(emails.split("\\s*,\\s*")));
        Notification notification = new Notification();
        notification.setSubject("RODA risk process finished");
        notification.setFromUser(this.getClass().getSimpleName());
        notification.setRecipientUsers(emailList);
        Map<String, Object> scopes = new HashMap<String, Object>();
        model.createNotification(notification, RodaConstants.RISK_EMAIL_TEMPLATE, scopes);
      }

    } catch (GenericException e) {
      LOGGER.error("Could not send risk job notification");
    }

    return new Report();
  }

  public <T extends Serializable> Report executePlugin(IndexService index, ModelService model, StorageService storage,
    List<T> list) throws PluginException {

    Map<String, String> mergedParams = new HashMap<String, String>(getParameterValues());
    if (mergedParams.containsKey(RodaConstants.PLUGIN_PARAMS_RISK_ID)) {
      riskIds = mergedParams.get(RodaConstants.PLUGIN_PARAMS_RISK_ID);
    }

    try {

      LOGGER.debug("Creating risk incidences");
      Report pluginReport = PluginHelper.initPluginReport(this);

      SimpleJobPluginInfo jobPluginInfo = new SimpleJobPluginInfo();
      PluginHelper.updateJobInformation(this, jobPluginInfo);

      if (!list.isEmpty() && riskIds != null) {
        String[] risks = riskIds.split(",");

        for (String riskId : risks) {
          if (list.get(0) instanceof AIP) {
            jobPluginInfo = addIncidenceToAIPList(model, index, list, riskId, jobPluginInfo);
          } else if (list.get(0) instanceof Representation) {
            jobPluginInfo = addIncidenceToRepresentationList(model, index, list, riskId, jobPluginInfo);
          } else if (list.get(0) instanceof File) {
            jobPluginInfo = addIncidenceToFileList(model, index, list, riskId, jobPluginInfo);
          }
        }
      }

      jobPluginInfo.setPluginExecutionIsDone(true);
      PluginHelper.updateJobInformation(this, jobPluginInfo);
      LOGGER.debug("Done creating risk incidences");
      return pluginReport;
    } catch (JobException e) {
      throw new PluginException("A job exception has occurred", e);
    }
  }

  private <T extends Serializable> SimpleJobPluginInfo addIncidenceToAIPList(ModelService model, IndexService index,
    List<T> list, String riskId, SimpleJobPluginInfo jobPluginInfo) throws JobException {

    PluginState state = PluginState.SUCCESS;
    List<AIP> aipList = (List<AIP>) list;
    for (AIP aip : aipList) {
      try {
        model.addRiskIncidence(riskId, aip.getId(), null, null, null, OTHER_METADATA_TYPE);
        jobPluginInfo.incrementObjectsProcessedWithSuccess();
      } catch (GenericException e) {
        jobPluginInfo.incrementObjectsProcessedWithFailure();
        state = PluginState.FAILURE;
      }

      PluginHelper.updateJobInformation(this, jobPluginInfo);

      try {
        PluginHelper.createPluginEvent(this, aip.getId(), model, index, state, "", true);
      } catch (RequestNotValidException | NotFoundException | GenericException | AuthorizationDeniedException
        | ValidationException | AlreadyExistsException e) {
        LOGGER.error("Could not create a Risk Job Plugin event");
      }
    }

    return jobPluginInfo;
  }

  private <T extends Serializable> SimpleJobPluginInfo addIncidenceToRepresentationList(ModelService model,
    IndexService index, List<T> list, String riskId, SimpleJobPluginInfo jobPluginInfo) throws JobException {

    PluginState state = PluginState.SUCCESS;
    List<Representation> representationList = (List<Representation>) list;
    for (Representation representation : representationList) {
      try {
        model.addRiskIncidence(riskId, representation.getAipId(), representation.getId(), null, null,
          OTHER_METADATA_TYPE);
        jobPluginInfo.incrementObjectsProcessedWithSuccess();
      } catch (GenericException e) {
        jobPluginInfo.incrementObjectsProcessedWithFailure();
        state = PluginState.FAILURE;
      }

      PluginHelper.updateJobInformation(this, jobPluginInfo);

      try {
        PluginHelper.createPluginEvent(this, representation.getAipId(), representation.getId(), model, index,
          new ArrayList<>(), new ArrayList<>(), state, "", true);
      } catch (RequestNotValidException | NotFoundException | GenericException | AuthorizationDeniedException
        | ValidationException | AlreadyExistsException e) {
        LOGGER.error("Could not create a Risk Job Plugin event");
      }
    }

    return jobPluginInfo;
  }

  private <T extends Serializable> SimpleJobPluginInfo addIncidenceToFileList(ModelService model, IndexService index,
    List<T> list, String riskId, SimpleJobPluginInfo jobPluginInfo) throws JobException {

    PluginState state = PluginState.SUCCESS;
    List<File> fileList = (List<File>) list;
    for (File file : fileList) {
      try {
        model.addRiskIncidence(riskId, file.getAipId(), file.getRepresentationId(), file.getPath(), file.getId(),
          OTHER_METADATA_TYPE);
        jobPluginInfo.incrementObjectsProcessedWithSuccess();
      } catch (GenericException e) {
        jobPluginInfo.incrementObjectsProcessedWithFailure();
        state = PluginState.FAILURE;
      }

      PluginHelper.updateJobInformation(this, jobPluginInfo);

      try {
        PluginHelper.createPluginEvent(this, file.getAipId(), file.getRepresentationId(), file.getPath(), file.getId(),
          model, index, new ArrayList<>(), new ArrayList<>(), state, "", true);
      } catch (RequestNotValidException | NotFoundException | GenericException | AuthorizationDeniedException
        | ValidationException | AlreadyExistsException e) {
        LOGGER.error("Could not create a Risk Job Plugin event");
      }

    }

    return jobPluginInfo;
  }

  @Override
  public PluginType getType() {
    return PluginType.RISK;
  }

  @Override
  public boolean areParameterValuesValid() {
    return true;
  }

  @Override
  public String getName() {
    return "Risk Job Plugin";
  }

  @Override
  public String getDescription() {
    return "Job responsible to associate risk with objects";
  }

  @Override
  public Plugin<Serializable> cloneMe() {
    return new RiskJobPlugin();
  }

  @Override
  public String getVersionImpl() {
    return "1.0";
  }

  @Override
  public PreservationEventType getPreservationEventType() {
    // TODO change to a more adequate event type?
    return PreservationEventType.VALIDATION;
  }

  @Override
  public String getPreservationEventDescription() {
    return "Job responsible to associate risk with objects";
  }

  @Override
  public String getPreservationEventSuccessMessage() {
    return "Risk was successfully associated with objects";
  }

  @Override
  public String getPreservationEventFailureMessage() {
    return "Risk was not successfully associated with objects";
  }
}
