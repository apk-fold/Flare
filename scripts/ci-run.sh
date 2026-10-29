#!/usr/bin/env bash
# Lance une commande de compilation. En cas d'échec, publie les lignes d'erreur et la fin du journal
# en annotation GitHub Actions (visible directement sur la page de la compilation).
name="$1"
shift
log="$(mktemp)"
"$@" 2>&1 | tee "$log"
code=${PIPESTATUS[0]}
if [ "$code" -ne 0 ]; then
  {
    grep -E "error|Error|ERROR|FAILED|What went wrong|✖|Cannot|not found" "$log" | grep -v "^\s*at " | head -n 40
    echo "----- fin du journal -----"
    tail -n 30 "$log"
  } | sed -e 's/%/%25/g' -e 's/\r//g' | awk 'BEGIN{ORS="%0A"} {print}' > "$log.msg"
  echo "::error title=${name} (code ${code})::$(cat "$log.msg")"
fi
exit "$code"
