package com.github.geovanegsfarias.cart;

import com.github.geovanegsfarias.commons.CartUtils;
import com.github.geovanegsfarias.commons.CategoryUtils;
import com.github.geovanegsfarias.commons.ProductUtils;
import com.github.geovanegsfarias.commons.UserUtils;
import com.github.geovanegsfarias.exception.ResourceNotFoundException;
import com.github.geovanegsfarias.user.UserService;
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
class CartServiceTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private CartService cartService;
    private CartUtils cartUtils;

    @BeforeEach
    void init() {
        var categoryUtils = new CategoryUtils();
        var userUtils = new UserUtils();
        cartUtils = new CartUtils(userUtils, new ProductUtils(categoryUtils));
    }

    @Test
    @DisplayName("findByEmail returns a cart for given email")
    @Order(1)
    void findByEmail_ReturnsCart_WhenSuccessful() {
        var expectedCart = cartUtils.savedCart();
        var user = expectedCart.getUser();

        BDDMockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(expectedCart));

        var cart = cartService.findByEmailOrThrowException(user.getEmail());

        Assertions.assertThat(expectedCart).isEqualTo(cart);
    }

    @Test
    @DisplayName("findByEmail throws ResourceNotFoundException when cart is not found")
    @Order(2)
    void findByEmail_ThrowsResourceNotFoundException_WhenCartNotFound() {
        var savedCart = cartUtils.savedCart();
        var user = savedCart.getUser();

        BDDMockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> cartService.findByEmailOrThrowException(user.getEmail()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Cart not found");
    }

    @Test
    @DisplayName("save returns existing cart when cart already exists")
    @Order(3)
    void save_ReturnsExistingCart_WhenCartAlreadyExists() {
        var expectedSavedCart = cartUtils.savedCart();
        var user = expectedSavedCart.getUser();

        BDDMockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(expectedSavedCart));

        var savedCart = cartService.save(user.getEmail());

        Assertions.assertThat(savedCart).isEqualTo(expectedSavedCart);
    }

    @Test
    @DisplayName("save creates a cart when cart does not exist")
    @Order(4)
    void save_CreatesCart_WhenCartDoesNotExist() {
        var expectedSavedCart = cartUtils.savedCart();
        var user = expectedSavedCart.getUser();

        BDDMockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.empty());
        BDDMockito.when(userService.findByEmailOrThrowException(user.getEmail())).thenReturn(user);
        BDDMockito.when(cartRepository.save(BDDMockito.any(Cart.class))).thenReturn(expectedSavedCart);

        var savedCart = cartService.save(user.getEmail());

        Assertions.assertThat(savedCart).isEqualTo(expectedSavedCart);
    }

    @Test
    @DisplayName("save throws ResourceNotFoundException when user is not found")
    @Order(5)
    void save_ThrowsResourceNotFoundException_WhenUserNotFound() {
        var expectedSavedCart = cartUtils.savedCart();
        var user = expectedSavedCart.getUser();

        BDDMockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.empty());
        BDDMockito.given(userService.findByEmailOrThrowException(user.getEmail())).willThrow(new ResourceNotFoundException("User not found"));

        Assertions.assertThatException()
                .isThrownBy(() -> cartService.save(user.getEmail()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("User not found");
    }

    @Test
    @DisplayName("delete removes all cart items")
    @Order(6)
    void delete_RemovesAllCartItems_WhenSuccessful() {
        var cartToDelete = cartUtils.savedCart();
        var user = cartToDelete.getUser();

        BDDMockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cartToDelete));

        Assertions.assertThatNoException().isThrownBy(() -> cartService.delete(user.getEmail()));
        BDDMockito.verify(cartItemRepository).deleteAllByCartId(cartToDelete.getId());
    }

    @Test
    @DisplayName("delete throws ResourceNotFoundException when cart is not found")
    @Order(7)
    void delete_ThrowsResourceNotFoundException_WhenCartNotFound() {
        var cartToDelete = cartUtils.savedCart();
        var user = cartToDelete.getUser();

        BDDMockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> cartService.delete(user.getEmail()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Cart not found");
    }
}