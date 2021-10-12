package Java;

import java.util.concurrent.atomic.AtomicReference;

public class ToArduino {
    public static void main(String[] args) {
        AtomicReference<String> str = new AtomicReference<>("");
       str.set("hello, world");
        System.out.println("s="+str);
        str.set("");
        System.out.println("s="+str);

    }
}
