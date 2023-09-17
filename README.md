# リポジトリについて

- リマインダーアプリのバックエンド（WebAPI）
- スキーマ駆動（OpenAPI generator）
- SpringBoot（Java） + MyBatis（PostgreSQL）
- ビルドツールは Gradle（gradlew）を使用
- リンターは Spotless を使用

## テーブル仕様

```sql
CREATE TABLE reminder (
  id SERIAL PRIMARY KEY,
  title VARCHAR(256) NOT NULL,
  description TEXT NOT NULL,
  due_date DATE NOT NULL,
  priority INT CHECK (priority >= 0 AND priority <= 2),
  is_completed BOOLEAN DEFAULT false,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## よく使う Gradle コマンド

1. 実行

```
./gradlew bootRun
```

2. ビルド

```
./gradlew build
```

3. 依存関係を出力

```
./gradlew dependencies
```

4. 利用可能なタスクを出力

```
./gradlew tasks
```

5. タスクの実行

```
./gradlew <task-name>
```
