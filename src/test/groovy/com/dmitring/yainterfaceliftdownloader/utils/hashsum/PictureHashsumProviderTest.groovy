package com.dmitring.yainterfaceliftdownloader.utils.hashsum

import com.dmitring.yainterfaceliftdownloader.domain.Picture
import com.dmitring.yainterfaceliftdownloader.utils.pictureStreams.PictureStreamProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.stubbing.Answer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull
import static org.mockito.Matchers.any
import static org.mockito.Mockito.*

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PictureHashsumProviderTest.class)
class PictureHashsumProviderTest {
    byte[] testData = [0x4f, 0x0c, 0x00, 0x94, 0x39, 0xff];
    def somePicture = new Picture();

    def mockPictureStreamProvider;

    @Before
    void setUp() {
        mockPictureStreamProvider = mock(PictureStreamProvider.class);
    }

    @Test
    void testHashSum() {
        // arrange
        def spyInputStream = spy(new ByteArrayInputStream(testData));
        when(mockPictureStreamProvider.getInputStream(any(Picture.class) as Picture)).thenReturn(spyInputStream);
        def provider = new PictureHashsumProvider(mockPictureStreamProvider);

        // act
        def hashsum = provider.getHashsum(somePicture);

        // assert
        assertEquals(hashsum, "24b3459e5aef93f6c7467336a6bba2b8");
        verify(spyInputStream).close();
    }

    @Test
    void testNullOnFileNotFound() throws Exception {
        // arrange
        def provider = new PictureHashsumProvider(mockPictureStreamProvider);
        when(mockPictureStreamProvider.getInputStream(any(Picture.class) as Picture)).thenThrow(new FileNotFoundException());

        // act
        String hashsum = provider.getHashsum(somePicture);

        // assert
        assertNull(hashsum);
    }

    @Test
    void testNullOnIOExceptionWhileFileReading() throws Exception {
        // arrange
        def InputStream mockErrorfulInputStream = mock(InputStream.class, (Answer){ invocation -> throw new IOException()});
        when(mockPictureStreamProvider.getInputStream(any(Picture.class) as Picture)).thenReturn(mockErrorfulInputStream);
        def provider = new PictureHashsumProvider(mockPictureStreamProvider);

        // act
        def hashsum = provider.getHashsum(somePicture);

        // assert
        assertNull(hashsum);
        verify(mockErrorfulInputStream).close();
    }
}
