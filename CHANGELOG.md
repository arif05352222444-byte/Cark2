# CHANGELOG

## 0.6.2 — ULTRA derleme düzeltmesi
- `strings.xml` içindeki `prize_money_body` metnindeki kaçırılmamış kesme işareti `\'` olarak kaçırıldı (aapt2 mergeDebugResources hatası veriyordu).

## 0.6.1 — Premium sahne + yeni 3D çark assetleri
- bg_game / bg_menu / bg_panel, yeni gece İstanbul yarışma sahnesi görselleriyle yenilendi.
- AİLE ÇARKI logosu yeni 3D altın marquee assetiyle değiştirildi.
- wheel_frame / wheel_hub / wheel_pointer / podium yeni yüksek detaylı şeffaf assetlerle yenilendi.
- 12 dilimli WheelEngine/WheelConfig yapısı aynen korundu; sadece görsel katman cilalandı.
- Çark göbeği büyütüldü, yeni geniş ibrenin oranı assetin gerçek en-boy oranından okunuyor.
- Segment ayırıcıları inceltildi, seçili dilim glow/pulse efekti güçlendirildi; frame glow aşırı patlamayacak şekilde dengelendi.
- Menü çarkı hafif büyütüldü; ayarlar ve final için bg_panel kullanılıyor.
- Android build bu çalışma ortamında doğrulanamadı: Gradle dağıtımı ağ erişimi olmadığı için indirilemedi.

## 0.6.0 — Görsel paket (AileCarki_ArtPack_v1) + 12 dilimli çark
- Çark 24 ince dilimden 12 büyük dilime indi (her biri 30°), WheelConfig.DEFAULT:
  100 · 500 · 2X · 300 · 1000 · JOKER · 400 · 750 · İFLAS · 250 · 2000 · SIRA GEÇ (özel dilimler her 3 dilimde bir).
  İFLAS ve SIRA GEÇ olasılığı aynı kaldı (1/12). WheelEngine matematiği değişmedi; ekrandaki dilimler aynı listeden çizilir.
- Çark yazıları büyük ve kalın (Paytone One), "SIRA GEÇ" tek satır (iki satırda yan dilime taşıyordu); dinlenme konumunda sol yarıdaki yazılar ters durmasın
  diye çevrilir. Dilim sınırları net altın çizgi.
- Çarkın sabit parçaları artık görsel: altın çerçeve + ampuller (wheel_frame, yavaşça parlayıp söner), altın yıldızlı göbek
  (wheel_hub), altın ibre (wheel_pointer), kaide (podium).
- Arka planlar görsel: ana menü / ayarlar / final → bg_menu, oyuncu kurulumu + oyun → bg_game (çark kaidesi görselde,
  çarkın dinlenme konumu kaideyle hizalı). Ekran değişince yumuşak geçiş. Kodla çizilen StageBackground artık kullanılmıyor.
- "AİLE ÇARKI" başlığı görsel logo (logo_aile_carki) + nefes alan altın hale ve parlayıp sönen ampuller. "FİNAL" başlığı kodla.
- TV launcher banner ve uygulama ikonu yeni görsellerden üretildi.
- Eski kayıtlar: 24 dilimli çarkla kaydedilmiş, çark aşamasında kalmış oyun "sıra sende, çarkı çevir" durumuna döner
  (SavedGame.wheelSize + GameEngine.sanitizeForResume). Test eklendi (57/57).
- Görseller res/drawable-nodpi altında WebP (toplam ~630 KB). Kaynak PNG'ler kullanıcının ChatGPT ile ürettiği ArtPack'ten.

## 0.5.3 — Hakem paneli bulmacanın üstüne binmiyor (TV geri bildirimi)
- Hakem paneli açıkken ekranlar sağda panel kadar alan boşaltıyor (REFEREE_RESERVED_WIDTH):
  final ekranında bulmaca + harf şeridi + süre/CEVABI SÖYLE sola kayıp küçülüyor; normal turda orta sütun daralıyor.
  Harf kutuları otomatik ölçeklendiği için uzun cevaplar da panelin altına girmiyor. Geçiş 300 ms animasyonlu.
