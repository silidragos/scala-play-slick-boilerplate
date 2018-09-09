##Reload libraries
sbt reload
sbt update

## Run app in dev mode
sbt "run -Dconfig.resource=application-dev.conf"