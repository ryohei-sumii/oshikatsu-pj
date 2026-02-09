# Oshikatsu Backend - Docker環境構築ガイド

## 概要

このドキュメントでは、Oshikatsu BackendアプリケーションをDockerで実行する方法を説明します。

## 前提条件

- Docker Desktop（Windows）がインストールされていること
- Docker Composeが利用可能であること

## 環境構築手順

### 1. 環境変数ファイルの作成

`.env.docker.example`をコピーして`.env.docker`を作成し、必要に応じて値を変更します。

```powershell
Copy-Item .env.docker.example .env.docker
```

**重要**: 本番環境では必ず以下の値を変更してください：
- `DB_PASSWORD`
- `DB_ROOT_PASSWORD`
- `JWT_SECRET`

### 2. アプリケーションの起動

```powershell
# 環境変数を読み込んで起動
docker-compose --env-file .env.docker up -d
```

または、開発環境（phpMyAdmin含む）で起動する場合：

```powershell
docker-compose --env-file .env.docker --profile dev up -d
```

### 3. ログの確認

```powershell
# 全サービスのログを表示
docker-compose logs -f

# アプリケーションのみ
docker-compose logs -f app

# データベースのみ
docker-compose logs -f db
```

### 4. 停止と削除

```powershell
# 停止
docker-compose down

# 停止 + ボリューム削除（データベースのデータも削除される）
docker-compose down -v
```

## アクセスURL

- **アプリケーション**: http://localhost:8080
- **phpMyAdmin**（開発環境）: http://localhost:8081

## よくあるコマンド

### データベースに直接接続

```powershell
docker-compose exec db mysql -u oshiuser -p oshikatsu_db
```

### アプリケーションコンテナに入る

```powershell
docker-compose exec app sh
```

### ビルドキャッシュをクリアして再ビルド

```powershell
docker-compose build --no-cache app
docker-compose up -d app
```

### データベースのバックアップ

```powershell
docker-compose exec db mysqldump -u oshiuser -p oshikatsu_db > backup.sql
```

### データベースのリストア

```powershell
Get-Content backup.sql | docker-compose exec -T db mysql -u oshiuser -p oshikatsu_db
```

## トラブルシューティング

### アプリケーションが起動しない

1. ログを確認：
```powershell
docker-compose logs app
```

2. データベースの接続確認：
```powershell
docker-compose exec db mysql -u oshiuser -p -e "SHOW DATABASES;"
```

3. 環境変数の確認：
```powershell
docker-compose config
```

### データベースの初期化

データベースを完全にリセットする場合：

```powershell
docker-compose down -v
docker-compose --env-file .env.docker up -d
```

### ポートが既に使用されている

`.env.docker`で別のポートを指定：

```
APP_PORT=8081
DB_PORT=3308
```

## 初期データの投入（オプション）

初期データを投入する場合は、`init-db`ディレクトリにSQLファイルを配置してください：

```
backend/
  ├── init-db/
  │   ├── 01-schema.sql
  │   └── 02-seed-data.sql
  └── docker-compose.yml
```

これらのファイルは、データベースコンテナの初回起動時に自動的に実行されます。

## 本番環境での推奨事項

1. **環境変数の管理**
   - `.env.docker`をバージョン管理に含めない（`.gitignore`に追加）
   - 本番環境では秘密情報管理ツール（AWS Secrets Manager、Azure Key Vaultなど）を使用

2. **セキュリティ**
   - 強力なパスワードを設定
   - JWT_SECRETを本番用に再生成
   - 不要なポートは公開しない

3. **パフォーマンス**
   - JVMメモリ設定を調整（Dockerfileの`ENTRYPOINT`で`-Xmx`等を追加）
   - データベースの接続プール設定を最適化

4. **監視とログ**
   - ログ集約ツール（ELKスタック、Grafana Loki等）の導入
   - ヘルスチェックエンドポイントの監視

5. **バックアップ**
   - 定期的なデータベースバックアップの設定
   - バックアップの復元テストの実施
