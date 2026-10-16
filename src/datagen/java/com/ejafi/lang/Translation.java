package com.ejafi.lang;

import static com.ejafi.Config.ALLOW_AUTO_CRAFT;
import static com.ejafi.Config.ENABLED_BY_DEFAULT;
import static com.ejafi.Config.ENFORCED_GROUND_BLACKLIST;
import static com.ejafi.Config.MINIMUM_TORCH_SPACING;
import static com.ejafi.Config.PREFERENCES;

import com.ejafi.Pathlighter;
import com.ejafi.toggle.ToggleText;
import java.util.Map;
import java.util.Objects;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

enum Translation {
    EN_US(
            "en_us",
            Map.ofEntries(
                    translateLabel(ENABLED_BY_DEFAULT, "On by Default"),
                    describeTooltip(ENABLED_BY_DEFAULT),
                    translateLabel(PREFERENCES.lightThreshold(), "Light Threshold"),
                    describeTooltip(PREFERENCES.lightThreshold()),
                    translateLabel(PREFERENCES.torchSpacing(), "Torch Spacing"),
                    describeTooltip(PREFERENCES.torchSpacing()),
                    translateLabel(PREFERENCES.requireMiningTool(), "Require Mining Tool"),
                    describeTooltip(PREFERENCES.requireMiningTool()),
                    translateLabel(PREFERENCES.pauseWhileSneaking(), "Pause While Sneaking"),
                    describeTooltip(PREFERENCES.pauseWhileSneaking()),
                    translateLabel(PREFERENCES.groundBlacklist(), "Ground Blacklist"),
                    describeTooltip(PREFERENCES.groundBlacklist()),
                    translateListButton(PREFERENCES.groundBlacklist(), "Edit List"),
                    translateLabel(PREFERENCES.autoCraft(), "Auto-Craft Torches"),
                    describeTooltip(PREFERENCES.autoCraft()),
                    translateLabel(PREFERENCES.coalReserve(), "Coal Reserve"),
                    describeTooltip(PREFERENCES.coalReserve()),
                    translateLabel(PREFERENCES.stickReserve(), "Stick Reserve"),
                    describeTooltip(PREFERENCES.stickReserve()),
                    translateLabel(ALLOW_AUTO_CRAFT, "Allow Auto-Crafting"),
                    describeTooltip(ALLOW_AUTO_CRAFT),
                    translateLabel(MINIMUM_TORCH_SPACING, "Minimum Torch Spacing"),
                    describeTooltip(MINIMUM_TORCH_SPACING),
                    translateLabel(ENFORCED_GROUND_BLACKLIST, "Enforced Ground Blacklist"),
                    describeTooltip(ENFORCED_GROUND_BLACKLIST),
                    translateListButton(ENFORCED_GROUND_BLACKLIST, "Edit List"),
                    translateScreenTitle("Pathlighter Configuration"),
                    translateSection(ModConfig.Type.CLIENT, "Player Settings"),
                    translateSectionTitle(ModConfig.Type.CLIENT, "Pathlighter Player Settings"),
                    translateSection(ModConfig.Type.SERVER, "Server Settings"),
                    translateSectionTitle(ModConfig.Type.SERVER, "Pathlighter Server Settings"),
                    Map.entry(ToggleText.SWITCHED_ON_KEY, ToggleText.SWITCHED_ON_ENGLISH),
                    Map.entry(ToggleText.SWITCHED_OFF_KEY, ToggleText.SWITCHED_OFF_ENGLISH),
                    Map.entry(ToggleText.UNAVAILABLE_KEY, "Pathlighter isn't installed on this server"),
                    Map.entry(ToggleText.KEY_CATEGORY, "Pathlighter"),
                    Map.entry(ToggleText.TOGGLE_KEY, "Toggle Pathlighter"))),
    DE_DE(
            "de_de",
            Map.ofEntries(
                    translateLabel(ENABLED_BY_DEFAULT, "Standardmäßig aktiv"),
                    translateTooltip(
                            ENABLED_BY_DEFAULT,
                            "Ob Pathlighter für Spieler aktiv ist, die es noch nicht selbst umgeschaltet haben"),
                    translateLabel(PREFERENCES.lightThreshold(), "Lichtschwelle"),
                    translateTooltip(
                            PREFERENCES.lightThreshold(),
                            "Platziert eine Fackel, wenn das Licht an den Füßen des Spielers höchstens diesen"
                                    + " Wert hat (0-15)"),
                    translateLabel(PREFERENCES.torchSpacing(), "Fackelabstand"),
                    translateTooltip(
                            PREFERENCES.torchSpacing(),
                            "Mindestabstand in Blöcken zwischen den Fackeln eines Spielers"),
                    translateLabel(PREFERENCES.requireMiningTool(), "Bergbauwerkzeug erforderlich"),
                    translateTooltip(
                            PREFERENCES.requireMiningTool(),
                            "Fackeln nur platzieren, wenn eine Spitzhacke, Schaufel, Axt oder Hacke gehalten wird"),
                    translateLabel(PREFERENCES.pauseWhileSneaking(), "Beim Schleichen pausieren"),
                    translateTooltip(PREFERENCES.pauseWhileSneaking(), "Beim Schleichen keine Fackeln platzieren"),
                    translateLabel(PREFERENCES.groundBlacklist(), "Boden-Blacklist"),
                    translateTooltip(
                            PREFERENCES.groundBlacklist(),
                            "Blöcke, auf die niemals Fackeln gesetzt werden (z. B. 'minecraft:glass')"),
                    translateListButton(PREFERENCES.groundBlacklist(), "Liste bearbeiten"),
                    translateLabel(PREFERENCES.autoCraft(), "Fackeln automatisch herstellen"),
                    translateTooltip(
                            PREFERENCES.autoCraft(),
                            "Stellt vier Fackeln aus einer Kohle oder Holzkohle und einem Stock her, wenn einem"
                                    + " Spieler die Fackeln ausgehen"),
                    translateLabel(PREFERENCES.coalReserve(), "Kohlereserve"),
                    translateTooltip(
                            PREFERENCES.coalReserve(),
                            "Kohle und Holzkohle, die beim automatischen Herstellen immer im Inventar bleiben"),
                    translateLabel(PREFERENCES.stickReserve(), "Stockreserve"),
                    translateTooltip(
                            PREFERENCES.stickReserve(),
                            "Stöcke, die beim automatischen Herstellen immer im Inventar bleiben"),
                    translateLabel(ALLOW_AUTO_CRAFT, "Automatisches Herstellen erlauben"),
                    translateTooltip(
                            ALLOW_AUTO_CRAFT,
                            "Ob Spieler Fackeln automatisch herstellen dürfen. Wenn aus, stellt kein Spieler"
                                    + " automatisch her, unabhängig von seiner eigenen Einstellung"),
                    translateLabel(MINIMUM_TORCH_SPACING, "Minimaler Fackelabstand"),
                    translateTooltip(
                            MINIMUM_TORCH_SPACING,
                            "Kleinster Fackelabstand, den ein Spieler verwenden kann. Wer weniger wählt, erhält"
                                    + " diesen Abstand"),
                    translateLabel(ENFORCED_GROUND_BLACKLIST, "Erzwungene Boden-Blacklist"),
                    translateTooltip(
                            ENFORCED_GROUND_BLACKLIST,
                            "Blöcke, auf die keine Fackeln eines Spielers gesetzt werden, zusätzlich zur eigenen"
                                    + " Blacklist jedes Spielers"),
                    translateListButton(ENFORCED_GROUND_BLACKLIST, "Liste bearbeiten"),
                    translateScreenTitle("Pathlighter-Konfiguration"),
                    translateSection(ModConfig.Type.CLIENT, "Spielereinstellungen"),
                    translateSectionTitle(ModConfig.Type.CLIENT, "Pathlighter-Spielereinstellungen"),
                    translateSection(ModConfig.Type.SERVER, "Servereinstellungen"),
                    translateSectionTitle(ModConfig.Type.SERVER, "Pathlighter-Servereinstellungen"),
                    Map.entry(ToggleText.SWITCHED_ON_KEY, "Pathlighter: Aktiviert"),
                    Map.entry(ToggleText.SWITCHED_OFF_KEY, "Pathlighter: Deaktiviert"),
                    Map.entry(ToggleText.UNAVAILABLE_KEY, "Pathlighter ist auf diesem Server nicht installiert"),
                    Map.entry(ToggleText.KEY_CATEGORY, "Pathlighter"),
                    Map.entry(ToggleText.TOGGLE_KEY, "Pathlighter umschalten"))),
    ES_ES(
            "es_es",
            Map.ofEntries(
                    translateLabel(ENABLED_BY_DEFAULT, "Activado por defecto"),
                    translateTooltip(
                            ENABLED_BY_DEFAULT,
                            "Si Pathlighter empieza activado para los jugadores que no lo han activado ni"
                                    + " desactivado por su cuenta"),
                    translateLabel(PREFERENCES.lightThreshold(), "Umbral de luz"),
                    translateTooltip(
                            PREFERENCES.lightThreshold(),
                            "Coloca una antorcha donde la luz a los pies del jugador es igual o inferior a este"
                                    + " nivel (0-15)"),
                    translateLabel(PREFERENCES.torchSpacing(), "Separación entre antorchas"),
                    translateTooltip(
                            PREFERENCES.torchSpacing(),
                            "Distancia mínima en bloques entre las antorchas que coloca un jugador"),
                    translateLabel(PREFERENCES.requireMiningTool(), "Requiere herramienta de minería"),
                    translateTooltip(
                            PREFERENCES.requireMiningTool(),
                            "Solo coloca antorchas al sostener un pico, una pala, un hacha o una azada"),
                    translateLabel(PREFERENCES.pauseWhileSneaking(), "Pausar al agacharse"),
                    translateTooltip(
                            PREFERENCES.pauseWhileSneaking(),
                            "Deja de colocar antorchas mientras el jugador está agachado"),
                    translateLabel(PREFERENCES.groundBlacklist(), "Lista negra de suelo"),
                    translateTooltip(
                            PREFERENCES.groundBlacklist(),
                            "Bloques sobre los que nunca se colocan antorchas (p. ej., 'minecraft:glass')"),
                    translateListButton(PREFERENCES.groundBlacklist(), "Editar lista"),
                    translateLabel(PREFERENCES.autoCraft(), "Fabricar antorchas automáticamente"),
                    translateTooltip(
                            PREFERENCES.autoCraft(),
                            "Fabrica cuatro antorchas con un carbón o carbón vegetal y un palo cuando a un"
                                    + " jugador se le acaban"),
                    translateLabel(PREFERENCES.coalReserve(), "Reserva de carbón"),
                    translateTooltip(
                            PREFERENCES.coalReserve(),
                            "Carbón y carbón vegetal que la fabricación automática siempre deja en el inventario"),
                    translateLabel(PREFERENCES.stickReserve(), "Reserva de palos"),
                    translateTooltip(
                            PREFERENCES.stickReserve(),
                            "Palos que la fabricación automática siempre deja en el inventario"),
                    translateLabel(ALLOW_AUTO_CRAFT, "Permitir fabricación automática"),
                    translateTooltip(
                            ALLOW_AUTO_CRAFT,
                            "Si los jugadores pueden fabricar antorchas automáticamente. Si está desactivado,"
                                    + " ningún jugador las fabrica, sea cual sea su propio ajuste"),
                    translateLabel(MINIMUM_TORCH_SPACING, "Separación mínima entre antorchas"),
                    translateTooltip(
                            MINIMUM_TORCH_SPACING,
                            "Separación más pequeña que puede usar un jugador. Quien elija menos recibe esta"
                                    + " separación"),
                    translateLabel(ENFORCED_GROUND_BLACKLIST, "Lista negra de suelo obligatoria"),
                    translateTooltip(
                            ENFORCED_GROUND_BLACKLIST,
                            "Bloques sobre los que nunca se colocan antorchas de ningún jugador, además de la"
                                    + " lista negra propia de cada jugador"),
                    translateListButton(ENFORCED_GROUND_BLACKLIST, "Editar lista"),
                    translateScreenTitle("Configuración de Pathlighter"),
                    translateSection(ModConfig.Type.CLIENT, "Ajustes del jugador"),
                    translateSectionTitle(ModConfig.Type.CLIENT, "Ajustes del jugador de Pathlighter"),
                    translateSection(ModConfig.Type.SERVER, "Ajustes del servidor"),
                    translateSectionTitle(ModConfig.Type.SERVER, "Ajustes del servidor de Pathlighter"),
                    Map.entry(ToggleText.SWITCHED_ON_KEY, "Pathlighter: Activado"),
                    Map.entry(ToggleText.SWITCHED_OFF_KEY, "Pathlighter: Desactivado"),
                    Map.entry(ToggleText.UNAVAILABLE_KEY, "Pathlighter no está instalado en este servidor"),
                    Map.entry(ToggleText.KEY_CATEGORY, "Pathlighter"),
                    Map.entry(ToggleText.TOGGLE_KEY, "Alternar Pathlighter"))),
    FR_FR(
            "fr_fr",
            Map.ofEntries(
                    translateLabel(ENABLED_BY_DEFAULT, "Activé par défaut"),
                    translateTooltip(
                            ENABLED_BY_DEFAULT,
                            "Indique si Pathlighter est activé pour les joueurs qui ne l'ont jamais activé ou"
                                    + " désactivé eux-mêmes"),
                    translateLabel(PREFERENCES.lightThreshold(), "Seuil de lumière"),
                    translateTooltip(
                            PREFERENCES.lightThreshold(),
                            "Place une torche là où la lumière aux pieds du joueur est inférieure ou égale à ce"
                                    + " niveau (0-15)"),
                    translateLabel(PREFERENCES.torchSpacing(), "Espacement des torches"),
                    translateTooltip(
                            PREFERENCES.torchSpacing(),
                            "Distance minimale en blocs entre les torches posées par un joueur"),
                    translateLabel(PREFERENCES.requireMiningTool(), "Nécessite un outil de minage"),
                    translateTooltip(
                            PREFERENCES.requireMiningTool(),
                            "Ne place des torches qu'en tenant une pioche, une pelle, une hache ou une houe"),
                    translateLabel(PREFERENCES.pauseWhileSneaking(), "Pause en s'accroupissant"),
                    translateTooltip(
                            PREFERENCES.pauseWhileSneaking(),
                            "Arrête de placer des torches quand le joueur est accroupi"),
                    translateLabel(PREFERENCES.groundBlacklist(), "Liste noire de sol"),
                    translateTooltip(
                            PREFERENCES.groundBlacklist(),
                            "Blocs sur lesquels aucune torche n'est jamais posée (ex : 'minecraft:glass')"),
                    translateListButton(PREFERENCES.groundBlacklist(), "Modifier la liste"),
                    translateLabel(PREFERENCES.autoCraft(), "Fabrication automatique de torches"),
                    translateTooltip(
                            PREFERENCES.autoCraft(),
                            "Fabrique quatre torches avec un charbon ou un charbon de bois et un bâton quand un"
                                    + " joueur n'en a plus"),
                    translateLabel(PREFERENCES.coalReserve(), "Réserve de charbon"),
                    translateTooltip(
                            PREFERENCES.coalReserve(),
                            "Charbon et charbon de bois que la fabrication automatique laisse toujours dans"
                                    + " l'inventaire"),
                    translateLabel(PREFERENCES.stickReserve(), "Réserve de bâtons"),
                    translateTooltip(
                            PREFERENCES.stickReserve(),
                            "Bâtons que la fabrication automatique laisse toujours dans l'inventaire"),
                    translateLabel(ALLOW_AUTO_CRAFT, "Autoriser la fabrication automatique"),
                    translateTooltip(
                            ALLOW_AUTO_CRAFT,
                            "Indique si les joueurs peuvent fabriquer des torches automatiquement. Si désactivé,"
                                    + " aucun joueur n'en fabrique, quel que soit son propre réglage"),
                    translateLabel(MINIMUM_TORCH_SPACING, "Espacement minimal des torches"),
                    translateTooltip(
                            MINIMUM_TORCH_SPACING,
                            "Plus petit espacement qu'un joueur peut utiliser. Un joueur qui choisit moins obtient"
                                    + " cet espacement"),
                    translateLabel(ENFORCED_GROUND_BLACKLIST, "Liste noire de sol imposée"),
                    translateTooltip(
                            ENFORCED_GROUND_BLACKLIST,
                            "Blocs sur lesquels aucune torche de joueur n'est posée, en plus de la liste noire"
                                    + " propre à chaque joueur"),
                    translateListButton(ENFORCED_GROUND_BLACKLIST, "Modifier la liste"),
                    translateScreenTitle("Configuration de Pathlighter"),
                    translateSection(ModConfig.Type.CLIENT, "Paramètres du joueur"),
                    translateSectionTitle(ModConfig.Type.CLIENT, "Paramètres du joueur Pathlighter"),
                    translateSection(ModConfig.Type.SERVER, "Paramètres du serveur"),
                    translateSectionTitle(ModConfig.Type.SERVER, "Paramètres du serveur Pathlighter"),
                    Map.entry(ToggleText.SWITCHED_ON_KEY, "Pathlighter : Activé"),
                    Map.entry(ToggleText.SWITCHED_OFF_KEY, "Pathlighter : Désactivé"),
                    Map.entry(ToggleText.UNAVAILABLE_KEY, "Pathlighter n'est pas installé sur ce serveur"),
                    Map.entry(ToggleText.KEY_CATEGORY, "Pathlighter"),
                    Map.entry(ToggleText.TOGGLE_KEY, "Basculer Pathlighter"))),
    JA_JP(
            "ja_jp",
            Map.ofEntries(
                    translateLabel(ENABLED_BY_DEFAULT, "デフォルトで有効"),
                    translateTooltip(ENABLED_BY_DEFAULT, "自分で切り替えていないプレイヤーに対して Pathlighter を有効にするかどうか"),
                    translateLabel(PREFERENCES.lightThreshold(), "明るさのしきい値"),
                    translateTooltip(PREFERENCES.lightThreshold(), "プレイヤーの足元の明るさがこの値以下のときに松明を設置します (0-15)"),
                    translateLabel(PREFERENCES.torchSpacing(), "松明の間隔"),
                    translateTooltip(PREFERENCES.torchSpacing(), "プレイヤーが設置する松明どうしの最小距離 (ブロック)"),
                    translateLabel(PREFERENCES.requireMiningTool(), "採掘ツールが必要"),
                    translateTooltip(PREFERENCES.requireMiningTool(), "ツルハシ、シャベル、斧、クワを持っているときだけ松明を設置します"),
                    translateLabel(PREFERENCES.pauseWhileSneaking(), "スニーク中は一時停止"),
                    translateTooltip(PREFERENCES.pauseWhileSneaking(), "スニーク中は松明を設置しません"),
                    translateLabel(PREFERENCES.groundBlacklist(), "設置禁止ブロック"),
                    translateTooltip(PREFERENCES.groundBlacklist(), "松明を絶対に設置しないブロック (例: 'minecraft:glass')"),
                    translateListButton(PREFERENCES.groundBlacklist(), "リストを編集"),
                    translateLabel(PREFERENCES.autoCraft(), "松明の自動クラフト"),
                    translateTooltip(PREFERENCES.autoCraft(), "松明がなくなったとき、石炭または木炭1個と棒1本から松明を4本クラフトします"),
                    translateLabel(PREFERENCES.coalReserve(), "石炭の予備数"),
                    translateTooltip(PREFERENCES.coalReserve(), "自動クラフトで常にインベントリに残す石炭と木炭の数"),
                    translateLabel(PREFERENCES.stickReserve(), "棒の予備数"),
                    translateTooltip(PREFERENCES.stickReserve(), "自動クラフトで常にインベントリに残す棒の数"),
                    translateLabel(ALLOW_AUTO_CRAFT, "自動クラフトを許可"),
                    translateTooltip(ALLOW_AUTO_CRAFT, "プレイヤーが松明を自動クラフトできるかどうか。オフの場合、各プレイヤーの設定にかかわらず誰も自動クラフトしません"),
                    translateLabel(MINIMUM_TORCH_SPACING, "松明の最小間隔"),
                    translateTooltip(MINIMUM_TORCH_SPACING, "プレイヤーが使える最小の松明間隔。これより小さい値を選んだプレイヤーにはこの間隔が使われます"),
                    translateLabel(ENFORCED_GROUND_BLACKLIST, "強制設置禁止ブロック"),
                    translateTooltip(ENFORCED_GROUND_BLACKLIST, "各プレイヤー自身の設置禁止ブロックに加えて、どのプレイヤーの松明も設置しないブロック"),
                    translateListButton(ENFORCED_GROUND_BLACKLIST, "リストを編集"),
                    translateScreenTitle("Pathlighter 設定"),
                    translateSection(ModConfig.Type.CLIENT, "プレイヤー設定"),
                    translateSectionTitle(ModConfig.Type.CLIENT, "Pathlighter プレイヤー設定"),
                    translateSection(ModConfig.Type.SERVER, "サーバー設定"),
                    translateSectionTitle(ModConfig.Type.SERVER, "Pathlighter サーバー設定"),
                    Map.entry(ToggleText.SWITCHED_ON_KEY, "Pathlighter: 有効"),
                    Map.entry(ToggleText.SWITCHED_OFF_KEY, "Pathlighter: 無効"),
                    Map.entry(ToggleText.UNAVAILABLE_KEY, "このサーバーには Pathlighter がインストールされていません"),
                    Map.entry(ToggleText.KEY_CATEGORY, "Pathlighter"),
                    Map.entry(ToggleText.TOGGLE_KEY, "Pathlighter を切り替え"))),
    ZH_CN(
            "zh_cn",
            Map.ofEntries(
                    translateLabel(ENABLED_BY_DEFAULT, "默认启用"),
                    translateTooltip(ENABLED_BY_DEFAULT, "尚未自行切换的玩家是否默认启用 Pathlighter"),
                    translateLabel(PREFERENCES.lightThreshold(), "亮度阈值"),
                    translateTooltip(PREFERENCES.lightThreshold(), "当玩家脚下的亮度不高于此等级时放置火把 (0-15)"),
                    translateLabel(PREFERENCES.torchSpacing(), "火把间距"),
                    translateTooltip(PREFERENCES.torchSpacing(), "同一玩家放置的火把之间的最小距离 (方块)"),
                    translateLabel(PREFERENCES.requireMiningTool(), "需要挖掘工具"),
                    translateTooltip(PREFERENCES.requireMiningTool(), "仅在手持镐、锹、斧或锄时放置火把"),
                    translateLabel(PREFERENCES.pauseWhileSneaking(), "潜行时暂停"),
                    translateTooltip(PREFERENCES.pauseWhileSneaking(), "潜行时不放置火把"),
                    translateLabel(PREFERENCES.groundBlacklist(), "地面黑名单"),
                    translateTooltip(PREFERENCES.groundBlacklist(), "永远不会在其上放置火把的方块 (例如 'minecraft:glass')"),
                    translateListButton(PREFERENCES.groundBlacklist(), "编辑列表"),
                    translateLabel(PREFERENCES.autoCraft(), "自动合成火把"),
                    translateTooltip(PREFERENCES.autoCraft(), "玩家火把用完时，用1个煤炭或木炭和1根木棍合成4个火把"),
                    translateLabel(PREFERENCES.coalReserve(), "煤炭保留数量"),
                    translateTooltip(PREFERENCES.coalReserve(), "自动合成时始终保留在物品栏中的煤炭和木炭数量"),
                    translateLabel(PREFERENCES.stickReserve(), "木棍保留数量"),
                    translateTooltip(PREFERENCES.stickReserve(), "自动合成时始终保留在物品栏中的木棍数量"),
                    translateLabel(ALLOW_AUTO_CRAFT, "允许自动合成"),
                    translateTooltip(ALLOW_AUTO_CRAFT, "玩家是否可以自动合成火把。关闭后，无论玩家自己如何设置，都不会自动合成"),
                    translateLabel(MINIMUM_TORCH_SPACING, "最小火把间距"),
                    translateTooltip(MINIMUM_TORCH_SPACING, "玩家可使用的最小火把间距。选择更小值的玩家将使用此间距"),
                    translateLabel(ENFORCED_GROUND_BLACKLIST, "强制地面黑名单"),
                    translateTooltip(ENFORCED_GROUND_BLACKLIST, "除每位玩家自己的黑名单外，任何玩家的火把都不会放置在其上的方块"),
                    translateListButton(ENFORCED_GROUND_BLACKLIST, "编辑列表"),
                    translateScreenTitle("Pathlighter 配置"),
                    translateSection(ModConfig.Type.CLIENT, "玩家设置"),
                    translateSectionTitle(ModConfig.Type.CLIENT, "Pathlighter 玩家设置"),
                    translateSection(ModConfig.Type.SERVER, "服务器设置"),
                    translateSectionTitle(ModConfig.Type.SERVER, "Pathlighter 服务器设置"),
                    Map.entry(ToggleText.SWITCHED_ON_KEY, "Pathlighter：已启用"),
                    Map.entry(ToggleText.SWITCHED_OFF_KEY, "Pathlighter：已禁用"),
                    Map.entry(ToggleText.UNAVAILABLE_KEY, "此服务器未安装 Pathlighter"),
                    Map.entry(ToggleText.KEY_CATEGORY, "Pathlighter"),
                    Map.entry(ToggleText.TOGGLE_KEY, "切换 Pathlighter")));

