/**
 * APIクライアント
 * Infrastructure層：バックエンド通信の実装
 */

/** バックエンドのベースURL（環境変数で上書き可能） */
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

const AUTH_TOKEN_KEY = 'oshikatsu_token';

/**
 * ローカルストレージに保存されたJWTトークンを取得する
 */
export function getStoredToken(): string | null {
  try {
    return localStorage.getItem(AUTH_TOKEN_KEY);
  } catch {
    return null;
  }
}

/**
 * JWTトークンをローカルストレージに保存する
 */
export function setStoredToken(token: string): void {
  try {
    localStorage.setItem(AUTH_TOKEN_KEY, token);
  } catch (e) {
    console.error('Failed to store auth token', e);
  }
}

/**
 * 保存されたトークンを削除する（ログアウト時）
 */
export function clearStoredToken(): void {
  try {
    localStorage.removeItem(AUTH_TOKEN_KEY);
  } catch {
    // ignore
  }
}

/**
 * APIエラーレスポンスの型定義
 */
export interface ApiErrorResponse {
  status: number;
  message: string;
  errors?: Record<string, string>;
}

/**
 * APIエラーレスポンスをパースし、メッセージとフィールドエラーを返す
 */
async function parseErrorResponse(response: Response): Promise<ApiErrorResponse> {
  let body: unknown;
  try {
    body = await response.json();
  } catch {
    return {
      status: response.status,
      message: response.statusText || '通信エラーが発生しました',
    };
  }

  if (body && typeof body === 'object' && 'status' in body && 'message' in body) {
    return body as ApiErrorResponse;
  }

  return {
    status: response.status,
    message: (body as { message?: string })?.message ?? response.statusText ?? 'エラーが発生しました',
  };
}

/**
 * 共通のfetchラッパー
 */
export interface RequestOptions extends Omit<RequestInit, 'body'> {
  body?: BodyInit | null;
  json?: unknown;
  skipAuth?: boolean;
}

export class ApiError extends Error {
  readonly status: number;
  readonly errors: Record<string, string> | undefined;

  constructor(message: string, status: number, errors?: Record<string, string>) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.errors = errors;
  }
}

export async function apiRequest<T>(
  path: string,
  options: RequestOptions = {}
): Promise<T> {
  const { json, skipAuth = false, headers: optHeaders = {}, ...rest } = options;

  const headers = new Headers(optHeaders as HeadersInit);
  if (json !== undefined) {
    headers.set('Content-Type', 'application/json');
  }
  if (!skipAuth) {
    const token = getStoredToken();
    if (token) {
      headers.set('Authorization', `Bearer ${token}`);
    }
  }

  const url = path.startsWith('http') ? path : `${API_BASE_URL}${path}`;
  const body = json !== undefined ? JSON.stringify(json) : options.body;

  let response: Response;
  try {
    response = await fetch(url, {
      ...rest,
      headers,
      body,
    });
  } catch (e) {
    const message = e instanceof Error ? e.message : 'ネットワークエラーが発生しました';
    throw new ApiError(message, 0);
  }

  if (!response.ok) {
    const parsed = await parseErrorResponse(response);
    throw new ApiError(parsed.message, parsed.status, parsed.errors);
  }

  const contentType = response.headers.get('Content-Type');
  if (contentType?.includes('application/json')) {
    return (await response.json()) as T;
  }
  return undefined as unknown as T;
}
