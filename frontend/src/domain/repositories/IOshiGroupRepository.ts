/**
 * 推しグループリポジトリインターフェース
 * ドメイン層：データアクセスの抽象化
 */

import type { OshiGroup } from '../entities/OshiGroup';

export interface CreateOshiGroupParams {
  groupName: string;
  company?: string;
  description?: string;
}

export interface UpdateOshiGroupParams {
  groupId: number;
  groupName: string;
  company?: string;
  description?: string;
}

export interface IOshiGroupRepository {
  /**
   * グループ名で検索
   * @param full 全文一致フラグ
   * @param fuzzy あいまい検索フラグ
   * @param groupName グループ名
   */
  findByGroupName(full: boolean, fuzzy: boolean, groupName: string): Promise<OshiGroup[]>;

  /**
   * 会社名で検索
   * @param company 会社名
   */
  findByCompany(company: string): Promise<OshiGroup[]>;

  /**
   * グループを作成
   * @param params 作成パラメータ
   */
  create(params: CreateOshiGroupParams): Promise<OshiGroup>;

  /**
   * グループを更新
   * @param params 更新パラメータ
   */
  update(params: UpdateOshiGroupParams): Promise<OshiGroup>;

  /**
   * グループを削除
   * @param groupId グループID
   */
  delete(groupId: number): Promise<void>;
}
