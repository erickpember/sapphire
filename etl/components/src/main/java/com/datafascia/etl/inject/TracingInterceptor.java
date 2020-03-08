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
package com.datafascia.etl.inject;

import com.google.common.base.Stopwatch;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Logs messages before and after method invocation.
 */
@Slf4j
public class TracingInterceptor implements MethodInterceptor {

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    if (!log.isDebugEnabled()) {
      return invocation.proceed();
    }

    Method method = invocation.getMethod();
    String methodName = method.getDeclaringClass().getSimpleName() + '.' + method.getName();
    log.debug("{} BEFORE", methodName);
    Stopwatch stopwatch = Stopwatch.createStarted();
    try {
      return invocation.proceed();
    } finally {
      stopwatch.stop();
      log.debug("{} AFTER, elapsed {}", methodName, stopwatch);
    }
  }
}
