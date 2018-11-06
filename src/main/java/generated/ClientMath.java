package generated;

import implementation.UnsyncRequestor;
import interfaces.Requestor;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.lang.Float;
import java.lang.Integer;
import model.AbsoluteObjectReference;

/**
 * Provides mathematical methods */
public class ClientMath implements Math {
  private AbsoluteObjectReference aor;

  private Requestor r;

  public ClientMath(AbsoluteObjectReference aor) {
    this.aor = aor;
    this.r = new UnsyncRequestor();
  }

  /**
   * Returns the value of pi given a precision
   * @param precision Desired precision
   * @return float */
  public Float pi(Float precision) throws exception.RemoteError, IOException,
      ClassNotFoundException {
    return (Float) r.request(aor,"pi",precision);
  }

  /**
   * Returns the i-th element of the fibonacci sequence
   * @param start Starting number of the sequence
   * @param i Desired element
   * @return int */
  public Integer fibonacci(Integer start, Integer i) throws exception.RemoteError, IOException,
      ClassNotFoundException {
    return (Integer) r.request(aor,"fibonacci",start,i);
  }
}
