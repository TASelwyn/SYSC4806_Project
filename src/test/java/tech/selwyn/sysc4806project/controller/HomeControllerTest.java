package tech.selwyn.sysc4806project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tech.selwyn.sysc4806project.model.Book;
import tech.selwyn.sysc4806project.service.CatalogService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CatalogService catalogService;

    @Test
    void homepageShowsAtMostFourFeaturedBooks() throws Exception {
        List<Book> sixBooks = IntStream.rangeClosed(1, 6)
                .mapToObj(i -> Book.builder()
                        .isbn("000000000000" + i).title("Book " + i).author("Author")
                        .price(new BigDecimal("10.00")).stockQuantity(1)
                        .build())
                .toList();
        when(catalogService.getAllBooks()).thenReturn(sixBooks);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("books", hasSize(4)));
    }
}
