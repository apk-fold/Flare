package fr.apkfold.flare.core

import android.content.Context
import android.content.pm.PackageManager
import rikka.shizuku.Shizuku
import java.lang.reflect.Method
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

/**
 * Commandes avec les droits ADB (Shizuku) ou root (su).
 * Shizuku lancé par le débogage sans fil donne l'identité « shell » : svc, cmd, settings, pm grant, appops…
 */
object Shell {
    const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
    const val SHIZUKU_REQUEST = 4242

    enum class State { NOT_INSTALLED, NOT_RUNNING, NO_PERMISSION, READY }

    data class Result(val code: Int, val out: String, val err: String) {
        val ok: Boolean get() = code == 0
        val message: String get() = (err.ifBlank { out }).trim().take(300)
    }

    fun shizukuState(ctx: Context): State {
        val alive = try {
            Shizuku.pingBinder()
        } catch (_: Throwable) {
            false
        }
        if (!alive) return if (isInstalled(ctx, SHIZUKU_PACKAGE)) State.NOT_RUNNING else State.NOT_INSTALLED
        return try {
            if (Shizuku.isPreV11()) State.NO_PERMISSION
            else if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) State.READY
            else State.NO_PERMISSION
        } catch (_: Throwable) {
            State.NO_PERMISSION
        }
    }

    fun requestShizuku(): Boolean = try {
        if (Shizuku.pingBinder() && !Shizuku.isPreV11()) {
            Shizuku.requestPermission(SHIZUKU_REQUEST)
            true
        } else false
    } catch (_: Throwable) {
        false
    }

    /** Vrai si une commande privilégiée peut être lancée maintenant. */
    fun available(ctx: Context): Boolean = shizukuState(ctx) == State.READY || Store.useRoot

    fun isInstalled(ctx: Context, pkg: String): Boolean = try {
        ctx.packageManager.getPackageInfo(pkg, 0)
        true
    } catch (_: Exception) {
        false
    }

    private val newProcess: Method by lazy {
        Shizuku::class.java.getDeclaredMethod(
            "newProcess", Array<String>::class.java, Array<String>::class.java, String::class.java,
        ).apply { isAccessible = true }
    }

    /** Lance la commande ; null si ni Shizuku ni root ne sont disponibles. */
    fun run(ctx: Context, cmd: String, timeoutMs: Long = 20_000): Result? {
        return try {
            when {
                shizukuState(ctx) == State.READY -> {
                    val p = newProcess.invoke(null, arrayOf("sh", "-c", cmd), null, null) as Process
                    collect(p, timeoutMs)
                }
                Store.useRoot -> collect(Runtime.getRuntime().exec(arrayOf("su", "-c", cmd)), timeoutMs)
                else -> null
            }
        } catch (e: Throwable) {
            val cause = e.cause ?: e
            Result(-1, "", cause.message ?: cause.javaClass.simpleName)
        }
    }

    private fun collect(p: Process, timeoutMs: Long): Result {
        try {
            p.outputStream.close()
        } catch (_: Exception) {
        }
        val out = StringBuffer()
        val err = StringBuffer()
        val t1 = thread(isDaemon = true) {
            try {
                out.append(p.inputStream.bufferedReader().readText())
            } catch (_: Exception) {
            }
        }
        val t2 = thread(isDaemon = true) {
            try {
                err.append(p.errorStream.bufferedReader().readText())
            } catch (_: Exception) {
            }
        }
        val done = AtomicBoolean(false)
        val killer = thread(isDaemon = true) {
            try {
                Thread.sleep(timeoutMs)
                if (!done.get()) p.destroy()
            } catch (_: InterruptedException) {
            }
        }
        val code = try {
            p.waitFor()
        } catch (_: InterruptedException) {
            -1
        }
        done.set(true)
        killer.interrupt()
        t1.join(2000)
        t2.join(2000)
        return Result(code, out.toString(), err.toString())
    }

    /** Met une valeur entre apostrophes pour le shell. */
    fun q(s: String): String = "'" + s.replace("'", "'\\''") + "'"
}
