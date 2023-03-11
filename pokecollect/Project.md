### Entwickelt von Berkan Yildiz und Johannes Belaschow in 100% Pair Programming.

### Genutzte Technologien:
Quarkus, JetBrains Code With (IntelliJ) Me, CodeTogether (VScode), Rest Client API, Keycloak,
JPA, Hibernate Validator, JSON-B, RestAssured, Hibernate Panache

### Start
1) Docker starten
``` 
2) ./mvnw compile quarkus:dev
```
### Optional wenn Angular Ionic Frontend genutz werden soll:
```
localhost:8180 im Webbrowser eingeben und mit "admin" (benutzername) und "admin" ("passwort") einloggen
Das Realm von Master auf Quarkus umstellen
Den Reiter "Clients" auswählen
Den Client frontend-ionic auswählen
Valid redirect Url setzen : http://localhost:4200/*
Weborigin setzen: *
Nun kann ein externes Frontend verwendet werden.
```
### Testen mit SwaggerUI
<b>localhost:8080</b> im Webbrowser eingeben und über die Eingabemaske das SwaggerUI öffnen und Benutzer anmelden
```
"berkan" (Benutzername) "berkan" (Password)
oder
"johannes" (Benutzername) "password" (Password)
Der Benutzer "berkan" hat die Rollen: user,admin
Der Benutzer "johannes" hat die Rolle: user
```
Nun ist das Programm lauffähig und kann getestet werden





