# Don Pepe — Guía de Presentación Técnica

Este documento te sirve para presentar el proyecto, estudiar su arquitectura y entender los datos y flujos clave. Incluye árbol de carpetas, estructura de datos, capas, ventajas y mejoras propuestas, además de un guion de demo y preguntas de doble verificación.

## Árbol de Carpetas (--tree)

Proyecto principal (extracto):

```
xano-store-kotlin-main/
├── README.md
├── docs/
│   ├── DEMO_ACCOUNTS.md
│   ├── ENDPOINTS.md
│   ├── PRESENTACION.md   ← este documento
│   ├── PRESENTACION.txt
│   ├── ProyectoFinal-AppAndroid-Kotlin-LayoutXML.pdf
│   └── images/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/miapp/xanostorekotlin/
│           │   ├── api/
│           │   │   ├── RetrofitClient.kt
│           │   │   ├── TokenManager.kt
│           │   │   ├── CartManager.kt
│           │   │   ├── ProductService.kt
│           │   │   ├── CartService.kt
│           │   │   ├── OrderService.kt
│           │   │   ├── UserService.kt
│           │   │   ├── CreateUserRequest.kt / UpdateUserRequest.kt
│           │   │   └── UpdateProductRequest.kt
│           │   ├── model/
│           │   │   ├── Product.kt
│           │   │   ├── ProductImage.kt (incluye ImageMeta)
│           │   │   ├── Cart.kt (incluye CartItem)
│           │   │   ├── Order.kt (incluye OrderItem, Shipping)
│           │   │   ├── User.kt
│           │   │   ├── LoginRequest.kt / AuthResponse.kt
│           │   │   └── CreateProductRequest.kt / CreateProductResponse.kt
│           │   ├── ui/
│           │   │   ├── HomeActivity.kt (Admin)
│           │   │   ├── HomeClientActivity.kt (Cliente)
│           │   │   ├── ProductDetailActivity.kt
│           │   │   ├── adapter/
│           │   │   │   ├── ProductAdapter.kt
│           │   │   │   ├── ProductAdminAdapter.kt
│           │   │   │   ├── CartAdapter.kt
│           │   │   │   └── OrderAdapter.kt
│           │   │   ├── fragments/
│           │   │   │   ├── ProductsFragment.kt
│           │   │   │   ├── CartFragment.kt
│           │   │   │   ├── ProfileFragment.kt
│           │   │   │   ├── AddProductFragment.kt
│           │   │   │   ├── ProductsAdminFragment.kt
│           │   │   │   ├── UsersFragment.kt
│           │   │   │   └── PaymentDialogFragment.kt
│           │   └── util/
│           │       └── ImageUrlResolver.kt
│           └── res/ (layouts, drawables, strings, etc.)
├── build.gradle.kts / gradle.properties / settings.gradle.kts / gradle/
└── XANO Tienda.postman_collection.json
```

## Carpetas y Propósito

- `app/`: módulo principal de la app Android.
  - `build.gradle.kts`: dependencias del módulo (Retrofit, Coil, etc.).
  - `release/`: artefactos o configuraciones de publicación.
  - `src/main/`: código y recursos de producción.
    - `AndroidManifest.xml`: declaración de actividades, permisos y configuración.
    - `java/com/miapp/xanostorekotlin/`: código Kotlin por paquetes.
      - `api/`: clientes y gestores de red y sesión (`RetrofitClient`, `TokenManager`, `CartManager`, `*Service` y requests).
      - `model/`: modelos de datos (`Product`, `User`, `Cart`, `Order`, `ProductImage`, etc.).
      - `ui/`: Activities, Fragments y Adapters (capa de presentación).
      - `util/`: utilidades generales (ej.: `ImageUrlResolver`).
    - `res/`: recursos Android (layouts, drawables, strings, temas, menús, navegación, etc.).
- `docs/`: documentación del proyecto (guías, endpoints, presentación, cuentas demo, imágenes).
- `preview/`: vista previa HTML para mostrar contenido relacionado con la app.
- `gradle/`, `gradlew`, `gradlew.bat`: wrapper y configuración de Gradle.
- `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`: configuración global del proyecto.
- `.idea/`: metadatos del IDE.
- `README.md`: descripción general del proyecto.
- `XANO Tienda.postman_collection.json`: colección Postman con endpoints de Xano.

