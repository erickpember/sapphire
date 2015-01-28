// Copyright (C) 2015 dataFascia Corporation.  All rights reserved.
// For license information, please contact http://datafascia.com/contact
package com.datafascia.shell;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.datafascia.common.command.Command;
import com.datafascia.common.command.CommandLoader;
import java.util.HashMap;

/**
 * Parses command line arguments and executes command.
 */
public class Main {

  private static final String PROGRAM_NAME = "dfp";

  /**
   * Parses command line arguments and executes command.
   */
  public static void main(String[] args) {
    Main application = new Main();
    application.run(args);
  }

  private void run(String[] args) {
    Command command = parseCommand(args);
    int exitStatus = command.execute();
    System.exit(exitStatus);
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
