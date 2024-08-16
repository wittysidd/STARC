package com.example.firebasetest_2

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt


val Tag = "FireBaseDemo"
@Composable
fun GovtScreen(navController : NavController) {

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val gradient = Brush.verticalGradient(
            colors = listOf(
                Color(244, 81, 30, 255),
                Color(0xFFF0EDED),
                Color(6, 136, 12, 255)
            )
        )

        Box(modifier = Modifier
            .fillMaxWidth(1f)
            .height(70.dp)
            .background(gradient)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Left){
                IconButton(onClick = { navController.popBackStack() }, Modifier.padding(0.dp,10.dp,0.dp,0.dp)) {
                    Icon(Icons.Default.ArrowBack,"", modifier = Modifier.size(35.dp))
                }
                Text(text = "Government Section", modifier = Modifier.padding(10.dp,5.dp,0.dp,0.dp),color=Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center, fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic)
            }
        }
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

            Button(
                onClick = {
                    Toast.makeText(context, "Looking for Complaints...", Toast.LENGTH_SHORT).show()
                    navController.navigate("check_for_complaints_screen")
                },
                modifier = Modifier
                    .padding(20.dp)
                    .shadow(elevation = 20.dp)
                    .fillMaxWidth(0.7f),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(251, 100, 0, 255),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Check For Complaints",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif
                )
            }

            Button(
                onClick = {
                          navController.navigate("view_updates_for_Complaint")
                }, modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(20.dp)
                    .shadow(elevation = 30.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(251, 100, 0, 255),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Check for Updates",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif
                )
            }

            Button(
                onClick = { navController.navigate("add_contractor_screen") },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(0.dp, 75.dp, 0.dp, 0.dp)
                    .shadow(elevation = 20.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(255, 179, 0, 255),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    "Add Contractor",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif
                )
            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun CheckForComplaintsScreen() {
    val retrievedComplaintData = remember { mutableStateListOf<ComplaintData>() }
    val viewModel = myViewModel()


    viewModel.RetrieveUserComplaints(retrievedComplaintData)
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Log.d(Tag, "b4 lazy column")


        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize(tween(2000))
        ) {

            if (retrievedComplaintData.isEmpty()) {
                item {
                    Text(
                        text = "No complaints left, go back",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .animateItemPlacement(tween(2000)),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(
                    items = retrievedComplaintData,
                    key = { complaint -> complaint.complaintID as Any }
                )
                { complaint ->
                    // val complaint = retrievedComplaintData[complaint]
                    AnimatedVisibility(
                        visible = true,
                        modifier = Modifier.animateItemPlacement(tween(2000)),
                        //exit = shrinkOut(tween(1500))

                    ) {
                        UserComplaintCard(complaint)
                        { priority,assign ->
                            viewModel.updateDatabase()
                            viewModel.generateQuote(complaint)
                            { quote,days ->
                                viewModel.OnAssignClick(
                                    complaint,
                                    assign,
                                    priority,
                                    quote,
                                    days,
                                    retrievedComplaintData
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun UserComplaintCard( complaintData: ComplaintData, onAssignClicked: (Boolean,Boolean) -> Unit) {

    Card(
        modifier = Modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth(0.9f)
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(5.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Complaint ID: ${complaintData.complaintID}")
            Text("Name: ${complaintData.name}")
            Text("Number: ${complaintData.number}")
            Text("Location: ${complaintData.location}")
            Text("Severity: ${complaintData.severity}")

            val context = LocalContext.current
            val showDialog = remember { mutableStateOf(false) }
            var priority = remember { mutableStateOf(false) }
            var visibility = remember { mutableStateOf(false) }
            Text(
                text = "View Image",
                color = Color.Blue,
                modifier = Modifier.clickable {

                    if (!complaintData.imageUri.isNullOrEmpty()) {
                        Log.d(Tag,"must be loading")
                        Toast.makeText(context, "Loading Image...", Toast.LENGTH_LONG).show()
                        showDialog.value = true
                    } else {
                        Toast.makeText(context, "No image available", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            if (showDialog.value) {
                Log.d(Tag,"alert dialog called")
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text(text = "Road Image") },
                    confirmButton = {
                        Button(onClick = { showDialog.value = false }) {
                            Text("Close")
                        }
                    },
                    text = {
                        Image(
                            painter = rememberImagePainter(
                                data = complaintData.imageUri),
                            contentDescription = null,
                            modifier = Modifier.size(300.dp) // Adjust size as needed
                        )
                    }
                )
            }
            var enabled by remember { mutableStateOf(true) }
            var assign by remember { mutableStateOf(true) }
            Row(
                modifier = Modifier.padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Checkbox(
                    checked = priority.value,
                    onCheckedChange = { isChecked ->
                        priority.value = isChecked
                    },
                    enabled = enabled
                )

                Text(text = "High Priority? ")
            }

            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    Toast.makeText(context,"Complaint Rejected",Toast.LENGTH_SHORT).show()
                    assign = false
                    onAssignClicked(priority.value, assign)
                    priority.value = false
                    enabled = false
                },
                    enabled = enabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(229, 57, 53, 255),
                        contentColor = Color.Black)

                    ) {
                    Text("Reject")
                }
                Button(onClick = {
                    Toast.makeText(context,"Assigned to MNC",Toast.LENGTH_SHORT).show()
                    onAssignClicked(priority.value, assign)
                    priority.value = false
                    enabled = false
                },
                    enabled = enabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(35, 180, 42, 255),
                        contentColor = Color.Black)
                    ) {
                    Text("Assign to MNC")
                }
            }


            if (enabled == false){
                CircularProgressIndicator()
            }

        }
    }
}

@Composable
fun AddContractorScreen(navController:NavController){

    val viewModel = myViewModel()
    var showText by remember { mutableStateOf(false) }
    var enabled by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(false) }

    var showTextOfYear by remember { mutableStateOf(false) }
    var showTextOfCount by remember { mutableStateOf(false) }

    val context = LocalContext.current


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Create new Account",
                fontSize = 25.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                "Please Enter your Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            var ownerName by remember { mutableStateOf("") }
            var companyName by remember { mutableStateOf("") }
            var establishmentYear by remember { mutableStateOf("") }
            var govtContractsCount by remember { mutableStateOf("")}
            var licenseBudget by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it }, singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Company Name", color = Color.Black) },
                )
                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it }, singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Owner Name", color = Color.Black) }
                )
                if(showTextOfYear == true){
                    Text("Please Enter Years in Digits", color = Color.Red)
                }
                OutlinedTextField(
                    value = establishmentYear,
                    onValueChange = { establishmentYear = it }, singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Establishment Year", color = Color.Black) }
                )
                if(showTextOfCount == true){
                    Text("Please Enter Count in Digits", color = Color.Red)
                }
                OutlinedTextField(
                    value = govtContractsCount,
                    onValueChange = { govtContractsCount = it }, singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "No. of Govt. Contracts", color = Color.Black) }
                )

                OutlinedTextField(
                    value = licenseBudget,
                    onValueChange = { licenseBudget = it }, singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "License Budget", color = Color.Black) }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it }, singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Email ID", color = Color.Black) }
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Create Password", color = Color.Black) },
                    visualTransformation = PasswordVisualTransformation()
                )

                 Button(onClick = {
                     showTextOfYear = false
                     showTextOfCount = false
                     if (establishmentYear.isDigitsOnly() && govtContractsCount.isDigitsOnly()) {

                         if (companyName.isEmpty() || ownerName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(context, "Please Enter all fields", Toast.LENGTH_SHORT)
                                .show()
                         }else {
                            enabled = false
                            progress = true
                            viewModel.SaveContractorData(
                                companyName = companyName,
                                ownerName = ownerName,
                                email = email,
                                establishmentYear = establishmentYear.toInt(),
                                govtContractsCount = govtContractsCount.toInt(),
                                licenseBudget = licenseBudget,
                                password = password,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Account Created Successfully for Contractor",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.popBackStack()
                                },
                                onSameEmail = { showText = true })
                        }
                    }
                    else{
                        enabled = true
                        if(establishmentYear.isDigitsOnly() == false){
                            showTextOfYear = true
                        }
                        else{
                            showTextOfCount = true
                        }
                    }
                },
                    enabled = enabled) {
                    Text("Create Account")
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Create Account"
                    )
                }

                if(showText)
                {
                    Text("This account already Exists!", color = Color.Red)
                    progress = false
                    enabled = true
                }
                if(progress)
                {
                  //  HideKeyboard()
                    CircularProgressIndicator()
                }
            }
        }
    }
}



