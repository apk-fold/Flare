#!/usr/bin/env python3
"""
Traductions de Flare.

Source : i18n/<langue>.json (une clé → un texte). L'anglais (en.json) est la référence.
Ce script génère android/app/src/main/res/values*/strings.xml et res/xml/locales_config.xml.

  python3 scripts/i18n.py           génère les fichiers Android
  python3 scripts/i18n.py --check   vérifie les clés, les %1$s… et que les fichiers générés sont à jour (CI)
"""
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT / "i18n"
RES = ROOT / "android" / "app" / "src" / "main" / "res"

# langue du dépôt → (dossier Android, étiquette BCP 47)
LANGS = {
    "en": ("values", "en"),
    "fr": ("values-fr", "fr"),
    "zh": ("values-b+zh+Hans", "zh-Hans"),
    "zh-TW": ("values-b+zh+Hant", "zh-Hant"),
    "ja": ("values-ja", "ja"),
    "ko": ("values-ko", "ko"),
    "tr": ("values-tr", "tr"),
    "ru": ("values-ru", "ru"),
    "vi": ("values-vi", "vi"),
    "th": ("values-th", "th"),
    "de": ("values-de", "de"),
    "es": ("values-es", "es"),
    "uk": ("values-uk", "uk"),
    "pl": ("values-pl", "pl"),
    "pt-BR": ("values-pt-rBR", "pt-BR"),
    "ar": ("values-ar", "ar"),
    "fa": ("values-fa", "fa"),
    "id": ("values-in", "id"),
    "it": ("values-it", "it"),
    "el": ("values-el", "el"),
    "ml": ("values-ml", "ml"),
    "ur-Latn": ("values-b+ur+Latn", "ur-Latn"),
    "ro": ("values-ro", "ro"),
}

PH = re.compile(r"%(\d+)\$[sd]")


def esc(s: str) -> str:
    s = s.replace("\\", "\\\\").replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
    s = s.replace("'", "\\'").replace('"', '\\"').replace("\n", "\\n")
    if s.startswith("@") or s.startswith("?"):
        s = "\\" + s
    return s


def placeholders(s: str):
    return sorted(PH.findall(s))


def build_xml(lang: str, data: dict, order: list) -> str:
    out = ['<?xml version="1.0" encoding="utf-8"?>',
           f"<!-- Généré par scripts/i18n.py depuis i18n/{lang}.json : ne pas modifier à la main -->",
           "<resources>"]
    for k in order:
        v = data[k]
        attrs = ""
        if "%" in v and not PH.search(v):
            attrs = ' formatted="false"'
        out.append(f'    <string name="{k}"{attrs}>{esc(v)}</string>')
    out.append("</resources>")
    return "\n".join(out) + "\n"


def build_locales() -> str:
    out = ['<?xml version="1.0" encoding="utf-8"?>',
           "<!-- Généré par scripts/i18n.py : langues proposées dans les réglages Android de l'appli -->",
           '<locale-config xmlns:android="http://schemas.android.com/apk/res/android">']
    for _, (_, tag) in LANGS.items():
        out.append(f'    <locale android:name="{tag}" />')
    out.append("</locale-config>")
    return "\n".join(out) + "\n"


def main():
    check = "--check" in sys.argv
    errors = []
    ref = json.loads((SRC / "en.json").read_text(encoding="utf-8"))
    order = list(ref.keys())
    files = {}
    for lang, (folder, _) in LANGS.items():
        p = SRC / f"{lang}.json"
        if not p.exists():
            if check:
                errors.append(f"{lang} : fichier i18n/{lang}.json absent")
            else:
                print(f"{lang} : pas encore traduit, ignoré")
            continue
        data = json.loads(p.read_text(encoding="utf-8"))
        missing = [k for k in order if k not in data]
        extra = [k for k in data if k not in ref]
        if missing:
            errors.append(f"{lang} : {len(missing)} clé(s) manquante(s) : {', '.join(missing[:12])}")
        if extra:
            errors.append(f"{lang} : clé(s) inconnue(s) : {', '.join(extra[:12])}")
        for k in order:
            if k in data:
                if not str(data[k]).strip():
                    errors.append(f"{lang} : « {k} » est vide")
                elif placeholders(data[k]) != placeholders(ref[k]):
                    errors.append(f"{lang} : « {k} » n'a pas les mêmes %1$s/%1$d que l'anglais")
        if not missing:
            files[RES / folder / "strings.xml"] = build_xml(lang, data, order)
    files[RES / "xml" / "locales_config.xml"] = build_locales()

    if errors:
        print("\n".join(errors))
        sys.exit(1)

    stale = []
    for path, text in files.items():
        if check:
            if not path.exists() or path.read_text(encoding="utf-8") != text:
                stale.append(str(path.relative_to(ROOT)))
        else:
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(text, encoding="utf-8")
    if stale:
        print("Fichiers à régénérer (python3 scripts/i18n.py) :\n  " + "\n  ".join(stale))
        sys.exit(1)
    print(f"{len(LANGS)} langues, {len(order)} textes : OK")


if __name__ == "__main__":
    main()
