package fr.apkfold.flare.ui

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.TileService
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.apkfold.flare.R
import fr.apkfold.flare.core.Automation
import fr.apkfold.flare.core.Engine
import fr.apkfold.flare.core.Store
import fr.apkfold.flare.service.FlareService
import fr.apkfold.flare.service.FlareTile1
import fr.apkfold.flare.service.FlareTile2
import fr.apkfold.flare.service.FlareTile3
import fr.apkfold.flare.service.RunShortcutActivity
import java.text.DateFormat
import java.util.Date

private fun listPadding(p: PaddingValues) = PaddingValues(
    start = 16.dp, end = 16.dp, top = p.calculateTopPadding() + 8.dp, bottom = p.calculateBottomPadding() + 96.dp,
)

@Composable
private fun EmptyState(padding: PaddingValues, text: String, action: (@Composable () -> Unit)? = null) {
    Column(
        Modifier.fillMaxSize().padding(padding).padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (action != null) {
            Spacer(Modifier.height(16.dp))
            action()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutomationsTab(padding: PaddingValues, onEdit: (Editing) -> Unit) {
    val ctx = LocalContext.current
    val tick = LocalTick.current
    val all by Store.items.collectAsState()
    val list = all.filter { !it.isShortcut }
    if (list.isEmpty()) {
        EmptyState(padding, stringResource(R.string.empty_automations)) {
            Button(onClick = {
                Store.save(Templates.carMode(ctx))
                FlareService.start(ctx)
            }) { Text(stringResource(R.string.add_example)) }
        }
        return
    }
    LazyColumn(contentPadding = listPadding(padding), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(list, key = { it.id }) { a ->
            val missing = remember(a, tick) { missingFor(ctx, a) }
            ElevatedCard(onClick = { onEdit(Editing(a, isNew = false, shortcut = false)) }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(a.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        val t = a.trigger
                        if (t != null) {
                            val s = summary(ctx, t)
                            Text(
                                stepTitle(ctx, t) + if (s.isNotBlank()) " · $s" else "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 2, overflow = TextOverflow.Ellipsis,
                            )
                        }
                        if (a.actions.isNotEmpty()) {
                            Text(
                                "→ " + a.actions.joinToString(", ") { stepTitle(ctx, it) },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2, overflow = TextOverflow.Ellipsis,
                            )
                        }
                        if (missing.isNotEmpty()) MissingLine(ctx, missing.map { it.label })
                    }
                    Spacer(Modifier.width(12.dp))
                    Switch(checked = a.enabled, onCheckedChange = { Store.setEnabled(a.id, it) })
                }
            }
        }
    }
}

@Composable
fun MissingLine(ctx: Context, labels: List<Int>) {
    Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            ctx.getString(R.string.needs, labels.distinct().joinToString(", ") { ctx.getString(it) }),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

private fun pinToHome(ctx: Context, a: Automation) {
    val sm = ctx.getSystemService(ShortcutManager::class.java) ?: return
    if (!sm.isRequestPinShortcutSupported) return
    val intent = Intent(ctx, RunShortcutActivity::class.java)
        .setAction("fr.apkfold.flare.RUN")
        .putExtra("id", a.id)
    val info = ShortcutInfo.Builder(ctx, "run_" + a.id)
        .setShortLabel(a.name.take(24).ifBlank { ctx.getString(R.string.app_name) })
        .setLongLabel(a.name.ifBlank { ctx.getString(R.string.app_name) })
        .setIcon(Icon.createWithResource(ctx, R.mipmap.ic_launcher))
        .setIntent(intent)
        .build()
    sm.requestPinShortcut(info, null)
}

private fun tileClass(n: Int) = when (n) {
    1 -> FlareTile1::class.java
    2 -> FlareTile2::class.java
    else -> FlareTile3::class.java
}

private fun assignTile(ctx: Context, n: Int, a: Automation) {
    Store.setTile(n, a.id)
    val comp = ComponentName(ctx, tileClass(n))
    try {
        TileService.requestListeningState(ctx, comp)
    } catch (_: Exception) {
    }
    if (Build.VERSION.SDK_INT >= 33) {
        try {
            ctx.getSystemService(StatusBarManager::class.java).requestAddTileService(
                comp, a.name.ifBlank { ctx.getString(R.string.app_name) },
                Icon.createWithResource(ctx, R.drawable.ic_stat_flare), ctx.mainExecutor,
            ) { }
        } catch (_: Exception) {
        }
    }
    Toast.makeText(ctx, ctx.getString(R.string.tile_assigned, n), Toast.LENGTH_SHORT).show()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortcutsTab(padding: PaddingValues, onEdit: (Editing) -> Unit) {
    val ctx = LocalContext.current
    val tick = LocalTick.current
    val all by Store.items.collectAsState()
    val list = all.filter { it.isShortcut }
    if (list.isEmpty()) {
        EmptyState(padding, stringResource(R.string.empty_shortcuts))
        return
    }
    LazyColumn(contentPadding = listPadding(padding), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(list, key = { it.id }) { a ->
            val missing = remember(a, tick) { missingFor(ctx, a) }
            var menu by remember { mutableStateOf(false) }
            ElevatedCard(onClick = { onEdit(Editing(a, isNew = false, shortcut = true)) }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalIconButton(onClick = {
                        Engine.runManual(ctx, a)
                        Toast.makeText(ctx, ctx.getString(R.string.running, a.name), Toast.LENGTH_SHORT).show()
                    }) { Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.run_now)) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(a.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            a.actions.joinToString(", ") { stepTitle(ctx, it) },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2, overflow = TextOverflow.Ellipsis,
                        )
                        if (missing.isNotEmpty()) MissingLine(ctx, missing.map { it.label })
                    }
                    Box {
                        IconButton(onClick = { menu = true }) { Icon(Icons.Default.MoreVert, contentDescription = null) }
                        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                            DropdownMenuItem(text = { Text(stringResource(R.string.edit)) }, onClick = {
                                menu = false
                                onEdit(Editing(a, isNew = false, shortcut = true))
                            })
                            DropdownMenuItem(text = { Text(stringResource(R.string.pin_home)) }, onClick = {
                                menu = false
                                pinToHome(ctx, a)
                            })
                            for (n in 1..3) {
                                val current = Store.tileTarget(n)?.let { Store.get(it)?.name }
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            stringResource(R.string.tile_n, n) +
                                                if (current != null) " ($current)" else "",
                                        )
                                    },
                                    onClick = {
                                        menu = false
                                        assignTile(ctx, n, a)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LogTab(padding: PaddingValues) {
    val log by Store.log.collectAsState()
    if (log.isEmpty()) {
        EmptyState(padding, stringResource(R.string.empty_log))
        return
    }
    val fmt = remember { DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM) }
    LazyColumn(contentPadding = listPadding(padding), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(log) { e ->
            OutlinedCard(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp)) {
                    Icon(
                        if (e.ok) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = null,
                        tint = if (e.ok) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(e.title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                            Text(fmt.format(Date(e.time)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(e.text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
