<div align="center">

[English](README_en.md) · [简体中文](README_zh.md) · [繁體中文](README_zh-TW.md) · [日本語](README_ja.md) · [한국어](README_ko.md) · [Türkçe](README_tr.md) · [Русский](README_ru.md) · [Tiếng Việt](README_vi.md) · [ไทย](README_th.md) · [Deutsch](README_de.md) · [Español](README_es.md) · [Français](../../README.md) · [Українська](README_uk.md) · [Polski](README_pl.md) · **Português (Brasil)** · [العربية](README_ar.md) · [فارسی](README_fa.md) · [Bahasa Indonesia](README_id.md) · [Italiano](README_it.md)
· [Ελληνικά](README_el.md) · [മലയാളം](README_ml.md) · [Roman Urdu](README_ur-Latn.md) · [Română](README_ro.md)

</div>

<p align="center"><img src="../../build/icon.png" width="120" alt="Flare"></p>

# Flare

Automações para **Android**, no espírito do app Atalhos da Apple, mas mais completas que os "Modos e rotinas" da
Samsung: **um gatilho** (dispositivo Bluetooth conectado, Wi-Fi, carregador, horário, app aberto, celular aberto…),
**condições** opcionais e depois **uma sequência de ações** (configurações, apps, música, comandos…), além de ações
"quando terminar" que desfazem tudo na hora certa.

