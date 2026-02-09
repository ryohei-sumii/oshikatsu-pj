/**
 * 推しグループユースケース
 * Application層：ビジネスロジックとユースケースの実装
 */

import { useCallback, useState } from 'react';
import type { OshiGroup } from '../../../domain/entities/OshiGroup';
import type { IOshiGroupRepository } from '../../../domain/repositories/IOshiGroupRepository';
import { oshiGroupFormSchema, type OshiGroupFormValues } from '../../validators/oshiGroupValidator';
import { ApiError } from '../../../infrastructure/api/client';

type SearchType = 'groupName' | 'company';

export function useOshiGroupUseCases(repository: IOshiGroupRepository) {
  // 検索関連
  const [searchType, setSearchType] = useState<SearchType>('groupName');
  const [searchFull, setSearchFull] = useState(true);
  const [searchFuzzy, setSearchFuzzy] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [groups, setGroups] = useState<OshiGroup[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | ApiError | null>(null);

  // モーダル関連
  const [modalOpen, setModalOpen] = useState(false);
  const [editingGroup, setEditingGroup] = useState<OshiGroup | null>(null);
  const [formValues, setFormValues] = useState<OshiGroupFormValues>({
    groupName: '',
    company: '',
    description: '',
  });
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const [submitError, setSubmitError] = useState<string | ApiError | null>(null);
  const [submitting, setSubmitting] = useState(false);

  /** 検索実行 */
  const runSearch = useCallback(async () => {
    setError(null);
    setLoading(true);
    try {
      if (searchType === 'company') {
        const list = await repository.findByCompany(searchQuery);
        setGroups(list);
      } else {
        if (!searchFull && !searchFuzzy) {
          setError('全文一致かあいまい検索のどちらかを選択してください');
          setGroups([]);
          return;
        }
        if (searchFull && searchFuzzy) {
          setError('全文一致とあいまい検索は同時に選択できません');
          setGroups([]);
          return;
        }
        const list = await repository.findByGroupName(searchFull, searchFuzzy, searchQuery);
        setGroups(list);
      }
    } catch (err) {
      if (err instanceof Error) {
        setError(err as ApiError);
      } else {
        setError('検索に失敗しました');
      }
      setGroups([]);
    } finally {
      setLoading(false);
    }
  }, [repository, searchType, searchFull, searchFuzzy, searchQuery]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    runSearch();
  };

  /** 新規作成モーダルを開く */
  const openCreateModal = () => {
    setEditingGroup(null);
    setFormValues({ groupName: '', company: '', description: '' });
    setFormErrors({});
    setSubmitError(null);
    setModalOpen(true);
  };

  /** 編集モーダルを開く */
  const openEditModal = (group: OshiGroup) => {
    setEditingGroup(group);
    setFormValues({
      groupName: group.groupName,
      company: group.company ?? '',
      description: group.description ?? '',
    });
    setFormErrors({});
    setSubmitError(null);
    setModalOpen(true);
  };

  /** モーダルを閉じる */
  const closeModal = () => {
    setModalOpen(false);
    setEditingGroup(null);
    setSubmitError(null);
  };

  /** フォーム送信 */
  const handleModalSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormErrors({});
    setSubmitError(null);

    const result = oshiGroupFormSchema.safeParse(formValues);
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
      if (editingGroup) {
        await repository.update({
          groupId: editingGroup.id,
          groupName: result.data.groupName,
          company: result.data.company || undefined,
          description: result.data.description || undefined,
        });
      } else {
        await repository.create({
          groupName: result.data.groupName,
          company: result.data.company || undefined,
          description: result.data.description || undefined,
        });
      }
      closeModal();
      // 新規作成・編集後は自動検索を実行しない
      // ユーザーが明示的に検索ボタンを押したときのみ検索する
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
  const handleDelete = async (groupId: number) => {
    if (!confirm('このグループを削除してもよろしいですか？')) {
      return;
    }
    try {
      await repository.delete(groupId);
      // 削除後は、現在表示中のリストから該当のグループを削除
      setGroups((prevGroups) => prevGroups.filter((g) => g.id !== groupId));
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
    searchQuery,
    setSearchQuery,
    groups,
    loading,
    error,
    setError,
    handleSearch,
    
    // モーダル関連
    modalOpen,
    editingGroup,
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
