package com.example.firebasetest_2


import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkOut
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.navigation.NavController
import com.google.gson.Gson
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun MNCScreen(navController: NavController)
{

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val gradient = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF0EDED), // Light color at the top
                Color(142, 36, 170, 255)  // Darker color at the bottom
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
                Text(text = "Municipal Corporation", modifier = Modifier.padding(10.dp,5.dp,0.dp,0.dp),color=Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center, fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic)
            }
        }
    }

    Column (modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome MNC officer",
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 20.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { navController.navigate("check_for_assignment_screen") },
                modifier = Modifier
                    .padding(20.dp)
                    .shadow(elevation = 20.dp)
                    .fillMaxWidth(0.7f),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(142, 36, 170, 255),
                    contentColor = Color.White)) {
                Text("Check for Assignments",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif)
            }

            Button(onClick = {navController.navigate("view_offers_screen") },
                modifier = Modifier
                    .padding(20.dp)
                    .shadow(elevation = 20.dp)
                    .fillMaxWidth(0.7f),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(142, 36, 170, 255),
                    contentColor = Color.White)) {
                Text("Offers from Contractors",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif)
            }
            Button(onClick = { navController.navigate("update_current_works_screen") },
                modifier = Modifier
                    .padding(20.dp)
                    .shadow(elevation = 20.dp)
                    .fillMaxWidth(0.7f),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(142, 36, 170, 255),
                    contentColor = Color.White)) {
                Text("Update Current Works",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif)
            }


        }
    }
}



