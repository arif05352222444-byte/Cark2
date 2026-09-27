# Aile Çarkı — Android TV / TV Box

Kumandayla oynanan, tamamen Türkçe, aile içi kelime ve çark yarışması. Native Android: Kotlin + Jetpack Compose.

**Paket:** `com.ailecarki.tv`  
**minSdk:** 21 · **targetSdk:** 34 · **landscape / Android TV / Leanback**  
**Yerel doğrulama:** Domain katmanı `kotlinc` ile derlendi; mevcut **66/66 domain unit test senaryosu geçti**.  
**Android build:** Bu çalışma ortamında dış ağa erişim olmadığı için Gradle dağıtımı indirilemedi; gerçek APK build'i `.github/workflows/build-apk.yml` ile GitHub Actions'ta alınmalıdır.

## Son oyun akışı

1. Uygulama açılır → **sessiz aile anma / Fâtiha ekranı** → `ÂMİN` → ana menü.
2. Yeni oyun → **2 / 3 / 4 oyuncu** seçilir; boş isimler istenirse rastgele aile isimleriyle doldurulur.
3. İlk oyuncu mevcut oyuncular arasından rastgele seçilir; kısa ışıklı çekiliş gösterilir.
4. Tur: çark → sonuç → harf → puan / sıra → sesli harf veya ÇÖZ.
5. ÇÖZ: ana yöntem **hakemli hızlı çözüm**. Sağ altta küçük panel; söylenen cevap isteğe bağlı `SpeechRecognizer(tr-TR)` ile yazıya dökülür fakat kararı hakem verir. Gerçek cevap perde altındadır. `TAMAM` doğru, `DEVAM` yanlış/sıra geçer, `YAZARAK GİR` yalnızca eksik harfleri doldurur.
6. Normal turlar sonunda finalist → final harfleri + süre → hakem/yazarak çözüm.
7. Final kazanılırsa hareketli kutlama → yalnız şampiyon kartı → **3 sürpriz zarf**. Ödüller her oyunda yeniden karıştırılır.

## Çark

12 büyük segment; TV'den uzaktan okunacak şekilde 30°:

`100 · 500 · 2X · 300 · 1000 · JOKER · 400 · 750 · İFLAS · 250 · 2000 · SIRA GEÇ`

Görsel sistem hibrit: dönen segmentler Canvas ile çizilir; dış altın LED frame, merkez yıldızlı göbek ve ibre sabit görsel assettir. Segment yazıları güvenli halkada otomatik sığdırılır.

### JOKER

- Ücretsiz ünsüz seçimi.
- Doğruysa: **1.000 × çıkan harf adedi** puan, tüm eşleri açılır, sıra devam eder.
- İlk seçim yanlışsa: **bir ücretsiz seçim hakkı daha**.
- İkinci de yanlışsa: puan yok, sıra geçer.
- Aktif 2X varsa Joker puanını katlamaz; fakat başarılı Joker ünsüzü 2X hakkını tüketir.

### 2X

Sonraki başarılı normal ünsüz seçiminin puanını ikiye katlar ve sonra tüketilir.

## Görsel kalite

- Gece İstanbul / Boğaz sahnesi, lacivert-mor derinlik, sıcak altın spotlar, neon mavi konturlar.
- 3D altın `AİLE ÇARKI` logo, glossy TV butonları, belirgin aktif oyuncu kartı.
- Aktif oyuncunun kartının üstünde yanıp sönen **SIRA SENDE** etiketi.
- Puan sonucu ekranında aktif oyuncu adı ayrı ve büyük gösterilir.
- Tur sonunda havai fişek, konfeti, hareketli sahne spotları ve kıvılcım fıskiyeleri.
- Büyük final kazanımında yalnız şampiyonun büyük puan kartı; sonra zarf ekranı.

## Ses

`app/src/main/assets/audio/` altında **76 MP3**: sunucu, efektler ve 3 müzik. Merkezi eşleme `audio/AudioManifest.kt` içindedir. Sunucu konuşurken müzik ducking uygulanır. Anma ekranı bilerek tamamen sessizdir.

Dinamik oyuncu isimleri için altyapı `DynamicSpeechService` üzerinden hazırdır; gerçek ElevenLabs API anahtarı APK'ya gömülmez.

## GitHub Actions ile APK

1. Bu ZIP'in içeriğini bir GitHub reposunun köküne yükleyin (`.github` dahil).
2. **Actions → Build APK** çalışır.
3. Workflow `testDebugUnitTest` ve `assembleDebug` çalıştırır.
4. Artifact: **AileCarki-debug-apk** → `app-debug.apk`.

Yerelde Android Studio + JDK 17 ile:

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

## Önemli klasörler

```text
app/src/main/java/com/ailecarki/tv/
  domain/                 # saf Kotlin oyun motoru
  data/                   # DataStore, puzzle repository
  audio/                  # SoundId, AudioManifest, AudioManager
  ui/components/          # çark, kartlar, kutlama, TV butonları
  ui/screens/             # Memorial, Home, Setup, Game, Final, Settings
app/src/main/assets/puzzles/word_bank.json
app/src/main/assets/audio/
app/src/main/res/drawable-nodpi/  # premium sahne / çark / logo assetleri
```

## Font

**Paytone One**, SIL Open Font License 1.1. Lisans: `FONT_LICENSE_OFL.txt`.
