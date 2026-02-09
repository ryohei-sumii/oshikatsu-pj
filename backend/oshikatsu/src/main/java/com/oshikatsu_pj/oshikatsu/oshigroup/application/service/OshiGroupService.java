package com.oshikatsu_pj.oshikatsu.oshigroup.application.service;

import com.oshikatsu_pj.oshikatsu.auth.domain.model.User;
import com.oshikatsu_pj.oshikatsu.auth.domain.repository.UserRepository;
import com.oshikatsu_pj.oshikatsu.oshigroup.application.dto.request.CreateOshiGroupRequest;
import com.oshikatsu_pj.oshikatsu.oshigroup.application.dto.request.UpdateOshiGroupRequest;
import com.oshikatsu_pj.oshikatsu.oshigroup.application.dto.response.OshiGroupResponse;
import com.oshikatsu_pj.oshikatsu.oshigroup.domain.exception.OshiGroupAlreadyExistsException;
import com.oshikatsu_pj.oshikatsu.oshigroup.domain.exception.OshiGroupNotFoundException;
import com.oshikatsu_pj.oshikatsu.oshigroup.domain.model.OshiGroup;
import com.oshikatsu_pj.oshikatsu.oshigroup.domain.repository.OshiGroupRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OshiGroupService {

    private final OshiGroupRepository oshiGroupRepository;
    private final UserRepository userRepository;

    public OshiGroupService(OshiGroupRepository oshiGroupRepository,
                           UserRepository userRepository) {
        this.oshiGroupRepository = oshiGroupRepository;
        this.userRepository = userRepository;
    }

    /**
     * 推しグループを作成
     */
    public OshiGroupResponse createOshiGroup(Long userId, CreateOshiGroupRequest request) {
        // ユーザーの存在確認
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new OshiGroupNotFoundException("ユーザーが見つかりません。"));

        // 推しグループの重複チェック
        if (oshiGroupRepository.existsByGroupName(request.groupName(), userId)){
            throw new OshiGroupAlreadyExistsException(String.format("%sは既に登録されています。", request.groupName()));
        }

        // エンティティの作成
        OshiGroup oshiGroup = new OshiGroup(
                user,
                request.groupName(),
                request.company(),
                request.description()
        );

        // 保存（JpaRepositoryのsaveメソッド）
        OshiGroup savedOshiGroup = oshiGroupRepository.save(oshiGroup);

        // レスポンスを生成
        return new OshiGroupResponse(
                savedOshiGroup.getId(),
                savedOshiGroup.getUser().getId(),
                savedOshiGroup.getGroupName(),
                savedOshiGroup.getCompany(),
                savedOshiGroup.getDescription(),
                savedOshiGroup.getCreatedAt(),
                savedOshiGroup.getUpdatedAt()
        );
    }

    /**
     * グループ名での完全一致検索
     */
    public OshiGroupResponse findByNameFullMatch(String groupName, Long userId) {
        OshiGroup oshiGroup = oshiGroupRepository.findByGroupName(groupName, userId)
                .orElseThrow(() -> new OshiGroupNotFoundException("該当する名称のグループはまだ登録されていません。"));

        return new OshiGroupResponse(
                oshiGroup.getId(),
                oshiGroup.getUser().getId(),
                oshiGroup.getGroupName(),
                oshiGroup.getCompany(),
                oshiGroup.getDescription(),
                oshiGroup.getCreatedAt(),
                oshiGroup.getUpdatedAt()
        );
    }

    /**
     * グループ名でのあいまい検索
     */
    public List<OshiGroupResponse> findByNameFuzzy(String groupName, Long userId) {
        Optional<List<OshiGroup>> oshiGroupOptional = oshiGroupRepository.findByGroupNameFuzzy(groupName, userId);
        if (oshiGroupOptional.isEmpty() || oshiGroupOptional.get().isEmpty()) {
            throw new OshiGroupNotFoundException("該当する名称のグループはまだ登録されていません。");
        }

        // レスポンスを生成
        List<OshiGroupResponse> responses = new ArrayList<>();
        for (OshiGroup oshiGroup : oshiGroupOptional.get()) {
            OshiGroupResponse response = new OshiGroupResponse(
                    oshiGroup.getId(),
                    oshiGroup.getUser().getId(),
                    oshiGroup.getGroupName(),
                    oshiGroup.getCompany(),
                    oshiGroup.getDescription(),
                    oshiGroup.getCreatedAt(),
                    oshiGroup.getUpdatedAt()
            );
            responses.add(response);
        }
        return responses;
    }

    /**
     * ユーザーIDでグループ一覧取得
     */
    public List<OshiGroupResponse> findByUserId(Long userId) {
        Optional<List<OshiGroup>> oshiGroupOptional = oshiGroupRepository.findByUserId(userId);
        if (oshiGroupOptional.isEmpty() || oshiGroupOptional.get().isEmpty()) {
            throw new OshiGroupNotFoundException("該当するグループはまだ登録されていません。");
        }

        // レスポンスを生成
        List<OshiGroupResponse> responses = new ArrayList<>();
        for (OshiGroup oshiGroup : oshiGroupOptional.get()) {
            OshiGroupResponse response = new OshiGroupResponse(
                    oshiGroup.getId(),
                    oshiGroup.getUser().getId(),
                    oshiGroup.getGroupName(),
                    oshiGroup.getCompany(),
                    oshiGroup.getDescription(),
                    oshiGroup.getCreatedAt(),
                    oshiGroup.getUpdatedAt()
            );
            responses.add(response);
        }
        return responses;
    }

    /**
     * 会社名でグループ一覧取得
     */
    public List<OshiGroupResponse> findByCompany(String company, Long userId) {
        Optional<List<OshiGroup>> oshiGroupOptional = oshiGroupRepository.findByCompany(company, userId);
        if (oshiGroupOptional.isEmpty() || oshiGroupOptional.get().isEmpty()) {
            throw new OshiGroupNotFoundException("該当する会社のグループはまだ登録されていません。");
        }

        // レスポンスを生成
        List<OshiGroupResponse> responses = new ArrayList<>();
        for (OshiGroup oshiGroup : oshiGroupOptional.get()) {
            OshiGroupResponse response = new OshiGroupResponse(
                    oshiGroup.getId(),
                    oshiGroup.getUser().getId(),
                    oshiGroup.getGroupName(),
                    oshiGroup.getCompany(),
                    oshiGroup.getDescription(),
                    oshiGroup.getCreatedAt(),
                    oshiGroup.getUpdatedAt()
            );
            responses.add(response);
        }
        return responses;
    }

    /**
     * グループ情報の更新
     */
    public OshiGroupResponse update(Long userId, UpdateOshiGroupRequest request) {
        OshiGroup oshiGroup = oshiGroupRepository.findById(request.groupId())
                .orElseThrow(() -> new OshiGroupNotFoundException("該当のグループが見つかりません。"));

        // 権限チェック（ユーザーIDが一致しているか）
        if (!oshiGroup.getUser().getId().equals(userId)) {
            throw new OshiGroupNotFoundException("該当のグループが見つかりません。");
        }

        // グループ名重複チェック（名前を変更する場合のみ）
        if (!oshiGroup.getGroupName().equals(request.groupName())) {
            if (oshiGroupRepository.existsByGroupName(request.groupName(), userId)) {
                throw new OshiGroupAlreadyExistsException("該当のグループ名は既に登録されています。");
            }
        }

        // グループ情報の更新
        oshiGroup.update(
                request.groupName(),
                request.company(),
                request.description()
        );

        OshiGroup updatedOshiGroup = oshiGroupRepository.save(oshiGroup);

        // レスポンスを生成
        return new OshiGroupResponse(
                updatedOshiGroup.getId(),
                updatedOshiGroup.getUser().getId(),
                updatedOshiGroup.getGroupName(),
                updatedOshiGroup.getCompany(),
                updatedOshiGroup.getDescription(),
                updatedOshiGroup.getCreatedAt(),
                updatedOshiGroup.getUpdatedAt()
        );
    }

    /**
     * グループの削除（物理削除）
     */
    public void delete(Long groupId, Long userId) {
        OshiGroup oshiGroup = oshiGroupRepository.findById(groupId)
                .orElseThrow(() -> new OshiGroupNotFoundException("該当のグループが見つかりません。"));

        // 権限チェック（ユーザーIDが一致しているか）
        if (!oshiGroup.getUser().getId().equals(userId)) {
            throw new OshiGroupNotFoundException("該当のグループが見つかりません。");
        }

        oshiGroupRepository.delete(oshiGroup);
    }
}
