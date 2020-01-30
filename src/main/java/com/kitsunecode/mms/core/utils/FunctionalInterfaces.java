package com.kitsunecode.mms.core.utils;

public class FunctionalInterfaces {

    @FunctionalInterface
    public interface CheckedRunnable {
        void run() throws Exception;
    }

}