    private final String locale;
    private final Map<String, String> entries;

    Translation(String locale, Map<String, String> entries) {
        this.locale = locale;
        this.entries = Map.copyOf(entries);
    }

    String getLocale() {
        return locale;
    }

    Map<String, String> getEntries() {
        return entries;
    }

    private static Map.Entry<String, String> translateLabel(ModConfigSpec.ConfigValue<?> value, String text) {
        return Map.entry(buildTranslationKey(value), text);
    }

    private static Map.Entry<String, String> translateTooltip(ModConfigSpec.ConfigValue<?> value, String text) {
        return Map.entry(buildTranslationKey(value) + ".tooltip", text);
    }

    private static Map.Entry<String, String> describeTooltip(ModConfigSpec.ConfigValue<?> value) {
        return translateTooltip(value, describe(value));
    }

    private static Map.Entry<String, String> translateListButton(ModConfigSpec.ConfigValue<?> value, String text) {
        return Map.entry(buildTranslationKey(value) + ".button", text);
    }

    private static String buildTranslationKey(ModConfigSpec.ConfigValue<?> value) {
        return Pathlighter.MOD_ID + ".configuration." + String.join(".", value.getPath());
    }

    private static String describe(ModConfigSpec.ConfigValue<?> value) {
        return Objects.requireNonNull(value.getSpec().getComment())
                .lines()
                .findFirst()
                .orElseThrow();
    }

    private static Map.Entry<String, String> translateScreenTitle(String text) {
        return Map.entry(Pathlighter.MOD_ID + ".configuration.title", text);
    }

    private static Map.Entry<String, String> translateSection(ModConfig.Type type, String text) {
        return Map.entry(buildSectionKey(type), text);
    }

    private static Map.Entry<String, String> translateSectionTitle(ModConfig.Type type, String text) {
        return Map.entry(buildSectionKey(type) + ".title", text);
    }

    private static String buildSectionKey(ModConfig.Type type) {
        return Pathlighter.MOD_ID + ".configuration.section." + Pathlighter.MOD_ID + "." + type.extension() + ".toml";
    }
}
