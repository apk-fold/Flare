#!/usr/bin/env python3
"""Ajoute (ou remplace) la barre de langues en tête de chaque README traduit (docs/readme/README_xx.md)."""
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
LANGS = [
    ("en", "English"), ("zh", "简体中文"), ("zh-TW", "繁體中文"), ("ja", "日本語"), ("ko", "한국어"),
    ("tr", "Türkçe"), ("ru", "Русский"), ("vi", "Tiếng Việt"), ("th", "ไทย"), ("de", "Deutsch"),
    ("es", "Español"), ("fr", "Français"), ("uk", "Українська"), ("pl", "Polski"),
    ("pt-BR", "Português (Brasil)"), ("ar", "العربية"), ("fa", "فارسی"), ("id", "Bahasa Indonesia"),
    ("it", "Italiano"), ("el", "Ελληνικά"), ("ml", "മലയാളം"), ("ur-Latn", "Roman Urdu"), ("ro", "Română"),
]


def bar(current: str) -> str:
    parts = []
    for code, label in LANGS:
        if code == current:
            parts.append(f"**{label}**")
        elif code == "fr":
            parts.append(f"[{label}](../../README.md)")
        else:
            parts.append(f"[{label}](README_{code}.md)")
    first, second = parts[:19], parts[19:]
    return '<div align="center">\n\n' + " · ".join(first) + "\n· " + " · ".join(second) + "\n\n</div>\n"


for code, _ in LANGS:
    if code == "fr":
        continue
    p = ROOT / "docs" / "readme" / f"README_{code}.md"
    text = p.read_text(encoding="utf-8")
    if text.startswith('<div align="center">'):
        text = text.split("</div>\n", 1)[1].lstrip("\n")
    p.write_text(bar(code) + "\n" + text, encoding="utf-8")
print("barres de langues mises à jour")
