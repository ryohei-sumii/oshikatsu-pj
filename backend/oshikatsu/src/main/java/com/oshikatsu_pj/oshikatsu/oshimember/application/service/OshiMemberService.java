package com.oshikatsu_pj.oshikatsu.oshimember.application.service;

import com.oshikatsu_pj.oshikatsu.auth.domain.model.User;
import com.oshikatsu_pj.oshikatsu.auth.domain.repository.UserRepository;
import com.oshikatsu_pj.oshikatsu.oshigroup.domain.exception.OshiGroupNotFoundException;
import com.oshikatsu_pj.oshikatsu.oshigroup.domain.model.OshiGroup;
import com.oshikatsu_pj.oshikatsu.oshigroup.domain.repository.OshiGroupRepository;
import com.oshikatsu_pj.oshikatsu.oshimember.application.dto.request.CreateOshiMemberRequest;
import com.oshikatsu_pj.oshikatsu.oshimember.application.dto.request.UpdateOshiMemberRequest;
import com.oshikatsu_pj.oshikatsu.oshimember.application.dto.response.OshiMemberResponse;
import com.oshikatsu_pj.oshikatsu.oshimember.domain.exception.OshiMemberAlreadyExistsException;
import com.oshikatsu_pj.oshikatsu.oshimember.domain.exception.OshiMemberNotFoundException;
import com.oshikatsu_pj.oshikatsu.oshimember.domain.model.OshiMember;
import com.oshikatsu_pj.oshikatsu.oshimember.domain.repository.OshiMemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OshiMemberService {

    private final OshiMemberRepository oshiMemberRepository;
    private final OshiGroupRepository oshiGroupRepository;
    private final UserRepository userRepository;

    public OshiMemberService(OshiMemberRepository oshiMemberRepository,
                             OshiGroupRepository oshiGroupRepository,
                             UserRepository userRepository) {
        this.oshiMemberRepository = oshiMemberRepository;
        this.oshiGroupRepository = oshiGroupRepository;
        this.userRepository = userRepository;
    }

    /**
     * 推しメンバーを作成
     */
    public OshiMemberResponse createOshiMember(Long userId, CreateOshiMemberRequest request) {
        // ユーザーの存在確認
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new OshiMemberNotFoundException("ユーザーが見つかりません。"));

        // グループの存在確認
        OshiGroup oshiGroup = oshiGroupRepository.findById(request.groupId())
                .orElseThrow(() -> new OshiGroupNotFoundException("指定されたグループが見つかりません。"));

        // メンバー名の重複チェック（同じグループ内で）
        if (oshiMemberRepository.existsByMemberName(request.memberName(), request.groupId(), userId)) {
            throw new OshiMemberAlreadyExistsException(
                    String.format("%sは既に登録されています。", request.memberName()));
        }

        // エンティティの作成
        OshiMember oshiMember = new OshiMember(
                user,
                oshiGroup,
                request.memberName(),
                request.memberNameKana(),
                request.gender(),
                request.birthDay()
        );

        // 保存
        OshiMember savedOshiMember = oshiMemberRepository.save(oshiMember);

        // レスポンスを生成
        return new OshiMemberResponse(
                savedOshiMember.getId(),
                savedOshiMember.getUser().getId(),
                savedOshiMember.getOshiGroup().getId(),
                savedOshiMember.getOshiGroup().getGroupName(),
                savedOshiMember.getMemberName(),
                savedOshiMember.getMemberNameKana(),
                savedOshiMember.getGender(),
                savedOshiMember.getBirthDay(),
                savedOshiMember.getCreatedAt(),
                savedOshiMember.getUpdatedAt()
        );
    }

    /**
     * グループID指定でメンバー一覧取得
     */
    public List<OshiMemberResponse> findByGroupId(Long groupId, Long userId) {
        Optional<List<OshiMember>> oshiMemberOptional = oshiMemberRepository.findByGroupId(groupId, userId);
        if (oshiMemberOptional.isEmpty() || oshiMemberOptional.get().isEmpty()) {
            throw new OshiMemberNotFoundException("該当するメンバーが見つかりません。");
        }

        // レスポンスを生成
        List<OshiMemberResponse> responses = new ArrayList<>();
        for (OshiMember oshiMember : oshiMemberOptional.get()) {
            OshiMemberResponse response = new OshiMemberResponse(
                    oshiMember.getId(),
                    oshiMember.getUser().getId(),
                    oshiMember.getOshiGroup().getId(),
                    oshiMember.getOshiGroup().getGroupName(),
                    oshiMember.getMemberName(),
                    oshiMember.getMemberNameKana(),
                    oshiMember.getGender(),
                    oshiMember.getBirthDay(),
                    oshiMember.getCreatedAt(),
                    oshiMember.getUpdatedAt()
            );
            responses.add(response);
        }
        return responses;
    }

    /**
     * メンバー名での完全一致検索
     */
    public OshiMemberResponse findByMemberName(String memberName, Long userId) {
        OshiMember oshiMember = oshiMemberRepository.findByMemberName(memberName, userId)
                .orElseThrow(() -> new OshiMemberNotFoundException("該当する名前のメンバーが見つかりません。"));

        return new OshiMemberResponse(
                oshiMember.getId(),
                oshiMember.getUser().getId(),
                oshiMember.getOshiGroup().getId(),
                oshiMember.getOshiGroup().getGroupName(),
                oshiMember.getMemberName(),
                oshiMember.getMemberNameKana(),
                oshiMember.getGender(),
                oshiMember.getBirthDay(),
                oshiMember.getCreatedAt(),
                oshiMember.getUpdatedAt()
        );
    }

    /**
     * メンバー名でのあいまい検索
     */
    public List<OshiMemberResponse> findByMemberNameFuzzy(String memberName, Long userId) {
        Optional<List<OshiMember>> oshiMemberOptional = oshiMemberRepository.findByMemberNameFuzzy(memberName, userId);
        if (oshiMemberOptional.isEmpty() || oshiMemberOptional.get().isEmpty()) {
            throw new OshiMemberNotFoundException("該当する名前のメンバーが見つかりません。");
        }

        // レスポンスを生成
        List<OshiMemberResponse> responses = new ArrayList<>();
        for (OshiMember oshiMember : oshiMemberOptional.get()) {
            OshiMemberResponse response = new OshiMemberResponse(
                    oshiMember.getId(),
                    oshiMember.getUser().getId(),
                    oshiMember.getOshiGroup().getId(),
                    oshiMember.getOshiGroup().getGroupName(),
                    oshiMember.getMemberName(),
                    oshiMember.getMemberNameKana(),
                    oshiMember.getGender(),
                    oshiMember.getBirthDay(),
                    oshiMember.getCreatedAt(),
                    oshiMember.getUpdatedAt()
            );
            responses.add(response);
        }
        return responses;
    }

    /**
     * メンバー情報の更新
     */
    public OshiMemberResponse update(Long userId, UpdateOshiMemberRequest request) {
        OshiMember oshiMember = oshiMemberRepository.findById(request.memberId())
                .orElseThrow(() -> new OshiMemberNotFoundException("該当のメンバーが見つかりません。"));

        // 権限チェック（ユーザーIDが一致しているか）
        if (!oshiMember.getUser().getId().equals(userId)) {
            throw new OshiMemberNotFoundException("該当のメンバーが見つかりません。");
        }

        // メンバー名重複チェック（名前を変更する場合のみ）
        if (!oshiMember.getMemberName().equals(request.memberName())) {
            if (oshiMemberRepository.existsByMemberName(
                    request.memberName(), oshiMember.getOshiGroup().getId(), userId)) {
                throw new OshiMemberAlreadyExistsException("該当のメンバー名は既に登録されています。");
            }
        }

        // メンバー情報の更新
        oshiMember.update(
                request.memberName(),
                request.memberNameKana(),
                request.gender(),
                request.birthDay()
        );

        OshiMember updatedOshiMember = oshiMemberRepository.save(oshiMember);

        // レスポンスを生成
        return new OshiMemberResponse(
                updatedOshiMember.getId(),
                updatedOshiMember.getUser().getId(),
                updatedOshiMember.getOshiGroup().getId(),
                updatedOshiMember.getOshiGroup().getGroupName(),
                updatedOshiMember.getMemberName(),
                updatedOshiMember.getMemberNameKana(),
                updatedOshiMember.getGender(),
                updatedOshiMember.getBirthDay(),
                updatedOshiMember.getCreatedAt(),
                updatedOshiMember.getUpdatedAt()
        );
    }

    /**
     * メンバーの削除（物理削除）
     */
    public void delete(Long memberId, Long userId) {
        OshiMember oshiMember = oshiMemberRepository.findById(memberId)
                .orElseThrow(() -> new OshiMemberNotFoundException("該当のメンバーが見つかりません。"));

        // 権限チェック（ユーザーIDが一致しているか）
        if (!oshiMember.getUser().getId().equals(userId)) {
            throw new OshiMemberNotFoundException("該当のメンバーが見つかりません。");
        }

        oshiMemberRepository.delete(oshiMember);
    }
}
