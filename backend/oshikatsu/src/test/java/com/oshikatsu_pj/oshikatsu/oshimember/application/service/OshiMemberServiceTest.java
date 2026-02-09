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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OshiMemberService ユニットテスト")
class OshiMemberServiceTest {

    @Mock
    private OshiMemberRepository oshiMemberRepository;

    @Mock
    private OshiGroupRepository oshiGroupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OshiMemberService oshiMemberService;

    private User testUser;
    private OshiGroup testGroup;
    private OshiMember testMember;

    @BeforeEach
    void setUp() throws Exception {
        // テスト用のユーザーを作成
        testUser = new User("testuser", "test@example.com", "password123");
        // リフレクションでIDを設定
        var idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(testUser, 1L);

        // テスト用のグループを作成
        testGroup = new OshiGroup("テストグループ", "テスト事務所", "説明");
        var groupIdField = OshiGroup.class.getDeclaredField("id");
        groupIdField.setAccessible(true);
        groupIdField.set(testGroup, 1L);

        // テスト用のメンバーを作成
        testMember = new OshiMember(
                testUser,
                testGroup,
                "テストメンバー",
                "テストメンバー",
                (byte) 0,
                LocalDate.of(2000, 1, 1)
        );
        var memberIdField = OshiMember.class.getDeclaredField("id");
        memberIdField.setAccessible(true);
        memberIdField.set(testMember, 1L);
    }

