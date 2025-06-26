```
(\ (\
( • •)  
━∪∪━━━━ 
ᵇʸ ᴬˡᵉᶠᵘᵉⁿᵗᵉˢ
```
# SPRING | Múltiples Brokers
<img src="https://img.shields.io/badge/Spring-informational?style=flat-square&logo=spring&logoColor=6db33f&color=ffffff" />
<img src="https://img.shields.io/badge/Redis-informational?style=flat-square&logo=redis&logoColor=ff4438&color=ffffff" />
<img src="https://img.shields.io/badge/Kafka-informational?style=flat-square&logo=apachekafka&logoColor=231f20&color=ffffff" />
<img src="https://img.shields.io/badge/RabbitMQ-informational?style=flat-square&logo=rabbitmq&logoColor=ff6600&color=ffffff" />
<img src="https://img.shields.io/badge/ActiveMQ-informational?style=flat-square&color=ffffff" />
<img src="https://img.shields.io/badge/Docker-informational?style=flat-square&logo=docker&logoColor=2496ed&color=ffffff" />

<img src="https://img.shields.io/badge/Dev-Alejandro.Fuentes-informational?style=flat-square&logoColor=white&color=cdcdcd" />


Este proyecto es una aplicación fullstack (Java/Angular) de ejemplo que demuestra uan arquitectura moderna y desacoplada para un sistema de gestion de notificaciones.

El objetivo principal es servir a un laboratorio práctico (hands-on lab) para entender y comparar la integración de `ActiveMQ`, `RabbitMQ` y `Kafka` en un ecosistema de Sprint Boot y Angular.



```mermaid
graph TD
    subgraph "Usuario"
        U[<br/>🙋‍♂️<br/>Usuario]
    end

    subgraph "Frontend"
        A[<b>Angular</b><br/>http://localhost:4200]
    end

    subgraph "Backend - Spring Boot"
        subgraph "Capa API"
            C(NotificationController)
        end
        subgraph "Capa de Servicio"
            S(NotificationService)
        end
        subgraph "Capa de Datos"
            R(NotificationRepository)
        end
        subgraph "Capa de Mensajería (Productores)"
            P(Productores<br/>JmsTemplate, RabbitTemplate, KafkaTemplate)
        end
        subgraph "Capa de Mensajería (Consumidores)"
            CON(MessageConsumers<br/>@JmsListener, @RabbitListener, @KafkaListener)
        end
        subgraph "Salida"
            LOGS[<br/>📝<br/>Consola del Backend]
        end
    end

    subgraph "Infraestructura (Docker)"
        DB[(<br/>🐘<br/>PostgreSQL)]
        B1{<br/>📦<br/>ActiveMQ}
        B2{<br/>🐰<br/>RabbitMQ}
        B3{<br/>⚫<br/>Kafka}
    end

    U -- Interactúa con --> A
    A -- 1. Envía Petición REST (POST) --> C
    C -- Llama a --> S
    S -- 2. Guarda en BD --> DB
    S -- 3. Publica Eventos --> P
    P -- 3a. Envía a --> B1
    P -- 3b. Envía a --> B2
    P -- 3c. Envía a --> B3
    S -- Responde a --> C
    C -- Responde OK --> A

    B1 -- 4a. Mensaje recibido --> CON
    B2 -- 4b. Mensaje recibido --> CON
    B3 -- 4c. Mensaje recibido --> CON
    CON -- Imprime en --> LOGS
```

## Subiendo los contenedores Docker

para subir las instancias dockers
```bash
docker-compose up -d
```

para finalizar las instancias dockers.
```bash
docker-compose down
```

## Verificar los contenedores

### Testando container | POSTGRES
Verificar los logs
```bash
docker-compose logs postgres
```
Verificar desde dentro del contenedor 
```
# verificar el nombre del contenedor postgres -> ho-sp-db-postgres
docker ps

# conectar al contenedor
docker -it ho-sp-db-postgres psql -U admin -d ho-sp-db-sc-notification
```

> ☕︎ explicando el comando: <br>
> `docker exec`: Ejecuta un comando en un contenedor en ejecución. <br>
> `-it`: Modos interactivo (-i) y TTY (-t), que te permiten interactuar con el comando. <br>
> `ho-sp-db-postgres`: El nombre de nuestro contenedor. <br>
> `psql -U admin -d ho-sp-db-sc-notification`: El comando a ejecutar dentro del contenedor.<br>
> * `psql` es el cliente, 
> * `-U admin` especifica el usuario y 
> * `-d ho-sp-db-sc-notification` especifica la base de datos a la que te quieres conectar. <br>


### Testando container | ACTIVEMQ

Verificar los logs
```bash
docker-compose logs activemq
```
Verificar desde dentro del contenedor 
```
# verificar el nombre del contenedor activemq -> ho-sp-broker-activemq
docker ps
```
Si esta todo bien, podemos utilizar la url `http://localhost:8161`

> ☕︎ al tentar usar el teste via http, te pedirá un usuario y una contraseña. Para la imagen de Docker que estamos usando (rmohr/activemq), las credenciales por defecto son: <br> 
> * Usuario: admin
> * Contraseña: admin

### Testando container | KAFKA

> ☕︎ en versiones mas actuales no será utilizado ZooKeeper

Kafka utiliza en su arquitectura para gestion el ZooKeeper, es por eso que al testar KAFKA, verificaremos el Zookeeper esté ejecutado.

**Testando Zookeeper**
```bash
# verificar el log
docker-compose logs zookeeper

# conectar a la consola zookeeper
docker exec -it ho-sp-broker-zookeeper zookeeper-shell localhost:2181
```

> ☕︎ dentro de la consola se puede ejecutar el comando `ls /` para ver todos los directorios creados

**Testando Kafka**

```bash
# verificar el contenedor de kafka
docker-compose ps

# verificar el log
docker-compose logs kafka
```

Para testar `producer` y `consumer` en kafka

en el primer bash (bash-1), ejectuar :
```bash
# conectar a um `producer`
docker exec -it ho-sp-broker-kafka //usr/bin/kafka-console-producer --bootstrap-server localhost:29092 --topic notification.topic
```

en otro bash (bash-2), ejectuar :
```bash
# conectar a um `consumer`
docker exec -it ho-sp-broker-kafka //usr/bin/kafka-console-consumer --bootstrap-server localhost:29092 --topic notification.topic
```

para testar escribir un mensage en el bash-1, y debera reflectirse en el bash-2.

