@echo off
set SCRIPT=JsonFlattener.groovy
set GROOVY_HOME=C:\groovy-4.0.18

echo Ejecutando el script Groovy: %SCRIPT%

"%GROOVY_HOME%\bin\groovy" %SCRIPT%

echo Script completado.

:: Muestra un mensaje y espera el clic del usuario
echo Presiona cualquier tecla para cerrar la consola...

pause > nul