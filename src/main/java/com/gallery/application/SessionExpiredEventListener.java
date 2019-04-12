package com.gallery.application;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.events.SessionExpiredEvent;

/**
 * SessionExpiredEventListener class
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 15:40 [Friday]
 */
@Configuration
public class SessionExpiredEventListener implements ApplicationListener<SessionExpiredEvent> {

    @Autowired
    Logger logger;

    @Override
    public void onApplicationEvent(SessionExpiredEvent event) {
        logger.debug("onApplicationEvent(SessionExpiredEvent event)");

    }
}
