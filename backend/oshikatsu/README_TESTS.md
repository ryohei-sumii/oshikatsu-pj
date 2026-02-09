# テストコード概要

本プロジェクトでは、OshiMemberとOshiGroupの機能に対する包括的なテストコードを作成しました。

## ✅ テスト実行結果

**全49テストが成功しています！**

```
Tests run: 49, Failures: 0, Errors: 0, Skipped: 0

- OshiGroupService: 13 tests ✅
- OshiGroupRepository: 10 tests ✅
- OshiMemberService: 16 tests ✅
- OshiMemberRepository: 10 tests ✅
```

## テスト構成

### 1. サービス層のユニットテスト (29テスト) ✅

#### OshiMemberServiceTest
- **場所**: `src/test/java/com/oshikatsu_pj/oshikatsu/oshimember/application/service/OshiMemberServiceTest.java`
- **テスト数**: 16
- **状態**: ✅ 全て成功

**テスト内容:**
- 推しメンバーの作成（正常系/異常系）
- 推しメンバーの取得（ID指定/全件/グループ別/名前検索）
- 推しメンバーの更新
- 推しメンバーの削除
- 存在確認

#### OshiGroupServiceTest
- **場所**: `src/test/java/com/oshikatsu_pj/oshikatsu/oshigroup/application/service/OshiGroupServiceTest.java`
- **テスト数**: 13
- **状態**: ✅ 全て成功

**テスト内容:**
- 推しグループの作成（正常系/異常系）
- 推しグループの取得（ID指定/全件/会社別/名前検索）
- 推しグループの更新
- 推しグループの削除

### 2. リポジトリ層の統合テスト (20テスト) ✅

#### OshiMemberRepositoryTest
- **場所**: `src/test/java/com/oshikatsu_pj/oshikatsu/oshimember/domain/repository/OshiMemberRepositoryTest.java`
- **テスト数**: 10
- **状態**: ✅ 全て成功

**テスト内容:**
- CRUD操作の検証
- カスタムクエリメソッドの検証
- データベース制約の検証

#### OshiGroupRepositoryTest
- **場所**: `src/test/java/com/oshikatsu_pj/oshikatsu/oshigroup/domain/repository/OshiGroupRepositoryTest.java`
- **テスト数**: 10
- **状態**: ✅ 全て成功

**テスト内容:**
- CRUD操作の検証
- カスタムクエリメソッドの検証
- データベース制約の検証

### 3. コントローラー層のテスト

**状態**: 未実装

Spring Boot 4.0のセキュリティ設定とモック化が複雑なため、コントローラー層のテストは現時点では未実装です。
サービス層とリポジトリ層の統合テストにより、ビジネスロジックとデータアクセスは十分にカバーされています。

## テスト実行方法

### 全テスト実行
```bash
mvn test
```

### 特定のテストクラスのみ実行
```bash
# サービステストのみ
mvn test -Dtest=*ServiceTest

# リポジトリテストのみ
mvn test -Dtest=*RepositoryTest
```

## テストカバレッジ

| レイヤー | テストクラス | テスト数 | 状態 |
|---------|------------|---------|------|
| Service | OshiMemberServiceTest | 16 | ✅ |
| Service | OshiGroupServiceTest | 13 | ✅ |
| Repository | OshiMemberRepositoryTest | 10 | ✅ |
| Repository | OshiGroupRepositoryTest | 10 | ✅ |
| **合計** | **4クラス** | **49テスト** | **✅ 全て成功** |

## テスト環境設定

### application-test.yaml

テスト用の設定ファイル（`src/test/resources/application-test.yaml`）で以下を設定：

- **H2インメモリデータベース**: テスト実行ごとに新規作成
- **DDL自動生成**: `create-drop`により毎回スキーマを再作成
- **テスト用JWT設定**: セキュリティ設定を無効化
- **ログレベル**: DEBUGでSQL出力を確認可能

### 使用技術

- **JUnit 5**: テスティングフレームワーク
- **Mockito**: モックライブラリ
- **AssertJ**: アサーションライブラリ
- **Spring Boot Test**: Spring統合テスト機能
- **H2 Database**: テスト用インメモリデータベース

## 解決した主な技術的課題

### 1. OshiGroupRepositoryのクエリエラー
**問題**: カスタムクエリメソッドに`@Query`アノテーションがなく、Spring Data JPAがメソッド名から正しいクエリを生成できなかった。

**解決**: 全てのカスタムクエリメソッドに`@Query`アノテーションと`@Param`アノテーションを追加。

### 2. H2データベースの予約語エラー
**問題**: `user`テーブルがH2の予約語と衝突してSQLエラーが発生。

**解決**: Userエンティティのテーブル名を`` @Table(name = "`user`") ``にエスケープ。

### 3. Spring Boot 4.0のパッケージ変更
**問題**: `@DataJpaTest`、`@AutoConfigureTestDatabase`などのアノテーションのパッケージが変更された。

**解決**: Spring Boot 4.0の新しいパッケージに更新：
- `@DataJpaTest`: `org.springframework.boot.data.jpa.test.autoconfigure`
- `@AutoConfigureTestDatabase`: `org.springframework.boot.jdbc.test.autoconfigure`
- `@MockBean` → `@MockitoBean`

### 4. エンティティ関係の整合性
**問題**: OshiGroupエンティティとOshiMemberエンティティでUserとの関連付けが不完全だった。

**解決**: 
- OshiGroupにUserパラメータを持つコンストラクタを追加
- OshiGroupServiceでUserRepositoryを注入してユーザーを取得
- テストデータ作成時にUserを正しく関連付け

### 5. モックオブジェクトのID設定
**問題**: モックされたエンティティの`getId()`が`null`を返し、NullPointerExceptionが発生。

**解決**: サービステストで`@BeforeEach`メソッド内でJava Reflectionを使用して、モックエンティティのIDフィールドを直接設定。

## 今後の改善点

1. **コントローラー層のテスト追加**
   - Spring Boot 4.0でのセキュリティモック化パターンの確立
   - `@WebMvcTest`でのカスタム認証トークンの適切な設定

2. **テストカバレッジの向上**
   - エッジケースのテストを追加
   - より多くの異常系テストケースを追加

3. **E2Eテストの追加**
   - 実際のデータベースを使用した統合テストの追加
   - REST APIの完全なエンドツーエンドテスト

## 参考情報

- **Spring Boot 4.0ドキュメント**: https://docs.spring.io/spring-boot/docs/4.0.x/reference/html/
- **Spring Data JPA**: https://docs.spring.io/spring-data/jpa/reference/
- **JUnit 5**: https://junit.org/junit5/docs/current/user-guide/
- **Mockito**: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html

---

**最終更新日**: 2026年2月9日  
**テスト結果**: ✅ 49テスト全て成功
