package com.ailecarki.tv.audio

enum class AudioCategory(val folder: String) { VOICE("voice"), EFFECT("effect"), MUSIC("music"), UI("ui") }

/**
 * Oyunun kullandığı sesler. Kod bu kimliklere bağlıdır; dosya adlarına değil.
 * Gerçek dosya adları tek yerde eşlenir: [AudioManifest].
 * [streamed] = uzun efekt (SoundPool bellek sınırına takılmasın diye MediaPlayer ile çalar).
 */
enum class SoundId(val category: AudioCategory, val fileBase: String, val streamed: Boolean = false) {
    // --- Sunucu (sabit cümleler)
    VOICE_WELCOME(AudioCategory.VOICE, "welcome"),
    VOICE_GAME_START(AudioCategory.VOICE, "game_start"),
    VOICE_YOUR_TURN(AudioCategory.VOICE, "your_turn"),
    VOICE_SPIN_PROMPT(AudioCategory.VOICE, "spin_prompt"),
    VOICE_WHEEL_SPINNING(AudioCategory.VOICE, "wheel_spinning"),
    VOICE_BUY_VOWEL(AudioCategory.VOICE, "buy_vowel"),
    VOICE_LETTER_MISSING(AudioCategory.VOICE, "letter_missing"),
    VOICE_COUNT_1(AudioCategory.VOICE, "count_1"),
    VOICE_COUNT_2(AudioCategory.VOICE, "count_2"),
    VOICE_COUNT_3(AudioCategory.VOICE, "count_3"),
    VOICE_COUNT_4(AudioCategory.VOICE, "count_4"),
    VOICE_COUNT_5(AudioCategory.VOICE, "count_5"),
    VOICE_GREAT(AudioCategory.VOICE, "great"),
    VOICE_POINTS_100(AudioCategory.VOICE, "points_100"),
    VOICE_POINTS_200(AudioCategory.VOICE, "points_200"),
    VOICE_POINTS_250(AudioCategory.VOICE, "points_250"),
    VOICE_POINTS_300(AudioCategory.VOICE, "points_300"),
    VOICE_POINTS_400(AudioCategory.VOICE, "points_400"),
    VOICE_POINTS_500(AudioCategory.VOICE, "points_500"),
    VOICE_POINTS_750(AudioCategory.VOICE, "points_750"),
    VOICE_POINTS_1000(AudioCategory.VOICE, "points_1000"),
    VOICE_POINTS_1500(AudioCategory.VOICE, "points_1500"),
    VOICE_POINTS_2000(AudioCategory.VOICE, "points_2000"),
    VOICE_JOKER(AudioCategory.VOICE, "joker"),
    VOICE_DOUBLE(AudioCategory.VOICE, "double"),
    VOICE_PASS(AudioCategory.VOICE, "pass"),
    VOICE_CORRECT(AudioCategory.VOICE, "correct"),
    VOICE_WRONG(AudioCategory.VOICE, "wrong"),
    VOICE_BANKRUPT(AudioCategory.VOICE, "bankrupt"),
    VOICE_ROUND_COMPLETE(AudioCategory.VOICE, "round_complete"),
    VOICE_FINAL_INTRO(AudioCategory.VOICE, "final_intro"),
    VOICE_FINAL_LETTERS(AudioCategory.VOICE, "final_letters"),
    VOICE_TIMER_START(AudioCategory.VOICE, "timer_start"),
    VOICE_FIVE_SECONDS(AudioCategory.VOICE, "five_seconds"),
    VOICE_TIME_UP(AudioCategory.VOICE, "time_up"),
    VOICE_FINAL_WIN(AudioCategory.VOICE, "final_win"),
    VOICE_FINAL_LOSE(AudioCategory.VOICE, "final_lose"),

    // --- Efektler
    FX_WHEEL_SPIN(AudioCategory.EFFECT, "wheel_spin", streamed = true),
    FX_WHEEL_STOP(AudioCategory.EFFECT, "wheel_stop"),
    FX_LETTER_REVEAL(AudioCategory.EFFECT, "letter_reveal"),
    FX_LETTER_WRONG(AudioCategory.EFFECT, "letter_wrong"),
    FX_SCORE_ADD(AudioCategory.EFFECT, "score_add"),
    FX_BANKRUPT(AudioCategory.EFFECT, "bankrupt"),
    FX_LOSE_TURN(AudioCategory.EFFECT, "lose_turn"),
    FX_DOUBLE(AudioCategory.EFFECT, "double"),
    FX_JOKER(AudioCategory.EFFECT, "joker"),
    FX_CORRECT(AudioCategory.EFFECT, "correct"),
    FX_WRONG(AudioCategory.EFFECT, "wrong"),
    FX_APPLAUSE(AudioCategory.EFFECT, "applause"),
    FX_ROUND_WIN(AudioCategory.EFFECT, "round_win", streamed = true),
    FX_FINAL_INTRO(AudioCategory.EFFECT, "final_intro", streamed = true),
    FX_CELEBRATION(AudioCategory.EFFECT, "celebration", streamed = true),
    FX_APPLAUSE_BIG(AudioCategory.EFFECT, "applause_big", streamed = true),
    FX_TIMER_TICK(AudioCategory.EFFECT, "timer_tick"),
    FX_TIMEOUT(AudioCategory.EFFECT, "timeout"),

    // --- Müzik
    MUSIC_MENU(AudioCategory.MUSIC, "menu"),
    MUSIC_GAME(AudioCategory.MUSIC, "game"),
    MUSIC_FINAL(AudioCategory.MUSIC, "final"),

    // --- Arayüz
    UI_MOVE(AudioCategory.UI, "move"),
    UI_SELECT(AudioCategory.UI, "select"),
    UI_BACK(AudioCategory.UI, "back"),
    UI_DISABLED(AudioCategory.UI, "disabled"),
    ;

    companion object {
        private val POINTS = mapOf(
            100 to VOICE_POINTS_100, 200 to VOICE_POINTS_200, 250 to VOICE_POINTS_250,
            300 to VOICE_POINTS_300, 400 to VOICE_POINTS_400, 500 to VOICE_POINTS_500,
            750 to VOICE_POINTS_750, 1000 to VOICE_POINTS_1000, 1500 to VOICE_POINTS_1500,
            2000 to VOICE_POINTS_2000,
        )

        /** "500 puan!" gibi; paket dışı bir değer için null. */
        fun pointsVoice(value: Int): SoundId? = POINTS[value]

        /** "Üç tane var!" gibi; 5'ten fazlası için "Harika!". */
        fun letterCountVoice(count: Int): SoundId = when (count) {
            1 -> VOICE_COUNT_1
            2 -> VOICE_COUNT_2
            3 -> VOICE_COUNT_3
            4 -> VOICE_COUNT_4
            5 -> VOICE_COUNT_5
            else -> VOICE_GREAT
        }
    }
}
