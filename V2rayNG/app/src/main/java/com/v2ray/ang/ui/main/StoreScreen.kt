package com.v2ray.ang.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private enum class StoreMode { NEW, TIME, DATA }

@Composable
fun StoreDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxSize(), color = Color(0xFFF7F7FF)) {
            StoreScreen(onClose = onDismiss)
        }
    }
}

@Composable
private fun StoreScreen(onClose: () -> Unit) {
    var plan by rememberSaveable { mutableStateOf("اقتصادی") }
    var modeName by rememberSaveable { mutableStateOf(StoreMode.NEW.name) }
    var volume by rememberSaveable { mutableIntStateOf(10) }
    var months by rememberSaveable { mutableIntStateOf(1) }
    var extraGb by rememberSaveable { mutableIntStateOf(1) }
    var invoice by rememberSaveable { mutableStateOf(false) }
    val mode = StoreMode.valueOf(modeName)
    val special = plan == "ویژه"
    val accent = if (special) Color(0xFF7557E8) else Color(0xFF168BEA)
    val base = if (special) 200000 else 100000
    val renewal = if (special) 30000 else 40000
    val extra = if (special) 15000 else 10000
    val total = when (mode) {
        StoreMode.NEW -> (volume / 10) * months * base
        StoreMode.TIME -> months * renewal
        StoreMode.DATA -> extraGb * extra
    }
    val totalText = "%,d".format(total).replace(',', '٬')

    Column(
        Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF8F8FF), Color(0xFFEAF2FF), Color(0xFFF4EEFF))))
            .verticalScroll(rememberScrollState()).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onClose) { Text("بازگشت") }
            Text("فروشگاه اشتراک", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("🛒")
        }
        Spacer(Modifier.height(12.dp))
        Column(
            Modifier.fillMaxWidth()
                .background(Brush.linearGradient(listOf(Color(0xFF8D69F5), Color(0xFF45B8F5))), RoundedCornerShape(26.dp))
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🚀", style = MaterialTheme.typography.displaySmall)
            Text("P VPN", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.ExtraBold)
            Text("اشتراک دلخواهت را انتخاب کن", color = Color.White)
        }
        Spacer(Modifier.height(18.dp))
        Text("انتخاب پلن", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PlanCard("اقتصادی", "۱۰۰٬۰۰۰ تومان", plan == "اقتصادی", Color(0xFF168BEA), Modifier.weight(1f)) { plan = "اقتصادی" }
            PlanCard("ویژه ⭐", "۲۰۰٬۰۰۰ تومان", special, Color(0xFF7557E8), Modifier.weight(1f)) { plan = "ویژه" }
        }
        Spacer(Modifier.height(16.dp))
        Text("نوع خرید", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            FilterChip(mode == StoreMode.NEW, { modeName = StoreMode.NEW.name }, label = { Text("اشتراک جدید") })
            FilterChip(mode == StoreMode.TIME, { modeName = StoreMode.TIME.name }, label = { Text("تمدید زمان") })
            FilterChip(mode == StoreMode.DATA, { modeName = StoreMode.DATA.name }, label = { Text("حجم اضافه") })
        }
        when (mode) {
            StoreMode.NEW -> {
                SelectorCard("حجم اشتراک", "$volume گیگابایت") {
                    Slider(volume.toFloat(), { volume = ((it.toInt() / 10) * 10).coerceIn(10, 500) }, valueRange = 10f..500f, steps = 48)
                }
                SelectorCard("مدت اشتراک", "$months ماه") {
                    Slider(months.toFloat(), { months = it.toInt().coerceIn(1, 12) }, valueRange = 1f..12f, steps = 10)
                }
            }
            StoreMode.TIME -> SelectorCard("تمدید فقط زمان", "$months ماه") {
                Slider(months.toFloat(), { months = it.toInt().coerceIn(1, 12) }, valueRange = 1f..12f, steps = 10)
                Text("حجم باقی‌مانده تغییر نمی‌کند.", style = MaterialTheme.typography.bodySmall)
            }
            StoreMode.DATA -> SelectorCard("خرید حجم اضافه", "$extraGb گیگابایت") {
                Slider(extraGb.toFloat(), { extraGb = it.toInt().coerceIn(1, 100) }, valueRange = 1f..100f, steps = 98)
                Text("حجم به مانده فعلی اضافه می‌شود؛ تاریخ انقضا تغییر نمی‌کند.", style = MaterialTheme.typography.bodySmall)
            }
        }
        Card(
            Modifier.fillMaxWidth().padding(top = 16.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("مبلغ قابل پرداخت", color = Color(0xFF666681))
                Text("$totalText تومان", style = MaterialTheme.typography.headlineMedium, color = accent, fontWeight = FontWeight.ExtraBold)
                Text(
                    when (mode) {
                        StoreMode.NEW -> "هر ۱۰ گیگابایت برای هر ماه: $base تومان"
                        StoreMode.TIME -> "هر ماه تمدید: $renewal تومان"
                        StoreMode.DATA -> "هر گیگابایت اضافه: $extra تومان"
                    },
                    textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = Color(0xFF77778E)
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { invoice = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent)
                ) { Text("ادامه و مشاهده فاکتور") }
                Text("نسخه آزمایشی؛ پرداخت واقعی فعال نیست.", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
        if (invoice) {
            Card(
                Modifier.fillMaxWidth().padding(top = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F7EF))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("فاکتور آزمایشی", fontWeight = FontWeight.Bold, color = Color(0xFF176B43))
                    Text("پلن: $plan")
                    Text("مبلغ: $totalText تومان")
                    Text("این سفارش فقط برای تست است و پرداخت یا ثبت واقعی انجام نمی‌شود.")
                    TextButton(onClick = { invoice = false }) { Text("بستن") }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun PlanCard(title: String, price: String, selected: Boolean, accent: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick, modifier = modifier, shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) accent.copy(alpha = 0.13f) else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 6.dp else 2.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = accent, fontWeight = FontWeight.Bold)
            Text(price, textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, color = Color(0xFF252545))
            Text("۱۰ گیگ + یک ماه", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            if (selected) Text("✓ انتخاب شده", color = accent, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun SelectorCard(title: String, value: String, content: @Composable () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(top = 12.dp), shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF343452))
                Text(value, fontWeight = FontWeight.Bold, color = Color(0xFF7557E8))
            }
            content()
        }
    }
}
