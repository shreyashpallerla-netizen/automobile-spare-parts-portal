package com.autoparts.service;

import com.autoparts.entity.CartItem;
import com.autoparts.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    CartItem addToCart(User user, Long productId, int quantity);
    void removeFromCart(User user, Long cartItemId);
    void updateCartItemQuantity(User user, Long cartItemId, int quantity);
    List<CartItem> getCartItems(User user);
    BigDecimal getCartTotal(User user);
    long getCartCount(User user);
    void clearCart(User user);
}
