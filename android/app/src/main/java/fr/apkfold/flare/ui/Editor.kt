package fr.apkfold.flare.ui

import android.Manifest
import android.widget.Toast
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.apkfold.flare.R
import fr.apkfold.flare.core.Automation
import fr.apkfold.flare.core.Def
import fr.apkfold.flare.core.Defs
import fr.apkfold.flare.core.Engine
import fr.apkfold.flare.core.Kind
import fr.apkfold.flare.core.Need
import fr.apkfold.flare.core.Perms
import fr.apkfold.flare.core.Step
import fr.apkfold.flare.core.Store
import fr.apkfold.flare.service.FlareService

enum class Slot(val kind: Kind) { TRIGGER(Kind.TRIGGER), CONDITION(Kind.CONDITION), ACTION(Kind.ACTION), EXIT(Kind.ACTION) }

private data class ParamEdit(val slot: Slot, val index: Int, val step: Step)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(editing: Editing, onClose: () -> Unit) {
    val ctx = LocalContext.current
    val request = LocalRequestPerms.current
    val tick = LocalTick.current
    val initial = editing.item
    val shortcutMode = editing.shortcut

    var name by remember { mutableStateOf(initial.name) }
    var trigger by remember { mutableStateOf(initial.trigger) }
    val conditions = remember { mutableStateListOf<Step>().apply { addAll(initial.conditions) } }
    val actions = remember { mutableStateListOf<Step>().apply { addAll(initial.actions) } }
    val exits = remember { mutableStateListOf<Step>().apply { addAll(initial.exitActions) } }
    var picker by remember { mutableStateOf(if (editing.isNew && !shortcutMode) Slot.TRIGGER else null) }
    var paramEdit by remember { mutableStateOf<ParamEdit?>(null) }
    var confirmDelete by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val triggerDef = trigger?.let { Defs.get(it.type) }

    fun build(): Automation {
        val t = if (shortcutMode) null else trigger
        val fallback = when {
            t != null -> stepTitle(ctx, t)
            else -> ctx.getString(R.string.default_shortcut_name)
        }
        return initial.copy(
            name = name.trim().ifBlank { fallback },
            trigger = t,
            conditions = if (shortcutMode) emptyList() else conditions.toList(),
            actions = actions.toList(),
            exitActions = if (shortcutMode || triggerDef?.exit == null) emptyList() else exits.toList(),
        )
    }

    fun listFor(slot: Slot): SnapshotStateList<Step>? = when (slot) {
        Slot.CONDITION -> conditions
        Slot.ACTION -> actions
        Slot.EXIT -> exits
        Slot.TRIGGER -> null
    }

    fun commit(slot: Slot, index: Int, step: Step) {
        if (slot == Slot.TRIGGER) {
            trigger = step
            return
        }
        val l = listFor(slot) ?: return
        if (index in l.indices) l[index] = step else l += step
    }

    fun askRuntime(def: Def) {
        val fineOk = Perms.has(ctx, Need.LOCATION)
        val perms = (def.needs + def.anyOf)
            .filter { !Perms.has(ctx, it) }
            .flatMap { Perms.runtimeFor(it)?.toList() ?: emptyList() }
            .filter { fineOk || it != Manifest.permission.ACCESS_BACKGROUND_LOCATION }
            .distinct()
        if (perms.isNotEmpty()) request(perms.toTypedArray())
    }

    fun pick(slot: Slot, def: Def) {
        picker = null
        val step = Defs.newStep(def)
        askRuntime(def)
        if (def.params.isEmpty()) commit(slot, -1, step) else paramEdit = ParamEdit(slot, -1, step)
    }

