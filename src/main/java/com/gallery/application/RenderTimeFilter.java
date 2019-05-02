package com.gallery.application;


import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Project: Gallery
 * Class: RenderTimeFilter
 * Date: 2019-03-24 13:15 [Sunday]
 *
 * @author Dennis Obukhov
 */

@Component
public final class RenderTimeFilter implements Filter {

    @Autowired
    @Lazy
    private Logger logger;

    @Value("${gallery.log.render-time}")
    private boolean enabled;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (enabled) {
            HttpServletRequest req = (HttpServletRequest) request;
            long nanoTime = System.nanoTime();
            chain.doFilter(request, response);
            double time = (System.nanoTime() - nanoTime) * 1e-9;
            logger.debug(
                    String.format("'%s' is served in %.3f s", req.getRequestURI(),
                            time));
        } else {
            chain.doFilter(request, response);
        }
    }
}
