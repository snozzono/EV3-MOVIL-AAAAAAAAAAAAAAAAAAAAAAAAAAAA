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