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

### Estado al finalizar — FASE 1 COMPLETADA ✅
- Build compila limpio (`assembleDebug` OK)
- Validado en dispositivo físico Samsung A17 (2026-05-17):
  - Login con email/contraseña ✅
  - Login con Google Sign-In ✅
  - Registro de nuevo usuario ✅
  - Flujo de navegación correcto ✅

---

## Sesión 2 — 2026-05-17

### Objetivo
Implementar la Fase 2 del roadmap: Inventario de frutas con Firestore.

### Cambios en Gradle

**`app/gradle/libs.versions.toml`**
- Añadidas versiones: `recyclerview = "1.3.2"`, `coroutines = "1.7.3"`
- Añadidas librerías: `androidx-recyclerview`, `firebase-firestore-ktx`, `kotlinx-coroutines-android`, `kotlinx-coroutines-play-services`

**`app/app/build.gradle.kts`**
- Dependencias de RecyclerView, Firestore y corrutinas añadidas

### Firestore — Modelo de datos
- Colección: `users/{uid}/inventory/{itemId}`
- Campos: `name`, `quantity`, `expiryDate`, `label`, `confidence`, `addedAt`

### Archivos nuevos
- `FruitItem.kt` — Data class con `@DocumentId`, mapeada a Firestore
- `InventoryRepository.kt` — CRUD + `callbackFlow` para lista en tiempo real (ordenada por `expiryDate`)
- `InventoryActivity.kt` — RecyclerView + swipe-to-delete + Snackbar Deshacer + FAB
- `FruitItemAdapter.kt` — `ListAdapter` con `DiffUtil`, chip fresh/rotten con colores
- `AddFruitDialogFragment.kt` — `BottomSheetDialogFragment` para añadir/editar; `MaterialDatePicker` para fecha
- `activity_inventory.xml` — CoordinatorLayout + MaterialToolbar + RecyclerView + FAB
- `item_fruit.xml` — Fila card con nombre, fecha, cantidad y chip de estado
- `fragment_add_fruit.xml` — Bottom Sheet con campos nombre, cantidad, fecha

### Archivos modificados
- `ResultActivity.kt` + `activity_result.xml` — Botón "Añadir al inventario" que abre `AddFruitDialogFragment` con `label`/`confidence` pre-rellenados
- `MainActivity.kt` + `activity_main.xml` — Botón "Ver inventario" que lanza `InventoryActivity`
- `AndroidManifest.xml` — `InventoryActivity` registrada
- `strings.xml` — 18 strings nuevos del inventario
- `colors.xml` — `fruit_fresh_bg` (#C8E6C9) y `fruit_rotten_bg` (#FFCCBC) para chips

### Estado al finalizar
- `BUILD SUCCESSFUL` — sin errores ni warnings
- Pendiente: probar flujo completo en dispositivo físico
- Pendiente: configurar reglas de seguridad en Firestore console (`users/{uid}/inventory` → solo el propio usuario)

---
