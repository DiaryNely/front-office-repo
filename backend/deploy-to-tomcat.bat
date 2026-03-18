@echo off
chcp 65001 >nul
echo =====================================================
echo   COMPILATION ET DEPLOIEMENT - FRONT-OFFICE
echo =====================================================
echo.

REM Configuration
set TOMCAT_HOME=C:\apache-tomcat-10.1.52
set PROJECT_DIR=%~dp0
set WAR_NAME=front-office.war

echo [1/4] Nettoyage et compilation Maven...
echo =====================================================
cd "%PROJECT_DIR%"
call mvn clean package
if errorlevel 1 (
    echo.
    echo ❌ ERREUR : La compilation a echoue
    pause
    exit /b 1
)

echo.
echo [2/4] Verification du fichier WAR...
echo =====================================================
if not exist "target\%WAR_NAME%" (
    echo ❌ ERREUR : Le fichier WAR n'a pas ete genere
    pause
    exit /b 1
)
echo ✅ Fichier WAR trouve : target\%WAR_NAME%

echo.
echo [3/4] Arret de Tomcat (si deja demarre)...
echo =====================================================
if exist "%TOMCAT_HOME%\bin\shutdown.bat" (
    call "%TOMCAT_HOME%\bin\shutdown.bat"
    timeout /t 5 /nobreak >nul
) else (
    echo ⚠️  Tomcat non trouve a : %TOMCAT_HOME%
    echo    Modifiez la variable TOMCAT_HOME dans ce script
    pause
    exit /b 1
)

echo.
echo [4/4] Deploiement du WAR sur Tomcat...
echo =====================================================
REM Supprimer l'ancien deploiement
if exist "%TOMCAT_HOME%\webapps\%WAR_NAME%" (
    del /F /Q "%TOMCAT_HOME%\webapps\%WAR_NAME%"
    echo ✅ Ancien WAR supprime
)
if exist "%TOMCAT_HOME%\webapps\front-office" (
    rmdir /S /Q "%TOMCAT_HOME%\webapps\front-office"
    echo ✅ Ancien dossier de deploiement supprime
)

REM Copier le nouveau WAR
copy /Y "target\%WAR_NAME%" "%TOMCAT_HOME%\webapps\"
if errorlevel 1 (
    echo ❌ ERREUR : Echec de la copie du WAR
    pause
    exit /b 1
)
echo ✅ WAR copie vers Tomcat

echo.
echo [5/5] Demarrage de Tomcat...
echo =====================================================
call "%TOMCAT_HOME%\bin\startup.bat"

echo.
echo =====================================================
echo   ✅ DEPLOIEMENT TERMINE
echo =====================================================
echo.
echo 📌 Application disponible sur :
echo    http://localhost:8080/front-office
echo.
echo 📌 API Reservations :
echo    http://localhost:8080/front-office/api/reservations
echo.
echo ⏳ Attendez quelques secondes que Tomcat demarre...
echo.
pause
