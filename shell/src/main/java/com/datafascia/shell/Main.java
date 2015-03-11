// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.shell;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.datafascia.common.command.Command;
import com.datafascia.common.command.CommandLoader;
import com.datafascia.common.urn.URNMap;
import com.datafascia.domain.model.Version;
import java.util.HashMap;

/**
 * Parses command line arguments and executes command.
 */
public class Main {
  /** Package name for models */
  private static final String MODELS_PKG = Version.class.getPackage().getName();

  private static final String PROGRAM_NAME = "dfp";

  /**
   * Parses command line arguments and executes command.
   *
   * @param args the arguments for the command
   */
  public static void main(String[] args) {
    URNMap.idNSMapping(MODELS_PKG);
    Main application = new Main();
    application.run(args);
  }

  private void run(String[] args) {
    Command command = parseCommand(args);
    int exitStatus = command.execute();
    if (exitStatus != Command.EXIT_STATUS_SUCCESS) {
      System.exit(exitStatus);
    }
  }

  private Command parseCommand(String[] args) {
    JCommander jc = new JCommander();
    jc.setProgramName(PROGRAM_NAME);

    HashMap<String, Command> nameToCommandMap = new HashMap<>();
    for (Command command : CommandLoader.getCommands()) {
      jc.addCommand(command);
      nameToCommandMap.put(getCommandName(command), command);
    }

    jc.parse(args);
    String commandName = jc.getParsedCommand();
    if (commandName == null) {
      jc.usage();
      System.exit(Command.EXIT_STATUS_FAILURE);
    }
    return nameToCommandMap.get(commandName);
  }

  private String getCommandName(Command command) {
    Class<?> clazz = command.getClass();
    Parameters parameters = clazz.getAnnotation(Parameters.class);
    if (parameters != null && parameters.commandNames().length > 0) {
      return parameters.commandNames()[0];
    } else {
      throw new ParameterException(
          clazz.getName() + " missing commandNames attribute in @Parameters annotation");
    }
  }
}
