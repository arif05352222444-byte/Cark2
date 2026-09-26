# CHANGELOG

## 0.5.0 — Hakem paneli son hali + TV rötuşları
- Hakem paneli artık SADECE sağ altta küçük pencere (ekranın ~%10'u). Ana ekran kararmıyor, bulmaca görünür kalıyor.
  İçerik: HAKEM + oyuncu (+ finalde süre), DOĞRU CEVAP perdesi, CEVABI AÇ/GİZLE, TAMAM / DEVAM, küçük YAZARAK GİR.
- Cevap açıldıktan 3 sn sonra kendiliğinden gizlenir; TAMAM/DEVAM/GERİ öncesi her zaman önce maskelenir.
- GERİ: panel kapanır, sıra/skor/tur değişmez (çözüm denemesi sayılmaz).
- Çözüm sırasında oyun ekranında küçük "ARİF, CEVABI SÖYLE!" kapsülü.
- Ana menü rötuşları: çark ~%13 büyük, logo daha büyük ve daha derin 3D, ampuller/hale daha parlak,
  mavi sis azaltıldı, İstanbul silueti ve ışıkları belirginleşti, sıcak yan spotlar güçlendi,
  zeminde daha fazla altın yansıma, butonlarda daha fazla parlaklık/derinlik, alt slogan bandı inceltildi.
- Mikrofon/SpeechRecognizer eklenmedi (bilinçli): karar zaten hakemde, çoğu TV Box'ta mikrofon yok.

## 0.4.0 — Oyuncu sayısı seçimi + hakemli hızlı çözüm
- Oyuncu isimleri ekranında 2 OYUNCU / 3 OYUNCU / 4 OYUNCU seçimi (varsayılan 3). Satır sayısı seçime göre.
  Boş satırlara başlarken otomatik rastgele isim verilir → isim yazmadan da başlanır. "OYUNCU EKLE" kaldırıldı.
- İlk oyuncu her yeni oyunda rastgele (GameEngine.newGame). Kartlar arasında hızla dolaşan ışık + tık sesi,
  sonra "İLK SIRA ARİF'TE!" (Türkçe ek uyumlu). Sonraki turlar bu oyuncudan başlayarak döner.
- ÇÖZ artık yazdırmıyor: "CEVABI SÖYLE" paneli + sağ altta perdeli HAKEM penceresi (AÇ / GİZLE).
  Hakem gizli cevaba bakar: TAMAM · DOĞRU → doğru çözüm akışı, DEVAM · YANLIŞ → sıra geçer, cevap açılmaz.
  Cevap yazısı bilerek küçük (uzaktan okunmasın). YAZARAK GİR yedek olarak duruyor.
- Finalde de aynı hakem sistemi (süre işlemeye devam eder).
- GameEngine: confirmSolve / confirmFinal (mevcut doğru/yanlış akışını kullanır), startingPlayerIndex (kayda eklendi).
- Sesler: ÇÖZ'de "Cevabın nedir?", çekilişte çark tık sesi, sonra "Sıra sende!".
- Testler 48 → 56: 2/3/4 oyuncu, rastgele başlangıç sadece mevcut oyunculardan, tur rotasyonu,
  hakem doğru/yanlış, final hakem, İZMİR/izmir.

## 0.3.0 — Final ses paketi (AileCarki_AudioPack_Final)
- 76 MP3 eklendi (51 sunucu cümlesi, 18 efekt, 4 arayüz efekti, 3 müzik) → assets/audio/{voice,effect,ui,music}.
- Tüm eşleme tek dosyada: audio/AudioManifest.kt (62 SoundId, hepsi dosyaya bağlı; eksik yok).
- Yeni sunucu anları: çark durunca puan ("500 puan!"), Joker/2X/Sıra geç/İflas anında; doğru harf sayısı ("Üç tane var!"),
  harf yok, sesli harf seç, sıra sende, oyun başlasın, final harfleri, süre başladı, son 5 saniye, süre doldu.
- Yeni efektler: joker, puan ekleme, kısa alkış (doğru cevap), büyük alkış + kutlama (final), tur galibi, final girişi.
- AudioManager: sunucu cümleleri artık birbirini kesmiyor (kısa kuyruk); uzun efektler MediaPlayer ile çalıyor
  (SoundPool bellek sınırı); menü → oyun geçişinde aynı müzik kesilmeden devam ediyor.
- Oyun motoru / kurallar / UI değişmedi.

## 0.2.0 — Görsel tasarım (yarışma programı görünümü)
- Oyuncu sayısı: en az 2, en fazla 4. Boş satırlar yok sayılır (3 isim zorunlu değil). Varsayılan 2 satır.
- Yeni sahne arka planı: spotlar, ışık hüzmeleri, İstanbul silueti (köprü, kubbeler, minareler), altın neon yan çerçeveler, parlak sahne zemini. Statik, blur yok.
- AİLE ÇARKI tabelası: 3D altın yazı (kontur + derinlik + gradyan), çizilmiş altın yıldızlar, ampuller yavaş dalga halinde yanıp sönüyor, dış hale nefes alıyor.
- Font: Paytone One (SIL OFL 1.1, FONT_LICENSE_OFL.txt) — Türkçe karakterlerin tamamı var.
- Butonlar: koyu mavi gradyan + gloss + neon kenar; odakta altın gradyan + hale + büyüme; ikonlar (Canvas, bağımlılıksız).
- Çark: parlak gradyanlı dilimler, kalın altın çerçeve, yanıp sönen ampuller, altın göbek + yıldız, büyük altın ibre, durduğu dilim parlıyor, sahne kaidesi. Açı matematiği aynı.
- Oyun ekranı: çark solda; ÇARKI ÇEVİR ile sahne kararır, çark büyüyerek merkeze gelir, döner, sonucu gösterir, yerine döner; harf kartelası alttan gelir. Sağda dikey menü (ÇARKI ÇEVİR / HARF SEÇ / SESLİ HARF AL / ÇÖZ) + kumanda yardımı. "500 PUAN / Harf seç" altın rozeti.
- Oyuncu kartları: parlak, oyuncu renkli, aktif oyuncuda altın hale; puan artınca pulse, iflasta sarsıntı; 2X rozeti; "SIRA SENDE" yuvarlağı.
- Olaylar: puan sayımı (500→1000→1500), iflasta ekran kararması + sarsıntı, JOKER/2X ışık patlaması, SIRA GEÇ camgöbeği.
- Harf kutuları 3D (gölge, parlaklık), açılışta dönme + altın parlama. Harf tuşları dikdörtgen, pasifler çarpılı ve soluk.
- Ana menü, oyuncu isimleri ve ayarlar ekranları referans görsellere göre yeniden düzenlendi (ayarlarda ikonlu satırlar, ◀ ▶ kutuları, yükselen seviye çubukları).
- Oyun motoru, puanlama, çark sonucu, bulmaca, kayıt sistemi DEĞİŞMEDİ (sadece GameRules oyuncu limitleri).

## 0.1.2 — CI düzeltmesi
- PuzzleRepository.kt: yorum içindeki `/*` iç içe yorum açıp dosyayı bozuyordu (Unclosed comment), düzeltildi.

## 0.1.1 — Final build hazırlık turu
### Düzeltildi
- **2X + JOKER kuralı:** 2X aktifken Joker'den gelen doğru ünsüz artık 2X hakkını tüketiyor.
  Joker puanı sabit 1000 kalıyor (2X ile katlanmıyor). Eski kodda 2X hakkı Joker sonrası da kalıyordu.
- **Odak:** Çıkış diyaloğu kapanınca odak ÇARKI ÇEVİR / kartelaya yeniden veriliyor.
- **Odak:** Oyuncu ismi diyaloğu kapanınca odak düzenlenen satıra dönüyor.
- **Final sayacı:** Oyun ekranından çıkılınca (dispose) sayaç temizleniyor.

### Eklendi
- `audio/AudioManifest.kt`: tek merkezi ses eşleme dosyası (SoundId → dosya adı). Numaralı final paket
  (01_hosgeldiniz…) buradan bağlanır. Paket `assets/audio/` altına düz de atılabilir.
- Unit testler (+10): 2X+Joker, Joker sabit 1000, 2X sonrası yanlış harf, final harf limitleri
  (verilen/tekrar/ikinci sesli), dead-end yok, çark çizim açısı ↔ pointer eşleşmesi (500 tekrar),
  0/360 wrap-around, 500 dilimi, İngilizce varsayılan locale'de İ/I, 29 harfli klavye.
- CHANGELOG.md, AUDIO_INTEGRATION.md

### Değişmedi
- Dependency / Gradle / SDK sürümleri değiştirilmedi.
- Mimari, oyun motoru ve tasarım korunarak sadece hedefli değişiklik yapıldı.

## 0.1.0 — İlk sürüm
- Proje, oyun motoru, tüm ekranlar, 156 soruluk kelime bankası, ses altyapısı, 38 unit test.
