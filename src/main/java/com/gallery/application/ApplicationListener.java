package com.gallery.application;

import com.gallery.GalleryApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.session.events.SessionExpiredEvent;
import org.springframework.stereotype.Component;

/**
 * ApplicationListener
 * <p>
 * 2019-03-22 14:06 [Friday]
 *
 * @author Dennis Obukhov
 */
@Component
public final class ApplicationListener {


    @Autowired
    Logger logger;

    @EventListener
    public void onApplicationEvent(ApplicationStartedEvent event) {
        logger.debug("onApplicationEvent fired");
    }

    @EventListener
    public void onSessionExpiredEvent(SessionExpiredEvent event) {
        LoggerFactory.getLogger(GalleryApplication.class).debug("SessionExpiredEvent Event");
    }

}
