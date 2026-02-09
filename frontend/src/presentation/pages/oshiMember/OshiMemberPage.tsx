/**
 * 推しメンバー画面
 * Presentation層：UIとユーザーインタラクション
 */

import { useMemo } from 'react';
import { ErrorMessage } from '../../components/ErrorMessage';
import { useOshiMemberUseCases } from '../../../application/usecases/oshiMember/useOshiMemberUseCases';
import { OshiGroupRepository } from '../../../infrastructure/repositories/OshiGroupRepository';
import { OshiMemberRepository } from '../../../infrastructure/repositories/OshiMemberRepository';
import { formatDate, formatDateTime, formatGender } from '../../../shared/formatters';
import './OshiMemberPage.css';

export function OshiMemberPage() {
  // リポジトリのインスタンス化（DIコンテナ的な役割）
  const memberRepository = useMemo(() => new OshiMemberRepository(), []);
  const groupRepository = useMemo(() => new OshiGroupRepository(), []);
  
  const {
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
    groups,
    groupsLoading,
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
    handleDelete,
  } = useOshiMemberUseCases(memberRepository, groupRepository);

  return (
    <div className="oshi-member-page">
      <header className="oshi-member-page__header">
        <h1>推しメンバー</h1>
        <button type="button" className="btn-primary" onClick={openCreateModal}>
          新規作成
        </button>
      </header>

      <section className="card-panel oshi-member-page__search">
        <h2>検索</h2>
        <form onSubmit={handleSearch} className="oshi-member-page__search-form">
          <div className="oshi-member-page__search-type">
            <label>
              <input
                type="radio"
                name="searchType"
                checked={searchType === 'groupId'}
                onChange={() => setSearchType('groupId')}
              />
              グループID
            </label>
            <label>
              <input
                type="radio"
                name="searchType"
                checked={searchType === 'memberName'}
                onChange={() => setSearchType('memberName')}
              />
              メンバー名
            </label>
          </div>

          {searchType === 'groupId' ? (
            <div className="form-field">
              <label htmlFor="search-group-id">グループ</label>
              <select
                id="search-group-id"
                value={searchGroupId}
                onChange={(e) => setSearchGroupId(e.target.value ? Number(e.target.value) : '')}
                disabled={groupsLoading}
              >
                <option value="">-- グループを選択 --</option>
                {groups.map((g) => (
                  <option key={g.id} value={g.id}>
                    {g.groupName}
                  </option>
                ))}
              </select>
            </div>
          ) : (
            <>
              <div className="oshi-member-page__search-mode">
                <label>
                  <input
                    type="radio"
                    name="searchMode"
                    checked={searchFull}
                    onChange={() => {
                      setSearchFull(true);
                      setSearchFuzzy(false);
                    }}
                  />
                  全文一致
                </label>
                <label>
                  <input
                    type="radio"
                    name="searchMode"
                    checked={searchFuzzy}
                    onChange={() => {
                      setSearchFuzzy(true);
                      setSearchFull(false);
                    }}
                  />
                  あいまい検索
                </label>
              </div>
              <div className="form-field">
                <label htmlFor="search-member-name">メンバー名</label>
                <input
                  id="search-member-name"
                  type="text"
                  value={searchMemberName}
                  onChange={(e) => setSearchMemberName(e.target.value)}
                  placeholder="メンバー名を入力"
                />
              </div>
            </>
          )}
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? '検索中...' : '検索'}
          </button>
        </form>
      </section>

      <ErrorMessage error={error} onDismiss={() => setError(null)} />

      <section className="card-panel oshi-member-page__list">
        <h2>一覧</h2>
        {loading ? (
          <div className="page-loading">
            <div className="spinner" aria-hidden />
          </div>
        ) : members.length === 0 ? (
          <p className="oshi-member-page__empty">
            {searchType === 'groupId' && searchGroupId
              ? '該当するメンバーはありません'
              : searchType === 'memberName' && searchMemberName.trim()
              ? '該当するメンバーはありません'
              : '検索条件を入力して検索してください'}
          </p>
        ) : (
          <ul className="oshi-member-page__members">
            {members.map((m) => (
              <li key={m.id} className="oshi-member-page__member-item">
                <div className="oshi-member-page__member-main">
                  <span className="oshi-member-page__member-name">{m.memberName}</span>
                  <span className="oshi-member-page__member-kana">({m.memberNameKana})</span>
                </div>
                <div className="oshi-member-page__member-info">
                  <span>グループ: {m.groupName}</span>
                  <span>性別: {formatGender(m.gender)}</span>
                  <span>誕生日: {formatDate(m.birthDay)}</span>
                </div>
                <div className="oshi-member-page__member-meta">
                  更新: {formatDateTime(m.updatedAt)}
                </div>
                <div className="oshi-member-page__member-actions">
                  <button
                    type="button"
                    className="btn-secondary"
                    onClick={() => openEditModal(m)}
                  >
                    編集
                  </button>
                  <button
                    type="button"
                    className="btn-danger"
                    onClick={() => handleDelete(m.id)}
                  >
                    削除
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </section>

      {modalOpen && (
        <div className="oshi-member-page__modal-overlay" onClick={closeModal}>
          <div
            className="oshi-member-page__modal card-panel"
            onClick={(e) => e.stopPropagation()}
            role="dialog"
            aria-modal="true"
            aria-labelledby="modal-title"
          >
            <h2 id="modal-title">
              {editingMember ? '推しメンバーを編集' : '推しメンバーを新規作成'}
            </h2>
            <form onSubmit={handleModalSubmit}>
              <ErrorMessage error={submitError} onDismiss={() => setSubmitError(null)} />
              <div className="form-field">
                <label htmlFor="form-groupId">グループ *</label>
                <select
                  id="form-groupId"
                  value={formValues.groupId}
                  onChange={(e) =>
                    setFormValues((v) => ({ ...v, groupId: Number(e.target.value) }))
                  }
                  className={formErrors.groupId ? 'invalid' : ''}
                  disabled={!!editingMember || groupsLoading}
                >
                  <option value={0}>-- グループを選択 --</option>
                  {groups.map((g) => (
                    <option key={g.id} value={g.id}>
                      {g.groupName}
                    </option>
                  ))}
                </select>
                {formErrors.groupId && (
                  <span className="hint" role="alert">
                    {formErrors.groupId}
                  </span>
                )}
              </div>
              <div className="form-field">
                <label htmlFor="form-memberName">メンバー名 *</label>
                <input
                  id="form-memberName"
                  value={formValues.memberName}
                  onChange={(e) =>
                    setFormValues((v) => ({ ...v, memberName: e.target.value }))
                  }
                  className={formErrors.memberName ? 'invalid' : ''}
                  placeholder="例: 山田太郎"
                />
                {formErrors.memberName && (
                  <span className="hint" role="alert">
                    {formErrors.memberName}
                  </span>
                )}
              </div>
              <div className="form-field">
                <label htmlFor="form-memberNameKana">メンバー名（カナ） *</label>
                <input
                  id="form-memberNameKana"
                  value={formValues.memberNameKana}
                  onChange={(e) =>
                    setFormValues((v) => ({ ...v, memberNameKana: e.target.value }))
                  }
                  className={formErrors.memberNameKana ? 'invalid' : ''}
                  placeholder="例: ヤマダタロウ"
                />
                {formErrors.memberNameKana && (
                  <span className="hint" role="alert">
                    {formErrors.memberNameKana}
                  </span>
                )}
              </div>
              <div className="form-field">
                <label htmlFor="form-gender">性別 *</label>
                <select
                  id="form-gender"
                  value={formValues.gender}
                  onChange={(e) =>
                    setFormValues((v) => ({ ...v, gender: Number(e.target.value) }))
                  }
                  className={formErrors.gender ? 'invalid' : ''}
                >
                  <option value={0}>男性</option>
                  <option value={1}>女性</option>
                </select>
                {formErrors.gender && (
                  <span className="hint" role="alert">
                    {formErrors.gender}
                  </span>
                )}
              </div>
              <div className="form-field">
                <label htmlFor="form-birthDay">誕生日 *</label>
                <input
                  id="form-birthDay"
                  type="date"
                  value={formValues.birthDay}
                  onChange={(e) =>
                    setFormValues((v) => ({ ...v, birthDay: e.target.value }))
                  }
                  className={formErrors.birthDay ? 'invalid' : ''}
                />
                {formErrors.birthDay && (
                  <span className="hint" role="alert">
                    {formErrors.birthDay}
                  </span>
                )}
              </div>
              <div className="oshi-member-page__modal-actions">
                <button type="button" className="btn-secondary" onClick={closeModal}>
                  キャンセル
                </button>
                <button type="submit" className="btn-primary" disabled={submitting}>
                  {submitting ? '保存中...' : '保存'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
