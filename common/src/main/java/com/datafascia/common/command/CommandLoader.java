// Copyright (C) 2015 dataFascia Corporation.  All rights reserved.
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.command;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceLoader;

/**
 * Loads {@link Command} implementations.
 */
public class CommandLoader {

  /**
   * Gets commands.
   */
  public static Collection<Command> getCommands() {
    ServiceLoader<Command> serviceLoader = ServiceLoader.load(Command.class);
    ArrayList<Command> commands = new ArrayList<>();
    Iterables.addAll(commands, serviceLoader);
    return commands;
  }

  // Private constructor disallows creating instances of this class
  private CommandLoader() {
  }
}
