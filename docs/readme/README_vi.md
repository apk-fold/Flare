<div align="center">

[English](README_en.md) · [简体中文](README_zh.md) · [繁體中文](README_zh-TW.md) · [日本語](README_ja.md) · [한국어](README_ko.md) · [Türkçe](README_tr.md) · [Русский](README_ru.md) · **Tiếng Việt** · [ไทย](README_th.md) · [Deutsch](README_de.md) · [Español](README_es.md) · [Français](../../README.md) · [Українська](README_uk.md) · [Polski](README_pl.md) · [Português (Brasil)](README_pt-BR.md) · [العربية](README_ar.md) · [فارسی](README_fa.md) · [Bahasa Indonesia](README_id.md) · [Italiano](README_it.md)
· [Ελληνικά](README_el.md) · [മലയാളം](README_ml.md) · [Roman Urdu](README_ur-Latn.md) · [Română](README_ro.md)

</div>

<p align="center"><img src="../../build/icon.png" width="120" alt="Flare"></p>

# Flare

Tự động hóa cho **Android**, theo tinh thần ứng dụng Phím tắt của Apple nhưng mạnh hơn "Chế độ và Quy trình" của
Samsung: **một bộ kích hoạt** (thiết bị Bluetooth kết nối, Wi-Fi, sạc, giờ, mở ứng dụng, mở điện thoại gập…), các
**điều kiện** tùy chọn, rồi **một chuỗi hành động** (cài đặt, ứng dụng, nhạc, lệnh…), cùng các hành động "khi kết thúc"
hoàn tác mọi thứ đúng lúc.

