package com.autoparts.repository;

import com.autoparts.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findByActiveTrue();

    Page<Product> findByActiveTrueAndNameContainingIgnoreCaseOrActiveTrueAndCategoryContainingIgnoreCase(
        String name, String category, Pageable pageable);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.active = true ORDER BY p.category")
    List<String> findDistinctCategories();

    @Query("SELECT p FROM Product p WHERE p.active = true " +
           "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))" +
           "AND (:category IS NULL OR p.category = :category)")
    Page<Product> searchProducts(@Param("search") String search,
                                  @Param("category") String category,
                                  Pageable pageable);

    boolean existsBySku(String sku);

    List<Product> findByActiveTrueAndStockQuantityLessThan(int threshold);
}
