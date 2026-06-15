package com.github.geovanegsfarias.order;

import com.github.geovanegsfarias.cart.CartItemRepository;
import com.github.geovanegsfarias.cart.CartRepository;
import com.github.geovanegsfarias.cart.CartService;
import com.github.geovanegsfarias.commons.*;
import com.github.geovanegsfarias.configuration.SecurityConfig;
import com.github.geovanegsfarias.user.UserRepository;
import com.github.geovanegsfarias.user.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

@WebMvcTest(controllers = OrderController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({OrderMapperImpl.class, OrderItemMapperImpl.class, OrderService.class, CartService.class, UserService.class, OrderUtils.class, CartUtils.class, ProductUtils.class, CategoryUtils.class, UserUtils.class, FileUtils.class, SecurityConfig.class})
class OrderControllerTest {
    private static final String URL = "/v1/order";
    private static final String USER_EMAIL = "user@gmail.com";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private OrderRepository orderRepository;
    @MockitoBean
    private CartRepository cartRepository;
    @MockitoBean
    private CartItemRepository cartItemRepository;
    @MockitoBean
    private UserRepository userRepository;
    @Autowired
    private OrderUtils orderUtils;
    @Autowired
    private CartUtils cartUtils;
    @Autowired
    private FileUtils fileUtils;

    @Test
    @DisplayName("GET v1/order returns a page with the authenticated user's orders")
    @Order(1)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void getAllOrders_ReturnsAllOrders_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("order/get-response-orders-200.json");

        var orders = List.of(orderUtils.savedOrder());
        var pageRequest = PageRequest.of(0, orders.size());
        var orderPage = new PageImpl<>(orders, pageRequest, orders.size());

        BDDMockito.when(orderRepository.findAllByUserEmail(
                BDDMockito.eq(USER_EMAIL),
                BDDMockito.any(Pageable.class)
        )).thenReturn(orderPage);

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET v1/order/1 returns an order with given id")
    @Order(2)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void getOrder_ReturnsOrder_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("order/get-response-order-200.json");

        var savedOrder = orderUtils.savedOrder();
        var id = savedOrder.getId();

        BDDMockito.when(orderRepository.findByIdAndUserEmail(id, USER_EMAIL)).thenReturn(Optional.of(savedOrder));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET v1/order/1 returns not found when order is not found")
    @Order(3)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void getOrder_ReturnsNotFound_WhenOrderNotFound() throws Exception {
        var response = fileUtils.readResourceFile("order/get-response-order-404.json");
        var id = 1L;

        BDDMockito.when(orderRepository.findByIdAndUserEmail(id, USER_EMAIL)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    @Test
    @DisplayName("POST v1/order creates an order")
    @Order(4)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void saveOrder_CreatesOrder_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("order/post-response-order-201.json");

        var cart = cartUtils.savedCartWithItems();
        var savedOrder = orderUtils.savedOrder();

        BDDMockito.when(cartRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.of(cart));
        BDDMockito.when(orderRepository.save(BDDMockito.any(com.github.geovanegsfarias.order.Order.class))).thenReturn(savedOrder);

        mockMvc.perform(MockMvcRequestBuilders.post(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST v1/order returns bad request when cart is empty")
    @Order(5)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void saveOrder_ReturnsBadRequest_WhenCartIsEmpty() throws Exception {
        var response = fileUtils.readResourceFile("order/post-response-order-empty-cart-400.json");

        var emptyCart = cartUtils.savedCart();
        emptyCart.setCartItems(List.of());

        BDDMockito.when(cartRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.of(emptyCart));

        mockMvc.perform(MockMvcRequestBuilders.post(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    @Test
    @DisplayName("DELETE v1/order/1 removes an order")
    @Order(6)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void deleteOrder_RemovesOrder_WhenSuccessful() throws Exception {
        var orderToDelete = orderUtils.savedOrder();
        var id = orderToDelete.getId();

        BDDMockito.when(orderRepository.findByIdAndUserEmail(id, USER_EMAIL)).thenReturn(Optional.of(orderToDelete));

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE v1/order/1 returns not found when order is not found")
    @Order(7)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void deleteOrder_ReturnsNotFound_WhenOrderNotFound() throws Exception {
        var response = fileUtils.readResourceFile("order/delete-response-order-404.json");
        var id = 1L;

        BDDMockito.when(orderRepository.findByIdAndUserEmail(id, USER_EMAIL)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    @Test
    @DisplayName("DELETE v1/order/1 returns bad request when order is not pending")
    @Order(8)
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void deleteOrder_ReturnsBadRequest_WhenOrderIsNotPending() throws Exception {
        var response = fileUtils.readResourceFile("order/delete-response-order-not-pending-400.json");

        var orderToDelete = orderUtils.savedOrder();
        orderToDelete.setStatus(OrderStatus.PAID);
        var id = orderToDelete.getId();

        BDDMockito.when(orderRepository.findByIdAndUserEmail(id, USER_EMAIL)).thenReturn(Optional.of(orderToDelete));

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }
}
