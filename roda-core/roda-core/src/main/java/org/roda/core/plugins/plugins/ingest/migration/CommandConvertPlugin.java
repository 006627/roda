/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.core.plugins.plugins.ingest.migration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.roda.core.data.exceptions.InvalidParameterException;
import org.roda.core.data.v2.jobs.PluginParameter;
import org.roda.core.data.v2.jobs.PluginParameter.PluginParameterType;

public abstract class CommandConvertPlugin<T extends Serializable> extends AbstractConvertPlugin<T> {

  private String commandArguments;

  protected CommandConvertPlugin() {
    super();
    commandArguments = "";
  }

  public String getCommandArguments() {
    return commandArguments;
  }

  public void setCommandArguments(String args) {
    commandArguments = args;
  }

  @Override
  public List<PluginParameter> getParameters() {
    List<PluginParameter> params = new ArrayList<PluginParameter>();

    PluginParameter commandArgs = new PluginParameter("commandArgs", "Command arguments", PluginParameterType.STRING,
      "", true, true, "Command arguments to modify the command to execute");

    params.add(commandArgs);
    params.addAll(super.getParameters());
    return params;
  }

  @Override
  public void setParameterValues(Map<String, String> parameters) throws InvalidParameterException {
    super.setParameterValues(parameters);

    // add command arguments
    if (parameters.containsKey("commandArguments")) {
      setCommandArguments(parameters.get("commandArguments").trim());
    }
  }

}
