package com.panda.demo.controller;

import com.panda.demo.repository.PandaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Panda
 * @date 4/13/2020
 */
@RestController
@RequestMapping("hello")
public class HelloController {

    @GetMapping(value = "a")
    public List<String> helloWorld() {
        return  SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(e->((GrantedAuthority) e).getAuthority()).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('Writer')")
    @RequestMapping("b")
    public String groupOne() {
        return "Hello Writer!";
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping("c")
    public String groupTwo() {
        return "Hello Users!";
    }
}
