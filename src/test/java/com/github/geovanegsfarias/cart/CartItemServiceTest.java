package com.github.geovanegsfarias.cart;

import com.github.geovanegsfarias.commons.CartUtils;
import com.github.geovanegsfarias.commons.CategoryUtils;
import com.github.geovanegsfarias.commons.ProductUtils;
import com.github.geovanegsfarias.commons.UserUtils;
import com.github.geovanegsfarias.exception.ForbiddenAccessException;
import com.github.geovanegsfarias.exception.InsufficientStockException;
import com.github.geovanegsfarias.exception.ResourceNotFoundException;
import com.github.geovanegsfarias.product.ProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartItemServiceTest {
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartService cartService;
    @Mock
    private ProductService productService;
    @InjectMocks
    private CartItemService cartItemService;
    private CartUtils cartUtils;
    private ProductUtils productUtils;
    private UserUtils userUtils;

    @BeforeEach
    void init() {
        cartUtils = new CartUtils(new UserUtils(), new ProductUtils(new CategoryUtils()));
        productUtils = new ProductUtils(new CategoryUtils());
        userUtils = new UserUtils();
    }

    @Test
    @DisplayName("findById returns a cart item with given id")
    @Order(1)
    void findById_ReturnsCartItem_WhenSuccessful() {
        var expectedCartItem = cartUtils.savedCartItem();

        BDDMockito.when(cartItemRepository.findById(expectedCartItem.getId())).thenReturn(Optional.of(expectedCartItem));

        var cartItem = cartItemService.findByIdOrThrowException(expectedCartItem.getId());

        Assertions.assertThat(expectedCartItem).isEqualTo(cartItem);
    }

    @Test
    @DisplayName("findById throws ResourceNotFoundException when cart item is not found")
    @Order(2)
    void findById_ThrowsResourceNotFoundException_WhenCartItemNotFound() {
        var savedCartItem = cartUtils.savedCartItem();

        BDDMockito.when(cartItemRepository.findById(savedCartItem.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> cartItemService.findByIdOrThrowException(savedCartItem.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Cart item not found");
    }

    @Test
    @DisplayName("save creates a cart item when product is not in cart")
    @Order(3)
    void save_CreatesCartItem_WhenProductNotInCart() {
        var cartItemToSave = cartUtils.newCartItemToSave();
        var cart = cartUtils.savedCart();
        var product = productUtils.savedProduct();
        var user = userUtils.savedUser();

        var expectedSavedCartItem = cartUtils.savedCartItem();

        BDDMockito.when(cartService.findByEmailOrThrowException(user.getEmail())).thenReturn(cart);
        BDDMockito.when(productService.findByIdOrThrowException(product.getId())).thenReturn(product);
        BDDMockito.when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())).thenReturn(Optional.empty());
        BDDMockito.when(cartItemRepository.save(BDDMockito.any(CartItem.class))).thenReturn(expectedSavedCartItem);

        var savedCartItem = cartItemService.save(cartItemToSave, product.getId(), user.getEmail());

        Assertions.assertThat(savedCartItem).isEqualTo(expectedSavedCartItem);
    }

    @Test
    @DisplayName("save throws ResourceNotFoundException when cart is not found")
    @Order(4)
    void save_ThrowsResourceNotFoundException_WhenCartNotFound() {
        var cartItemToSave = cartUtils.newCartItemToSave();
        var product = productUtils.savedProduct();
        var user = userUtils.savedUser();

        BDDMockito.given(cartService.findByEmailOrThrowException(user.getEmail())).willThrow(new ResourceNotFoundException("Cart not found"));

        Assertions.assertThatException()
                .isThrownBy(() -> cartItemService.save(cartItemToSave, product.getId(), user.getEmail()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Cart not found");
    }

    @Test
    @DisplayName("save throws ResourceNotFoundException when product is not found")
    @Order(5)
    void save_ThrowsResourceNotFoundException_WhenProductNotFound() {
        var cartItemToSave = cartUtils.newCartItemToSave();
        var cart = cartUtils.savedCart();
        var product = productUtils.savedProduct();
        var user = userUtils.savedUser();

        BDDMockito.when(cartService.findByEmailOrThrowException(user.getEmail())).thenReturn(cart);
        BDDMockito.given(productService.findByIdOrThrowException(product.getId())).willThrow(new ResourceNotFoundException("Product not found"));

        Assertions.assertThatException()
                .isThrownBy(() -> cartItemService.save(cartItemToSave, product.getId(), user.getEmail()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Product not found");
    }

    @Test
    @DisplayName("save throws InsufficientStockException when quantity exceeds stock")
    @Order(6)
    void save_ThrowsInsufficientStockException_WhenQuantityExceedsStock() {
        var cartItemToSave = cartUtils.newCartItemToSave();
        cartItemToSave.setQuantity(30);
        var cart = cartUtils.savedCart();
        var product = productUtils.savedProduct();
        var user = userUtils.savedUser();

        BDDMockito.when(cartService.findByEmailOrThrowException(user.getEmail())).thenReturn(cart);
        BDDMockito.when(productService.findByIdOrThrowException(product.getId())).thenReturn(product);
        BDDMockito.when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> cartItemService.save(cartItemToSave, product.getId(), user.getEmail()))
                .isInstanceOf(InsufficientStockException.class)
                .withMessage("Insufficient stock");
    }

    @Test
    @DisplayName("save adds quantity to existing cart item when product already in cart")
    @Order(7)
    void save_AddsQuantityToExistingCartItem_WhenProductAlreadyInCart() {
        var cartItemToSave = cartUtils.newCartItemToSave();
        var existingCartItem = cartUtils.savedCartItem();
        var expectedQuantity = cartItemToSave.getQuantity() + existingCartItem.getQuantity();

        var cart = cartUtils.savedCart();
        var product = productUtils.savedProduct();
        var user = userUtils.savedUser();

        BDDMockito.when(cartService.findByEmailOrThrowException(user.getEmail())).thenReturn(cart);
        BDDMockito.when(productService.findByIdOrThrowException(product.getId())).thenReturn(product);
        BDDMockito.when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())).thenReturn(Optional.of(existingCartItem));
        BDDMockito.when(cartItemRepository.save(existingCartItem)).thenReturn(existingCartItem);

        var savedCartItem = cartItemService.save(cartItemToSave, product.getId(), user.getEmail());

        Assertions.assertThat(savedCartItem.getQuantity()).isEqualTo(expectedQuantity);
    }

    @Test
    @DisplayName("save throws InsufficientStockException when new quantity exceeds stock")
    @Order(8)
    void save_ThrowsInsufficientStockException_WhenNewQuantityExceedsStock() {
        var cartItemToSave = cartUtils.newCartItemToSave();
        cartItemToSave.setQuantity(25);
        var existingCartItem = cartUtils.savedCartItem();

        var cart = cartUtils.savedCart();
        var product = productUtils.savedProduct();
        var user = userUtils.savedUser();

        BDDMockito.when(cartService.findByEmailOrThrowException(user.getEmail())).thenReturn(cart);
        BDDMockito.when(productService.findByIdOrThrowException(product.getId())).thenReturn(product);
        BDDMockito.when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())).thenReturn(Optional.of(existingCartItem));

        Assertions.assertThatException()
                .isThrownBy(() -> cartItemService.save(cartItemToSave, product.getId(), user.getEmail()))
                .isInstanceOf(InsufficientStockException.class)
                .withMessage("Insufficient stock");
    }

    @Test
    @DisplayName("delete removes a cart item")
    @Order(9)
    void delete_RemovesCartItem_WhenSuccessful() {
        var cartItemToDelete = cartUtils.savedCartItem();
        var user = cartItemToDelete.getCart().getUser();

        BDDMockito.when(cartItemRepository.findById(cartItemToDelete.getId())).thenReturn(Optional.of(cartItemToDelete));

        Assertions.assertThatNoException().isThrownBy(() -> cartItemService.delete(cartItemToDelete.getId(), user.getEmail()));
        BDDMockito.verify(cartItemRepository).delete(cartItemToDelete);
    }

    @Test
    @DisplayName("delete throws ResourceNotFoundException when cart item is not found")
    @Order(10)
    void delete_ThrowsResourceNotFoundException_WhenCartItemNotFound() {
        var cartItemToDelete = cartUtils.savedCartItem();
        var user = cartItemToDelete.getCart().getUser();

        BDDMockito.when(cartItemRepository.findById(cartItemToDelete.getId())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> cartItemService.delete(cartItemToDelete.getId(), user.getEmail()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Cart item not found");
    }

    @Test
    @DisplayName("delete throws ForbiddenAccessException when cart item does not belong to user")
    @Order(11)
    void delete_ThrowsForbiddenAccessException_WhenCartItemDoesNotBelongToUser() {
        var cartItemToDelete = cartUtils.savedCartItem();
        var wrongEmail = "wrong@gmail.com";

        BDDMockito.when(cartItemRepository.findById(cartItemToDelete.getId())).thenReturn(Optional.of(cartItemToDelete));

        Assertions.assertThatException()
                .isThrownBy(() -> cartItemService.delete(cartItemToDelete.getId(), wrongEmail))
                .isInstanceOf(ForbiddenAccessException.class)
                .withMessage("Access denied");
    }
}