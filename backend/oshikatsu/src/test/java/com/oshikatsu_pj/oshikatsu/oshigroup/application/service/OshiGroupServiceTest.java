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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OshiGroupService ユニットテスト")
class OshiGroupServiceTest {

    @Mock
    private OshiGroupRepository oshiGroupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OshiGroupService oshiGroupService;

    private User testUser;
    private OshiGroup testGroup;

    @BeforeEach
    void setUp() throws Exception {
        testUser = new User("testuser", "test@example.com", "password123");
        // リフレクションでIDを設定
        var userIdField = User.class.getDeclaredField("id");
        userIdField.setAccessible(true);
        userIdField.set(testUser, 1L);

        testGroup = new OshiGroup("テストグループ", "テスト事務所", "テスト説明");
        var groupIdField = OshiGroup.class.getDeclaredField("id");
        groupIdField.setAccessible(true);
        groupIdField.set(testGroup, 1L);
        
        // userフィールドも設定
        var groupUserField = OshiGroup.class.getDeclaredField("user");
        groupUserField.setAccessible(true);
        groupUserField.set(testGroup, testUser);
    }

    @Test
    @DisplayName("推しグループの作成 - 正常系")
    void createOshiGroup_Success() {
        // Given
        CreateOshiGroupRequest request = new CreateOshiGroupRequest(
                "新しいグループ",
                "新しい事務所",
                "新しい説明"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(oshiGroupRepository.existsByGroupName("新しいグループ", 1L)).thenReturn(false);
        when(oshiGroupRepository.save(any(OshiGroup.class))).thenReturn(testGroup);

        // When
        OshiGroupResponse response = oshiGroupService.createOshiGroup(1L, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.groupName()).isEqualTo("テストグループ");
        verify(userRepository, times(1)).findById(1L);
        verify(oshiGroupRepository, times(1)).existsByGroupName("新しいグループ", 1L);
        verify(oshiGroupRepository, times(1)).save(any(OshiGroup.class));
    }

    @Test
    @DisplayName("推しグループの作成 - グループ名が既に存在する場合")
    void createOshiGroup_AlreadyExists() {
        // Given
        CreateOshiGroupRequest request = new CreateOshiGroupRequest(
                "既存のグループ",
                "既存の事務所",
                "既存の説明"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(oshiGroupRepository.existsByGroupName("既存のグループ", 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> oshiGroupService.createOshiGroup(1L, request))
                .isInstanceOf(OshiGroupAlreadyExistsException.class)
                .hasMessage("既存のグループは既に登録されています。");

        verify(userRepository, times(1)).findById(1L);
        verify(oshiGroupRepository, times(1)).existsByGroupName("既存のグループ", 1L);
        verify(oshiGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("グループ名での完全一致検索 - 正常系")
    void findByNameFullMatch_Success() {
        // Given
        when(oshiGroupRepository.findByGroupName("テストグループ", 1L))
                .thenReturn(Optional.of(testGroup));

        // When
        OshiGroupResponse response = oshiGroupService.findByNameFullMatch("テストグループ", 1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.groupName()).isEqualTo("テストグループ");
        verify(oshiGroupRepository, times(1)).findByGroupName("テストグループ", 1L);
    }

    @Test
    @DisplayName("グループ名での完全一致検索 - グループが見つからない場合")
    void findByNameFullMatch_NotFound() {
        // Given
        when(oshiGroupRepository.findByGroupName("存在しないグループ", 1L))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> oshiGroupService.findByNameFullMatch("存在しないグループ", 1L))
                .isInstanceOf(OshiGroupNotFoundException.class)
                .hasMessage("該当する名称のグループはまだ登録されていません。");

        verify(oshiGroupRepository, times(1)).findByGroupName("存在しないグループ", 1L);
    }

    @Test
    @DisplayName("グループ名でのあいまい検索 - 正常系")
    void findByNameFuzzy_Success() {
        // Given
        List<OshiGroup> groups = new ArrayList<>();
        groups.add(testGroup);
        
        when(oshiGroupRepository.findByGroupNameFuzzy("テスト", 1L))
                .thenReturn(Optional.of(groups));

        // When
        List<OshiGroupResponse> responses = oshiGroupService.findByNameFuzzy("テスト", 1L);

        // Then
        assertThat(responses).isNotEmpty();
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).groupName()).isEqualTo("テストグループ");
        verify(oshiGroupRepository, times(1)).findByGroupNameFuzzy("テスト", 1L);
    }

    @Test
    @DisplayName("グループ名でのあいまい検索 - グループが見つからない場合")
    void findByNameFuzzy_NotFound() {
        // Given
        when(oshiGroupRepository.findByGroupNameFuzzy("存在しない", 1L))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> oshiGroupService.findByNameFuzzy("存在しない", 1L))
                .isInstanceOf(OshiGroupNotFoundException.class)
                .hasMessage("該当する名称のグループはまだ登録されていません。");
    }

    @Test
    @DisplayName("ユーザーIDでグループ一覧取得 - 正常系")
    void findByUserId_Success() {
        // Given
        List<OshiGroup> groups = new ArrayList<>();
        groups.add(testGroup);
        
        when(oshiGroupRepository.findByUserId(1L))
                .thenReturn(Optional.of(groups));

        // When
        List<OshiGroupResponse> responses = oshiGroupService.findByUserId(1L);

        // Then
        assertThat(responses).isNotEmpty();
        assertThat(responses).hasSize(1);
        verify(oshiGroupRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("ユーザーIDでグループ一覧取得 - グループが見つからない場合")
    void findByUserId_NotFound() {
        // Given
        when(oshiGroupRepository.findByUserId(1L))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> oshiGroupService.findByUserId(1L))
                .isInstanceOf(OshiGroupNotFoundException.class)
                .hasMessage("該当するグループはまだ登録されていません。");
    }

    @Test
    @DisplayName("事務所名でグループ検索 - 正常系")
    void findByCompany_Success() {
        // Given
        List<OshiGroup> groups = new ArrayList<>();
        groups.add(testGroup);
        
        when(oshiGroupRepository.findByCompany("テスト事務所", 1L))
                .thenReturn(Optional.of(groups));

        // When
        List<OshiGroupResponse> responses = oshiGroupService.findByCompany("テスト事務所", 1L);

        // Then
        assertThat(responses).isNotEmpty();
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).company()).isEqualTo("テスト事務所");
        verify(oshiGroupRepository, times(1)).findByCompany("テスト事務所", 1L);
    }

