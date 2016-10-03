package com.dmitring.yainterfaceliftdownloader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.logging.Logger;

@Configuration
@AutoConfigureAfter(DispatcherServletAutoConfiguration.class)
public class ServeDownloadPicturesConfig extends WebMvcConfigurerAdapter {
    private static final Logger log = Logger.getLogger(ServeDownloadPicturesConfig.class.getName());

    @Value("#{'/'+'${com.dmitring.yainterfaceliftdownloader.thumbnailPrefixPath}'+'/'}")
    private String thumbnailPath = "/thumbnails/";

    @Value("#{'/'+'${com.dmitring.yainterfaceliftdownloader.fullPicturePrefixPath}'+'/'}")
    private String fullPicturesPath = "/fullPictures/";

    @Value("#{'/'+'${com.dmitring.yainterfaceliftdownloader.downloadUrlPrefix}' + '/' + '${com.dmitring.yainterfaceliftdownloader.thumbnailPrefixPath}'+'/'}")
    private String thumbnailUriPath = "/downloaded/thumbnails/*";

    @Value("#{'/'+'${com.dmitring.yainterfaceliftdownloader.downloadUrlPrefix}' + '/' + '${com.dmitring.yainterfaceliftdownloader.fullPicturePrefixPath}'+'/'}")
    private String fullPicturesUriPath = "/downloaded/fullPictures/*";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String prefix = System.getProperty("user.dir");
        registry.addResourceHandler(thumbnailUriPath).addResourceLocations("file:" + prefix + thumbnailPath);
        registry.addResourceHandler(fullPicturesUriPath).addResourceLocations("file:" + prefix + fullPicturesPath);
        super.addResourceHandlers(registry);
        log.fine(String.format("Request filter %s, %s has been registered", thumbnailUriPath, fullPicturesUriPath));
    }
}
