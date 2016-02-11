package org.roda.core.plugins.plugins.ingest.migration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ghost4j.Ghostscript;
import org.ghost4j.GhostscriptException;
import org.roda.core.RodaCoreFactory;
import org.roda.core.util.CommandException;
import org.roda.core.util.CommandUtility;

public class GhostScriptConvertPluginUtils {

  public static Path runGhostScriptConvert(Path input, String inputFormat, String outputFormat, String commandArguments)
    throws IOException, CommandException, GhostscriptException {

    Path output = Files.createTempFile("result", "." + outputFormat);
    return executeGS(input, output, commandArguments);
  }

  private static Path executeGS(Path input, Path output, String commandArguments) throws GhostscriptException,
    IOException, UnsupportedOperationException {

    String command = RodaCoreFactory.getRodaConfigurationAsString("tools", "ghostscriptconvert", "commandLine");
    command = command.replace("{input_file}", input.toString());
    command = command.replace("{output_file}", output.toString());

    if (commandArguments.length() > 0) {
      command = command.replace("{arguments}", commandArguments);
    } else {
      command = command.replace("{arguments}", "-sDEVICE=pdfwrite");
    }

    // GhostScript transformation command
    String[] gsArgs = command.split("\\s+");
    Ghostscript gs = Ghostscript.getInstance();

    try {
      gs.initialize(gsArgs);
      gs.exit();
    } catch (GhostscriptException e) {
      throw new GhostscriptException("Exception when using GhostScript: ", e);
    }

    return output;
  }

  public static String getVersion() throws CommandException, IOException, UnsupportedOperationException {
    String version = CommandUtility.execute("gs", "--version");
    if (version.indexOf('\n') > 0) {
      version = version.substring(0, version.indexOf('\n'));
      version = version.replace(" ", "_");
    }
    return "GhostScript_" + version.trim();
  }

  /*************************** FILLING FILE FORMAT STRUCTURES ***************************/

  public static Map<String, List<String>> getPronomToExtension() {
    Map<String, List<String>> map = new HashMap<>();
    String inputFormatPronoms = RodaCoreFactory.getRodaConfigurationAsString("tools", "ghostscriptconvert",
      "inputFormatPronoms");

    for (String pronom : Arrays.asList(inputFormatPronoms.split(" "))) {
      // TODO add missing pronoms
      String mimeExtensions = RodaCoreFactory.getRodaConfigurationAsString("tools", "pronom", pronom);

      map.put(pronom, Arrays.asList(mimeExtensions.split(" ")));
    }

    return map;
  }

  public static Map<String, List<String>> getMimetypeToExtension() {
    Map<String, List<String>> map = new HashMap<>();
    String inputFormatMimetypes = RodaCoreFactory.getRodaConfigurationAsString("tools", "ghostscriptconvert",
      "inputFormatMimetypes");

    for (String mimetype : Arrays.asList(inputFormatMimetypes.split(" "))) {
      // TODO add missing mimetypes
      String mimeExtensions = RodaCoreFactory.getRodaConfigurationAsString("tools", "mimetype", mimetype);

      map.put(mimetype, Arrays.asList(mimeExtensions.split(" ")));
    }

    return map;
  }

  public static List<String> getInputExtensions() {
    // TODO add missing extensions
    String inputFormatExtensions = RodaCoreFactory.getRodaConfigurationAsString("tools", "ghostscriptconvert",
      "inputFormatExtensions");
    return Arrays.asList(inputFormatExtensions.split(" "));
  }

}
