package com.github.geovanegsfarias.cart;

import com.github.geovanegsfarias.category.CategoryRepository;
import com.github.geovanegsfarias.category.CategoryService;
import com.github.geovanegsfarias.commons.*;
import com.github.geovanegsfarias.configuration.SecurityConfig;
import com.github.geovanegsfarias.product.ProductRepository;
import com.github.geovanegsfarias.product.ProductService;
import com.github.geovanegsfarias.user.UserRepository;
import com.github.geovanegsfarias.user.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@WebMvcTest(controllers = CartController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({CartMapperImpl.class, CartItemMapperImpl.class, CartService.class, CartItemService.class, ProductService.class, CategoryService.class, UserService.class, CartUtils.class, ProductUtils.class, CategoryUtils.class, UserUtils.class, FileUtils.class, SecurityConfig.class})
class CartControllerTest {
    private static final String URL = "/v1/cart";
    private static final String USER_EMAIL = "user@gmail.com";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CartRepository cartRepository;
    @MockitoBean
    private CartItemRepository cartItemRepository;
    @MockitoBean
    private ProductRepository productRepository;
    @MockitoBean
    private CategoryRepository categoryRepository;
    @MockitoBean
    private UserRepository userRepository;
    @Autowired
    private CartUtils cartUtils;
    @Autowired
    private ProductUtils productUtils;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private FileUtils fileUtils;

    @Test
    @DisplayName("GET v1/cart returns the authenticated user's cart")
    @Order(1)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void getCart_ReturnsCart_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("cart/get-response-cart-200.json");
        var savedCart = cartUtils.savedCartWithItems();

        BDDMockito.when(cartRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.of(savedCart));

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET v1/cart returns not found when cart is not found")
    @Order(2)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void getCart_ReturnsNotFound_WhenCartNotFound() throws Exception {
        var response = fileUtils.readResourceFile("cart/get-response-cart-404.json");

        BDDMockito.when(cartRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    @Test
    @DisplayName("POST v1/cart creates a cart")
    @Order(3)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void saveCart_CreatesCart_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("cart/post-response-cart-201.json");

        var user = userUtils.savedUser();
        var savedCart = cartUtils.savedCart();
        savedCart.setCartItems(List.of());

        BDDMockito.when(cartRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.empty());
        BDDMockito.when(userRepository.findByEmailIgnoreCase(USER_EMAIL)).thenReturn(Optional.of(user));
        BDDMockito.when(cartRepository.save(BDDMockito.any(Cart.class))).thenReturn(savedCart);

        mockMvc.perform(MockMvcRequestBuilders.post(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("DELETE v1/cart clears the cart")
    @Order(4)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void deleteCart_ClearsCart_WhenSuccessful() throws Exception {
        var savedCart = cartUtils.savedCart();

        BDDMockito.when(cartRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.of(savedCart));

        mockMvc.perform(MockMvcRequestBuilders.delete(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE v1/cart returns not found when cart is not found")
    @Order(5)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void deleteCart_ReturnsNotFound_WhenCartNotFound() throws Exception {
        var response = fileUtils.readResourceFile("cart/delete-response-cart-404.json");

        BDDMockito.when(cartRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    @Test
    @DisplayName("POST v1/cart/items adds an item to the cart")
    @Order(6)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void saveCartItem_AddsItem_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("cart/post-request-cart-item-200.json");
        var response = fileUtils.readResourceFile("cart/post-response-cart-item-201.json");

        var cart = cartUtils.savedCart();
        var product = productUtils.savedProduct();
        var savedCartItem = cartUtils.savedCartItem();
        savedCartItem.setCart(cart);
        savedCartItem.setProduct(product);
        savedCartItem.setQuantity(2);

        BDDMockito.when(cartRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.of(cart));
        BDDMockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        BDDMockito.when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())).thenReturn(Optional.empty());
        BDDMockito.when(cartItemRepository.save(BDDMockito.any(CartItem.class))).thenReturn(savedCartItem);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL + "/items")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @ParameterizedTest
    @MethodSource("saveCartItemBadRequestSource")
    @DisplayName("POST v1/cart/items returns bad request when fields are not valid")
    @Order(7)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void saveCartItem_ReturnsBadRequest_WhenFieldsAreNotValid(String requestPath, String responsePath) throws Exception {
        var request = fileUtils.readResourceFile(requestPath);
        var response = fileUtils.readResourceFile(responsePath);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL + "/items")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    @Test
    @DisplayName("POST v1/cart/items returns bad request when stock is insufficient")
    @Order(8)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void saveCartItem_ReturnsBadRequest_WhenStockIsInsufficient() throws Exception {
        var request = fileUtils.readResourceFile("cart/post-request-cart-item-insufficient-stock-400.json");
        var response = fileUtils.readResourceFile("cart/post-response-cart-item-insufficient-stock-400.json");

        var cart = cartUtils.savedCart();
        var product = productUtils.savedProduct();

        BDDMockito.when(cartRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.of(cart));
        BDDMockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        BDDMockito.when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL + "/items")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    @Test
    @DisplayName("DELETE v1/cart/items/1 removes an item from the cart")
    @Order(9)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void deleteCartItem_RemovesItem_WhenSuccessful() throws Exception {
        var cartItemToDelete = cartUtils.savedCartItem();
        var id = cartItemToDelete.getId();

        BDDMockito.when(cartItemRepository.findById(id)).thenReturn(Optional.of(cartItemToDelete));

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/items/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE v1/cart/items/1 returns not found when cart item is not found")
    @Order(10)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void deleteCartItem_ReturnsNotFound_WhenCartItemNotFound() throws Exception {
        var response = fileUtils.readResourceFile("cart/delete-response-cart-item-404.json");
        var id = 1L;

        BDDMockito.when(cartItemRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/items/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    @Test
    @DisplayName("DELETE v1/cart/items/1 returns forbidden when cart item does not belong to user")
    @Order(11)
    @WithMockUser(username = "wrong@gmail.com", roles = "USER")
    void deleteCartItem_ReturnsForbidden_WhenCartItemDoesNotBelongToUser() throws Exception {
        var response = fileUtils.readResourceFile("cart/delete-response-cart-item-403.json");
        var cartItemToDelete = cartUtils.savedCartItem();
        var id = cartItemToDelete.getId();

        BDDMockito.when(cartItemRepository.findById(id)).thenReturn(Optional.of(cartItemToDelete));

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/items/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    private static Stream<Arguments> saveCartItemBadRequestSource() {
        return Stream.of(
                Arguments.of("cart/post-request-cart-item-null-product-400.json", "cart/post-response-cart-item-null-product-400.json"),
                Arguments.of("cart/post-request-cart-item-invalid-quantity-400.json", "cart/post-response-cart-item-invalid-quantity-400.json")
        );
    }
}
