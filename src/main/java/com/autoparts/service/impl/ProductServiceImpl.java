package com.autoparts.service.impl;

import com.autoparts.dto.ProductDto;
import com.autoparts.entity.Product;
import com.autoparts.exception.ResourceNotFoundException;
import com.autoparts.exception.StockException;
import com.autoparts.repository.ProductRepository;
import com.autoparts.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String search, String category, Pageable pageable) {
        String searchParam = (search == null || search.isBlank()) ? null : search.trim();
        String categoryParam = (category == null || category.isBlank()) ? null : category.trim();
        return productRepository.searchProducts(searchParam, categoryParam, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
    }

    @Override
    public Product createProduct(ProductDto dto) {
        if (productRepository.existsBySku(dto.getSku())) {
            throw new IllegalArgumentException("Product with SKU " + dto.getSku() + " already exists");
        }
        Product product = mapDtoToProduct(new Product(), dto);
        Product saved = productRepository.save(product);
        log.info("Created product: {} (SKU: {})", saved.getName(), saved.getSku());
        return saved;
    }

    @Override
    public Product updateProduct(Long id, ProductDto dto) {
        Product product = getProductById(id);
        if (!product.getSku().equals(dto.getSku()) && productRepository.existsBySku(dto.getSku())) {
            throw new IllegalArgumentException("Another product with SKU " + dto.getSku() + " already exists");
        }
        mapDtoToProduct(product, dto);
        Product updated = productRepository.save(product);
        log.info("Updated product: {} (id: {})", updated.getName(), updated.getId());
        return updated;
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        product.setActive(false);
        productRepository.save(product);
        log.info("Soft-deleted product id: {}", id);
    }

    @Override
    public void adjustStock(Long productId, int quantity) {
        Product product = getProductById(productId);
        int newQty = product.getStockQuantity() + quantity;
        if (newQty < 0) {
            throw new StockException("Insufficient stock for product: " + product.getName() +
                ". Available: " + product.getStockQuantity() + ", Requested: " + Math.abs(quantity));
        }
        product.setStockQuantity(newQty);
        productRepository.save(product);
        log.info("Stock adjusted for {}: {} -> {}", product.getName(), product.getStockQuantity() - quantity, newQty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return productRepository.findDistinctCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findByActiveTrueAndStockQuantityLessThan(threshold);
    }

    private Product mapDtoToProduct(Product product, ProductDto dto) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setSku(dto.getSku());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setCategory(dto.getCategory());
        product.setBrand(dto.getBrand());
        if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
            product.setImageUrl(dto.getImageUrl());
        } else if (product.getImageUrl() == null || product.getImageUrl().isBlank()) {
            product.setImageUrl("/images/products/default-part.svg");
        }
        product.setActive(dto.isActive());
        return product;
    }
}
