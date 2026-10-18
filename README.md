# Pathlighter

Pathlighter places torches at your feet as you walk through dark areas, so caves and night paths stay lit without stopping to place them by hand.

# Features

*   **Placement**: Places a torch at your feet when the light there is at or below the configured threshold. Light counts block light and skylight for the current time of day, so the open surface gets torches at night but not during the day. A torch replaces short grass, ferns, and a single snow layer, the same way a hand-placed torch does.
*   **Spacing**: Keeps the configured minimum distance from the last torch Pathlighter placed for you. Spacing counts only that torch, and only while you are in the dimension it was placed in. The position isn't saved, so it's forgotten when you die or log out.
*   **Auto-crafting**: When you run out of torches, Pathlighter crafts four torches from one coal or charcoal and one stick, from your inventory or offhand. It keeps the configured coal and stick reserves, and it only crafts when the four torches fit in your inventory.
*   **Sneak to pause**: Sneaking stops placement.
*   **Mining tool requirement**: Optionally places torches only while you hold a pickaxe, shovel, axe, or hoe.
*   **Ground blacklist**: Torches are never placed on blacklisted ground blocks, such as glass or ice.
*   **Build permissions**: Nothing is placed in adventure or spectator mode, inside spawn protection, or where a land-claim mod refuses the placement. A refused placement leaves the world and your inventory unchanged.
*   **Creative mode**: Creative players get torches without carrying any, and nothing is taken from their inventory.
*   **Languages**: English, German, Spanish, French, Japanese, and Chinese.

# Usage

*   **Command**: `/pathlighter toggle` turns Pathlighter on or off for the player who runs it. It needs no operator permission.
*   **Keybinding**: Press **V** (changeable in Controls) to toggle Pathlighter for yourself.
*   **Per-player choice**: A player's own on/off choice is saved with that player and kept through death. Players who never toggled follow the `enabledByDefault` setting, including after it changes.

# Configuration

Each player chooses their own settings, and the server sets defaults and limits.

## Player settings

A player's settings live in `config/pathlighter-client.toml` and can be edited from the in-game config screen at any time, including while connected to a server. Pathlighter sends them to the server when the player joins and again whenever they change, and the server keeps them with that player through death.

| Key | Default | Meaning |
|---|---|---|
| `lightThreshold` | `7` | Places a torch where the light is at or below this level (0-15) |
| `torchSpacing` | `4` | Minimum distance in blocks between a player's torches (1-16) |
| `requireMiningTool` | `true` | Only place torches while holding a pickaxe, shovel, axe, or hoe |
| `pauseWhileSneaking` | `true` | Stop placing torches while sneaking |
| `groundBlacklist` | glass, ice | Blocks torches are never placed on |
| `autoCraft` | `true` | Craft torches from coal or charcoal and sticks when a player runs out |
| `coalReserve` | `0` | Coal and charcoal that auto-crafting always leaves (0-64) |
| `stickReserve` | `0` | Sticks that auto-crafting always leaves (0-64) |

## Server settings

The server settings live in `config/pathlighter-server.toml`, which all worlds share. To give one world its own settings, copy that file into the world's `serverconfig/` folder. The in-game config screen can edit them only while a single-player world is open.

The server file holds the same eight player keys. They apply to players who don't have Pathlighter installed. The server file also holds the following keys, which apply to every player.

| Key | Default | Meaning |
|---|---|---|
| `enabledByDefault` | `true` | Whether Pathlighter is on for players who haven't toggled it themselves |
| `allowAutoCraft` | `true` | When `false`, no player auto-crafts, whatever their own `autoCraft` setting |
| `minimumTorchSpacing` | `1` | Smallest `torchSpacing` a player can use. A player who chooses less gets this spacing (1-16) |
| `enforcedGroundBlacklist` | empty | Blocks torches are never placed on for any player, in addition to each player's own `groundBlacklist` |

# Multiplayer

All placement and crafting runs on the server. Players without Pathlighter installed can join a server that has it and use `/pathlighter toggle`, and they get the server's player settings. A client with Pathlighter installed can join a server without it, and the toggle key then reports that Pathlighter isn't installed on that server.

Pathlighter checks each player every 5 ticks and reads only the blocks at that player's feet, so its cost grows only with the number of players online.

# Requirements

*   **NeoForge**: 21.1.262 or later
*   **Minecraft**: 1.21.1
