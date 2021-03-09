package org.geekbang.time.read.column.java.kim;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.lang.instrument.Instrumentation;

/**
 * Instrumentation.getObjectSize()估算一个对象占用的内存空间
 * JOL (Java Object Layout) 可以用来查看对象内存布局
 */
@Slf4j
public class MemoryWatchDemo {

    public static void main(String[] args) {
        Integer[] arr = {1,2,3,4,5};
        Demo demo = new Demo();
        demo.setName("Ok");
        demo.setArr(arr);
        log.info("查看对象内存布局");
        log.info(ClassLayout.parseInstance(demo).toPrintable());
        log.info("查看对象所占空间大小");
    }

}

@Data
class Demo{
    String name;
    Integer[] arr;

}
