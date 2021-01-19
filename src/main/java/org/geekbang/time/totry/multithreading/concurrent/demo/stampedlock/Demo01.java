package org.geekbang.time.totry.multithreading.concurrent.demo.stampedlock;

import java.util.concurrent.locks.StampedLock;

/**
 * stampedlock示例 来自Oracle官方
 * {@link StampedLock}
 */
public class Demo01 {

}

class Point {

    private double x, y;
    private final StampedLock sl = new StampedLock();

    //涉及对共享资源的修改，使用写锁-独占操作
    void move(double deltaX, double deltaY) { // an exclusively locked method
        long stamp = sl.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    /**
     * 使用乐观读访问共享资源
     * 注意：乐观读在保证数据一致性上需要拷贝一份要操作的变量到方法栈，
     * 并且在操作数据时候可能其他写线程已经修改了数据，
     *
     * 而我们操作的是方法栈里面的数据，也就是一个快照，
     * 所以最多返回的不是最新的数据，但是一致性还是得到保障的。
     * @return
     */
    double distanceFromOrigin() { // A read-only method
        long stamp = sl.tryOptimisticRead(); //使用乐观读
        double currentX = x, currentY = y;// 拷贝共享资源到本地方法栈中
        if (!sl.validate(stamp)) {// 如果有写锁被占用，可能造成数据不一致，所以要切换到普通读锁模式
            stamp = sl.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                sl.unlockRead(stamp);
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }

    void moveIfAtOrigin(double newX, double newY) { // upgrade
        // Could instead start with optimistic, not read mode
        long stamp = sl.readLock();
        try {
            while (x == 0.0 && y == 0.0) {
                long ws = sl.tryConvertToWriteLock(stamp);//读锁转换为写锁
                if (ws != 0L) {
                    stamp = ws;
                    x = newX;
                    y = newY;
                    break;
                }
                else {
                    sl.unlockRead(stamp);
                    stamp = sl.writeLock();
                }
            }
        } finally {
            sl.unlock(stamp);
        }
    }
}