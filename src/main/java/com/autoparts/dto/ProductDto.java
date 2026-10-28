package com.autoparts.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 200, message = "Name must be 2-200 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 1000, message = "Description must be 5-1000 characters")
    private String description;

    @NotBlank(message = "SKU is required")
    @Pattern(regexp = "^[A-Z0-9\\-]{3,20}$", message = "SKU must be 3-20 uppercase alphanumeric characters or hyphens")
    private String sku;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Price cannot exceed 999999.99")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stockQuantity;

    @NotBlank(message = "Category is required")
    private String category;

    private String brand;
    private String imageUrl;
    private boolean active = true;
}
