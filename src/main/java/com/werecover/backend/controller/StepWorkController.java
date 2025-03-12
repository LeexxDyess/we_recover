package com.werecover.backend.controller;

import com.werecover.backend.model.StepWork;
import com.werecover.backend.service.StepWorkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/step-work")
public class StepWorkController {
    private final StepWorkService stepWorkService;

    public StepWorkController(StepWorkService stepWorkService) {
        this.stepWorkService = stepWorkService;
    }

    @PostMapping("/assign")
    public ResponseEntity<StepWork> assignStepWork(@RequestBody Map<String, Object> request) {
        Long sponsorId = ((Number) request.get("sponsorId")).longValue();
        Long sponseeId = ((Number) request.get("sponseeId")).longValue();
        int stepNumber = ((Number) request.get("stepNumber")).intValue();
        String instructions = (String) request.get("instructions");

        StepWork stepWork = stepWorkService.assignStepWork(sponsorId, sponseeId, stepNumber, instructions);
        return ResponseEntity.ok(stepWork);
    }

    @GetMapping("/sponsee/{sponseeId}")
    public ResponseEntity<List<StepWork>> getSponseeStepWork(@PathVariable Long sponseeId) {
        return ResponseEntity.ok(stepWorkService.getSponseeStepWork(sponseeId));
    }

    @PutMapping("/{stepWorkId}/submit")
    public ResponseEntity<StepWork> submitStepWorkResponse(@PathVariable Long stepWorkId, @RequestBody Map<String, String> request) {
        String response = request.get("response");
        StepWork updatedStepWork = stepWorkService.submitStepWorkResponse(stepWorkId, response);
        return ResponseEntity.ok(updatedStepWork);
    }
}
