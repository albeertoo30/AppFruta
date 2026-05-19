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

## Sesión 3 — 2026-05-19

### Objetivo
Rediseño completo de la interfaz: sistema de diseño minimalista (blancos y verdes, sin tarjetas flotantes ni sombras).

### Sistema de diseño aplicado
- Fondo directo `fruit_background` (#F6FBF5), sin cards superpuestas
- Botones principales: filled verde pildora (`cornerRadius="28dp"`)
- Botones secundarios: `OutlinedButton` o `TextButton` según jerarquía
- Campos de texto: `OutlinedBox` con `boxCornerRadius=12dp` en las cuatro esquinas
- Espaciado generoso, tipografía con alpha reducido para jerarquía secundaria
- Sin sombras ni bordes decorativos en ningún componente

### Pantallas rediseñadas

**`activity_login.xml`**
- `FrameLayout` raíz + `ScrollView` (fillViewport, sin scrollbars)
- Logo 96dp, título 32sp bold verde, subtítulo alpha=0.55
- Campos email + contraseña: `OutlinedBox`, `boxCornerRadius=12dp`, `endIconMode=password_toggle` en password
- `btnLogin`: filled verde, cornerRadius=28dp, paddingVertical=14dp
- `btnGoogle`: OutlinedButton, strokeColor=#DEDEDE (sutil), icono `ic_google` (vector multicolor)
- `btnGoRegister`: TextButton verde centrado
- `CircularProgressIndicator` superpuesto vía `FrameLayout`

**`activity_register.xml`**
- Misma estructura que login
- Campo `tilName`/`etName` añadido como primero (inputType=textPersonName|textCapWords)
- Cuatro campos en orden: nombre, email, contraseña, confirmar contraseña
- `btnRegister`: filled verde; `btnGoLogin`: TextButton verde

**`activity_main.xml`**
- `LinearLayout` vertical con dos `View` weight para centrado flexible
- Logo 80dp, título 32sp, subtítulo alpha=0.55
- `btnCamera`: filled verde, paddingVertical=20dp, iconSize=22dp — jerarquía principal
- `btnGallery`: OutlinedButton verde, paddingVertical=14dp — captura secundaria
- Espaciador weight=0.4 separa captura de navegación
- `btnInventory`: OutlinedButton verde secundario — navegación
- `btnLogout`: TextButton, alpha=0.45 — utilidad discreta

**`activity_result.xml`**
- `ScrollView` (fillViewport) + `LinearLayout` height=match_parent (habilita weight)
- `imageCard`: 200dp×200dp, `layout_gravity=center_horizontal`, marginTop=40dp, cornerRadius=12dp, sin sombra ni borde
- `ivResultPhoto`: `scaleType=centerInside` (imagen completa, sin recorte)
- Dos `View` weight=1 flanqueando el bloque de resultado para centrado vertical
- Bloque resultado: icono 36dp + `tvResultStatus` 40sp bold centrado en fila horizontal
- `tvResultConfidence`: 15sp, alpha=0.5, centrado
- `tvResultMessage`: 14sp, alpha=0.55, centrado
- `btnAddToInventory`: filled verde (acción principal)
- `btnAnalyzeAgain`: TextButton, alpha=0.6 (acción secundaria)

**`activity_inventory.xml`**
- `AppBarLayout` con `elevation=0dp` (sin línea divisora bajo toolbar)
- `tvEmptyState`: alpha=0.45, textSize=15sp (más discreto)

**`item_fruit.xml`**
- `MaterialCardView`: elevation=0dp, strokeWidth=0dp, cornerRadius=8dp, background=fruit_surface
- Padding interno: 12dp horizontal + 12dp vertical
- `tvFruitName`: 15sp bold
- `chipFruitStatus`: 11sp, chipMinHeight=26dp, chipStrokeWidth=0dp, ensureMinTouchTargetSize=false
- `tvFruitExpiry`: 12sp, alpha=0.55
- `tvFruitQuantity`: 12sp, alpha=0.55, color neutro (eliminado verde bold anterior)

### Estado al finalizar
- `BUILD SUCCESSFUL` — sin errores ni warnings
- Pendiente de rediseñar: `fragment_add_fruit.xml` (bottom sheet añadir/editar fruta)
- Pendiente de probar en dispositivo físico: flujo completo del rediseño, especialmente `activity_result.xml` (imagen 200×200dp centerInside sin recorte)

---