@Composable
fun AssignmentCard(assignment: AssignmentData, OnOpenContractClicked: () -> Unit) {

    var expanded by remember { mutableStateOf(false) }

    val assignmentPriority: String
    val cardColor: Color
    if (assignment.priority) {
        assignmentPriority = "High Priority"
        cardColor = Color.Red
    } else {
        assignmentPriority = "No Priority"
        cardColor = Color(255, 140, 60, 255)
    }

    val quotation = (assignment.quoteGenerated/10000000).roundToInt()

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { expanded = !expanded }
            .animateContentSize(tween(1000))
        ,
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Assignment ID: ${assignment.assignmentID}")
            Text("Priority: ${assignmentPriority}")


            if (expanded) {

                Text("Name: ${assignment.name}")
                Text("Number: ${assignment.number}")
                Text("Location: ${assignment.location}")
                Text("Distance: ${assignment.distance} km")
                Text("Duration: ${assignment.daysRequired} days")
                Text("Quote: ${quotation} Cr")
                Text("Severity: ${assignment.severity} ")


                val context = LocalContext.current
                //val showDialog = remember { mutableStateOf(false) }

                Button(onClick = {
                    Toast.makeText(context, "Contract now open to Bid...", Toast.LENGTH_SHORT)
                        .show()
                    OnOpenContractClicked()
                }) {
                    Text("Open Contract for Bidding")
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CheckForAssignmentsScreen() {
    val retrievedAssignmentData = remember { mutableStateListOf<AssignmentData>() }
    val viewModel = myViewModel()

    viewModel.RetrieveAssignments(retrievedAssignmentData)
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Log.d(TAG, "b4 lazy column")

        Text(
            text = "Check for Assignments",
            modifier = Modifier.padding(0.dp, 50.dp, 0.dp, 20.dp),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Click for Details/Get Offers",
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 20.dp),
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                //.animateContentSize(tween(200))
        ) {

            if (retrievedAssignmentData.isEmpty()) {
                item {
                    Text(
                        text = "No Assignments left, go back.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .animateItemPlacement(tween(1800)),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(retrievedAssignmentData,
                    key = {assignment -> assignment.assignmentID as Any})
                { assignment ->                             // declared here bcz cardColor needs to be sent back thus.

                    AnimatedVisibility(
                        visible = true,
                        modifier = Modifier.animateItemPlacement(tween(2000)),
                        exit = shrinkOut(tween(2000))
                    ) {
                        AssignmentCard(assignment) {
                            viewModel.OnListToBidClick(assignment, retrievedAssignmentData)
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun ClickableContractItem(contract: OffersOnContract, onItemClick: (OffersOnContract) -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onItemClick(contract) } // Handle click event
            .fillMaxWidth()
            .border(width = 5.dp, color = Color.LightGray)
    ) {
        val assignmentPriority: String
        if (contract.contract.priority) {
            assignmentPriority = "High Priority"
        } else {
            assignmentPriority = "No Priority"
        }

        Text(text = "Contract ID: ${contract.contract.contractID}",modifier = Modifier.padding(15.dp,15.dp,0.dp,2.dp) , fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text(text = "Location: ${assignmentPriority}",modifier = Modifier.padding(15.dp,2.dp,0.dp,15.dp))

    }
}



@Composable
fun ViewOffersScreen(navController:NavController) {

    val viewModel = myViewModel()

    val retrievedOffersList = remember { mutableStateOf(listOf<OffersOnContract>()) }

    viewModel.viewOffersFromContractors { offersList ->
        retrievedOffersList.value = offersList
    }

    LazyColumn {
        items(retrievedOffersList.value) { contract ->
            ClickableContractItem(contract = contract, onItemClick = {
                val offersJson = Gson().toJson(contract)
                Log.d("ContractListScreen", "Navigating with data: $offersJson")
                navController.navigate("show_list_of_contractor_bids/$offersJson")
            })
        }
    }
}

fun calculatePoints(offers: OffersOnContract): List<Pair<BidOfContractor, Int>> {
    val maxContractsDone = offers.contractorBids.maxOfOrNull { it.contractor.govtContractsCount } ?: 0
    val minYearOfEstablishment = offers.contractorBids.minOfOrNull { it.contractor.establishmentYear } ?: Int.MAX_VALUE     // get the min( oldest company)

    return offers.contractorBids.map { bidOfContractor ->           // return the List* of (bidOfContractor (calculating points) and points
        var points = 0

        // Bid matching the quote points
        val bidDifference = abs(bidOfContractor.bid.totalCost.toDouble() - offers.contract.quoteGenerated) / bidOfContractor.bid.totalCost.toDouble()
        points += when {
            bidDifference <= 0.05 -> 10
            bidDifference <= 0.10 -> 7
            bidDifference <= 0.20 -> 5
            else -> 3
        }

        // Number of contracts done points
        points += when (bidOfContractor.contractor.govtContractsCount) {
            maxContractsDone -> 10
            else -> when {
                bidOfContractor.contractor.govtContractsCount >= maxContractsDone * 0.7 -> 7
                bidOfContractor.contractor.govtContractsCount >= maxContractsDone * 0.5 -> 5
                else -> 3
            }
        }

        // Year of establishment points
        points += when (bidOfContractor.contractor.establishmentYear) {
            minYearOfEstablishment -> 10
            else -> when {
                bidOfContractor.contractor.establishmentYear < minYearOfEstablishment + 5 -> 7
                bidOfContractor.contractor.establishmentYear < minYearOfEstablishment + 10 -> 5
                else -> 3
            }
        }

        bidOfContractor to points
    }
}

fun rankContractors(offers: OffersOnContract): List<Pair<BidOfContractor, String>> {
    val contractorPoints = calculatePoints(offers)                              // [{contract1,25}, {contractor2,18}, ....
    println(contractorPoints)
    val sortedContractors = contractorPoints.sortedByDescending { it.second }       // sort based on second element of pair i.e. points

    return sortedContractors.mapIndexed { index, pair ->
        val label = when (index) {
            0 -> "Best"
            1 -> "Good"
            2 -> "Average"
            else -> "Other"
        }
        pair.first to label     // therefore pair 1st element(i.e.contractor) with assigned label
    }
}

fun getBorderColor(label: String): Color {
    return when (label) {
        "Best" -> Color(0xFF1DAD24)
        "Good" -> Color(0xFFFB8C00)
        "Average" -> Color(0xFFFFA500)
        else -> Color.Red
    }
}
@Composable
fun ShowListOfContractorBids(offers : OffersOnContract, navController: NavController)
{
    val rankedContractors = rankContractors(offers)
    val viewModel = myViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp, 30.dp, 5.dp, 20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val quotation = (offers.contract.quoteGenerated/10000000).roundToInt()
        val contractID = offers.contract.contractID
        Text(text = "Contract ID: ${contractID}",fontWeight = FontWeight.Medium, fontSize = 18.sp, textAlign = TextAlign.Center)
        Text(text = "Location: ${offers.contract.location}",textAlign = TextAlign.Center)
        Text(text = "Quote: $quotation Cr",fontWeight = FontWeight.Bold, fontSize = 15.sp,textAlign = TextAlign.Center)

        LazyColumn {

            items(rankedContractors) {(bidOfContractor,label)->
                ContractorBidDetail(bidOfContractor, label){
                    val selected = "SELECTED"
                    if (contractID != null) {
                        viewModel.onSelectingContractor(navController,rankedContractors,bidOfContractor,contractID,selected)
                    }
                }
            }
        }
    }
    
}

@Composable
fun ContractorBidDetail(contractor: BidOfContractor, label: String, onSelect:()->Unit) {



    val borderColor = getBorderColor(label)
    Column(modifier = Modifier
        .padding(10.dp)
        .border(10.dp, borderColor, RoundedCornerShape(10.dp))
        .fillMaxWidth(0.9f)) {
        val bidInCR = (contractor.bid.totalCost.toDouble())/10000000
        val context = LocalContext.current
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 15.sp,modifier = Modifier.padding(20.dp,20.dp,2.dp,2.dp))
        Text(text = "Contractor Name: ${contractor.contractor.companyName}", fontWeight = FontWeight.Medium, fontSize = 16.sp,modifier = Modifier.padding(20.dp,2.dp,2.dp,2.dp))
        Text(text = "Establishment year : ${contractor.contractor.establishmentYear}",modifier = Modifier.padding(20.dp,2.dp,2.dp,2.dp))
        Text(text = "Past Contract : ${contractor.contractor.govtContractsCount} Done",modifier = Modifier.padding(20.dp,2.dp,2.dp,2.dp))
        Text(text = "Bid Amount: $bidInCR Cr",modifier = Modifier.padding(20.dp,2.dp,2.dp,2.dp))
        Button(onClick = {onSelect()
                         Toast.makeText(context,"Selected Contractor", Toast.LENGTH_SHORT).show()
                         },modifier = Modifier.padding(20.dp,5.dp,2.dp,2.dp)){
            Text("Select")
        }
        Spacer(modifier = Modifier.padding(10.dp))
    }
}


@Composable
fun ClickableActiveContract(contract: ComplainUpdatesData, onItemClick: (ComplainUpdatesData) -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onItemClick(contract) } // Handle click event
            .fillMaxWidth()
            .border(width = 5.dp, color = Color.LightGray)
    ) {

        val viewModel = myViewModel()
        Text(text = "Contract ID: ${contract.complaintID}",modifier = Modifier.padding(15.dp,15.dp,0.dp,2.dp) , fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text(text = "Funds Assigned : ${contract.quoteToBeAssigned}",modifier = Modifier.padding(15.dp,2.dp,0.dp,15.dp))

        val showDialog = remember { mutableStateOf(false) }
        var enabled by remember { mutableStateOf(true) }
        var step1 by remember { mutableStateOf(contract.workDone.step1) }
        var step2 by remember { mutableStateOf(contract.workDone.step2) }
        var step3 by remember { mutableStateOf(contract.workDone.step3) }
        var step4 by remember { mutableStateOf(contract.workDone.step4) }

        Button(onClick = { showDialog.value = true },modifier = Modifier.padding(20.dp,2.dp,2.dp,20.dp)) {
            Text("Update Work Status")
        }
        if (showDialog.value) {
            Log.d(Tag, "alert dialog called")
            val context = LocalContext.current
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text(text = "Enter your Bid : ") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {

                        Row(
                            modifier = Modifier.padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Checkbox(
                                checked = step1,
                                onCheckedChange = { isChecked ->
                                    step1 = isChecked
                                },
                                enabled = !contract.workDone.step1
                            )

                            Text(text = "Step1 - Base Layer - Subgrade ")
                        }
                        Row(
                            modifier = Modifier.padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Checkbox(
                                checked = step2,
                                onCheckedChange = { isChecked ->
                                    step2 = isChecked
                                },
                                enabled = !contract.workDone.step2
                            )

                            Text(text = "Step2 - Aggregate Layer 2 ")
                        }
                        Row(
                            modifier = Modifier.padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Checkbox(
                                checked = step3,
                                onCheckedChange = { isChecked ->
                                    step3 = isChecked
                                },
                                enabled = !contract.workDone.step3
                            )

                            Text(text = "Step3 - Binder Course ")
                        }
                        Row(
                            modifier = Modifier.padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Checkbox(
                                checked = step4,
                                onCheckedChange = { isChecked ->
                                    step4 = isChecked
                                },
                                enabled = !contract.workDone.step4
                            )

                            Text(text = "Step4 - Asphalt Layer(Wearing Course) ")
                        }
                    }
                },
                confirmButton = {
                    Row(horizontalArrangement = Arrangement.Absolute.Left) {
                        Button(onClick = {
                            showDialog.value = false
                        }) {
                            Text("Close")                       // enable bid button
                        }

                        Spacer(modifier = Modifier.padding(20.dp))

                        Button(onClick = {
                            viewModel.onSubmittingWorkUpdate(contract,WorkDone(step1,step2,step3,step4))
                            showDialog.value = false
                            Toast.makeText(context, "Updating work status.", Toast.LENGTH_SHORT).show()}
                        ) {        // send data of bid to MNC + save to Contractors Assigned Contracts button
                            Text("Submit")
                        }
                    }

                }
            )
        }
    }
}




@Composable
fun UpdateCurrentWorksScreen(navController:NavController) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        val viewModel = myViewModel()

        val retrievedActiveWorkList = remember { mutableStateListOf<ComplainUpdatesData>() }

        viewModel.updateCurrentWorks(retrievedActiveWorkList)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(retrievedActiveWorkList) { contract ->
                ClickableActiveContract(contract = contract, onItemClick = {
                })
            }
        }
    }
}
