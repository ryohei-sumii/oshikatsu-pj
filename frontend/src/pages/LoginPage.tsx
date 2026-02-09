/**
 * ログイン画面
 * ユーザー名・パスワードでログインし、成功時はダッシュボードへ遷移する
 */

import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { ApiError } from '../infrastructure/api/client';
import { loginSchema } from '../application/validators/authValidator';
import { ErrorMessage } from '../presentation/components/ErrorMessage';
import './LoginPage.css';

export function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | ApiError | Record<string, string> | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [submitting, setSubmitting] = useState(false);

  /** ログイン送信後のリダイレクト先（ProtectedRoute から渡された location） */
  const from = (location.state as { from?: { pathname: string } })?.from?.pathname ?? '/';

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setFieldErrors({});

    const result = loginSchema.safeParse({ username, password });
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
      await login(result.data.username, result.data.password);
      navigate(from, { replace: true });
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err);
      } else if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('ログインに失敗しました。しばらく経ってからお試しください。');
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-page__card card-panel">
        <h1 className="login-page__title">推し活</h1>
        <p className="login-page__subtitle">ログイン</p>

        <form onSubmit={handleSubmit} className="login-page__form" noValidate>
          <ErrorMessage error={error} onDismiss={() => setError(null)} />

          <div className="form-field">
            <label htmlFor="login-username">ユーザー名</label>
            <input
              id="login-username"
              type="text"
              autoComplete="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className={fieldErrors.username ? 'invalid' : ''}
              placeholder="ユーザー名を入力"
              disabled={submitting}
              aria-invalid={!!fieldErrors.username}
              aria-describedby={fieldErrors.username ? 'login-username-error' : undefined}
            />
            {fieldErrors.username && (
              <span id="login-username-error" className="hint" role="alert">
                {fieldErrors.username}
              </span>
            )}
          </div>

          <div className="form-field">
            <label htmlFor="login-password">パスワード</label>
            <input
              id="login-password"
              type="password"
              autoComplete="current-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className={fieldErrors.password ? 'invalid' : ''}
              placeholder="パスワードを入力"
              disabled={submitting}
              aria-invalid={!!fieldErrors.password}
              aria-describedby={fieldErrors.password ? 'login-password-error' : undefined}
            />
            {fieldErrors.password && (
              <span id="login-password-error" className="hint" role="alert">
                {fieldErrors.password}
              </span>
            )}
          </div>

          <button
            type="submit"
            className="btn-primary login-page__submit"
            disabled={submitting}
          >
            {submitting ? 'ログイン中...' : 'ログイン'}
          </button>

          <p className="login-page__link">
            アカウントをお持ちでない方は<Link to="/register">会員登録</Link>
          </p>
        </form>
      </div>
    </div>
  );
}
