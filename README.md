# TP 6 — Simuler un conflit de réservation concurrent et démontrer l'optimistic locking avec @Version

## 1. Configuration Maven et persistence.xml

![pom.xml et persistence.xml](screenshots/01-pom-persistence.png)

## 2. Entité Reservation avec le champ @Version

![Entité Reservation](screenshots/02-entite-version.png)

## 3. Service de gestion des réservations

![Service](screenshots/03-service.png)

## 4. Simulation du conflit et stratégie de retry

![Simulation et retry](screenshots/04-simulation-retry.png)

## 5. Exécution — conflit détecté puis résolu après retry

![Exécution](screenshots/05-execution.png)
