package com.oshikatsu_pj.oshikatsu.oshigroup.domain.repository;

import com.oshikatsu_pj.oshikatsu.oshigroup.domain.model.OshiGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OshiGroupRepository extends JpaRepository<OshiGroup, Long> {
    // ユーザーIDでグループ一覧取得
    @Query("SELECT g FROM OshiGroup g WHERE g.user.id = :userId")
    Optional<List<OshiGroup>> findByUserId(@Param("userId") Long userId);
    
    // グループ名での完全一致検索
    @Query("SELECT g FROM OshiGroup g WHERE g.groupName = :groupName AND g.user.id = :userId")
    Optional<OshiGroup> findByGroupName(@Param("groupName") String groupName, @Param("userId") Long userId);
    
    // グループ名の重複チェック
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM OshiGroup g WHERE g.groupName = :groupName AND g.user.id = :userId")
    boolean existsByGroupName(@Param("groupName") String groupName, @Param("userId") Long userId);
    
    // 会社名でグループ一覧取得
    @Query("SELECT g FROM OshiGroup g WHERE g.company = :company AND g.user.id = :userId")
    Optional<List<OshiGroup>> findByCompany(@Param("company") String company, @Param("userId") Long userId);
    
    // グループ名でのあいまい検索
    @Query("SELECT g FROM OshiGroup g WHERE g.groupName LIKE %:groupName% AND g.user.id = :userId")
    Optional<List<OshiGroup>> findByGroupNameFuzzy(@Param("groupName") String groupName, @Param("userId") Long userId);
}
