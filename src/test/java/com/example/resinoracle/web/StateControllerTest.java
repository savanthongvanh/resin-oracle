package com.example.resinoracle.web;

import com.example.resinoracle.domain.StateRecord;
import com.example.resinoracle.service.InvalidStateAbbreviationException;
import com.example.resinoracle.service.StateNotFoundException;
import com.example.resinoracle.service.StateService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StateControllerTest {

    @Test
    void shouldReturnStatePayload() throws Exception {
        StateService stateService = mock(StateService.class);
        when(stateService.getState("TX"))
                .thenReturn(new StateRecord("TX", "Texas", "Austin", "South", "2026-03-27T00:00:00Z"));

        MockMvc mockMvc = buildMockMvc(stateService);

        mockMvc.perform(get("/api/v1/states/TX").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.abbreviation").value("TX"))
                .andExpect(jsonPath("$.name").value("Texas"))
                .andExpect(jsonPath("$.capital").value("Austin"))
                .andExpect(jsonPath("$.region").value("South"));
    }

    @Test
    void shouldReturnBadRequestForInvalidStateCode() throws Exception {
        StateService stateService = mock(StateService.class);
        when(stateService.getState("TEX"))
                .thenThrow(new InvalidStateAbbreviationException("State abbreviation must contain exactly two letters."));

        MockMvc mockMvc = buildMockMvc(stateService);

        mockMvc.perform(get("/api/v1/states/TEX").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_STATE_ABBREVIATION"));
    }

    @Test
    void shouldReturnNotFoundForMissingState() throws Exception {
        StateService stateService = mock(StateService.class);
        when(stateService.getState("ZZ")).thenThrow(new StateNotFoundException("ZZ"));

        MockMvc mockMvc = buildMockMvc(stateService);

        mockMvc.perform(get("/api/v1/states/ZZ").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("STATE_NOT_FOUND"));
    }

    private MockMvc buildMockMvc(StateService stateService) {
        return MockMvcBuilders.standaloneSetup(new StateController(stateService))
                .setControllerAdvice(new StateExceptionHandler())
                .build();
    }
}

