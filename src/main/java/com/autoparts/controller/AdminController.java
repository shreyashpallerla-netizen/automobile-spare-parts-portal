package com.autoparts.controller;

import com.autoparts.dto.ProductDto;
import com.autoparts.entity.Order;
import com.autoparts.entity.Product;
import com.autoparts.entity.User;
import com.autoparts.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping
    public String dashboard(Model model) {
        List<Product> products = productService.getAllActiveProducts();
        List<User> users = userService.getAllUsers();
        List<Order> orders = orderService.getAllOrders();
        List<Product> lowStock = productService.getLowStockProducts(5);

        model.addAttribute("productCount", products.size());
        model.addAttribute("userCount", users.size());
        model.addAttribute("orderCount", orders.size());
        model.addAttribute("lowStockProducts", lowStock);
        model.addAttribute("recentOrders", orders.stream().limit(5).toList());
        return "admin/dashboard";
    }

    // ---- Product Management ----

    @GetMapping("/products")
    public String manageProducts(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Product> products = productService.searchProducts(null, null,
            PageRequest.of(page, 15, Sort.by("createdAt").descending()));
        model.addAttribute("products", products);
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("productDto", new ProductDto());
        model.addAttribute("categories", productService.getAllCategories());
        return "admin/product-form";
    }

    @PostMapping("/products/new")
    public String createProduct(@Valid @ModelAttribute("productDto") ProductDto dto,
                                BindingResult result,
                                RedirectAttributes redirectAttrs,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", productService.getAllCategories());
            return "admin/product-form";
        }
        try {
            Product p = productService.createProduct(dto);
            redirectAttrs.addFlashAttribute("successMsg", "Product '" + p.getName() + "' created successfully.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        ProductDto dto = ProductDto.builder()
            .name(product.getName())
            .description(product.getDescription())
            .sku(product.getSku())
            .price(product.getPrice())
            .stockQuantity(product.getStockQuantity())
            .category(product.getCategory())
            .brand(product.getBrand())
            .imageUrl(product.getImageUrl())
            .active(product.isActive())
            .build();
        model.addAttribute("productDto", dto);
        model.addAttribute("productId", id);
        model.addAttribute("categories", productService.getAllCategories());
        return "admin/product-form";
    }

    @PostMapping("/products/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("productDto") ProductDto dto,
                                BindingResult result,
                                RedirectAttributes redirectAttrs,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("productId", id);
            model.addAttribute("categories", productService.getAllCategories());
            return "admin/product-form";
        }
        try {
            Product p = productService.updateProduct(id, dto);
            redirectAttrs.addFlashAttribute("successMsg", "Product '" + p.getName() + "' updated successfully.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            productService.deleteProduct(id);
            redirectAttrs.addFlashAttribute("successMsg", "Product deactivated successfully.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // ---- User Management ----

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            userService.toggleUserStatus(id);
            redirectAttrs.addFlashAttribute("successMsg", "User status updated.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ---- Order Management ----

    @GetMapping("/orders")
    public String manageOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam Order.OrderStatus status,
                                    RedirectAttributes redirectAttrs) {
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttrs.addFlashAttribute("successMsg", "Order #" + id + " status updated to " + status);
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/orders";
    }
}
