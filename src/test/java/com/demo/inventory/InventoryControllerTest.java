package com.demo.inventory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

// The store is a singleton bean, so without this one test's restock leaks into
// the next test's assertion about empty stock -- and which one runs first is
// not something JUnit promises.
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class InventoryControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void healthIsOk() throws Exception {
        mvc.perform(get("/health"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void listsEveryItem() throws Exception {
        mvc.perform(get("/items"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.items.length()").value(3));
    }

    @Test
    void anUnknownItemIsA404() throws Exception {
        mvc.perform(get("/items/4242")).andExpect(status().isNotFound());
    }

    @Test
    void reservingTooMuchIsAConflict() throws Exception {
        mvc.perform(post("/items/3/reserve")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"amount\": 1}"))
           .andExpect(status().isConflict());
    }

    @Test
    void restockThenReserveWorks() throws Exception {
        mvc.perform(post("/items/3/restock")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"amount\": 4}"))
           .andExpect(status().isOk());

        mvc.perform(post("/items/3/reserve")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"amount\": 2}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.quantity").value(2));
    }
}
