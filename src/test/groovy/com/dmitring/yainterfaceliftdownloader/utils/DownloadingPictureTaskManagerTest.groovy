package com.dmitring.yainterfaceliftdownloader.utils

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.function.Supplier

import static org.junit.Assert.*

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DownloadingPictureTaskManagerTest.class)
class DownloadingPictureTaskManagerTest {
    def downloadingPictureTaskManager

    @Before
    void setUp() {
        downloadingPictureTaskManager = new DownloadingPictureTaskManager()
    }

    @Test
    void testTaskPutCompleteSuccessfully() {
        // arrange and act
        def taskFuture = downloadingPictureTaskManager.putTask("someKey", (Supplier<Boolean>) { return true })

        // assert
        try {
            assertTrue(taskFuture.get(1000, TimeUnit.MILLISECONDS))
        } catch (TimeoutException exception) {
            fail("Download must complete almost immediately. All long time operation was mocked.")
        }
    }

    @Test
    void testTaskPutCompleteUnsuccessfully() {
        // arrange & act
        def taskFuture = downloadingPictureTaskManager.putTask("someKey", (Supplier<Boolean>) { return false })

        // assert
        try {
            assertFalse(taskFuture.get(1000, TimeUnit.MILLISECONDS))
        } catch (TimeoutException exception) {
            fail("Download must complete almost immediately. All long time operation was mocked.")
        }
    }

    @Test
    void testReturnNullOnRunningTask() {
        // arrange & act
        downloadingPictureTaskManager.putTask("someKey", (Supplier<Boolean>) {
            Thread.sleep(100000)
            return true
        })
        def taskFuture2 = downloadingPictureTaskManager.putTask("someKey", (Supplier<Boolean>) { return true })

        // assert
        assertNull(taskFuture2)
    }

    @Test
    void testEnsureCancelTask() {
        // arrange & act
        def key = "someKey"
        def taskFuture = downloadingPictureTaskManager.putTask(key, (Supplier<Boolean>) {
            Thread.sleep(100)
            return false
        })
        downloadingPictureTaskManager.ensureCancelTask(key)

        // assert
        try {
            taskFuture.get(100, TimeUnit.MILLISECONDS)
        } catch (CancellationException) {

        }
        catch (TimeoutException exception) {
            fail("Download must complete almost immediately. All long time operation was mocked.")
        }
    }
}
