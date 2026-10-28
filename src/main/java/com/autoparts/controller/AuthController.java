package com.autoparts.controller;

import com.autoparts.dto.RegisterDto;
import com.autoparts.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMsg", "Invalid username or password.");
        }
        if (logout != null) {
            model.addAttribute("logoutMsg", "You have been logged out successfully.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerDto") RegisterDto dto,
                               BindingResult result,
                               RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
            return "auth/register";
        }
        try {
            userService.registerUser(dto);
            redirectAttrs.addFlashAttribute("successMsg", "Registration successful! Please login.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            result.rejectValue("username", "error.username", e.getMessage());
            return "auth/register";
        }
    }
}
