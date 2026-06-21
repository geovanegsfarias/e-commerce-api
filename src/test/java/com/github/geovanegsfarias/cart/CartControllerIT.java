package com.github.geovanegsfarias.cart;

import com.github.geovanegsfarias.commons.AuthHelper;
import com.github.geovanegsfarias.commons.FileUtils;
import com.github.geovanegsfarias.configuration.RestAssuredConfig;
import com.github.geovanegsfarias.configuration.TestcontainersConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({TestcontainersConfig.class, RestAssuredConfig.class, FileUtils.class, AuthHelper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles({"test", "itest"})
class CartControllerIT {
    private static final String URL = "/v1/cart";
    @Autowired
    private RequestSpecification requestSpecification;
    @Autowired
    private CartRepository cartRepository;
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
    @DisplayName("GET v1/cart returns the authenticated user's cart")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/product/init_three_products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/cart/init_one_cart.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/cart/init_one_cart_item.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(1)
    void getCart_ReturnsCart_WhenSuccessful() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("cart/get-response-cart-200.json");
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
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("GET v1/cart returns not found when cart is not found")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(2)
    void getCart_ReturnsNotFound_WhenCartNotFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("cart/get-response-cart-404.json");
        var jwtToken = authHelper.login("user@gmail.com", "test1234");

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("POST v1/cart creates a cart")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(3)
    void saveCart_CreatesCart_WhenSuccessful() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("cart/post-response-cart-201.json");
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
                .isEqualTo(expectedResponse);

        Assertions.assertThat(cartRepository.findById(1L)).isPresent();
    }

    @Test
    @DisplayName("DELETE v1/cart clears the cart")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/product/init_three_products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/cart/init_one_cart.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/cart/init_one_cart_item.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(4)
    void deleteCart_ClearsCart_WhenSuccessful() {
        var jwtToken = authHelper.login("user@gmail.com", "test1234");

        RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .delete(URL)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        Assertions.assertThat(cartRepository.findById(1L)).isPresent();
        Assertions.assertThat(cartItemRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("DELETE v1/cart returns not found when cart is not found")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(5)
    void deleteCart_ReturnsNotFound_WhenCartNotFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("cart/delete-response-cart-404.json");
        var jwtToken = authHelper.login("user@gmail.com", "test1234");

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .delete(URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("POST v1/cart/items adds an item to the cart")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/product/init_three_products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/cart/init_one_cart.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(6)
    void saveCartItem_AddsItem_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("cart/post-request-cart-item-200.json");
        var expectedResponse = fileUtils.readResourceFile("cart/post-response-cart-item-201.json");
        var jwtToken = authHelper.login("user@gmail.com", "test1234");

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/items")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .isEqualTo(expectedResponse);

        var cartItem = cartItemRepository.findById(1L).orElseThrow();
        Assertions.assertThat(cartItem.getQuantity()).isEqualTo(2);
        Assertions.assertThat(cartItem.getProduct().getId()).isEqualTo(1L);
    }

    @ParameterizedTest
    @MethodSource("saveCartItemBadRequestSource")
    @DisplayName("POST v1/cart/items returns bad request when fields are not valid")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(7)
    void saveCartItem_ReturnsBadRequest_WhenFieldsAreNotValid(String requestPath, String responsePath) throws Exception {
        var request = fileUtils.readResourceFile(requestPath);
        var expectedResponse = fileUtils.readResourceFile(responsePath);
        var jwtToken = authHelper.login("user@gmail.com", "test1234");

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/items")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("POST v1/cart/items returns bad request when stock is insufficient")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/product/init_three_products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/cart/init_one_cart.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(8)
    void saveCartItem_ReturnsBadRequest_WhenStockIsInsufficient() throws Exception {
        var request = fileUtils.readResourceFile("cart/post-request-cart-item-insufficient-stock-400.json");
        var expectedResponse = fileUtils.readResourceFile("cart/post-response-cart-item-insufficient-stock-400.json");
        var jwtToken = authHelper.login("user@gmail.com", "test1234");

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/items")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("DELETE v1/cart/items/1 removes an item from the cart")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/product/init_three_products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/cart/init_one_cart.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/cart/init_one_cart_item.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(9)
    void deleteCartItem_RemovesItem_WhenSuccessful() {
        var jwtToken = authHelper.login("user@gmail.com", "test1234");
        var id = 1L;

        RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .delete(URL + "/items/{id}", id)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        Assertions.assertThat(cartItemRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("DELETE v1/cart/items/1 returns not found when cart item is not found")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(10)
    void deleteCartItem_ReturnsNotFound_WhenCartItemNotFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("cart/delete-response-cart-item-404.json");
        var jwtToken = authHelper.login("user@gmail.com", "test1234");
        var id = 1L;

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .delete(URL + "/items/{id}", id)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("DELETE v1/cart/items/1 returns forbidden when cart item does not belong to user")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/init_one_admin_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/product/init_three_products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/cart/init_one_cart.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/cart/init_one_cart_item.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(11)
    void deleteCartItem_ReturnsForbidden_WhenCartItemDoesNotBelongToUser() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("cart/delete-response-cart-item-403.json");
        var jwtToken = authHelper.login("admin@gmail.com", "test1234");
        var id = 1L;

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .delete(URL + "/items/{id}", id)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .isEqualTo(expectedResponse);
    }

    private static Stream<Arguments> saveCartItemBadRequestSource() {
        return Stream.of(
                Arguments.of("cart/post-request-cart-item-null-product-400.json", "cart/post-response-cart-item-null-product-400.json"),
                Arguments.of("cart/post-request-cart-item-invalid-quantity-400.json", "cart/post-response-cart-item-invalid-quantity-400.json")
        );
    }
}
