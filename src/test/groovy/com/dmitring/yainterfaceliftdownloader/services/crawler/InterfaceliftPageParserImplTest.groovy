package com.dmitring.yainterfaceliftdownloader.services.crawler

import com.dmitring.yainterfaceliftdownloader.domain.PictureInfo
import com.dmitring.yainterfaceliftdownloader.services.crawler.impl.InterfaceliftPageParserImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static org.junit.Assert.*

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InterfaceliftPageParserImplTest.class)
class InterfaceliftPageParserImplTest {
    def interfaceliftPageParser

    @Before
    void setUp() {
        interfaceliftPageParser = new InterfaceliftPageParserImpl()
    }

    @Test(expected = IllegalArgumentException.class)
    void testParseNull() {
        // arrange
        def incorrectPageContent = null;

        // act
        interfaceliftPageParser.parsePage(incorrectPageContent);
    }

    @Test(expected = IllegalArgumentException.class)
    void testParseIncorrectData() {
        // arrange
        def incorrectPageContent = "fsdf f 456r Tf56df2s00aopW2c 2e";

        // act
        interfaceliftPageParser.parsePage(incorrectPageContent);
    }

    @Test(expected = IllegalArgumentException.class)
    void testParseNonInterfaceliftHtml() {
        // arrange
        def nonInterfaceliftHtmlPageContent = this.getClass().getResource('/NonInterfaceliftHtml.html').text

        // act
        interfaceliftPageParser.parsePage(nonInterfaceliftHtmlPageContent);
    }

    @Test
    void testParseLastPageWith6Pictures() {
        // arrange
        def nonInterfaceliftHtmlPageContent = this.getClass().getResource('/InterfaceliftLastPageWith6Pictures.html').text
        def pictures = [
                new PictureInfo("01173_twinlakesco",
                        "Twin Lakes, Colorado",
                        "http://interfacelift.com/wallpaper/previews/01173_twinlakesco_672x420.jpg",
                        "http://interfacelift.com/wallpaper/7yz4ma1/01173_twinlakesco_1920x1080.jpg"),

                new PictureInfo("01174_motionstripes",
                        "Motion Stripes",
                        "http://interfacelift.com/wallpaper/previews/01174_motionstripes_672x420.jpg",
                        "http://interfacelift.com/wallpaper/7yz4ma1/01174_motionstripes_1920x1080.jpg"),

                new PictureInfo("01171_paradisebeach",
                        "Paradise Beach",
                        "http://interfacelift.com/wallpaper/previews/01171_paradisebeach_672x420.jpg",
                        "http://interfacelift.com/wallpaper/7yz4ma1/01171_paradisebeach_1920x1080.jpg"),

                new PictureInfo("01172_retinaburn",
                        "Retina Burn",
                        "http://interfacelift.com/wallpaper/previews/01172_retinaburn_672x420.jpg",
                        "http://interfacelift.com/wallpaper/7yz4ma1/01172_retinaburn_1920x1080.jpg"),

                new PictureInfo("01090_fastburndetail",
                        "Fast Burn Detail",
                        "http://interfacelift.com/wallpaper/previews/01090_fastburndetail_672x420.jpg",
                        "http://interfacelift.com/wallpaper/7yz4ma1/01090_fastburndetail_1920x1080.jpg"),

                new PictureInfo("00375_aprettygoodwall",
                        "A Pretty Good Wall",
                        "http://interfacelift.com/wallpaper/previews/00375_aprettygoodwall_672x420.jpg",
                        "http://interfacelift.com/wallpaper/7yz4ma1/00375_aprettygoodwall_1920x1080.jpg")
        ]


        // act
        def parsedPage = interfaceliftPageParser.parsePage(nonInterfaceliftHtmlPageContent)

        // assert
        assertFalse(parsedPage.isNextPageExists())
        assertArrayEquals(pictures.toArray(), parsedPage.getPictureInfo().toArray())
    }

    @Test
    void testNonLastPageWith2Pictures() {
        // arrange
        def nonInterfaceliftHtmlPageContent = this.getClass().getResource('/InterfaceliftNonLastPageWith2Pictures.html').text
        def pictures = [
                new PictureInfo("01173_twinlakesco",
                        "Twin Lakes, Colorado",
                        "http://interfacelift.com/wallpaper/previews/01173_twinlakesco_672x420.jpg",
                        "http://interfacelift.com/wallpaper/7yz4ma1/01173_twinlakesco_1920x1080.jpg"),

                new PictureInfo("01174_motionstripes",
                        "Motion Stripes",
                        "http://interfacelift.com/wallpaper/previews/01174_motionstripes_672x420.jpg",
                        "http://interfacelift.com/wallpaper/7yz4ma1/01174_motionstripes_1920x1080.jpg")
        ]


        // act
        def parsedPage = interfaceliftPageParser.parsePage(nonInterfaceliftHtmlPageContent)

        // assert
        assertTrue(parsedPage.isNextPageExists())
        assertArrayEquals(pictures.toArray(), parsedPage.getPictureInfo().toArray())
    }
}
