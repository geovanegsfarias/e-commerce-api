package com.github.geovanegsfarias.category;

import com.github.geovanegsfarias.commons.CategoryUtils;
import com.github.geovanegsfarias.commons.FileUtils;
import com.github.geovanegsfarias.configuration.SecurityConfig;
import com.github.geovanegsfarias.product.ProductRepository;
import org.junit.jupiter.api.*;
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

import java.util.Optional;

@WebMvcTest(controllers = CategoryController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({CategoryMapperImpl.class, CategoryService.class, CategoryUtils.class, FileUtils.class, SecurityConfig.class})
class CategoryControllerTest {
    private static final String URL = "/v1/category";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CategoryRepository categoryRepository;
    @MockitoBean
    private ProductRepository productRepository;
    @Autowired
    private CategoryUtils categoryUtils;
    @Autowired
    private FileUtils fileUtils;

    @Test
    @DisplayName("GET v1/category returns a list with all categories")
    @Order(1)
    @WithMockUser(roles = "USER")
    void getAllCategories_ReturnsAllCategories_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("category/get-response-categories-200.json");

        var categoryList = categoryUtils.newCategoryList();

        BDDMockito.when(categoryRepository.findAll()).thenReturn(categoryList);

        mockMvc.perform(MockMvcRequestBuilders
                .get(URL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET v1/category/1 returns a category with given id")
    @Order(2)
    @WithMockUser(roles = "USER")
    void getCategoryById_ReturnsCategory_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("category/get-response-category-200.json");

        var savedCategory = categoryUtils.savedCategory();
        var id = savedCategory.getId();

        BDDMockito.when(categoryRepository.findById(id)).thenReturn(Optional.of(savedCategory));

        mockMvc.perform(MockMvcRequestBuilders
                .get(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET v1/category/1 returns not found when category is not found")
    @Order(3)
    @WithMockUser(roles = "USER")
    void getCategoryById_ReturnsNotFound_WhenCategoryNotFound() throws Exception {
        var response = fileUtils.readResourceFile("category/get-response-category-404.json");

        var id = 1L;

        BDDMockito.when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    @Test
    @DisplayName("POST v1/category creates a category")
    @Order(4)
    @WithMockUser(roles = "ADMIN")
    void saveCategory_CreatesCategory_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("category/post-request-category-200.json");
        var response = fileUtils.readResourceFile("category/post-response-category-201.json");

        var savedCategory = categoryUtils.savedCategory();

        BDDMockito.when(categoryRepository.save(BDDMockito.any())).thenReturn(savedCategory);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST v1/category returns bad request when fields are not valid")
    @Order(5)
    @WithMockUser(roles = "ADMIN")
    void saveCategory_ReturnsBadRequest_WhenFieldsAreNotValid() throws Exception {
        var request = fileUtils.readResourceFile("category/post-request-category-blank-field-400.json");
        var response = fileUtils.readResourceFile("category/post-response-category-blank-field-400.json");

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
    @DisplayName("POST v1/category returns conflict when category name is already in use")
    @Order(6)
    @WithMockUser(roles = "ADMIN")
    void saveCategory_ReturnsConflict_WhenCategoryNameAlreadyInUse() throws Exception {
        var request = fileUtils.readResourceFile("category/post-request-category-200.json");
        var response = fileUtils.readResourceFile("category/post-response-category-409.json");

        var savedCategory = categoryUtils.savedCategory();

        BDDMockito.when(categoryRepository.existsByNameIgnoreCase(savedCategory.getName())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

    @Test
    @DisplayName("PUT v1/category/1 updates a category")
    @Order(7)
    @WithMockUser(roles = "ADMIN")
    void updateCategory_UpdatesCategory_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("category/put-request-category-200.json");

        var categoryToUpdate = categoryUtils.savedCategory();
        var id = categoryToUpdate.getId();

        BDDMockito.when(categoryRepository.findById(id)).thenReturn(Optional.of(categoryToUpdate));

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + "/{id}", id)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("PUT v1/category/1 returns bad request when fields are not valid")
    @Order(8)
    @WithMockUser(roles = "ADMIN")
    void updateCategory_ReturnsBadRequest_WhenFieldsAreNotValid() throws Exception {
        var request = fileUtils.readResourceFile("category/put-request-category-blank-field-400.json");
        var response = fileUtils.readResourceFile("category/put-response-category-blank-field-400.json");

        var categoryToUpdate = categoryUtils.savedCategory();
        var id = categoryToUpdate.getId();

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
    @DisplayName("DELETE v1/category/1 removes a category")
    @Order(9)
    @WithMockUser(roles = "ADMIN")
    void deleteCategory_RemovesCategory_WhenSuccessful() throws Exception {
        var categoryToDelete = categoryUtils.savedCategory();
        var id = categoryToDelete.getId();

        BDDMockito.when(categoryRepository.findById(id)).thenReturn(Optional.of(categoryToDelete));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE v1/category/1 returns not found when category is not found")
    @Order(10)
    @WithMockUser(roles = "ADMIN")
    void deleteCategory_ReturnsNotFound_WhenCategoryNotFound() throws Exception {
        var response = fileUtils.readResourceFile("category/delete-response-category-404.json");

        var id = 1L;

        BDDMockito.when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL + "/{id}", id))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response))
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE));
    }

}
