package com.autoparts.service;

import com.autoparts.dto.ProductDto;
import com.autoparts.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Page<Product> searchProducts(String search, String category, Pageable pageable);
    List<Product> getAllActiveProducts();
    Product getProductById(Long id);
    Product getProductBySku(String sku);
    Product createProduct(ProductDto dto);
    Product updateProduct(Long id, ProductDto dto);
    void deleteProduct(Long id);
    void adjustStock(Long productId, int quantity);
    List<String> getAllCategories();
    List<Product> getLowStockProducts(int threshold);
}
