package com.hsnayal.dimora

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun OverlayPermissionDialog(
    onContinue: () -> Unit
) {
    AlertDialog(
        // ❌ Disable dismiss on outside tap
        onDismissRequest = {},

        title = {
            Text("Permission Required")
        },
        text = {
            Text(
                "Dimora needs permission to display over other apps to dim your screen and to enable Night Mode and OLED Red Mode.\n\n" +
                        "After tapping Continue, you will be taken to system settings. Please search for \"Dimora\", tap it, and turn on \"Display over other apps\", then return to Dimora to continue."
            )
        },
        confirmButton = {
            Button(
                onClick = onContinue
            ) {
                Text("Continue")
            }
        },
        // ❌ No cancel / dismiss button
        dismissButton = {}
    )
}