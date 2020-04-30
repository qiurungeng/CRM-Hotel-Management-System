package com.neu.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ClassifyController {
    @GetMapping("classify")
    public String classify(){

        return "classification";
    }
}
