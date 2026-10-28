package com.autoparts.service.impl;

import com.autoparts.entity.CartItem;
import com.autoparts.entity.Product;
import com.autoparts.entity.User;
import com.autoparts.exception.ResourceNotFoundException;
import com.autoparts.exception.StockException;
import com.autoparts.repository.CartItemRepository;
import com.autoparts.repository.ProductRepository;
import com.autoparts.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    public CartItem addToCart(User user, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        if (!product.isActive()) {
            throw new IllegalArgumentException("Product is not available: " + product.getName());
        }

        if (product.getStockQuantity() < quantity) {
            throw new StockException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product)
            .orElse(CartItem.builder().user(user).product(product).quantity(0).build());

        int newQuantity = cartItem.getQuantity() + quantity;
        if (product.getStockQuantity() < newQuantity) {
            throw new StockException("Insufficient stock. Available: " + product.getStockQuantity() +
                ", In cart: " + cartItem.getQuantity() + ", Adding: " + quantity);
        }

        cartItem.setQuantity(newQuantity);
        return cartItemRepository.save(cartItem);
    }

    @Override
    public void removeFromCart(User user, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + cartItemId));
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access denied to cart item: " + cartItemId);
        }
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void updateCartItemQuantity(User user, Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + cartItemId));
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access denied to cart item: " + cartItemId);
        }
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return;
        }
        if (cartItem.getProduct().getStockQuantity() < quantity) {
            throw new StockException("Insufficient stock. Available: " + cartItem.getProduct().getStockQuantity());
        }
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(User user) {
        return cartItemRepository.findByUser(user).stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public long getCartCount(User user) {
        return cartItemRepository.countByUser(user);
    }

    @Override
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }
}
