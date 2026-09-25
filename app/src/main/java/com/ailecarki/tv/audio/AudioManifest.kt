package com.ailecarki.tv.audio

/**
 * TEK MERKEZİ SES EŞLEME DOSYASI — AileCarki_AudioPack_Final (ElevenLabs, tr-TR).
 *
 * Oyun kodu yalnızca [SoundId] kullanır. Dosya adları sadece burada yazılır.
 * Paket dosyaları: assets/audio/voice (speech), effect (efektler), music, ui (arayüz efektleri).
 * Bir ses değişecekse sadece bu tabloyu güncelleyin. Uzantı yazılmaz (ogg/mp3/wav/m4a denenir).
 * Dosya yoksa ses sessizce atlanır; oyun durmaz.
 */
object AudioManifest {
    val overrides: Map<SoundId, List<String>> = mapOf(
        // Sunucu
        SoundId.VOICE_WELCOME to listOf("voice_welcome"),
        SoundId.VOICE_GAME_START to listOf("voice_game_start"),
        SoundId.VOICE_YOUR_TURN to listOf("voice_your_turn"),
        SoundId.VOICE_SPIN_PROMPT to listOf("voice_spin"),
        SoundId.VOICE_WHEEL_SPINNING to listOf("voice_wheel_spinning"),
        SoundId.VOICE_BUY_VOWEL to listOf("voice_buy_vowel"),
        SoundId.VOICE_LETTER_MISSING to listOf("voice_letter_missing"),
        SoundId.VOICE_COUNT_1 to listOf("voice_one_letter"),
        SoundId.VOICE_COUNT_2 to listOf("voice_two_letters"),
        SoundId.VOICE_COUNT_3 to listOf("voice_three_letters"),
        SoundId.VOICE_COUNT_4 to listOf("voice_four_letters"),
        SoundId.VOICE_COUNT_5 to listOf("voice_five_letters"),
        SoundId.VOICE_GREAT to listOf("voice_great"),
        SoundId.VOICE_POINTS_100 to listOf("voice_100_points"),
        SoundId.VOICE_POINTS_200 to listOf("voice_200_points"),
        SoundId.VOICE_POINTS_250 to listOf("voice_250_points"),
        SoundId.VOICE_POINTS_300 to listOf("voice_300_points"),
        SoundId.VOICE_POINTS_400 to listOf("voice_400_points"),
        SoundId.VOICE_POINTS_500 to listOf("voice_500_points"),
        SoundId.VOICE_POINTS_750 to listOf("voice_750_points"),
        SoundId.VOICE_POINTS_1000 to listOf("voice_1000_points"),
        SoundId.VOICE_POINTS_1500 to listOf("voice_1500_points"),
        SoundId.VOICE_POINTS_2000 to listOf("voice_2000_points"),
        SoundId.VOICE_JOKER to listOf("voice_joker"),
        SoundId.VOICE_DOUBLE to listOf("voice_double"),
        SoundId.VOICE_PASS to listOf("voice_pass"),
        SoundId.VOICE_CORRECT to listOf("voice_correct"),
        SoundId.VOICE_WRONG to listOf("voice_wrong"),
        SoundId.VOICE_BANKRUPT to listOf("voice_bankrupt"),
        SoundId.VOICE_ROUND_COMPLETE to listOf("voice_round_complete"),
        SoundId.VOICE_FINAL_INTRO to listOf("voice_final"),
        SoundId.VOICE_FINAL_LETTERS to listOf("voice_final_letters"),
        SoundId.VOICE_TIMER_START to listOf("voice_timer_start"),
        SoundId.VOICE_FIVE_SECONDS to listOf("voice_five_seconds"),
        SoundId.VOICE_TIME_UP to listOf("voice_time_up"),
        SoundId.VOICE_FINAL_WIN to listOf("voice_final_correct"),
        SoundId.VOICE_FINAL_LOSE to listOf("voice_final_wrong"),
        // Efektler
        SoundId.FX_WHEEL_SPIN to listOf("effect_wheel_spin"),
        SoundId.FX_WHEEL_STOP to listOf("effect_wheel_stop"),
        SoundId.FX_LETTER_REVEAL to listOf("effect_letter_reveal"),
        SoundId.FX_LETTER_WRONG to listOf("effect_letter_wrong"),
        SoundId.FX_SCORE_ADD to listOf("effect_score_add"),
        SoundId.FX_BANKRUPT to listOf("effect_bankrupt"),
        SoundId.FX_LOSE_TURN to listOf("effect_pass"),
        SoundId.FX_DOUBLE to listOf("effect_double"),
        SoundId.FX_JOKER to listOf("effect_joker"),
        SoundId.FX_CORRECT to listOf("effect_letter_correct"),
        SoundId.FX_WRONG to listOf("effect_letter_wrong"),
        SoundId.FX_APPLAUSE to listOf("effect_applause_short"),
        SoundId.FX_ROUND_WIN to listOf("effect_round_win"),
        SoundId.FX_FINAL_INTRO to listOf("effect_final_intro"),
        SoundId.FX_CELEBRATION to listOf("effect_final_win"),
        SoundId.FX_APPLAUSE_BIG to listOf("effect_applause_big"),
        SoundId.FX_TIMER_TICK to listOf("effect_countdown"),
        SoundId.FX_TIMEOUT to listOf("effect_timeout"),
        // Müzik (menü ve oyun aynı parça → ekran değişince kesilmeden devam eder)
        SoundId.MUSIC_MENU to listOf("music_game_loop"),
        SoundId.MUSIC_GAME to listOf("music_game_loop"),
        SoundId.MUSIC_FINAL to listOf("music_final_loop"),
        // Arayüz
        SoundId.UI_MOVE to listOf("effect_ui_move"),
        SoundId.UI_SELECT to listOf("effect_ui_select"),
        SoundId.UI_BACK to listOf("effect_ui_back"),
        SoundId.UI_DISABLED to listOf("effect_ui_disabled"),
    )

    fun candidates(id: SoundId): List<String> = overrides[id].orEmpty() + id.fileBase

    val extensions = listOf("ogg", "mp3", "wav", "m4a")
}
