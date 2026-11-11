# P VPN publishing notes

- P VPN app label and launcher icon are customized.
- Ten dark Material 3 palettes are selectable in Settings and persisted through MMKV.
- All configuration and backup sharing entry points are removed. Import, local backup/export, restore, and WebDAV backup/restore remain available.
- The original v2rayNG typography/font behavior is preserved exactly; no replacement font or extra font dependency was introduced.
- Pinned native submodule revisions and a preparation script are included because source ZIP archives do not preserve Git submodule entries. Both GitHub Actions workflows run this script before native builds. A debug GitHub Actions workflow builds on GitHub-hosted runners and uploads APK artifacts.
- Local Gradle build was attempted but could not start because `services.gradle.org` was unreachable in this environment. The APK has therefore not been build-verified here.
- Keep upstream license and third-party notices when redistributing this fork.
