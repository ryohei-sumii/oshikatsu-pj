# ファイル移行マップ

このドキュメントは、旧構成から新しいクリーンアーキテクチャへのファイル移行を記録しています。

## 移行済みファイル

### Domain層（新規作成）

| 新しいパス | 説明 |
|---------|------|
| `src/domain/entities/OshiGroup.ts` | 推しグループエンティティ（新規） |
| `src/domain/entities/OshiMember.ts` | 推しメンバーエンティティ（新規） |
| `src/domain/entities/User.ts` | ユーザーエンティティ（新規） |
| `src/domain/repositories/IOshiGroupRepository.ts` | 推しグループリポジトリIF（新規） |
| `src/domain/repositories/IOshiMemberRepository.ts` | 推しメンバーリポジトリIF（新規） |
| `src/domain/repositories/IAuthRepository.ts` | 認証リポジトリIF（新規） |

### Application層

| 旧パス | 新しいパス | 変更内容 |
|-------|---------|---------|
| `src/lib/validation.ts` | `src/application/validators/oshiGroupValidator.ts` | 推しグループバリデーション抽出 |
| `src/lib/validation.ts` | `src/application/validators/oshiMemberValidator.ts` | 推しメンバーバリデーション抽出 |
| `src/lib/validation.ts` | `src/application/validators/authValidator.ts` | 認証バリデーション抽出 |
| `src/hooks/useOshiGroup.ts` | `src/application/usecases/oshiGroup/useOshiGroupUseCases.ts` | ユースケースとして再実装 |
| `src/hooks/useOshiMember.ts` | `src/application/usecases/oshiMember/useOshiMemberUseCases.ts` | ユースケースとして再実装 |

### Infrastructure層

| 旧パス | 新しいパス | 変更内容 |
|-------|---------|---------|
| `src/lib/apiClient.ts` | `src/infrastructure/api/client.ts` | APIクライアント移行 |
| `src/api/oshiGroupApi.ts` | `src/infrastructure/repositories/OshiGroupRepository.ts` | リポジトリパターンで再実装 |
| `src/api/oshiMemberApi.ts` | `src/infrastructure/repositories/OshiMemberRepository.ts` | リポジトリパターンで再実装 |
| - | `src/infrastructure/repositories/AuthRepository.ts` | 認証リポジトリ（新規） |

### Presentation層

| 旧パス | 新しいパス | 変更内容 |
|-------|---------|---------|
| `src/pages/OshiGroupPage.tsx` | `src/presentation/pages/oshiGroup/OshiGroupPage.tsx` | DIとユースケース使用に更新 |
| `src/pages/OshiGroupPage.css` | `src/presentation/pages/oshiGroup/OshiGroupPage.css` | 移動のみ |
| `src/pages/OshiMemberPage.tsx` | `src/presentation/pages/oshiMember/OshiMemberPage.tsx` | DIとユースケース使用に更新 |
| `src/pages/OshiMemberPage.css` | `src/presentation/pages/oshiMember/OshiMemberPage.css` | 移動のみ |
| `src/components/ErrorMessage.tsx` | `src/presentation/components/ErrorMessage.tsx` | インポートパス更新 |
| `src/components/AppLayout.tsx` | `src/presentation/components/AppLayout.tsx` | インポートパス更新 |
| `src/components/AppLayout.css` | `src/presentation/components/AppLayout.css` | 移動のみ |
| `src/components/ProtectedRoute.tsx` | `src/presentation/components/ProtectedRoute.tsx` | インポートパス更新 |

### Shared層

| 旧パス | 新しいパス | 変更内容 |
|-------|---------|---------|
| `src/lib/formatters.ts` | `src/shared/formatters.ts` | 移動のみ |

### 既存ファイル（更新済み）

| ファイルパス | 変更内容 |
|-----------|---------|
| `src/App.tsx` | インポートパスを新構成に更新 |
| `src/contexts/AuthContext.tsx` | インポートパスを更新 |
| `src/pages/LoginPage.tsx` | インポートパスを更新 |
| `src/pages/RegisterPage.tsx` | インポートパスを更新 |
| `src/api/authApi.ts` | インポートパスを更新（後方互換性のため残存） |

## 削除予定ファイル

以下のファイルは新しい構成に移行済みのため、削除可能です：

```
src/lib/validation.ts       → application/validators/* に分割
src/lib/apiClient.ts        → infrastructure/api/client.ts に移行
src/hooks/useOshiGroup.ts   → application/usecases/oshiGroup/* に移行
src/hooks/useOshiMember.ts  → application/usecases/oshiMember/* に移行
src/api/oshiGroupApi.ts     → infrastructure/repositories/OshiGroupRepository.ts に移行
src/api/oshiMemberApi.ts    → infrastructure/repositories/OshiMemberRepository.ts に移行
src/types/api.ts           → domain/entities/* に移行（一部は後方互換性のため残存）
```

## 移行後のインポート例

### 旧構成
```typescript
import { useOshiGroup } from '../hooks/useOshiGroup';
import { ApiError } from '../lib/apiClient';
import { oshiGroupFormSchema } from '../lib/validation';
```

### 新構成
```typescript
import { useOshiGroupUseCases } from '../application/usecases/oshiGroup/useOshiGroupUseCases';
import { OshiGroupRepository } from '../infrastructure/repositories/OshiGroupRepository';
import { ApiError } from '../infrastructure/api/client';
import { oshiGroupFormSchema } from '../application/validators/oshiGroupValidator';
```

## 移行チェックリスト

### 完了済み
- [x] Domain層の作成
- [x] Application層の作成（Validators, UseCases）
- [x] Infrastructure層の作成（API Client, Repositories）
- [x] Presentation層の整理（Components, Pages）
- [x] Shared層の作成
- [x] OshiGroup機能の移行
- [x] OshiMember機能の移行
- [x] 既存ファイルのインポートパス更新
- [x] Linterエラーの解消
- [x] アーキテクチャドキュメントの作成

### 今後の作業
- [ ] LoginPage/RegisterPageのPresentation層への移行
- [ ] DashboardPageの実装と移行
- [ ] 旧ファイルの削除
- [ ] ユニットテストの追加
- [ ] E2Eテストの追加
- [ ] Storybookの設定（オプション）

## 注意事項

1. **段階的移行**: すべてのファイルを一度に移行する必要はありません。機能ごとに段階的に移行できます。

2. **後方互換性**: `src/api/authApi.ts` などの一部ファイルは、既存コードの互換性のため残しています。

3. **テスト**: 各機能の移行後、動作確認を行ってください。

4. **パフォーマンス**: リポジトリのインスタンス化は `useMemo` で最適化されています。

5. **型安全性**: すべての層で厳密な型チェックが有効です。

## サポート

質問や問題がある場合は、`ARCHITECTURE.md` を参照してください。
