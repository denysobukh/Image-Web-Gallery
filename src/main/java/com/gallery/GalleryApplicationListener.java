package com.gallery;

import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * GalleryApplicationListener
 * <p>
 * 2019-03-22 14:06 [Friday]
 *
 * @author Dennis Obukhov
 */
@Component
public class GalleryApplicationListener {

    @EventListener
    public void onApplicationEvent(ApplicationStartedEvent event) {
        LoggerFactory.getLogger(GalleryApplication.class).debug("Application Started Event");
    }
}
