package com.dmitring.yainterfaceliftdownloader.utils.crawler.impl;

import com.dmitring.yainterfaceliftdownloader.domain.ParsedPage;
import com.dmitring.yainterfaceliftdownloader.domain.PictureInfo;
import com.dmitring.yainterfaceliftdownloader.utils.crawler.InterfaceliftPageParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class InterfaceliftPageParserImpl implements InterfaceliftPageParser {
    @Override
    public ParsedPage parsePage(String pageContent) {
        validatePage(pageContent);

        Document pageDocument = Jsoup.parse(pageContent);
        Collection<PictureInfo> pictureInfo = parsePictureInfo(pageDocument);
        boolean nextPageExists = doesNextPageExists(pageDocument);

        return new ParsedPage(pictureInfo, nextPageExists);
    }

    private boolean doesNextPageExists(Document pageDocument) {
        Element nextPage = pageDocument.select(".pagenums_bottom a:matchesOwn(next page)").first();
        String href = nextPage.attr("href");
        return (href != null && !href.isEmpty());
    }

    private List<PictureInfo> parsePictureInfo(Document pageDocument) {
        List<PictureInfo> pictures = new ArrayList<>();
        Elements pictureElements = pageDocument.select("#wallpaper > [id~=list_\\d+] > .item");

        for (Element pictureDomElement : pictureElements) {
            handleSinglePictureInfo(pictureDomElement).ifPresent(pictures::add);
        }

        return pictures;
    }

    private Optional<PictureInfo> handleSinglePictureInfo(Element pictureDomElement) {
        Element previewLinkDomElement = pictureDomElement.select(".preview a > img[title~=preview]").first();
        Element downloadLinkDomElement = pictureDomElement.select(".preview .download [id~=download_\\d+] > a").first();
        Element titleDomElement = pictureDomElement.select(".details h1 > a").first();

        if (previewLinkDomElement == null || downloadLinkDomElement == null || titleDomElement == null)
            return Optional.empty();

        String thumbnailUrl = previewLinkDomElement.attr("src");
        String fullUrlSuffix = downloadLinkDomElement.attr("href");
        String title = titleDomElement.text();
        return Optional.of(new PictureInfo(
                parsePictureId(thumbnailUrl),
                title,
                thumbnailUrl,
                getFullUrlPrefix() + fullUrlSuffix));
    }

    private void validatePage(String pageContent) {
        if (pageContent == null || pageContent.length() < 128 ||
                !pageContent.contains("<title>InterfaceLIFT: 1920x1080 Wallpaper sorted by Date</title>")) {
            throw new IllegalArgumentException("Incorrect web page content");
        }
    }

    private String parsePictureId(String pictureUrl) {
        int beginIndex = pictureUrl.lastIndexOf('/');
        int finishIndex = pictureUrl.lastIndexOf('_');

        return pictureUrl.substring(beginIndex + 1, finishIndex);
    }

    private String getFullUrlPrefix() {
        return "http://interfacelift.com";
    }
}
