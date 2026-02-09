/**
 * 推しグループエンティティ
 * ドメイン層：ビジネスルールと不変条件を表現
 */

export interface OshiGroup {
  readonly id: number;
  readonly userId: number;
  readonly groupName: string;
  readonly company: string | null;
  readonly description: string | null;
  readonly createdAt: string;
  readonly updatedAt: string;
}
