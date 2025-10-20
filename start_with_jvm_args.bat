@echo off
echo Starting application with Java 22 module system fixes...

set JAVA_OPTS=--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED

.\mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments="%JAVA_OPTS%"

pause
