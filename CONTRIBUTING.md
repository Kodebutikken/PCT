# Bidrag til PCT

Tak fordi du vil bidrage til PCT. Denne fil beskriver projektets arbejdsgang, kodestandarder, testkrav og forventninger til pull requests.

## Indhold

- [Arbejdsgang](#arbejdsgang)
- [Branch naming](#branch-naming)
- [Kodestandarder](#kodestandarder)
- [Projektets lagdeling](#projektets-lagdeling)
- [Databaseændringer](#databaseændringer)
- [Tests](#tests)
- [Pull requests](#pull-requests)
- [Commits](#commits)
- [Secrets og konfiguration](#secrets-og-konfiguration)
- [Code quality](#code-quality)

## Arbejdsgang

1. Vælg eller opret et issue.
   - Brug de eksisterende GitHub issue templates til bugs, tasks og user stories.
   - Sørg for, at issuet tydeligt beskriver problemet eller den ønskede funktionalitet.

2. Opret en ny branch.
   - Branchen bør tage udgangspunkt i den relevante base branch.
   - Hold branchen fokuseret på én feature, bugfix, refaktorering eller dokumentationsændring.

3. Lav ændringer lokalt.
   - Følg projektets lagdelte struktur.
   - Hold ændringerne så små og overskuelige som muligt.

4. Kør tests.
   - Eksisterende tests må ikke fejle.
   - Tilføj eller opdater tests ved ny forretningslogik eller ændret databaseadfærd.

5. Opret en pull request.
   - Udfyld repositoryets pull request template.
   - Link til det relevante issue.

## Branch naming

Brug korte og beskrivende branchnavne. Anbefalede prefixes:

```text
feature/add-project-members
bugfix/fix-task-deletion
docs/update-readme
refactor/project-service-validation
test/add-task-repository-tests
```

## Kodestandarder

- Brug engelske navne til klasser, metoder, variabler og kommentarer i kode.
- Hold controllers simple og fokuserede på HTTP requests, session, modeldata og redirects.
- Placér forretningslogik, validering og adgangskontrol i services.
- Placér SQL og databaseadgang i repositories.
- Brug `JdbcTemplate` konsekvent i repository-klasser.
- Hold modelklasser fokuseret på domænedata.
- Brug DTO/form-klasser til input fra Thymeleaf views.
- Undgå hardcoded credentials, database-URL'er, passwords og secrets.
- Fjern ubrugte imports og død kode.
- Fjern midlertidigt debug-output såsom `System.out.println`.
- Undgå uforklarede TODO-kommentarer. Hvis en TODO er nødvendig, skal den forklare hvorfor.

## Projektets lagdeling

Projektet følger en klassisk Spring Boot struktur:

```text
Controller -> Service -> Repository -> Database
```

### Controllers

Controllers findes i:

```text
src/main/java/com/kodebutikken/pct/controller
```

Controllers bør:

- Definere routes og request mappings
- Håndtere sessiondata hvor det er nødvendigt
- Tilføje data til `Model`
- Delegere forretningslogik til services
- Returnere view names eller redirects

### Services

Services findes i:

```text
src/main/java/com/kodebutikken/pct/service
```

Services bør:

- Indeholde forretningslogik
- Validere vigtig input
- Håndhæve adgangsregler via `ProjectAccessService`, hvor det er relevant
- Koordinere kald til repositories

### Repositories

Repositories findes i:

```text
src/main/java/com/kodebutikken/pct/repository
```

Repositories bør:

- Bruge `JdbcTemplate` til databaseadgang
- Indeholde SQL tæt på den databaseoperation, som SQL'en understøtter
- Mappe databaserækker til modelklasser
- Undgå web-, session- og controllerlogik

### Models og DTOs

Modelklasser findes i:

```text
src/main/java/com/kodebutikken/pct/model
```

DTO/form-klasser findes i:

```text
src/main/java/com/kodebutikken/pct/dto
```

Brug modelklasser til centrale domænedata og DTO/form-klasser til input fra brugergrænsefladen.

## Databaseændringer

Hvis en ændring påvirker databaseskemaet, skal du:

1. Opdatere:

   ```text
   src/main/resources/sql/schema.sql
   ```

2. Opdatere relevante SQL-queries i repository-klasser.

3. Tilføje eller opdatere tests, hvor det er relevant.

4. Beskrive databaseændringen tydeligt i pull requesten.

Runtime-databasen er MySQL, mens H2 bruges til tests. Vær derfor opmærksom på SQL-syntax, der ikke virker på tværs af begge databaser.

## Tests

Kør tests før du opretter en pull request.

På Windows PowerShell:

```powershell
.\mvnw.cmd test
```

På macOS/Linux:

```bash
./mvnw test
```

Tilføj eller opdater tests når du:

- Tilføjer ny forretningslogik
- Ændrer valideringsregler i services
- Ændrer SQL i repositories
- Retter en bug, som ikke bør opstå igen

Ved større ændringer bør projektet også bygges lokalt.

På Windows PowerShell:

```powershell
.\mvnw.cmd clean install
```

På macOS/Linux:

```bash
./mvnw clean install
```

## Pull requests

Før du åbner en pull request, skal du sikre at:

- Koden compiler.
- Eksisterende tests er kørt.
- Du har lavet self-review af dine ændringer.
- Debug-output og midlertidige kommentarer er fjernet.
- Dokumentation er opdateret, hvis setup, konfiguration eller adfærd ændres.
- Screenshots er vedhæftet, hvis brugergrænsefladen ændres.
- Det relevante issue er linket, fx med `Closes #issue-number`.

Repositoryet har allerede en pull request template. Udfyld den grundigt, så reviewers hurtigt kan forstå hvad der er ændret og hvorfor.

## Commits

Brug korte og tydelige commit messages, der beskriver ændringen.

Eksempler:

```text
Add validation for task estimated hours
Fix project member access update
Update README setup instructions
Refactor resource service permissions
```

## Secrets og konfiguration

Commit aldrig rigtige secrets eller credentials. Projektet bruger miljøvariabler til databasekonfiguration:

- `DEV_DATABASE_URL`
- `DEV_USERNAME`
- `DEV_PASSWORD`
- `PROD_DATABASE_URL`
- `PROD_USERNAME`
- `PROD_PASSWORD`

Produktionssecrets skal håndteres via deploymentmiljøet eller GitHub Actions secrets.

## Code quality

Repositoryet bruger Qodana via GitHub Actions. Forsøg at løse advarsler før review, især advarsler om ubrugte imports, død kode, risikable SQL-ændringer eller maintainability.

## Review forventninger

Reviewers bør kontrollere at:

- Ændringen løser det relevante issue eller user story.
- Koden følger controller/service/repository-strukturen.
- Tests er tilføjet eller opdateret hvor relevant.
- Databaseændringer er dokumenteret og afspejlet i SQL-scriptet.
- Den berørte UI-flow stadig fungerer.
- Der ikke er credentials, debug-output eller irrelevante ændringer med.