package com.autoparts.unit;

import com.autoparts.dto.ProductDto;
import com.autoparts.entity.Product;
import com.autoparts.exception.ResourceNotFoundException;
import com.autoparts.exception.StockException;
import com.autoparts.repository.ProductRepository;
import com.autoparts.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = Product.builder()
            .id(1L)
            .name("Bosch Spark Plug")
            .description("High performance iridium spark plug")
            .sku("BSP-001")
            .price(new BigDecimal("299.99"))
            .stockQuantity(50)
            .category("Engine")
            .brand("Bosch")
            .active(true)
            .build();
    }

    @Test
    @DisplayName("Should return product when found by ID")
    void testGetProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Bosch Spark Plug");
        assertThat(result.getSku()).isEqualTo("BSP-001");
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product not found")
    void testGetProductById_NotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should create product successfully")
    void testCreateProduct_Success() {
        ProductDto dto = ProductDto.builder()
            .name("Test Filter").description("Air filter for cars")
            .sku("AF-001").price(new BigDecimal("150.00"))
            .stockQuantity(30).category("Filters").active(true).build();

        when(productRepository.existsBySku("AF-001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        Product result = productService.createProduct(dto);

        assertThat(result).isNotNull();
        verify(productRepository).existsBySku("AF-001");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw when creating product with duplicate SKU")
    void testCreateProduct_DuplicateSku() {
        ProductDto dto = ProductDto.builder()
            .name("Duplicate").description("Duplicate product")
            .sku("BSP-001").price(new BigDecimal("100")).stockQuantity(10).category("Engine").active(true).build();

        when(productRepository.existsBySku("BSP-001")).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("BSP-001");
    }

    @Test
    @DisplayName("Should adjust stock correctly")
    void testAdjustStock_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        productService.adjustStock(1L, -10);

        assertThat(sampleProduct.getStockQuantity()).isEqualTo(40);
        verify(productRepository).save(sampleProduct);
    }

    @Test
    @DisplayName("Should throw StockException when adjusting stock below zero")
    void testAdjustStock_BelowZero() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        assertThatThrownBy(() -> productService.adjustStock(1L, -100))
            .isInstanceOf(StockException.class)
            .hasMessageContaining("Insufficient stock");
    }

    @Test
    @DisplayName("Should soft-delete product by setting active=false")
    void testDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        productService.deleteProduct(1L);

        assertThat(sampleProduct.isActive()).isFalse();
        verify(productRepository).save(sampleProduct);
    }

    @Test
    @DisplayName("Should return active products list")
    void testGetAllActiveProducts() {
        when(productRepository.findByActiveTrue()).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.getAllActiveProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();
    }

    @Test
    @DisplayName("Should return low stock products below threshold")
    void testGetLowStockProducts() {
        Product lowStockProduct = Product.builder()
            .id(2L).name("Low Stock Item").sku("LS-001")
            .stockQuantity(2).active(true).build();
        when(productRepository.findByActiveTrueAndStockQuantityLessThan(5)).thenReturn(List.of(lowStockProduct));

        List<Product> result = productService.getLowStockProducts(5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStockQuantity()).isLessThan(5);
    }
}
