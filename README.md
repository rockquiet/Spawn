<div align="center">

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/spawn?style=flat-square&logo=modrinth&labelColor=3A3025&color=407F35)](https://modrinth.com/plugin/spawn)
[![Spigot Downloads](https://img.shields.io/spiget/downloads/106188?style=flat-square&logo=image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQBAMAAADt3eJSAAAAD1BMVEVHcEwAAAD/0ADi6D86RuhWkE5HAAAAAXRSTlMAQObYZgAAAFBJREFUeJxljdERgDAIQ8OdAxicgHQCZQH3n0pqrT99P7y7QADAYkfHiIHFJ4yRwDWlDaHy7IPeMupOUkvVFiu5XL3hyLBXjIT/nfPLdq/yAL5yBqT7qDihAAAAAElFTkSuQmCC&labelColor=3A3025&color=407F35)](https://www.spigotmc.org/resources/106188)
[![Hangar Downloads](https://img.shields.io/hangar/dt/spawn?style=flat-square&logo=image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABkAAAAgBAMAAAAVss41AAAAJFBMVEVHcEz///////////////////////////////////////////8Uel1nAAAAC3RSTlMAH47fTa3EafI0C3ZKri0AAADwSURBVHicTZGvC8JQEMdPDeKSYBEsEwSFFbGJZcVkMRiEV7S6IgiCWNS6ZlBhxWZ3/ti8f87bfbfplePDvXff7/s+Iqlyr0p5FQy/3JwuzBw2s9GAk5qAaozqK3mAaKkL09FcR3XAE9cc0ELB8hVC6KfX4qtSCdQ+6LCB9TWYGyndt7LVzVbufQ7Pk8xJW9S9qEqwPORQ7Nop1Tk2zGNS8VfALWld0N3hk7QdTr7NM1DyQA9HT5qEHuZTmTLPaKvkzchaCQUgW0wOx3jD22goVypCwc2DFvVRlnWhIzk08uSttfj8/QMd/c3fJ9HNRv8CUjKn1XnSu4wAAAAASUVORK5CYII=&label=Downloads&labelColor=3A3025&color=407F35)](https://hangar.papermc.io/rockquiet/Spawn)
[![GitHub Downloads](https://img.shields.io/github/downloads/rockquiet/joinprotection/total?style=flat-square&logo=github&labelColor=3A3025&color=407F35)](https://github.com/rockquiet/Spawn/releases)

[![GitHub release (with filter)](https://img.shields.io/github/v/release/rockquiet/spawn?style=for-the-badge&labelColor=3A3025&color=407F35)](https://github.com/rockquiet/Spawn/releases)
![Minecraft Versions](https://img.shields.io/badge/minecraft-1.8_--_1.21.8-407F35?style=for-the-badge&logoColor=407F35&labelColor=3A3025&color=407F35)
![Java](https://img.shields.io/badge/java-8+-407F35?style=for-the-badge&logoColor=407F35&labelColor=3A3025&color=407F35)

# Spawn

Yet another Spawn plugin with many configurable options.

</div>

## Features

- You can set a Spawn and teleport to it (duh)
- Teleport to Spawn...
    - ...on join / only on first join
    - ...on death (bed / respawn anchor spawn-point can be ignored)
    - ...when falling into the void (void height is configurable)
    - ...on world change
- Command cooldown
- Teleport delay (with cancel on move / blindness effect)
- Teleport another player to Spawn via command
- Fall damage on teleport can be disabled
- Particles & sound on teleport
- Whitelist/Blacklist worlds to restrict the plugin's functionality
- Restrict the usage to specific game modes
- [MiniMessage](https://docs.advntr.dev/minimessage/format.html) (only 1.18.2+ Paper based servers)
  and [Legacy Formatting](https://minecraft.wiki/w/Formatting_codes) is supported
- Everything can be edited, bypassed with permissions, or completely disabled

## Documentation

All commands, permissions and configuration options are available on
the [GitHub Wiki](https://github.com/rockquiet/Spawn/wiki).

## Download

Plugin jars are available in the [Releases](https://github.com/rockquiet/Spawn/releases) section,
on [Modrinth](https://modrinth.com/plugin/spawn), [Spigot](https://www.spigotmc.org/resources/106188)
and [Hangar](https://hangar.papermc.io/rockquiet/Spawn).

[<img alt="github" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg">](https://github.com/rockquiet/Spawn/releases)
[<img alt="modrinth" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg">](https://modrinth.com/plugin/spawn)
[<img alt="spigot" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/spigot_vector.svg">](https://www.spigotmc.org/resources/106188)
[<img alt="hangar" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/hangar_vector.svg">](https://hangar.papermc.io/rockquiet/Spawn)

## Metrics

This plugin uses bStats to collect some (non-identifying) data about the servers it runs on.
You can opt out by editing the `config.yml` in the `/plugins/bStats` folder located in your server directory.
(More information [here](https://bstats.org/getting-started))

[<img alt="bstats" src="https://bstats.org/signatures/bukkit/SpawnWasTaken.svg">](https://bstats.org/plugin/bukkit/SpawnWasTaken)
