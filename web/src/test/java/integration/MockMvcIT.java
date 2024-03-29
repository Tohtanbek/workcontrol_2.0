package integration;

import com.tosDev.web.dto.equip.EquipTypeDto;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AllArgsConstructor
@Sql({"/sql/test.sql"})
@Transactional
public class MockMvcIT extends IntegrationTestBase {
    private MockMvc mockMvc;

    @Test
    void testUnauthenticatedAccessDenied() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tables/equip/main"))
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "admin",password = "12345", authorities = {"ADMIN"})
    void checkAdminGetPagesAccessible() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/tables/equip/main"))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/tables/equip/main_table"))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/tables/equip/equip_types"))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/tables/equip/equip_types_array"))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "admin",password = "12345", authorities = {"ADMIN"})
    void checkAdminPostPagesAccessible() throws Exception {
        EquipTypeDto mockedDto = mock(EquipTypeDto.class);
        String json = """
                [
                  {
                    "name": "example1"
                  },
                  {
                    "name": "example2"
                  }
                ]
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/tables/equip/add_equip_type")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "user",password = "12345", authorities = {"USER"})
    void checkUserGetPagesNotAccessible() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/tables/equip/main"))
                .andExpect(status().isForbidden());
        mockMvc.perform(MockMvcRequestBuilders.get("/tables/equip/main_table"))
                .andExpect(status().isForbidden());
        mockMvc.perform(MockMvcRequestBuilders.get("/tables/equip/equip_types"))
                .andExpect(status().isForbidden());
        mockMvc.perform(MockMvcRequestBuilders.get("/tables/equip/equip_types_array"))
                .andExpect(status().isForbidden());
    }
    @Test
    @WithMockUser(username = "user",password = "12345", authorities = {"USER"})
    void checkUserPostPagesNotAccessible() throws Exception {
        EquipTypeDto mockedDto = mock(EquipTypeDto.class);
        String json = """
                [
                  {
                    "name": "example1"
                  },
                  {
                    "name": "example2"
                  }
                ]
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/tables/equip/add_equip_type")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

}