    fun save() {
        if (!shortcutMode && trigger == null) {
            error = ctx.getString(R.string.err_no_trigger)
            return
        }
        val a = build()
        Store.save(a)
        if (!a.isShortcut) FlareService.start(ctx)
        onClose()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            when {
                                !editing.isNew -> R.string.edit
                                shortcutMode -> R.string.new_shortcut
                                else -> R.string.new_automation
                            },
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back)) }
                },
                actions = {
                    IconButton(onClick = {
                        val a = build()
                        Engine.runManual(ctx, a)
                        Toast.makeText(ctx, ctx.getString(R.string.running, a.name), Toast.LENGTH_SHORT).show()
                    }) { Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.run_now)) }
                    if (!editing.isNew) {
                        IconButton(onClick = { confirmDelete = true }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                        }
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, top = padding.calculateTopPadding() + 8.dp, bottom = padding.calculateBottomPadding() + 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name)) },
                    singleLine = true, modifier = Modifier.fillMaxWidth(),
                )
            }

            if (!shortcutMode) {
                item { Section(stringResource(R.string.section_when)) }
                item {
                    val t = trigger
                    if (t == null) {
                        OutlinedButton(onClick = { picker = Slot.TRIGGER }, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.choose_trigger))
                        }
                    } else {
                        StepRow(
                            step = t, tick = tick,
                            onClick = { if (Defs.get(t.type)?.params?.isNotEmpty() == true) paramEdit = ParamEdit(Slot.TRIGGER, 0, t) },
                            onReplace = { picker = Slot.TRIGGER },
                        )
                    }
                }

                item { Section(stringResource(R.string.section_if)) }
                if (conditions.isEmpty()) {
                    item { Text(stringResource(R.string.no_conditions), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
                itemsIndexed(conditions) { i, c ->
                    StepRow(
                        step = c, tick = tick,
                        onClick = { paramEdit = ParamEdit(Slot.CONDITION, i, c) },
                        onDelete = { conditions.removeAt(i) },
                    )
                }
                item { AddButton(stringResource(R.string.add_condition)) { picker = Slot.CONDITION } }
            }

            item { Section(stringResource(R.string.section_then)) }
            itemsIndexed(actions) { i, a ->
                StepRow(
                    step = a, tick = tick,
                    onClick = { if (Defs.get(a.type)?.params?.isNotEmpty() == true) paramEdit = ParamEdit(Slot.ACTION, i, a) },
                    onUp = if (i > 0) ({ actions.add(i - 1, actions.removeAt(i)) }) else null,
                    onDown = if (i < actions.size - 1) ({ actions.add(i + 1, actions.removeAt(i)) }) else null,
                    onDelete = { actions.removeAt(i) },
                )
            }
            item { AddButton(stringResource(R.string.add_action)) { picker = Slot.ACTION } }

            if (!shortcutMode && triggerDef?.exit != null) {
                item { Section(stringResource(R.string.section_exit)) }
                item { Text(stringResource(R.string.section_exit_hint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                itemsIndexed(exits) { i, a ->
                    StepRow(
                        step = a, tick = tick,
                        onClick = { if (Defs.get(a.type)?.params?.isNotEmpty() == true) paramEdit = ParamEdit(Slot.EXIT, i, a) },
                        onUp = if (i > 0) ({ exits.add(i - 1, exits.removeAt(i)) }) else null,
                        onDown = if (i < exits.size - 1) ({ exits.add(i + 1, exits.removeAt(i)) }) else null,
                        onDelete = { exits.removeAt(i) },
                    )
                }
                item { AddButton(stringResource(R.string.add_action)) { picker = Slot.EXIT } }
            }

            item {
                Spacer(Modifier.height(12.dp))
                Button(onClick = { save() }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.save)) }
            }
        }
    }

    picker?.let { slot ->
        StepPicker(kind = slot.kind, onDismiss = { picker = null }, onPick = { pick(slot, it) })
    }

    paramEdit?.let { pe ->
        val def = Defs.get(pe.step.type)
        if (def != null) {
            ParamDialog(
                def = def, step = pe.step, currentId = initial.id,
                onDismiss = { paramEdit = null },
                onSave = {
                    commit(pe.slot, pe.index, it)
                    paramEdit = null
                },
            )
        } else paramEdit = null
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text(stringResource(R.string.delete_confirm, initial.name)) },
            confirmButton = {
                TextButton(onClick = {
                    Store.delete(initial.id)
                    confirmDelete = false
                    onClose()
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text(stringResource(R.string.cancel)) } },
        )
    }

    error?.let { msg ->
        AlertDialog(
            onDismissRequest = { error = null },
            text = { Text(msg) },
            confirmButton = { TextButton(onClick = { error = null }) { Text(stringResource(R.string.ok)) } },
        )
    }
}

@Composable
private fun Section(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 12.dp, bottom = 2.dp),
    )
}

@Composable
private fun AddButton(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepRow(
    step: Step,
    tick: Int,
    onClick: () -> Unit,
    onReplace: (() -> Unit)? = null,
    onUp: (() -> Unit)? = null,
    onDown: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
) {
    val ctx = LocalContext.current
    val def = Defs.get(step.type)
    val missing = remember(step, tick) { if (def != null) Perms.missing(ctx, def) else emptyList() }
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            if (def != null) {
                Icon(catIcon(def.cat), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(stepTitle(ctx, step), style = MaterialTheme.typography.titleSmall)
                val s = summary(ctx, step)
                if (s.isNotBlank()) Text(s, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (missing.isNotEmpty()) MissingLine(ctx, missing.map { it.label })
            }
            if (onReplace != null) IconButton(onClick = onReplace) { Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit)) }
            if (onUp != null) IconButton(onClick = onUp) { Icon(Icons.Default.KeyboardArrowUp, contentDescription = stringResource(R.string.move_up)) }
            if (onDown != null) IconButton(onClick = onDown) { Icon(Icons.Default.KeyboardArrowDown, contentDescription = stringResource(R.string.move_down)) }
            if (onDelete != null) IconButton(onClick = onDelete) { Icon(Icons.Default.Close, contentDescription = stringResource(R.string.delete)) }
        }
    }
}
