package com.oshikatsu_pj.oshikatsu.oshimember.presentation;

import com.oshikatsu_pj.oshikatsu.oshimember.application.dto.request.CreateOshiMemberRequest;
import com.oshikatsu_pj.oshikatsu.oshimember.application.dto.request.UpdateOshiMemberRequest;
import com.oshikatsu_pj.oshikatsu.oshimember.application.dto.response.OshiMemberResponse;
import com.oshikatsu_pj.oshikatsu.oshimember.application.service.OshiMemberService;
import com.oshikatsu_pj.oshikatsu.security.CustomAuthenticationToken;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/oshi-members")
public class OshiMemberController {

    private final OshiMemberService oshiMemberService;

    public OshiMemberController(OshiMemberService oshiMemberService) {
        this.oshiMemberService = oshiMemberService;
    }

    /**
     * 推しメンバーの作成
     */
    @PostMapping("/create")
    public ResponseEntity<OshiMemberResponse> create(
            @Valid @RequestBody CreateOshiMemberRequest request,
            Authentication authentication) {

        Long userId = ((CustomAuthenticationToken) authentication).getUserId();

        OshiMemberResponse response = oshiMemberService.createOshiMember(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * グループID指定でメンバー一覧取得
     */
    @GetMapping("/list-group")
    public ResponseEntity<List<OshiMemberResponse>> findByGroupId(
            @RequestParam(name = "groupId") Long groupId,
            Authentication authentication) {

        Long userId = ((CustomAuthenticationToken) authentication).getUserId();

        List<OshiMemberResponse> responseList = oshiMemberService.findByGroupId(groupId, userId);
        return ResponseEntity.ok(responseList);
    }

    /**
     * メンバー名での検索（完全一致またはあいまい検索）
     */
    @GetMapping("/list-member")
    public ResponseEntity<?> findByMemberName(
            @RequestParam(name = "full") boolean full,
            @RequestParam(name = "fuzzy") boolean fuzzy,
            @RequestParam(name = "memberName") String memberName,
            Authentication authentication) {

        Long userId = ((CustomAuthenticationToken) authentication).getUserId();

        // ラジオボタンの選択状態の整合性確認
        if ((full && fuzzy) || (!full && !fuzzy)) {
            throw new IllegalArgumentException("全文一致かあいまい検索どちらかをチェックしてください。");
        }

        if (full) {
            // 完全一致の場合は単一のレスポンスを返す
            OshiMemberResponse response = oshiMemberService.findByMemberName(memberName, userId);
            return ResponseEntity.ok(response);
        } else {
            // あいまい検索の場合はリストを返す
            List<OshiMemberResponse> responseList = oshiMemberService.findByMemberNameFuzzy(memberName, userId);
            return ResponseEntity.ok(responseList);
        }
    }

    /**
     * メンバー情報の更新
     */
    @PostMapping("/update")
    public ResponseEntity<OshiMemberResponse> update(
            @Valid @RequestBody UpdateOshiMemberRequest request,
            Authentication authentication) {

        Long userId = ((CustomAuthenticationToken) authentication).getUserId();

        OshiMemberResponse response = oshiMemberService.update(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * メンバーの削除（物理削除）
     */
    @DeleteMapping("/delete/{memberId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long memberId,
            Authentication authentication) {

        Long userId = ((CustomAuthenticationToken) authentication).getUserId();

        oshiMemberService.delete(memberId, userId);
        return ResponseEntity.noContent().build();
    }
}
