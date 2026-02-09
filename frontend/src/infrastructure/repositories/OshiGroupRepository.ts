/**
 * 推しグループリポジトリ実装
 * Infrastructure層：APIを使ったデータアクセス実装
 */

import type { OshiGroup } from '../../domain/entities/OshiGroup';
import type {
  IOshiGroupRepository,
  CreateOshiGroupParams,
  UpdateOshiGroupParams,
} from '../../domain/repositories/IOshiGroupRepository';
import { apiRequest } from '../api/client';

export class OshiGroupRepository implements IOshiGroupRepository {
  async findByGroupName(full: boolean, fuzzy: boolean, groupName: string): Promise<OshiGroup[]> {
    const params = new URLSearchParams({
      full: String(full),
      fuzzy: String(fuzzy),
      groupName: groupName || '',
    });
    return apiRequest<OshiGroup[]>(`/api/oshi-groups/list-group?${params.toString()}`);
  }

  async findByCompany(company: string): Promise<OshiGroup[]> {
    const params = new URLSearchParams({ company: company || '' });
    return apiRequest<OshiGroup[]>(`/api/oshi-groups/list-company?${params.toString()}`);
  }

  async create(params: CreateOshiGroupParams): Promise<OshiGroup> {
    return apiRequest<OshiGroup>('/api/oshi-groups/create', {
      method: 'POST',
      json: params,
    });
  }

  async update(params: UpdateOshiGroupParams): Promise<OshiGroup> {
    return apiRequest<OshiGroup>('/api/oshi-groups/update', {
      method: 'POST',
      json: params,
    });
  }

  async delete(groupId: number): Promise<void> {
    return apiRequest<void>(`/api/oshi-groups/delete/${groupId}`, {
      method: 'DELETE',
    });
  }
}
