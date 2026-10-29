# Clé de signature de l'APK

`flare.jks` (PKCS12, alias `flare`, mot de passe `flare-apk-fold`) signe toutes les versions de l'APK pour que les
mises à jour s'installent par-dessus la version précédente.

Elle est publique : n'importe qui peut signer un APK avec. Pour une clé privée, voir la section « Signature de
l'APK » du README (secrets GitHub `FLARE_KEYSTORE_B64`, `FLARE_KEYSTORE_PASSWORD`, `FLARE_KEY_ALIAS`,
`FLARE_KEY_PASSWORD`).
