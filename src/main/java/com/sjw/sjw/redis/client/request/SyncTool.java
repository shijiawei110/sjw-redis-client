package com.sjw.sjw.redis.client.request;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author shijiawei
 * @version SyncTool.java, v 0.1
 * @date 2018/11/20
 */
public class SyncTool extends AbstractQueuedSynchronizer {

    private SyncTool() {
    }

    public static final SyncTool instance() {
        return SyncTool.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final SyncTool INSTANCE = new SyncTool();
    }

    public static final int LOCK = 1;

    public static final int UNLOCK = 0;

    @Override
    protected boolean tryAcquire(int acquires) {
        if (compareAndSetState(UNLOCK, acquires)) {
            return true;
        }
        return false;

    }

    @Override
    protected boolean tryRelease(int releases) {
        setState(releases);
        return true;
    }
}
