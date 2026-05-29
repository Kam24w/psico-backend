# Mindsee Backend — Plataforma de Acompañamiento Emocional Inteligente

Backend empresarial de alto rendimiento desarrollado en **Spring Boot 3.2** y **Java 21**, diseñado para el procesamiento y análisis del estado emocional del usuario en tiempo real. Integra seguridad JWT de nivel empresarial, servicios de Rate Limiting con bypass inteligente y el consumo de modelos avanzados de Inteligencia Artificial de Google (Gemini) y Groq a través del patrón Facade.

---

## 🛠️ Stack Tecnológico & Arquitectura

*   **Core Framework:** Java 21 & Spring Boot 3.2.x (Spring MVC, Spring Data JPA, Spring Security)
*   **Seguridad:** JWT (Json Web Tokens) con cifrado robusto para protección de endpoints y contraseñas hasheadas mediante **BCrypt**.
*   **Base de Datos:** Postgres / Supabase con mapeo objeto-relacional mediante Hibernate.
*   **Inteligencia Artificial:** Google Gemini API y Groq configurados con soporte multimodelo dinámico.
*   **Patrones de Diseño Clave:**
    *   **Facade (`EmotionPipelineFacade`):** Orquesta en un solo flujo unificado la evaluación emocional de la cámara, la actualización de perfiles, el guardado de memorias clínicas y la evaluación de riesgos de seguridad.
    *   **Strategy (`EstrategiaFeliz`, `EstrategiaTriste`, `EstrategiaEstresado`, `EstrategiaNeutral`):** Aborda el comportamiento del psicólogo IA en base al estado de ánimo.
    *   **Factory (`FabricaEstrategia`):** Instancia dinámicamente la estrategia cognitiva adecuada en base al estado del usuario.
    *   **Observer (`NotificadorEmocion` y `DetectorEmocion`):** Suscripción reactiva para propagar notificaciones emocionales.

---

## 📁 Estructura Detallada del Proyecto (Backend)

A continuación se muestra el árbol completo del código fuente con la explicación técnica de lo que hace cada parte:

```text
src/main/java/com/psico/app/
│
├── PsicoApplication.java             # Clase principal que arranca la aplicación Spring Boot.
├── GlobalExceptionHandler.java       # Manejador global de excepciones (RestControllerAdvice) que formatea y centraliza las respuestas de error de la API.
│
├── ai/                               # Gestión e integración de Inteligencia Artificial
│   ├── client/
│   │   ├── AIProvider.java           # Interfaz común para los proveedores de IA.
│   │   ├── AIProviderFactory.java    # Fábrica encargada de instanciar y enrutar las llamadas dinámicamente a Gemini o Groq.
│   │   ├── GeminiClient.java         # Cliente de integración con la API de Google Gemini.
│   │   └── GroqClient.java           # Cliente de integración con los modelos de código abierto a través de Groq.
│   └── facade/
│       └── EmotionPipelineFacade.java # [Facade] Simplifica el pipeline: recibe el mensaje, analiza la emoción, invoca la IA, registra notas clínicas y evalúa alertas de seguridad en un solo llamado.
│
├── auth/                             # Autenticación, Seguridad y Control de Acceso
│   ├── controller/
│   │   └── AuthController.java       # Endpoints públicos `/api/auth` para login y registro de usuarios.
│   ├── dto/
│   │   └── AuthDTOs.java             # Objetos de transferencia de datos (LoginRequest, RegisterRequest, AuthResponse).
│   ├── model/
│   │   ├── User.java                 # Entidad JPA del usuario con contraseñas cifradas y roles.
│   │   └── Role.java                 # Enumeración de roles del sistema (USER, ADMIN).
│   ├── security/
│   │   ├── JwtUtil.java              # Utilidad para generar, firmar y validar tokens JWT.
│   │   ├── JwtAuthenticationFilter.java # Filtro interceptor HTTP que procesa y valida la cabecera Bearer Token.
│   │   └── SecurityConfig.java       # Configuración global de Spring Security, CORS, filtros y protección de endpoints.
│   └── service/
│       ├── AuthService.java          # Lógica de registro, hashing con BCrypt y generación de sesiones.
│       ├── RateLimiterService.java   # [Rate Limiter] Control in-memory de fuerza bruta (5 intentos / 15 mins) con lista blanca local.
│       └── CustomUserDetailsService.java # Carga las credenciales del usuario desde la base de datos para Spring Security.
│
├── conversation/                     # Módulo de Chat y Sesiones Terapéuticas
│   ├── controller/
│   │   ├── ConversationController.java # Endpoints `/api/conversations` para enviar mensajes, ver historiales y gestionar el chat.
│   │   └── InitiateController.java   # Endpoint `/api/conversations/initiate` para crear sesiones y elegir la personalidad de la IA.
│   ├── dto/
│   │   └── ConversationDTOs.java     # Modelos de mensajería para transferencias de red.
│   ├── model/
│   │   ├── Conversation.java         # Entidad de la sesión de chat (guarda la personalidad de la IA y el estado de cierre).
│   │   └── Message.java              # Entidad que modela un mensaje individual (emisor, texto, fecha y emoción).
│   ├── repository/
│   │   ├── ConversationRepository.java # Operaciones de persistencia para sesiones de conversación.
│   │   └── MessageRepository.java     # Operaciones de persistencia para los mensajes.
│   └── service/
│       └── ConversationService.java  # Lógica de cierre, reanudación y borrado de historiales de conversación.
│
├── emotion/                          # Registro y Análisis de Emociones
│   ├── controller/
│   │   └── EmotionController.java    # Endpoints `/api/emotions` para consultas de logs emocionales.
│   ├── model/
│   │   ├── EmotionRecord.java        # Log individual de lectura emocional con fecha y hora.
│   │   └── EmotionType.java          # Enumerado con los estados (FELIZ, TRISTE, ANSIOSO, ESTRESADO, ENOJADO, NEUTRAL).
│   ├── repository/
│   │   └── EmotionRepository.java    # Consultas para obtener estadísticas semanales e históricos del estado de ánimo.
│   └── service/
│       └── EmotionService.java       # Lógica para computar y persistir registros emocionales y sus variaciones.
│
├── dashboard/                        # Reportes y Consola Analítica para Demostraciones
│   ├── controller/
│   │   └── DashboardController.java  # Endpoints `/api/dashboard` para extraer métricas agrupadas por usuario.
│   ├── dto/
│   │   └── DashboardDTOs.java        # Estructura del payload con gráficos, emociones y alertas agregadas.
│   └── service/
│       └── DashboardService.java     # Calcula la distribución de emociones, evolución diaria y alertas acumuladas.
│
├── memory/                           # Notas Clínicas de Persistencia de la IA
│   ├── controller/
│   │   └── MemoryController.java     # Endpoints `/api/memories` para gestionar notas clínicas y observaciones.
│   ├── model/
│   │   └── UserMemory.java           # Entidad que almacena notas conductuales permanentes inferidas por la IA.
│   ├── repository/
│   │   └── MemoryRepository.java     # Repositorio JPA para persistencia de recuerdos u observaciones terapéuticas.
│   └── service/
│       └── MemoryService.java        # Lógica de procesamiento y creación de notas clínicas del paciente.
│
├── intervention/                     # Estrategias y Recomendaciones Terapéuticas
│   ├── controller/
│   │   ├── InterventionController.java # Endpoints `/api/intervention` para recomendaciones reactivas por emoción.
│   │   └── TherapeuticController.java # Endpoints `/api/therapy` para ejercicios guiados de TCC y relajación.
│   └── service/
│       ├── TherapeuticService.java   # Gestiona el catálogo de ejercicios de mindfulness e intervenciones.
│       ├── FabricaEstrategia.java    # [Factory Pattern] Retorna la estrategia cognitiva correspondiente.
│       └── EstrategiaCognitiva.java   # Estrategias adaptativas por emoción (EstrategiaFeliz, EstrategiaTriste, etc.).
│
├── risk/                             # Evaluación de Alertas Clínicas y Seguridad
│   ├── model/
│   │   └── RiskAlert.java            # Entidad que registra palabras de riesgo, fragmentos detectados y niveles de alerta.
│   ├── repository/
│   │   └── RiskAlertRepository.java  # Repositorio JPA para auditorías de riesgo clínico.
│   └── service/
│       └── RiskAlertService.java     # Lógica automática que escanea los textos de los chats en busca de síntomas alarmantes.
│
├── user/                             # Perfiles y Preferencias del Usuario
│   ├── controller/
│   │   └── UserProfileController.java # Endpoints `/api/users` para obtener y guardar configuraciones personales.
│   ├── model/
│   │   └── UserProfile.java          # Entidad que guarda preferencias (como avatar seleccionado y tipo de IA).
│   ├── repository/
│   │   └── UserProfileRepository.java # Operaciones de persistencia para configuraciones individuales del perfil.
│   └── service/
│       └── UserProfileService.java   # Lógica para cambiar preferencias y asociar avatares al perfil del usuario.
```

