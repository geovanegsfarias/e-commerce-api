package com.geovane.e_commerce_api.service;

import com.geovane.e_commerce_api.dto.request.CreateCartItemRequest;
import com.geovane.e_commerce_api.dto.response.CartItemResponse;
import com.geovane.e_commerce_api.exception.ForbiddenAccessException;
import com.geovane.e_commerce_api.exception.InsufficientStockException;
import com.geovane.e_commerce_api.exception.ResourceNotFoundException;
import com.geovane.e_commerce_api.mapper.CartItemMapper;
import com.geovane.e_commerce_api.model.Cart;
import com.geovane.e_commerce_api.model.CartItem;
import com.geovane.e_commerce_api.model.Product;
import com.geovane.e_commerce_api.repository.CartItemRepository;
import com.geovane.e_commerce_api.repository.CartRepository;
import com.geovane.e_commerce_api.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CartItemService(CartItemRepository cartItemRepository, CartRepository cartRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CartItemResponse save(CreateCartItemRequest request, String email) {
        Cart cart = cartRepository.findByUserEmail(email).orElseThrow(() -> new ResourceNotFoundException("Cart not found."));
        Product product = productRepository.findById(request.productId()).orElseThrow(() -> new ResourceNotFoundException("Product not found."));

        Optional<CartItem> cartItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        if (cartItemOpt.isPresent()) {
            CartItem cartItem = cartItemOpt.get();
            if (request.quantity() + cartItem.getQuantity() > product.getStock()) {
                throw new InsufficientStockException("Insufficient stock.");
            }
            cartItem.setQuantity(cartItem.getQuantity() + request.quantity());
            return CartItemMapper.toCartItemResponse(cartItemRepository.save(cartItem));
        }

        if (request.quantity() > product.getStock()) {
            throw new InsufficientStockException("Insufficient stock.");
        }

        return CartItemMapper.toCartItemResponse(cartItemRepository.save(CartItemMapper.toCartItem(cart, product, request.quantity())));
    }

    public void delete(Long id, String email) {
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cart item not found."));
        if (!cartItem.getCart().getUser().getEmail().equals(email)) {
            throw new ForbiddenAccessException("Access denied.");
        }
        cartItemRepository.delete(cartItem);
    }

}