@Composable
fun ClickableComplaintUpdateItem(contract: ComplainUpdatesData, onItemClick: (ComplainUpdatesData) -> Unit) {

}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewUpdatesForComplaintScreen(navController: NavController) {
    val viewModel = myViewModel()
    val listOfComplaintUpdates = remember { mutableStateOf<List<ComplainUpdatesData>>(emptyList()) }

    // Call the function to retrieve data
    LaunchedEffect(Unit) {
        viewModel.retrieveUpdatesForComplaints { updates ->
            listOfComplaintUpdates.value = updates
        }
    }

    // Display the list using LazyColumn
    LazyColumn {
        if (listOfComplaintUpdates.value.isEmpty()) {
            item {
                Text(
                    text = "No Contracts left, go back.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(listOfComplaintUpdates.value) { contract ->
                AnimatedVisibility(
                    visible = true,
                    modifier = Modifier.animateItemPlacement(tween(2000))
                ) {
                    ActiveWorkCard(contract)
                }
            }
        }
    }
}

@Composable
fun ActiveWorkCard(activeWork:ComplainUpdatesData) {

    var expanded by remember { mutableStateOf(false) }

    //val quotation = (activeWork.quotation/10000000).roundToInt()

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { expanded = !expanded }
            .animateContentSize(tween(1000))

    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            val assignmentPriority: String
            if (activeWork.priority) {
                assignmentPriority = "High Priority"
            } else {
                assignmentPriority = "No Priority"
            }
            val formatter = NumberFormat.getInstance(Locale("en", "IN")) as DecimalFormat
            formatter.applyPattern("#,##,##,###.00")
            val quotation = formatter.format(activeWork.quoteGenerated.roundToInt())
            val assignQuote = formatter.format(activeWork.quoteToBeAssigned.roundToInt())

            Text(
                "Complaint ID: ${activeWork.complaintID}",
                modifier = Modifier.padding(0.dp, 2.dp, 0.dp, 2.dp),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Contractor: ${activeWork.assigned}",
                modifier = Modifier.padding(0.dp, 2.dp, 0.dp, 2.dp),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Quote: ${quotation} Cr",
                modifier = Modifier.padding(0.dp, 2.dp, 0.dp, 10.dp),
                fontSize = 15.sp
            )

            var workDonePercent by remember { mutableStateOf("0%") }
            var fundsUsed: Double = 0.0

            if (expanded) {
                when {
                    activeWork.workDone.step4 -> {
                        workDonePercent = "100%"
                        fundsUsed = activeWork.quoteToBeAssigned * 1
                    }

                    activeWork.workDone.step3 -> {
                        workDonePercent = "75%"
                        fundsUsed = activeWork.quoteToBeAssigned * 0.75
                    }

                    activeWork.workDone.step2 -> {
                        workDonePercent = "50%"
                        fundsUsed = activeWork.quoteToBeAssigned * 0.5
                    }

                    activeWork.workDone.step1 -> {
                        workDonePercent = "25%"
                        fundsUsed = activeWork.quoteToBeAssigned * 0.25
                    }

                    else -> {
                        workDonePercent = "0%"
                        fundsUsed = activeWork.quoteToBeAssigned * 0
                    }
                }


                Text(text = "Assigned Quote: ${assignQuote} Cr")
                Text("Work Done : $workDonePercent")
                val funds = formatter.format(fundsUsed)
                Text("Funds Used : $funds")
            }
        }
    }
}

@Composable
fun CheckForActiveUpdates(navController:NavController) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val viewModel = myViewModel()

        val retrievedActiveWorkList = remember { mutableStateListOf<ComplainUpdatesData>() }

        viewModel.updateCurrentWorks(retrievedActiveWorkList)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(retrievedActiveWorkList) { contract ->
                ActiveWorkCard(contract)
            }
        }
    }
}
