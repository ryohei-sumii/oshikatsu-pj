/**
 * 推しグループ画面
 * Presentation層：UIとユーザーインタラクション
 */

import { ErrorMessage } from '../../components/ErrorMessage';
import { useOshiGroupUseCases } from '../../../application/usecases/oshiGroup/useOshiGroupUseCases';
import { OshiGroupRepository } from '../../../infrastructure/repositories/OshiGroupRepository';
import { formatDateTime } from '../../../shared/formatters';
import './OshiGroupPage.css';
import { useMemo } from 'react';

export function OshiGroupPage() {
  // リポジトリのインスタンス化（DIコンテナ的な役割）
  const repository = useMemo(() => new OshiGroupRepository(), []);
  
  const {
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
    handleDelete,
  } = useOshiGroupUseCases(repository);

  return (
    <div className="oshi-group-page">
      <header className="oshi-group-page__header">
        <h1>推しグループ</h1>
        <button type="button" className="btn-primary" onClick={openCreateModal}>
          新規作成
        </button>
      </header>

      <section className="card-panel oshi-group-page__search">
        <h2>検索</h2>
        <form onSubmit={handleSearch} className="oshi-group-page__search-form">
          <div className="oshi-group-page__search-type">
            <label>
              <input
                type="radio"
                name="searchType"
                checked={searchType === 'groupName'}
                onChange={() => setSearchType('groupName')}
              />
              グループ名
            </label>
            <label>
              <input
                type="radio"
                name="searchType"
                checked={searchType === 'company'}
                onChange={() => setSearchType('company')}
              />
              会社名
            </label>
          </div>

          {searchType === 'groupName' && (
            <div className="oshi-group-page__search-mode">
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
          )}

          <div className="form-field">
            <label htmlFor="search-query">
              {searchType === 'groupName' ? 'グループ名' : '会社名'}
            </label>
            <input
              id="search-query"
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder={searchType === 'groupName' ? 'グループ名を入力' : '会社名を入力'}
            />
          </div>
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? '検索中...' : '検索'}
          </button>
        </form>
      </section>

      <ErrorMessage error={error} onDismiss={() => setError(null)} />

      <section className="card-panel oshi-group-page__list">
        <h2>一覧</h2>
        {loading ? (
          <div className="page-loading">
            <div className="spinner" aria-hidden />
          </div>
        ) : groups.length === 0 ? (
          <p className="oshi-group-page__empty">
            {searchQuery.trim() ? '該当するグループはありません' : '検索条件を入力して検索してください'}
          </p>
        ) : (
          <ul className="oshi-group-page__groups">
            {groups.map((g) => (
              <li key={g.id} className="oshi-group-page__group-item">
                <div className="oshi-group-page__group-main">
                  <span className="oshi-group-page__group-name">{g.groupName}</span>
                  {g.company && (
                    <span className="oshi-group-page__group-company">{g.company}</span>
                  )}
                </div>
                {g.description && (
                  <p className="oshi-group-page__group-desc">{g.description}</p>
                )}
                <div className="oshi-group-page__group-meta">
                  更新: {formatDateTime(g.updatedAt)}
                </div>
                <div className="oshi-group-page__group-actions">
                  <button
                    type="button"
                    className="btn-secondary"
                    onClick={() => openEditModal(g)}
                  >
                    編集
                  </button>
                  <button
                    type="button"
                    className="btn-danger"
                    onClick={() => handleDelete(g.id)}
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
        <div className="oshi-group-page__modal-overlay" onClick={closeModal}>
          <div
            className="oshi-group-page__modal card-panel"
            onClick={(e) => e.stopPropagation()}
            role="dialog"
            aria-modal="true"
            aria-labelledby="modal-title"
          >
            <h2 id="modal-title">
              {editingGroup ? '推しグループを編集' : '推しグループを新規作成'}
            </h2>
            <form onSubmit={handleModalSubmit}>
              <ErrorMessage error={submitError} onDismiss={() => setSubmitError(null)} />
              <div className="form-field">
                <label htmlFor="form-groupName">グループ名 *</label>
                <input
                  id="form-groupName"
                  value={formValues.groupName}
                  onChange={(e) => setFormValues((v) => ({ ...v, groupName: e.target.value }))}
                  className={formErrors.groupName ? 'invalid' : ''}
                  placeholder="例: 〇〇組"
                />
                {formErrors.groupName && (
                  <span className="hint" role="alert">{formErrors.groupName}</span>
                )}
              </div>
              <div className="form-field">
                <label htmlFor="form-company">会社名</label>
                <input
                  id="form-company"
                  value={formValues.company ?? ''}
                  onChange={(e) => setFormValues((v) => ({ ...v, company: e.target.value }))}
                  placeholder="例: 〇〇事務所"
                />
              </div>
              <div className="form-field">
                <label htmlFor="form-description">説明（1000文字以内）</label>
                <textarea
                  id="form-description"
                  rows={3}
                  value={formValues.description ?? ''}
                  onChange={(e) => setFormValues((v) => ({ ...v, description: e.target.value }))}
                  className={formErrors.description ? 'invalid' : ''}
                  placeholder="メモや説明"
                />
                {formErrors.description && (
                  <span className="hint" role="alert">{formErrors.description}</span>
                )}
              </div>
              <div className="oshi-group-page__modal-actions">
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
