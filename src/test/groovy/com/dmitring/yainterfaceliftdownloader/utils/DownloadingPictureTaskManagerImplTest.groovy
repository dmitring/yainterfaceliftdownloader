package com.dmitring.yainterfaceliftdownloader.utils

import com.dmitring.yainterfaceliftdownloader.services.DownloadingPictureTaskManager
import com.dmitring.yainterfaceliftdownloader.services.impl.DownloadingPictureTaskManagerImpl
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
@SpringBootTest(classes = DownloadingPictureTaskManagerImplTest.class)
class DownloadingPictureTaskManagerImplTest {
    DownloadingPictureTaskManager downloadingPictureTaskManager

    @Before
    void setUp() {
        downloadingPictureTaskManager = new DownloadingPictureTaskManagerImpl()
    }

    @Test
    void testTaskPutCompleteSuccessfully() {
        // arrange and act
        def taskFuture = downloadingPictureTaskManager.run("someKey", (Supplier<Boolean>) { return true })

        // assert
        AssertFutureUtil.getAndAssert(taskFuture, true, 100)
    }

    @Test
    void testTaskPutCompleteUnsuccessfully() {
        // arrange & act
        def taskFuture = downloadingPictureTaskManager.run("someKey", (Supplier<Boolean>) { return false })

        // assert
        AssertFutureUtil.getAndAssert(taskFuture, false, 100)
    }

    @Test
    void testReturnNullOnRunningTask() {
        // arrange & act
        downloadingPictureTaskManager.run("someKey", (Supplier<Boolean>) {
            Thread.sleep(100000)
            return true
        })
        def taskFuture2 = downloadingPictureTaskManager.run("someKey", (Supplier<Boolean>) { return true })

        // assert
        assertNull(taskFuture2)
    }

    @Test
    void testEnsureCancelTask() {
        // arrange & act
        def key = "someKey"
        def taskFuture = downloadingPictureTaskManager.run(key, (Supplier<Boolean>) {
            Thread.sleep(100)
            return false
        })
        downloadingPictureTaskManager.tryCancelTask(key)

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
