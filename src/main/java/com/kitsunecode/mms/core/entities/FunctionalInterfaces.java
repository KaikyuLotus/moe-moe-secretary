package com.kitsunecode.mms.core.entities;

public class FunctionalInterfaces {

    @FunctionalInterface
    public interface CheckedRunnable {
        void run() throws Exception;
    }

}
