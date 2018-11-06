package generated;

import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.lang.Float;
import java.lang.Integer;

/**
 * Provides mathematical methods */
public interface Math {
  /**
   * Returns the value of pi given a precision
   * @param precision Desired precision
   * @return float */
  Float pi(Float precision) throws exception.RemoteError, IOException, ClassNotFoundException;

  /**
   * Returns the i-th element of the fibonacci sequence
   * @param start Starting number of the sequence
   * @param i Desired element
   * @return int */
  Integer fibonacci(Integer start, Integer i) throws exception.RemoteError, IOException,
      ClassNotFoundException;
}
