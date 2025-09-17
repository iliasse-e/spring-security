# Spring Security

[Cours youtube](https://www.youtube.com/watch?v=3q3w-RT1sg0&ab_channel=ProfesseurMohamedYOUSSFI)

## Resumer des systèmes d'authentification

![Capture d'écran 2025-09-17 004715.png](src/main/resources/static/Capture%20d%27%C3%A9cran%202025-09-17%20004715.png)

### Tableau 1 : Stateful

![stateful.png](src/main/resources/static/stateful.png)

### Tableau 2 : Stateless

![stateless.png](src/main/resources/static/stateless.png)

## Faille de sécurité CSRF

### Problème :

![Capture d'écran 2025-09-17 005359.png](src/main/resources/static/Capture%20d%27%C3%A9cran%202025-09-17%20005359.png)

### Une solution :

![Capture d'écran 2025-09-17 005615.png](src/main/resources/static/Capture%20d%27%C3%A9cran%202025-09-17%20005615.png)

## JWT

![Capture d'écran 2025-09-17 005801.png](src/main/resources/static/Capture%20d%27%C3%A9cran%202025-09-17%20005801.png)

## Spring Security

Spring security génère une page d'authentification (en communiquant le mot de passe en console).
Ensuite, il cntourne la faille de sécurité CSRF en mettant en place le jeton de sécurité (d'ou la raison pour laquelle la page est bloquée).

Pour avoir la main sur la configuration (dans le cas ou on utilise une stratégie stateless),
on créé un fichier `SecurityConfiguration` pour personnaliser les paramètres :


`.permitAll()` Pour authoriser toutes URL

`.csrf(...)` Pour désactiver le csrf

`.header(...)` dans lequel se trouve frameOption, qu'il faut désactiver aussi 

