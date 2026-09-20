package com.bagas.pinjam100.controller.dashboard;

import com.bagas.pinjam100.dto.common.BaseResponse;
import com.bagas.pinjam100.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<BaseResponse<?>> dashboard() {
        return ResponseEntity.ok(
                BaseResponse.success(
                        "Data dashboard berhasil ditemukan",
                        dashboardService.dashboard()
                )
        );
    }
}