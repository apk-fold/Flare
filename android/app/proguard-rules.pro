# Shizuku : la création de processus passe par une méthode privée appelée par réflexion
-keep class rikka.shizuku.** { *; }
-keep class moe.shizuku.** { *; }
-dontwarn rikka.shizuku.**

# Services, récepteurs et tuiles déclarés dans le manifeste
-keep class fr.apkfold.flare.** { *; }

-dontwarn org.jetbrains.annotations.**
