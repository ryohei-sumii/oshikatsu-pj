package com.oshikatsu_pj.oshikatsu.oshigroup.application.service;

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

    public OshiGroupService(OshiGroupRepository oshiGroupRepository) {
        this.oshiGroupRepository = oshiGroupRepository;
    }

    public OshiGroupResponse createOshiGroup(Long userId, CreateOshiGroupRequest request) {
        // 推しグループの重複チェック
        if (oshiGroupRepository.existsByGroupName(request.groupName(), userId)){
            throw new OshiGroupAlreadyExistsException(String.format("%sは既に登録されています。", request.groupName()));
        }

        // エンティティの作成
        OshiGroup oshiGroup = new OshiGroup(
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

    public List<OshiGroupResponse> findByNameFullMatch(String groupName, Long userId) {
        Optional<OshiGroup> oshiGroupOptional = oshiGroupRepository.findByGroupName(groupName, userId);
        if (oshiGroupOptional.isEmpty()) {
            throw new OshiGroupNotFoundException("該当する名称のグループはまだ登録されていません。");
        }

        // レスポンスを生成
        List<OshiGroupResponse> responses = new ArrayList<>();
        for(OshiGroup oshiGroup: oshiGroupOptional.stream().toList()) {
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

    public List<OshiGroupResponse> findByNameFuzzy(String groupName, Long userId) {
        Optional<OshiGroup> oshiGroupOptional = oshiGroupRepository.findByGroupNameFuzzy(groupName, userId);
        if (oshiGroupOptional.isEmpty()) {
            throw new OshiGroupNotFoundException("該当する名称のグループはまだ登録されていません。");
        }

        // レスポンスを生成
        List<OshiGroupResponse> responses = new ArrayList<>();
        for(OshiGroup oshiGroup: oshiGroupOptional.stream().toList()) {
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

    public List<OshiGroupResponse> findByUserId(Long userId) {
        Optional<OshiGroup> oshiGroupOptional = oshiGroupRepository.findByUserId(userId);
        if (oshiGroupOptional.isEmpty()) {
            throw new OshiGroupNotFoundException("該当する名称のグループはまだ登録されていません。");
        }

        // レスポンスを生成
        List<OshiGroupResponse> responses = new ArrayList<>();
        for(OshiGroup oshiGroup: oshiGroupOptional.stream().toList()) {
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

    public List<OshiGroupResponse> findByCompany(String company, Long userId) {
        Optional<OshiGroup> oshiGroupOptional = oshiGroupRepository.findByCompany(company, userId);
        if (oshiGroupOptional.isEmpty()) {
            throw new OshiGroupNotFoundException("該当する名称のグループはまだ登録されていません。");
        }

        // レスポンスを生成
        List<OshiGroupResponse> responses = new ArrayList<>();
        for(OshiGroup oshiGroup: oshiGroupOptional.stream().toList()) {
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

    public OshiGroupResponse update(Long userId, UpdateOshiGroupRequest request){
        OshiGroup oshiGroup = oshiGroupRepository.findById(request.groupId())
                .orElseThrow(() -> new OshiGroupNotFoundException("該当のグループが見つかりません。"));

        // グループ名重複チェック
        if (!oshiGroup.getGroupName().equals(request.groupName())) {
            if (oshiGroupRepository.existsByGroupName(request.groupName(), userId)) {
                throw new OshiGroupAlreadyExistsException("該当のグループ名は既に登録されています。");
            }

            oshiGroupRepository.save(oshiGroup);
        }

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
}
