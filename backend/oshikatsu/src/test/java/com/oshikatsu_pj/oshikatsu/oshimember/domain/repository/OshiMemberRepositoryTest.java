package com.oshikatsu_pj.oshikatsu.oshimember.domain.repository;

import com.oshikatsu_pj.oshikatsu.auth.domain.model.User;
import com.oshikatsu_pj.oshikatsu.auth.domain.repository.UserRepository;
import com.oshikatsu_pj.oshikatsu.oshigroup.domain.model.OshiGroup;
import com.oshikatsu_pj.oshikatsu.oshigroup.domain.repository.OshiGroupRepository;
import com.oshikatsu_pj.oshikatsu.oshimember.domain.model.OshiMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("OshiMemberRepository 統合テスト")
class OshiMemberRepositoryTest {

    @Autowired
    private OshiMemberRepository oshiMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OshiGroupRepository oshiGroupRepository;

    private User testUser1;
    private User testUser2;
    private OshiGroup testGroup1;
    private OshiGroup testGroup2;

    @BeforeEach
    void setUp() {
        // テストデータのクリーンアップ
        oshiMemberRepository.deleteAll();
        oshiGroupRepository.deleteAll();
        userRepository.deleteAll();

        // テスト用ユーザーの作成
        testUser1 = new User("user1", "user1@example.com", "password123");
        testUser1 = userRepository.save(testUser1);

        testUser2 = new User("user2", "user2@example.com", "password456");
        testUser2 = userRepository.save(testUser2);

        // テスト用グループの作成
        testGroup1 = new OshiGroup(testUser1, "グループ1", "事務所1", "説明1");
        testGroup1 = oshiGroupRepository.save(testGroup1);

        testGroup2 = new OshiGroup(testUser1, "グループ2", "事務所2", "説明2");
        testGroup2 = oshiGroupRepository.save(testGroup2);
    }

    @Test
    @DisplayName("メンバーの保存と取得")
    void saveAndFindById() {
        // Given
        OshiMember member = new OshiMember(
                testUser1,
                testGroup1,
                "メンバー1",
                "メンバー1",
                (byte) 0,
                LocalDate.of(2000, 1, 1)
        );

        // When
        OshiMember savedMember = oshiMemberRepository.save(member);
        Optional<OshiMember> foundMember = oshiMemberRepository.findById(savedMember.getId());

        // Then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getMemberName()).isEqualTo("メンバー1");
        assertThat(foundMember.get().getUser().getId()).isEqualTo(testUser1.getId());
        assertThat(foundMember.get().getOshiGroup().getId()).isEqualTo(testGroup1.getId());
    }

    @Test
    @DisplayName("グループIDでメンバー一覧を取得")
    void findByGroupId() {
        // Given
        OshiMember member1 = new OshiMember(
                testUser1,
                testGroup1,
                "メンバー1",
                "メンバー1",
                (byte) 0,
                LocalDate.of(2000, 1, 1)
        );
        OshiMember member2 = new OshiMember(
                testUser1,
                testGroup1,
                "メンバー2",
                "メンバー2",
                (byte) 1,
                LocalDate.of(2001, 2, 2)
        );
        OshiMember member3 = new OshiMember(
                testUser1,
                testGroup2,
                "メンバー3",
                "メンバー3",
                (byte) 0,
                LocalDate.of(2002, 3, 3)
        );

        oshiMemberRepository.save(member1);
        oshiMemberRepository.save(member2);
        oshiMemberRepository.save(member3);

        // When
        Optional<List<OshiMember>> result = oshiMemberRepository.findByGroupId(
                testGroup1.getId(),
                testUser1.getId()
        );

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(2);
        assertThat(result.get()).extracting(OshiMember::getMemberName)
                .containsExactlyInAnyOrder("メンバー1", "メンバー2");
    }

