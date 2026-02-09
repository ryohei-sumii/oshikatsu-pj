/**
 * フォーマッター関数
 * Shared層：共有ユーティリティ
 */

/**
 * 日時を日本語形式でフォーマット
 * @param dateStr ISO 8601形式の日時文字列
 * @returns フォーマットされた日時文字列（例: 2024/01/15 10:30）
 */
export function formatDateTime(dateStr: string): string {
  try {
    return new Date(dateStr).toLocaleString('ja-JP', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch {
    return dateStr;
  }
}

/**
 * 日付を日本語形式でフォーマット
 * @param dateStr ISO 8601形式の日付文字列
 * @returns フォーマットされた日付文字列（例: 2024/01/15）
 */
export function formatDate(dateStr: string): string {
  try {
    return new Date(dateStr).toLocaleDateString('ja-JP', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    });
  } catch {
    return dateStr;
  }
}

/**
 * 性別コードをラベルに変換
 * @param gender 性別コード（0: 男性, 1: 女性）
 * @returns 性別ラベル
 */
export function formatGender(gender: number): string {
  return gender === 0 ? '男性' : '女性';
}
