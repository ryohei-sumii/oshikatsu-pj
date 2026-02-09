/**
 * 推しメンバーリポジトリインターフェース
 * ドメイン層：データアクセスの抽象化
 */

import type { OshiMember } from '../entities/OshiMember';

export interface CreateOshiMemberParams {
  groupId: number;
  memberName: string;
  memberNameKana: string;
  gender: number;
  birthDay: string;
}

export interface UpdateOshiMemberParams {
  memberId: number;
  memberName: string;
  memberNameKana: string;
  gender: number;
  birthDay: string;
}

export interface IOshiMemberRepository {
  /**
   * グループIDでメンバー一覧を取得
   * @param groupId グループID
   */
  findByGroupId(groupId: number): Promise<OshiMember[]>;

  /**
   * メンバー名で検索
   * @param full 全文一致フラグ
   * @param fuzzy あいまい検索フラグ
   * @param memberName メンバー名
   */
  findByMemberName(full: boolean, fuzzy: boolean, memberName: string): Promise<OshiMember | OshiMember[]>;

  /**
   * メンバーを作成
   * @param params 作成パラメータ
   */
  create(params: CreateOshiMemberParams): Promise<OshiMember>;

  /**
   * メンバーを更新
   * @param params 更新パラメータ
   */
  update(params: UpdateOshiMemberParams): Promise<OshiMember>;

  /**
   * メンバーを削除
   * @param memberId メンバーID
   */
  delete(memberId: number): Promise<void>;
}