    @Test
    @DisplayName("事務所名でグループ検索 - グループが見つからない場合")
    void findByCompany_NotFound() {
        // Given
        when(oshiGroupRepository.findByCompany("存在しない事務所", 1L))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> oshiGroupService.findByCompany("存在しない事務所", 1L))
                .isInstanceOf(OshiGroupNotFoundException.class)
                .hasMessage("該当する会社のグループはまだ登録されていません。");
    }

    @Test
    @DisplayName("グループ情報の更新 - 正常系")
    void update_Success() {
        // Given
        UpdateOshiGroupRequest request = new UpdateOshiGroupRequest(
                1L,
                "更新されたグループ",
                "更新された事務所",
                "更新された説明"
        );

        when(oshiGroupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(oshiGroupRepository.existsByGroupName("更新されたグループ", 1L)).thenReturn(false);
        when(oshiGroupRepository.save(any(OshiGroup.class))).thenReturn(testGroup);

        // When
        OshiGroupResponse response = oshiGroupService.update(1L, request);

        // Then
        assertThat(response).isNotNull();
        verify(oshiGroupRepository, times(1)).findById(1L);
        verify(oshiGroupRepository, times(1)).save(any(OshiGroup.class));
    }

    @Test
    @DisplayName("グループ情報の更新 - グループが見つからない場合")
    void update_NotFound() {
        // Given
        UpdateOshiGroupRequest request = new UpdateOshiGroupRequest(
                999L,
                "更新されたグループ",
                "更新された事務所",
                "更新された説明"
        );

        when(oshiGroupRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> oshiGroupService.update(1L, request))
                .isInstanceOf(OshiGroupNotFoundException.class)
                .hasMessage("該当のグループが見つかりません。");

        verify(oshiGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("グループ情報の更新 - 権限がない場合")
    void update_Unauthorized() {
        // Given
        UpdateOshiGroupRequest request = new UpdateOshiGroupRequest(
                1L,
                "更新されたグループ",
                "更新された事務所",
                "更新された説明"
        );

        when(oshiGroupRepository.findById(1L)).thenReturn(Optional.of(testGroup));

        // When & Then
        // 異なるユーザーIDで更新を試みる
        assertThatThrownBy(() -> oshiGroupService.update(999L, request))
                .isInstanceOf(OshiGroupNotFoundException.class)
                .hasMessage("該当のグループが見つかりません。");

        verify(oshiGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("グループ情報の更新 - グループ名が既に存在する場合")
    void update_GroupNameAlreadyExists() {
        // Given
        UpdateOshiGroupRequest request = new UpdateOshiGroupRequest(
                1L,
                "既存のグループ名",
                "更新された事務所",
                "更新された説明"
        );

        when(oshiGroupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(oshiGroupRepository.existsByGroupName("既存のグループ名", 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> oshiGroupService.update(1L, request))
                .isInstanceOf(OshiGroupAlreadyExistsException.class)
                .hasMessage("該当のグループ名は既に登録されています。");
    }

    @Test
    @DisplayName("グループの削除 - 正常系")
    void delete_Success() {
        // Given
        when(oshiGroupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        doNothing().when(oshiGroupRepository).delete(testGroup);

        // When
        oshiGroupService.delete(1L, 1L);

        // Then
        verify(oshiGroupRepository, times(1)).findById(1L);
        verify(oshiGroupRepository, times(1)).delete(testGroup);
    }

    @Test
    @DisplayName("グループの削除 - グループが見つからない場合")
    void delete_GroupNotFound() {
        // Given
        when(oshiGroupRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> oshiGroupService.delete(1L, 1L))
                .isInstanceOf(OshiGroupNotFoundException.class)
                .hasMessage("該当のグループが見つかりません。");

        verify(oshiGroupRepository, never()).delete(any());
    }

    @Test
    @DisplayName("グループの削除 - 権限がない場合")
    void delete_Unauthorized() {
        // Given
        when(oshiGroupRepository.findById(1L)).thenReturn(Optional.of(testGroup));

        // When & Then
        // 異なるユーザーIDで削除を試みる
        assertThatThrownBy(() -> oshiGroupService.delete(1L, 999L))
                .isInstanceOf(OshiGroupNotFoundException.class)
                .hasMessage("該当のグループが見つかりません。");

        verify(oshiGroupRepository, never()).delete(any());
    }
}
