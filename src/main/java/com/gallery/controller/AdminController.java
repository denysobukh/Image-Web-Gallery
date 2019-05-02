package com.gallery.controller;

import com.gallery.model.Difference;
import com.gallery.model.Disk;
import com.gallery.model.DiskException;
import com.gallery.model.directory.Directory;
import com.gallery.model.directory.DirectoryRepository;
import com.gallery.model.image.ImageCrudRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * AdminController
 * <p>
 * 2019-03-22 15:11 [Friday]
 *
 * @author Dennis Obukhov
 */

@Controller
@RequestMapping(value = "/admin")
public final class AdminController {

    @Autowired
    private ImageCrudRepository imageCrudRepository;

    @Autowired
    private Disk storage;

    @Autowired
    private DirectoryRepository directoryRepository;

    @Autowired
    private Logger logger;


    @ModelAttribute
    public void setPreferences(Model model, HttpServletRequest request) {
        model.addAttribute("userName", request.getSession().getId());
        long size = imageCrudRepository.count();
        model.addAttribute("statusLine", String.format("Total images %d, including %d indexed.", size, 0));
    }

    @RequestMapping(value = "")
    public String index(Model model) {
        return "admin";
    }

    @RequestMapping(value = "/dirs")
    public String tree(Model model) {
        try {
            Set<Directory> disk = this.storage.getTreeAsList();
            Set<Directory> repo = directoryRepository.findAll();

            Difference<Directory> diff = new Difference<>(disk, repo);
            if (diff.addedCount() > 0) {
                directoryRepository.saveAll(diff.getAdded());
            }
            if (diff.removedCount() > 0) {
                directoryRepository.deleteAll(diff.getRemoved());
            }
            if (diff.addedCount() > 0 || diff.removedCount() > 0) {
                repo = directoryRepository.findAll();
            }

            // TODO: 2019-05-02 more elegant
            Optional<Directory> repoRoot =
                    repo.stream().filter(Directory::isRoot).reduce((a, b) -> null);

            model.addAttribute("treeRoot", repoRoot.get());

        } catch (DiskException e) {
            logger.warn("can not root tree", e);
        }
        return "admin";
    }
}
