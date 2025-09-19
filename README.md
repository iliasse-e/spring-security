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

## Une autre solution : JWT

![Capture d'écran 2025-09-17 005801.png](src/main/resources/static/Capture%20d%27%C3%A9cran%202025-09-17%20005801.png)

## Utilisation de Spring Security

Spring security génère une page d'authentification (en communiquant le mot de passe en console).
Ensuite, il cntourne la faille de sécurité CSRF en mettant en place le jeton de sécurité (d'ou la raison pour laquelle la page est bloquée).

Pour avoir la main sur la configuration (dans le cas ou on utilise une stratégie stateless),
on créé un fichier `SecurityConfiguration` pour personnaliser les paramètres de la méthode `configure()` :


`.permitAll()` Pour authoriser toutes URL

`.csrf(...)` Pour désactiver le csrf

`.header(...)` Dans lequel se trouve frameOption, qu'il faut désactiver aussi (dans le cas de l'utilisation de H2 DB)

`.formLogin()` Génère un formulaire d'authentification

### l'interface UserDetailsService

L’interface UserDetailsService est le point d’entrée standard que Spring Security utilise pour récupérer les
informations d’un utilisateur (nom, mot de passe, rôles, etc.) lors de l’authentification.

Spring Security appelle cette méthode pour :

- Trouver l’utilisateur dans ta base de données (ou autre source).

- Retourner un objet UserDetails contenant les infos nécessaires à l’authentification.

```java
UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
```

## Stateless : implémenter JWT

![jwt diagram.png](src/main/resources/static/jwt%20diagram.png)

Conformément au diagram, pour implémenter JWT, on va devoir manipuler 2 méthodes importantes (qu'il va falloir surcharger) :

- ``attemptAuthentication`` Lorsque l'utilisateur tente de s'authentifier
- ``successfulAuthentication`` Une foi l'utilisateur authentifié

Pour cela, on créé une class qui hérite de la class `UsernamePasswordAuthenticationFilter` :

```java
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
    }
}
```

Dans `successfulAuthentication` on génère le jeton `jwt` :

```java
User user = (User) authResult.getPrincipal();
Algorithm algorithm = Algorithm.HMAC256("mySecret1234");
String jwtAccessToken = JWT.create()
        .withSubject(user.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
        .withIssuer(request.getRequestURL().toString())
        .withClaim("roles", user.getAuthorities().stream().map(ga -> ga.toString()).collect(Collectors.toList()))
        .sign(algorithm);

response.setHeader("Authorization", jwtAccessToken);
```

Pour tester :

Methode : POST, URL : `{url}/login`,

avec un body au format `x-www-form-urlencoded`.

En retour, dans le header de la réponse, on a un JWT dans le champ `Authorization`.

### Gestion de la révocation du token

**Problème** : Une fois que l'on génère un token qui expire dans 30 jours, rien n'empêche l'utilisateur d'accéder à l'application durant toute cette période.

**Solution** : Utilisation de 2 token ``Access Token`` & ``Refresh token`` :

#### 🔐 Access Token : le passeport temporaire

- Durée de vie courte : souvent entre 15 minutes et 1 heure.

- Contient les permissions (scopes) : ce que l’utilisateur est autorisé à faire.

- Utilisé pour accéder aux ressources : chaque requête vers une API inclut cet access token.

- Format JWT : il est autoportant, signé, et peut être vérifié sans requête à la base de données.

**⚠️ S’il est volé, l’attaquant peut accéder aux ressources jusqu’à expiration.**

#### 🔁 Refresh Token : le ticket de renouvellement
- Durée de vie longue : plusieurs jours, voire semaines.

- Utilisé pour obtenir un nouvel access token sans que l’utilisateur se reconnecte.

- Stocké de manière sécurisée : souvent côté serveur ou dans un cookie httpOnly.

- Peut être révoqué : contrairement à l’access token, il est souvent stocké en base et donc traçable.

**🎯 Il permet de maintenir une session fluide sans compromettre la sécurité.**


L’access token est comme une carte d’accès temporaire, 
tandis que le refresh token est le mécanisme qui permet de prolonger cette carte sans redemander l’identité à chaque fois. 
Ce duo permet de concilier sécurité stricte et fluidité d’usage.