Ví dụ có sẵn trong ứng dụng: khi điện thoại kết nối Bluetooth với ô tô, Flare bật Không làm phiền, chỉnh âm lượng, phát
nhạc trong [Opus](https://github.com/apkangelo/opus) và mở Maps; khi ngắt kết nối, Flare tạm dừng nhạc và tắt Không
làm phiền.

Thiết kế cho Galaxy Z Fold chạy One UI: bộ kích hoạt "điện thoại gập / mở", và không cần root (Samsung không còn cho mở
khóa bootloader) nhờ Shizuku.

## Tải xuống

**[Flare.apk](https://github.com/apkangelo/Flare/releases/latest/download/Flare.apk)** (Android 8 trở lên). Mở tệp,
cho phép cài đặt từ nguồn này, rồi chọn *Cài đặt*. Bản cập nhật được cài đè lên mà không mất tự động hóa của bạn. Mọi
phiên bản đều ở thẻ [*Releases*](https://github.com/apkangelo/Flare/releases), được GitHub Actions biên dịch sau mỗi
thay đổi.

## Ảnh chụp màn hình

| Tự động hóa | Trình chỉnh sửa | Nhật ký | Quyền |
|---|---|---|---|
| <img src="../screenshots/automations.png" width="200"> | <img src="../screenshots/editor.png" width="200"> | <img src="../screenshots/log.png" width="200"> | <img src="../screenshots/rights.png" width="200"> |

## Flare làm được gì

**Bộ kích hoạt (27)**: thiết bị Bluetooth kết nối / ngắt kết nối (chỉ cần một phần tên: "Media Nav" nhận ra
"MEDIA-NAV"), Wi-Fi kết nối / ngắt, chế độ máy bay, cắm / rút sạc, mức pin, khởi động điện thoại, màn hình bật / tắt,
mở khóa, **điện thoại gập / mở**, cắm / rút tai nghe, giờ và ngày cụ thể, mỗi X phút, mở / đóng ứng dụng, nhận thông
báo (ứng dụng, văn bản), cuộc gọi đến / bắt đầu / kết thúc, nhận SMS, đến / rời một địa điểm.

**Điều kiện (11)**: khoảng giờ, ngày trong tuần, pin, đang sạc, thiết bị Bluetooth đã kết nối, mạng Wi-Fi, gập / mở,
tai nghe, màn hình bật, chế độ chuông, ứng dụng trên màn hình.

**Hành động (40)**
- *Kết nối*: Wi-Fi, dữ liệu di động, Bluetooth, chế độ máy bay, NFC, vị trí (bật, tắt, đảo trạng thái).
- *Màn hình*: độ sáng, thời gian chờ, xoay, chế độ tối, luôn bật khi sạc, tiết kiệm pin, đèn pin, khóa, bật màn hình,
  chụp màn hình.
- *Âm thanh và phương tiện*: chế độ chuông, Không làm phiền, âm lượng từng luồng, phát / tạm dừng / tiếp theo, phát nhạc
  trong một ứng dụng cụ thể, đọc to văn bản, rung.
- *Ứng dụng*: mở ứng dụng, liên kết hoặc deep link (`opus://flow`, `tel:`, `geo:`…), dẫn đường Maps, buộc dừng, mở một
  trang cài đặt, gửi intent.
- *Điều khiển màn hình*: nút hệ thống (quay lại, màn hình chính, gần đây, bảng thông báo, cài đặt nhanh, menu nguồn,
  chia đôi màn hình), chạm vào văn bản trên màn hình, xóa thông báo.
- *Cuộc gọi và SMS*: gửi SMS, gọi điện.
- *Hệ thống*: thông báo, **lệnh shell**, **mọi cài đặt hệ thống** (`system`, `secure`, `global`), bao gồm cả cài đặt
  riêng của One UI.
- *Luồng*: chờ, chạy lối tắt, bật / tắt tự động hóa.

**Lối tắt**: danh sách hành động không có bộ kích hoạt, chạy chỉ với một chạm từ ứng dụng, từ **màn hình chính** hoặc
từ một trong **3 ô cài đặt nhanh**.

**Nhật ký** mọi thứ đã chạy (kèm kết quả từng hành động), **xuất / nhập** JSON, giao diện **23 ngôn ngữ**.

## Cấp quyền từng bước

Flare không bao giờ cần root. Mỗi hành động cho biết quyền nào còn thiếu, và thẻ **Quyền** gom tất cả lại.

1. **Tùy chọn nhà phát triển**: Cài đặt › Thông tin điện thoại › Thông tin phần mềm, chạm 7 lần vào *Số hiệu bản tạo*.
2. **Shizuku**: cài [Shizuku](https://shizuku.rikka.app/download/), bật *Gỡ lỗi không dây* trong tùy chọn nhà phát
   triển (khi có Wi-Fi), rồi trong Shizuku: *Khởi động qua gỡ lỗi không dây* › *Ghép nối*, và nhập mã ghép nối trong
   thông báo.
3. Trong Flare › **Quyền**: *Shizuku › Cấp*, rồi **Cấp tất cả**. Flare tự cấp cho mình quyền hệ thống vĩnh viễn, hỗ
   trợ tiếp cận, truy cập thông báo, Không làm phiền, hiển thị trên ứng dụng khác, không giới hạn pin và các quyền
   Android.
4. Trên Samsung: Cài đặt › Ứng dụng › Flare › Pin › **Không hạn chế**.

Sau khi khởi động lại, cần chạy lại Shizuku (bước 2, không cần ghép nối lại) cho các hành động Wi-Fi, dữ liệu di động,
Bluetooth, chế độ máy bay, chế độ tối và lệnh. Phần còn lại vẫn hoạt động nhờ quyền hệ thống vĩnh viễn.

Không có Shizuku, từ máy tính:

```sh
adb shell pm grant fr.apkfold.flare android.permission.WRITE_SECURE_SETTINGS
```

Nếu hỗ trợ tiếp cận hoặc truy cập thông báo bị mờ (ứng dụng cài ngoài Play Store): Thông tin ứng dụng › ⋮ › *Cho phép
chế độ cài đặt bị hạn chế*.

## Với Opus

Ví dụ "Chế độ ô tô" mở `opus://flow`, khởi chạy Flow của [Opus](https://github.com/apkangelo/opus). Hành động *Phát
nhạc trong một ứng dụng* cũng dùng được với mọi trình phát (Spotify, YouTube Music, Deezer…): qua phiên phương tiện của
ứng dụng, hoặc gửi phím "phát" cho nó.

## Tự biên dịch

Bạn cần JDK 21 và Android SDK (hoặc Android Studio).

```sh
python3 scripts/i18n.py        # tạo lại chuỗi Android từ i18n/*.json
cd android
./gradlew assembleRelease      # → android/app/build/outputs/apk/release/app-release.apk
```

Sau mỗi thay đổi, GitHub Actions kiểm tra 23 bản dịch, biên dịch và phát hành APK. Một workflow thứ hai chạy ứng dụng
trên trình giả lập Android 15, giả lập cắm sạc để kiểm tra bộ máy phản hồi, và chụp các ảnh màn hình ở trên.

## Chữ ký APK

Mọi phiên bản được ký bằng **cùng một khóa** (`signing/flare.jks`) để bản cập nhật cài đè được. Khóa này công khai
trong kho: chỉ cài Flare từ kho này. Để dùng khóa riêng, thêm vào *Settings › Secrets › Actions* các secret
`FLARE_KEYSTORE_B64` (tệp .jks dạng base64), `FLARE_KEYSTORE_PASSWORD`, `FLARE_KEY_ALIAS` và `FLARE_KEY_PASSWORD`.

## Nên biết

- Tự động hóa, nhật ký và cài đặt đều nằm **trên điện thoại**. Flare không có quyền truy cập Internet.
- Lệnh shell chạy với quyền ADB của Shizuku: chỉ dán những lệnh bạn hiểu.
- Tên mạng Wi-Fi chỉ đọc được khi có quyền vị trí (quy định của Android).

## Cấu trúc kho

| Thư mục | Nội dung |
|---|---|
| `android/` | Ứng dụng (Kotlin, Jetpack Compose): bộ máy trong `core/`, dịch vụ trong `service/`, màn hình trong `ui/` |
| `i18n/` | Văn bản giao diện bằng 23 ngôn ngữ |
| `scripts/` | Tạo chuỗi, kiểm thử trên trình giả lập, hỗ trợ biên dịch |
| `docs/` | Ảnh chụp màn hình và README đã dịch |
| `signing/` | Khóa ký APK |

## Các ứng dụng apk-fold khác

- [Haiku](https://github.com/apkangelo/haiku): trình quản lý mật khẩu ngoại tuyến
- [Opus](https://github.com/apkangelo/opus): trình phát nhạc cho Navidrome
- [Sonnet](https://github.com/apkangelo/sonnet): ứng dụng khách Jellyfin
- [Mythos](https://github.com/apkangelo/mythos): bộ ứng dụng văn phòng đồng bộ không cần máy chủ
- [Fable](https://github.com/apkangelo/fable): theo dõi giờ làm việc
- [Cyber](https://github.com/apkangelo/Cyber): terminal SSH và shell cục bộ
