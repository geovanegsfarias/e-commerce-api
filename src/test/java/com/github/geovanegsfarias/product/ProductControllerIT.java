package com.github.geovanegsfarias.product;

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

import java.math.BigDecimal;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({TestcontainersConfig.class, RestAssuredConfig.class, FileUtils.class, AuthHelper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles({"test", "itest"})
class ProductControllerIT {
    private static final String URL = "/v1/product";
    @Autowired
    private RequestSpecification requestSpecification;
    @Autowired
    private ProductRepository productRepository;
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
    @DisplayName("GET v1/product returns a page with all products")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/product/init_three_products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(1)
    void getAllProducts_ReturnsAllProducts_WhenSuccessful() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("product/get-response-products-200.json");
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
    @DisplayName("GET v1/product/1 returns a product with given id")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/product/init_three_products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(2)
    void getProductById_ReturnsProduct_WhenSuccessful() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("product/get-response-product-200.json");
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
    @DisplayName("GET v1/product/1 returns not found when product is not found")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(3)
    void getProductById_ReturnsNotFound_WhenProductNotFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("product/get-response-product-404.json");
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
    @DisplayName("POST v1/product creates a product")
    @Sql(value = "/sql/user/init_one_admin_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(4)
    void saveProduct_CreatesProduct_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("product/post-request-product-200.json");
        var expectedResponse = fileUtils.readResourceFile("product/post-response-product-201.json");
        var jwtToken = authHelper.login("admin@gmail.com", "test1234");

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .isEqualTo(expectedResponse);
    }

    @ParameterizedTest
    @MethodSource("saveProductBadRequestSource")
    @DisplayName("POST v1/product returns bad request when fields are not valid")
    @Sql(value = "/sql/user/init_one_admin_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(5)
    void saveProduct_ReturnsBadRequest_WhenFieldsAreNotValid(String requestPath, String responsePath) throws Exception {
        var request = fileUtils.readResourceFile(requestPath);
        var expectedResponse = fileUtils.readResourceFile(responsePath);
        var jwtToken = authHelper.login("admin@gmail.com", "test1234");

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
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
    @DisplayName("POST v1/product returns not found when category is not found")
    @Sql(value = "/sql/user/init_one_admin_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(6)
    void saveProduct_ReturnsNotFound_WhenCategoryNotFound() throws Exception {
        var request = fileUtils.readResourceFile("product/post-request-product-200.json");
        var expectedResponse = fileUtils.readResourceFile("product/post-response-product-category-404.json");
        var jwtToken = authHelper.login("admin@gmail.com", "test1234");

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("PUT v1/product/1 updates a product")
    @Sql(value = "/sql/user/init_one_admin_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/product/init_three_products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(7)
    void updateProduct_UpdatesProduct_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("product/put-request-product-200.json");
        var jwtToken = authHelper.login("admin@gmail.com", "test1234");
        var id = 1L;

        RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .put(URL + "/{id}", id)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        var updatedProduct = productRepository.findById(id).orElseThrow();
        Assertions.assertThat(updatedProduct.getName()).isEqualTo("Gaming headphones");
        Assertions.assertThat(updatedProduct.getDescription()).isEqualTo("Wireless gaming headphones");
        Assertions.assertThat(updatedProduct.getPrice()).isEqualByComparingTo(new BigDecimal("349.90"));
        Assertions.assertThat(updatedProduct.getStock()).isEqualTo(30);
        Assertions.assertThat(updatedProduct.getCategory().getId()).isEqualTo(1L);
    }

    @ParameterizedTest
    @MethodSource("updateProductBadRequestSource")
    @DisplayName("PUT v1/product/1 returns bad request when fields are not valid")
    @Sql(value = "/sql/user/init_one_admin_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(8)
    void updateProduct_ReturnsBadRequest_WhenFieldsAreNotValid(String requestPath, String responsePath) throws Exception {
        var request = fileUtils.readResourceFile(requestPath);
        var expectedResponse = fileUtils.readResourceFile(responsePath);
        var jwtToken = authHelper.login("admin@gmail.com", "test1234");
        var id = 1L;

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .put(URL + "/{id}", id)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("DELETE v1/product/1 removes a product")
    @Sql(value = "/sql/user/init_one_admin_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/product/init_three_products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(9)
    void deleteProduct_RemovesProduct_WhenSuccessful() throws Exception {
        var jwtToken = authHelper.login("admin@gmail.com", "test1234");
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

        Assertions.assertThat(productRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("DELETE v1/product/1 returns not found when product is not found")
    @Sql(value = "/sql/user/init_one_admin_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(10)
    void deleteProduct_ReturnsNotFound_WhenProductNotFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("product/delete-response-product-404.json");
        var jwtToken = authHelper.login("admin@gmail.com", "test1234");
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

    private static Stream<Arguments> saveProductBadRequestSource() {
        return Stream.of(
                Arguments.of("product/post-request-product-blank-name-400.json", "product/post-response-product-blank-name-400.json"),
                Arguments.of("product/post-request-product-blank-description-400.json", "product/post-response-product-blank-description-400.json"),
                Arguments.of("product/post-request-product-null-price-400.json", "product/post-response-product-null-price-400.json"),
                Arguments.of("product/post-request-product-invalid-price-400.json", "product/post-response-product-invalid-price-400.json"),
                Arguments.of("product/post-request-product-negative-stock-400.json", "product/post-response-product-negative-stock-400.json"),
                Arguments.of("product/post-request-product-null-category-400.json", "product/post-response-product-null-category-400.json")
        );
    }

    private static Stream<Arguments> updateProductBadRequestSource() {
        return Stream.of(
                Arguments.of("product/put-request-product-blank-name-400.json", "product/put-response-product-blank-name-400.json"),
                Arguments.of("product/put-request-product-blank-description-400.json", "product/put-response-product-blank-description-400.json"),
                Arguments.of("product/put-request-product-null-price-400.json", "product/put-response-product-null-price-400.json"),
                Arguments.of("product/put-request-product-invalid-price-400.json", "product/put-response-product-invalid-price-400.json"),
                Arguments.of("product/put-request-product-negative-stock-400.json", "product/put-response-product-negative-stock-400.json"),
                Arguments.of("product/put-request-product-null-category-400.json", "product/put-response-product-null-category-400.json")
        );
    }
}
