# MythicWorldTweaks

A modular Minecraft utility mod designed for **my internal server hosting**, with gameplay simplifications and technical tweaks that others may find useful or adaptable.

---

## 🧭 Purpose

This mod provides configurable mechanics and behavioral adjustments intended to support internal gameplay scenarios and testing. It simplifies several aspects of vanilla gameplay, introduces toggleable features, and allows for structured customization.

The mod is designed for servers, but it is not bad to use it for singleplayer.

---

## 🔧 Features

- Every subsystems below can be toggled by a main switch.

---

### 🔘 Binary Toggles — Simple On/Off tweaks

- Throwable fire charges (and a big variant)
- Creeper griefing disabled
- Explosion-proof item entities
- Nest shulker boxes and control nesting depth
- Player riding via `/ride` command and player interactions(gestures).
- Elytra-safe player riding for rider (In case of vehicle player disconnection in the sky)
- Dispenser-launched tridents; persistent trident behaviour.
- Warden weakening (disable Sonic Boom)
- Beds usable anytime (sleep button for all players)
- Carpet fake players excluded from sleep checks
- Experience retention on death with `keepInventory` disabled
- Dragon egg generation post EVERY ender dragon defeat
- Void protections (for tridents, players in creative)
- Suicide command
- Villager-to-zombie conversions forced regardless of difficulty
- Crossbows inherit bow enchantments

---

### ⚙️ Configurable Tweaks — Can be Customized by Parameters

- Fireball discard timing and lifespan
- Shulker box nesting depth and stack size limits
- Death drop protection and discard time customization
- Warden stats tuning: health, speed, Sonic Boom damage

---

### 🛠 Item Editor

Use structured JSON configs to:

- Adjust stack sizes, damage values, and rarity
- Set recipe remainders and fire resistance
- Customize food properties, effects, and leftovers

---

### Others

- Server-friendly mod ID validation for multiplayer
- Embedded data pack support (contained within the JAR)

---

### Config Processing

- Config file is in JSON.
- Config hot-update and synchronization between client and server(logical) are supported.

---

## 🛠 Compatibility

- Designed mainly for server-side use, with client side support.
- Developed and used internally; documentation provided as-is

---

For full configuration reference, see the included documentation or visit the mod repository.

---

> ℹ️ Some sections of this documentation were co-authored with assistance from **Microsoft Copilot**.