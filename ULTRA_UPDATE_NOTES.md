# Aile Çarkı — ULTRA TV polish

Bu paket son PRO kaynak sürümünün üzerine hazırlanmıştır.

## Görsel / oyun içi güncellemeler
- Çark yazıları dış çember ve göbekten daha uzağa alındı; otomatik sığdırma güvenli alanı daraltıldı.
- Çark segmentleri daha derin 3D gradient aldı; iç metal halkalar ve daha güçlü LED halesi eklendi.
- Seçili/duran dilim glow'u güçlendirildi; göbek ve ibre biraz daha tok hale getirildi.
- Aktif oyuncunun kartının üstüne yanıp sönen `SIRA SENDE` etiketi taşındı.
- Aktif oyuncu ismi ve puanı daha büyük/parlak; kartın altın glow'u daha belirgin.
- Çark sonucu / harf seç ekranında aktif oyuncu adı büyük gösteriliyor (`HALA HARF SEÇ`).
- Tur sonu ekranı daha güçlü: tek büyük kazanan kartı + hareketli konfeti + havai fişek + yan kıvılcım fıskiyeleri.
- Büyük final kazanma ekranı daha özel: yalnızca şampiyonun büyük puan kartı, büyük `ŞAMPİYON <isim>` paneli ve sürekli hareketli kutlama efektleri.
- `SceneBackground` Compose uyumsuz `filterQuality` parametresi kaldırıldı.

## Korunanlar
- WheelEngine / puan / tur / final kuralları değiştirilmedi.
- 12 dilimli çark düzeni korunur.
- Sesler, hakemli hızlı çöz, 2/3/4 oyuncu seçimi ve mevcut navigasyon korunur.

## Build
Repo içindeki `.github/workflows/build-apk.yml` GitHub Actions üzerinde JDK 17 + Gradle 8.9 ile unit test ve debug APK build alır.
