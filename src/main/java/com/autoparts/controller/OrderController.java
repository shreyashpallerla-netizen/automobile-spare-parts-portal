package com.autoparts.controller;

import com.autoparts.dto.OrderRequest;
import com.autoparts.entity.Order;
import com.autoparts.entity.User;
import com.autoparts.service.OrderService;
import com.autoparts.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    private User getCurrentUser(Authentication auth) {
        return userService.getUserByUsername(auth.getName());
    }

    @GetMapping
    public String listOrders(Authentication auth, Model model) {
        User user = getCurrentUser(auth);
        List<Order> orders = orderService.getUserOrders(user);
        model.addAttribute("orders", orders);
        return "order/list";
    }

    @GetMapping("/checkout")
    public String checkoutPage(Model model) {
        model.addAttribute("orderRequest", new OrderRequest());
        return "order/checkout";
    }

    @PostMapping("/place")
    public String placeOrder(@Valid @ModelAttribute("orderRequest") OrderRequest request,
                             BindingResult result,
                             Authentication auth,
                             RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            return "order/checkout";
        }
        try {
            User user = getCurrentUser(auth);
            Order order = orderService.createOrder(user, request);
            redirectAttrs.addFlashAttribute("successMsg",
                "Order #" + order.getId() + " placed successfully! Total: ₹" + order.getTotalAmount());
            return "redirect:/orders";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Authentication auth, Model model) {
        User user = getCurrentUser(auth);
        Order order = orderService.getOrderById(id);
        // Security check — user can only see their own orders unless admin
        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!order.getUser().getId().equals(user.getId()) && !isAdmin) {
            return "redirect:/orders";
        }
        model.addAttribute("order", order);
        return "order/detail";
    }
}
