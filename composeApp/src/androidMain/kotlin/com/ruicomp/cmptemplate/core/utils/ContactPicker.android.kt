package com.ruicomp.cmptemplate.core.utils

import android.app.Activity
import android.content.Intent
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Android implementation of ContactPicker.
 */
actual object ContactPicker {
    @Composable
    actual fun RegisterPicker(onContactPicked: (PickedContact?) -> Unit): () -> Unit {
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                    val contactUri = it.data?.data
                    if (contactUri != null) {
                        val cursor = context.contentResolver.query(
                            contactUri,
                            arrayOf(
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            ),
                            null, null, null
                        )
                        cursor?.use {
                            if (it.moveToFirst()) {
                                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                                val name = if (nameIndex != -1) it.getString(nameIndex) else null
                                val number = if (numberIndex != -1) it.getString(numberIndex) else null
                                if (name != null && number != null) {
                                    onContactPicked(PickedContact(name, number))
                                } else {
                                    onContactPicked(null) // Name or number was null
                                }
                            }
                            else {
                                onContactPicked(null) // Cursor was empty
                            }
                        }
                    } else {
                        onContactPicked(null) // contactUri was null
                    }
                } else {
                    onContactPicked(null) // Result not OK or data is null
                }
            }
        )

        return {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            launcher.launch(intent)
        }
    }
}
