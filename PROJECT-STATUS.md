# Project status

## Done
- New icons (Blood Moon, Malenia, Manjar, Praise the Sun, Radahn, Solaire) and Sekiro death popup + sound (`items.yaml` -> `deaths`)
- Build: `intellijIdea(...)` instead of removed `IC` platform type
- UI fixes: listener leaks, empty icon set, missing assets, logger, persisted icon sync, double widget dispose
- Resources: wav compressed to mono 22 kHz, gif/sound names use `_`
- Changelog updated; UI test sample project trimmed of IDE-only files

## Decisions
- Deprecation report (StatusBarWidget.getPresentation, PlatformType, Instant) not reproducible: verifyPlugin clean on IDE 253/261/262/263
- mp3 not playable by JDK -> death sounds are wav
- Death popup fires only for "Run", not "Debug" (debug stops are often intentional)
- `applicationActivated` keeps reinstalling ProgressBarUI (needed at startup, cheap)
- plugin.xml description / change-notes / idea-version are injected by `patchPluginXml` (README, CHANGELOG, gradle.properties)

## Next
- Optimize gifs: sekiro_dead.gif 480x480 / 466 KB, you_died.gif 269 KB (pt 11)
- Drop duplicate/unused gifs: bonfire_darksouls_small vs bonfire_darksouls, fire_ds3 (pt 14)
- Optional: block deselecting all icons in settings
- Release deploy configuration
