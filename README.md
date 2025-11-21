# Don Pepe — Xano Store Kotlin

Aplicación Android (Kotlin + XML) para una tienda conectada a Xano. Incluye autenticación, catálogo, carrito, pedidos y panel de administración.

## Características
- Autenticación con sesiones y perfiles (admin/usuario)
- Catálogo con búsqueda y detalle, carrusel de imágenes
- Carrito de compras y checkout
- Pedidos: listado y detalle
- Panel admin: CRUD de productos y usuarios
- Distinción visual de roles (badge y color) en usuarios
- Switch de estado sincronizado (texto dinámico Activo/Inactivo)
- Icono de app personalizado (carrito morado) y nombre "Don Pepe"

## Requisitos
- Android Studio (Flamingo o superior)
- JDK 17
- Gradle Wrapper incluido
- Dispositivo/emulador Android API 24+

## Configuración
1. Clona el repositorio.
2. Abre `xano-store-kotlin-main` en Android Studio.
3. Ajusta endpoints/keys en servicios o `BuildConfig` si cambian.
4. Sincroniza Gradle.

## Construcción y ejecución
- Debug desde Android Studio: botón Run sobre `app`.
- Debug APK: `Build > Build Bundle(s) / APK(s) > Build APK(s)`.

## Exportar APK firmado
1. `Build > Generate Signed Bundle / APK...`.
2. Selecciona `APK`.
3. Crea/elige keystore (ruta, alias, password).
4. Marca `v1` y `v2` (según compatibilidad).
5. El APK queda en `app/release/`.

## Personalización
- Nombre de app: `app/src/main/res/values/strings.xml` (`app_name = Don Pepe`).
- Icono launcher: `app/src/main/res/drawable/ic_don_pepe.xml` (carrito morado `#7E57C2`).
- Manifiesto: `app/src/main/AndroidManifest.xml` (`android:icon="@drawable/ic_don_pepe"`).

## Estructura
```
xano-store-kotlin-main/
├── app/
│   ├── src/main/
│   │   ├── java/com/miapp/xanostorekotlin
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── release/
├── docs/
├── gradle/
├── settings.gradle.kts
└── README.md
```

## Notas Técnicas
- `ImageUrlResolver` gestiona URLs de imágenes para Coil.
- `SwitchCompat` en usuarios evita listeners al setear estado y actualiza texto.
- Badge de rol: admin (chip azul claro), user (chip gris claro).

## Troubleshooting
- Si Gradle falla: `Build > Clean Project` y `Rebuild`.
- Verifica permisos de Internet en `AndroidManifest.xml`.
- Para íconos: `File > Invalidate Caches / Restart`.

## Licencia
Uso educativo/demostrativo.

## Capturas (texto)
Para mejorar accesibilidad y legibilidad en GitHub, se reemplazan las imágenes por descripciones textuales de cada vista y flujo:

- Inicio y navegación
  - Encabezado con branding de "Don Pepe" y acceso a secciones principales.
  - Accesos rápidos a `Catálogo`, `Carrito` y `Perfil`.
  - Controles de sesión visibles: entrar/registrarse o ver estado del usuario.
  - Navegación clara entre pantallas (barra inferior o botones de acción, según dispositivo).

- Catálogo y detalle de producto
  - Listado de productos con imagen, nombre, precio y stock, usando `RecyclerView`.
  - Posibilidad de búsqueda/filtrado (si se habilita en la configuración).
  - Pantalla de detalle con descripción, galería de imágenes y botón `Añadir al carrito`.
  - Indicadores de estado: cargando, vacío y error ante fallos de la API.

- Carrito y proceso de checkout
  - Carrito con productos seleccionados, cantidades editables y subtotal por ítem.
  - Cálculo de total y validación antes de pagar.
  - Botón `Pagar` que crea la orden (pago simulado) y muestra estados: `pendiente`, `aceptado`, `rechazado`, `enviado`.
  - Confirmaciones y mensajes de éxito/error para acciones críticas.

## Configuración de BuildConfig (opcional)
Si necesitas centralizar URLs/constantes, puedes definir campos en `app/build.gradle.kts`:

```kts
defaultConfig {
    buildConfigField("String", "XANO_STORE_BASE", "\"https://<tu-xano>/api:<id_store>\"")
    buildConfigField("String", "XANO_AUTH_BASE",  "\"https://<tu-xano>/api:<id_auth>\"")
    buildConfigField("int", "XANO_TOKEN_TTL_SEC", "86400")
}
```

Luego léelos en tu `ApiConfig` o servicios vía `BuildConfig.XANO_STORE_BASE`, etc.

## Endpoints y ejemplos
- Referencia detallada: `docs/ENDPOINTS.md`.
- Autenticación (ejemplo cURL):

```bash
curl -X POST \
  "$XANO_AUTH_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"usuario@example.com","password":"tu_password"}'
```

- Productos (listado):

```bash
curl -X GET \
  "$XANO_STORE_BASE/product" \
  -H "Authorization: Bearer TU_TOKEN"
```

## Preguntas frecuentes (FAQ)
- No se ven imágenes en GitHub: usa URLs absolutas (ya aplicado) o verifica rutas/branch.
- Error de conexión a Xano: confirma `android.permission.INTERNET` y URLs en `BuildConfig`.
- Fallo de build en Windows (CRLF): habilita autocrlf o re-formatea line endings.
- Icono no actualiza: limpia caché de Android Studio (`Invalidate Caches / Restart`).

## Proyecto Final (resumen)
Este repositorio cumple con el proyecto final propuesto: app e-commerce con roles (Cliente/Admin), sesión con `SharedPreferences`, CRUD de productos con múltiples imágenes, gestión de usuarios, pagos/órdenes (simulados) y flujo de demo completo.

- Objetivo: e-commerce básico con dos roles y flujo de pago simulado.
- Requisitos funcionales: autenticación, catálogo/carrito, CRUD productos, usuarios (bloqueo/desbloqueo), pagos/órdenes (pendiente → aceptar/rechazar → enviado).
- Técnicos: Kotlin + XML, ViewBinding, Retrofit, RecyclerView, subida de múltiples imágenes, ícono personalizado, APK funcional, validaciones y estados.
- Backend recomendado: Xano (alternativas: Supabase/Firebase/REST propio). Documentar inicialización y credenciales de prueba.
- UX/UI mínimos: responsivo, estados (cargando/vacío/error), confirmaciones y feedback claro, controles por rol.
- Flujo demo: Admin (productos/usuarios/pagos) y Cliente (catálogo/carrito/pago simulado/envío/perfil).
- Entregables: repo con README, APK y video demo (5–7 min).
- Evaluación: 100 pts (auth/sesión, cliente, admin productos/usuarios, pagos/órdenes, integración técnica, calidad UI/UX, entrega).

Documento completo (PDF):

- [Proyecto Final — App Android (Kotlin + Layout XML)](docs/ProyectoFinal-AppAndroid-Kotlin-LayoutXML.pdf)