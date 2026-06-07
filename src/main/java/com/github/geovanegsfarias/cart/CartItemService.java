package com.github.geovanegsfarias.cart;

import com.github.geovanegsfarias.exception.ForbiddenAccessException;
import com.github.geovanegsfarias.exception.InsufficientStockException;
import com.github.geovanegsfarias.exception.ResourceNotFoundException;
import com.github.geovanegsfarias.product.Product;
import com.github.geovanegsfarias.product.ProductRepository;
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