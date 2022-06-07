package com.wcc.geographicdistance.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.geographicdistance.domain.DistanceBetweenLocations;
import com.wcc.geographicdistance.domain.Postcode;
import com.wcc.geographicdistance.repo.PostcodeRepo;
import com.wcc.geographicdistance.service.PostcodeService;
import com.wcc.geographicdistance.service.PostcodeServiceImpl;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
class PostcodeResourceTest {

    private static PostcodeService postcodeService;
    @Mock
    private static PostcodeRepo postcodeRepo;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    private static Postcode postcode1;
    private static Postcode postcode2;
    private static Postcode postcode3;
    private static Postcode postcode4;
    private static Postcode postcode5;
    private static DistanceBetweenLocations distance;

    @BeforeAll
    static void beforeAll() {
        postcode1 = new Postcode(2693957L,"YO7 3FN",54.224961,-1.354025);
        postcode2 = new Postcode(2693958L,"YO7 3FP",54.224226,-1.354328);
        postcode3 = new Postcode(2693959L,"YO7 3FQ",54.223396,-1.357010);
        postcode4 = new Postcode(5L,"AB11 6UL",null,null);
        postcode5 = new Postcode(6L,"AB11 8RQ",null,null);
        distance = new DistanceBetweenLocations();
        postcodeService = new PostcodeServiceImpl(postcodeRepo);
    }

    @Test
    @Order(1)
    void unauthorizedRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/geo-distance/login"))
                .andExpect(MockMvcResultMatchers.status().is(401));
    }

    @Test
    @WithMockUser(username = "wcc_user")
    @Order(2)
    void forbiddenRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/geo-distance/postcodes/save")
                )
                .andExpect(MockMvcResultMatchers.status().is(403));
    }

    @Test
    @WithMockUser(username = "wcc_user")
    @Order(3)
    void getPostcodes() throws Exception {
        // given
        // when
        ResultActions response =mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/geo-distance/postcodes"));

        // then
        response.andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(95)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].postcode").value("AB11 6UL"));
    }

    @Test
    @WithMockUser(username = "wcc_admin",roles = {"ADMIN"})
    @Order(4)
    void savePostcodes() throws Exception {
        // given
        List<Postcode> postcodes = Arrays.asList(postcode1, postcode2, postcode3);
        String postcodesAsJson = mapper.writeValueAsString(postcodes);
        // when
        ResultActions response =mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/geo-distance/postcodes/save")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(postcodesAsJson));
        // then
        response.andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(98)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[95].postcode").value(postcode1.getPostcode()));
    }

    @Test
    @WithMockUser(username = "wcc_admin",roles = {"ADMIN"})
    @Order(5)
    void savePostcode() throws Exception {
        // given
        String postcodeAsJson = mapper.writeValueAsString(postcode1);
        // when
        ResultActions response =mockMvc.perform(MockMvcRequestBuilders
                .post("/v1/geo-distance/postcode/save")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(postcodeAsJson));
        // then
        response.andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.jsonPath("$.postcode").value(postcode1.getPostcode()));
    }

    @Test
    @WithMockUser(username = "wcc_user")
    @Order(6)
    void getDistanceBetweenPostcodes() throws Exception {
        // given
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("postcodeA",postcode4.getPostcode());
        requestMap.put("postcodeB",postcode5.getPostcode());
        String requestJson = mapper.writeValueAsString(requestMap);

        // when

        ResultActions response =mockMvc.perform(MockMvcRequestBuilders
                .get("/v1/geo-distance/postcodes/distance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON));
        // then
        response.andDo(print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.distanceBetweenLocations")
                        .value(Matchers.is(2.4397590195445047)));
    }
}