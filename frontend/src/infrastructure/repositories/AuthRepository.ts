/**
 * 認証リポジトリ実装
 * Infrastructure層：APIを使ったデータアクセス実装
 */

import type { AuthenticatedUser } from '../../domain/entities/User';
import type {
  IAuthRepository,
  LoginParams,
  RegisterParams,
} from '../../domain/repositories/IAuthRepository';
import { apiRequest } from '../api/client';

export class AuthRepository implements IAuthRepository {
  async login(params: LoginParams): Promise<AuthenticatedUser> {
    return apiRequest<AuthenticatedUser>('/api/auth/login', {
      method: 'POST',
      json: params,
      skipAuth: true,
    });
  }

  async register(params: RegisterParams): Promise<AuthenticatedUser> {
    return apiRequest<AuthenticatedUser>('/api/auth/register', {
      method: 'POST',
      json: params,
      skipAuth: true,
    });
  }
}
