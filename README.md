# Dragon Block Plugin

A **Paper / Folia** plugin (1.21.x) that prevents the Ender Dragon from destroying blocks — unless they're explicitly whitelisted by the server administrator.

---

## Purpose

When players fight the Ender Dragon, the dragon destroys large swaths of terrain as it flies through blocks. On survival / anarchy / kit servers this can:

- Gut end-island farms (chorus, endermen)
- Destroy bridges, pathways, and builds near the portal
- Leave the End dimension permanently scarred

This plugin intercepts both pathways the dragon destroys blocks:

| Pathway | Event handled | Behaviour |
|---------|--------------|-----------|
| Dragon flying through blocks | `EntityChangeBlockEvent` | Cancelled unless block is whitelisted |
| Dragon / fireball explosions | `EntityExplodeEvent` | Non-whitelisted blocks removed from explosion |

---

## Config

File: `plugins/dragon-block-plugin/config.yml`

```yaml
# Block names are Material enum values (case-insensitive).
# Empty list = dragon cannot destroy ANY blocks.
whitelisted-blocks: []
```

### Example — allow only obsidian and end stone

```yaml
whitelisted-blocks:
  - OBSIDIAN
  - END_STONE
```

Full list of block Material names:  
https://jd.papermc.io/paper/1.21/org/bukkit/Material.html

---

## Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/dragonblock reload` | `dragonblock.admin` (OP by default) | Reloads config.yml without a server restart |
| `/dragonblock` | `dragonblock.admin` | Shows usage |

---

## Permissions

| Node | Default | Description |
|------|---------|-------------|
| `dragonblock.admin` | `op` | Access to `/dragonblock` commands |

---

## Installation

1. Drop the JAR into your server's `plugins/` folder
2. Restart the server (or use a plugin manager like PlugManX)
3. Edit `plugins/dragon-block-plugin/config.yml` to whitelist blocks
4. Run `/dragonblock reload` to apply changes

---

## Compatibility

| Platform | Status |
|----------|--------|
| **Paper** 1.21.x | ✅ Fully supported |
| **Folia** 1.21.x | ✅ Fully supported (`folia-supported: true`) |
| **Spigot** 1.21.x | ⚠️ Untested — the Brigadier command API requires Paper |

The plugin uses Paper's **Brigadier command API** and the **Adventure component API**. It does not use `BukkitScheduler` — events fire on the correct region thread in both Paper and Folia.

---

## Build

Requirements: JDK 21+

```bash
./gradlew build
# JAR → build/libs/dragon-block-plugin-1.0-SNAPSHOT.jar
```

Run a test server (requires Paper 1.21.x in Gradle cache):

```bash
./gradlew runServer
```

---

## License

MIT — do whatever you want.
