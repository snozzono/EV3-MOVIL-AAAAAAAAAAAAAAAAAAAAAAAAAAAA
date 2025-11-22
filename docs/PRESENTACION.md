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