---

## 🔒 Rate Limiter con Bypass Inteligente (Local Whitelist)

La aplicación cuenta con un módulo de protección de fuerza bruta en el login (`RateLimiterService`):
*   **Bloqueo Estándar:** Bloquea de forma in-memory cualquier IP externa que realice **5 intentos de login fallidos** consecutivos durante **15 minutos**.
*   **Bypass de Desarrollo (Whitelist):** Las IPs correspondientes a entornos locales (`127.0.0.1`, `::1`, `localhost`, `0:0:0:0:0:0:0:1`) están en una lista blanca permanente. Como desarrollador o dueño de la app, **nunca quedarás bloqueado** durante las pruebas locales, asegurando la máxima comodidad en desarrollo sin comprometer la seguridad en producción.

---

## 🚀 Levantar el Proyecto Localmente

### 1. Variables de Entorno (Archivo `.env` en `psico-backend/`)
```env
DATABASE_URL=jdbc:postgresql://aws-1-us-west-1.pooler.supabase.com:6543/postgres?sslmode=require
DATABASE_USERNAME=postgres.viuesoohxisioluoveap
DATABASE_PASSWORD=psico2026...
JWT_SECRET=psico-virtual-secret-key-2024-muy-segura-32chars
GEMINI_API_KEY=tu-api-key-de-google-gemini
GEMINI_MODEL=gemini-1.5-flash
CORS_ORIGINS=http://localhost:5173
```

### 2. Ejecutar la Aplicación
```bash
# Windows
.\mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```
El servidor iniciará localmente en: `http://localhost:8080`

---

## 📊 Semillero de Datos Integrado (Python Script)
Para cargar de forma inmediata la demo con **10 usuarios de prueba**, **10 días completos de historial de conversaciones**, emociones diarias, memorias y alertas en Supabase, ejecuta:
```bash
python C:\Users\USUARIO\.gemini\antigravity-ide\brain\b9dbaa37-a65c-4647-965f-29903f94f549\scratch\seed_mock_data.py
```
*   **Contraseña Universal de Demo:** `123456`
*   **Usuario Clave:** `juan.perez@psico.com` (contiene caso clínico de Burnout, alerta de riesgo moderada e historial completo para pruebas del Dashboard).
