package com.oshikatsu_pj.oshikatsu.oshigroup.domain.repository;

import com.oshikatsu_pj.oshikatsu.auth.domain.model.User;
import com.oshikatsu_pj.oshikatsu.auth.domain.repository.UserRepository;
import com.oshikatsu_pj.oshikatsu.oshigroup.domain.model.OshiGroup;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("OshiGroupRepository 統合テスト")
class OshiGroupRepositoryTest {

    @Autowired
    private OshiGroupRepository oshiGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        // テストデータのクリーンアップ
        oshiGroupRepository.deleteAll();
        userRepository.deleteAll();

        // テスト用ユーザーの作成
        testUser1 = new User("user1", "user1@example.com", "password123");
        testUser1 = userRepository.save(testUser1);

        testUser2 = new User("user2", "user2@example.com", "password456");
        testUser2 = userRepository.save(testUser2);
    }

    @Test
    @DisplayName("グループの保存と取得")
    void saveAndFindById() {
        // Given
        OshiGroup group = new OshiGroup(testUser1, "テストグループ", "テスト事務所", "テスト説明");

        // When
        OshiGroup savedGroup = oshiGroupRepository.save(group);
        Optional<OshiGroup> foundGroup = oshiGroupRepository.findById(savedGroup.getId());

        // Then
        assertThat(foundGroup).isPresent();
        assertThat(foundGroup.get().getGroupName()).isEqualTo("テストグループ");
        assertThat(foundGroup.get().getCompany()).isEqualTo("テスト事務所");
        assertThat(foundGroup.get().getDescription()).isEqualTo("テスト説明");
    }

    @Test
    @DisplayName("グループの更新")
    void updateGroup() {
        // Given
        OshiGroup group = new OshiGroup(testUser1, "元のグループ名", "元の事務所", "元の説明");
        OshiGroup savedGroup = oshiGroupRepository.save(group);

        // When
        savedGroup.update("新しいグループ名", "新しい事務所", "新しい説明");
        OshiGroup updatedGroup = oshiGroupRepository.save(savedGroup);

        // Then
        Optional<OshiGroup> foundGroup = oshiGroupRepository.findById(updatedGroup.getId());
        assertThat(foundGroup).isPresent();
        assertThat(foundGroup.get().getGroupName()).isEqualTo("新しいグループ名");
        assertThat(foundGroup.get().getCompany()).isEqualTo("新しい事務所");
        assertThat(foundGroup.get().getDescription()).isEqualTo("新しい説明");
    }

    @Test
    @DisplayName("グループの削除")
    void deleteGroup() {
        // Given
        OshiGroup group = new OshiGroup(testUser1, "削除されるグループ", "削除される事務所", "削除される説明");
        OshiGroup savedGroup = oshiGroupRepository.save(group);
        Long groupId = savedGroup.getId();

        // When
        oshiGroupRepository.delete(savedGroup);

        // Then
        Optional<OshiGroup> foundGroup = oshiGroupRepository.findById(groupId);
        assertThat(foundGroup).isEmpty();
    }

    @Test
    @DisplayName("全グループの取得")
    void findAllGroups() {
        // Given
        OshiGroup group1 = new OshiGroup(testUser1, "グループ1", "事務所1", "説明1");
        OshiGroup group2 = new OshiGroup(testUser1, "グループ2", "事務所2", "説明2");
        OshiGroup group3 = new OshiGroup(testUser1, "グループ3", "事務所3", "説明3");

        oshiGroupRepository.save(group1);
        oshiGroupRepository.save(group2);
        oshiGroupRepository.save(group3);

        // When
        var allGroups = oshiGroupRepository.findAll();

        // Then
        assertThat(allGroups).hasSize(3);
    }

    @Test
    @DisplayName("存在確認 - 存在する場合")
    void existsById_True() {
        // Given
        OshiGroup group = new OshiGroup(testUser1, "テストグループ", "テスト事務所", "テスト説明");
        OshiGroup savedGroup = oshiGroupRepository.save(group);

        // When
        boolean exists = oshiGroupRepository.existsById(savedGroup.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("存在確認 - 存在しない場合")
    void existsById_False() {
        // When
        boolean exists = oshiGroupRepository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("グループ数のカウント")
    void countGroups() {
        // Given
        OshiGroup group1 = new OshiGroup(testUser1, "グループ1", "事務所1", "説明1");
        OshiGroup group2 = new OshiGroup(testUser1, "グループ2", "事務所2", "説明2");

        oshiGroupRepository.save(group1);
        oshiGroupRepository.save(group2);

        // When
        long count = oshiGroupRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("複数のグループを一括保存")
    void saveAllGroups() {
        // Given
        OshiGroup group1 = new OshiGroup(testUser1, "グループ1", "事務所1", "説明1");
        OshiGroup group2 = new OshiGroup(testUser1, "グループ2", "事務所2", "説明2");
        OshiGroup group3 = new OshiGroup(testUser1, "グループ3", "事務所3", "説明3");

        // When
        oshiGroupRepository.saveAll(java.util.Arrays.asList(group1, group2, group3));

        // Then
        long count = oshiGroupRepository.count();
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("全グループを削除")
    void deleteAllGroups() {
        // Given
        OshiGroup group1 = new OshiGroup(testUser1, "グループ1", "事務所1", "説明1");
        OshiGroup group2 = new OshiGroup(testUser1, "グループ2", "事務所2", "説明2");

        OshiGroup savedGroup1 = oshiGroupRepository.save(group1);
        OshiGroup savedGroup2 = oshiGroupRepository.save(group2);
        
        // DBに確実に書き込む
        entityManager.flush();
        entityManager.clear();

        // When - deleteAllではなくdeleteAllInBatchを使用（楽観的ロックを回避）
        oshiGroupRepository.deleteAllInBatch();

        // Then
        long count = oshiGroupRepository.count();
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("グループ情報の部分更新")
    void partialUpdateGroup() {
        // Given
        OshiGroup group = new OshiGroup(testUser1, "元のグループ名", "元の事務所", "元の説明");
        OshiGroup savedGroup = oshiGroupRepository.save(group);

        // When - グループ名のみ変更
        savedGroup.update("新しいグループ名", savedGroup.getCompany(), savedGroup.getDescription());
        OshiGroup updatedGroup = oshiGroupRepository.save(savedGroup);

        // Then
        Optional<OshiGroup> foundGroup = oshiGroupRepository.findById(updatedGroup.getId());
        assertThat(foundGroup).isPresent();
        assertThat(foundGroup.get().getGroupName()).isEqualTo("新しいグループ名");
        assertThat(foundGroup.get().getCompany()).isEqualTo("元の事務所");
        assertThat(foundGroup.get().getDescription()).isEqualTo("元の説明");
    }
}
