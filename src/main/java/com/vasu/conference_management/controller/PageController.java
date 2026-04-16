package com.vasu.conference_management.controller;

import com.vasu.conference_management.dto.RegisterRequest;
import com.vasu.conference_management.entity.User;
import com.vasu.conference_management.entity.UserProfile;
import com.vasu.conference_management.service.AuthService;
import com.vasu.conference_management.service.ConferenceService;
import com.vasu.conference_management.service.DashboardService;
import com.vasu.conference_management.service.PaperService;
import com.vasu.conference_management.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
public class PageController {

    private final UserService userService;
    private final AuthService authService;
    private final DashboardService dashboardService;
    private final ConferenceService conferenceService;
    private final PaperService paperService;

    public PageController(UserService userService,
                          AuthService authService,
                          DashboardService dashboardService,
                          ConferenceService conferenceService,
                          PaperService paperService) {
        this.userService = userService;
        this.authService = authService;
        this.dashboardService = dashboardService;
        this.conferenceService = conferenceService;
        this.paperService = paperService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String registered,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out");
        }
        if (registered != null) {
            model.addAttribute("message", "Registration successful. Please log in.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request, Model model) {
        if (request.getPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("registerRequest", request);
            return "register";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(request.getPassword());
        user.setEnabled(true);

        try {
            authService.encodePassword(user);
            userService.register(user, Set.of("AUTHOR"));
            return "redirect:/login?registered=true";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("registerRequest", request);
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", dashboardService.getDashboardStats());
        return "dashboard/index";
    }

    @GetMapping("/conferences")
    public String conferenceList(Model model) {
        model.addAttribute("conferences", conferenceService.findAll());
        return "conference/list";
    }

    @GetMapping("/papers")
    public String paperList(Model model) {
        model.addAttribute("papers", paperService.findAll());
        return "paper/list";
    }

    @GetMapping("/user/profile")
    public String profile(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(authentication.getName());
        UserProfile profile = userService.findProfileByUserId(user.getId()).orElseGet(UserProfile::new);

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        return "user/profile";
    }
}

