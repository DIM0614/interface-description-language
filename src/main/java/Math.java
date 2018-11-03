/**
 * Provides mathematical methods */
public interface Math {
  /**
   * Returns the value of pi given a precision
   * @param precision Desired precision
   * @return float */
  float pi(float precision) throws RemoteError;

  /**
   * Returns the i-th element of the fibonacci sequence
   * @param start Starting number of the sequence
   * @param i Desired element
   * @return int */
  int fibonacci(int start, int i) throws RemoteError;
}
