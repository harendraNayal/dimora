import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DimoraScreen(
    onEnable: () -> Unit,
    onDisable: () -> Unit,
    onSliderChange: (Float) -> Unit,
    onRedModeChange: (Boolean) -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf(1f) }
    var redMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Dimora Night", style = MaterialTheme.typography.headlineLarge
        )

        // ✅ Extra space below title
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (redMode) "🔴  Red OLED Night Mode"
            else "🌙  Normal Dim Mode",
            style = MaterialTheme.typography.titleMedium
        )

        // ✅ Slider description
        Text(
            text = "Adjust the slider to control screen dimming intensity.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                onSliderChange(it)
            },
            valueRange = 0f..1f,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)   // ✅ minimum touch target
                .padding(vertical = 12.dp) // ✅ increases touch area
        )

        //        Spacer(modifier = Modifier.height(0.dp))

        // ✅ OLED mode description
        Text(
            text = "Switch between normal dim mode and red OLED mode for eye comfort and battery saving.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Red OLED Mode")
            Switch(
                checked = redMode,
                onCheckedChange = {
                    redMode = it
                    onRedModeChange(it)
                },
            )
        }

//        Button(
//            onClick = onEnable, modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Enable Dimmer")
//        }

        Button(
            onClick = onDisable, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Disable Dimmer")
        }
    }
}