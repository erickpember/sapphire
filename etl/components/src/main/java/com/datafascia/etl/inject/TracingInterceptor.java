// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.inject;

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
    try {
      return invocation.proceed();
    } finally {
      log.debug("{} AFTER", methodName);
    }
  }
}
