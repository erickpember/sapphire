// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.storm.trident;

import backtype.storm.Config;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import storm.trident.operation.Filter;
import storm.trident.operation.Function;
import storm.trident.operation.Operation;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.state.QueryFunction;
import storm.trident.state.State;
import storm.trident.tuple.TridentTuple;

/**
 * Puts values into MDC so they are logged.
 */
@Slf4j
public class LoggingProxy {

  private static abstract class LoggingOperation implements Operation {
    protected Operation delegate;
    private transient String topologyName;

    LoggingOperation(Operation delegate) {
      this.delegate = delegate;
    }

    @Override
    public void prepare(Map stormConf, TridentOperationContext context) {
      delegate.prepare(stormConf, context);

      topologyName = stormConf.get(Config.TOPOLOGY_NAME).toString();
    }

    protected void before(TridentTuple tuple) {
      MDC.put("topology", topologyName);
      if (log.isDebugEnabled()) {
        MDC.put("tuple", StringUtils.abbreviate(tuple.toString(), 256));
      }
    }

    protected void after() {
      MDC.clear();
    }

    @Override
    public void cleanup() {
      delegate.cleanup();
    }
  }

  private static class LoggingFilter extends LoggingOperation implements Filter {
    LoggingFilter(Filter delegate) {
      super(delegate);
    }

    @Override
    public boolean isKeep(TridentTuple tuple) {
      try {
        before(tuple);
        return ((Filter) delegate).isKeep(tuple);
      } finally {
        after();
      }
    }
  }

  private static class LoggingFunction extends LoggingOperation implements Function {
    LoggingFunction(Function delegate) {
      super(delegate);
    }

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
      try {
        before(tuple);
        ((Function) delegate).execute(tuple, collector);
      } finally {
        after();
      }
    }
  }

  private static class LoggingQueryFunction<S extends State, T>
      extends LoggingOperation implements QueryFunction<S, T> {

    /**
     * Constructor
     *
     * @param delegate
     *     query function to delegate to
     */
    LoggingQueryFunction(QueryFunction<S, T> delegate) {
      super(delegate);
    }

    @Override
    public List<T> batchRetrieve(S state, List<TridentTuple> inputs) {
      try {
        before(inputs.get(0));
        return ((QueryFunction<S, T>) delegate).batchRetrieve(state, inputs);
      } finally {
        after();
      }
    }

    @Override
    public void execute(TridentTuple tuple, T result, TridentCollector collector) {
      try {
        before(tuple);
        ((QueryFunction<S, T>) delegate).execute(tuple, result, collector);
      } finally {
        after();
      }
    }
  }

  /**
   * Creates filter which logs input tuple.
   *
   * @param delegate
   *     filter to delegate to
   * @return filter
   */
  public static Filter create(Filter delegate) {
    return new LoggingFilter(delegate);
  }

  /**
   * Creates function which logs input tuple.
   *
   * @param delegate
   *     function to delegate to
   * @return function
   */
  public static Function create(Function delegate) {
    return new LoggingFunction(delegate);
  }

  /**
   * Creates query function which logs input tuple.
   *
   * @param delegate
   *     query function to delegate to
   * @param <S>
   *     state type
   * @param <T>
   *     result type
   * @return query function
   */
  public static <S extends State, T> QueryFunction<S, T> create(QueryFunction<S, T> delegate) {
    return new LoggingQueryFunction<>(delegate);
  }

  // Private constructor prevents creating instances of this class.
  private LoggingProxy() {
  }
}
