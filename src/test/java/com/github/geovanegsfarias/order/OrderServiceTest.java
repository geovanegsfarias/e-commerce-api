package com.github.geovanegsfarias.order;

import com.github.geovanegsfarias.cart.CartItemRepository;
import com.github.geovanegsfarias.cart.CartService;
import com.github.geovanegsfarias.commons.*;
import com.github.geovanegsfarias.exception.EmptyCartException;
import com.github.geovanegsfarias.exception.IllegalOperationException;
import com.github.geovanegsfarias.exception.InsufficientStockException;
import com.github.geovanegsfarias.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartService cartService;
    @Mock
    private CartItemRepository cartItemRepository;
    @InjectMocks
    private OrderService orderService;
    private OrderUtils orderUtils;
    private CartUtils cartUtils;

    @BeforeEach
    void init() {
        var categoryUtils = new CategoryUtils();
        var userUtils = new UserUtils();
        var productUtils = new ProductUtils(categoryUtils);

        orderUtils = new OrderUtils(userUtils, productUtils);
        cartUtils = new CartUtils(userUtils, productUtils);
    }

    @Test
    @DisplayName("findAll returns a page with all orders")
    @Order(1)
    void findAll_ReturnsAllOrders_WhenSuccessful() {
        var orders = List.of(orderUtils.savedOrder());
        var user = orders.getFirst().getUser();

        var pageRequest = PageRequest.of(0, 1);
        var expectedPage = new PageImpl<>(orders, pageRequest, orders.size());

        BDDMockito.when(orderRepository.findAllByUserEmail(user.getEmail(), pageRequest)).thenReturn(expectedPage);

        var ordersPage = orderService.findAll(user.getEmail(), pageRequest);

        Assertions.assertThat(ordersPage).isNotNull().hasSameElementsAs(orders);
    }

    @Test
    @DisplayName("findByIdAndEmail returns an order with given id")
    @Order(2)
    void findByIdAndEmail_ReturnsOrder_WhenSuccessful() {
        var expectedOrder = orderUtils.savedOrder();
        var user = expectedOrder.getUser();

        BDDMockito.when(orderRepository.findByIdAndUserEmail(expectedOrder.getId(), user.getEmail())).thenReturn(Optional.of(expectedOrder));

        var order = orderService.findByIdAndEmailOrThrowException(expectedOrder.getId(), user.getEmail());

        Assertions.assertThat(expectedOrder).isEqualTo(order);
    }

    @Test
    @DisplayName("findByIdAndEmail throws ResourceNotFoundException when order is not found")
    @Order(3)
    void findByIdAndEmail_ThrowsResourceNotFoundException_WhenOrderNotFound() {
        var savedOrder = orderUtils.savedOrder();
        var user = savedOrder.getUser();

        BDDMockito.when(orderRepository.findByIdAndUserEmail(savedOrder.getId(), user.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> orderService.findByIdAndEmailOrThrowException(savedOrder.getId(), user.getEmail()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Order not found");
    }

    @Test
    @DisplayName("save creates an order")
    @Order(4)
    void save_CreatesOrder_WhenSuccessful() {
        var cart = cartUtils.savedCartWithItems();
        var user = cart.getUser();

        var expectedPrice = cart.getCartItems().getFirst().getProduct().getPrice().multiply(BigDecimal.valueOf(cart.getCartItems().getFirst().getQuantity()));
        var expectedSavedOrder = orderUtils.savedOrder();

        BDDMockito.when(cartService.findByEmailOrThrowException(user.getEmail())).thenReturn(cart);
        BDDMockito.when(orderRepository.save(BDDMockito.any())).thenReturn(expectedSavedOrder);
        BDDMockito.doNothing().when(cartItemRepository).deleteAllByCartId(cart.getId());

        var savedOrder = orderService.save(user.getEmail());

        Assertions.assertThat(savedOrder).isNotNull().isEqualTo(expectedSavedOrder);
        BDDMockito.verify(orderRepository).save(BDDMockito.argThat(order -> order.getPrice().compareTo(expectedPrice) == 0));
        BDDMockito.verify(cartItemRepository).deleteAllByCartId(cart.getId());
    }

    @Test
    @DisplayName("save throws ResourceNotFoundException when cart is not found")
    @Order(5)
    void save_ThrowsResourceNotFoundException_WhenCartNotFound() {
        var cart = cartUtils.savedCartWithItems();
        var user = cart.getUser();

        BDDMockito.given(cartService.findByEmailOrThrowException(user.getEmail())).willThrow(new ResourceNotFoundException("Cart not found"));

        Assertions.assertThatException()
                .isThrownBy(() -> orderService.save(user.getEmail()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Cart not found");
    }

    @Test
    @DisplayName("save throws EmptyCartException when cart is empty")
    @Order(6)
    void save_ThrowsEmptyCartException_WhenCartIsEmpty() {
        var cart = cartUtils.savedCart();
        var user = cart.getUser();

        cart.setCartItems(List.of());

        BDDMockito.when(cartService.findByEmailOrThrowException(user.getEmail())).thenReturn(cart);

        Assertions.assertThatException()
                .isThrownBy(() -> orderService.save(user.getEmail()))
                .isInstanceOf(EmptyCartException.class)
                .withMessage("Cart is empty");
    }

    @Test
    @DisplayName("save throws InsufficientStockException when quantity exceeds product stock")
    @Order(7)
    void save_ThrowsInsufficientStockException_WhenQuantityExceedsStock() {
        var cart = cartUtils.savedCartWithItems();
        var user = cart.getUser();

        cart.getCartItems().getFirst().setQuantity(30);

        BDDMockito.when(cartService.findByEmailOrThrowException(user.getEmail())).thenReturn(cart);

        Assertions.assertThatException()
                .isThrownBy(() -> orderService.save(user.getEmail()))
                .isInstanceOf(InsufficientStockException.class)
                .withMessage("Insufficient stock");
    }

    @Test
    @DisplayName("delete removes an order")
    @Order(8)
    void delete_RemovesOrder_WhenSuccessful() {
        var orderToDelete = orderUtils.savedOrder();
        var user = orderToDelete.getUser();

        BDDMockito.when(orderRepository.findByIdAndUserEmail(orderToDelete.getId(), user.getEmail())).thenReturn(Optional.of(orderToDelete));
        BDDMockito.doNothing().when(orderRepository).delete(orderToDelete);

        Assertions.assertThatNoException().isThrownBy(() -> orderService.delete(orderToDelete.getId(), user.getEmail()));
        BDDMockito.verify(orderRepository).delete(orderToDelete);
    }

    @Test
    @DisplayName("delete throws ResourceNotFoundException when order is not found")
    @Order(9)
    void delete_ThrowsResourceNotFoundException_WhenOrderNotFound() {
        var orderToDelete = orderUtils.savedOrder();
        var user = orderToDelete.getUser();

        BDDMockito.when(orderRepository.findByIdAndUserEmail(orderToDelete.getId(), user.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> orderService.delete(orderToDelete.getId(), user.getEmail()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("Order not found");
    }

    @Test
    @DisplayName("delete throws IllegalOperationException when order status is not pending")
    @Order(10)
    void delete_ThrowsIllegalOperationException_WhenOrderStatusIsNotPending() {
        var orderToDelete = orderUtils.savedOrder();
        var user = orderToDelete.getUser();

        orderToDelete.setStatus(OrderStatus.PAID);

        BDDMockito.when(orderRepository.findByIdAndUserEmail(orderToDelete.getId(), user.getEmail())).thenReturn(Optional.of(orderToDelete));

        Assertions.assertThatException()
                .isThrownBy(() -> orderService.delete(orderToDelete.getId(), user.getEmail()))
                .isInstanceOf(IllegalOperationException.class)
                .withMessage("Only pending orders can be deleted");
    }

}