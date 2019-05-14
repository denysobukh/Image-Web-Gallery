package com.gallery.service;

import com.ibm.icu.text.Transliterator;

import java.nio.file.Path;

/**
 * Path2UriConverter class
 * encodes Path to be use as id of a folder in URL
 *
 * @author Dennis Obukhov
 * @date 2019-05-14 18:11 [Tuesday]
 */
public class Path2UriConverter {
    private static final Transliterator transliterator = Transliterator.getInstance("Russian-Latin/BGN");

    public String getUri() {
        return uri;
    }

    private final String uri;

    public Path2UriConverter(Path uri) {
        String u = encode(uri.toString());
        u = u.replaceAll("[^a-zA-Z0-9ʹ]" , "-");
        u = u.replace("/", ".");
        u = u.replace("ʹ", "");
        this.uri = u;
    }

    public String encode(String in) {
        return transliterator.transform(in);
    }
}
