plugins {
    alias(libs.plugins.android.application) // Plugin de aplicación Android (AGP)
    alias(libs.plugins.kotlin.android) // Plugin de Kotlin para Android
}

// Usar el directorio de build por defecto para evitar fricciones con herramientas/IDE
// (antes estaba en build2 y podía generar locks en Windows)


android { // Bloque principal de configuración Android
    namespace = "com.miapp.xanostorekotlin" // Paquete base para R y BuildConfig
    compileSdk = 36 // API level de compilación (Android 15)

    defaultConfig { // Configuración por defecto del módulo app
        applicationId = "com.miapp.xanostorekotlin" // ID único del paquete de la app
        minSdk = 24 // Mínimo nivel de API soportado
        targetSdk = 36 // Nivel de API objetivo
        versionCode = 1 // Código de versión para Play Store
        versionName = "1.0" // Nombre de versión mostrado

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // Runner para tests instrumentados
        // Configuración de endpoints Xano (actualizados según indicación del usuario)
        // Nota: Retrofit requiere que la base URL termine con "/".
        buildConfigField("String", "XANO_STORE_BASE", "\"https://x8ki-letl-twmt.n7.xano.io/api:3EYdZ3Ae/\"") // Base URL Store (Xano)
        buildConfigField("String", "XANO_AUTH_BASE", "\"https://x8ki-letl-twmt.n7.xano.io/api:R4CkUNlW/\"") // Base URL Auth (Xano)
        buildConfigField("int", "XANO_TOKEN_TTL_SEC", "86400") // TTL de token simulado

        // Credenciales DEMO (para pruebas locales). Asegúrate de crear estas cuentas en Xano.
        buildConfigField("String", "DEMO_USER_EMAIL", "\"user@demo.local\"")
        buildConfigField("String", "DEMO_USER_PASSWORD", "\"User123!\"")
        buildConfigField("String", "DEMO_ADMIN_EMAIL", "\"admin@demo.local\"")
        buildConfigField("String", "DEMO_ADMIN_PASSWORD", "\"Admin123!\"")
    }

    buildTypes { // Tipos de build (debug/release)
        release { // Configuración para versión release
            isMinifyEnabled = false // No minificar para facilitar depuración
            proguardFiles( // Archivos de reglas Proguard/R8
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions { // Opciones de compatibilidad Java
        sourceCompatibility = JavaVersion.VERSION_11 // Compilar con Java 11
        targetCompatibility = JavaVersion.VERSION_11 // Target Java 11
    }
    kotlinOptions { // Opciones del compilador Kotlin
        jvmTarget = "11" // Bytecode objetivo Java 11
    }
    buildFeatures { // Activamos features del módulo
        viewBinding = true // Generación de clases de binding por layout
        buildConfig = true // Generación de BuildConfig con campos custom
    }
}

// Tarea para intentar desbloquear/eliminar R.jar antes de procesar recursos.
// Útil en Windows cuando algún proceso mantiene locks sobre archivos en build.
tasks.register("unlockRJar") {
    group = "build"
    description = "Intentar eliminar R.jar bloqueado en intermediates"
    doLast {
        val intermediates = file("${buildDir}/intermediates/compile_and_runtime_not_namespaced_r_class_jar")
        if (intermediates.exists()) {
            intermediates.walkTopDown().filter { it.isFile && it.name == "R.jar" }.forEach { jar ->
                var deleted = false
                repeat(8) {
                    try {
                        if (jar.delete()) { deleted = true; return@repeat }
                    } catch (_: Exception) { /* ignore */ }
                    Thread.sleep(200)
                }
                if (!deleted) {
                    logger.warn("No se pudo eliminar ${jar.absolutePath}. Continuando...")
                }
            }
        }
    }
}

// Ejecutar unlockRJar antes de todas las tareas de process*Resources de todas las variantes
tasks.matching { it.name.contains("process") && it.name.contains("Resources") }.configureEach {
    dependsOn("unlockRJar")
}

dependencies { // Dependencias del módulo

    implementation(libs.androidx.core.ktx) // Extensiones Kotlin para Android core
    implementation(libs.androidx.appcompat) // Compatibilidad de componentes UI
    implementation(libs.material) // Componentes Material Design
    implementation(libs.androidx.recyclerview) // Lista y grids eficientes
    implementation(libs.androidx.constraintlayout) // Layout flexible para vistas
    implementation(libs.androidx.lifecycle.runtime.ktx) // Corrutinas y lifecycle integrados
    implementation(libs.androidx.activity.ktx) // Extensiones para Activities en Kotlin

    // Retrofit / OkHttp / Gson
    implementation(libs.retrofit) // Cliente HTTP de alto nivel
    implementation(libs.converter.gson) // Convertidor JSON usando Gson
    implementation(libs.okhttp) // Cliente HTTP subyacente
    implementation(libs.okhttp.logging) // Interceptor de logging para depuración

    // Corutinas
    implementation(libs.kotlinx.coroutines.android) // Soporte de corrutinas en Android

    // Imágenes
    implementation(libs.coil)
    implementation(libs.androidx.activity)

    testImplementation(libs.junit) // Unit testing con JUnit4
    androidTestImplementation(libs.androidx.junit) // Testing instrumentado (JUnit ext)
    androidTestImplementation(libs.androidx.espresso.core) // Testing de UI con Espresso
}