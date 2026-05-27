# PCT

## Introduktion af projektet

PCT er en Java Spring Boot webapplikation til projektstyring. Systemet gør det muligt at arbejde med projekter, delprojekter, opgaver, projektmedlemmer og ressourcer i en samlet applikation.

Projektet er bygget med en klassisk lagdelt struktur, hvor controllers håndterer webforespørgsler, services indeholder forretningslogik, repositories står for databaseadgang med `JdbcTemplate`, og modelklasser repræsenterer systemets centrale data.

Den primære arbejdsgang i systemet er, at brugere kan oprette og tilgå projekter, opdele projekter i delprojekter, oprette opgaver, knytte ressourcer til arbejdet og få overblik over projektets estimerede timer og omkostninger.

## Link til Live Demo
https://pct-e8gcf6drfqemg7ep.norwayeast-01.azurewebsites.net/

Log ind med en af følgende demo brugere:
```
Email: anders@pct-demo.dk -- Har rollen Project Manager
Kodeord: DemoKode1234
```

## Indhold

- [Introduktion af projektet](#introduktion-af-projektet)
- [Features](#features)
- [Teknologier](#teknologier)
- [Værktøjer og services](#værktøjer-og-services)
- [Installation](#installation)
- [Brug](#brug)
- [Bidrag](#bidrag)
- [Developers](#developers)

## Features

- Registrering og login af brugere
- Rollebaseret adgangsstyring
- Projektbaseret adgangsstyring med projektmedlemmer
- Oprettelse, redigering og sletning af projekter
- Oprettelse, redigering og sletning af delprojekter
- Oprettelse og sletning af opgaver under delprojekter
- Ressourcestyring med kompetencer, arbejdstimer og timeløn
- Projektstatistik med antal delprojekter, antal opgaver, estimerede timer og forventede omkostninger
- Server-side rendered brugergrænseflade med Thymeleaf

## Teknologier

- Java 25
- Spring Boot 4.0.6
- Spring MVC 4.0.6
- Spring JDBC / `JdbcTemplate`
- Spring Validation
- Thymeleaf 4.0.6
- MySQL 9.7.0 _som runtime-database_
- H2 til tests
- Maven / Maven Wrapper
- Lombok 1.18.46

## Værktøjer og services

Projektet bruger følgende værktøjer og services i udviklings- og deploymentflowet:

- IntelliJ IDEA som anbefalet IDE
- Maven Wrapper til build, test og lokal kørsel
- Git og GitHub til versionsstyring
- GitHub Issues til user stories, tasks og bug reports
- GitHub Pull Requests til code review
- GitHub Actions til build og deployment
- Qodana til statisk kodeanalyse
- Azure Web App til deployment

## Installation

### Krav

Før projektet køres lokalt, skal følgende være installeret eller tilgængeligt:

- Java 25
- Git
- En MySQL-kompatibel database

Maven behøver ikke være installeret globalt, da projektet indeholder Maven Wrapper scripts.

### Klon repository

```bash
git clone https://github.com/Kodebutikken/PCT.git
cd PCT
```

### Database

Databaseskemaet findes her:

```text
src/main/resources/sql/schema.sql
```

Kør SQL-scriptet i din lokale MySQL-database, før applikationen startes.

Scriptet opretter blandt andet tabellerne:

- `user`
- `project`
- `project_user`
- `subproject`
- `resource_type`
- `resource`
- `task`
- `task_resource`

### Miljøvariabler

Projektets aktive profil styres i `application.properties`. Den lokale udviklingskonfiguration bruger miljøvariabler fra `application-dev.properties`.

For lokal udvikling skal følgende variabler sættes:

| Variabel | Beskrivelse |
| --- | --- |
| `DEV_DATABASE_URL` | JDBC URL til development-databasen |
| `DEV_USERNAME` | Brugernavn til development-databasen |
| `DEV_PASSWORD` | Password til development-databasen |
| `PORT` | Valgfri port. Standard er `8080` |

Eksempel på database-URL:

```text
jdbc:mysql://localhost:3306/pct
```


Produktionsmiljøet bruger disse variabler:

| Variabel | Beskrivelse |
| --- | --- |
| `PROD_DATABASE_URL` | JDBC URL til produktionsdatabasen |
| `PROD_USERNAME` | Brugernavn til produktionsdatabasen |
| `PROD_PASSWORD` | Password til produktionsdatabasen |

Credentials og secrets må ikke committes til repositoryet.

## Brug

### Start applikationen lokalt

På Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

På macOS/Linux:

```bash
./mvnw spring-boot:run
```

Applikationen kan som standard åbnes på:

```text
http://localhost:8080
```

### Overordnet projektstruktur

```text
src/main/java/com/kodebutikken/pct
├── config          # Spring-konfiguration
├── controller      # Web controllers og routes
├── dto             # Form- og inputobjekter
├── exception       # Custom exceptions og global exception handling
├── model           # Domæne- og modelklasser
├── repository      # Databaseadgang med JdbcTemplate
└── service         # Forretningslogik og adgangskontrol

src/main/resources
├── application.properties
├── application-dev.properties
├── application-prod.properties
├── sql/schema.sql
├── static
└── templates
```

## Bidrag

Læs [CONTRIBUTING.md](CONTRIBUTING.md) for retningslinjer omkring branch naming, coding conventions, tests, pull requests og databaseændringer.

## Developers

Projektet er udviklet af Kodebutikken som en del af PCT-projektet:

- Marcus Bramsen Ejlersen - [Brammern](https://github.com/brammern)
- Oswald Sandengen Carstensen - [Walterb0b](https://github.com/Walterb0b)
- Nikolaj Esberg - [NikolajEsberg](https://github.com/NikolajEsberg)
- Zander Engelstad - [Zander3-2](https://github.com/Zander3-2)

Repository: [Kodebutikken/PCT](https://github.com/Kodebutikken/PCT)
