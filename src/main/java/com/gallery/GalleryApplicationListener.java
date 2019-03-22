package com.gallery.model;

import com.gallery.GalleryApplication;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * GalleryApplicationListener
 * <p>
 * 2019-03-22 14:06 [Friday]
 *
 * @author Dennis Obukhov
 */
@Component
public class GalleryApplicationListener implements ApplicationListener<ApplicationContextInitializedEvent> {
    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent event) {
        LoggerFactory.getLogger(GalleryApplication.class).debug("ApplicationStartedEvent: " + event);
        System.out.println("FFF");
        throw new NullPointerException();
    }
}
