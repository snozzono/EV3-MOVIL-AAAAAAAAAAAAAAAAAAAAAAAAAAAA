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

## Preguntas de Doble Check

- ¿El `storeBaseUrl` y credenciales de Xano están configurados en `BuildConfig` o `ApiConfig` correctamente para el entorno del profe?
- ¿`TokenManager` limpia sesión en logout y redirige por rol sin estados inconsistentes?
- ¿`CartManager` garantiza `cart_id` (crea si falta) y resiste reinstalaciones?
- ¿`ImageUrlResolver` cubre cases sin `url` y con `path` relativo en todas las vistas?
- ¿Validaciones de formularios (precios, stock, email) impiden datos inválidos?
- ¿Se manejan estados `cargando/vacío/error` con feedback claro y reintento donde aplica?
- ¿Los endpoints de Xano para CRUD producto/usuario y órdenes están alineados con los modelos actuales?
- ¿Hay límites de tamaño/formatos de imagen definidos y comunicados?
- ¿Se necesitan seeds de datos (≥10 productos) para una demo fluida?

## Exportar a .docx / .pdf

- Puedes abrir este `.md` en VSCode y exportar a `.docx` con extensiones tipo `Markdown PDF` o usar `pandoc`.
- Alternativa rápida: copiar a Google Docs y descargar `.docx`/`.pdf`.

## Cierre

Este proyecto cumple el “Proyecto Final” y está listo para demo. Con las mejoras propuestas, tienes una ruta clara para llevarlo a nivel producción.