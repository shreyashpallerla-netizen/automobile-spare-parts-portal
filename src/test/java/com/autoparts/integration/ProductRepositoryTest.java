package com.autoparts.integration;

import com.autoparts.entity.Product;
import com.autoparts.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProductRepository Integration Tests")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        Product p1 = Product.builder()
            .name("Bosch Spark Plug").description("Iridium spark plug for all cars")
            .sku("BSP-001").price(new BigDecimal("299.99")).stockQuantity(50)
            .category("Engine").brand("Bosch").active(true).build();

        Product p2 = Product.builder()
            .name("Denso Air Filter").description("High-flow air filter for engine protection")
            .sku("DAF-002").price(new BigDecimal("450.00")).stockQuantity(3)
            .category("Filters").brand("Denso").active(true).build();

        Product p3 = Product.builder()
            .name("MRF Tyre 185/65R15").description("Passenger car radial tyre")
            .sku("MRF-003").price(new BigDecimal("3500.00")).stockQuantity(0)
            .category("Tyres").brand("MRF").active(true).build();

        productRepository.saveAll(List.of(p1, p2, p3));
    }

    @Test
    @DisplayName("Should find product by SKU")
    void testFindBySku() {
        Optional<Product> result = productRepository.findBySku("BSP-001");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Bosch Spark Plug");
    }

    @Test
    @DisplayName("Should return all active products")
    void testFindByActiveTrue() {
        List<Product> active = productRepository.findByActiveTrue();
        assertThat(active).hasSize(3);
    }

    @Test
    @DisplayName("Should find products with stock below threshold")
    void testFindLowStockProducts() {
        List<Product> lowStock = productRepository.findByActiveTrueAndStockQuantityLessThan(5);
        assertThat(lowStock).hasSize(2); // stockQty 3 and 0
    }

    @Test
    @DisplayName("Should check SKU existence")
    void testExistsBySku() {
        assertThat(productRepository.existsBySku("BSP-001")).isTrue();
        assertThat(productRepository.existsBySku("NOTEXIST")).isFalse();
    }

    @Test
    @DisplayName("Should return distinct categories")
    void testFindDistinctCategories() {
        List<String> cats = productRepository.findDistinctCategories();
        assertThat(cats).containsExactlyInAnyOrder("Engine", "Filters", "Tyres");
    }
}
