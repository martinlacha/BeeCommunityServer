# BeeCommunityServer

## Build
1. git clone
2. docker-compose up -d --build

## Structure
- .github - Konfigurace procesů GitHub Actions (CI, CD)
- .mvn - Konfigurační soubory Apache Maven
- db_backup - Složka se zálohami databáze
- src - Zdrojové soubory projektu
- .gitignore - Seznam ignorovaných souborů pro Git
- Dockerfile - Konfigurační soubor pro sestavení projektu
- README.md - Dokument s popisem projektu
- codecov.yaml - Konfigurační soubor nástroje CodeCov
- compose.yaml - Definice kontejnerů při nasazení
- mvnw - Skript pro spuštění nástroje Maven (Linux)
- mvnw.cmd - Skript pro spuštění nástroje Maven (Windows)
- pom.xml - Konfigurace Maven (metadata, závislosti, pluginy)
