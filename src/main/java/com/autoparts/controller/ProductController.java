package com.autoparts.controller;

import com.autoparts.dto.ProductDto;
import com.autoparts.entity.Product;
import com.autoparts.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String listProducts(@RequestParam(required = false) String search,
                               @RequestParam(required = false) String category,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "12") int size,
                               @RequestParam(defaultValue = "name") String sort,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Product> products = productService.searchProducts(search, category, pageable);
        List<String> categories = productService.getAllCategories();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("search", search);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);
        return "product/list";
    }

    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product/detail";
    }
}
