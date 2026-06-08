package com.github.geovanegsfarias.cart;

import com.github.geovanegsfarias.exception.ForbiddenAccessException;
import com.github.geovanegsfarias.exception.InsufficientStockException;
import com.github.geovanegsfarias.exception.ResourceNotFoundException;
import com.github.geovanegsfarias.product.Product;
import com.github.geovanegsfarias.product.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final ProductService productService;

    @Autowired
    public CartItemService(CartItemRepository cartItemRepository, CartService cartService, ProductService productService) {
        this.cartItemRepository = cartItemRepository;
        this.cartService = cartService;
        this.productService = productService;
    }

    public CartItem findByIdOrThrowException(Long id) {
        return cartItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
    }

    @Transactional
    public CartItem save(CartItem cartItemToSave, Long productId, String userEmail) {
        var cart = cartService.findByEmailOrThrowException(userEmail);

        var product = productService.findByIdOrThrowException(productId);

        return cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .map(cartItem -> addQuantityToExistingItem(cartItem, cartItemToSave.getQuantity(), product))
                .orElseGet(() -> createCartItem(cart, product, cartItemToSave.getQuantity()));
    }

    public void delete(Long id, String userEmail) {
        var cartItem = findByIdOrThrowException(id);

        assertCartItemBelongsToUser(cartItem, userEmail);

        cartItemRepository.delete(cartItem);
    }

    private CartItem addQuantityToExistingItem(CartItem cartItem, int quantityToAdd, Product product) {
        var newQuantity = cartItem.getQuantity() + quantityToAdd;

        assertStockIsAvailable(product, newQuantity);

        cartItem.setQuantity(newQuantity);

        return cartItemRepository.save(cartItem);
    }

    private CartItem createCartItem(Cart cart, Product product, int quantity) {
        assertStockIsAvailable(product, quantity);

        var cartItem = new CartItem(cart, product, quantity);

        return cartItemRepository.save(cartItem);
    }

    private void assertStockIsAvailable(Product product, int requestedQuantity) {
        if (requestedQuantity > product.getStock()) {
            throw new InsufficientStockException("Insufficient stock");
        }
    }

    private void assertCartItemBelongsToUser(CartItem cartItem, String userEmail) {
        var cartOwnerEmail = cartItem.getCart().getUser().getEmail();

        if (!cartOwnerEmail.equals(userEmail)) {
            throw new ForbiddenAccessException("Access denied");
        }
    }

}