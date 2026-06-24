package com.sri_web.sri_web.controller;

import org.springframework.ui.Model;

public abstract class BaseWebController {

    protected void page(Model model, String active, String title, String subtitle) {
        model.addAttribute("active", active);
        model.addAttribute("pageTitle", title);
        model.addAttribute("pageSubtitle", subtitle);
    }
}