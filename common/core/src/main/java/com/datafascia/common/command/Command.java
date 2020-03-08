// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.common.command;

/**
 * Command invoked from command line. Implementation classes must be annotated with
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
