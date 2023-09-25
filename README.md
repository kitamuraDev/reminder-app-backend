# リポジトリについて

- リマインダーアプリのバックエンド（WebAPI）
- スキーマ駆動（OpenAPI generator）
- SpringBoot（Java） + MyBatis（PostgreSQL）
- ビルドツールは Gradle（gradlew）を使用
- リンターは Spotless（[公式ドキュメント](https://github.com/diffplug/spotless/tree/main/plugin-gradle) ）を使用

## セットアップ

1. `git clone https://github.com/kitamuraDev/reminder-app-backend.git`
2. プロジェクトへ移動
3. `./gradlew installPreCommitScript`

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

## 【モデル】Record, Entity, DTO について

- Record
  - DB のテーブルを表したモデル
  - DB のテーブルと互換性がある
  - DB からデータを取得するときに格納する
- Entity
  - ビジネスロジック層（service）で扱うモデル
  - Record から Entity へ変換する
  - Record をそのままビジネスロジック層でも扱っても良いが、クライアントへ返す DTO と Record のデータ型が違う場合に、その差異を吸収する役割として間に Entity を噛ませる
  - ↑↑ 間に噛ませることで、DTO と Record が互いに影響を受けず、疎結合にできる
- DTO
  - クライアントへ返す最終的なモデル
  - Response でも良いかもしれない

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
