package com.crowd.mvc.controller;

import com.crowd.service.api.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author cjn
 * @create 2020-03-17 9:10
 */
@Controller
public class AuthController {

    @Autowired
    private AuthService authService;
}
