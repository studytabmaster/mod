package net.essentialslike.config;

import java.util.ArrayList;
import java.util.List;

/**
 * config/essentials_like.json のデータクラス。
 * global: 管理者（OP / ホスト）のみ変更可能。
 * player: 各プレイヤーが変更可能（現状は全員共通のデフォルト値として読み込む。Phase 8 で個別化）。
 */
public class ModConfig {
    public int schemaVersion = 2;
    public Global global = new Global();
    public Player player = new Player();

    public static class Global {
        // ---- 機能ON/OFF ----
        public boolean oreBreaker = true;
        public boolean stoneBreaker = true;
        public boolean sandBreaker = true;
        public boolean treeBreaker = true;
        public boolean leavesBreaker = false;
        public boolean cropBreaker = true;
        public boolean cropReplanting = true;
        public boolean treeReplanting = true;
        public boolean lavaReflow = true;
        public boolean breakableSpawner = true;
        public boolean autoUpdatingMap = false;
        public boolean privateDeathpoint = false;
        public boolean lockableChest = false;
        public boolean chunkBorderHud = false;

        // ---- 危険設定・調整 ----
        public boolean obsidianInStoneBreaker = true;
        public boolean treeBreakerFixedDurability = true;
        public boolean treeBreakerLimitless = false;
        public boolean disenchanterRemovesCurse = true;
        /** 1〜20 は1tickあたりの破壊数、0 は MSPT に応じた自動調整 */
        public int breakerSpeedLimit = 0;
        /** 1回の連鎖の総上限 1〜4096 */
        public int maxChainBlocks = 512;
        /** デスウェイポイントのタイムアウト（分）20〜600 */
        public int deathWaypointTimeoutMinutes = 360;
    }

    public static class Player {
        public boolean oreInvertSneak = false;
        public boolean stoneInvertSneak = false;
        public boolean sandInvertSneak = false;
        public boolean treeInvertSneak = false;
        /** 鉱石ブレーカーの探索半径 1〜8 */
        public int oreBreakerRadius = 5;
        /** ストーン・サンドブレーカーの範囲 各1〜3 */
        public int breakerSizeW = 3;
        public int breakerSizeH = 3;
        public int breakerSizeD = 3;
        /** 砂利の対象 */
        public boolean sandGravel = true;
        /** ソウルサンド・ソウルソイルの対象 */
        public boolean sandSoulSandAndSoil = false;
        /** ストーンブレーカーで対象外にするブロックID（例: "minecraft:granite"） */
        public List<String> stoneDisabledBlocks = new ArrayList<>();
        /** 食料情報の詳細度 0〜3 */
        public int foodInfoDetail = 1;
    }
}