    @Test
    @DisplayName("メンバー名で完全一致検索")
    void findByMemberName() {
        // Given
        OshiMember member = new OshiMember(
                testUser1,
                testGroup1,
                "特定のメンバー",
                "トクテイノメンバー",
                (byte) 0,
                LocalDate.of(2000, 1, 1)
        );
        oshiMemberRepository.save(member);

        // When
        Optional<OshiMember> result = oshiMemberRepository.findByMemberName(
                "特定のメンバー",
                testUser1.getId()
        );

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getMemberName()).isEqualTo("特定のメンバー");
    }

    @Test
    @DisplayName("メンバー名でのあいまい検索")
    void findByMemberNameFuzzy() {
        // Given
        OshiMember member1 = new OshiMember(
                testUser1,
                testGroup1,
                "山田太郎",
                "ヤマダタロウ",
                (byte) 0,
                LocalDate.of(2000, 1, 1)
        );
        OshiMember member2 = new OshiMember(
                testUser1,
                testGroup1,
                "山田花子",
                "ヤマダハナコ",
                (byte) 1,
                LocalDate.of(2001, 2, 2)
        );
        OshiMember member3 = new OshiMember(
                testUser1,
                testGroup1,
                "田中太郎",
                "タナカタロウ",
                (byte) 0,
                LocalDate.of(2002, 3, 3)
        );

        oshiMemberRepository.save(member1);
        oshiMemberRepository.save(member2);
        oshiMemberRepository.save(member3);

        // When
        Optional<List<OshiMember>> result = oshiMemberRepository.findByMemberNameFuzzy(
                "山田",
                testUser1.getId()
        );

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(2);
        assertThat(result.get()).extracting(OshiMember::getMemberName)
                .containsExactlyInAnyOrder("山田太郎", "山田花子");
    }

    @Test
    @DisplayName("メンバー名の重複チェック - 存在する場合")
    void existsByMemberName_Exists() {
        // Given
        OshiMember member = new OshiMember(
                testUser1,
                testGroup1,
                "既存のメンバー",
                "キゾンノメンバー",
                (byte) 0,
                LocalDate.of(2000, 1, 1)
        );
        oshiMemberRepository.save(member);

        // When
        boolean exists = oshiMemberRepository.existsByMemberName(
                "既存のメンバー",
                testGroup1.getId(),
                testUser1.getId()
        );

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("メンバー名の重複チェック - 存在しない場合")
    void existsByMemberName_NotExists() {
        // When
        boolean exists = oshiMemberRepository.existsByMemberName(
                "存在しないメンバー",
                testGroup1.getId(),
                testUser1.getId()
        );

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("ユーザーIDでメンバー一覧を取得")
    void findByUserId() {
        // Given
        OshiMember member1 = new OshiMember(
                testUser1,
                testGroup1,
                "メンバー1",
                "メンバー1",
                (byte) 0,
                LocalDate.of(2000, 1, 1)
        );
        OshiMember member2 = new OshiMember(
                testUser1,
                testGroup2,
                "メンバー2",
                "メンバー2",
                (byte) 1,
                LocalDate.of(2001, 2, 2)
        );
        OshiMember member3 = new OshiMember(
                testUser2,
                testGroup1,
                "メンバー3",
                "メンバー3",
                (byte) 0,
                LocalDate.of(2002, 3, 3)
        );

        oshiMemberRepository.save(member1);
        oshiMemberRepository.save(member2);
        oshiMemberRepository.save(member3);

        // When
        Optional<List<OshiMember>> result = oshiMemberRepository.findByUserId(testUser1.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(2);
        assertThat(result.get()).extracting(OshiMember::getMemberName)
                .containsExactlyInAnyOrder("メンバー1", "メンバー2");
    }

    @Test
    @DisplayName("メンバーの更新")
    void updateMember() {
        // Given
        OshiMember member = new OshiMember(
                testUser1,
                testGroup1,
                "元の名前",
                "モトノナマエ",
                (byte) 0,
                LocalDate.of(2000, 1, 1)
        );
        OshiMember savedMember = oshiMemberRepository.save(member);

        // When
        savedMember.update(
                "新しい名前",
                "アタラシイナマエ",
                (byte) 1,
                LocalDate.of(2001, 2, 2)
        );
        OshiMember updatedMember = oshiMemberRepository.save(savedMember);

        // Then
        Optional<OshiMember> foundMember = oshiMemberRepository.findById(updatedMember.getId());
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getMemberName()).isEqualTo("新しい名前");
        assertThat(foundMember.get().getMemberNameKana()).isEqualTo("アタラシイナマエ");
        assertThat(foundMember.get().getGender()).isEqualTo((byte) 1);
        assertThat(foundMember.get().getBirthDay()).isEqualTo(LocalDate.of(2001, 2, 2));
    }

    @Test
    @DisplayName("メンバーの削除")
    void deleteMember() {
        // Given
        OshiMember member = new OshiMember(
                testUser1,
                testGroup1,
                "削除されるメンバー",
                "サクジョサレルメンバー",
                (byte) 0,
                LocalDate.of(2000, 1, 1)
        );
        OshiMember savedMember = oshiMemberRepository.save(member);
        Long memberId = savedMember.getId();

        // When
        oshiMemberRepository.delete(savedMember);

        // Then
        Optional<OshiMember> foundMember = oshiMemberRepository.findById(memberId);
        assertThat(foundMember).isEmpty();
    }

    @Test
    @DisplayName("異なるユーザーのメンバーは検索されない")
    void findByGroupId_DifferentUser() {
        // Given
        OshiMember member1 = new OshiMember(
                testUser1,
                testGroup1,
                "ユーザー1のメンバー",
                "ユーザー1ノメンバー",
                (byte) 0,
                LocalDate.of(2000, 1, 1)
        );
        OshiMember member2 = new OshiMember(
                testUser2,
                testGroup1,
                "ユーザー2のメンバー",
                "ユーザー2ノメンバー",
                (byte) 1,
                LocalDate.of(2001, 2, 2)
        );

        oshiMemberRepository.save(member1);
        oshiMemberRepository.save(member2);

        // When
        Optional<List<OshiMember>> result1 = oshiMemberRepository.findByGroupId(
                testGroup1.getId(),
                testUser1.getId()
        );
        Optional<List<OshiMember>> result2 = oshiMemberRepository.findByGroupId(
                testGroup1.getId(),
                testUser2.getId()
        );

        // Then
        assertThat(result1).isPresent();
        assertThat(result1.get()).hasSize(1);
        assertThat(result1.get().get(0).getMemberName()).isEqualTo("ユーザー1のメンバー");

        assertThat(result2).isPresent();
        assertThat(result2.get()).hasSize(1);
        assertThat(result2.get().get(0).getMemberName()).isEqualTo("ユーザー2のメンバー");
    }
}
