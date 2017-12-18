import com.ubidots.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        ProcessorTemperature processorTemperature = new ProcessorTemperature();
        processorTemperature.start();
    }
}
class ProcessorTemperature extends Thread {
    public void run(){
        Double jniCall = new JNICall().getMemoryUsage();
        System.out.println(jniCall);

        ApiClient api = new ApiClient("A1E-d8254ec1d2f61c404501021aa5319f2e59f5");

        DataSource dataSource = api.getDataSource("5a1846bf76254227cfb8578b");
        Variable[] variable = dataSource.getVariables();

        for(int i = 0; i < variable.length; i++){
            if(variable[i].getName().contains("MemoryUsage")){
                double[] values = new double[10];
                long[] timestamps = new long[10];
                for(int j = 0; j < 10; j++) {
                    values[j] = new JNICall().getMemoryUsage() * 100;

                    Date date = new Date();
                    timestamps[j] = date.getTime();
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(values[j] + " " + timestamps[j]);
                }
                variable[i].saveValues(values, timestamps);
            }
        }
    }
}
