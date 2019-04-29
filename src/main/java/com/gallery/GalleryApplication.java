package com.gallery;

import com.gallery.model.file.ImageFile;
import com.gallery.model.file.FileRepository;
import com.gallery.model.user.User;
import com.gallery.model.user.UserPreferences;
import com.gallery.model.user.UserPreferencesRepository;
import com.gallery.model.user.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import java.nio.file.Paths;


@SpringBootApplication
public class GalleryApplication {
    @Autowired
    @Lazy
    private Logger logger;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    @Autowired
    private ApplicationContext context;

    private static boolean console;

    public static void main(String[] args) {

        console = args.length > 0 && args[0].equals("--console");

        if (console) {
            SpringApplication.run(GalleryApplication.class, args).close();
        } else {
            SpringApplication.run(GalleryApplication.class, args);
        }
    }

    @Bean
    @Lazy
    CommandLineRunner commandLineRunner() {
        return args -> {
            if (console) {
                fileRepository.deleteAll();
                userRepository.deleteAll();

                UserPreferences userPreferences = context.getBean(UserPreferences.class);
                userPreferences.setCurrentDir(Paths.get("/tmp"));

                User user = new User.UserBuilder()
                        .setName("Tester")
                        .setType(User.Type.ADMIN)
                        .setUserPreferences(userPreferences)
                        .build();

                System.out.println(user);

                userRepository.save(user);

                for (ImageFile f : fileRepository.findAll()) {
                    System.out.println(f);
                }

                for (User u : userRepository.findAll()) {
                    System.out.println(u);
                }
            }
        };
    }
}