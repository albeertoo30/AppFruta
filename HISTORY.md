# Historial de sesiones de desarrollo

---

## Sesión 1 — 2026-05-16

### Objetivo
Implementar la Fase 1 del roadmap: autenticación con Firebase (email/contraseña + Google Sign-In).

### Cambios en Gradle

**`app/gradle/libs.versions.toml`**
- Añadidas versiones: `firebaseBom = "33.1.0"`, `googleServices = "4.4.2"`, `credentials = "1.3.0"`, `googleid = "1.1.1"`
- Añadidas librerías: `firebase-bom`, `firebase-auth-ktx`, `androidx-credentials`, `androidx-credentials-play-services-auth`, `google-id`
- Añadido plugin: `google-services`

**`app/app/build.gradle.kts`**
- Plugin `google-services` activado
- Dependencias Firebase (via BOM) + Credential Manager añadidas

**`app/build.gradle.kts` (raíz)**
- Plugin `google-services` declarado con `apply false`

### Firebase (configuración manual guiada)
- Proyecto Firebase creado: `FrutaIA`
- App Android registrada con package `com.appfruta`
- Authentication activado: Email/contraseña + Google
- SHA-1 debug añadido: `98:C6:0C:87:40:25:BE:AF:A9:3B:AE:6D:DA:13:4F:D2:FA:E5:FE:D2`
- `google-services.json` colocado en `app/app/`
- Build verificado: `BUILD SUCCESSFUL`

### Archivos nuevos
- `app/app/src/main/java/com/appfruta/LoginActivity.kt` — Login email + Google Sign-In via Credential Manager
- `app/app/src/main/java/com/appfruta/RegisterActivity.kt` — Registro con validación de campos
- `app/app/src/main/res/layout/activity_login.xml` — Pantalla de login (Material Design 3)
- `app/app/src/main/res/layout/activity_register.xml` — Pantalla de registro (Material Design 3)

### Archivos modificados
- `AndroidManifest.xml` — `LoginActivity` es el nuevo LAUNCHER; `MainActivity` y `ResultActivity` pasan a `exported=false`
- `strings.xml` — 14 strings nuevos de autenticación
- `MainActivity.kt` — `onStart()` redirige a login si no hay sesión activa; botón "Cerrar sesión"
- `activity_main.xml` — Botón `btnLogout` añadido dentro de la card

### Estado al finalizar
- Build compila limpio (`assembleDebug` OK)
- Pendiente: probar flujo completo en emulador/dispositivo
- Pendiente: verificar Google Sign-In en dispositivo real (requiere SHA-1 en Firebase y app instalada via Play o debug)

### Próximos pasos (Fase 1 restante)
- [ ] Probar LoginActivity y RegisterActivity en emulador
- [ ] Verificar flujo Google Sign-In en dispositivo físico
- [ ] Añadir perfil de usuario básico (nombre, foto)
- [ ] Pulir UX: mensajes de error más específicos de Firebase, estado de carga

---
