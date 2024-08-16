package com.example.firebasetest_2

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ContractorScreen(navController: NavController, ownerName:String)
{

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val gradient = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF0EDED), // Light color at the top
                Color(0, 172, 193, 255)  // Darker color at the bottom
            )
        )

        Box(modifier = Modifier
            .fillMaxWidth(1f)
            .height(70.dp)
            .background(gradient)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Left){
                IconButton(onClick = { navController.popBackStack() }, Modifier.padding(0.dp,10.dp,0.dp,0.dp)) {
                    Icon(Icons.Default.ArrowBack,"", modifier = Modifier.size(35.dp))
                }
                Text(text = "Contractor Section", modifier = Modifier.padding(10.dp,5.dp,0.dp,0.dp),color= Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center, fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){

        Column(modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
           
            Text(text = "Welcome Mr. $ownerName",
                modifier = Modifier.padding(0.dp,0.dp,0.dp,20.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center)
        }

        Column(modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { navController.navigate("check_for_contracts_screen") },
                modifier = Modifier
                    .padding(20.dp)
                    .shadow(elevation = 20.dp)
                    .fillMaxWidth(0.7f),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0, 172, 193, 255) ,
                    contentColor = Color.Black) ){
                Text("Check for Contracts",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif)
            }
            Button(onClick = {navController.navigate("view_submitted_contracts")},
                modifier = Modifier
                    .padding(20.dp)
                    .shadow(elevation = 20.dp)
                    .fillMaxWidth(0.7f),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0, 172, 193, 255) ,
                    contentColor = Color.Black) ) {
                Text("Assigned Contracts",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif)
            }

        }
    }
}

