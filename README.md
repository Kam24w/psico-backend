# 🧠 Psicólogo Virtual - Backend

Backend de la aplicación de acompañamiento emocional inteligente.

## 🛠 Stack
- Java 21
- Spring Boot 3.2
- Spring Security + JWT
- MySQL
- Claude API (Anthropic)

## 🚀 Levantar localmente

### 1. Requisitos
- Java 21
- Maven
- MySQL corriendo en localhost:3306

### 2. Variables de entorno (crear archivo `.env` o configurar en IDE)
```
DATABASE_URL=jdbc:mysql://localhost:3306/psicodb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
DATABASE_USERNAME=root
DATABASE_PASSWORD=tu_password
JWT_SECRET=psico-virtual-secret-key-2024-muy-segura-32chars
ANTHROPIC_API_KEY=tu-api-key-de-claude
CORS_ORIGINS=http://localhost:5173
```

### 3. Ejecutar
```bash
mvn spring-boot:run
```

El servidor corre en: `http://localhost:8080`

## 📡 Endpoints principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/register` | Registro de usuario |
| POST | `/api/auth/login` | Login, retorna JWT |
| POST | `/api/conversacion/mensaje` | Enviar mensaje + emoción |
| GET  | `/api/conversacion/historial/{id}` | Historial de chat |
| POST | `/api/emocion` | Registrar emoción detectada |
| GET  | `/api/emocion/historial/{usuarioId}` | Historial emocional |

## 🚂 Deploy en Railway

1. Crear proyecto en [railway.app](https://railway.app)
2. Conectar repositorio GitHub
3. Agregar plugin MySQL en Railway
4. Configurar variables de entorno en Railway
5. Railway detecta el `Procfile` y despliega automáticamente

## 🏗 Patrones de diseño implementados

- **Strategy**: `EstrategiaFeliz`, `EstrategiaTriste`, `EstrategiaEstresado`, `EstrategiaNeutral`
- **Factory**: `FabricaEstrategia` crea la estrategia según emoción
- **Observer**: `NotificadorEmocion` + `DetectorEmocion` reaccionan a cambios emocionales
- **MVC**: Controllers → Services → Repositories
