package com.dmitring.yainterfaceliftdownloader.services.crawler

import com.dmitring.yainterfaceliftdownloader.services.crawler.impl.PictureDownloaderImpl
import com.dmitring.yainterfaceliftdownloader.utils.pictureStreams.PictureStreamProvider
import com.dmitring.yainterfaceliftdownloader.utils.pictureStreams.UrlStreamProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.stubbing.Answer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static org.junit.Assert.assertArrayEquals
import static org.mockito.Matchers.any
import static org.mockito.Mockito.*

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PictureDownloaderImplTest.class)
class PictureDownloaderImplTest {
    byte[] testData = [0xff, 0x0c, 0x00, 0x94, 0x39, 0xff, 0x00, 0x01]

    def getSpyInputStream() {
        return spy(new ByteArrayInputStream(testData))
    }

    def getErrorfulInputStream() {
        def errorfulInputStream = mock(InputStream.class, { invocation -> throw new IOException() } as Answer)
        return errorfulInputStream
    }

    def getSpyOutputStream() {
        return spy(new ByteArrayOutputStream())
    }

    def getErrorfulOutputStream() {
        def errorfulOutputStream = mock(OutputStream.class, {invocation -> throw new IOException()} as Answer)
        return errorfulOutputStream
    }

    void test(def inputStreamSupplier, def outputStreamSupplier, def doTestData) {
        // arrange
        def inputStream = inputStreamSupplier()
        def mockInputStreamProvider = mock(UrlStreamProvider.class)
        when(mockInputStreamProvider.getInputStream(any(String.class) as String)).thenReturn(inputStream)

        def outputStream = outputStreamSupplier();
        def mockOutputStreamProvider = mock(PictureStreamProvider.class)
        when(mockOutputStreamProvider.getOutputStream(any(String.class) as String)).thenReturn(outputStream)

        def pictureDownloader = new PictureDownloaderImpl(mockInputStreamProvider, mockOutputStreamProvider)

        // act
        pictureDownloader.download("test://some_url", "/test/some_path")

        // assert
        if (doTestData)
            assertArrayEquals(testData, outputStream.toByteArray())

        verify(inputStream).close()
        verify(outputStream).close()
    }

    @Test
    void testDownload() {
        test(this.&getSpyInputStream, this.&getSpyOutputStream, true)
    }

    @Test
    void testInputErrors() {
        test(this.&getErrorfulInputStream, this.&getSpyOutputStream, false);
    }

    @Test
    void testOutputErrors() {
        test(this.&getSpyInputStream, this.&getErrorfulOutputStream, false);
    }

    @Test
    void testInputAndOutputErrors() {
        test(this.&getErrorfulInputStream, this.&getErrorfulOutputStream, false);
    }
}
