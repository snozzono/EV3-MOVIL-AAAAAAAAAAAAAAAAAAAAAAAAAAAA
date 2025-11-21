# Cuentas demo (User y Admin)

Para facilitar las pruebas, se han definido credenciales demo en `BuildConfig`:

- Usuario (role: user):
  - Email: `user@demo.local`
  - Password: `User123!`
- Admin (role: admin):
  - Email: `admin@demo.local`
  - Password: `Admin123!`

Estas claves están disponibles en tiempo de ejecución como:

- `BuildConfig.DEMO_USER_EMAIL`, `BuildConfig.DEMO_USER_PASSWORD`
- `BuildConfig.DEMO_ADMIN_EMAIL`, `BuildConfig.DEMO_ADMIN_PASSWORD`

## Importante

La app no incluye actualmente un endpoint de registro (`auth/signup`). Para poder iniciar sesión con estas cuentas:

1. Crea los usuarios en tu instancia de Xano (tabla `users`) o habilita un endpoint de registro (`POST /auth/signup`).
2. Asegura que el campo `role` del usuario sea `user` para la cuenta de usuario y `admin` para la cuenta de administrador.

### Opción A: Crear usuarios desde el panel de Xano

- Ve a la tabla `users` y añade manualmente:
  - `name`: "Demo User" / "Demo Admin"
  - `email`: `user@demo.local` / `admin@demo.local`
  - `password`: establece una contraseña (en claro si Xano la hashea en el endpoint de auth, ajusta tu flujo)
  - `role`: `user` / `admin`

### Opción B: Usar un endpoint `auth/signup` (si existe)

Si tu backend Xano dispone de `POST ${XANO_AUTH_BASE}/auth/signup` que acepta `{ name, email, password, role }`, puedes crear las cuentas por API. Ejemplo genérico:

```sh
curl -X POST "${XANO_AUTH_BASE}/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{"name":"Demo User","email":"user@demo.local","password":"User123!","role":"user"}'

curl -X POST "${XANO_AUTH_BASE}/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{"name":"Demo Admin","email":"admin@demo.local","password":"Admin123!","role":"admin"}'
```

> Nota: Ajusta las rutas y payload según tus endpoints reales. Si tu registro no permite "role" directamente, crea el usuario y luego actualiza su rol vía un endpoint admin.

## Uso en la app

- En la pantalla de login, introduce el email y contraseña correspondientes. Tras el login, la app llamará a `GET /auth/me` y navegará según el `role` del usuario.

## Personalización

- Puedes cambiar estas credenciales editando `xano-store-kotlin-main/app/build.gradle.kts` y recompilando la app.