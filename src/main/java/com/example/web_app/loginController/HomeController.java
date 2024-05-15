package com.example.web_app.loginController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping({ "/home", "/" })
    public String getHome() {
        return "home/home";
    }

    @GetMapping("/about")
    public String getAbout() {
        return "home/about";
    }

    @GetMapping("/contact")
    public String getContact() {
        return "home/contact";
    }

    @GetMapping("/admin")
    public String getAdmin() {
        return "Pages/admin";
    }
}
