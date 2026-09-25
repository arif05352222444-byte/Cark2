# Aile Çarkı Audio Pack

## Paket özeti

- **Sunucu sesi:** Aile Çarkı Sunucusu
- **Voice ID:** `TWUsX2Dsrl6Po7HWFhWW`
- **Dil:** Türkçe (`tr-TR`)
- **Sağlayıcı:** ElevenLabs
- **Speech MP3:** 51
- **Effect MP3:** 22
- **Music MP3:** 3
- **Toplam MP3:** 76
- **API anahtarı:** Pakete dahil değildir.

Konuşma dosyaları Türkçedir. Efektlerde konuşma yoktur. Müzikler tamamen enstrümantaldir.

## Projedeki gerçek semantic ID eşlemesi

Bu tablo `AileCarkiTV_final` projesindeki gerçek `SoundId` anahtarları okunarak hazırlanmıştır.

| SoundId | Paket dosyası |
|---|---|
| `VOICE_WELCOME` | `speech/voice_welcome.mp3` |
| `VOICE_SPIN_PROMPT` | `speech/voice_spin.mp3` |
| `VOICE_CORRECT` | `speech/voice_correct.mp3` |
| `VOICE_WRONG` | `speech/voice_wrong.mp3` |
| `VOICE_BANKRUPT` | `speech/voice_bankrupt.mp3` |
| `VOICE_ROUND_COMPLETE` | `speech/voice_round_complete.mp3` |
| `VOICE_FINAL_INTRO` | `speech/voice_final.mp3` |
| `VOICE_FINAL_WIN` | `speech/voice_final_correct.mp3` |
| `VOICE_FINAL_LOSE` | `speech/voice_final_wrong.mp3` |
| `FX_WHEEL_SPIN` | `effects/effect_wheel_spin.mp3` |
| `FX_WHEEL_STOP` | `effects/effect_wheel_stop.mp3` |
| `FX_LETTER_REVEAL` | `effects/effect_letter_reveal.mp3` |
| `FX_LETTER_WRONG` | `effects/effect_letter_wrong.mp3` |
| `FX_BANKRUPT` | `effects/effect_bankrupt.mp3` |
| `FX_LOSE_TURN` | `effects/effect_pass.mp3` |
| `FX_DOUBLE` | `effects/effect_double.mp3` |
| `FX_CORRECT` | `effects/effect_letter_correct.mp3` |
| `FX_WRONG` | `effects/effect_letter_wrong.mp3` |
| `FX_CELEBRATION` | `effects/effect_final_win.mp3` |
| `FX_TIMER_TICK` | `effects/effect_countdown.mp3` |
| `FX_TIMEOUT` | `effects/effect_timeout.mp3` |
| `MUSIC_MENU` | `music/music_game_loop.mp3` |
| `MUSIC_GAME` | `music/music_game_loop.mp3` |
| `MUSIC_FINAL` | `music/music_final_loop.mp3` |
| `UI_MOVE` | `effects/effect_ui_move.mp3` |
| `UI_SELECT` | `effects/effect_ui_select.mp3` |
| `UI_BACK` | `effects/effect_ui_back.mp3` |
| `UI_DISABLED` | `effects/effect_ui_disabled.mp3` |

## Dinamik TTS neden ayrı tutuldu?

Oyuncu adı, anlık puan, seçilen harf, harf adedi ve sonraki oyuncu gibi oyun sırasında değişen bilgiler sabit MP3 olarak üretilmedi. Bunlar için `dynamic_tts_templates.json` içinde 10 şablon bulunmaktadır. İleride `DynamicSpeechService` bu şablonları gerçek oyun değerleriyle doldurup ElevenLabs API üzerinden canlı konuşma üretebilir.

## Dosya katalogları

- `speech_catalog.json`: 51 sabit Türkçe cümle ve süreleri
- `effects_catalog.json`: 22 efekt istemi ve süreleri
- `music_catalog.json`: 3 özgün enstrümantal müzik istemi ve süreleri
- `audio_manifest.json`: Uygulamanın gerçek semantic ID eşlemesi
- `dynamic_tts_templates.json`: Değişken konuşma şablonları
- `audio_qc_report.json` / `.csv`: Dosya bütünlüğü ve teknik kalite kontrolü

## Kalite kontrol

- 76/76 MP3 dosyası MP3 kare düzeyinde okunabildi.
- Boş veya bozuk dosya bulunmadı.
- SHA-256 bütünlük değeri her dosya için kaydedildi.
- `effect_ui_move.mp3` **0,192 sn** olacak şekilde kısaltıldı.
- `effect_wheel_tick.mp3` **0,144 sn** olacak şekilde kısaltıldı.
- Müzik süreleri: oyun **60,029 sn**, düşünme **30,041 sn**, final **30,041 sn**.
- Tüm speech dosyaları aynı özel ses ve aynı TTS ayarlarıyla üretildi.
- Efekt ve müzik üretimlerinde insan sesi kapatıldı; müziklerde Instrumental seçildi.

Teknik kontrol sonuçlarının tamamı `audio_qc_report.json` içindedir. Yayına almadan önce televizyonun gerçek hoparlörlerinde son bir insan kulağı kontrolü yapılması önerilir.

## Proje entegrasyonu

Uygulama dosyaları projenin mevcut `app/src/main/assets/audio/voice`, `effect`, `music` ve `ui` klasörlerine kopyalanır. GameEngine, UI ve oyun kuralları değiştirilmez. `MUSIC_MENU` ve `MUSIC_GAME` aynı ana oyun müziğini kullanır; `music_thinking_loop.mp3` paket içinde ayrıca teslim edilmiştir ve ileride istenirse ayrı bir semantic ID ile bağlanabilir.

## Build durumu

Bu bilgisayarda yalnızca 32-bit Java 8 bulunduğu ve Android SDK kurulu olmadığı için Android testleri ile APK derlemesi çalıştırılamadı. Ayrıntı: `BUILD_STATUS.txt`. Bu durum ses paketinin 76/76 teknik kalite kontrol sonucunu etkilemez.

