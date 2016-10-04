package com.dmitring.yainterfaceliftdownloader.utils

import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

import static org.junit.Assert.*

class AssertFutureUtil {
    public static <T extends Future<Boolean>> void getAndAssert(T future, Boolean ethalonResult, long timeout) {
        final result = get(future, timeout)
        assertEquals(ethalonResult, result)
    }

    public static <T extends Future<Boolean>> Boolean get(T future, long timeout) {
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS)
        } catch (TimeoutException exception) {
            fail("Operation must be completed in ${timeout} milliseconds")
        }
    }
}
