package com.gallery.service;


import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Previewer class
 *
 * @author Dennis Obukhov
 * @date 2019-04-14 12:08 [Sunday]
 */
@Component
public class Previewer {

    private static final String PATH_SEPARATOR = FileSystems.getDefault().getSeparator();
    @Value("${gallery.storage.images-directory}")
    private String imagesDirectory;
    @Value("${gallery.storage.thumbnails-directory}")
    private String thumbnailsDirectory;
    @Autowired
    private Logger logger;

    private void resize(File inFile, File outFile, int boundWidth, int boundHeight, boolean expand) {
        try {
            ImageIcon inImage = new ImageIcon(inFile.getAbsolutePath().toString());
            ImageIcon outputImage = null;

            int originalWidth = inImage.getImage().getWidth(null);
            int originalHeight = inImage.getImage().getHeight(null);
            double originalRatio = originalWidth / (double) originalHeight;
            double scaledRatio = boundWidth / (double) boundHeight;

            if (expand || originalWidth > boundWidth || originalHeight > boundHeight) {
                if (originalHeight - (Math.abs(originalWidth - boundWidth) / originalRatio) <= boundHeight) {
                    outputImage = new ImageIcon(inImage.getImage().getScaledInstance(boundWidth, -1, java.awt.Image.SCALE_SMOOTH));
                } else if (originalWidth - (Math.abs(originalHeight - boundHeight) * originalRatio) <= boundWidth) {
                    outputImage = new ImageIcon(inImage.getImage().getScaledInstance(-1, boundHeight, java.awt.Image.SCALE_SMOOTH));
                }
            } else {

            }

            BufferedImage image = new BufferedImage(outputImage.getIconWidth(), outputImage.getIconHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            g.drawImage(outputImage.getImage(), 0, 0, null);

            ImageIO.write(image, "jpg", outFile);
        } catch (IOException ioe) {
            System.out.println("Error occurred saving scaled image");
        }

        /*
            int originalWidth = imgSize.width;
    int originalHeight = imgSize.height;
    int boundWidth = boundary.width;
    int bound_height = boundary.height;
    int new_width = originalWidth;
    int new_height = originalHeight;

    // first check if we need to resize width
    if (originalWidth > boundWidth) {
        //resize width to fit
        new_width = boundWidth;
        //resize height to maintain aspect ratio
        new_height = (new_width * originalHeight) / originalWidth;
    }

    // then check if we need to resize even with the new height
    if (new_height > bound_height) {
        //resize height to fit instead
        new_height = bound_height;
        //resize width to maintain aspect ratio
        new_width = (new_height * originalWidth) / originalHeight;
    }
         */
    }

    String md5(String value) {
        String myHash;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes());
            byte[] digest = md.digest();
            myHash = DatatypeConverter.printHexBinary(digest).toLowerCase();

        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
        return myHash;
    }

    private String getAbsolutePath(String source) {
        return imagesDirectory + PATH_SEPARATOR + source;
    }


    public byte[] getBytes(String source) throws IOException {
        Path path = Paths.get(getAbsolutePath(source));
        return Files.readAllBytes(path);
    }

    public String getMime(String source) throws IOException {
        Path path = Paths.get(imagesDirectory, PATH_SEPARATOR, source);
        return Files.probeContentType(path);
    }

    public void writePreview(String source, OutputStream outputStream) throws IOException {
        Thumbnails.of(getAbsolutePath(source)).height(256).toOutputStream(outputStream);
    }

}