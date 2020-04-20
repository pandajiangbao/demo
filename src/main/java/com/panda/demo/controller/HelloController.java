package com.panda.demo.controller;

import com.panda.demo.po.Panda;
import com.panda.demo.repository.PandaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Panda
 * @date 4/13/2020
 */
@CrossOrigin
@RestController
public class HelloController {

    @Resource
    PandaRepository pandaRepository;

    @PreAuthorize("hasRole('users')")
    @GetMapping(value = "Users",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Panda> helloWorld() {
        return pandaRepository.findAll();
    }
    @PreAuthorize("hasRole('Group1')")
    @RequestMapping("Group1")
    public String groupOne() {
        return "Hello Group 1 Users!";
    }
    @PreAuthorize("hasRole('Group2')")
    @RequestMapping("Group2")
    public String groupTwo() {
        return "Hello Group 2 Users!";
    }
}