## Estructura de Datos (Modelos)

- `Product`
  - Campos: `id:Int`, `name:String`, `description:String?`, `price:Int?`, `stock:Int`, `brand:String`, `category:String`, `images:List<ProductImage>?`, `enabled:Boolean?`.
  - Uso: listados, detalle y CRUD en Admin.

- `ProductImage` y `ImageMeta`
  - Campos: `path:String`, `name:String?`, `type:String?`, `size:Int?`, `mime:String?`, `access:String?`, `url:String?`, `meta:ImageMeta?` con `width:Int?`, `height:Int?`.
  - Uso: galería de productos y subida múltiple.

- `Cart` y `CartItem`
  - `Cart`: `id`, `createdAt`, `userId`.
  - `CartItem`: `id`, `createdAt`, `quantity`, `cartId`, `productId`.
  - Uso: edición de carrito (agregar, incrementar, decrementar, eliminar).

- `Order`, `OrderItem`, `Shipping`
  - `Order`: `id`, `createdAt`, `total:Double?`, `status:String?`, `userId`.
  - `OrderItem`: `id`, `createdAt`, `quantity`, `price:Double?`, `orderId`, `productId`.
  - `Shipping`: `id`, `createdAt`, `address:String`, `shippingDate:String?`, `status:String?`, `orderId`.
  - Uso: pago simulado, estados y envío.

- `User`
  - `id`, `name`, `email`, `role`, `status:Boolean?`, `shippingAddress:String?`, `phone:String?`, `createdAt`.
  - Uso: listado/búsqueda/edición/bloqueo.

- Requests/Responses relevantes
  - `LoginRequest`, `AuthResponse`
  - `CreateProductRequest`, `UpdateProductRequest`, `CreateProductResponse`
  - `CreateUserRequest`, `UpdateUserRequest`
  - `CartService` lleva `CreateCartRequest`, `UpdateCartRequest`, `AddCartItemRequest`, `UpdateCartItemRequest`
  - `OrderService` lleva `CreateOrderRequest`, `UpdateOrderRequest`, `CreateOrderItemRequest`, `UpdateOrderItemRequest`, `CreateShippingRequest`, `UpdateShippingRequest`

## Capas y Contextos

- UI (Activities/Fragments/Adapters)
  - `HomeActivity` (Admin) y `HomeClientActivity` (Cliente): navegación por rol.
  - Fragments: `ProductsFragment`, `CartFragment`, `ProfileFragment`, `AddProductFragment`, `ProductsAdminFragment`, `UsersFragment`, `PaymentDialogFragment`.
  - Adapters: `ProductAdapter`, `ProductAdminAdapter`, `CartAdapter`, `OrderAdapter`.

- Estado y sesión
  - `TokenManager`: guarda token, nombre y email en `SharedPreferences` (nota: educativo, no producción).
  - `CartManager`: asegura/recuerda `cart_id` por usuario en `SharedPreferences`.

- Red y servicios
  - `RetrofitClient`: crea instancias de servicios con `OkHttp` e interceptor de autorización.
  - Servicios: `ProductService`, `CartService`, `OrderService`, `UserService`.

- Utilidades
  - `ImageUrlResolver`: resuelve URL absoluta de imagen usando `image.url` o `storeBaseUrl` + `path`.

## Código Clave Explicado (línea por línea y decisiones)

### TokenManager — gestión de sesión con SharedPreferences
- Propósito: centraliza el almacenamiento del token y datos mínimos del usuario (nombre, email) usando `SharedPreferences`. Es una aproximación didáctica y simple para persistencia.
- Uso típico: `TokenManager.saveAuth(token, name, email)` tras login; `TokenManager.getToken()` para el interceptor; `TokenManager.clear()` en logout.
- Decisiones:
  - Se usa `SharedPreferences` por facilidad; en producción conviene `EncryptedSharedPreferences` o `DataStore` para mayor seguridad y robustez.
  - Métodos de lectura retornan tipos simples (`String?`/`Boolean`), evitando depender de un modelo complejo.
