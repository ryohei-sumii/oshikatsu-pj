/**
 * 認証API
 * バックエンド AuthController に対応
 */

import { apiRequest } from '../infrastructure/api/client';
import type { AuthResponse, LoginRequest, RegisterRequest } from '../types/api';

/**
 * ログイン
 * POST /api/auth/login
 * @throws {ApiError} 認証失敗時（401）またはバリデーションエラー時（400）
 */
export async function login(request: LoginRequest): Promise<AuthResponse> {
  return apiRequest<AuthResponse>('/api/auth/login', {
    method: 'POST',
    json: request,
    skipAuth: true,
  });
}

/**
 * 会員登録
 * POST /api/auth/register
 * @returns 登録成功時の認証情報（トークン含む）
 * @throws {ApiError} ユーザー名・メール重複時（409）またはバリデーションエラー時（400）
 */
export async function register(request: RegisterRequest): Promise<AuthResponse> {
  return apiRequest<AuthResponse>('/api/auth/register', {
    method: 'POST',
    json: request,
    skipAuth: true,
  });
}
