# TPA - Universal Teleport Request Plugin

A lightweight and powerful teleport request plugin for Minecraft servers.

## 🌟 Features

- **Multi-Version Support**: Compatible with Minecraft versions 1.8 - 1.20
- **Fully Configurable**: 
  - Customizable messages and timers
  - Custom sound effects configuration
- **Flexible Cooldown System**:
  - Default cooldown: 30 seconds
  - Request timeout: 60 seconds
- **Toggle Functionality**: 
  - `/tpatoggle` to enable/disable teleport requests
- **Enhanced User Experience**:
  - Interactive sound effects
  - Comprehensive permission system
- **Performance**:
  - Memory optimized
  - No external dependencies
  - Lightweight (< 50KB)

## 🎮 Commands

| Command | Description |
|---------|-------------|
| `/tpa <player>` | Request to teleport to a player |
| `/tpahere <player>` | Request a player to teleport to you |
| `/tpaccept` | Accept incoming teleport request |
| `/tpadeny` | Deny teleport request |
| `/tpatoggle` | Toggle teleport requests on/off |

## 🔐 Permissions

| Permission | Default | Description |
|-----------|---------|-------------|
| `tpasystem.use` | true | Access to all TPA commands |
| `tpasystem.bypass` | op | Bypass cooldown timer |

## 🔊 Sound Effects

Immersive audio cues for various actions:
- Experience orb sound when sending requests 
- Enderman teleport sound on successful teleport 
- Anvil break sound when denying requests 
- Fully configurable or can be disabled

## ⚙️ Configuration

### config.yml
```yaml
# Teleport request settings
request-timeout: 60     # Time in seconds before request expires
request-cooldown: 30    # Cooldown between teleport requests
enable-sounds: true     # Enable or disable sound effects

# Message configuration
messages:
  prefix: "&8[&bTPA&8]"  # Chat prefix with color codes
  
  # Placeholders: %player%, %target%, %time%
  # Example placeholders will be replaced dynamically
  send-request: "%player% has requested to teleport to %target%"
  request-sent: "Teleport request sent to %target%"
  request-received: "%player% wants to teleport to you"
  request-accepted: "Teleport request accepted"
  request-denied: "Teleport request denied"
  request-timeout: "Teleport request to %target% has expired"
  
# Sound configuration
sounds:
  request-send: 
    sound: ENTITY_EXPERIENCE_ORB_PICKUP  # Sound when sending a request
    volume: 1.0
    pitch: 1.0
  teleport-success: 
    sound: ENTITY_ENDERMAN_TELEPORT  # Sound on successful teleport
    volume: 1.0
    pitch: 1.0
  request-deny: 
    sound: BLOCK_ANVIL_BREAK  # Sound when denying a request
    volume: 1.0
    pitch: 1.0
```

## 🚀 Why Choose This Plugin?

- **Universal Compatibility**: Works across multiple Minecraft versions
- **Performance**: Optimized, minimal resource usage
- **Customization**: Flexible configuration options
- **User-Friendly**: Intuitive commands and clear messaging
- **Admin Features**: Cooldown bypass for staff

## 📦 Installation

1. Download the plugin
2. Place in your server's `plugins` folder
3. Restart the server
4. (Optional) Customize `config.yml` to your preferences
5. You're ready to go!

## 🔮 Upcoming Features

- MySQL support for cross-server toggle states
- Warmup timer before teleport
- Distance limit configurations
- Language file support
- Advanced permission system

## 🆘 Support

Encountered an issue or have a suggestion?
- Create an issue on GitHub
- [Join our Discord server](https://discord.gg/a2TZt88HHN)

**Enjoying the plugin? Please leave a review!**

## 🤝 Contributing

Contributions are welcome! Please read our contributing guidelines before getting started.
