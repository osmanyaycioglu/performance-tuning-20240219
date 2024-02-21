package training.performance.test.memory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerCache {

    private Map<String, Customer> customerMap = new ConcurrentHashMap<>(1_000_000,
                                                                        0.9f,
                                                                        1_000);

    public void add(String username,Customer customerParam){
        customerMap.put(username, customerParam);
    }

    public void method1(){
        UUID uuidLoc = UUID.randomUUID();
        try {
            Thread.sleep(1);
        } catch (Exception exp) {
        }
        method2();
    }

    public void method2(){
        UUID uuidLoc = UUID.randomUUID();
        try {
            Thread.sleep(3);
        } catch (Exception exp) {
        }
        method3();

    }
    public void method3(){
        UUID uuidLoc = UUID.randomUUID();
        try {
            Thread.sleep(4);
        } catch (Exception exp) {
        }
        method4();

    }
    public void method4(){
        UUID uuidLoc = UUID.randomUUID();
        try {
            Thread.sleep(1);
        } catch (Exception exp) {
        }

    }
}
