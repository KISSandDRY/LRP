package com.lrp.controllers;

import com.lrp.models.Route;
import com.lrp.services.LogisticsOptimizationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/optimization")
@CrossOrigin(origins = "*")
public class OptimizationController {
    private final LogisticsOptimizationService optimizationService;

    public OptimizationController(LogisticsOptimizationService optimizationService) {
        this.optimizationService = optimizationService;
    }

    @PostMapping("/run")
    public List<Route> runOptimization(@RequestParam(defaultValue = "1.0") double fuelPrice) {
        return optimizationService.optimizeAndAssign(fuelPrice);
    }
}
