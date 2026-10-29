package fr.apkfold.flare.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import fr.apkfold.flare.R
import fr.apkfold.flare.core.Automation
import fr.apkfold.flare.core.Store
import fr.apkfold.flare.service.FlareService
import rikka.shizuku.Shizuku

class MainActivity : ComponentActivity() {

    private val tick = mutableIntStateOf(0)
    private val permListener = Shizuku.OnRequestPermissionResultListener { _, _ -> tick.intValue++ }
    private val binderListener = Shizuku.OnBinderReceivedListener { tick.intValue++ }
    private val deadListener = Shizuku.OnBinderDeadListener { tick.intValue++ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Store.init(this)
        try {
            Shizuku.addRequestPermissionResultListener(permListener)
            Shizuku.addBinderReceivedListenerSticky(binderListener)
            Shizuku.addBinderDeadListener(deadListener)
        } catch (_: Throwable) {
        }
        FlareService.start(this)
        setContent { FlareTheme { FlareRoot(tick.intValue) } }
    }

    override fun onResume() {
        super.onResume()
        tick.intValue++
    }

    override fun onDestroy() {
        try {
            Shizuku.removeRequestPermissionResultListener(permListener)
            Shizuku.removeBinderReceivedListener(binderListener)
            Shizuku.removeBinderDeadListener(deadListener)
        } catch (_: Throwable) {
        }
        super.onDestroy()
    }
}

/** Ce qu'on modifie : l'élément, s'il est nouveau, et s'il s'agit d'un raccourci. */
data class Editing(val item: Automation, val isNew: Boolean, val shortcut: Boolean)

@Composable
fun FlareRoot(tick: Int) {
    var editing by remember { mutableStateOf<Editing?>(null) }
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }
    val request: (Array<String>) -> Unit = { if (it.isNotEmpty()) launcher.launch(it) }

    CompositionLocalProvider(LocalTick provides tick, LocalRequestPerms provides request) {
        val e = editing
        if (e != null) {
            BackHandler { editing = null }
            EditorScreen(e, onClose = { editing = null })
        } else {
            Home(tab, onTab = { tab = it }, onEdit = { editing = it })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(tab: Int, onTab: (Int) -> Unit, onEdit: (Editing) -> Unit) {
    val snack = remember { SnackbarHostState() }
    val titles = listOf(R.string.nav_automations, R.string.nav_shortcuts, R.string.nav_log, R.string.nav_rights)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(titles[tab])) },
                actions = {
                    if (tab == 2) {
                        IconButton(onClick = { Store.clearLog() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = stringResource(R.string.log_clear))
                        }
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                val icons = listOf(Icons.Default.Bolt, Icons.Default.PlayArrow, Icons.AutoMirrored.Filled.List, Icons.Default.Security)
                titles.forEachIndexed { i, t ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { onTab(i) },
                        icon = { Icon(icons[i], contentDescription = null) },
                        label = { Text(stringResource(t)) },
                    )
                }
            }
        },
        floatingActionButton = {
            if (tab == 0 || tab == 1) {
                val shortcut = tab == 1
                ExtendedFloatingActionButton(
                    onClick = { onEdit(Editing(Automation(), isNew = true, shortcut = shortcut)) },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text(stringResource(if (shortcut) R.string.new_shortcut else R.string.new_automation)) },
                )
            }
        },
        snackbarHost = { SnackbarHost(snack) },
    ) { padding ->
        when (tab) {
            0 -> AutomationsTab(padding, onEdit)
            1 -> ShortcutsTab(padding, onEdit)
            2 -> LogTab(padding)
            else -> RightsTab(padding, snack)
        }
    }
}
