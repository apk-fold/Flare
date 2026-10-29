package fr.apkfold.flare.ui

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.location.LocationManager
import android.text.format.DateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import fr.apkfold.flare.R
import fr.apkfold.flare.core.Cat
import fr.apkfold.flare.core.Def
import fr.apkfold.flare.core.DeviceState
import fr.apkfold.flare.core.Engine
import fr.apkfold.flare.core.Kind
import fr.apkfold.flare.core.Need
import fr.apkfold.flare.core.P
import fr.apkfold.flare.core.PType
import fr.apkfold.flare.core.Perms
import fr.apkfold.flare.core.Step
import fr.apkfold.flare.core.Store
import fr.apkfold.flare.core.Defs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

/** Liste plein écran des déclencheurs, conditions ou actions, groupés par catégorie, avec recherche. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepPicker(kind: Kind, onDismiss: () -> Unit, onPick: (Def) -> Unit) {
    val ctx = LocalContext.current
    val tick = LocalTick.current
    var query by remember { mutableStateOf("") }
    val defs = Defs.list(kind)
    val title = when (kind) {
        Kind.TRIGGER -> R.string.choose_trigger
        Kind.CONDITION -> R.string.add_condition
        Kind.ACTION -> R.string.add_action
    }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxSize()) {
            Column {
                TopAppBar(
                    title = { Text(stringResource(title)) },
                    navigationIcon = { IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cancel)) } },
                )
                OutlinedTextField(
                    value = query, onValueChange = { query = it },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    placeholder = { Text(stringResource(R.string.search)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                )
                val q = query.trim().lowercase()
                val visible = defs.filter { q.isEmpty() || ctx.getString(it.title).lowercase().contains(q) }
                LazyColumn(Modifier.fillMaxSize()) {
                    for (cat in Cat.entries) {
                        val inCat = visible.filter { it.cat == cat }
                        if (inCat.isEmpty()) continue
                        item(key = "h_" + cat.name) {
                            Row(Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(catIcon(cat), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(cat.label), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                        items(inCat, key = { it.type }) { def ->
                            val missing = remember(def, tick) { Perms.missing(ctx, def) }
                            val needsText = ctx.getString(R.string.needs, missing.map { it.label }.distinct().joinToString(", ") { ctx.getString(it) })
                            val support: (@Composable () -> Unit)? = if (missing.isNotEmpty()) ({
                                Text(needsText, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }) else null
                            ListItem(
                                headlineContent = { Text(stringResource(def.title)) },
                                supportingContent = support,
                                leadingContent = { Icon(stepIcon(def), contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                                modifier = Modifier.clickable { onPick(def) },
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Réglage des paramètres d'une étape. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ParamDialog(def: Def, step: Step, currentId: String, onDismiss: () -> Unit, onSave: (Step) -> Unit) {
    val values = remember { mutableStateMapOf<String, String>().apply { putAll(step.params) } }
    var appFor by remember { mutableStateOf<String?>(null) }
    var timeFor by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(def.title)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                for (p in def.params) {
                    ParamField(
                        p = p,
                        value = values[p.key].orEmpty(),
                        currentId = currentId,
                        onChange = { values[p.key] = it },
                        onPickApp = { appFor = p.key },
                        onPickTime = { timeFor = p.key },
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSave(Step(def.type, values.toMap())) }) { Text(stringResource(R.string.ok)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )

    appFor?.let { key ->
        AppPickerDialog(onDismiss = { appFor = null }, onPick = {
            values[key] = it
            appFor = null
        })
    }
    timeFor?.let { key ->
        TimeDialog(value = values[key].orEmpty(), onDismiss = { timeFor = null }, onPick = {
            values[key] = it
            timeFor = null
        })
    }
}

@Composable
private fun Label(p: P) {
    Text(stringResource(p.label), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ParamField(
    p: P,
    value: String,
    currentId: String,
    onChange: (String) -> Unit,
    onPickApp: () -> Unit,
    onPickTime: () -> Unit,
) {
    val ctx = LocalContext.current
    val tick = LocalTick.current
    val request = LocalRequestPerms.current
    val hint: (@Composable () -> Unit)? = if (p.hint != 0) ({ Text(stringResource(p.hint)) }) else null

    when (p.type) {
        PType.TEXT -> OutlinedTextField(
            value = value, onValueChange = onChange, label = { Text(stringResource(p.label)) },
            supportingText = hint, singleLine = true, modifier = Modifier.fillMaxWidth(),
        )
        PType.MULTILINE -> OutlinedTextField(
            value = value, onValueChange = onChange, label = { Text(stringResource(p.label)) },
            supportingText = hint, minLines = 3, modifier = Modifier.fillMaxWidth(),
        )
        PType.NUMBER -> OutlinedTextField(
            value = value, onValueChange = { v -> onChange(v.filter { it.isDigit() || it == '.' }) },
            label = { Text(stringResource(p.label)) }, supportingText = hint, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(),
        )
        PType.CHOICE -> Column {
            Label(p)
            for (c in p.choices) {
                Row(
                    Modifier.fillMaxWidth().selectable(selected = value == c.value, role = Role.RadioButton, onClick = { onChange(c.value) }),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = value == c.value, onClick = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (c.label == 0) c.value else stringResource(c.label))
                }
            }
        }
        PType.APP -> Column {
            Label(p)
            OutlinedButton(onClick = onPickApp, modifier = Modifier.fillMaxWidth()) {
                Text(if (value.isBlank()) stringResource(R.string.pick_app) else Apps.label(ctx, value))
            }
            if (value.isNotBlank()) TextButton(onClick = { onChange("") }) { Text(stringResource(R.string.any)) }
        }
        PType.BT -> Column {
            OutlinedTextField(
                value = value, onValueChange = onChange, label = { Text(stringResource(p.label)) },
                supportingText = hint, modifier = Modifier.fillMaxWidth(),
            )
            val granted = remember(tick) { Perms.has(ctx, Need.BLUETOOTH) }
            if (granted) {
                val bonded = remember(tick) {
                    try {
                        ctx.getSystemService(BluetoothManager::class.java)?.adapter?.bondedDevices?.mapNotNull { it.name }?.sorted() ?: emptyList()
                    } catch (_: SecurityException) {
                        emptyList()
                    }
                }
                if (bonded.isNotEmpty()) {
                    Text(stringResource(R.string.paired_devices), style = MaterialTheme.typography.labelMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (n in bonded) {
                            val selected = DeviceState.tokens(value).any { it.equals(n, ignoreCase = true) }
                            FilterChip(selected = selected, onClick = {
                                val t = DeviceState.tokens(value).toMutableList()
                                if (selected) t.removeAll { it.equals(n, ignoreCase = true) } else t += n
                                onChange(t.joinToString(", "))
                            }, label = { Text(n) })
                        }
                    }
                }
            } else {
                TextButton(onClick = { Perms.runtimeFor(Need.BLUETOOTH)?.let(request) }) { Text(stringResource(R.string.grant_bt)) }
            }
        }
        PType.SSID -> Column {
            OutlinedTextField(
                value = value, onValueChange = onChange, label = { Text(stringResource(p.label)) },
                supportingText = hint, modifier = Modifier.fillMaxWidth(),
            )
            val cur = DeviceState.ssid
            if (cur != null) {
                AssistChip(onClick = { onChange(cur) }, label = { Text(stringResource(R.string.current_network, cur)) })
            }
            if (!remember(tick) { Perms.has(ctx, Need.LOCATION) }) {
                TextButton(onClick = { Perms.runtimeFor(Need.LOCATION)?.let(request) }) { Text(stringResource(R.string.grant_location)) }
            }
        }
        PType.TIME -> Column {
            Label(p)
            OutlinedButton(onClick = onPickTime) { Text(value.ifBlank { "--:--" }) }
        }
        PType.DAYS -> Column {
            Label(p)
            val set = Engine.parseDays(value)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (d in 1..7) {
                    FilterChip(
                        selected = d in set,
                        onClick = {
                            val n = if (d in set) set - d else set + d
                            onChange(n.sorted().joinToString(","))
                        },
                        label = { Text(DayOfWeek.of(d).getDisplayName(TextStyle.SHORT, Locale.getDefault())) },
                    )
                }
            }
        }
        PType.ROUTINE -> Column {
            Label(p)
            val items = Store.items.value.filter { it.id != currentId }
            if (items.isEmpty()) Text(stringResource(R.string.not_set), color = MaterialTheme.colorScheme.onSurfaceVariant)
            for (a in items) {
                Row(
                    Modifier.fillMaxWidth().selectable(selected = value == a.id, role = Role.RadioButton, onClick = { onChange(a.id) }),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = value == a.id, onClick = null)
                    Spacer(Modifier.width(8.dp))
                    Text(a.name)
                }
            }
        }
        PType.LATLON -> Column {
            OutlinedTextField(
                value = value, onValueChange = onChange, label = { Text(stringResource(p.label)) },
                placeholder = { Text("48.8584, 2.2945") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
            )
            TextButton(onClick = {
                if (!Perms.has(ctx, Need.LOCATION)) {
                    request(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                } else {
                    val lm = ctx.getSystemService(LocationManager::class.java)
                    val loc = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
                        .mapNotNull { prov ->
                            try {
                                lm.getLastKnownLocation(prov)
                            } catch (_: Exception) {
                                null
                            }
                        }
                        .maxByOrNull { it.time }
                    if (loc != null) onChange(String.format(Locale.US, "%.5f, %.5f", loc.latitude, loc.longitude))
                }
            }) { Text(stringResource(R.string.my_location)) }
        }
    }
}

/** Liste des applis installées avec recherche. */
@Composable
fun AppPickerDialog(onDismiss: () -> Unit, onPick: (String) -> Unit) {
    val ctx = LocalContext.current
    var query by remember { mutableStateOf("") }
    val apps by produceState<List<AppInfo>?>(null) { value = withContext(Dispatchers.IO) { Apps.list(ctx) } }
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.extraLarge, tonalElevation = 6.dp) {
            Column(Modifier.padding(vertical = 16.dp)) {
                Text(
                    stringResource(R.string.pick_app), style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                OutlinedTextField(
                    value = query, onValueChange = { query = it }, singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    placeholder = { Text(stringResource(R.string.search)) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                )
                HorizontalDivider()
                val list = apps
                if (list == null) {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else {
                    val q = query.trim().lowercase()
                    LazyColumn(Modifier.heightIn(max = 460.dp)) {
                        items(list.filter { q.isEmpty() || it.label.lowercase().contains(q) || it.pkg.contains(q) }, key = { it.pkg }) { app ->
                            val icon by produceState<ImageBitmap?>(null, app.pkg) { value = withContext(Dispatchers.IO) { Apps.icon(ctx, app.pkg) } }
                            ListItem(
                                headlineContent = { Text(app.label) },
                                supportingContent = { Text(app.pkg, style = MaterialTheme.typography.bodySmall) },
                                leadingContent = {
                                    val b = icon
                                    if (b != null) Image(bitmap = b, contentDescription = null, modifier = Modifier.size(36.dp))
                                    else Spacer(Modifier.size(36.dp))
                                },
                                modifier = Modifier.clickable { onPick(app.pkg) },
                            )
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDialog(value: String, onDismiss: () -> Unit, onPick: (String) -> Unit) {
    val ctx = LocalContext.current
    val t = Engine.parseTime(value)
    val state = rememberTimePickerState(
        initialHour = t?.hour ?: 8,
        initialMinute = t?.minute ?: 0,
        is24Hour = DateFormat.is24HourFormat(ctx),
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { TimePicker(state = state) },
        confirmButton = {
            TextButton(onClick = { onPick(String.format(Locale.US, "%02d:%02d", state.hour, state.minute)) }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}
