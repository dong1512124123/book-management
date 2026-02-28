@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF)
@REM Maven Wrapper start up script for Windows
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0__%"=="" SET __MVNW_ARG0__=%~dpnx0
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0'; $env:MVNW_REPOURL = if($env:MVNW_REPOURL){$env:MVNW_REPOURL}else{'https://repo.maven.apache.org/maven2'}; $scp=$scriptDir+'.mvn/wrapper/maven-wrapper.properties'; if (Test-Path $scp) { Get-Content $scp | ForEach-Object { if(/^(?:distributionUrl|wrapperUrl)\s*=/) { $_.Trim() } } } else { Write-Output ('distributionUrl='+$env:MVNW_REPOURL+'/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip') }; Write-Output ('SET MVNW_JAVA_COMMAND='+$(if($env:JAVA_HOME){$env:JAVA_HOME+'\bin\java'}else{'java'}))}"`) DO @SET "%%A"
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%

@SET "distributionUrl=%distributionUrl:~0,-1%"

@SET MVNW_HOME=%USERPROFILE%\.m2\wrapper
@SET WRAPPER_JAR=%MVNW_HOME%\maven-wrapper.jar

@IF NOT EXIST "%WRAPPER_JAR%" (
    powershell -noprofile -Command "&{$uri='%wrapperUrl%'; $out='%WRAPPER_JAR%'; $dir=(Split-Path $out); if(!(Test-Path $dir)){New-Item -Path $dir -ItemType Directory -Force | Out-Null}; [Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri $uri -OutFile $out}"
)

@IF NOT EXIST "%WRAPPER_JAR%" (
    echo Error: Could not download Maven wrapper jar
    exit /b 1
)

@SET MAVEN_HOME=%MVNW_HOME%\apache-maven-3.9.6
@IF NOT EXIST "%MAVEN_HOME%" (
    powershell -noprofile -Command "&{$uri='%distributionUrl%'; $zip='%MVNW_HOME%\maven.zip'; [Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri $uri -OutFile $zip; Expand-Archive -Path $zip -DestinationPath '%MVNW_HOME%' -Force; Remove-Item $zip}"
)

@SET PATH=%MAVEN_HOME%\bin;%PATH%
%MVNW_JAVA_COMMAND% %MAVEN_OPTS% -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