- Hakem paneli 262 → 280dp; TAMAM / DEVAM butonları eşit genişlikte, daha az iç boşluk → yazılar artık kesilmiyor
  (TV'de "TAMA" / "DEVA" görünüyordu). TvButton'a isteğe bağlı contentPadding eklendi (varsayılan aynı).

## 0.5.2 — Final ekranında harf bilgisi belirginleştirildi (TV geri bildirimi)
- Silik "Verilen harfler: R S T L N E" yazısı yerine altın çerçeveli bilgi şeridi (ui/components/FinalLettersBar.kt):
  VERİLEN HARFLER → harf kartelası gibi altın kutucuklar; SENİN HARFLERİN → 3 ünsüz + 1 sesli yuvası
  (boşken kesik çizgili "?", seçildikçe altın kutu ve harf). Çözüm sırasında da görünür kalır.
- "3 ÜNSÜZ + 1 SESLİ SEÇ (ünsüz 0/3 · sesli 0/1)" yerine altın yazıyla "3 ÜNSÜZ + 1 SESLİ HARF SEÇ".
- Değişen dosyalar: FinalScreen.kt, yeni FinalLettersBar.kt, strings.xml. Oyun motoru aynı.

## 0.5.1 — Sıra göstergesi belirginleştirildi (TV geri bildirimi)
- Sıradaki oyuncunun kartı: kalın altın çerçeve, yavaşça yanıp söner (kalınlık 5→8dp ve parlaklık nabız gibi, ~0,75 sn),
  dış altın hale de onunla birlikte parlayıp söner. Animasyon sadece çizimde okunur (recomposition yok).
- Oyuncu kartları artık sabit genişlikte (en fazla 230dp) ve ortalı: 2 oyuncuda kartlar ekranı boydan boya kaplamıyor.
- Sadece ui/components/PlayerScoreCard.kt değişti. Oyun motoru, sesler, hakem paneli aynı.

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

## ULTRA TV polish — active turn + celebration + wheel depth
- Çark yazıları güvenli halkaya alındı; dış çember/merkez göbek tarafından yenme riski azaltıldı.
- Çark segmentlerine daha güçlü 3D gradient, iç metal halkalar, seçili dilim glow'u ve daha belirgin LED halesi eklendi.
- Aktif oyuncu kartına kartın ÜSTÜNDE yanıp sönen `SIRA SENDE` etiketi eklendi; aktif isim ve puan büyütüldü.
- Çark puanı sonrası bilgi paneli artık aktif oyuncuyu büyük puntoda gösteriyor (`HALA HARF SEÇ`).
- Tur sonu ekranı tam kutlama sahnesine çevrildi: hareketli konfeti, havai fişek ve iki yan kıvılcım fıskiyesi; kazanan tek ve büyük kartla öne çıkarılıyor.
- Büyük final kazanma ekranı daha özel hale getirildi: `ŞAMPİYON <isim>` marquee paneli, yalnızca kazananın büyük puan kartı, sürekli hareketli kutlama efektleri.
- `SceneBackground` üzerindeki uyumsuz `filterQuality` parametresi kaldırıldı (Compose painter Image build uyumluluğu).

## 2026-09-27 — Sürpriz zarf + eksik harfli çözüm
- Büyük final kazanıldıktan sonra `ÖDÜL ZARFLARI` aşaması eklendi.
- Her oyunda üç ödül üç zarfa rastgele dağıtılıyor: Dilek Hakkı, Ceza Hakkı, 1.000 TL Ödül ev kuralı.
- Zarf ekranı TV kumandasıyla tam odak/OK desteğine sahip ve seçilen zarf kutlama efektiyle açılıyor.
- `YAZARAK GİR` artık cevabı baştan istemiyor: daha önce açılmış harfler sabit geliyor, yalnızca kapalı kutular dolduruluyor.
- Aynı eksik-harf sistemi normal tur çözümünde ve final çözümünde kullanılıyor.
- `PartialAnswerComposer` ve unit testleri eklendi.

## 2026-09-27 — Sessiz Fâtiha / anma açılışı
- Uygulama artık her yeni açılışta ana menüden önce özel aile anma ekranını gösterir.
- Metin, başta kıymetli babamız Muammer Uğurluel olmak üzere ebediyete uğurlanan aile büyüklerini sevgi, özlem ve rahmetle anar.
- Ekranda otomatik seslendirme ve yarışma müziği bilinçli olarak yoktur; aile Fâtiha'sını okuduktan sonra yalnızca `ÂMİN` butonuna basar.
- `ÂMİN` varsayılan odaktır ve basıldığında ana menüye geçilir; normal Aile Çarkı müziği bundan sonra başlar.
- Anma ekranı oyun içi state, kayıt, skor, zarf ödülleri, hakem sistemi ve ses paketine dokunmaz.
