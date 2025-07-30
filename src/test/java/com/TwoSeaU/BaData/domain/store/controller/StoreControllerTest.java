package com.TwoSeaU.BaData.domain.store.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.TwoSeaU.BaData.domain.common.TestSecurityConfig;
import com.TwoSeaU.BaData.domain.store.service.StoreService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ContextConfiguration(classes = {
        StoreController.class,
        TestSecurityConfig.class
})
@WebMvcTest(controllers = StoreController.class)
class StoreControllerTest {

    private static final String STORE_URL = "/api/v1/stores";

    @MockBean
    StoreService storeService;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void init() {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("utf-8", true))
                .apply(springSecurity())
                .build();
    }

    @Nested
    @DisplayName("정상적인 요청일 경우")
    class Context_with_valid_request {

        @Nested
        @DisplayName("GET /api/v1/stores/map")
        class Describe_getStoreMapResponse {

            @Test
            @DisplayName("유저 인증 포함하여 200 OK를 반환한다")
            @WithMockUser(username = "userId")
            void it_returns_200_ok_with_user() throws Exception {
                // given
                given(storeService.getStoreMapResponse(any(), any(),3)).willReturn(List.of());

                // when
                ResultActions result = mockMvc.perform(get(STORE_URL + "/map")
                                .param("swLat", "37.0")
                                .param("swLng", "127.0")
                                .param("neLat", "38.0")
                                .param("neLng", "128.0")
                                .param("rentalStartDate", "2025-08-01T00:00:00")
                                .param("rentalEndDate", "2025-08-02T00:00:00")
                        )
                        .andDo(print());

                // then
                result.andExpect(status().isOk());
                verify(storeService).getStoreMapResponse(any(), eq("userId"),3);
            }
        }

        @Nested
        @DisplayName("GET /api/v1/stores")
        class Describe_getStoresResponse {

            @Test
            @DisplayName("쿼리 파라미터와 페이징 정보를 받아 200 OK를 반환한다")
            void it_returns_200_ok() throws Exception {
                // given
                given(storeService.getStoresResponse(any(), any(), any())).willReturn(null);

                // when
                ResultActions result = mockMvc.perform(get(STORE_URL)
                                .param("swLat", "37.0")
                                .param("swLng", "127.0")
                                .param("neLat", "38.0")
                                .param("neLng", "128.0")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "distance,asc")
                        )
                        .andDo(print());

                // then
                result.andExpect(status().isOk());
                verify(storeService).getStoresResponse(any(), any(), any());
            }
        }

        @Nested
        @DisplayName("GET /api/v1/stores/{storeId}/devices")
        class Describe_getStoreDeviceResponse {

            @Test
            @DisplayName("매장 ID와 검색 조건으로 기기 목록 조회에 성공하고 200 OK를 반환한다")
            void it_returns_200_ok() throws Exception {
                // given
                given(storeService.getStoreDeviceResponse(any(), eq(1L))).willReturn(List.of());

                // when
                ResultActions result = mockMvc.perform(get(STORE_URL + "/1/devices")
                                .param("rentalStartDate", "2025-08-01T00:00:00")
                                .param("rentalEndDate", "2025-08-02T00:00:00")
                        )
                        .andDo(print());

                // then
                result.andExpect(status().isOk());
                verify(storeService).getStoreDeviceResponse(any(), eq(1L));
            }
        }
    }



}