# EMO Pet 🤖

Aplicación Android moderna de mascota virtual robot inspirada en **EMO**, con inteligencia artificial de **Géminis 3.5 Flash**, expresiones emocionales animadas en tiempo real, interacción táctil física y juegos integrados.

---

## 🚀 Integración Continua con GitHub Actions (CI/CD)

Este repositorio incluye un flujo de trabajo automatizado en `.github/workflows/build.yml` que compila el proyecto y genera el instalador APK automáticamente.

### ¿Cómo funciona?
1. Cada vez que haces **push** o abres un **Pull Request** a las ramas `main` o `master`, GitHub Actions:
   - Descarga el código y configura **Java 17 (Temurin)**.
   - Restaura el almacén de claves `debug.keystore` y el archivo de entorno `.env`.
   - Ejecuta las pruebas unitarias automáticas (`testDebugUnitTest`).
   - Compila la aplicación Android (`assembleDebug`).
   - Sube el archivo **APK generado** como un artefacto descargable.

2. **Descarga del APK compilado:**
   - Ve a la pestaña **Actions** en tu repositorio de GitHub.
   - Haz clic en la última ejecución de **Android CI - Build & Test**.
   - En la sección inferior **Artifacts**, descarga **`EMO-Pet-Debug-APK`**.

3. **(Opcional) Configurar tu clave de Gemini en GitHub Secrets:**
   - Ve a **Settings > Secrets and variables > Actions > New repository secret**.
   - Nombre: `GEMINI_API_KEY`
   - Valor: Tu clave de API de Google AI Studio.

---

## 📱 Características de la Aplicación

- **Avatar EMO Siempre Visible**: El robot EMO está presente en la parte superior en todo momento con animaciones de respiración, parpadeo e inclinación física.
- **Expresiones Emocionales en Vivo**: Más de 12 estados de ánimo renderizados por Canvas vectorial (*Feliz, Risa a Carcajadas, Enojado con cejas de fuego y temblor, Enamorado con corazones, Confundido con ?, Modo Facha con gafas cyber, etc.*).
- **Interacción Táctil Directa**: Acariciar la cabeza con el dedo activa ronroneos y corazones; tocar los auriculares activa el baile; tocar la batería recarga su energía.
- **Chat en Tiempo Real con IA Géminis**: Charla en primera persona sin censura, adaptando el rostro de EMO instantáneamente al tono de cada mensaje.
- **Juegos Integrados**: Verdad o Reto (Adulto / Picante +18 / Extremo / Confesiones), Ritmo & Baile con flechas y Ruleta Cyber.
