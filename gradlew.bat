@echo off
setlocal

:: Check for JAVA_HOME
if "%JAVA_HOME%"=="" (
    echo ERROR: JAVA_HOME is not set. Please set the JAVA_HOME environment variable.
    exit /b 1
)

set GRADLE_WRAPPER_JAR=gradle/wrapper/gradle-wrapper.jar

:: Check if the gradle-wrapper.jar exists
if not exist "%GRADLE_WRAPPER_JAR%" (
    echo ERROR: The file %GRADLE_WRAPPER_JAR% does not exist. Please ensure you have the Gradle wrapper files set up.
    exit /b 1
)

set CLASSPATH=%GRADLE_WRAPPER_JAR%

:: Execute the Gradle wrapper
java -cp "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

endlocal
