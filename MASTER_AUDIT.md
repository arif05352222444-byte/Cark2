# Aile Çarkı — MASTER FINAL QA

## Kullanıcı kararları yeniden kontrol edildi
- [x] Açılışın ilk ekranı sessiz Fâtiha/anma ekranı; ÂMİN ile devam.
- [x] 2 / 3 / 4 oyuncu; rastgele ilk oyuncu.
- [x] 12 segmentli premium çark.
- [x] Segment yazıları çembere/göbeğe değmeyecek güvenli halkada.
- [x] Çark 3D derinlik + glossy dilimler + güçlü marquee LED halesi.
- [x] Aktif oyuncu üstünde yanıp sönen SIRA SENDE; isim/puan daha büyük.
- [x] Puan sonucu ekranında aktif oyuncu adı ayrı ve büyük.
- [x] Joker: 1.000 × harf adedi; ilk yanlışta 1 ekstra seçim; ikinci yanlışta sıra geçer.
- [x] 2X Joker puanını katlamaz, başarılı Joker 2X hakkını tüketir.
- [x] Hakemli hızlı çözüm; cevap perde altında; TAMAM / DEVAM; BACK state değiştirmez.
- [x] SpeechRecognizer yalnız yardımcı; otomatik doğru kararı vermez; mikrofonsuz fallback var.
- [x] Yazarak girişte yalnız eksik harfler doldurulur.
- [x] Tur kazananı büyük ve belirgin; hareketli kutlama katmanı.
- [x] Büyük final: güçlü kutlama, yalnız şampiyon kartı, sonra 3 zarf.
- [x] Zarf sonuçları her oyunda yeniden karışır: Dilek / Ceza / 1.000 TL.
- [x] 76 ses dosyası; anma ekranında ses/müzik yok.

## Teknik doğrulama
- [x] Domain Kotlin derleme: başarılı.
- [x] Unit test senaryoları: **66 passed / 0 failed** (bağımsız yerel runner).
- [x] `R.string` audit: eksik referans yok.
- [x] `R.drawable` audit: eksik referans yok.
- [x] XML parse: tüm resource XML'leri geçerli.
- [x] SoundId referans audit: tanımsız enum yok.
- [x] AudioManifest: **66/66** SoundId eşli.
- [x] Eşlenen ses dosyaları: eksik yok.
- [x] Ses paketi: **76 MP3**.
- [x] Kelime bankası: **156 soru / 12 kategori / mükerrer ID yok**.
- [x] Değiştirilen UI kaynaklarında Kotlin parser seviyesinde syntax hatası yok.

## Bu ortamda yapılamayan tek doğrulama
Android Gradle build'i yerelde çalıştırılamadı; Gradle 8.9 dağıtımı `services.gradle.org` DNS erişimi olmadığı için indirilemedi. `.github/workflows/build-apk.yml` gerçek Android build/test/APK için hazırdır.
