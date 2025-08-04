package com.TwoSeaU.BaData.domain.rental.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.TwoSeaU.BaData.domain.common.TestSecurityConfig;
import com.TwoSeaU.BaData.domain.rental.dto.request.ReserveDeviceRequest;
import com.TwoSeaU.BaData.domain.rental.dto.request.ReserveRentalRequest;
import com.TwoSeaU.BaData.domain.rental.service.RentalService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
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
        RentalController.class,
        TestSecurityConfig.class
})
@WebMvcTest(controllers = RentalController.class)
class RentalControllerTest {

    private static final String BASE_URL = "/api/v1/rentals";

    @MockBean
    RentalService rentalService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("utf-8", true))
                .apply(springSecurity())
                .build();
    }

    @Nested
    @DisplayName("GET /{storeId}/devices")
    class Describe_getStoreDetailDeviceResponse {

        @Test
        @DisplayName("200 OK 반환")
        void it_returns_200() throws Exception {
            // given
            given(rentalService.getReservationDeviceInfoResponse(any(), any(), eq(1L))).willReturn(List.of());

            // when
            ResultActions result = mockMvc.perform(get(BASE_URL + "/1/devices")
                            .param("rentalStartDate", "2025-08-01T00:00:00")
                            .param("rentalEndDate", "2025-08-02T00:00:00"))
                    .andDo(print());

            // then
            result.andExpect(status().isOk());
            verify(rentalService).getReservationDeviceInfoResponse(any(), any(), eq(1L));
        }
    }

    @Nested
    @DisplayName("POST /devices")
    class Describe_reserveRental {

        @Test
        @WithMockUser(username = "userId")
        @DisplayName("예약 요청에 성공하면 200 OK 반환")
        void it_returns_200() throws Exception {
            // given
            ReserveRentalRequest request = ReserveRentalRequest.builder()
                    .storeDevices(List.of(ReserveDeviceRequest.builder().storeDeviceId(1L)
                            .count(3).build()))
                    .rentalStartDate(LocalDateTime.now().plusDays(1))
                    .rentalEndDate(LocalDateTime.now().plusDays(2))
                    .storeId(3L)
                    .build();

            given(rentalService.reserveRental(any(), eq("userId"))).willReturn(10L);

            // when
            ResultActions result = mockMvc.perform(post(BASE_URL + "/devices")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print());

            // then
            result.andExpect(status().isOk());
            verify(rentalService).reserveRental(any(), eq("userId"));
        }
    }

    @Nested
    @DisplayName("DELETE /{reservationId}")
    class Describe_deleteReservation {

        @Test
        @WithMockUser(username = "userId")
        @DisplayName("예약 삭제 요청에 성공하면 200 OK 반환")
        void it_returns_200() throws Exception {
            // given
            given(rentalService.deleteReserveRental(eq(5L), eq("userId"))).willReturn(5L);

            // when
            ResultActions result = mockMvc.perform(delete(BASE_URL + "/5"))
                    .andDo(print());

            // then
            result.andExpect(status().isOk());
            verify(rentalService).deleteReserveRental(eq(5L), eq("userId"));
        }
    }

    @Nested
    @DisplayName("GET /reservations/{reservationId}/devices")
    class Describe_getReservedDevice {

        @Test
        @WithMockUser(username = "userId")
        @DisplayName("예약된 기기 정보 요청에 성공하면 200 OK 반환")
        void it_returns_200() throws Exception {
            // given
            given(rentalService.getReservedDeviceByReservationId(eq(3L), eq("userId"))).willReturn(null);

            // when
            ResultActions result = mockMvc.perform(get(BASE_URL + "/reservations/3/devices"))
                    .andDo(print());

            // then
            result.andExpect(status().isOk());
            verify(rentalService).getReservedDeviceByReservationId(eq(3L), eq("userId"));
        }
    }
}
