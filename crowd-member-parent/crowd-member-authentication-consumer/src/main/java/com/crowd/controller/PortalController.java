package com.crowd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author cjn
 * @create 2020-03-19 20:34
 */
@Controller
public class PortalController {
    @RequestMapping("/")
    public String showPortalPage() {
        // 需要加载数据
        return "portal";
    }
}
