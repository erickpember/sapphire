// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.storm.trident;

import com.datafascia.common.inject.Injectors;
import java.util.List;
import java.util.Map;
import storm.trident.operation.Filter;
import storm.trident.operation.Function;
import storm.trident.operation.Operation;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.state.QueryFunction;
import storm.trident.state.State;
import storm.trident.tuple.TridentTuple;

/**
 * Injects dependencies into an operation. The operation class must define
 * fields annotated with {@code @Inject} which will be set with the
 * dependencies.
 */
public class DependencyInjectingProxy {

  private static abstract class DependencyInjectingOperation implements Operation {
    protected Operation delegate;

    public DependencyInjectingOperation(Operation delegate) {
      this.delegate = delegate;
    }

    @Override
    public void prepare(Map stormConf, TridentOperationContext context) {
      Injectors.getInjector().injectMembers(delegate);
      delegate.prepare(stormConf, context);
    }

    @Override
    public void cleanup() {
      delegate.cleanup();
    }
  }

  private static class DependencyInjectingFilter
      extends DependencyInjectingOperation implements Filter {

    public DependencyInjectingFilter(Filter delegate) {
      super(delegate);
    }

    @Override
    public boolean isKeep(TridentTuple tuple) {
      return ((Filter) delegate).isKeep(tuple);
    }
  }

  private static class DependencyInjectingFunction
      extends DependencyInjectingOperation implements Function {

    public DependencyInjectingFunction(Function delegate) {
      super(delegate);
    }

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
      ((Function) delegate).execute(tuple, collector);
    }
  }

  private static class DependencyInjectingQueryFunction<S extends State, T>
      extends DependencyInjectingOperation implements QueryFunction<S, T> {

    public DependencyInjectingQueryFunction(QueryFunction<S, T> delegate) {
      super(delegate);
    }

    @Override
    public List<T> batchRetrieve(S state, List<TridentTuple> inputs) {
      return ((QueryFunction<S, T>) delegate).batchRetrieve(state, inputs);
    }

    @Override
    public void execute(TridentTuple tuple, T result, TridentCollector collector) {
      ((QueryFunction<S, T>) delegate).execute(tuple, result, collector);
    }
  }

  /**
   * Creates filter with injected dependencies.
   *
   * @param delegate
   *     filter to delegate to
   * @return filter
   */
  public static Filter create(Filter delegate) {
    return new DependencyInjectingFilter(delegate);
  }

  /**
   * Creates function with injected dependencies.
   *
   * @param delegate
   *     function to delegate to
   * @return function
   */
  public static Function create(Function delegate) {
    return new DependencyInjectingFunction(delegate);
  }

  /**
   * Creates query function with injected dependencies.
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
    return new DependencyInjectingQueryFunction<>(delegate);
  }

  // Private constructor prevents creating instances of this class.
  private DependencyInjectingProxy() {
  }
}
