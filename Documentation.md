# Monster Trading Cards Game (MTCG) - Dokumentation

## Inhaltsverzeichnis
1. [Einleitung](#einleitung)
2. [Projektübersicht](#projekt%C3%BCbersicht)
3. [Systemarchitektur](#systemarchitektur)
4. [API-Spezifikation](#api-spezifikation)
5. [Implementierungsdetails](#implementierungsdetails)
6. [Bekannte Probleme und Lösungen](#bekannte-probleme-und-l%C3%B6sungen)
7. [Weiterentwicklungsmöglichkeiten](#weiterentwicklungsm%C3%B6glichkeiten)
8. [Fazit](#fazit)

---

## Einleitung
Das **Monster Trading Cards Game (MTCG)** ist eine serverbasierte Anwendung für ein Sammelkartenspiel, in dem Spieler Karten sammeln, verwalten, tauschen und Kämpfe gegen andere Spieler austragen können. Das Projekt beinhaltet die Entwicklung eines REST-basierten Servers unter Verwendung von Java, einer PostgreSQL-Datenbank zur Speicherung der Spielinformationen und einer API zur Interaktion mit dem System.

## Projektübersicht
Das MTCG besteht aus folgenden Hauptkomponenten:
- **Benutzerverwaltung** (Registrierung, Login, Profildaten)
- **Kartensystem** (Erwerb von Kartenpaketen, Deck-Management)
- **Kampfsystem** (Spielmechanik, Kampflogik, ELO-Bewertung)
- **Handelssystem** (Tausch von Karten)
- **Scoreboard** (Rangliste basierend auf ELO-Punkten)

## Systemarchitektur
Die Anwendung basiert auf einer **Client-Server-Architektur** mit folgenden Hauptkomponenten:

- **REST-Server:** Behandelt HTTP-Anfragen und verarbeitet die Spielmechanik.
- **Datenbank (PostgreSQL):** Speichert Nutzerinformationen, Karten, Kämpfe und Statistiken.
- **Authentifizierung:** Bearer-Token für gesicherte API-Anfragen.
- **Business-Logik:** Implementierung von Kampflogik, Handelssystem und Deckverwaltung.

## API-Spezifikation
Die API des MTCG-Servers basiert auf REST und bietet folgende Endpunkte:

### Benutzerverwaltung
- **POST /users** - Registrierung eines neuen Nutzers
- **POST /sessions** - Login eines Nutzers und Erhalt eines Tokens
- **GET /users/{username}** - Abrufen von Profildaten
- **PUT /users/{username}** - Aktualisieren von Profildaten

### Kartensystem
- **POST /packages** - Erstellen von Kartenpaketen (nur für Admins)
- **POST /transactions/packages** - Erwerb eines Kartenpakets
- **GET /cards** - Abrufen aller gesammelten Karten
- **GET /deck** - Abrufen des aktuellen Decks
- **PUT /deck** - Konfigurieren eines Decks mit vier Karten

### Kampfsystem
- **GET /stats** - Abrufen der Nutzerstatistiken
- **GET /scoreboard** - Abrufen der globalen Rangliste
- **POST /battles** - Teilnahme an einem Kampf

### Handelssystem
- **GET /tradings** - Abrufen verfügbarer Handelsangebote
- **POST /tradings** - Erstellen eines Handels
- **DELETE /tradings/{tradingdealid}** - Löschen eines Handels
- **POST /tradings/{tradingdealid}** - Ausführen eines Handels

## Implementierungsdetails
Die Implementierung folgt diesen technischen Anforderungen:
- **Server:** Eigenständige Implementierung der HTTP-Protokoll-Logik ohne Frameworks
- **Persistenz:** Speicherung von Daten in einer PostgreSQL-Datenbank
- **Sicherheit:** Authentifizierung mittels Token-basierter Zugriffskontrolle
- **Spielmechanik:** Kampf- und Handelslogik mit spezialisierten Regeln für verschiedene Kartentypen

### Kampfmechanik
- Kämpfe bestehen aus mehreren Runden (max. 100, um Endlosschleifen zu vermeiden)
- Elementareffekte beeinflussen den Schaden (Wasser schlägt Feuer, Feuer schlägt Normal, Normal schlägt Wasser)
- Bestimmte Karteninteraktionen haben Sonderregeln (z. B. Goblins greifen keine Drachen an)
- Gewinner erhält Karten des Gegners und verbessert sein ELO

## Bekannte Probleme und Lösungen
- **Dateninkonsistenzen:** Lösung durch Transaktionssicherung in der Datenbank
- **Kartenbalance:** Feinabstimmung durch Anpassung der Schadenswerte
- **Performanzprobleme:** Optimierung der SQL-Abfragen zur Reduzierung der Latenz

## Weiterentwicklungsmöglichkeiten
- Erweiterung der API um **Booster-Effekte** für Karten
- Einführung eines **Rangsystems** für verschiedene Spielstärken
- Unterstützung für **asynchrone Spielmodi**
- Implementierung eines **Web-Frontends** für bessere Benutzerfreundlichkeit

## Fazit
Das MTCG bietet eine robuste serverseitige Implementierung eines Sammelkartenspiels mit Kampf-, Handels- und Punktesystem. Die Architektur ermöglicht eine einfache Erweiterbarkeit und Skalierung. Durch zukünftige Verbesserungen kann das Spiel weiter optimiert werden.

