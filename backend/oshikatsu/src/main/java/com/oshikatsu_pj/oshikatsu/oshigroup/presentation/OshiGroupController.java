package com.oshikatsu_pj.oshikatsu.oshigroup.presentation;

import com.oshikatsu_pj.oshikatsu.oshigroup.application.dto.request.CreateOshiGroupRequest;
import com.oshikatsu_pj.oshikatsu.oshigroup.application.dto.request.UpdateOshiGroupRequest;
import com.oshikatsu_pj.oshikatsu.oshigroup.application.dto.response.OshiGroupResponse;
import com.oshikatsu_pj.oshikatsu.oshigroup.application.service.OshiGroupService;
import com.oshikatsu_pj.oshikatsu.security.CustomAuthenticationToken;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/oshi-groups")
public class OshiGroupController {

    private final OshiGroupService oshiGroupService;

    public OshiGroupController(OshiGroupService oshiGroupService) {
        this.oshiGroupService = oshiGroupService;
    }

    @PostMapping("/create")
    public ResponseEntity<OshiGroupResponse> create(
            @Valid @RequestBody CreateOshiGroupRequest request,
            Authentication authentication) {

        Long userId = ((CustomAuthenticationToken) authentication).getUserId();

        OshiGroupResponse response = oshiGroupService.createOshiGroup(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/list-group")
    public ResponseEntity<List<OshiGroupResponse>> findByGroupName(
            @RequestParam(name = "full") boolean full,
            @RequestParam(name = "fuzzy") boolean fuzzy,
            @RequestParam(name = "groupName") String groupName,
            Authentication authentication) {

        Long userId = ((CustomAuthenticationToken) authentication).getUserId();

        // ラジオボタンの選択状態の整合性確認
        if ((full && fuzzy) ||
                (!full && !fuzzy)) {
            throw new IllegalArgumentException("全文一致かあいまい検索どちらかをチェックしてください。");
        }

        List<OshiGroupResponse> responseList;
        if (full) {
            responseList = oshiGroupService.findByNameFullMatch(groupName, userId);
        } else {
            responseList = oshiGroupService.findByNameFuzzy(groupName, userId);
        }
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/list-company")
    public ResponseEntity<List<OshiGroupResponse>> findByCompanyName(
            @RequestParam(name = "company") String company,
            Authentication authentication
    ) {
        Long userId = ((CustomAuthenticationToken) authentication).getUserId();

        List<OshiGroupResponse> responseList = oshiGroupService.findByCompany(company, userId);
        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/update")
    public ResponseEntity<OshiGroupResponse> update(
            @Valid @RequestBody UpdateOshiGroupRequest request,
            Authentication authentication
    ) {
        Long userId = ((CustomAuthenticationToken) authentication).getUserId();
        OshiGroupResponse response = oshiGroupService.update(userId, request);
        return ResponseEntity.ok(response);
    }
}
