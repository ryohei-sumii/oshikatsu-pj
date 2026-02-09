/**
 * 推しメンバーエンティティ
 * ドメイン層：ビジネスルールと不変条件を表現
 */

export interface OshiMember {
  readonly id: number;
  readonly userId: number;
  readonly groupId: number;
  readonly groupName: string;
  readonly memberName: string;
  readonly memberNameKana: string;
  readonly gender: number; // 0: 男性, 1: 女性
  readonly birthDay: string;
  readonly createdAt: string;
  readonly updatedAt: string;
}