Exemplo incluído no app: quando o celular se conecta ao carro por Bluetooth, o Flare ativa o Não perturbe, ajusta o
volume, inicia a música no [Opus](https://github.com/apkangelo/opus) e abre o Maps; ao desconectar, pausa a música e
desativa o Não perturbe.

Feito para o Galaxy Z Fold com One UI: gatilhos "celular dobrado / aberto", e sem root (a Samsung não permite mais
desbloquear o bootloader) graças ao Shizuku.

## Baixar

**[Flare.apk](https://github.com/apkangelo/Flare/releases/latest/download/Flare.apk)** (Android 8 ou mais recente).
Abra o arquivo, permita a instalação desta fonte e toque em *Instalar*. As atualizações são instaladas por cima sem
perder suas automações. Todas as versões ficam na aba [*Releases*](https://github.com/apkangelo/Flare/releases),
compiladas pelo GitHub Actions a cada alteração.

## Capturas de tela

| Automações | Editor | Registro | Permissões |
|---|---|---|---|
| <img src="../screenshots/automations.png" width="200"> | <img src="../screenshots/editor.png" width="200"> | <img src="../screenshots/log.png" width="200"> | <img src="../screenshots/rights.png" width="200"> |

## O que o Flare faz

**Gatilhos (27)**: dispositivo Bluetooth conectado / desconectado (basta parte do nome: "Media Nav" reconhece
"MEDIA-NAV"), Wi-Fi conectado / desconectado, modo avião, carregador conectado / desconectado, nível da bateria,
celular ligado, tela ligada / desligada, desbloqueio, **celular dobrado / aberto**, fones conectados / desconectados,
horário e dias, a cada X minutos, app aberto / fechado, notificação recebida (app, texto), chamada recebida /
iniciada / encerrada, SMS recebido, chegada a um lugar / saída.

**Condições (11)**: faixa de horário, dias da semana, bateria, carregando, dispositivo Bluetooth conectado, rede Wi-Fi,
dobrado / aberto, fones, tela ligada, modo de toque, app na tela.

**Ações (40)**
- *Conexões*: Wi-Fi, dados móveis, Bluetooth, modo avião, NFC, localização (ligar, desligar, inverter).
- *Tela*: brilho, tempo limite da tela, giro, tema escuro, manter ligada ao carregar, economia de energia, lanterna,
  bloquear, ligar a tela, captura de tela.
- *Som e mídia*: modo de toque, Não perturbe, volume de cada canal, tocar / pausar / próxima, iniciar a música em um
  app específico, ler um texto em voz alta, vibrar.
- *Apps*: abrir um app, um link ou um deep link (`opus://flow`, `tel:`, `geo:`…), navegação no Maps, forçar parada,
  abrir uma página das configurações, enviar um intent.
- *Controle da tela*: botões do sistema (voltar, início, recentes, painel de notificações, configurações rápidas, menu
  liga/desliga, tela dividida), tocar em um texto na tela, limpar notificações.
- *Ligações e SMS*: enviar SMS, ligar.
- *Sistema*: notificação, **comando shell**, **qualquer configuração do sistema** (`system`, `secure`, `global`), o que
  cobre também as configurações próprias da One UI.
- *Fluxo*: aguardar, executar um atalho, ativar / desativar uma automação.

**Atalhos**: listas de ações sem gatilho, iniciadas com um toque no app, na **tela inicial** ou em um dos **3 blocos
das configurações rápidas**.

**Registro** de tudo o que foi executado (com o resultado de cada ação), **exportação / importação** em JSON, interface
em **23 idiomas**.

## As permissões, passo a passo

O Flare nunca precisa de root. Cada ação mostra o acesso que falta, e a aba **Permissões** reúne tudo.

1. **Opções do desenvolvedor**: Configurações › Sobre o telefone › Informações do software, toque 7 vezes em *Número
   de compilação*.
2. **Shizuku**: instale o [Shizuku](https://shizuku.rikka.app/download/), ative a *Depuração sem fio* nas opções do
   desenvolvedor (no Wi-Fi) e, no Shizuku: *Iniciar pela depuração sem fio* › *Parear*, e digite o código de pareamento
   na notificação.
3. No Flare › **Permissões**: *Shizuku › Conceder* e depois **Conceder tudo**. O Flare concede a si mesmo as permissões
   de sistema permanentes, acessibilidade, acesso às notificações, Não perturbe, sobreposição a outros apps, ausência de
   restrições de bateria e as permissões do Android.
4. Na Samsung: Configurações › Aplicativos › Flare › Bateria › **Sem restrições**.

Depois de reiniciar, o Shizuku precisa ser iniciado de novo (passo 2, sem parear de novo) para as ações de Wi-Fi, dados
móveis, Bluetooth, modo avião, tema escuro e comandos. O resto continua funcionando graças às permissões de sistema
permanentes.

Sem o Shizuku, a partir de um computador:

```sh
adb shell pm grant fr.apkfold.flare android.permission.WRITE_SECURE_SETTINGS
```

Se a acessibilidade ou o acesso às notificações estiver em cinza (app instalado fora da Play Store): Informações do
app › ⋮ › *Permitir configurações restritas*.

## Com o Opus

O exemplo "Modo carro" abre `opus://flow`, que inicia o Flow do [Opus](https://github.com/apkangelo/opus). A ação
*Tocar música em um app* também funciona com qualquer player (Spotify, YouTube Music, Deezer…): usa a sessão de mídia
do app ou envia a tecla "tocar".

## Compilar você mesmo

Você precisa do JDK 21 e do SDK do Android (ou do Android Studio).

```sh
python3 scripts/i18n.py        # gera os textos do Android a partir de i18n/*.json
cd android
./gradlew assembleRelease      # → android/app/build/outputs/apk/release/app-release.apk
```

A cada alteração, o GitHub Actions verifica as 23 traduções, compila e publica o APK. Um segundo workflow roda o app em
um emulador Android 15, simula a conexão do carregador para verificar que o motor reage e faz as capturas acima.

## Assinatura do APK

Todas as versões são assinadas com **a mesma chave** (`signing/flare.jks`) para que as atualizações sejam instaladas
por cima. Essa chave é pública no repositório: instale o Flare só a partir deste repositório. Para usar uma chave
privada, adicione em *Settings › Secrets › Actions* os segredos `FLARE_KEYSTORE_B64` (o arquivo .jks em base64),
`FLARE_KEYSTORE_PASSWORD`, `FLARE_KEY_ALIAS` e `FLARE_KEY_PASSWORD`.

## Bom saber

- Automações, registro e configurações ficam **no celular**. O Flare não tem acesso à internet.
- Os comandos shell rodam com as permissões ADB do Shizuku: cole só comandos que você entende.
- O nome da rede Wi-Fi só pode ser lido com a permissão de localização (regra do Android).

## Organização do repositório

| Pasta | Conteúdo |
|---|---|
| `android/` | O app (Kotlin, Jetpack Compose): motor em `core/`, serviços em `service/`, telas em `ui/` |
| `i18n/` | Os textos da interface nos 23 idiomas |
| `scripts/` | Geração dos textos, teste no emulador, ajuda para compilar |
| `docs/` | Capturas de tela e READMEs traduzidos |
| `signing/` | Chave de assinatura do APK |

## Outros apps apk-fold

- [Haiku](https://github.com/apkangelo/haiku): gerenciador de senhas offline
- [Opus](https://github.com/apkangelo/opus): player de música para Navidrome
- [Sonnet](https://github.com/apkangelo/sonnet): cliente Jellyfin
- [Mythos](https://github.com/apkangelo/mythos): pacote de escritório que sincroniza sem servidor
- [Fable](https://github.com/apkangelo/fable): registro de horas de trabalho
- [Cyber](https://github.com/apkangelo/Cyber): terminal SSH e shell local
