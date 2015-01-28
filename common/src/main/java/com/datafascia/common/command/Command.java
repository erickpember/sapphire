// Copyright (C) 2015 dataFascia Corporation.  All rights reserved.
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.command;

/**
 * Command invoked from command line.  Implementation classes must be annotated with
 * {@link com.beust.jcommander.Parameters} and have fields annotated with
 * {@link com.beust.jcommander.Parameter}.
 */
public interface Command {

  int EXIT_STATUS_FAILURE = 1;
  int EXIT_STATUS_SUCCESS = 0;

  /**
   * Executes command.
   *
   * @return exit status
   */
  int execute();
}
