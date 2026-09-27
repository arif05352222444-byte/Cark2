# DEVELOPER NOTES — MASTER FINAL

## CURRENT STATUS

- Native Android TV projesi: Kotlin 2.0.21 + Jetpack Compose.
- Domain kaynakları `kotlinc` ile derlendi.
- Mevcut **66/66 domain unit test** yerel bağımsız runner ile geçti.
- Android/Compose tam build bu ortamda Gradle dağıtımı indirilemediği için burada alınamadı; GitHub Actions workflow hazır.

## WORKING / IMPLEMENTED

- [x] Android TV Leanback launcher, landscape, dokunmatik zorunlu değil.
- [x] Sessiz anma/Fâtiha açılışı; ÂMİN → ana menü. Metin/buton çakışmayacak üç bölgeli responsive layout.
- [x] 2 / 3 / 4 oyuncu seçimi, rastgele isim doldurma, rastgele ilk oyuncu + intro çekilişi.
- [x] 12 segment premium hibrit çark; WheelEngine sonuç matematiği ile aynı liste.
- [x] Puan, İFLAS, SIRA GEÇ, 2X, yeni JOKER ikinci-şans kuralı.
- [x] JOKER: 1000 × adet; ilk miss → bir retry; ikinci miss → sıra geçer; 2X Joker'i katlamaz ama başarılı Joker 2X'i tüketir.
- [x] Aktif oyuncu: güçlü altın pulse, büyüyen isim/puan, kart üstünde yanıp sönen SIRA SENDE.
- [x] Puan/harf seçiminde aktif oyuncu adı ayrı ve büyük.
- [x] Hakemli hızlı çözüm: küçük sağ-alt panel, SpeechRecognizer yardımcı, otomatik karar yok, cevap perdesi, TAMAM/DEVAM/BACK güvenliği.
- [x] YAZARAK GİR: tüm cevabı tekrar yazdırmaz, yalnız kapalı kutuları doldurtur.
- [x] Final: 3 ünsüz + 1 sesli, geri sayım, hakem/yazarak çözüm.
- [x] Tur/final kutlamaları: hareketli spotlar, havai fişek, yan fıskiyeler, konfeti.
- [x] Final kazanımında yalnız şampiyonun büyük kartı.
- [x] Final sonrası 3 rastgele zarf: Dilek Hakkı / Ceza Hakkı / 1.000 TL Ödül.
- [x] 76 MP3; AudioManifest merkezi eşleme; voice queue + music ducking.
- [x] Ayarlar ve aktif oyun kaydı DataStore.
- [x] 156 offline puzzle / 12 kategori.

## QA / STATIC CHECKS

- Domain: 66 passed / 0 failed.
- Resource audit: tüm `R.string` ve `R.drawable` referansları mevcut.
- XML audit: tüm `res/**/*.xml` dosyaları parse oluyor.
- SoundId audit: tüm SoundId'ler AudioManifest'te eşli; ilgili gerçek audio assetleri mevcut.
- Audio pack: 76 MP3 mevcut.

## BUILD LIMITATION

Bu çalışma ortamında `services.gradle.org` DNS erişimi yok. `./gradlew` bu nedenle Gradle 8.9 dağıtımını indiremedi. Bu, kaynak kod hatası kanıtı değildir; GitHub Actions gerçek Android compile/build kontrolüdür.

## NEXT STEP

GitHub'a MASTER FINAL ZIP içeriğini yükle → Actions / Build APK. Compile hatası çıkarsa logdaki ilk gerçek `e: file` satırından düzelt.

## KEY FILES

- Kurallar: `domain/rules/GameRules.kt`
- Çark: `domain/rules/WheelConfig.kt`, `ui/components/Wheel.kt`
- Joker: `domain/engine/GameEngine.kt`
- Hakem: `ui/screens/RefereeSolveDialog.kt`
- Eksik harf çözümü: `ui/components/MissingLettersInputDialog.kt`, `domain/engine/PartialAnswerComposer.kt`
- Aktif oyuncu: `ui/components/PlayerScoreCard.kt`
- Kutlama: `ui/components/Confetti.kt`
- Final/zarf: `ui/screens/GameCompleteScreen.kt`
- Anma: `ui/screens/MemorialScreen.kt`
- Ses: `audio/SoundId.kt`, `audio/AudioManifest.kt`, `audio/AudioManager.kt`
