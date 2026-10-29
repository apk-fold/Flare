<div align="center">

[English](README_en.md) · [简体中文](README_zh.md) · [繁體中文](README_zh-TW.md) · [日本語](README_ja.md) · **한국어** · [Türkçe](README_tr.md) · [Русский](README_ru.md) · [Tiếng Việt](README_vi.md) · [ไทย](README_th.md) · [Deutsch](README_de.md) · [Español](README_es.md) · [Français](../../README.md) · [Українська](README_uk.md) · [Polski](README_pl.md) · [Português (Brasil)](README_pt-BR.md) · [العربية](README_ar.md) · [فارسی](README_fa.md) · [Bahasa Indonesia](README_id.md) · [Italiano](README_it.md)
· [Ελληνικά](README_el.md) · [മലയാളം](README_ml.md) · [Roman Urdu](README_ur-Latn.md) · [Română](README_ro.md)

</div>

<p align="center"><img src="../../build/icon.png" width="120" alt="Flare"></p>

# Flare

**Android**용 자동화 앱입니다. Apple의 '단축어'와 같은 발상이지만 삼성의 '모드 및 루틴'보다 훨씬 강력합니다. **트리거**
(블루투스 기기 연결, Wi-Fi, 충전기, 시간, 앱 열림, 휴대전화 펼침…), 선택적인 **조건**, 그리고 **일련의 동작**(설정, 앱,
음악, 명령…)에 더해, 알맞은 때에 모든 것을 되돌리는 '끝날 때' 동작도 지정할 수 있습니다.

