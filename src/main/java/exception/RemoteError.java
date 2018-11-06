package exception;

/**
 * This class represents an error that occurred
 * at some point in the remote communication.
 * It can be caused by several known factors during development time.
 *
 * Note: Inheriting from Exception this class requires the programmer to handle the exception
 *
 * @author MatheusAlvesA
 */
public class RemoteError extends Exception {

    private static final long serialVersionUID = 1L;

    private String state;

    /*
     * Overwriting java exception constructors to grant all features.
     * */
    public RemoteError() {/* empty */}
    public RemoteError(String arg0) {super(arg0);}
    public RemoteError(Throwable arg0) {super(arg0);}
    public RemoteError(String arg0, Throwable arg1) {super(arg0, arg1);}
    public RemoteError(String arg0, Throwable arg1, boolean arg2, boolean arg3) {super(arg0, arg1, arg2, arg3);}

    /**
     * This function saves the state of a variable during the occurrence of the exception.
     *
     * @param varName - Variable's name.
     * @param value - Value of the variable.
     */
    public void addState(String varName, Object value) {
        if(this.state == null)
            this.state = "";

        if(value != null)
            this.state += "Var ("+value.getClass().getName()+") "+varName+" = "+value.toString()+"\n";
        else
            this.state += "Var (???) "+varName+" = NULL\n";
    }

    /**
     * This function returns the state of the method variables at the time the exception occurred.
     *
     * @return The state serialized of the method
     */
    public String getState() {
        if(this.state == null)
            return "Nothing save on state\n";
        return this.state;
    }

}
