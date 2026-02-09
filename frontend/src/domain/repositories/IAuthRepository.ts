/**
 * 認証リポジトリインターフェース
 * ドメイン層：データアクセスの抽象化
 */

import type { AuthenticatedUser } from '../entities/User';

export interface LoginParams {
  username: string;
  password: string;
}

export interface RegisterParams {
  username: string;
  email: string;
  password: string;
}

export interface IAuthRepository {
  /**
   * ログイン
   * @param params ログインパラメータ
   */
  login(params: LoginParams): Promise<AuthenticatedUser>;

  /**
   * 会員登録
   * @param params 登録パラメータ
   */
  register(params: RegisterParams): Promise<AuthenticatedUser>;
}