- Implicaciones:
  - La sesión está ligada al dispositivo. Al limpiar o reinstalar, se pierde el estado; la app maneja esto reautenticando.
  - El interceptor no inyecta si `getToken()` devuelve `null`.

### RetrofitClient — configuración de red e interceptor de autorización
- Propósito: expone fábricas `createProductService`, `createUserService`, etc., todas basadas en un `OkHttpClient` común.
- Decisiones:
  - `GsonConverterFactory`: simplifica la (de)serialización de JSON sin boilerplate.
  - Interceptor `Authorization`: lee `TokenManager.getToken()` y añade la cabecera `Bearer <token>` cuando existe. Esto evita repetir lógica en cada llamada.
  - Base URLs: se leen de `BuildConfig` (por variante) y se centralizan en `ApiConfig`.
- Detalle del flujo:
  - Cada petición pasa por el interceptor; si hay token, se añade `Authorization`. Si no, la petición se hace anónima.
  - Retrofit mapea interfaces `suspend fun` a llamadas de red en hilos de IO.

### CartManager — garantizar `cart_id` por usuario (suspending y persistencia)
- Contexto: el carrito necesita un `cart_id` asociado al usuario. Si no existe en `SharedPreferences`, se crea uno llamando a Xano.
- Método clave: `ensureCartId(userId: Int): Int`.
  - Lee `cart_id` de `SharedPreferences`. Si existe, retorna inmediatamente.
  - Si no existe: ejecuta en `Dispatchers.IO` una llamada a `CartService.createCart(CreateCartRequest(userId))`.
  - Al recibir el `id` del carrito, lo guarda en `SharedPreferences` y lo devuelve.
- Decisiones y riesgos:
  - `Dispatchers.IO`: evita bloquear el hilo principal; correcto para I/O de red y disco.
  - Idempotencia: llamar varias veces sin `cart_id` puede crear múltiples carritos; se mitiga persistiendo inmediatamente el `id`. Mejora posible: hacer "singleflight" con un candado para evitar paralelismo creando duplicados.
  - Reinstalaciones: al perder `SharedPreferences`, se creará un nuevo `cart_id`. La continuidad queda a criterio del backend.

### ImageUrlResolver — construir URL absoluta robusta
- Contexto: algunos `ProductImage` vienen con `url` absoluta; otros solo traen `path` relativo.
- Lógica:
  - Si `image.url` no es nula/blank, se devuelve tal cual (evita romper si el backend ya dio el absoluto).
  - Si no, se toma `ApiConfig.storeBaseUrl` y se extraen `scheme` y `host`; se construye `scheme://host + image.path` (sin duplicar `/`).
  - `firstImageUrl(images)`: recorre la lista y retorna el primer URL resolviendo con la misma estrategia.
- Decisiones:
  - Se prioriza `url` para no rehacer lo que el backend entrega.
  - El uso de `scheme`/`host` evita problemas si `storeBaseUrl` tiene rutas (p.ej., `/api`) que no deben incluirse antes del `path` de la imagen.
  - Fallbacks: si la lista está vacía o el `path` está en blanco, devuelve `null` y la UI usa placeholders.

### AddProductFragment — subida paralela de imágenes y creación/edición de productos
- Flujo al crear producto:
  - Selección de imágenes: el fragment guarda URIs locales seleccionadas.
  - Subida paralela: usa `lifecycleScope.launch` + `async(Dispatchers.IO)` por cada imagen para subir en paralelo; luego `awaitAll()` espera todas.
  - Mapeo de respuesta: las subidas retornan objetos con `path`/`url`; se construye `List<ProductImage>` para el producto.
  - Creación: con el payload listo (`CreateProductRequest`), llama a `ProductService.createProduct`.
  - UI: muestra estado de carga, deshabilita controles y maneja errores con `Toast/Snackbar`.
- Edición de producto:
  - Carga producto existente; permite reemplazar/agregar imágenes; llama a `updateProduct` con `UpdateProductRequest`.
