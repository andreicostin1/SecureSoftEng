# VaxApp

## About

VaxApp is a web application consisting in an online reservation system for citizens aged 18 years and over who would like to receive their first COVID vaccination (1st or 2nd dose). The system keeps track of the doses received by each individual registered in the system. It also shows statistics about the profile of the individuals who received the vaccination, and provides a forum to ask questions about the vaccination campaign.

## Installation

To install and run this project, make sure you have Maven installed on your computer and that you are using Java version 11.

Clone the repository into a directory of your choice using

> `git clone`

The project can be open in any IDE (we recommend VSCode or Intellij). To run it, use the following command:

> `mvn clean install spring-boot:run`

IntelliJ also provides in-built features for using Maven and running Springboot applications. Simply run Maven lifecycles `clean` and `install`, and then run the `VaxApplication.java` file, which is the entrypoint to the project.

When the application is up and running, the port used will be specified in the terminal. Generally this should be localhost:8080, but it may differ depending on the machine. Please check the terminal to make sure.

## Authors

- Dragoș Feleaga
- Andrei Costin
- Andra Antal-Berbecaru
