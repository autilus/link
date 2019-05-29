package com.test.links.controller;

import com.fasterxml.jackson.annotation.JsonView;

import com.test.links.domain.Link;
import com.test.links.domain.User;
import com.test.links.domain.Views;
import com.test.links.repo.LinkRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("")
public class LinkController {
    private final LinkRepo linkRepo;

    @Autowired
    public LinkController(LinkRepo linkRepo) {
        this.linkRepo = linkRepo;
    }

    @GetMapping
    @JsonView(Views.IdName.class)
    public List<Link> list(
            @AuthenticationPrincipal User user
    ) {
        return linkRepo.findByUserId(user.getId());
    }



    @GetMapping("{id}")
    @JsonView(Views.FullMessage.class)

    public String getOne(@PathVariable("id") Link link) {
        Long i= Long.valueOf(1);
        i++;
        try {
            if (isUrlValid(link.getLink())){
                link.setQuantity(i);
                return "<script>window.location.replace('"+link.getLink()+"');</script>";
            }else {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Not Found");
            }
        }
        catch (NullPointerException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Not Found", e);
        }
    }
    private boolean isUrlValid(String url) {
        boolean valid = true;
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            valid = false;
        }
        return valid;
    }
    @PostMapping
    public Link create(@RequestBody Link link, @AuthenticationPrincipal User user) {
        link.setCreationDate(LocalDateTime.now());
        link.setUserId(user.getId());
        return linkRepo.save(link);
    }

    @PutMapping("{id}")
    public Link update(
            @PathVariable("id") Link linkFromDb,
            @RequestBody Link link
    ) {
        BeanUtils.copyProperties(link, linkFromDb, "id","userId", "creationDate");
        return linkRepo.save(linkFromDb);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Link link) {
        linkRepo.delete(link);
    }
}
