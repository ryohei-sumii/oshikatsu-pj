/**
 * エラーメッセージ表示用コンポーネント
 * APIエラーや一般的なエラーメッセージを統一的に表示する
 */

import { ApiError } from '../../infrastructure/api/client';

interface ErrorMessageProps {
  /** 表示するエラー（ApiError または メッセージ文字列、フィールド別エラー） */
  error: ApiError | string | Record<string, string> | null;
  /** クリア用コールバック（閉じるボタン用） */
  onDismiss?: () => void;
}

/**
 * エラーメッセージを1件または複数表示する
 */
export function ErrorMessage({ error, onDismiss }: ErrorMessageProps) {
  if (!error) return null;

  let message: string;
  let fieldErrors: Record<string, string> | undefined;

  if (error instanceof ApiError) {
    message = error.message;
    fieldErrors = error.errors;
  } else if (typeof error === 'string') {
    message = error;
  } else if (typeof error === 'object' && error !== null) {
    fieldErrors = error;
    const messages = Object.values(error);
    message = messages.length > 0 ? messages.join(' ') : '入力内容を確認してください';
  } else {
    return null;
  }

  return (
    <div className="error-message" role="alert">
      {onDismiss && (
        <button
          type="button"
          className="error-message__dismiss"
          onClick={onDismiss}
          aria-label="閉じる"
        >
          ×
        </button>
      )}
      <p className="error-message__text">{message}</p>
      {fieldErrors && Object.keys(fieldErrors).length > 0 && (
        <ul className="error-message__list">
          {Object.entries(fieldErrors).map(([field, msg]) => (
            <li key={field}>
              <span className="error-message__field">{field}</span>: {msg}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
