package generated;

import implementation.Invocation;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.lang.Float;
import java.lang.Integer;
import java.lang.Object;

/**
 * Provides mathematical methods */
public abstract class Invoker implements Math {
  private Integer id;

  public Invoker(Integer id) {
    this.id = id;
  }

  /**
   * Returns the value of pi given a precision
   * @param precision Desired precision
   * @return float */
  public abstract Float pi(Float precision) throws exception.RemoteError, IOException,
      ClassNotFoundException;

  /**
   * Returns the i-th element of the fibonacci sequence
   * @param start Starting number of the sequence
   * @param i Desired element
   * @return int */
  public abstract Integer fibonacci(Integer start, Integer i) throws exception.RemoteError,
      IOException, ClassNotFoundException;

  public Object invoke(Invocation invocation) throws exception.RemoteError, IOException,
      ClassNotFoundException {
    Object[] params = invocation.getInvocationData().getActualParams();
    if (invocation.getInvocationData().getOperationName().equals( "pi" )) {
      return pi( (float) params[0] );
    }
    if (invocation.getInvocationData().getOperationName().equals( "fibonacci" )) {
      return fibonacci( (int) params[0],  (int) params[1] );
    }
    return null;
  }
}