앱에 포함된 예시: 휴대전화가 블루투스로 차에 연결되면 Flare가 방해 금지를 켜고, 음량을 맞추고,
[Opus](https://github.com/apkangelo/opus)에서 음악을 재생한 뒤 지도를 엽니다. 연결이 끊기면 음악을 일시정지하고 방해
금지를 끕니다.

One UI를 쓰는 Galaxy Z Fold를 위해 만들었습니다. '휴대전화 접음 / 펼침' 트리거를 지원하고, Shizuku 덕분에 루팅이 필요
없습니다(삼성은 더 이상 부트로더 잠금 해제를 허용하지 않습니다).

## 다운로드

**[Flare.apk](https://github.com/apkangelo/Flare/releases/latest/download/Flare.apk)** (Android 8 이상). 파일을 열고
이 출처의 설치를 허용한 뒤 *설치*를 누르세요. 업데이트는 자동화를 잃지 않고 기존 버전 위에 설치됩니다. 모든 버전은
[*Releases*](https://github.com/apkangelo/Flare/releases) 탭에 있으며, 변경할 때마다 GitHub Actions가 빌드합니다.

## 스크린샷

| 자동화 | 편집기 | 기록 | 권한 |
|---|---|---|---|
| <img src="../screenshots/automations.png" width="200"> | <img src="../screenshots/editor.png" width="200"> | <img src="../screenshots/log.png" width="200"> | <img src="../screenshots/rights.png" width="200"> |

## Flare로 할 수 있는 일

**트리거(27개)**: 블루투스 기기 연결 / 해제(이름 일부만으로 충분: 'Media Nav'로 'MEDIA-NAV'를 인식), Wi-Fi 연결 / 해제,
비행기 모드, 충전기 연결 / 분리, 배터리 잔량, 휴대전화 켜짐, 화면 켜짐 / 꺼짐, 잠금 해제, **휴대전화 접음 / 펼침**, 이어폰
연결 / 분리, 정해진 시간과 요일, X분마다, 앱 열림 / 닫힘, 알림 수신(앱, 텍스트), 전화 수신 / 통화 시작 / 통화 종료, SMS
수신, 장소 도착 / 출발.

**조건(11개)**: 시간대, 요일, 배터리, 충전 중, 블루투스 기기 연결됨, Wi-Fi 네트워크, 접힘 / 펼침, 이어폰, 화면 켜짐,
벨소리 모드, 화면의 앱.

**동작(40개)**
- *연결*: Wi-Fi, 모바일 데이터, 블루투스, 비행기 모드, NFC, 위치(켜기, 끄기, 전환).
- *화면*: 밝기, 화면 자동 꺼짐 시간, 자동 회전, 다크 모드, 충전 중 화면 켜짐 유지, 절전 모드, 손전등, 잠금, 화면 켜기,
  스크린샷.
- *소리 및 미디어*: 벨소리 모드, 방해 금지, 종류별 음량, 재생 / 일시정지 / 다음, 특정 앱에서 음악 재생, 텍스트 소리 내어
  읽기, 진동.
- *앱*: 앱·링크·딥 링크 열기(`opus://flow`, `tel:`, `geo:`…), 지도 길안내, 강제 중지, 설정 페이지 열기, 인텐트 보내기.
- *화면 조작*: 시스템 버튼(뒤로, 홈, 최근 앱, 알림 패널, 빠른 설정, 전원 메뉴, 화면 분할), 화면의 텍스트 탭, 알림 지우기.
- *통화 및 SMS*: SMS 보내기, 전화 걸기.
- *시스템*: 알림, **셸 명령**, **모든 시스템 설정**(`system`, `secure`, `global`) — One UI 고유 설정도 포함됩니다.
- *흐름*: 대기, 바로가기 실행, 자동화 켜기 / 끄기.

**바로가기**: 트리거가 없는 동작 목록으로, 앱이나 **홈 화면**, **빠른 설정 타일 3개** 중 하나에서 한 번의 탭으로
실행합니다.

실행된 모든 항목의 **기록**(동작별 결과 포함), JSON **내보내기 / 가져오기**, **23개 언어** 인터페이스.

## 권한 설정 단계

Flare는 루팅이 전혀 필요 없습니다. 각 동작에는 부족한 권한이 표시되고, **권한** 탭에서 모두 관리할 수 있습니다.

1. **개발자 옵션**: 설정 › 휴대전화 정보 › 소프트웨어 정보에서 *빌드번호*를 7번 누르세요.
2. **Shizuku**: [Shizuku](https://shizuku.rikka.app/download/)를 설치하고 개발자 옵션에서 *무선 디버깅*을 켭니다
   (Wi-Fi 연결 상태). 그런 다음 Shizuku에서 *무선 디버깅으로 시작* › *페어링*을 선택하고 알림에 페어링 코드를
   입력하세요.
3. Flare › **권한**에서 *Shizuku › 허용*을 누른 뒤 **모두 허용**을 누르세요. Flare가 영구 시스템 권한, 접근성, 알림 접근
   권한, 방해 금지, 다른 앱 위에 표시, 배터리 제한 없음, Android 권한을 스스로 부여합니다.
4. 삼성 기기: 설정 › 애플리케이션 › Flare › 배터리 › **제한 없음**.

재부팅한 뒤에는 Wi-Fi, 모바일 데이터, 블루투스, 비행기 모드, 다크 모드, 명령 동작을 위해 Shizuku를 다시 시작해야 합니다
(2단계, 다시 페어링할 필요 없음). 나머지는 영구 시스템 권한 덕분에 계속 작동합니다.

Shizuku 없이 컴퓨터에서:

```sh
adb shell pm grant fr.apkfold.flare android.permission.WRITE_SECURE_SETTINGS
```

접근성이나 알림 접근 권한이 회색으로 표시되면(Play 스토어 외부에서 설치한 앱): 앱 정보 › ⋮ › *제한된 설정 허용*.

## Opus와 함께

'차량 모드' 예시는 `opus://flow`를 열어 [Opus](https://github.com/apkangelo/opus)의 Flow를 시작합니다. *앱에서 음악
재생* 동작은 다른 플레이어(Spotify, YouTube Music, Deezer…)에서도 작동합니다. 앱의 미디어 세션을 이용하거나 '재생' 키를
보냅니다.

## 직접 빌드하기

JDK 21과 Android SDK(또는 Android Studio)가 필요합니다.

```sh
python3 scripts/i18n.py        # i18n/*.json에서 Android 문자열 생성
cd android
./gradlew assembleRelease      # → android/app/build/outputs/apk/release/app-release.apk
```

변경할 때마다 GitHub Actions가 23개 번역을 검사하고 APK를 빌드해 게시합니다. 두 번째 workflow는 Android 15 에뮬레이터에서
앱을 실행하고, 충전기 연결을 시뮬레이션해 엔진이 반응하는지 확인한 뒤 위의 스크린샷을 찍습니다.

## APK 서명

모든 버전은 업데이트가 기존 버전 위에 설치되도록 **같은 키**(`signing/flare.jks`)로 서명됩니다. 이 키는 저장소에 공개되어
있으니 Flare는 이 저장소에서만 설치하세요. 개인 키를 쓰려면 *Settings › Secrets › Actions*에 `FLARE_KEYSTORE_B64`
(base64로 인코딩한 .jks 파일), `FLARE_KEYSTORE_PASSWORD`, `FLARE_KEY_ALIAS`, `FLARE_KEY_PASSWORD`를 추가하세요.

## 알아 두면 좋은 점

- 자동화, 기록, 설정은 모두 **휴대전화에만** 저장됩니다. Flare는 인터넷에 접근하지 않습니다.
- 셸 명령은 Shizuku의 ADB 권한으로 실행됩니다. 이해하는 명령만 붙여 넣으세요.
- Wi-Fi 네트워크 이름은 위치 권한이 있어야 읽을 수 있습니다(Android 규칙).

## 저장소 구조

| 폴더 | 내용 |
|---|---|
| `android/` | 앱(Kotlin, Jetpack Compose): 엔진은 `core/`, 서비스는 `service/`, 화면은 `ui/` |
| `i18n/` | 23개 언어의 인터페이스 문구 |
| `scripts/` | 문자열 생성, 에뮬레이터 테스트, 빌드 도우미 |
| `docs/` | 스크린샷과 번역된 README |
| `signing/` | APK 서명 키 |

## 다른 apk-fold 앱

- [Haiku](https://github.com/apkangelo/haiku): 오프라인 비밀번호 관리자
- [Opus](https://github.com/apkangelo/opus): Navidrome용 음악 플레이어
- [Sonnet](https://github.com/apkangelo/sonnet): Jellyfin 클라이언트
- [Mythos](https://github.com/apkangelo/mythos): 서버 없이 동기화되는 오피스 제품군
- [Fable](https://github.com/apkangelo/fable): 근무 시간 기록
- [Cyber](https://github.com/apkangelo/Cyber): SSH 터미널과 로컬 셸
