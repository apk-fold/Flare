package fr.apkfold.flare.ui

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.apkfold.flare.BuildConfig
import fr.apkfold.flare.R
import fr.apkfold.flare.core.Automation
import fr.apkfold.flare.core.Need
import fr.apkfold.flare.core.Perms
import fr.apkfold.flare.core.Shell
import fr.apkfold.flare.core.Store
import fr.apkfold.flare.service.FlareService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val REPO = "https://github.com/apkangelo/Flare"

private fun open(ctx: Context, i: Intent?) {
    if (i == null) return
    try {
        ctx.startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (_: Exception) {
        try {
            ctx.startActivity(Perms.appDetails(ctx).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (_: Exception) {
        }
    }
}

@Composable
private fun Status(ok: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            if (ok) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (ok) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            stringResource(if (ok) R.string.st_ok else R.string.st_missing),
            style = MaterialTheme.typography.labelLarge,
            color = if (ok) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RightCard(
    title: String,
    desc: String,
    ok: Boolean?,
    button: String? = null,
    onClick: (() -> Unit)? = null,
    extra: (@Composable () -> Unit)? = null,
) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                if (ok != null) Status(ok)
            }
            Spacer(Modifier.height(4.dp))
            Text(desc, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            extra?.invoke()
            if (button != null && onClick != null && ok != true) {
                Spacer(Modifier.height(10.dp))
                FilledTonalButton(onClick = onClick) { Text(button) }
            }
        }
    }
}

@Composable
private fun Hint(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier.padding(top = 6.dp),
    )
}

private fun runtimeAll(): Array<String> {
    val l = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.SEND_SMS,
    )
    if (Build.VERSION.SDK_INT >= 31) l += Manifest.permission.BLUETOOTH_CONNECT
    if (Build.VERSION.SDK_INT >= 33) l += Manifest.permission.POST_NOTIFICATIONS
    return l.toTypedArray()
}

