/**
 * 会員登録画面
 * ユーザー名・メール・パスワードで登録し、成功時はログイン状態にしてダッシュボードへ遷移する
 */

import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { register as registerApi } from '../api/authApi';
import { useAuth } from '../contexts/AuthContext';
import { ApiError } from '../infrastructure/api/client';
import { registerSchema } from '../application/validators/authValidator';
import { ErrorMessage } from '../presentation/components/ErrorMessage';
import './RegisterPage.css';

export function RegisterPage() {
  const navigate = useNavigate();
  const { setAuthFromResponse } = useAuth();

  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | ApiError | Record<string, string> | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setFieldErrors({});

    const result = registerSchema.safeParse({ username, email, password });
    if (!result.success) {
      const issues = result.error.flatten();
      const fieldMap: Record<string, string> = {};
      if (issues.fieldErrors) {
        Object.entries(issues.fieldErrors).forEach(([key, messages]) => {
          if (messages && messages[0]) fieldMap[key] = messages[0];
        });
      }
      setFieldErrors(fieldMap);
      setError(issues.formErrors[0] ?? '入力内容を確認してください');
      return;
    }

    setSubmitting(true);
    try {
      const res = await registerApi(result.data);
      setAuthFromResponse(res);
      navigate('/', { replace: true });
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err);
      } else if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('登録に失敗しました。しばらく経ってからお試しください。');
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="register-page">
      <div className="register-page__card card-panel">
        <h1 className="register-page__title">推し活</h1>
        <p className="register-page__subtitle">会員登録</p>

        <form onSubmit={handleSubmit} className="register-page__form" noValidate>
          <ErrorMessage error={error} onDismiss={() => setError(null)} />

          <div className="form-field">
            <label htmlFor="register-username">ユーザー名（3〜50文字）</label>
            <input
              id="register-username"
              type="text"
              autoComplete="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className={fieldErrors.username ? 'invalid' : ''}
              placeholder="ユーザー名を入力"
              disabled={submitting}
              aria-invalid={!!fieldErrors.username}
            />
            {fieldErrors.username && (
              <span className="hint" role="alert">{fieldErrors.username}</span>
            )}
          </div>

          <div className="form-field">
            <label htmlFor="register-email">メールアドレス</label>
            <input
              id="register-email"
              type="email"
              autoComplete="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className={fieldErrors.email ? 'invalid' : ''}
              placeholder="メールアドレスを入力"
              disabled={submitting}
              aria-invalid={!!fieldErrors.email}
            />
            {fieldErrors.email && (
              <span className="hint" role="alert">{fieldErrors.email}</span>
            )}
          </div>

          <div className="form-field">
            <label htmlFor="register-password">パスワード</label>
            <input
              id="register-password"
              type="password"
              autoComplete="new-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className={fieldErrors.password ? 'invalid' : ''}
              placeholder="パスワードを入力"
              disabled={submitting}
              aria-invalid={!!fieldErrors.password}
            />
            {fieldErrors.password && (
              <span className="hint" role="alert">{fieldErrors.password}</span>
            )}
          </div>

          <button
            type="submit"
            className="btn-primary register-page__submit"
            disabled={submitting}
          >
            {submitting ? '登録中...' : '会員登録'}
          </button>
        </form>

        <p className="register-page__link">
          <Link to="/login">すでにアカウントをお持ちの方はログイン</Link>
        </p>
      </div>
    </div>
  );
}
