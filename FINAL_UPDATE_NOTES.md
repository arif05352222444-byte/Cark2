# Aile Çarkı — Final Ek Güncelleme

## Yeni: Sürpriz Zarf Seremonisi
Finali kazanan oyuncu için şampiyon ekranından sonra `ÖDÜL ZARFLARI` aşaması bulunur. Üç ödül her oyun sonunda üç zarf arasında yeniden karıştırılır:

1. **Dilek Hakkı** — Şampiyon kaybeden oyunculardan bir dilek ister; oyun başında kabul edilen sınırlar içinde diğer oyuncular “EVET” der.
2. **Ceza Hakkı** — Şampiyon kaybedenler için eğlenceli bir ev cezası seçer.
3. **1.000 TL Ödül** — Ev kuralı olarak kaybeden her oyuncunun şampiyona 1.000 TL vermesini ekranda bildirir. Uygulama ödeme işlemi yapmaz; yalnızca oyun sonucunu gösterir.

## Yeni: Eksik Harfleri Gir
Hakem penceresindeki **YAZARAK GİR** artık tüm cevabı baştan yazdırmaz. Bulmacada daha önce açılmış harfler ekranda sabit tutulur. Oyuncu yalnızca boş kutuların harflerini sırayla girer; uygulama tam cevabı kendisi birleştirip mevcut cevap motoruna gönderir.

Örnek:

`İ _ _ A _ _ U _`  → oyuncu yalnızca eksik harfleri girer.

Bu sistem hem normal turlarda hem finalde kullanılır.

## Build notu
Bu çalışma ortamında Gradle dağıtımı ağa çıkamadığı için gerçek Android build burada alınamadı. GitHub Actions veya Android Studio ile `testDebugUnitTest` ve `assembleDebug` çalıştırılmalıdır.
