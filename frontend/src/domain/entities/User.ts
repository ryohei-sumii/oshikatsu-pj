/**
 * ユーザーエンティティ
 * ドメイン層：ビジネスルールと不変条件を表現
 */

export interface User {
  readonly userId: number;
  readonly username: string;
  readonly email: string;
}

export interface AuthenticatedUser extends User {
  readonly token: string;
  readonly type: string;
}