- Decisiones:
  - Paralelizar subidas disminuye el tiempo total. `async + awaitAll` aprovecha I/O concurrente.
  - `Dispatchers.IO` para red/archivo; correcto para evitar bloquear UI.
  - Manejo de errores: se captura excepción en cada `async`; si alguna falla, se notifica y se decide si continuar parcial o abortar.
  - Validaciones: se verifica `name`/`price` básicos; se recomienda añadir validaciones de rango y formato.

### MainActivity — login, obtención de perfil y navegación por rol
- Flujo:
  - Si hay sesión (`TokenManager.getToken()`/datos guardados), se navega directamente según rol.
  - Validación: se revisa que email y contraseña no estén vacíos; se muestra `progress`.
  - Autenticación: se llama a `AuthService.login(LoginRequest)`. Al recibir token, se guarda temporalmente y se usa para pedir el perfil.
  - Perfil: con el token, se llama a `UserService.getProfile()` (o equivalente) para obtener nombre, email y rol; se guarda en `TokenManager.saveAuth(token, name, email)`.
  - Navegación: redirige a `HomeActivity` (Admin) o `HomeClientActivity` (Cliente) según `role`.
- Decisiones:
  - Dos pasos (token → perfil) permiten rellenar `TokenManager` con datos humanos (nombre/email) y no solo el token.
  - Se usa `lifecycleScope` para respetar el ciclo de vida y evitar fugas.
  - En error, se limpia sesión y vuelve a login.

### Services Retrofit — contratos con Xano (Product, User, Cart, Order)
- Patrón:
  - Interfaces `suspend fun` con anotaciones HTTP (`@GET`, `@POST`, `@PATCH`, `@DELETE`).
  - Rutas concisas: `product`, `user`, `cart`, `order`, `shipping`; parámetros por `@Path` y `@Query` cuando aplica.
  - Requests/Responses tipados: clases `data` en el mismo paquete cuando el contrato es pequeño (p.ej., `AddCartItemRequest`, `UpdateCartItemRequest`).
- Decisiones:
  - Mantener requests junto al servicio mejora la trazabilidad del contrato.
  - Tipos opcionales (`?`) reflejan la realidad del backend; se gestionan en parseo sin romper la UI.

### Adapters — DiffUtil y carga de imágenes
- `ProductAdapter`/`ProductAdminAdapter`/`CartAdapter` usan `ListAdapter` con `DiffUtil.ItemCallback`:
  - Ventaja: actualizaciones eficientes por ítem sin refrescar toda la lista.
  - Vinculación de imagen: usan `ImageUrlResolver.firstImageUrl(product.images)` y Coil con `placeholder`/`error`.
- Decisiones:
  - `DiffUtil` evita parpadeos y mejora rendimiento.
  - Placeholders coherentes mejoran UX cuando la imagen tarda.

### ProfileFragment — edición de datos y logout
- Carga: obtiene nombre, email y otros datos desde `TokenManager` para mostrar en los campos.
- Actualización: construye `UpdateUserRequest` y llama a `UserService.updateUser`. En éxito, refleja cambios y muestra confirmación.
- Logout: `TokenManager.clear()` y navegación a `MainActivity`.
- Decisiones:
  - Sencillez en UI y flujo; pendiente validar formato email/teléfono y dirección.

### Errores y estados — patrón actual y mejoras
- Actual: se usan `progress` simples, `Toast/Snackbar` para errores y logs en capas de red.
- Mejora propuesta: un `sealed class UiState { Loading, Data, Empty, Error }` por pantalla y `StateFlow` en `ViewModel`, para manejo uniforme y reintentos visibles.

### BuildConfig y ApiConfig — centralización de configuración
- `BuildConfig`: variables por variante (`debug/release`) definidas en `build.gradle.kts` con `buildConfigField`.
- `ApiConfig`: lee `BuildConfig.XANO_STORE_BASE` y `XANO_AUTH_BASE` y expone `storeBaseUrl`/`authBaseUrl`.
- Decisiones:
  - Separar por entorno evita tocar código al cambiar URLs.
  - Consumir desde utilidades (p.ej., `ImageUrlResolver`) reduce duplicación y errores.

## ¿Por qué funciona bien?

