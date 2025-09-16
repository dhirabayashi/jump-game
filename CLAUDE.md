# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## プロジェクト概要

KotlinとJava Swingで構築された2Dプラットフォーマーゲーム。プレイヤーはブルーのキャラクターを操作し、プラットフォーム間をジャンプして移動する。

## ビルドシステムとコマンド

### 主要コマンド

```bash
# プロジェクトのビルド
./gradlew build

# アプリケーションの実行
./gradlew run

# 全テストの実行
./gradlew test

# 特定テストクラスの実行
./gradlew test --tests "com.jumpgame.core.GameWorldTest"

# 特定テストメソッドの実行
./gradlew test --tests "*falls through pits*"

# クリーンビルド
./gradlew clean build

# 実行可能JARの作成
./gradlew jar
```

### プロジェクト設定

- **言語**: Kotlin 1.9.10
- **JVMターゲット**: Java 17
- **ビルドツール**: Gradle with Kotlin DSL
- **テストフレームワーク**: JUnit 5 + kotlin-test
- **エントリーポイント**: `com.jumpgame.ui.MainKt`

## アーキテクチャ構造

### 3層アーキテクチャ

**UIレイヤー** (`com.jumpgame.ui`)
- `Main.kt`: アプリケーションエントリーポイント、SwingベースのGUI setup
- `GamePanel.kt`: ゲーム描画、タイマーベースのゲームループ（60 FPS）、キー入力処理

**コアレイヤー** (`com.jumpgame.core`)
- `GameWorld.kt`: 中央制御クラス、物理演算、衝突検知、プラットフォーム生成
- `Player.kt`: プレイヤーキャラクター、移動・ジャンプロジック
- `Enemy.kt`: 敵キャラクター、AI・衝突判定
- `InputHandler.kt`: キーボード入力の状態管理

**ユーティリティレイヤー** (`com.jumpgame.util`)
- `Vector2D.kt`: 2D座標・ベクトル計算

### 重要な設計概念

**プラットフォームシステム**
- 通常プラットフォーム: 地上レベルの基本地形
- 浮遊プラットフォーム: 空中に配置された8つの浮遊する足場
- 階段システム: 昇り・降り階段の自動生成
- `Platform` データクラス: startX, endX, y, thickness プロパティ

**衝突検知システム**
- 4方向完全衝突検知（上下左右）
- 速度ベース衝突解決
- `checkVerticalCollisions()`: 上下衝突（着地・頭上接触）
- `checkSideCollisions()`: 左右衝突（壁押し付け）
- プラットフォーム貫通防止機能

**物理システム**
- Delta time ベース物理シミュレーション
- 重力システム（落下死判定含む）
- プレイヤー境界制限
- 残機システム（初期5ライフ）

### テストアーキテクチャ

包括的テストスイート（計111テスト）:
- `GameWorldTest.kt`: 37テスト - ゲーム世界、衝突検知、プラットフォームロジック
- `PlayerTest.kt`: 20テスト - プレイヤー移動、物理、状態管理
- `EnemyTest.kt`: 16テスト - 敵キャラクター動作
- `InputHandlerTest.kt`: 15テスト - 入力処理
- `GamePanelTest.kt`: 12テスト - UI コンポーネント
- `Vector2DTest.kt`: 11テスト - 数学ユーティリティ

## 開発時の重要ポイント

**衝突検知の変更**
このプロジェクトでは最近、基本的な上方向のみの衝突検知から、全方向対応の包括的システムに完全に再設計された。プラットフォーム関連のコードを変更する際は、すべてのテストが通ることを確認すること。

**プラットフォーム座標系**
- ゲーム世界: 800×600ピクセル
- 地面レベル: Y=400
- プレイヤーサイズ: 32×48ピクセル
- プラットフォーム thickness: 20ピクセル

**テスト実行の注意**
物理シミュレーションを含むため、一部のテストは実行時間に依存する。テスト失敗時は複数回実行して安定性を確認すること。

**ゲームループ**
UIレイヤーの `GamePanel` が `Timer(16, this)` で約60FPSのゲームループを管理。ゲームロジックは `GameWorld.update()` で実行される。