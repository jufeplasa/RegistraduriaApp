# Guía para Ejecutar el Proyecto

## Requisitos Previos
Asegúrate de tener configurada la conexión a la base de datos (BD) antes de proceder.

---

## Pasos para Ejecutar el Proyecto

### 1. Desplegar el Registro de IceGrid
En una consola, navega a la carpeta `iceGrid` y ejecuta el siguiente comando:
```bash
icegridregistry --Ice.Config=config.registry
```

En otra Consola en la carpeta iceGrid, despliegue el nodo
```bash
icegridnode --Ice.Config=config.node 
```
En otra consola despliegue
```bash
icegridadmin
```

Te pedira user y password puedes escribir cualquier cosa
revisa si hay servidores con
```bash
server list
```
y verifica que este esperando con
```bash
server state SimpleServer-#(El numero del servidor que quieras revisar)
```
En caso de que no te aparezcan los servers puedes crearlos con
```bash
application add/update (solo una de las dos) application.xml
```

Si deseas agregar otro servidor
```bash
server template instantiate Simple node1 SimpleServer index=#(número de un server que no existe)
```

Ahora hay que elevar el servicio del OBSERVER

desde la raiz del proyecto en una consola ejecuta.
```bash
icebox --Ice.Config=config.icebox  
```

Luego ejecutar la aplicación Servidor 
```bash
java -jar server/build/libs/server.jar
```
Desplegar N clientes se quiera
```bash
java -jar client/build/libs/client.jar
```