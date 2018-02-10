Projekt Aufgabenplaner
======================

                                                          Michal Kubacki, Daniel Schobert
                                                          
Dieses Projekt werden Sie im Laufe des Semesters zu einer Applikation unter Berücksichtigung
wichtiger Prinzipien des Software Engineering entwickeln.

Es wird zunächst nur in rudimentärer Form ausgegeben.

Dieses Projekt benutzt

- JDK 8
- Maven 3
- Hibernate 5.1.0.Final
- JPA 2.1
- JavaFX für das GUI/Frontend

Genauere Angaben findet man in der Datei `pom.xml`. 

Mit  `mvn clean test`   werden alle notwendigen Bibliotheken geholt, das Projekt kompiliert und die (anfangs leere) Testsuite ausgeführt.

Wenn das funktioniert, können Sie das Maven-Projekt in Ihre Java-IDE importieren.

Wo sind die Meldungstexte?

- In der Datei `MessageResources.properties`. Das Original, welches Sie editieren können, ist unter `src/main/resources/`

Why did the changes to my  `MessageResources.properties`  or other resource file disappear?
Why didn't the changes to my  `MessageResources.properties`  or other resource file appear?

- The original resource files are under  `src/main/resources/`  and copied to  `target/classes/`  during a build. 
  Change the content of `src/main/resources/MessageResources.properties` and rebuild before running it again.
  The exception message texts extracted from the JavaDoc comments of all exceptions under `src/main/java/`
  are collected by the  `ExceptionMessagesDoclet`  as configured for the `maven-javadoc-plugin`.
  


