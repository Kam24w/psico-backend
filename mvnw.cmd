@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements. See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership. The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License. You may obtain a copy of the License at
@REM
@REM https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied. See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET "BASE_DIR=%~dp0") ELSE (SET "BASE_DIR=%__MVNW_ARG0_NAME__%")
@IF "%BASE_DIR:~-1%"=="\" SET "BASE_DIR=%BASE_DIR:~0,-1%"
@SET MAVEN_PROJECTBASEDIR=%BASE_DIR%
@SET WRAPPERJAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
@SET WRAPPERPROPERTIES="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties"

@REM Find java.exe
@IF NOT "%JAVA_HOME%"=="" goto OkJHome
@FOR %%i IN (java.exe) DO @SET JAVA_EXE=%%~$PATH:i
@IF NOT "%JAVA_EXE%"=="" goto init
@ECHO.
@ECHO ERROR: JAVA_HOME not found in your environment. >&2
@ECHO Please set the JAVA_HOME variable in your environment to match the >&2
@ECHO location of your Java installation. >&2
@GOTO error

:OkJHome
@SET JAVA_EXE=%JAVA_HOME%/bin/java.exe
@IF EXIST "%JAVA_EXE%" goto init
@ECHO.
@ECHO ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% >&2
@ECHO Please set the JAVA_HOME variable in your environment to match the >&2
@ECHO location of your Java installation. >&2
@GOTO error

:init
@SET MAVEN_OPTS=%MAVEN_OPTS% "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%"

@IF EXIST %WRAPPERJAR% (
    @SET DOWNLOAD_URL=
    @FOR /F "tokens=1,2 delims==" %%A IN (%WRAPPERPROPERTIES%) DO (
        @IF "%%A"=="wrapperUrl" SET DOWNLOAD_URL=%%B
    )
) ELSE (
    @SET DOWNLOAD_URL=
    @FOR /F "tokens=1,2 delims==" %%A IN (%WRAPPERPROPERTIES%) DO (
        @IF "%%A"=="wrapperUrl" SET DOWNLOAD_URL=%%B
    )
    @ECHO Downloading: %DOWNLOAD_URL%
    @powershell -Command "& {(New-Object System.Net.WebClient).DownloadFile('%DOWNLOAD_URL%', '%WRAPPERJAR%')}"
    @IF "%ERRORLEVEL%"=="0" (ECHO Downloaded successfully) ELSE (GOTO error)
)

@SET MAVEN_CMD_LINE_ARGS=%*
@"%JAVA_EXE%" %JVM_CONFIG_MAVEN_PROPS% %MAVEN_OPTS% %MAVEN_DEBUG_OPTS% -classpath %WRAPPERJAR% "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %MAVEN_CMD_LINE_ARGS%
@IF NOT "%ERRORLEVEL%"=="0" GOTO error
@GOTO end

:error
@SET ERROR_CODE=%ERRORLEVEL%
@ECHO.
@ECHO ERROR: Maven wrapper failed with exit code %ERROR_CODE% >&2
@EXIT /B %ERROR_CODE%

:end
@EXIT /B 0
