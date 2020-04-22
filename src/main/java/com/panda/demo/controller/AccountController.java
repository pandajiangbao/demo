package com.panda.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Panda
 * @date 4/19/2020
 */
@RestController
public class AccountController {
    @GetMapping("/account")
    public Authentication getAccount() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
