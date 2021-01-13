package org.geekbang.time.totry.distributelock.curator;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.LockInternalsDriver;

@Slf4j
public class InterProcessCloseableMutex extends InterProcessMutex implements AutoCloseable {
    public InterProcessCloseableMutex(CuratorFramework client, String path) {
        super(client, path);
    }

    public InterProcessCloseableMutex(CuratorFramework client, String path, LockInternalsDriver driver) {
        super(client, path, driver);
    }

    @Override
    public void close() throws Exception {
        log.info(Thread.currentThread().getName()+" curator release success");
        super.release();
    }
}
