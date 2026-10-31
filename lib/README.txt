Place the MySQL Connector/J JAR file here.

Download from:
  https://dev.mysql.com/downloads/connector/j/

Example file name:  mysql-connector-java-8.0.33.jar

After placing the JAR here, compile and run with:

  Windows:
    javac -cp "lib\mysql-connector-java-8.0.33.jar" -d out\classes <all .java files>
    java  -cp "out\classes;lib\mysql-connector-java-8.0.33.jar" com.placement.Main

  Linux/macOS:
    javac -cp "lib/mysql-connector-java-8.0.33.jar" -d out/classes @sources.txt
    java  -cp "out/classes:lib/mysql-connector-java-8.0.33.jar" com.placement.Main
