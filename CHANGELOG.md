# Changelog

## 2.0.0

### Upgrading from 1.0.x

Settings moved from `config/pathlighter-common.toml` to two files, and most keys were renamed. Each player's own settings live in `config/pathlighter-client.toml`. The server's defaults for players without Pathlighter, and its limits, live in `config/pathlighter-server.toml`. Pathlighter ignores the old file, so copy any values you changed into the new ones.

| 1.0.x key | 2.0.0 key |
|---|---|
| `enabled` | none (each player toggles for themselves, and `enabledByDefault` sets the starting state) |
| `enableAutoCraft` | `autoCraft` |
| `maxCoalUseLimit` | `coalReserve` |
| `maxStickUseLimit` | `stickReserve` |
| `maxTorchesLimit` | none (auto-crafting makes one batch of four torches at a time) |
| `minLightLevel` | `lightThreshold` (default is now 7, which also lights the open surface at night) |
| `blockBlacklist` | `groundBlacklist` |
| `requireMiningTool` | `requireMiningTool` |
| `disableWhileSneaking` | `pauseWhileSneaking` |
| `placementDistance` | `torchSpacing` |

`/pathlighter toggle` and the toggle key now switch Pathlighter for the player who uses them, not for the whole server. A player who had the mod switched off under 1.0.x starts 2.0.0 with it on and needs to toggle it again.

### Changes

*   Each player chooses their own placement and crafting settings. A server can turn off auto-crafting, set a minimum torch spacing, and enforce a ground blacklist for every player.
*   Torches replace short grass, ferns, and single snow layers, and are placed on soul sand and mud.
*   Auto-crafting uses coal, charcoal, and sticks from the offhand.
*   A placement refused by a land-claim mod no longer flickers in the world or uses up coal and sticks.
*   Nothing is placed above the build height, by dead players, or by players riding a mount.
*   Creative players get torches without carrying any.
*   Hoes count as mining tools again.
*   Placed torches trigger sculk sensors, the placed-block advancement criterion, and the torch "used" statistic.
*   Holding the toggle key toggles once.
