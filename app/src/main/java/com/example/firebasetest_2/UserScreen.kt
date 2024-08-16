package com.example.firebasetest_2

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun UserScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome User",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Here you can Register Complains and Check Past History",
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 20.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { navController.navigate(route = "register_complaint_button") }) {
                Text("Register Complaint")
            }
            Button(onClick = { navController.navigate("history_of_complaints") }) {
                Text("History")
            }

        }
    }
}

@Composable
fun ClickableComplaintItem(complaint: ComplaintHistoryData, onItemClick: (ComplaintHistoryData) -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onItemClick(complaint) } // Handle click event
            .fillMaxWidth()
            .border(width = 5.dp, color = Color.LightGray )
    ) {

        val acceptanceColor : Color

        if(complaint.acceptance == "Accepted")
        {
            acceptanceColor = Color.Green
        } else if (complaint.acceptance == "Pending"){
            acceptanceColor = Color(255, 165, 30, 255)
        }else{
            acceptanceColor = Color.Red
        }

        Text(text = "Complaint ID: ${complaint.complaintID}",modifier = Modifier.padding(8.dp,8.dp,0.dp,2.dp))
        Text(text = "Location: ${complaint.location}",modifier = Modifier.padding(8.dp,2.dp,0.dp,2.dp))
        Text(text = "Acceptance Status: ${complaint.acceptance}", color = acceptanceColor, modifier = Modifier.padding(8.dp,2.dp,0.dp,2.dp))
    }
}

@Composable
fun HistoryScreen() {
    val retrievedHistoryList = remember { mutableStateListOf<ComplaintHistoryData>() }
    val viewModel = myViewModel()

    viewModel.viewUserComplaintsHistory(retrievedHistoryList)


    LazyColumn {
        items(retrievedHistoryList) { complaint ->
            ClickableComplaintItem(complaint = complaint, onItemClick = { })
        }
    }
}