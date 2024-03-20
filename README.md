# Regulus: Waste Management System
Regulus is a waste management system designed to efficiently manage the
collection and disposal of waste materials. It provides a platform for
coordinating the efforts of collection agents, optimizing routes, and ensuring
timely disposal of waste in an environmentally sustainable manner.


## Setup Instructions
Clone the repository:

```shell
git clone https://github.com/maelstrom0x8/regulus.git
```
Build the project using Gradle:

```shell
./gradlew build # Linux/Unix
gradlew build # Windows
```
Deploy the application using Payara Micro:

```shell
java -jar payara-micro-<version>.jar --deploy build/libs/regulus.war
```

Access the application at http://localhost:8080/regulus.
