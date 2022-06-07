package com.wcc.geographicdistance.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.geographicdistance.domain.Role;
import com.wcc.geographicdistance.domain.RoleToUserForm;
import com.wcc.geographicdistance.domain.User;
import com.wcc.geographicdistance.service.PostcodeService;
import com.wcc.geographicdistance.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserResourceTest {

    @MockBean
    private UserService userService;
    @MockBean
    private PostcodeService postcodeService;
    @MockBean
    UserDetailsService userDetailsService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    private static User user1;
    private static User user2;
    private static Role role1;
    private static RoleToUserForm roleToUserForm;

    @BeforeAll
    static void beforeAll() {
        user1 = new User(1L,"WCC User","wcc_user","user",new ArrayList<>());
        user2 = new User(2L,"WCC Admin","wcc_admin","admin",new ArrayList<>());
        role1 = new Role(1L, "ADMIN");
        roleToUserForm = new RoleToUserForm();
    }

    @Test
    void unauthorizedRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/geo-distance/login"))
                .andExpect(MockMvcResultMatchers.status().is(401));
    }

    @Test
    @WithMockUser(username = "wcc_user")
    void forbiddenRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/geo-distance/management/user/save")
                )
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    @Test
    @WithMockUser(username = "wcc_admin",roles = {"ADMIN"})
    void getUsers() throws Exception {
        // given user1 and user2
        // when
        when(userService.getUsers()).thenReturn(Arrays.asList(user1,user2));
        ResultActions response =mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/geo-distance/management/users"));

        // then
        response.andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("WCC User"));
    }

    @Test
    @WithMockUser(username = "wcc_admin",roles = {"ADMIN"})
    void saveUser() throws Exception {
        // given
        String userAsJson = mapper.writeValueAsString(user1);
        // when
        when(userService.saveUser(any(User.class))).thenReturn(user1);
        ResultActions response =mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/geo-distance/management/user/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(userAsJson));
        // then
        response.andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("WCC User"));
    }

    @Test
    @WithMockUser(username = "wcc_admin",roles = {"ADMIN"})
    void saveRole() throws Exception {
        // given
        String roleAsJson = mapper.writeValueAsString(role1);
        // when
        when(userService.saveRole(any(Role.class))).thenReturn(role1);
        ResultActions response =mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/geo-distance/management/role/save")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(roleAsJson));
        // then
        response.andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("ADMIN"));
    }

    @Test
    @WithMockUser(username = "wcc_admin",roles = {"ADMIN"})
    void addRoleToUser() throws Exception {
        // given
        roleToUserForm.setRoleName("ROLE_ADMIN");
        roleToUserForm.setUsername("wcc_admin");
        String formAsJson = mapper.writeValueAsString(roleToUserForm);
        // when
        ResultActions response =mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/geo-distance/management/role/add-to-user")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(formAsJson));
        // then
        response.andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200));
    }
}