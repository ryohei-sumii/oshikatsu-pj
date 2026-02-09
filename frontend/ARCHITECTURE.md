# フロントエンドアーキテクチャ

このプロジェクトは **クリーンアーキテクチャ** と **ドメイン駆動設計（DDD）** の原則に基づいて構成されています。

## ディレクトリ構造

```
src/
├── domain/                    # ドメイン層
│   ├── entities/             # ドメインエンティティ
│   │   ├── OshiGroup.ts     # 推しグループエンティティ
│   │   ├── OshiMember.ts    # 推しメンバーエンティティ
│   │   └── User.ts          # ユーザーエンティティ
│   └── repositories/        # リポジトリインターフェース
│       ├── IOshiGroupRepository.ts
│       ├── IOshiMemberRepository.ts
│       └── IAuthRepository.ts
│
├── application/              # アプリケーション層
│   ├── validators/          # バリデーションロジック
│   │   ├── oshiGroupValidator.ts
│   │   ├── oshiMemberValidator.ts
│   │   └── authValidator.ts
│   └── usecases/           # ユースケース（ビジネスロジック）
│       ├── oshiGroup/
│       │   └── useOshiGroupUseCases.ts
│       └── oshiMember/
│           └── useOshiMemberUseCases.ts
│
├── infrastructure/          # インフラストラクチャ層
│   ├── api/                # APIクライアント
│   │   └── client.ts      # 共通HTTPクライアント
│   └── repositories/       # リポジトリ実装
│       ├── OshiGroupRepository.ts
│       ├── OshiMemberRepository.ts
│       └── AuthRepository.ts
│
├── presentation/            # プレゼンテーション層
│   ├── components/         # 共通UIコンポーネント
│   │   ├── ErrorMessage.tsx
│   │   ├── ProtectedRoute.tsx
│   │   └── AppLayout.tsx
│   └── pages/             # ページコンポーネント
│       ├── oshiGroup/
│       │   ├── OshiGroupPage.tsx
│       │   └── OshiGroupPage.css
│       └── oshiMember/
│           ├── OshiMemberPage.tsx
│           └── OshiMemberPage.css
│
├── shared/                 # 共有ユーティリティ
│   └── formatters.ts      # フォーマッター関数
│
├── contexts/              # React Context（横断的関心事）
│   └── AuthContext.tsx   # 認証コンテキスト
│
└── pages/                # レガシーページ（移行中）
    ├── LoginPage.tsx
    ├── RegisterPage.tsx
    └── DashboardPage.tsx
```

## 各層の責務

### 1. Domain層（ドメイン層）

**責務**: ビジネスルールとドメインの概念を表現

- **Entities**: ビジネスの中心概念を表すイミュータブルなデータ構造
- **Repository Interfaces**: データアクセスの抽象化（実装は Infrastructure 層）

**依存関係**: なし（他の層に依存しない）

**例**:
```typescript
// domain/entities/OshiGroup.ts
export interface OshiGroup {
  readonly id: number;
  readonly groupName: string;
  // ...
}

// domain/repositories/IOshiGroupRepository.ts
export interface IOshiGroupRepository {
  findByGroupName(full: boolean, fuzzy: boolean, groupName: string): Promise<OshiGroup[]>;
  create(params: CreateOshiGroupParams): Promise<OshiGroup>;
  // ...
}
```

### 2. Application層（アプリケーション層）

**責務**: ユースケースの実装とビジネスロジックの調整

- **Validators**: 入力値のバリデーションルール（Zodスキーマ）
- **UseCases**: ビジネスロジックの実装（Reactカスタムフックとして実装）

**依存関係**: Domain層のみに依存

**例**:
```typescript
// application/validators/oshiGroupValidator.ts
export const oshiGroupFormSchema = z.object({
  groupName: z.string().min(1, 'グループ名を入力してください'),
  // ...
});

// application/usecases/oshiGroup/useOshiGroupUseCases.ts
export function useOshiGroupUseCases(repository: IOshiGroupRepository) {
  // ビジネスロジックの実装
  const runSearch = async () => { /* ... */ };
  const handleModalSubmit = async () => { /* ... */ };
  return { /* ... */ };
}
```

### 3. Infrastructure層（インフラストラクチャ層）

**責務**: 外部システムとの通信実装

- **API Client**: HTTPクライアントの実装
- **Repository Implementations**: Domain層のリポジトリインターフェースの具体実装

