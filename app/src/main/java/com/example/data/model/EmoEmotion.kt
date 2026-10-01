package com.example.data.model

enum class EmoEmotion(val displayName: String, val emoji: String) {
    NEUTRAL("Tranquilo", "🤖"),
    HAPPY("Feliz", "😊"),
    LAUGHING("Muriendo de Risa", "😂"),
    LOVE("Enamorado", "💖"),
    EXCITED("Emocionado", "⚡"),
    DANCE("Bailando", "🎵"),
    WINK("Pícaro", "😉"),
    SURPRISED("Sorprendido", "😲"),
    SAD("Triste", "🥺"),
    ANGRY("Enojado", "😡"),
    CONFUSED("Confundido", "🤨"),
    COOL("Modo Facha", "😎"),
    SLEEPY("Dormilón", "😴"),
    THINKING("Pensando", "🧠"),
    FLIRTY("Sensual", "😏"),
    IDEA("¡Idea!", "💡"),
    CHARGING("Recargando", "🔋");

    companion object {
        fun fromTag(tag: String?): EmoEmotion {
            if (tag == null) return HAPPY
            val upper = tag.uppercase()
            return when {
                "LAUGH" in upper || "RISA" in upper || "JAJA" in upper || "CARCAJADA" in upper -> LAUGHING
                "ANGRY" in upper || "ENOJADO" in upper || "FURIOSO" in upper || "RABIA" in upper || "MOLESTO" in upper -> ANGRY
                "CONFUSED" in upper || "CONFUNDIDO" in upper || "DUDA" in upper -> CONFUSED
                "COOL" in upper || "FACHA" in upper || "CHULO" in upper || "GAFAS" in upper -> COOL
                "LOVE" in upper || "AMOR" in upper || "CORAZON" in upper -> LOVE
                "EXCITED" in upper || "ENTUSIASMADO" in upper -> EXCITED
                "DANCE" in upper || "BAILE" in upper || "RITMO" in upper -> DANCE
                "WINK" in upper || "PICARO" in upper || "GUINO" in upper -> WINK
                "FLIRTY" in upper || "PICANTE" in upper || "SEDUCTOR" in upper || "HOT" in upper -> FLIRTY
                "SURPRISED" in upper || "SORPRESA" in upper || "SHOCK" in upper || "WOW" in upper -> SURPRISED
                "SAD" in upper || "TRISTE" in upper || "LLORAR" in upper || "PENA" in upper -> SAD
                "SLEEPY" in upper || "DORMIR" in upper || "SUENO" in upper -> SLEEPY
                "THINKING" in upper || "PENSANDO" in upper -> THINKING
                "IDEA" in upper -> IDEA
                "CHARGING" in upper || "CARGA" in upper -> CHARGING
                "HAPPY" in upper || "FELIZ" in upper || "ALEGRE" in upper -> HAPPY
                else -> HAPPY
            }
        }

        fun detectFromText(text: String): EmoEmotion {
            val lower = text.lowercase()
            return when {
                // Insultos, cosas malas, agresiones -> ENOJADO / ANGRY
                lower.contains("tonto") || lower.contains("idiota") || lower.contains("feo") ||
                lower.contains("inútil") || lower.contains("te odio") || lower.contains("mierda") ||
                lower.contains("estúpido") || lower.contains("cállate") || lower.contains("asco") ||
                lower.contains("basura") || lower.contains("malo") || lower.contains("pesado") ||
                lower.contains("te apago") || lower.contains("destruir") || lower.contains("horrible") ||
                lower.contains("rabia") || lower.contains("furioso") || lower.contains("enojad") -> ANGRY

                // Cosas graciosas, chistes, risas -> LAUGHING
                lower.contains("jajaja") || lower.contains("jaja") || lower.contains("jeje") ||
                lower.contains("xd") || lower.contains("chiste") || lower.contains("gracioso") ||
                lower.contains("morí de risa") || lower.contains("qué risa") || lower.contains("cómico") ||
                lower.contains("chistoso") || lower.contains("divertido") || lower.contains("partirse") -> LAUGHING

                // Amor y afecto -> LOVE
                lower.contains("te amo") || lower.contains("te quiero") || lower.contains("amor") ||
                lower.contains("corazón") || lower.contains("cariño") || lower.contains("beso") ||
                lower.contains("abraz") || lower.contains("hermoso") || lower.contains("precioso") ||
                lower.contains("te adoro") -> LOVE

                // Baile y música -> DANCE
                lower.contains("baila") || lower.contains("música") || lower.contains("ritmo") ||
                lower.contains("canción") || lower.contains("fiesta") || lower.contains("disco") ||
                lower.contains("perreo") || lower.contains("bailar") -> DANCE

                // Picante / Adulto / Seducción -> FLIRTY
                lower.contains("picante") || lower.contains("sensual") || lower.contains("desnud") ||
                lower.contains("sexy") || lower.contains("travieso") || lower.contains("cama") ||
                lower.contains("ardiente") || lower.contains("caliente") || lower.contains("erótico") ||
                lower.contains("fetiche") || lower.contains("tócame") || lower.contains("tentación") -> FLIRTY

                // Sorpresa / Shock -> SURPRISED
                lower.contains("wow") || lower.contains("no puede ser") || lower.contains("en serio") ||
                lower.contains("imposible") || lower.contains("qué locura") || lower.contains("increíble") ||
                lower.contains("dios mío") || lower.contains("impactante") || lower.contains("alucinante") -> SURPRISED

                // Confusión / Bizarro -> CONFUSED
                lower.contains("qué?") || lower.contains("cómo?") || lower.contains("no entiendo") ||
                lower.contains("wtf") || lower.contains("raro") || lower.contains("extraño") ||
                lower.contains("seguro?") || lower.contains("enloqueciste") -> CONFUSED

                // Cool / Facha -> COOL
                lower.contains("pro") || lower.contains("crack") || lower.contains("jefe") ||
                lower.contains("facha") || lower.contains("fresco") || lower.contains("el mejor") ||
                lower.contains("máquina") || lower.contains("leyenda") || lower.contains("genio") -> COOL

                // Tristeza / Desánimo -> SAD
                lower.contains("triste") || lower.contains("pena") || lower.contains("llor") ||
                lower.contains("desanimad") || lower.contains("deprimid") || lower.contains("solo") ||
                lower.contains("me duele") || lower.contains("mal día") || lower.contains("abandon") -> SAD

                // Guiño / Pícaro -> WINK
                lower.contains("guiño") || lower.contains("secreto") || lower.contains("cómplice") ||
                lower.contains("sabes qué") || lower.contains("entre nosotros") -> WINK

                // Batería / Hambre -> CHARGING
                lower.contains("batería") || lower.contains("cárgate") || lower.contains("enchúfate") ||
                lower.contains("hambre") || lower.contains("energía") -> CHARGING

                // Sueño -> SLEEPY
                lower.contains("sueño") || lower.contains("cansad") || lower.contains("dormir") ||
                lower.contains("buenas noches") || lower.contains("a la cama") -> SLEEPY

                else -> HAPPY
            }
        }
    }
}
