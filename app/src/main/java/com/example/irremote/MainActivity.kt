package com.example.irremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sender = IrSender(this)
        val remotes = loadRemotes(this)
        setContent {
            MaterialTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    App(sender, remotes)
                }
            }
        }
    }
}

@Composable
fun App(sender: IrSender, remotes: List<Remote>) {
    var type by remember { mutableStateOf("tv") }
    var selected by remember { mutableStateOf<Remote?>(null) }
    var searching by remember { mutableStateOf(false) }

    Surface(Modifier.fillMaxSize()) {
        Column(Modifier.padding(16.dp).statusBarsPadding()) {
            if (!sender.isSupported()) {
                Text("⚠ هذا الهاتف لا يحتوي على باعث IR", color = MaterialTheme.colorScheme.error)
            }
            val sel = selected
            when {
                sel != null -> RemoteScreen(sel, sender) { selected = null }
                searching -> SearchScreen(remotes.filter { it.type == type && it.power != null }, sender,
                    onFound = { selected = it; searching = false }, onStop = { searching = false })
                else -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        typeLabels.forEach { (k, v) ->
                            FilterChip(selected = type == k, onClick = { type = k }, label = { Text(v) })
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { searching = true }, Modifier.fillMaxWidth()) { Text("🔍 بحث تلقائي") }
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(remotes.filter { it.type == type }) { r ->
                            val ready = r.buttons.isNotEmpty()
                            OutlinedButton(onClick = { selected = r }, enabled = ready, modifier = Modifier.fillMaxWidth()) {
                                Text(if (ready) r.brand else "${r.brand} (الأكواد غير مضافة بعد)")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RemoteScreen(r: Remote, sender: IrSender, onBack: () -> Unit) {
    TextButton(onClick = onBack) { Text("← رجوع") }
    Text(r.brand, style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(12.dp))
    LazyVerticalGrid(GridCells.Fixed(3), horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(r.buttons) { b ->
            Button(onClick = { sender.sendNec(b.address, b.command) }) { Text(b.name) }
        }
    }
}

@Composable
fun SearchScreen(list: List<Remote>, sender: IrSender, onFound: (Remote) -> Unit, onStop: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    if (list.isEmpty()) {
        Text("لا توجد أكواد لهذا النوع بعد"); TextButton(onClick = onStop) { Text("رجوع") }; return
    }
    val cur = list.getOrNull(index)
    LaunchedEffect(index) {
        if (cur == null) return@LaunchedEffect
        cur.power?.let { sender.sendNec(it.address, it.command) }
        delay(3000)
        index++
    }
    if (cur == null) {
        Text("انتهت التجربة دون نتيجة. أضف أكواد جهازك إلى codes.json")
        TextButton(onClick = onStop) { Text("رجوع") }
    } else {
        Text("جاري تجربة: ${cur.brand}  (${index + 1}/${list.size})", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        Button(onClick = { onFound(cur) }, Modifier.fillMaxWidth()) { Text("✔ استجاب الجهاز") }
        TextButton(onClick = onStop) { Text("إيقاف") }
    }
}
