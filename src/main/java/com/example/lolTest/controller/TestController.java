package com.example.lolTest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController
{
    @GetMapping("/main")
    public String main()
    {
        return "layout/main";
    }
}
