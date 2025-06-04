# DirtyCouture

**DirtyCouture** es una plataforma de venta de ropa urbana construida con arquitectura fullstack moderna:  
- **Backend:** Ktor + Kotlin  
- **Frontend:** Vue.js 3 + Pinia  
- **Base de datos:** Supabase (PostgreSQL)  
- **Servicios externos:** Stripe (pagos), SendGrid (emails)  
- **CI/CD:** GitHub Actions + Render + Docker

---

## 🚀 Funcionalidades clave

- Registro e inicio de sesión con JWT
- Navegación por catálogo de productos con variantes (talla/color)
- Carrito de compra y lista de deseos
- Pagos con Stripe y puntos de fidelización
- Notificaciones por email con SendGrid
- Historial de pedidos y gestión de direcciones

---

## 📁 Estructura del proyecto
```text
dirtycouture/
├── backend/                                 # Proyecto del backend construido con Ktor (Kotlin)
│   ├── src/main/kotlin/com/dirtycouture/
│   │   ├── Main.kt                          # Punto de entrada principal del servidor Ktor
│   │   ├── config/                          # Configuraciones de la aplicación
│   │   │   ├── AppConfig.kt                 # Configuraciones generales (puerto, claves, etc.)
│   │   │   ├── CorsConfig.kt                # Configuración de CORS (quién puede acceder al backend)
│   │   │   ├── SecurityConfig.kt            # Configuración de autenticación y JWT
│   │   │   └── RoutingConfig.kt             # Registro centralizado de las rutas
│   │   ├── routes/                          # Rutas HTTP que definen los endpoints REST
│   │   │   ├── AuthRoutes.kt                # Rutas de login, registro, etc.
│   │   │   ├── ProductRoutes.kt             # Rutas de productos (listar, detalles, etc.)
│   │   │   ├── CartRoutes.kt                # Rutas para añadir, quitar o ver el carrito
│   │   │   ├── OrderRoutes.kt               # Rutas para crear o consultar pedidos
│   │   │   ├── PaymentRoutes.kt             # Rutas para iniciar o recibir pagos
│   │   │   └── NotificationRoutes.kt        # Rutas relacionadas con notificaciones por email
│   │   ├── controllers/                     # Controladores: manejan las peticiones HTTP
│   │   │   ├── AuthController.kt
│   │   │   ├── ProductController.kt
│   │   │   ├── CartController.kt
│   │   │   ├── OrderController.kt
│   │   │   ├── PaymentController.kt
│   │   │   └── NotificationController.kt
│   │   ├── services/                        # Lógica de negocio (validaciones, reglas, etc.)
│   │   │   ├── AuthService.kt
│   │   │   ├── ProductService.kt
│   │   │   ├── CartService.kt
│   │   │   ├── OrderService.kt
│   │   │   ├── PaymentService.kt
│   │   │   └── NotificationService.kt
│   │   ├── repositories/                    # Acceso a base de datos (leer, guardar, etc.)
│   │   │   ├── UserRepository.kt
│   │   │   ├── ProductRepository.kt
│   │   │   ├── CartRepository.kt
│   │   │   ├── OrderRepository.kt
│   │   │   ├── PaymentRepository.kt
│   │   │   └── WishlistRepository.kt
│   │   ├── models/                          # Modelos que representan entidades de la base de datos
│   │   │   ├── User.kt
│   │   │   ├── Product.kt
│   │   │   ├── ProductVariant.kt
│   │   │   ├── CartItem.kt
│   │   │   ├── Order.kt
│   │   │   ├── OrderItem.kt
│   │   │   ├── Payment.kt
│   │   │   ├── WishlistItem.kt
│   │   │   └── Points.kt
│   │   ├── dto/                             # Objetos para enviar/recibir datos del frontend
│   │   │   ├── RegisterRequest.kt
│   │   │   ├── LoginRequest.kt
│   │   │   ├── LoginResponse.kt
│   │   │   ├── ProductResponse.kt
│   │   │   ├── AddToCartRequest.kt
│   │   │   ├── OrderRequest.kt
│   │   │   ├── OrderResponse.kt
│   │   │   ├── PaymentRequest.kt
│   │   │   └── WishlistSubscribeRequest.kt
│   │   ├── database/                        # Inicialización y configuración de la base de datos
│   │   │   ├── DatabaseFactory.kt           # Configura la conexión a la BD
│   │   │   └── Migrations.kt                # Script para crear/modificar las tablas
│   │   ├── utils/                           # Funciones auxiliares reutilizables
│   │   │   ├── PasswordHasher.kt            # Hasheo y verificación de contraseñas
│   │   │   ├── JwtProvider.kt               # Generación y validación de tokens JWT
│   │   │   └── EmailSender.kt               # Envío de emails vía SendGrid
│   │   └── di/                              # Configuración de inyección de dependencias (Koin)
│   │       └── AppModule.kt
│   ├── resources/application.conf           # Configuración general del servidor (puerto, claves, etc.)
│   ├── Dockerfile                           # Instrucciones para crear la imagen Docker del backend
│   ├── build.gradle.kts                     # Script de construcción del backend con Gradle
│   └── settings.gradle.kts
│
├── frontend/                                # Proyecto frontend con Vue.js
│   ├── src/
│   │   ├── components/                      # Componentes reutilizables (Botones, Tarjetas, etc.)
│   │   ├── pages/                           # Páginas completas (Home, Login, Checkout...)
│   │   ├── router/                          # Rutas de navegación con Vue Router
│   │   ├── store/                           # Estado global con Pinia (carrito, usuario, etc.)
│   │   ├── services/                        # Comunicación con el backend mediante Axios
│   │   └── assets/                          # Imágenes, fuentes y estilos globales
│   ├── public/                              # Archivos estáticos públicos (favicon, index.html...)
│   ├── vite.config.ts                       # Configuración de Vite (dev server, alias, plugins...)
│   ├── Dockerfile                           # Docker para construir y servir el frontend
│   ├── package.json                         # Dependencias del frontend y scripts NPM
│   └── .env                                 # Variables de entorno del frontend
│
├── db/                                      # Scripts SQL para inicializar o poblar la base de datos
│   ├── init.sql
│   └── seed.sql
│
├── docker/                                  # Archivos compartidos de Docker (como nginx, volúmenes...)
│   └── nginx.conf                           # Configuración del servidor reverse proxy (opcional)
│
├── .github/workflows/                       # CI/CD automatizado con GitHub Actions
│   ├── test-backend.yml                     # Ejecuta tests y lint para el backend (Ktor)
│   ├── test-frontend.yml                    # Ejecuta tests unitarios y de integración en el frontend (Vue)
│   ├── deploy-backend.yml                   # Compila y despliega el backend automáticamente
│   ├── deploy-frontend.yml                  # Compila y despliega el frontend automáticamente
│   └── fullstack-deploy.yml                 # Pipeline combinado para todo el proyecto
│
├── .env                                     # Variables de entorno comunes (puertos, claves...)
├── .gitignore                               # Archivos que no se deben subir al repositorio
└── README.md                                # Documentación del proyecto (instalación, uso, etc.)
```
---

