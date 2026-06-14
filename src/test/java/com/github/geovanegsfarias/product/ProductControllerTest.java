package com.github.geovanegsfarias.product;

import com.github.geovanegsfarias.category.CategoryMapperImpl;
import com.github.geovanegsfarias.category.CategoryRepository;
import com.github.geovanegsfarias.category.CategoryService;
import com.github.geovanegsfarias.commons.CategoryUtils;
import com.github.geovanegsfarias.commons.FileUtils;
import com.github.geovanegsfarias.commons.ProductUtils;
import com.github.geovanegsfarias.configuration.SecurityConfig;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

import java.util.Optional;
import java.util.stream.Stream;

@WebMvcTest(controllers = ProductController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({ProductMapperImpl.class, CategoryMapperImpl.class, ProductService.class, CategoryService.class, ProductUtils.class, CategoryUtils.class, FileUtils.class, SecurityConfig.class})
class ProductControllerTest {
    private static final String URL = "/v1/product";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ProductRepository productRepository;
    @MockitoBean
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductUtils productUtils;
    @Autowired
    private CategoryUtils categoryUtils;
    @Autowired
    private FileUtils fileUtils;

    @Test
    @DisplayName("GET v1/product returns a page with all products")
    @Order(1)
    @WithMockUser(roles = "USER")
    void getAllProducts_ReturnsAllProducts_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("product/get-response-products-200.json");

        var products = productUtils.newProductList();
        var pageRequest = PageRequest.of(0, products.size());
        var productPage = new PageImpl<>(products, pageRequest, products.size());

        BDDMockito.when(productRepository.findAll(BDDMockito.any(Pageable.class))).thenReturn(productPage);

        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET v1/product/1 returns a product with given id")
    @Order(2)
    @WithMockUser(roles = "USER")
    void getProductById_ReturnsProduct_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("product/get-response-product-200.json");

        var savedProduct = productUtils.savedProduct();
        var id = savedProduct.getId();

        BDDMockito.when(productRepository.findById(id)).thenReturn(Optional.of(savedProduct));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET v1/product/1 returns not found when product is not found")
    @Order(3)
    @WithMockUser(roles = "USER")
    void getProductById_ReturnsNotFound_WhenProductNotFound() throws Exception {
        var response = fileUtils.readResourceFile("product/get-response-product-404.json");
        var id = 1L;

        BDDMockito.when(productRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    @Test
    @DisplayName("POST v1/product creates a product")
    @Order(4)
    @WithMockUser(roles = "ADMIN")
    void saveProduct_CreatesProduct_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("product/post-request-product-200.json");
        var response = fileUtils.readResourceFile("product/post-response-product-201.json");

        var category = categoryUtils.savedCategory();
        var savedProduct = productUtils.savedProduct();

        BDDMockito.when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        BDDMockito.when(productRepository.save(BDDMockito.any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @ParameterizedTest
    @MethodSource("saveProductBadRequestSource")
    @DisplayName("POST v1/product returns bad request when fields are not valid")
    @Order(5)
    @WithMockUser(roles = "ADMIN")
    void saveProduct_ReturnsBadRequest_WhenFieldsAreNotValid(String requestPath, String responsePath) throws Exception {
        var request = fileUtils.readResourceFile(requestPath);
        var response = fileUtils.readResourceFile(responsePath);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    @Test
    @DisplayName("POST v1/product returns not found when category is not found")
    @Order(6)
    @WithMockUser(roles = "ADMIN")
    void saveProduct_ReturnsNotFound_WhenCategoryNotFound() throws Exception {
        var request = fileUtils.readResourceFile("product/post-request-product-200.json");
        var response = fileUtils.readResourceFile("product/post-response-product-category-404.json");
        var categoryId = 1L;

        BDDMockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    @Test
    @DisplayName("PUT v1/product/1 updates a product")
    @Order(7)
    @WithMockUser(roles = "ADMIN")
    void updateProduct_UpdatesProduct_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("product/put-request-product-200.json");

        var productToUpdate = productUtils.savedProduct();
        var category = categoryUtils.savedCategory();
        var id = productToUpdate.getId();

        BDDMockito.when(productRepository.findById(id)).thenReturn(Optional.of(productToUpdate));
        BDDMockito.when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + "/{id}", id)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @ParameterizedTest
    @MethodSource("updateProductBadRequestSource")
    @DisplayName("PUT v1/product/1 returns bad request when fields are not valid")
    @Order(8)
    @WithMockUser(roles = "ADMIN")
    void updateProduct_ReturnsBadRequest_WhenFieldsAreNotValid(String requestPath, String responsePath) throws Exception {
        var request = fileUtils.readResourceFile(requestPath);
        var response = fileUtils.readResourceFile(responsePath);
        var id = 1L;

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + "/{id}", id)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    @Test
    @DisplayName("DELETE v1/product/1 removes a product")
    @Order(9)
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_RemovesProduct_WhenSuccessful() throws Exception {
        var productToDelete = productUtils.savedProduct();
        var id = productToDelete.getId();

        BDDMockito.when(productRepository.findById(id)).thenReturn(Optional.of(productToDelete));

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE v1/product/1 returns not found when product is not found")
    @Order(10)
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_ReturnsNotFound_WhenProductNotFound() throws Exception {
        var response = fileUtils.readResourceFile("product/delete-response-product-404.json");
        var id = 1L;

        BDDMockito.when(productRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
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
