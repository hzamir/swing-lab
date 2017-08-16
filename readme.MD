# swing-lab is a sample application demonstrating some swing components

##Architectural Notes
###Spring Boot
SwingLab is now Springbootified to get the basic spring boot goodies
* the yaml based configuration hierarchy
* configurations are loaded into configuration objects that validate settings
* the logback logging
* the very nice annotations for components and services

###Division of responsibilities
* Application is nearly generic setup for swing application
  * It also sets up the look and feel
* Runner
  * processes the command line (not necessary in this casee)
  * makes the main swing frame visible within an AWT EventQueue
* SwingLabMainFrame
  * Springboot component (a singleton)
  * Uses Constructor-based injection


##UI Features
The key feature is to demonstrate how a snaking table can be implemented
