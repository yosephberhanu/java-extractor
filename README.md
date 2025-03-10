# Java-Extractor
A small java tool to extract package, class,  methods+properties infomration from a java project.

## How it works 
It travrses through the provided project folder and extract the required infomration using [JavaParser](https://github.com/javaparser/javaparser). It saves the information to an sqlite database.

## How to run
### Compile
`bash
./gradlew clean build
`
### Run
`bash
./gradlew run --args="<project-folder>  <sqlite-db-file>"
`