- Separación clara de responsabilidades: UI, datos (model), red (services), estado (managers).
- Modelos consistentes con `GSON` y anotaciones `@SerializedName`, reduciendo fricciones de parseo.
- `ViewBinding` simplifica acceso a vistas y reduce NPEs.
- Uso de `RecyclerView` + `ListAdapter` con `DiffUtil` en listas clave (productos/usuarios/órdenes).
- Flujo didáctico y directo: llamadas `suspend` con `lifecycleScope`, estados básicos de carga/vacío/error.
- `ImageUrlResolver` evita roturas si `url` no viene y solo existe `path`.

## ¿Qué puede mejorar con XYZ?

- Arquitectura: adoptar `MVVM + Repository` y `UseCases` para separar UI/negocio/red.
- Estado: usar `StateFlow`/`LiveData` para estados (cargando, datos, error) y unificar manejo de fallos.
- Almacenamiento: `Room` para carrito y productos (caché/persistencia), soporte offline y reconexión.
- Red: interceptores de reintento/backoff, parseo uniforme de errores (sealed `Result`).
- DI: incorporar `Hilt` para inyección de dependencias y testabilidad.
- Configuración segura: `EncryptedSharedPreferences` o `DataStore` para token, y `BuildConfig` para URLs.
- Imágenes: compresión previa y carga diferida; transformar a `webp` o `avif`, placeholders y `error` coherentes.
- Listas: paginación (`Paging 3`) si el catálogo crece.
- UI/UX: componentes de Material, `Navigation Component`, transiciones y estados accesibles.
- Testing: tests de unidad para `ImageUrlResolver`, `CartManager`, adapters; instrumentados básicos para flujos.

## Guion de Presentación (profe)

- Introducción rápida: objetivo del proyecto y roles.
- Explicar estructura (`--tree`) y capas (UI, red, modelos, estado).
- Demo Admin: login → CRUD productos con múltiples imágenes → usuarios (bloqueo) → revisar orden → aceptar y rechazar.
- Demo Cliente: login → catálogo → carrito (editar cantidades) → pagar simulado → ver estado → perfil.
- Cerrar con notas técnicas: Retrofit, ViewBinding, RecyclerView, `SharedPreferences` y `ImageUrlResolver`.
- Señalar mejoras que dejarías para producción (MVVM/Hilt/Room, etc.).


## Doble Check — Preguntas y Respuestas

- ¿El `storeBaseUrl` y credenciales de Xano están bien configurados?
  - Respuesta: Sí, se definen en `app/build.gradle.kts` con `buildConfigField` y se consumen en `ApiConfig`. Verifica que `XANO_STORE_BASE` y `XANO_AUTH_BASE` apunten al entorno del profe.
- ¿`TokenManager` limpia sesión y redirige por rol correctamente?
  - Respuesta: Sí. `clear()` borra token y preferencias; logout desde `ProfileFragment`, `HomeActivity` y `HomeClientActivity` redirige a login con navegación por rol.
- ¿`CartManager` asegura o recuerda `cart_id`?
  - Respuesta: Sí. Si falta, crea uno vía API y lo guarda en `SharedPreferences`. Tras reinstalación, se crea un nuevo `cart_id`; la continuidad depende del backend.
- ¿`ImageUrlResolver` cubre casos `url` y `path` relativo?
  - Respuesta: Sí. Usa `image.url` cuando existe y construye `scheme://host + path` desde `storeBaseUrl` cuando solo hay `path`. Se usa en adapters, detalle y carrito.
- ¿Validaciones de formularios son suficientes?
  - Respuesta: Son básicas (no vacío y parseo de `price`). Falta validar formato de email y rangos (`price >= 0`, `stock >= 0`) con errores en cada campo.
- ¿Estados `cargando/vacío/error` tienen feedback y reintento?
  - Respuesta: Hay `progress` y `Toast/Snackbar` en pantallas clave; falta un patrón unificado de estados y botones de reintento visibles.
- ¿Endpoints de Xano alinean con modelos?
  - Respuesta: Sí, las rutas `product/user/cart/order/shipping` coinciden con los modelos. Conviene confirmar esquemas exactos con backend.
- ¿Límites de tamaño/formatos de imagen están definidos?
  - Respuesta: No están validados en la app; Coil muestra errores de carga. Debe confirmarse tamaño máximo y MIME aceptado en backend.