**依存関係**: Domain層に依存（インターフェースを実装）

**例**:
```typescript
// infrastructure/api/client.ts
export async function apiRequest<T>(path: string, options?: RequestOptions): Promise<T> {
  // HTTPリクエストの実装
}

// infrastructure/repositories/OshiGroupRepository.ts
export class OshiGroupRepository implements IOshiGroupRepository {
  async findByGroupName(full: boolean, fuzzy: boolean, groupName: string): Promise<OshiGroup[]> {
    return apiRequest<OshiGroup[]>(`/api/oshi-groups/list-group?...`);
  }
  // ...
}
```

### 4. Presentation層（プレゼンテーション層）

**責務**: UIの表示とユーザーインタラクション

- **Components**: 共通UIコンポーネント
- **Pages**: ページコンポーネント

**依存関係**: Application層とInfrastructure層に依存

**例**:
```typescript
// presentation/pages/oshiGroup/OshiGroupPage.tsx
export function OshiGroupPage() {
  // DIコンテナ的な役割：リポジトリのインスタンス化
  const repository = useMemo(() => new OshiGroupRepository(), []);
  
  // ユースケースの使用
  const { searchType, groups, handleSearch, /* ... */ } = useOshiGroupUseCases(repository);
  
  // UIのレンダリング
  return <div>...</div>;
}
```

### 5. Shared層（共有ユーティリティ）

**責務**: 全層で使用可能な共通ユーティリティ

- フォーマッター関数
- 定数定義
- 共通型定義

**依存関係**: なし

## 依存関係の方向

```
Presentation ──→ Application ──→ Domain
      │                            ↑
      └──→ Infrastructure ─────────┘
```

**依存性逆転の原則（DIP）**:
- Infrastructure層は Domain層のインターフェースを実装
- Application層と Presentation層は抽象（インターフェース）に依存

## 設計原則

### 1. 単一責任の原則（SRP）
各モジュールは単一の責任を持つ

### 2. 依存性逆転の原則（DIP）
- 上位層は下位層の実装に依存しない
- 抽象（インターフェース）に依存する

### 3. インターフェース分離の原則（ISP）
- リポジトリは必要なメソッドのみを定義
- クライアントは使用しないメソッドに依存しない

### 4. 開放閉鎖の原則（OCP）
- 拡張に対して開いている
- 修正に対して閉じている

## テスタビリティ

### ユースケースのテスト
```typescript
// モックリポジトリを使用してユースケースをテスト
const mockRepository: IOshiGroupRepository = {
  findByGroupName: jest.fn(),
  create: jest.fn(),
  // ...
};

const { result } = renderHook(() => useOshiGroupUseCases(mockRepository));
```

### リポジトリのテスト
```typescript
// APIクライアントをモックしてリポジトリをテスト
jest.mock('../api/client');
const repository = new OshiGroupRepository();
await repository.findByGroupName(true, false, 'テスト');
```

## 移行ガイドライン

### 既存コードからの移行手順

1. **Domain層の作成**
   - エンティティ定義
   - リポジトリインターフェース定義

2. **Infrastructure層の実装**
   - リポジトリの具体実装
   - APIクライアントの移行

3. **Application層の作成**
   - バリデーターの移行
   - ユースケースの実装

4. **Presentation層の更新**
   - ページコンポーネントの更新
   - DIの実装

## ベストプラクティス

### 1. リポジトリのインスタンス化
```typescript
// ページコンポーネント内で useMemo を使用
const repository = useMemo(() => new OshiGroupRepository(), []);
```

### 2. エラーハンドリング
```typescript
// Application層でビジネスエラーを処理
// Infrastructure層で技術的エラーを処理
```

### 3. 型安全性
```typescript
// すべての境界でstrictな型定義を使用
// Domain層のエンティティは readonly を使用
```

### 4. バリデーション
```typescript
// Application層で Zod を使用したバリデーション
// 早期バリデーションの原則
```

## まとめ

このアーキテクチャにより：

✅ **保守性**: 各層が独立しており、変更の影響範囲が明確
✅ **テスタビリティ**: モックやスタブで容易にテスト可能
✅ **拡張性**: 新機能追加が既存コードに影響しない
✅ **可読性**: 責務が明確で理解しやすい
✅ **再利用性**: ドメインロジックとUIが分離されている

このアーキテクチャは、スケーラブルで保守しやすいフロントエンドアプリケーションの構築を可能にします。
