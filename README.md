# Выпуск форка 3.7.2

Настройки → VPN → Исключения: любые приложения `.exe` на Windows, локальная сеть
и выбранные домены. После изменения переподключитесь; профиль сервера прежний.

Основа: Desktop/shared 3.7.0; ядро 0.17.1-danik.2. Сохранены защита TUN
Windows/Linux, проверка готовности полного туннеля, повторное открытие окна
и обновления из нашего форка. Перед сборкой выполнить `scripts/sync-core.sh`;
patch общего модуля применяется через `scripts/sync-shared.sh`.
Linux package собирается из исходников. Windows portable использует проверенный
runtime upstream 3.7.0 с пересобранными desktop/shared jars и нашим ядром;
installer сохраняет пользовательские профили. Windows acceptance не выполнялся.

Windows 2.6.4: для исправления ошибки WFP «The specified group does not exist»
выберите исправленное отдельное ядро из [hotfix 0.7.3-danik.2](https://github.com/danusha2345/OpenFlux/releases/tag/v0.7.3-danik.2)
в настройках собственного пути ядра. Профили и ключ менять не нужно.

## Наши исправления полного туннеля

Linux: режим TUN с запросом прав только для ядра через pkexec; нужны ip,
nft и systemd-resolved/resolvectl. Windows: WFP блокирует обход TUN.
IPv6 в полном туннеле блокируется. SOCKS5/системный прокси охватывает только
использующие его программы. Реальная проверка на Windows и Linux ещё нужна.

Исправления общего модуля хранятся в patches/shared-full-tunnel.patch.
Gradle автоматически проверяет закреплённую ревизию shared и применяет патч;
чужие конфликтующие изменения не перезаписываются. Ядро закреплено в
core-revision.txt; перед сборкой выполните scripts/sync-core.sh.

# Наш выпуск 3.7.1

Основан на upstream 3.7.0. Ядро закреплено в `core-revision.txt`:
Ядро 0.17.1-danik.2: Session/AES-GCM, Avito/Cups/Direct, DNS через туннель, защита приватных назначений.
Go 1.26.10, исправления MAX и восстановления Avito; обновлённый общий UI 3.7.0.
Перед сборкой: `scripts/sync-core.sh`, затем команды ниже.
Старые `openflux://noise-v1/` не подходят; нужен новый профиль `openflux://v1/`.
Личные ссылки и секреты в репозиторий и релизы не включаются.

Релизы публикуются после локальных проверок; автоматическая публикация по tag
выключена, workflow Release можно запустить вручную.

# OpenFluxDesktop

Desktop client for [OpenFlux](https://github.com/p1neappleXpress/OpenFlux)
(Windows/macOS/Linux): full-tunnel or SOCKS5/HTTP proxy, multi-transport
sessions with automatic failover, AES-256-GCM encryption, a node-deployment
wizard (deploy an exit over SSH from the app), and a built-in browser for
passing a transport's check (SmartCaptcha, a login wall).

This repository's app code and UI (the `desktopApp/` and `shared/` modules)
come from [meepo161/OpenFluxClient](https://github.com/meepo161/OpenFluxClient),
used here with the author's agreement. **Huge thanks to
[@meepo161](https://github.com/meepo161)** — see [Credits](#credits) below.
The previous, independent desktop client that used to live in this
repository is preserved at the [`legacy-native-app`](../../tree/legacy-native-app)
tag.

## Getting the code

```bash
git clone --recurse-submodules https://github.com/p1neappleXpress/OpenFluxDesktop.git
```

Already cloned without `--recurse-submodules`?

```bash
git submodule update --init --recursive
```

This checks out two submodules:

- `shared/` → [OpenFluxClientShared](https://github.com/p1neappleXpress/OpenFluxClientShared),
  the Compose Multiplatform UI and models shared with
  [OpenFluxAndroid](https://github.com/p1neappleXpress/OpenFluxAndroid).
- `OpenFlux/` → [OpenFlux](https://github.com/p1neappleXpress/OpenFlux), the
  core this app bundles as a separate process.

## Building

Needs JDK 17 and Go (or Docker, for a build without Go). Core binaries are
not committed to git: `run`/`package*` build the one for this machine into
`desktopApp/resources/<windows|macos|linux>` automatically (from the
`OpenFlux/` submodule, `-PcoreDir=<path>`, or `../OpenFlux` next to this
repository) if it isn't already there. `-PskipCore=true` runs the app
without a core (Settings → Core then takes a file of your own).

```bash
./gradlew :desktopApp:run                              # run (builds the core on first run)
scripts/build-core.sh                                   # core by hand, from the OpenFlux/ submodule
./gradlew :shared:jvmTest                                # tests
./gradlew -PwindowsPackage=true :desktopApp:packageMsi   # Windows: .msi (also packageExe)
./gradlew :desktopApp:packageDmg                         # macOS: .dmg
./gradlew :desktopApp:packageDeb                         # Linux: .deb (needs fakeroot)
```

## Structure

```
desktopApp/   Window, tray, shortcuts, packaging, icons
shared/       Submodule: models, service interfaces, design system, screens
  jvmMain/
    core/     Core process launch, IPC status, Yandex checks
    node/     The node wizard (core --node-wizard over stdin/stdout)
    web/      The built-in browser: off-screen KCEF, a proxy router, a log
    platform/ Windows system proxy (WinInet), admin rights
    data/     Settings and profile storage
OpenFlux/     Submodule: the core (CLI), spawned as a subprocess
scripts/      build-core.sh
```

## Credits

- **[meepo161](https://github.com/meepo161)** — author of
  [OpenFluxClient](https://github.com/meepo161/OpenFluxClient), the source of
  this app's UI and logic (`desktopApp/`, `shared/`): multi-transport
  sessions with failover, AES-256-GCM encryption, the built-in browser and
  check/captcha flow, the node-deployment wizard, and the Compose design
  system. Thank you!
- **[p1neappleXpress](https://github.com/p1neappleXpress)** — author of the
  [OpenFlux](https://github.com/p1neappleXpress/OpenFlux) core this app
  bundles: the tunnel, transports, and negotiation protocol.
- **[damnurmum](https://github.com/damnurmum)** — author of
  [OpenFlux-Android](https://github.com/damnurmum/OpenFlux-Android) and,
  in the core, `openflux://` links and QR codes, and the cups.online
  transport, which this app's share and scan screens build on. Thank you!

## License

GNU General Public License v3.0 or later — see [LICENSE](LICENSE).
