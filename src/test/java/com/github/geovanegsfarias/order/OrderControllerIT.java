package com.github.geovanegsfarias.order;

import com.github.geovanegsfarias.cart.CartItemRepository;
import com.github.geovanegsfarias.commons.AuthHelper;
import com.github.geovanegsfarias.commons.FileUtils;
import com.github.geovanegsfarias.configuration.RestAssuredConfig;
import com.github.geovanegsfarias.configuration.TestcontainersConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({TestcontainersConfig.class, RestAssuredConfig.class, FileUtils.class, AuthHelper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles({"test", "itest"})
class OrderControllerIT {
    private static final String URL = "/v1/order";
    @Autowired
    private RequestSpecification requestSpecification;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private FileUtils fileUtils;
    @Autowired
    private AuthHelper authHelper;
    @LocalServerPort
    int port;

    @BeforeEach
    void setup() {
        requestSpecification.port(port);
    }

    @Test
    @DisplayName("GET v1/order returns a page with the authenticated user's orders")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/product/init_three_products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/order/init_one_pending_order.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/order/init_one_order_item.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(1)
    void getAllOrders_ReturnsAllOrders_WhenSuccessful() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("order/get-response-orders-200.json");
        var jwtToken = authHelper.login("user@gmail.com", "test1234");

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("GET v1/order/1 returns an order with given id")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/product/init_three_products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/order/init_one_pending_order.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/order/init_one_order_item.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(2)
    void getOrder_ReturnsOrder_WhenSuccessful() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("order/get-response-order-200.json");
        var jwtToken = authHelper.login("user@gmail.com", "test1234");
        var id = 1L;

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL + "/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("GET v1/order/1 returns not found when order is not found")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(3)
    void getOrder_ReturnsNotFound_WhenOrderNotFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("order/get-response-order-404.json");
        var jwtToken = authHelper.login("user@gmail.com", "test1234");
        var id = 1L;

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL + "/{id}", id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("POST v1/order creates an order")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/product/init_three_products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/cart/init_one_cart.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/cart/init_one_cart_item.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(4)
    void saveOrder_CreatesOrder_WhenSuccessful() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("order/post-response-order-201.json");
        var jwtToken = authHelper.login("user@gmail.com", "test1234");

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("createdAt")
                .isEqualTo(expectedResponse);

        Assertions.assertThat(orderRepository.findById(1L)).isPresent();
        Assertions.assertThat(cartItemRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("POST v1/order returns bad request when cart is empty")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/cart/init_one_cart.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(5)
    void saveOrder_ReturnsBadRequest_WhenCartIsEmpty() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("order/post-response-order-empty-cart-400.json");
        var jwtToken = authHelper.login("user@gmail.com", "test1234");

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("DELETE v1/order/1 removes an order")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/order/init_one_pending_order.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(6)
    void deleteOrder_RemovesOrder_WhenSuccessful() {
        var jwtToken = authHelper.login("user@gmail.com", "test1234");
        var id = 1L;

        RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .delete(URL + "/{id}", id)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        Assertions.assertThat(orderRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("DELETE v1/order/1 returns not found when order is not found")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(7)
    void deleteOrder_ReturnsNotFound_WhenOrderNotFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("order/delete-response-order-404.json");
        var jwtToken = authHelper.login("user@gmail.com", "test1234");
        var id = 1L;

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .delete(URL + "/{id}", id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("DELETE v1/order/1 returns bad request when order is not pending")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/order/init_one_paid_order.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(8)
    void deleteOrder_ReturnsBadRequest_WhenOrderIsNotPending() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("order/delete-response-order-not-pending-400.json");
        var jwtToken = authHelper.login("user@gmail.com", "test1234");
        var id = 1L;

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .delete(URL + "/{id}", id)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .isEqualTo(expectedResponse);
    }
}