@Composable
fun ContractCard(contract: ContractData, onSubmitClick: (bidDetails:BidDetails) -> Unit) {

    val context = LocalContext.current
    val viewModel = myViewModel()

    val submitEnabled = remember { mutableStateOf(false) }
    var bidButtonEnabled by remember { mutableStateOf(true) }
    val showDialog = remember { mutableStateOf(false) }
    val showProgress = remember { mutableStateOf(false) }
    var materialsQuote by remember { mutableStateOf("") }
    var laboursQuote by remember { mutableStateOf("") }
//    var waterQuote by remember { mutableDoubleStateOf(((laboursQuote.toDoubleOrNull()?.plus(materialsQuote.toDoubleOrNull()!!))?.times(0.02)!!))}
//    var totalQuote by remember { mutableDoubleStateOf(((laboursQuote.toDoubleOrNull()?.plus(materialsQuote.toDoubleOrNull()?.plus(waterQuote)!!)!!)))}

    var waterQuote by remember { mutableStateOf("") }
    var totalQuote by remember { mutableStateOf("") }

    val formatter = NumberFormat.getInstance(Locale("en", "IN")) as DecimalFormat
    formatter.applyPattern("#,##,##,###.00")

    val waterPrice =  (((laboursQuote.toDoubleOrNull() ?: 0.0) + (materialsQuote.toDoubleOrNull() ?: 0.0)) * 0.02)      // auto calculated water tax
    val totalPrice = ((materialsQuote.toDoubleOrNull() ?: 0.0) + (laboursQuote.toDoubleOrNull() ?: 0.0) + waterPrice)   // auto total calculator

    if (!materialsQuote.isDigitsOnly() || !laboursQuote.isDigitsOnly()) {
        Toast.makeText(context, "Please Enter all values(in Digits)!", Toast.LENGTH_SHORT).show()
    } else {
        waterQuote = formatter.format(waterPrice)
        totalQuote = formatter.format(totalPrice)
    }

    if (materialsQuote.isNotEmpty() || laboursQuote.isNotEmpty() || totalQuote.length >= 8) {       // enter in crores only
        submitEnabled.value = true
    }


    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 20.dp)
            .clip(RoundedCornerShape(8.dp))
            .shadow(elevation = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            val quotation = (contract.quoteGenerated / 10000000).roundToInt()
            Text(
                "Contract ID: ${contract.contractID}",
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )
            Text("Location: ${contract.location}")
            Text("Severity: ${contract.severity}")
            Text("Quotation: ${quotation} Cr")
            Text("Duration: ${contract.durationGiven} Days")


            Button(
                onClick = {
                    showDialog.value = true
                    Toast.makeText(context, "Fill the Details", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .padding(15.dp)
                    .shadow(elevation = 15.dp),
                shape = RoundedCornerShape(15.dp),
                enabled = bidButtonEnabled
            ) {
                Text("Bid an Amount")
            }
            if (bidButtonEnabled == false) {
                CircularProgressIndicator()
            }

            if (showDialog.value) {
                Log.d(Tag, "alert dialog called")
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text(text = "Enter your Bid : ") },
                    confirmButton = {
                        Row(horizontalArrangement = Arrangement.Absolute.Left) {
                            Button(onClick = {
                                showDialog.value = false
                                materialsQuote = ""
                                laboursQuote = ""
                                waterQuote = ""
                                totalQuote = ""
                            }) {
                                Text("Close")                       // enable bid button
                            }

                            Spacer(modifier = Modifier.padding(20.dp))

                            val WaterPrice = formatter.parse(waterQuote)?.toDouble()
                            val TotalPrice = formatter.parse(totalQuote)?.toLong()

                            val bidAmounts = BidDetails(
                                materialsQuote,
                                laboursQuote,
                                WaterPrice.toString(),
                                TotalPrice.toString()
                            )
                            Button(
                                onClick = {
                                    Toast.makeText(
                                        context,
                                        "Sending Bid Amount to MNC...",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onSubmitClick(bidAmounts)
                                    Toast.makeText(
                                        context,
                                        "Sent Succuessfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    showDialog.value = false
                                    //bidButtonEnabled = false
                                    materialsQuote = ""
                                    laboursQuote = ""
                                    waterQuote = ""
                                    totalQuote = ""
                                },
                                enabled = submitEnabled.value
                            ) {        // send data of bid to MNC + save to Contractors Assigned Contracts button
                                Text("Submit")  //
                            }
                        }

                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            OutlinedTextField(
                                value = (materialsQuote),
                                onValueChange = { materialsQuote = it },
                                modifier = Modifier.padding(8.dp),
                                label = { Text("Materials Quote") })
                            OutlinedTextField(
                                value = laboursQuote,
                                onValueChange = { laboursQuote = it },
                                modifier = Modifier.padding(8.dp),
                                label = { Text("Labours Quote") })
                            OutlinedTextField(
                                value = waterQuote.toString(),
                                onValueChange = { },
                                modifier = Modifier.padding(8.dp),
                                label = { Text("Water Charges") },
                                enabled = false
                            )
                            OutlinedTextField(
                                value = totalQuote.toString(),
                                onValueChange = { },
                                modifier = Modifier.padding(8.dp),
                                label = { Text("Total Amount") },
                                enabled = false
                            )
                        }
                    }
                )
            }

        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CheckForContractsScreen() {
    val retrievedContractData = remember { mutableStateListOf<ContractData>() }
    val viewModel = myViewModel()

    viewModel.RetrieveContractsForCurrentContractor(retrievedContractData)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Log.d(tag, "b4 lazy column")

        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize(tween(2000))
        ) {

            if (retrievedContractData.isEmpty()) {
                item {
                    Text(
                        text = "No Contracts left, go back.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .animateItemPlacement(tween(2000)),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(retrievedContractData)
                { contract ->
                    AnimatedVisibility(
                        visible = true,
                        modifier = Modifier.animateItemPlacement(tween(2000))
                    ) {
                        ContractCard(contract) {
                            viewModel.onSubmitBidClick(contract, it, retrievedContractData)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClickableSubmittedContract(contract: FinalContractData, onItemClick: (FinalContractData) -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onItemClick(contract) } // Handle click event
            .fillMaxWidth()
            .border(width = 5.dp, color = Color.LightGray)
    ) {

        val formatter = NumberFormat.getInstance(Locale("en", "IN")) as DecimalFormat
        formatter.applyPattern("#,##,##,###.00")

       val totalCost = formatter.format(contract.bidAmounts.totalCost.toDouble())

        val assign = contract.contractDetails.assigned
        var textForAssign : String = "Not Assigned"
        var colorOfAssign: Color = Color.Black
        if(assign == true){
            textForAssign = "Contract Assigned !"
            colorOfAssign = Color(67, 160, 71, 255)
        }
        else{
            textForAssign = "Not Assigned"
            colorOfAssign = Color(255, 165, 30, 255)
        }

        Text(text = "Contract ID: ${contract.contractDetails.contractID}",modifier = Modifier.padding(15.dp,15.dp,0.dp,2.dp) , fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text(text = "Location: ${contract.contractDetails.location}",modifier = Modifier.padding(15.dp,2.dp,0.dp,15.dp))
        Text(text = "Bid Amount: ${totalCost} Rs",modifier = Modifier.padding(15.dp,2.dp,0.dp,2.dp))
        Text(text = "Assigned: ${textForAssign}",modifier = Modifier.padding(15.dp,2.dp,0.dp,15.dp) , fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colorOfAssign)


    }
}


@Composable
fun ViewSubmittedContractsScreen() {
    val retrievedSubmittedList = remember { mutableStateListOf<FinalContractData>() }
    val viewModel = myViewModel()

    viewModel.RetrieveSubmittedBids(retrievedSubmittedList)

    LazyColumn {
        items(retrievedSubmittedList) { contract ->
            ClickableSubmittedContract(contract = contract, onItemClick = {})
        }
    }
}

@Composable
fun OnSubmittedContractClickScreen()
{
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {

    }
}