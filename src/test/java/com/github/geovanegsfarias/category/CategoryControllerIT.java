package com.github.geovanegsfarias.category;

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
class CategoryControllerIT {
    private static final String URL = "/v1/category";
    @Autowired
    private RequestSpecification requestSpecification;
    @Autowired
    private CategoryRepository categoryRepository;
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
    @DisplayName("GET v1/category returns a list with all categories")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(1)
    void getAllCategories_ReturnsAllCategories_WhenSuccessful() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("category/get-response-categories-200.json");
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
    @DisplayName("GET v1/category/1 returns a category with given id")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(2)
    void getCategoryById_ReturnsCategory_WhenSuccessful() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("category/get-response-category-200.json");
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
    @DisplayName("GET v1/category/1 returns not found when category is not found")
    @Sql(value = "/sql/user/init_one_regular_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(3)
    void getCategoryById_ReturnsNotFound_WhenCategoryNotFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("category/get-response-category-404.json");
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
    @DisplayName("POST v1/category creates a category")
    @Sql(value = "/sql/user/init_one_admin_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(4)
    void saveCategory_CreatesCategory_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("category/post-request-category-200.json");
        var expectedResponse = fileUtils.readResourceFile("category/post-response-category-201.json");
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

    @Test
    @DisplayName("POST v1/category returns bad request when fields are not valid")
    @Sql(value = "/sql/user/init_one_admin_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(5)
    void saveCategory_ReturnsBadRequest_WhenFieldsAreNotValid() throws Exception {
        var request = fileUtils.readResourceFile("category/post-request-category-blank-field-400.json");
        var expectedResponse = fileUtils.readResourceFile("category/post-response-category-blank-field-400.json");
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
    @DisplayName("POST v1/category returns conflict when category name is already in use")
    @Sql(value = "/sql/user/init_one_admin_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(6)
    void saveCategory_ReturnsConflict_WhenCategoryNameAlreadyInUse() throws Exception {
        var request = fileUtils.readResourceFile("category/post-request-category-200.json");
        var expectedResponse = fileUtils.readResourceFile("category/post-response-category-409.json");
        var jwtToken = authHelper.login("admin@gmail.com", "test1234");

        var response = RestAssured.given()
                .spec(requestSpecification)
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("PUT v1/category/1 updates a category")
    @Sql(value = "/sql/user/init_one_admin_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(7)
    void updateCategory_UpdatesCategory_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("category/put-request-category-200.json");
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

        var updatedCategory = categoryRepository.findById(id).orElseThrow();
        Assertions.assertThat(updatedCategory.getName()).isEqualTo("Video Games");
    }

    @Test
    @DisplayName("PUT v1/category/1 returns bad request when fields are not valid")
    @Sql(value = "/sql/user/init_one_admin_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(8)
    void updateCategory_ReturnsBadRequest_WhenFieldsAreNotValid() throws Exception {
        var request = fileUtils.readResourceFile("category/put-request-category-blank-field-400.json");
        var expectedResponse = fileUtils.readResourceFile("category/put-response-category-blank-field-400.json");
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
    @DisplayName("DELETE v1/category/1 removes a category")
    @Sql(value = "/sql/user/init_one_admin_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/category/init_three_categories.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(9)
    void deleteCategory_RemovesCategory_WhenSuccessful() throws Exception {
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

        Assertions.assertThat(categoryRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("DELETE v1/category/1 returns not found when category is not found")
    @Sql(value = "/sql/user/init_one_admin_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/clean_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Order(10)
    void deleteCategory_ReturnsNotFound_WhenCategoryNotFound() throws Exception {
        var expectedResponse = fileUtils.readResourceFile("category/delete-response-category-404.json");
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

}
