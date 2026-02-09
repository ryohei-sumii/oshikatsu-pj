/**
 * 推しメンバーリポジトリ実装
 * Infrastructure層：APIを使ったデータアクセス実装
 */

import type { OshiMember } from '../../domain/entities/OshiMember';
import type {
  IOshiMemberRepository,
  CreateOshiMemberParams,
  UpdateOshiMemberParams,
} from '../../domain/repositories/IOshiMemberRepository';
import { apiRequest } from '../api/client';

export class OshiMemberRepository implements IOshiMemberRepository {
  async findByGroupId(groupId: number): Promise<OshiMember[]> {
    const params = new URLSearchParams({ groupId: String(groupId) });
    return apiRequest<OshiMember[]>(`/api/oshi-members/list-group?${params.toString()}`);
  }

  async findByMemberName(
    full: boolean,
    fuzzy: boolean,
    memberName: string
  ): Promise<OshiMember | OshiMember[]> {
    const params = new URLSearchParams({
      full: String(full),
      fuzzy: String(fuzzy),
      memberName: memberName || '',
    });
    return apiRequest<OshiMember | OshiMember[]>(
      `/api/oshi-members/list-member?${params.toString()}`
    );
  }

  async create(params: CreateOshiMemberParams): Promise<OshiMember> {
    return apiRequest<OshiMember>('/api/oshi-members/create', {
      method: 'POST',
      json: params,
    });
  }

  async update(params: UpdateOshiMemberParams): Promise<OshiMember> {
    return apiRequest<OshiMember>('/api/oshi-members/update', {
      method: 'POST',
      json: params,
    });
  }

  async delete(memberId: number): Promise<void> {
    return apiRequest<void>(`/api/oshi-members/delete/${memberId}`, {
      method: 'DELETE',
    });
  }
}
