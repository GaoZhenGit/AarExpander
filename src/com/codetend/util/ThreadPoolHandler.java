package com.codetend.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolHandler {

    public static Item create() {
        return new Item();
    }

    public static class Item {
        private final ExecutorService mThreadPool;
        private List<Future> mResultSet = new ArrayList<>();

        public Item() {
            mThreadPool = Executors.newCachedThreadPool();
        }

        public void submit(Callable callable) {
            mResultSet.add(mThreadPool.submit(callable));
        }

        public void startWait() {
            mThreadPool.shutdown();
            for (Future future : mResultSet) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