- ¿Seeds de datos son suficientes para la demo?
  - Respuesta: Recomendado ≥10 productos y varios usuarios para fluidez; no se fuerzan desde código.

## Definiciones Clave (StateFlow, Room, Paging 3, BuildConfig)

- StateFlow: flujo “caliente” de Kotlin para exponer estado en `ViewModel` con un valor actual; la vista colecta respetando el ciclo de vida.
- Room: capa de persistencia sobre SQLite con `@Entity`, `@Dao`, `@Database` y validación en compile-time; soporta `Flow` y se integra con Paging.
- Paging 3: librería Jetpack para paginación eficiente; produce `Flow<PagingData<T>>` desde `Pager` y se integra con Retrofit y Room.
- BuildConfig: clase generada por Gradle por variante (debug/release) con constantes definidas por `buildConfigField`; se usa para URLs base y flags.

## Puntos a confirmar en backend

- Grupos y rutas de API: que `XANO_STORE_BASE` y `XANO_AUTH_BASE` correspondan a los grupos correctos y que las rutas (`product`, `user`, `cart_item`, `order_item`, `shipping`) existan tal cual.
- Esquemas: tipos/opcionales de `CreateProductResponse`, `OrderItem`, `Shipping` y requests de CRUD.
- Carritos por usuario: política de deduplicación por `user_id` o múltiples carritos.
- Restricciones de imagen: tamaño máximo y tipos MIME aceptados para validar antes de subir.

## Recursos Android (carpeta `res`)

- Estructura habitual:
  - `layout/`: vistas XML. Convención: `activity_*`, `fragment_*`, `item_*`. Ej.: `fragment_profile.xml` genera `FragmentProfileBinding`.
  - `values/`: `strings.xml`, `colors.xml`, `dimens.xml`, `styles.xml`, `themes.xml`. Usar `@string/...`, `@color/...` en XML y `getString(R.string...)` en Kotlin.
  - `drawable/`: imágenes y vectores (`ic_*` para íconos, `bg_*` para fondos). Preferir `VectorDrawable` sobre PNG.
  - `mipmap/`: íconos de app (launcher). Mantener `ic_launcher` aquí.
  - `menu/`: menús de Toolbar/BottomNavigation. Ej.: `menu_client.xml`, `menu_admin.xml`.
  - `navigation/`: grafos de navegación (`nav_graph.xml`).
  - Otros: `font/` (tipografías), `anim/` y `animator/` (animaciones), `xml/` (config), `raw/` (archivos brutos), `transition/`.

- Acceso a recursos:
  - En XML: `@string/...`, `@color/...`, `@drawable/...`, `@style/...`.
  - En código: `R.layout.*`, `R.id.*`, `R.string.*`, `R.color.*`, `R.drawable.*`.
  - ViewBinding: cada `layout` genera una clase de binding que evita `findViewById` y es type-safe.

- Qualifiers (variantes):
  - Idioma: `values-es/strings.xml`.
  - Tema: `values-night/` para Dark Mode.
  - Orientación: `layout-land/`.
  - Densidad: `drawable-xxhdpi/`, `-xhdpi`, etc.

- Buenas prácticas:
  - No hardcodear textos/colores; centralizar en `values/`.
  - Nombrar en `snake_case` y con prefijos semánticos (`btn_`, `tv_`, `ic_`, `bg_`).
  - Reutilizar estilos/temas; usar atributos de tema (`?attr/colorPrimary`).
  - Añadir `contentDescription` en imágenes con significado.
  - Usar vectores cuando sea posible; colocar íconos de app en `mipmap/`.
  - Separar `item_*` (RecyclerView) de `fragment_*`/`activity_*`.

## Exportar a .docx / .pdf

- Puedes abrir este `.md` en VSCode y exportar a `.docx` con extensiones tipo `Markdown PDF` o usar `pandoc`.
- Alternativa rápida: copiar a Google Docs y descargar `.docx`/`.pdf`.

## Cierre

Este proyecto cumple el “Proyecto Final” y está listo para demo. Con las mejoras propuestas, tienes una ruta clara para llevarlo a nivel producción.