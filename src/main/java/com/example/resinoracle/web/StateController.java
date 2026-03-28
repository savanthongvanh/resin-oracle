package com.example.resinoracle.web;

import com.example.resinoracle.dto.StateResponse;
import com.example.resinoracle.service.StateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StateController {

    private final StateService stateService;

    public StateController(StateService stateService) {
        this.stateService = stateService;
    }

    @GetMapping("/api/v1/states/{abbreviation}")
    public StateResponse getState(@PathVariable("abbreviation") String abbreviation) {
        return StateResponse.from(stateService.getState(abbreviation));
    }
}

