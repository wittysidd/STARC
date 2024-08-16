package com.example.firebasetest_2

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController

val tag = "FireBaseDemo"
@Composable
fun RegisterComplainButton(navController: NavController) {

    var indicatorState by remember { mutableStateOf(false) }
    val viewModel = myViewModel()
    var enabled by remember { mutableStateOf(true) }
    var enabledForImage by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var iExpanded by remember { mutableStateOf(false)}
    var severity by remember { mutableStateOf("Select Severity") }
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
        }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Register Complaint", fontSize = 24.sp, fontWeight = FontWeight.Bold )
            OutlinedTextField(
                value = name,
                enabled = enabled,
                singleLine = true,
                onValueChange = { name = it },
                modifier = Modifier.padding(8.dp),
                label = { Text("Name") })
            OutlinedTextField(
                value = number,
                enabled = enabled,
                singleLine = true,
                onValueChange = { number = it },
                modifier = Modifier.padding(8.dp),
                label = { Text("Number") })
            OutlinedTextField(
                value = location,
                enabled = enabled,
                singleLine = true,
                onValueChange = { location = it },
                modifier = Modifier.padding(8.dp),
                label = { Text("Location") })
            OutlinedTextField(
                value = distance,
                enabled = enabled,
                singleLine = true,
                onValueChange = { distance = it },
                modifier = Modifier.padding(8.dp),
                label = { Text("Distance(km)") })

// SELECT PHOTO _______________________

            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Add Photo")
                IconButton(
                    onClick = {
                        launcher.launch("image/*")
                        enabledForImage = false
                    },
                    enabled = enabledForImage
                ) {
                    Icon(imageVector = Icons.Default.AddCircle, contentDescription = "add photo")

                }
            }
            if(indicatorState == true) {
                showProgress()
            }

// SELECT SEVERITY ------------
            Box{
                // INPUT BUTTON
                Button(onClick = { iExpanded = true },
                    enabled = enabled) {
                    Text(text = severity)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Shows list of items")
                }
                DropdownMenu(expanded = iExpanded, onDismissRequest = { iExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Low") },
                        onClick = {
                            severity = "Low Severity"
                            iExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Medium") },
                        onClick = {
                            severity = "Medium Severity"
                            iExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("High") },
                        onClick = {
                            severity = "High Severity"
                            iExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("No Road") },
                        onClick = {
                            severity = "No Road (NEW)"
                            iExpanded = false
                        }
                    )
                }
            }

// SAVE DATA ---------------------------------------

            Button(onClick = {

                if( name.isEmpty() || number.isEmpty() || location.isEmpty() || severity == "Select Severity")
                {
                    Toast.makeText(
                        context,
                        "Please Enter & Select all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    if (distance.isDigitsOnly()) {
                        Toast.makeText(context, "Saving Data...", Toast.LENGTH_SHORT).show()
                        indicatorState = true
                        viewModel.SaveData(name,
                            number,
                            location,
                            imageUri,
                            severity,
                            distance.toDouble(),
                            OnSuccess = {
                                Toast.makeText(
                                    context,
                                    "Image uploaded Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                indicatorState = false
                                navController.popBackStack()
                            },
                            OnNoImageSelection = {
                                indicatorState = false
                                navController.popBackStack()
                            })

                        name = ""
                        number = ""
                        distance = ""
                        location = ""
                        severity = "Select Severity"
                        imageUri = null
                        enabled = false
                    }
                    else{
                        Toast.makeText(context,"Please enter Digits only in Distance", Toast.LENGTH_SHORT).show()
                    }
                }


            }) {
                Text(text = "Submit")
            }
        }
    }
}

@Composable
fun showProgress()
{
    CircularProgressIndicator()
}