@Composable
fun RightsTab(padding: PaddingValues, snack: SnackbarHostState) {
    val ctx = LocalContext.current
    val tick = LocalTick.current
    val request = LocalRequestPerms.current
    val scope = rememberCoroutineScope()
    var local by remember { mutableIntStateOf(0) }
    var busy by remember { mutableStateOf(false) }
    val key = tick + local

    val shizuku = remember(key) { Shell.shizukuState(ctx) }
    val shell = remember(key) { Shell.available(ctx) }
    val secure = remember(key) { Perms.secureGranted(ctx) }
    fun has(n: Need) = Perms.has(ctx, n)

    val exporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) {
            try {
                ctx.contentResolver.openOutputStream(uri)?.use { it.write(Automation.listToJson(Store.items.value).toByteArray()) }
            } catch (_: Exception) {
            }
        }
    }
    val importer = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val msg = try {
                val text = ctx.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }.orEmpty()
                val list = Automation.listFromJson(text)
                Store.importAll(list)
                FlareService.start(ctx)
                ctx.getString(R.string.imported, list.size)
            } catch (_: Exception) {
                ctx.getString(R.string.import_error)
            }
            scope.launch { snack.showSnackbar(msg) }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = padding.calculateTopPadding() + 8.dp, bottom = padding.calculateBottomPadding() + 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Text(stringResource(R.string.rights_intro), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Shizuku
        item {
            val (label, action) = when (shizuku) {
                Shell.State.NOT_INSTALLED -> stringResource(R.string.btn_install) to {
                    open(ctx, Intent(Intent.ACTION_VIEW, Uri.parse("https://shizuku.rikka.app/download/")))
                }
                Shell.State.NOT_RUNNING -> stringResource(R.string.btn_open) to {
                    open(ctx, ctx.packageManager.getLaunchIntentForPackage(Shell.SHIZUKU_PACKAGE))
                }
                Shell.State.NO_PERMISSION -> stringResource(R.string.btn_grant) to {
                    Shell.requestShizuku()
                    Unit
                }
                Shell.State.READY -> "" to {}
            }
            val stateText = when (shizuku) {
                Shell.State.NOT_INSTALLED -> R.string.st_not_installed
                Shell.State.NOT_RUNNING -> R.string.st_not_running
                Shell.State.NO_PERMISSION -> R.string.st_missing
                Shell.State.READY -> R.string.st_ok
            }
            RightCard(
                title = stringResource(R.string.r_shizuku),
                desc = stringResource(R.string.r_shizuku_desc),
                ok = shizuku == Shell.State.READY,
                button = label.ifBlank { null },
                onClick = action,
                extra = {
                    if (shizuku != Shell.State.READY) Hint(stringResource(stateText))
                    Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.use_root), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Switch(checked = Store.useRoot.also { key }, onCheckedChange = {
                            Store.useRoot = it
                            local++
                        })
                    }
                },
            )
        }

        // Tout accorder
        item {
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.r_grant_all), style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.r_grant_all_desc), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.height(10.dp))
                    Button(enabled = shell && !busy, onClick = {
                        busy = true
                        scope.launch {
                            val (ok, total) = withContext(Dispatchers.IO) { Perms.grantAll(ctx) }
                            busy = false
                            local++
                            FlareService.start(ctx)
                            snack.showSnackbar(ctx.getString(R.string.grant_all_done, ok, total))
                        }
                    }) {
                        if (busy) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        else Text(stringResource(R.string.btn_grant_all))
                    }
                    if (!shell) Hint(stringResource(R.string.grant_all_needs_shizuku))
                }
            }
        }

        // Droits système permanents
        item {
            val cmd = "adb shell pm grant ${ctx.packageName} android.permission.WRITE_SECURE_SETTINGS"
            RightCard(
                title = stringResource(R.string.r_secure),
                desc = stringResource(R.string.r_secure_desc),
                ok = secure,
                button = if (shell) stringResource(R.string.btn_grant) else null,
                onClick = {
                    scope.launch {
                        withContext(Dispatchers.IO) { Shell.run(ctx, "pm grant ${ctx.packageName} android.permission.WRITE_SECURE_SETTINGS") }
                        local++
                    }
                },
                extra = {
                    if (!secure) {
                        Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            SelectionContainer(Modifier.weight(1f)) {
                                Text(cmd, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                            }
                            IconButton(onClick = {
                                ctx.getSystemService(ClipboardManager::class.java).setPrimaryClip(ClipData.newPlainText("adb", cmd))
                                Toast.makeText(ctx, R.string.copied, Toast.LENGTH_SHORT).show()
                            }) { Icon(Icons.Default.ContentCopy, contentDescription = null) }
                        }
                    }
                },
            )
        }

        // Accessibilité
        item {
            val ok = remember(key) { Perms.accessibilityEnabled(ctx) }
            RightCard(
                title = stringResource(R.string.r_accessibility),
                desc = stringResource(R.string.r_accessibility_desc),
                ok = ok,
                button = stringResource(if (secure || shell) R.string.btn_enable else R.string.btn_open),
                onClick = {
                    if (secure || shell) {
                        scope.launch {
                            val done = withContext(Dispatchers.IO) { Perms.enableAccessibility(ctx) }
                            if (!done) open(ctx, Perms.settingsIntent(ctx, Need.ACCESSIBILITY))
                            local++
                        }
                    } else open(ctx, Perms.settingsIntent(ctx, Need.ACCESSIBILITY))
                },
                extra = {
                    if (!ok) {
                        Hint(stringResource(R.string.r_restricted_hint))
                        TextButton(onClick = { open(ctx, Perms.appDetails(ctx)) }) { Text(stringResource(R.string.btn_app_info)) }
                    }
                },
            )
        }

        // Accès aux notifications
        item {
            val ok = remember(key) { has(Need.NOTIF_LISTENER) }
            RightCard(
                title = stringResource(R.string.r_notif_listener),
                desc = stringResource(R.string.r_notif_listener_desc),
                ok = ok,
                button = stringResource(R.string.btn_open),
                onClick = { open(ctx, Perms.settingsIntent(ctx, Need.NOTIF_LISTENER)) },
                extra = { if (!ok) Hint(stringResource(R.string.r_restricted_hint)) },
            )
        }

        item {
            RightCard(
                stringResource(R.string.r_write_settings), stringResource(R.string.r_write_settings_desc),
                remember(key) { has(Need.WRITE_SETTINGS) }, stringResource(R.string.btn_open),
                { open(ctx, Perms.settingsIntent(ctx, Need.WRITE_SETTINGS)) },
            )
        }
        item {
            RightCard(
                stringResource(R.string.r_dnd), stringResource(R.string.r_dnd_desc),
                remember(key) { has(Need.DND) }, stringResource(R.string.btn_open),
                { open(ctx, Perms.settingsIntent(ctx, Need.DND)) },
            )
        }
        item {
            RightCard(
                stringResource(R.string.r_overlay), stringResource(R.string.r_overlay_desc),
                remember(key) { has(Need.OVERLAY) }, stringResource(R.string.btn_open),
                { open(ctx, Perms.settingsIntent(ctx, Need.OVERLAY)) },
            )
        }
        item {
            RightCard(
                stringResource(R.string.r_battery), stringResource(R.string.r_battery_desc),
                remember(key) { Perms.batteryUnrestricted(ctx) }, stringResource(R.string.btn_grant),
                { open(ctx, Perms.batteryIntent(ctx)) },
            )
        }
        if (Build.VERSION.SDK_INT >= 31) {
            item {
                RightCard(
                    stringResource(R.string.r_alarms), stringResource(R.string.r_alarms_desc),
                    remember(key) { Perms.exactAlarms(ctx) }, stringResource(R.string.btn_open),
                    { open(ctx, Perms.alarmsIntent(ctx)) },
                )
            }
        }
        item {
            val needs = listOf(Need.BLUETOOTH, Need.LOCATION, Need.PHONE, Need.CALL, Need.SMS, Need.SEND_SMS, Need.NOTIFICATIONS)
            RightCard(
                stringResource(R.string.r_runtime), stringResource(R.string.r_runtime_desc),
                remember(key) { needs.all { has(it) } }, stringResource(R.string.btn_grant),
                { request(runtimeAll()) },
            )
        }
        if (Build.VERSION.SDK_INT >= 29) {
            item {
                RightCard(
                    stringResource(R.string.r_bg_location), stringResource(R.string.r_bg_location_desc),
                    remember(key) { has(Need.BG_LOCATION) }, stringResource(R.string.btn_grant),
                    {
                        if (!has(Need.LOCATION)) {
                            request(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                        } else {
                            request(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                        }
                    },
                )
            }
        }

        // Réglages de l'appli
        item {
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.titleLarge)
        }
        item {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(stringResource(R.string.background_service), style = MaterialTheme.typography.titleMedium)
                            Text(
                                stringResource(R.string.background_service_desc),
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(checked = Store.serviceEnabled.also { key }, onCheckedChange = {
                            Store.serviceEnabled = it
                            if (it) FlareService.start(ctx) else FlareService.stop(ctx)
                            local++
                        })
                    }
                    HorizontalDivider(Modifier.padding(vertical = 12.dp))
                    if (Build.VERSION.SDK_INT >= 33) {
                        OutlinedButton(onClick = {
                            open(ctx, Intent(Settings.ACTION_APP_LOCALE_SETTINGS, Uri.parse("package:" + ctx.packageName)))
                        }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.language)) }
                        Spacer(Modifier.height(8.dp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { exporter.launch("flare.json") }, modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.export))
                        }
                        OutlinedButton(onClick = { importer.launch(arrayOf("application/json", "text/plain", "application/octet-stream")) }, modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.import_btn))
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 12.dp))
                    Text(
                        stringResource(R.string.about, BuildConfig.VERSION_NAME),
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(onClick = { open(ctx, Intent(Intent.ACTION_VIEW, Uri.parse(REPO))) }) {
                        Text(stringResource(R.string.source_code))
                    }
                }
            }
        }
    }
}