    @Test
    @DisplayName("推しメンバーの作成 - 正常系")
    void createOshiMember_Success() {
        // Given
        CreateOshiMemberRequest request = new CreateOshiMemberRequest(
                1L,
                "新しいメンバー",
                "アタラシイメンバー",
                (byte) 1,
                LocalDate.of(1995, 5, 15)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(oshiGroupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(oshiMemberRepository.existsByMemberName("新しいメンバー", 1L, 1L)).thenReturn(false);
        when(oshiMemberRepository.save(any(OshiMember.class))).thenReturn(testMember);

        // When
        OshiMemberResponse response = oshiMemberService.createOshiMember(1L, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.memberName()).isEqualTo("テストメンバー");
        verify(userRepository, times(1)).findById(1L);
        verify(oshiGroupRepository, times(1)).findById(1L);
        verify(oshiMemberRepository, times(1)).existsByMemberName("新しいメンバー", 1L, 1L);
        verify(oshiMemberRepository, times(1)).save(any(OshiMember.class));
    }

    @Test
    @DisplayName("推しメンバーの作成 - ユーザーが見つからない場合")
    void createOshiMember_UserNotFound() {
        // Given
        CreateOshiMemberRequest request = new CreateOshiMemberRequest(
                1L,
                "新しいメンバー",
                "アタラシイメンバー",
                (byte) 1,
                LocalDate.of(1995, 5, 15)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> oshiMemberService.createOshiMember(1L, request))
                .isInstanceOf(OshiMemberNotFoundException.class)
                .hasMessage("ユーザーが見つかりません。");

        verify(userRepository, times(1)).findById(1L);
        verify(oshiGroupRepository, never()).findById(anyLong());
        verify(oshiMemberRepository, never()).save(any());
    }

    @Test
    @DisplayName("推しメンバーの作成 - グループが見つからない場合")
    void createOshiMember_GroupNotFound() {
        // Given
        CreateOshiMemberRequest request = new CreateOshiMemberRequest(
                1L,
                "新しいメンバー",
                "アタラシイメンバー",
                (byte) 1,
                LocalDate.of(1995, 5, 15)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(oshiGroupRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> oshiMemberService.createOshiMember(1L, request))
                .isInstanceOf(OshiGroupNotFoundException.class)
                .hasMessage("指定されたグループが見つかりません。");

        verify(oshiGroupRepository, times(1)).findById(1L);
        verify(oshiMemberRepository, never()).save(any());
    }

    @Test
    @DisplayName("推しメンバーの作成 - メンバー名が既に存在する場合")
    void createOshiMember_MemberAlreadyExists() {
        // Given
        CreateOshiMemberRequest request = new CreateOshiMemberRequest(
                1L,
                "既存のメンバー",
                "キゾンノメンバー",
                (byte) 1,
                LocalDate.of(1995, 5, 15)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(oshiGroupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(oshiMemberRepository.existsByMemberName("既存のメンバー", 1L, 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> oshiMemberService.createOshiMember(1L, request))
                .isInstanceOf(OshiMemberAlreadyExistsException.class)
                .hasMessage("既存のメンバーは既に登録されています。");

        verify(oshiMemberRepository, times(1)).existsByMemberName("既存のメンバー", 1L, 1L);
        verify(oshiMemberRepository, never()).save(any());
    }

    @Test
    @DisplayName("グループIDでメンバー一覧取得 - 正常系")
    void findByGroupId_Success() {
        // Given
        List<OshiMember> members = new ArrayList<>();
        members.add(testMember);

        when(oshiMemberRepository.findByGroupId(1L, 1L)).thenReturn(Optional.of(members));

        // When
        List<OshiMemberResponse> responses = oshiMemberService.findByGroupId(1L, 1L);

        // Then
        assertThat(responses).isNotEmpty();
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).memberName()).isEqualTo("テストメンバー");
        verify(oshiMemberRepository, times(1)).findByGroupId(1L, 1L);
    }

    @Test
    @DisplayName("グループIDでメンバー一覧取得 - メンバーが見つからない場合")
    void findByGroupId_NotFound() {
        // Given
        when(oshiMemberRepository.findByGroupId(1L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> oshiMemberService.findByGroupId(1L, 1L))
                .isInstanceOf(OshiMemberNotFoundException.class)
                .hasMessage("該当するメンバーが見つかりません。");

        verify(oshiMemberRepository, times(1)).findByGroupId(1L, 1L);
    }

    @Test
    @DisplayName("メンバー名での完全一致検索 - 正常系")
    void findByMemberName_Success() {
        // Given
        when(oshiMemberRepository.findByMemberName("テストメンバー", 1L))
                .thenReturn(Optional.of(testMember));

        // When
        OshiMemberResponse response = oshiMemberService.findByMemberName("テストメンバー", 1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.memberName()).isEqualTo("テストメンバー");
        verify(oshiMemberRepository, times(1)).findByMemberName("テストメンバー", 1L);
    }

    @Test
    @DisplayName("メンバー名での完全一致検索 - メンバーが見つからない場合")
    void findByMemberName_NotFound() {
        // Given
        when(oshiMemberRepository.findByMemberName("存在しないメンバー", 1L))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> oshiMemberService.findByMemberName("存在しないメンバー", 1L))
                .isInstanceOf(OshiMemberNotFoundException.class)
                .hasMessage("該当する名前のメンバーが見つかりません。");
    }

    @Test
    @DisplayName("メンバー名でのあいまい検索 - 正常系")
    void findByMemberNameFuzzy_Success() {
        // Given
        List<OshiMember> members = new ArrayList<>();
        members.add(testMember);

        when(oshiMemberRepository.findByMemberNameFuzzy("テスト", 1L))
                .thenReturn(Optional.of(members));

        // When
        List<OshiMemberResponse> responses = oshiMemberService.findByMemberNameFuzzy("テスト", 1L);

        // Then
        assertThat(responses).isNotEmpty();
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).memberName()).isEqualTo("テストメンバー");
    }

    @Test
    @DisplayName("メンバー情報の更新 - 正常系")
    void update_Success() {
        // Given
        UpdateOshiMemberRequest request = new UpdateOshiMemberRequest(
                1L,
                "更新されたメンバー",
                "コウシンサレタメンバー",
                (byte) 1,
                LocalDate.of(1996, 6, 16)
        );

        when(oshiMemberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(oshiMemberRepository.existsByMemberName(
                eq("更新されたメンバー"),
                any(),
                eq(1L)
        )).thenReturn(false);
        when(oshiMemberRepository.save(any(OshiMember.class))).thenReturn(testMember);

        // When
        OshiMemberResponse response = oshiMemberService.update(1L, request);

        // Then
        assertThat(response).isNotNull();
        verify(oshiMemberRepository, times(1)).findById(1L);
        verify(oshiMemberRepository, times(1)).save(any(OshiMember.class));
    }

    @Test
    @DisplayName("メンバー情報の更新 - メンバーが見つからない場合")
    void update_MemberNotFound() {
        // Given
        UpdateOshiMemberRequest request = new UpdateOshiMemberRequest(
                1L,
                "更新されたメンバー",
                "コウシンサレタメンバー",
                (byte) 1,
                LocalDate.of(1996, 6, 16)
        );

        when(oshiMemberRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> oshiMemberService.update(1L, request))
                .isInstanceOf(OshiMemberNotFoundException.class)
                .hasMessage("該当のメンバーが見つかりません。");

        verify(oshiMemberRepository, never()).save(any());
    }

    @Test
    @DisplayName("メンバー情報の更新 - 権限がない場合")
    void update_Unauthorized() {
        // Given
        UpdateOshiMemberRequest request = new UpdateOshiMemberRequest(
                1L,
                "更新されたメンバー",
                "コウシンサレタメンバー",
                (byte) 1,
                LocalDate.of(1996, 6, 16)
        );

        when(oshiMemberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        // When & Then
        // 異なるユーザーIDで更新を試みる
        assertThatThrownBy(() -> oshiMemberService.update(999L, request))
                .isInstanceOf(OshiMemberNotFoundException.class)
                .hasMessage("該当のメンバーが見つかりません。");

        verify(oshiMemberRepository, never()).save(any());
    }

    @Test
    @DisplayName("メンバー情報の更新 - メンバー名が既に存在する場合")
    void update_MemberNameAlreadyExists() {
        // Given
        UpdateOshiMemberRequest request = new UpdateOshiMemberRequest(
                1L,
                "既存のメンバー名",
                "キゾンノメンバーメイ",
                (byte) 1,
                LocalDate.of(1996, 6, 16)
        );

        when(oshiMemberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(oshiMemberRepository.existsByMemberName(
                eq("既存のメンバー名"),
                any(),
                eq(1L)
        )).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> oshiMemberService.update(1L, request))
                .isInstanceOf(OshiMemberAlreadyExistsException.class)
                .hasMessage("該当のメンバー名は既に登録されています。");

        verify(oshiMemberRepository, never()).save(any());
    }

    @Test
    @DisplayName("メンバーの削除 - 正常系")
    void delete_Success() {
        // Given
        when(oshiMemberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        doNothing().when(oshiMemberRepository).delete(testMember);

        // When
        oshiMemberService.delete(1L, 1L);

        // Then
        verify(oshiMemberRepository, times(1)).findById(1L);
        verify(oshiMemberRepository, times(1)).delete(testMember);
    }

    @Test
    @DisplayName("メンバーの削除 - メンバーが見つからない場合")
    void delete_MemberNotFound() {
        // Given
        when(oshiMemberRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> oshiMemberService.delete(1L, 1L))
                .isInstanceOf(OshiMemberNotFoundException.class)
                .hasMessage("該当のメンバーが見つかりません。");

        verify(oshiMemberRepository, never()).delete(any());
    }

    @Test
    @DisplayName("メンバーの削除 - 権限がない場合")
    void delete_Unauthorized() {
        // Given
        when(oshiMemberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        // When & Then
        // 異なるユーザーIDで削除を試みる
        assertThatThrownBy(() -> oshiMemberService.delete(1L, 999L))
                .isInstanceOf(OshiMemberNotFoundException.class)
                .hasMessage("該当のメンバーが見つかりません。");

        verify(oshiMemberRepository, never()).delete(any());
    }
}