## 🧰 Tecnologías usadas

| Parte          | Tecnología               |
|----------------|--------------------------|
| Frontend       | Vue 3, Vite, Pinia, Axios|
| Backend        | Ktor, Kotlin, jOOQ       |
| Base de datos  | Supabase (PostgreSQL)    |
| Auth           | JWT + Bcrypt             |
| Pagos          | Stripe                   |
| Emails         | SendGrid                 |
| Contenedores   | Docker                   |
| CI/CD          | GitHub Actions, Render   |

---

## ⚙️ Instalación local

1. Clona el proyecto

```bash
git clone https://github.com/alonsogonzalo/DirtyCouture.git
cd dirtycouture
```

2. Configura tus variables de entorno  
Copia el archivo `.env.example` a `.env` y completa tus claves (Stripe, SendGrid, etc.)

3. Levanta el sistema con Docker

**Crear imagen Docker**
```bash
docker build -t nombre_imagen
```

**Listar imágenes**
```bash
docker images
```

**Ejecutar imagen**
```bash
docker run -it nombre_imagen
```

4. Accede a:
- `http://` → Frontend
- `http://` → Backend (API REST)

---

## 🧪 Pruebas

Los tests se ejecutan automáticamente con GitHub Actions.  
Manual:

- Backend (JUnit):

```bash
cd backend
./gradlew test
```

- Frontend (Vitest):

```bash
cd frontend
npm run test
```

---

## 🚀 Despliegue automático en Render

Este proyecto está configurado para desplegar automáticamente la rama `release` en _Render_ usando un **Deploy Hook**.

El workflow `ci-release.yml` se ejecuta cada vez que se hace push a la rama `release`. Si las pruebas pasan correctamente, se lanza un despliegue automático a Render.

---

## 👥 Equipo de desarrollo

- Diego Cisneros Morales
- Diego Alcoba Arias  
- Gonzalo Alonso Olaiz  

---

