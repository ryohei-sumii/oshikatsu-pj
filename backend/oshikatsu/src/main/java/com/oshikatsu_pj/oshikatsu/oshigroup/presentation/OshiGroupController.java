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

import java.util.List;

@RestController
@RequestMapping("/api/oshi-groups")
public class OshiGroupController {

    private final OshiGroupService oshiGroupService;

    public OshiGroupController(OshiGroupService oshiGroupService) {
        this.oshiGroupService = oshiGroupService;
    }

    /**
     * 推しグループの作成
     */
    @PostMapping("/create")
    public ResponseEntity<OshiGroupResponse> create(
            @Valid @RequestBody CreateOshiGroupRequest request,
            Authentication authentication) {

        Long userId = ((CustomAuthenticationToken) authentication).getUserId();

        OshiGroupResponse response = oshiGroupService.createOshiGroup(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * グループ名での検索（完全一致またはあいまい検索）
     */
    @GetMapping("/list-group")
    public ResponseEntity<?> findByGroupName(
            @RequestParam(name = "full") boolean full,
            @RequestParam(name = "fuzzy") boolean fuzzy,
            @RequestParam(name = "groupName") String groupName,
            Authentication authentication) {

        Long userId = ((CustomAuthenticationToken) authentication).getUserId();

        // ラジオボタンの選択状態の整合性確認
        if ((full && fuzzy) || (!full && !fuzzy)) {
            throw new IllegalArgumentException("全文一致かあいまい検索どちらかをチェックしてください。");
        }

        if (full) {
            // 完全一致の場合は単一のレスポンスを返す
            OshiGroupResponse response = oshiGroupService.findByNameFullMatch(groupName, userId);
            return ResponseEntity.ok(response);
        } else {
            // あいまい検索の場合はリストを返す
            List<OshiGroupResponse> responseList = oshiGroupService.findByNameFuzzy(groupName, userId);
            return ResponseEntity.ok(responseList);
        }
    }

    /**
     * 会社名でグループ一覧取得
     */
    @GetMapping("/list-company")
    public ResponseEntity<List<OshiGroupResponse>> findByCompanyName(
            @RequestParam(name = "company") String company,
            Authentication authentication) {

        Long userId = ((CustomAuthenticationToken) authentication).getUserId();

        List<OshiGroupResponse> responseList = oshiGroupService.findByCompany(company, userId);
        return ResponseEntity.ok(responseList);
    }

    /**
     * グループ情報の更新
     */
    @PostMapping("/update")
    public ResponseEntity<OshiGroupResponse> update(
            @Valid @RequestBody UpdateOshiGroupRequest request,
            Authentication authentication) {

        Long userId = ((CustomAuthenticationToken) authentication).getUserId();

        OshiGroupResponse response = oshiGroupService.update(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * グループの削除（物理削除）
     */
    @DeleteMapping("/delete/{groupId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long groupId,
            Authentication authentication) {

        Long userId = ((CustomAuthenticationToken) authentication).getUserId();

        oshiGroupService.delete(groupId, userId);
        return ResponseEntity.noContent().build();
    }
}
