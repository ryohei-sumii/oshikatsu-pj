package com.oshikatsu_pj.oshikatsu.oshimember.domain.repository;

import com.oshikatsu_pj.oshikatsu.oshimember.domain.model.OshiMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OshiMemberRepository extends JpaRepository<OshiMember, Long> {
    
    // グループID指定でメンバー一覧取得
    @Query("SELECT m FROM OshiMember m WHERE m.oshiGroup.id = :groupId AND m.user.id = :userId")
    Optional<List<OshiMember>> findByGroupId(@Param("groupId") Long groupId, @Param("userId") Long userId);
    
    // メンバー名での完全一致検索
    @Query("SELECT m FROM OshiMember m WHERE m.memberName = :memberName AND m.user.id = :userId")
    Optional<OshiMember> findByMemberName(@Param("memberName") String memberName, @Param("userId") Long userId);
    
    // メンバー名でのあいまい検索
    @Query("SELECT m FROM OshiMember m WHERE m.memberName LIKE %:memberName% AND m.user.id = :userId")
    Optional<List<OshiMember>> findByMemberNameFuzzy(@Param("memberName") String memberName, @Param("userId") Long userId);
    
    // メンバー名の重複チェック（同じグループ内で）
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM OshiMember m WHERE m.memberName = :memberName AND m.oshiGroup.id = :groupId AND m.user.id = :userId")
    boolean existsByMemberName(@Param("memberName") String memberName, @Param("groupId") Long groupId, @Param("userId") Long userId);
    
    // ユーザーIDでメンバー一覧取得
    @Query("SELECT m FROM OshiMember m WHERE m.user.id = :userId")
    Optional<List<OshiMember>> findByUserId(@Param("userId") Long userId);
}
