<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# darksouls-progress-bar Changelog

## [Unreleased]
### Added
- New progress bar icons: Blood Moon, Malenia, Manjar, Praise the Sun, Radahn, Solaire
- Sekiro death popup with sound, picked randomly together with "You Died"
- Newly added icons are selected automatically after a plugin update

### Fixed
- Message bus listeners no longer leak from progress bar UI
- Empty icon selection and missing assets no longer throw
- Death popup closes itself when the sound cannot be played

### Changed
- Death sounds converted to mono 22 kHz wav to shrink the plugin
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
