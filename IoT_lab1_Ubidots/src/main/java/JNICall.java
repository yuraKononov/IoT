public class JNICall {
    static {
        System.loadLibrary("JNICall");
    }
    public JNICall()
    {}

    public native double getMemoryUsage();
    //public native double getTemperature();
}