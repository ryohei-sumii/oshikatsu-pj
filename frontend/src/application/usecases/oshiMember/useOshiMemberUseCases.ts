/**
 * 推しメンバーユースケース
 * Application層：ビジネスロジックとユースケースの実装
 */

import { useCallback, useEffect, useState } from 'react';
import type { OshiGroup } from '../../../domain/entities/OshiGroup';
import type { OshiMember } from '../../../domain/entities/OshiMember';
import type { IOshiGroupRepository } from '../../../domain/repositories/IOshiGroupRepository';
import type { IOshiMemberRepository } from '../../../domain/repositories/IOshiMemberRepository';
import { oshiMemberFormSchema, type OshiMemberFormValues } from '../../validators/oshiMemberValidator';
import { ApiError } from '../../../infrastructure/api/client';

type SearchType = 'groupId' | 'memberName';

export function useOshiMemberUseCases(
  memberRepository: IOshiMemberRepository,
  groupRepository: IOshiGroupRepository
) {
  // 検索関連
  const [searchType, setSearchType] = useState<SearchType>('groupId');
  const [searchFull, setSearchFull] = useState(true);
  const [searchFuzzy, setSearchFuzzy] = useState(false);
  const [searchGroupId, setSearchGroupId] = useState<number | ''>('');
  const [searchMemberName, setSearchMemberName] = useState('');
  const [members, setMembers] = useState<OshiMember[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | ApiError | null>(null);

  // グループ一覧（ドロップダウン用）
  const [groups, setGroups] = useState<OshiGroup[]>([]);
  const [groupsLoading, setGroupsLoading] = useState(false);

  // モーダル関連
  const [modalOpen, setModalOpen] = useState(false);
  const [editingMember, setEditingMember] = useState<OshiMember | null>(null);
  const [formValues, setFormValues] = useState<OshiMemberFormValues>({
    groupId: 0,
    memberName: '',
    memberNameKana: '',
    gender: 0,
    birthDay: '',
  });
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [submitError, setSubmitError] = useState<string | ApiError | null>(null);
  const [submitting, setSubmitting] = useState(false);

  /** グループ一覧を取得（あいまい検索で全件取得） */
  useEffect(() => {
    const fetchGroups = async () => {
      setGroupsLoading(true);
      try {
        const list = await groupRepository.findByGroupName(false, true, '');
        setGroups(list);
      } catch (err) {
        console.error('グループ一覧の取得に失敗しました', err);
      } finally {
        setGroupsLoading(false);
      }
    };
    fetchGroups();
  }, [groupRepository]);

  /** 検索実行 */
  const runSearch = useCallback(async () => {
    setError(null);
    setLoading(true);
    try {
      if (searchType === 'groupId') {
        if (!searchGroupId) {
          setError('グループを選択してください');
          setMembers([]);
          return;
        }
        const list = await memberRepository.findByGroupId(searchGroupId);
        setMembers(Array.isArray(list) ? list : [list]);
      } else {
        if (!searchFull && !searchFuzzy) {
          setError('全文一致かあいまい検索のどちらかを選択してください');
          setMembers([]);
          return;
        }
        if (searchFull && searchFuzzy) {
          setError('全文一致とあいまい検索は同時に選択できません');
          setMembers([]);
          return;
        }
        const result = await memberRepository.findByMemberName(searchFull, searchFuzzy, searchMemberName);
        setMembers(Array.isArray(result) ? result : [result]);
      }
    } catch (err) {
      if (err instanceof Error) {
        setError(err as ApiError);
      } else {
        setError('検索に失敗しました');
      }
      setMembers([]);
    } finally {
      setLoading(false);
    }
  }, [memberRepository, searchType, searchFull, searchFuzzy, searchGroupId, searchMemberName]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    runSearch();
  };

  /** 新規作成モーダルを開く */
  const openCreateModal = () => {
    setEditingMember(null);
    setFormValues({
      groupId: searchType === 'groupId' && searchGroupId ? searchGroupId : 0,
      memberName: '',
      memberNameKana: '',
      gender: 0,
      birthDay: '',
    });
    setFormErrors({});
    setSubmitError(null);
    setModalOpen(true);
  };

  /** 編集モーダルを開く */
  const openEditModal = (member: OshiMember) => {
    setEditingMember(member);
    setFormValues({
      groupId: member.groupId,
      memberName: member.memberName,
      memberNameKana: member.memberNameKana,
      gender: member.gender,
      birthDay: member.birthDay,
    });
    setFormErrors({});
    setSubmitError(null);
    setModalOpen(true);
  };

  /** モーダルを閉じる */
  const closeModal = () => {
    setModalOpen(false);
    setEditingMember(null);
    setSubmitError(null);
  };

  /** フォーム送信 */
  const handleModalSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormErrors({});
    setSubmitError(null);

    const result = oshiMemberFormSchema.safeParse(formValues);
    if (!result.success) {
      const issues = result.error.flatten();
      const fieldMap: Record<string, string> = {};
      if (issues.fieldErrors) {
        Object.entries(issues.fieldErrors).forEach(([key, messages]) => {
          if (messages && messages[0]) fieldMap[key] = messages[0];
        });
      }
      setFormErrors(fieldMap);
      setSubmitError(issues.formErrors[0] ?? '入力内容を確認してください');
      return;
    }

    setSubmitting(true);
    try {
      if (editingMember) {
        await memberRepository.update({
          memberId: editingMember.id,
          memberName: result.data.memberName,
          memberNameKana: result.data.memberNameKana,
          gender: result.data.gender,
          birthDay: result.data.birthDay,
        });
      } else {
        await memberRepository.create({
          groupId: result.data.groupId,
          memberName: result.data.memberName,
          memberNameKana: result.data.memberNameKana,
          gender: result.data.gender,
          birthDay: result.data.birthDay,
        });
      }
      closeModal();
      runSearch();
    } catch (err) {
      if (err instanceof Error) {
        setSubmitError(err as ApiError);
      } else {
        setSubmitError('保存に失敗しました');
      }
    } finally {
      setSubmitting(false);
    }
  };

  /** 削除 */
  const handleDelete = async (memberId: number) => {
    if (!confirm('このメンバーを削除してもよろしいですか？')) {
      return;
    }
    try {
      await memberRepository.delete(memberId);
      runSearch();
    } catch (err) {
      if (err instanceof Error) {
        setError(err as ApiError);
      } else {
        setError('削除に失敗しました');
      }
    }
  };

  return {
    // 検索関連
    searchType,
    setSearchType,
    searchFull,
    setSearchFull,
    searchFuzzy,
    setSearchFuzzy,
    searchGroupId,
    setSearchGroupId,
    searchMemberName,
    setSearchMemberName,
    members,
    loading,
    error,
    setError,
    handleSearch,
    
    // グループ一覧
    groups,
    groupsLoading,
    
    // モーダル関連
    modalOpen,
    editingMember,
    formValues,
    setFormValues,
    formErrors,
    submitError,
    setSubmitError,
    submitting,
    openCreateModal,
    openEditModal,
    closeModal,
    handleModalSubmit,
    
    // 削除
    handleDelete,
  };
}
