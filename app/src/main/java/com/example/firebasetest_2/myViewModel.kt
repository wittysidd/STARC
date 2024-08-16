package com.example.firebasetest_2

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import kotlin.random.Random

val TAG = "FireBaseDemo"

data class ComplaintData(val userID: String?,val complaintID: String,val name: String, val number : String, val location: String,val distance: Double?, var severity: String, var imageUri: String? = null , var isVisible: Boolean = true){
     constructor() : this("","","", "", "", 0.0, "",null)
}
data class AssignmentData(val assignmentID: String?,val name: String, val number : String, val location: String, val distance: Double?, var priority : Boolean,var severity: String, var imageUri: String? = null,var quoteGenerated : Double = 0.0, var daysRequired: Int = 0 ){
    constructor() : this("","", "", "", 0.0,false,"",null)
}
data class ContractData(val contractID: String?, val location: String, val distance: Double?, var priority : Boolean, var severity: String, var durationGiven: String, var quoteGenerated : Double = 0.0, var assigned: Boolean = false){
    constructor() : this("", "",  0.0,false,"","")
}
data class ComplaintHistoryData(val complaintID: String?, val location: String, val distance: Double?, val acceptance: String, var quoteGenerated : Double = 0.0, var daysRequired: Int = 0){
    constructor() : this("", "",  0.0,"Pending")
}
data class LayerMaterialValues(val layerName: String, val materialName: String?,var daysRequired: Int, val height: Double, val density: Int, var rate: Double, var quantity: Double = 0.0, var price : Double = 0.0)
data class ContractorData(val contractorID: String, val companyName: String, val ownerName: String, val contractorEmail: String,val establishmentYear: Int ,val govtContractsCount: Int, val licenseBudget: String, val availableContracts: MutableList<ContractData> = mutableListOf(),val contractsSubmitted: MutableList<ContractData> = mutableListOf()){
    constructor() : this("","","","",0,0,"")
}
data class FinalContractData(var contractDetails: ContractData, var bidAmounts:BidDetails){
    constructor() : this(ContractData(),BidDetails())
}   // for Contractor Screens
data class BidOfContractor(val contractor:ContractorData, val bid:BidDetails){
    constructor(): this(ContractorData(), BidDetails())
}                   // each contractor and their bid (Single)
data class OffersOnContract(val contract: ContractData, val contractorBids: MutableList<BidOfContractor>){
    constructor(): this(ContractData(), mutableListOf())
}   // final list of Contractors and their bids
data class BidDetails(val materialsCost: String, val laboursCost: String, val waterTax: String, val totalCost: String){
    constructor() : this("0.0","0.0", "0.0","0.0")
}
data class ComplainUpdatesData(val complaintID: String,val priority: Boolean, val listing: String, val assigned: String, val quoteGenerated: Double = 0.0, val quoteToBeAssigned: Double = 0.0, val rankedContractor: List<RankedContractor>, val workDone: WorkDone){
    constructor(): this("",false,"Not Listed","Not Assigned",0.0,0.0, listOf(),WorkDone())
}
data class RankedContractor(val name: String, val label: String, val selected: Boolean)
data class WorkDone(var step1:Boolean, var step2:Boolean, var step3:Boolean, var step4:Boolean){
    constructor(): this(false,false,false,false)
}
data class activeContractData(val complaintID:String, val quotation: Double, val workDone:WorkDone){
    constructor(): this("",0.0, WorkDone())
}

class myViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun SaveData(name: String, number: String, location: String, imageUri: Uri? = null, severity: String, distance: Double?, OnSuccess: () -> Unit, OnNoImageSelection: () -> Unit) {
        Log.d(TAG, "Saving Data")
        val database = FirebaseDatabase.getInstance().reference
        val Complaints = database.child("Complaints")
        //val HistoryOfComplaints = database.child("HistoryOfComplaints")

        // val HistoryID = HistoryOfComplaints.push().key ?: return

        var randomNumber: Int
        var existingNumbers: MutableSet<Int> = mutableSetOf()
        do {
            randomNumber = Random.nextInt(10000, 100000) // Generates a number between 10000 and 99999
        } while (randomNumber in existingNumbers)

        existingNumbers.add(randomNumber)

        val ComplaintID = "$randomNumber - ${location}"

        if (currentUser != null) {
            val userId = currentUser.uid
            val userComplaintsRef =
                database.child("Users").child(userId).child("HistoryOfComplaints")
            val userComplaintId = "$randomNumber - $location"

            var userComplaint =
                ComplaintData(
                    userID = userId,
                    complaintID = ComplaintID,
                    name,
                    number,
                    location,
                    distance,
                    severity
                )     // set everything else

            var ComplaintHistory = ComplaintHistoryData(
                complaintID = ComplaintID,
                location = location,
                distance = distance,
                acceptance = "Pending"
            )

            imageUri?.let { uri ->                                          // store image to Storage and get Uri of image and store as string
                val storageRef =
                    FirebaseStorage.getInstance().reference.child("images/$ComplaintID")
                storageRef.putFile(uri)
                    .addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                            userComplaint.imageUri = uri.toString()
                            Complaints.child(ComplaintID).setValue(userComplaint)
                            userComplaintsRef.child(userComplaintId).setValue(ComplaintHistory)
                            Log.d(TAG, "Image uploaded successfully: $uri")
                            OnSuccess()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error uploading image: $exception")
                    }
            } ?: run {                          // if got imageUri then ok else run this:{}
                Complaints.child(ComplaintID).setValue(userComplaint)
                userComplaintsRef.child(userComplaintId).setValue(ComplaintHistory)
                // HistoryOfComplaints.child(HistoryID).setValue(ComplaintHistory)
                OnNoImageSelection()
            }
        } else {
            // if user not logged in ***
        }
    }

    fun viewUserComplaintsHistory(HistoryList: MutableList<ComplaintHistoryData>) {

        val database = FirebaseDatabase.getInstance().reference
        if (currentUser != null) {
            val userId = currentUser.uid
            val userComplaintsRef =
                database.child("Users").child(userId).child("HistoryOfComplaints")


            userComplaintsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    HistoryList.clear()
                    for (childSnapshot in snapshot.children) {
                        val complaints =
                            childSnapshot.getValue(ComplaintHistoryData::class.java)
                        complaints?.let {
                            HistoryList.add(it)
                            Log.d(TAG, "Data retrieved successfully")
                        }
                    }
                    //onDataLoaded(complaintList)

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error retrieving data: ${error.message}")
                }
            })

        } else {
            Log.e(TAG, "Error Logging user")
        }
    }

    /*
    -----------------------------------------------  VVVVVVVVV On Govt. Screen VVVVVVVVV ----------------------------------------------------------------------------
    ------------------------------------------------------------- Govt ------------------------------------------------------------------
    */

    fun SaveContractorData(companyName: String, ownerName: String, email: String, establishmentYear: Int, govtContractsCount: Int, licenseBudget: String, password: String, onSuccess: () -> Unit, onSameEmail: () -> Unit) {
        Log.d(TAG, "Saving Data")
        val database = FirebaseDatabase.getInstance().reference
        val contractorsRef = database.child("Contractors")

        val firebaseAuth =
            FirebaseAuth.getInstance()                           // sign up for creating user in authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("tag", "sign up success - contractor")
                    val currentContractor = FirebaseAuth.getInstance().currentUser
                    if (currentContractor != null) {
                        val contUID = currentContractor.uid
                        val contractorData =
                            ContractorData(
                                contractorID = contUID,
                                companyName = companyName,
                                ownerName = ownerName,
                                contractorEmail = email,
                                establishmentYear = establishmentYear,
                                govtContractsCount = govtContractsCount,
                                licenseBudget = licenseBudget,
                            )

                        contractorsRef.child(contUID)
                            .setValue(contractorData)         // save details in Contractor DB - for future security
                        onSuccess()
                    }
                } else if (task.exception is FirebaseAuthUserCollisionException) {
                    Log.d("tag", "Same email and password")
                    onSameEmail()
                } else {
                    Log.d("tag", "sign up Error")
                }
            }
    }

    fun RetrieveUserComplaints(complaintList: MutableList<ComplaintData>) {
        val database = FirebaseDatabase.getInstance().reference
        val ComplaintDataRef = database.child("Complaints")


        ComplaintDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                complaintList.clear()
                for (childSnapshot in snapshot.children) {
                    val complaints = childSnapshot.getValue(ComplaintData::class.java)
                    complaints?.let {
                        complaintList.add(it)
                        Log.d(TAG, "Data retrieved successfully")
                    }
                }
                //onDataLoaded(complaintList)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error retrieving data: ${error.message}")
            }
        })
    }

    fun OnAssignClick(complaint: ComplaintData, assign: Boolean, priority: Boolean, quoteGenerated: Double, daysRequired: Int, userComplaintsUpdated: MutableList<ComplaintData>) {
        val database = Firebase.database.reference
        val ComplaintsRef = database.child("Complaints")
        val AssignmentsRef = database.child("Assignments MNC")
        val GovtUpdatesRef = database.child("Govt Updates")
        val usersRef = database.child("Users")
        val userID = complaint.userID

        if (assign) {
            if (userID != null) {
                usersRef.child(userID).child("HistoryOfComplaints").orderByChild("complaintID").equalTo(complaint.complaintID)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (childSnapshot in snapshot.children) {
                                // Update the acceptance parameter
                                childSnapshot.ref.child("acceptance").setValue("Accepted")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(TAG, "Error Updating acceptance: ${error.message}")
                        }
                    })
            }
            // Find the entry in the database matching the provided UserData and delete it
            ComplaintsRef.orderByChild("complaintID").equalTo(complaint.complaintID)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childSnapshot in snapshot.children) {
                            val key = childSnapshot.key
                            if (key != null) {
                                // Get the complaint data
                                val complaintData =
                                    childSnapshot.getValue(ComplaintData::class.java)
                                if (complaintData != null) {
                                    // Add the complaint data to the "Assignments" database
                                    val assignmentData = AssignmentData(
                                        assignmentID = complaintData.complaintID,
                                        name = complaintData.name,
                                        number = complaintData.number,
                                        location = complaintData.location,
                                        distance = complaintData.distance,
                                        severity = complaintData.severity,
                                        priority = priority,
                                        imageUri = complaintData.imageUri,
                                        quoteGenerated = quoteGenerated,
                                        daysRequired = daysRequired
                                    )

                                    AssignmentsRef.child(key).setValue(assignmentData)
                                        .addOnSuccessListener {
                                            childSnapshot.ref.removeValue()
                                                .addOnSuccessListener {
                                                    // Successfully removed complaint from "Complaints" database
                                                    // Now update the list of complaints in the UI
                                                    //complaintData.isVisible = false
                                                    userComplaintsUpdated.remove(complaintData)
                                                    Log.d(TAG, "Data updated successfully")
                                                }
                                        }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            ComplaintsRef.orderByChild("complaintID").equalTo(complaint.complaintID)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childSnapshot in snapshot.children) {
                            val key = childSnapshot.key
                            if (key != null) {
                                // Get the complaint data
                                val complaintData =
                                    childSnapshot.getValue(ComplaintData::class.java)
                                if (complaintData != null) {
                                    // Add the complaint data to the "Assignments" database
                                    val updatesData = ComplainUpdatesData(
                                        complaintID = complaintData.complaintID,
                                        listing = "Not Listed",
                                        assigned = "Not Assigned",
                                        quoteGenerated = quoteGenerated,
                                        priority = priority,
                                        quoteToBeAssigned = 0.0,
                                        rankedContractor = listOf(),
                                        workDone = WorkDone()
                                    )

                                    GovtUpdatesRef.child(key).setValue(updatesData)
                                        .addOnSuccessListener {
                                            childSnapshot.ref.removeValue()
                                                .addOnSuccessListener {
                                                    // Successfully removed complaint from "Complaints" database
                                                    // Now update the list of complaints in the UI
                                                    //complaintData.isVisible = false
                                                    userComplaintsUpdated.remove(complaintData)
                                                    Log.d(TAG, "Data updated successfully")
                                                }
                                        }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        } else {
            if (userID != null) {
                usersRef.child(userID).child("HistoryOfComplaints").orderByChild("complaintID")
                    .equalTo(complaint.complaintID)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (childSnapshot in snapshot.children) {
                                // Update the acceptance parameter
                                childSnapshot.ref.child("acceptance")
                                    .setValue("Rejected: Please provide correct Location or Photo")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(TAG, "Error Updating acceptance: ${error.message}")
                        }
                    })
            }
            // Find the entry in the database matching the provided UserData and delete it
            ComplaintsRef.orderByChild("complaintID").equalTo(complaint.complaintID)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childSnapshot in snapshot.children) {
                            val key = childSnapshot.key
                            if (key != null) {
                                // Get the complaint data
                                val complaintData =
                                    childSnapshot.getValue(ComplaintData::class.java)
                                childSnapshot.ref.removeValue()
                                    .addOnSuccessListener {
                                        // Successfully removed complaint from "Complaints" database
                                        // Now update the list of complaints in the UI
                                        //complaintData.isVisible = false
                                        userComplaintsUpdated.remove(complaintData)
                                        Log.d(TAG, "Complaint Rejected , Data updated successfully")
                                    }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }
    }

    fun retrieveUpdatesForComplaints(onDataLoaded: (List<ComplainUpdatesData>) -> Unit) {
        val GovtUpdatesRef = database.child("Govt Updates")
        GovtUpdatesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listOfComplaintUpdates = mutableListOf<ComplainUpdatesData>()
                for (childSnapshot in snapshot.children) {
                    val complaints = childSnapshot.getValue(ComplainUpdatesData::class.java)
                    complaints?.let {
                        listOfComplaintUpdates.add(it)
                    }
                }
                onDataLoaded(listOfComplaintUpdates)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error retrieving data: ${error.message}")
            }
        })
    }

    fun updateDatabase() {
        val database = Firebase.database.reference
        val pricesRef = database.child("Government Prices Database")
        val newConstruction = pricesRef.child("New Road")
        val LayersRef = newConstruction.child("Layers")
        val Layer1Ref = LayersRef.child("Layer 1")
        val Layer2Ref = LayersRef.child("Layer 2")
        val Layer3Ref = LayersRef.child("Layer 3")
        val Layer4Ref = LayersRef.child("Layer 4")

        val repairRoad = pricesRef.child("Repair Road")
        val repairLayersRef = repairRoad.child("Layers")
        val repairLayer3Ref = repairLayersRef.child("Layer 3")
        val repairLayer4Ref = repairLayersRef.child("Layer 4")


        val length = 1000                           // convert in meters
        val width = 6.0                                     // in meters


        val Layer1 = LayerMaterialValues(
            "Base Layer - Subgrade",
            "Crushed Stone 40mm",
            14,
            0.6096,                         // in meters
            1700,                           // in kg/m3
            750.0                              // Rs/Tonne
        )
        if (length != null) {
            Layer1.quantity =
                ((length * width * Layer1.height) * Layer1.density) / 1000        // get value in KG and convert in tonne
        }

        Layer1.price = Layer1.quantity * Layer1.rate

        //-----------

        val Layer2 = LayerMaterialValues(
            "Aggregate Layer 2",
            "Crushed Stone 20mm",
            7,
            0.2032,                         // in meters
            1550,                           // in kg/m3
            900.0                              // Rs/Tonne
        )
        if (length != null) {
            Layer2.quantity =
                ((length * width * Layer2.height) * Layer2.density) / 1000        // get value in KG and convert in tonne
        }

        Layer2.price = Layer2.quantity * Layer2.rate

        //-----------

        val Layer3 = LayerMaterialValues(
            "Binder Course 3",
            "Bitumen + Crushed Stone",
            5,
            0.0762,                         // in meters
            2300,                           // in kg/m3
            41000.0                              // Rs/Tonne
        )
        if (length != null) {
            Layer3.quantity =
                ((length * width * Layer3.height) * Layer2.density) / 1000        // get value in KG and convert in tonne
        }

        Layer3.price = Layer3.quantity * Layer3.rate


        //-----------

        val Layer4 = LayerMaterialValues(
            "Asphalt Layer(Wearing Course) 4",
            "Asphalt Cement + Sand",
            7,
            0.04445,                         // in meters
            2400,                           // in kg/m3
            4000.0                             // Rs/Tonne
        )
        if (length != null) {
            Layer4.quantity =
                ((length * width * Layer4.height) * Layer4.density) / 1000        // get value in KG and convert in tonne
        }

        Layer4.price = Layer4.quantity * Layer4.rate

        // val total = Layer1.price + Layer2.price + Layer3.price + Layer4.price

        Layer1Ref.setValue(Layer1)
        Layer2Ref.setValue(Layer2)
        Layer3Ref.setValue(Layer3)
        Layer4Ref.setValue(Layer4)

        repairLayer3Ref.setValue(Layer3)
        repairLayer4Ref.setValue(Layer4)


    }

    fun generateQuote(complaint: ComplaintData, callbackPrice: (Double, Int) -> Unit) {

        var totalPrice = 0.0
        var totalDays = 0
        val labourCostForNewRoad = 15688530.0             // calculated with Govt provided values(check Notebook and folder of Analysis)
        val labourCostForRoadRepair = 700000.0
        val waterCost = totalPrice * 0.02           // 2% of total
        val contractorsProfit = totalPrice * 0.1    // 10% of total

        Log.d(TAG, "generate quote started")
        val database = Firebase.database.reference
        val pricesRef = database.child("Government Prices Database")
        val newConstruction = pricesRef.child("New Road")
        val LayersRef = newConstruction.child("Layers")

        if (complaint.severity == "No Road (NEW)") {
            LayersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    dataSnapshot.children.forEach { layerSnapshot ->
                        Log.d(TAG, "getting prices for new Road")
                        val priceSnapshot = layerSnapshot.child("price")
                        val daysSnapshot = layerSnapshot.child("daysRequired")
                        val days = daysSnapshot.getValue(Int::class.java)
                        val price = priceSnapshot.getValue(Double::class.java)
                        if (price != null) {
                            totalPrice += price
                            Log.d(TAG, "Price & days added")

                        }
                        if (days != null) {     // Calculating the days required
                            totalDays += days
                            Log.d(TAG, "Price & days added")
                        }
                    }
                    val FinalPrice =
                        (totalPrice + labourCostForNewRoad + waterCost + contractorsProfit) * complaint.distance!!
                    callbackPrice(FinalPrice, (totalDays * complaint.distance).toInt())
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                }
            })
        } else {

            val repairRoad = pricesRef.child("Repair Road")
            val repairLayersRef = repairRoad.child("Layers")

            repairLayersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    dataSnapshot.children.forEach { layerSnapshot ->
                        Log.d(TAG, "getting prices for repair")
                        val priceSnapshot = layerSnapshot.child("price")
                        val daysSnapshot = layerSnapshot.child("daysRequired")
                        val days = daysSnapshot.getValue(Int::class.java)
                        val price = priceSnapshot.getValue(Double::class.java)
                        if (price != null) {
                            totalPrice += price
                            Log.d(TAG, "Price added")
                        }
                        if (days != null) {     // Calculating the days required
                            totalDays += days
                            Log.d(TAG, "Price & days added")
                        }
                    }
                    Log.d(TAG, "Checking Conditions")
                    if (complaint.severity == "High Severity") {
                        val FinalPrice =
                            (totalPrice + labourCostForRoadRepair + waterCost + contractorsProfit) * complaint.distance!!
                        callbackPrice(FinalPrice, (totalDays * complaint.distance).toInt())
                    } else if (complaint.severity == "Medium Severity") {
                        val FinalPrice =
                            (totalPrice + labourCostForRoadRepair + waterCost + contractorsProfit) * complaint.distance!!
                        callbackPrice(
                            FinalPrice - (FinalPrice * 0.25),
                            (totalDays * complaint.distance).toInt()
                        )     // reduce 25% Medium severity
                    } else {
                        val FinalPrice =
                            (totalPrice + labourCostForRoadRepair + waterCost + contractorsProfit) * complaint.distance!!
                        callbackPrice(
                            FinalPrice - (FinalPrice * 0.50),
                            (totalDays * complaint.distance).toInt()
                        )     //  50% reduced Low Severity
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    Log.d(TAG, "Database error: $databaseError")
                }
            })
        }

    }

    // fun OnAssignSwipe(){TODO swipe left or right to accept or reject }

    /*
    -----------------------------------------------  VVVVVVVVV On MNC. Screen VVVVVVVVV ----------------------------------------------------------------------------
    ------------------------------------------------------------- MNC -----------------------------------------------------------------
    */

    fun RetrieveAssignments(AssignmentList: MutableList<AssignmentData>) {
        val database = FirebaseDatabase.getInstance().reference
        val ComplaintDataRef = database.child("Assignments MNC")


        ComplaintDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                AssignmentList.clear()
                for (childSnapshot in snapshot.children) {
                    val complaints = childSnapshot.getValue(AssignmentData::class.java)
                    complaints?.let {
                        AssignmentList.add(it)
                        Log.d(TAG, "Data retrieved successfully")
                    }
                }
                //onDataLoaded(complaintList)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error retrieving data: ${error.message}")
            }
        })
    }

    fun OnListToBidClick(assignment: AssignmentData, updatedAssignmentData: MutableList<AssignmentData>) {
        val database = Firebase.database.reference
        val AssignmentsRef = database.child("Assignments MNC")
        val ContractsRef = database.child("Contracts")
        val OffersRef = database.child("View Offers MNC")
        val contractID = ContractsRef.push().key ?: return

        val ContractorsBidList = mutableListOf<BidOfContractor>()

        // Find the entry in the database matching the provided UserData and delete it
        AssignmentsRef.orderByChild("assignmentID").equalTo(assignment.assignmentID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val key = childSnapshot.key
                        if (key != null) {
                            // Get the assignment data
                            val assignmentData = childSnapshot.getValue(AssignmentData::class.java)
                            if (assignmentData != null) {
                                // Add the assignment data to the "Contracts" database
                                val contractData = ContractData(
                                    contractID = assignmentData.assignmentID,
                                    location = assignmentData.location,
                                    distance = assignmentData.distance,
                                    priority = assignment.priority,
                                    severity = assignmentData.severity,
                                    quoteGenerated = assignmentData.quoteGenerated,
                                    durationGiven = assignmentData.daysRequired.toString()
                                )
                                assignmentData.assignmentID?.let {
                                    OffersRef.child(it).setValue(OffersOnContract(contractData,ContractorsBidList))
                                    ContractsRef.child(it).setValue(contractData)     // will be viewed in MNC screen thus
                                        .addOnSuccessListener {
                                            childSnapshot.ref.removeValue()
                                        }.addOnSuccessListener {
                                            updatedAssignmentData.remove(assignment)
                                        }
                                    //OffersRef.child(it).child("Contractor Bids").setValue(ContractorsList)
                                }



                                // Remove the complaint entry from the "Assignments" database

                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    fun viewOffersFromContractors(onOffersRetrieved: (List<OffersOnContract>) -> Unit) {
        val database = Firebase.database.reference
        val offersRef = database.child("View Offers MNC")

        offersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val offersList = mutableListOf<OffersOnContract>()

                for (contractSnapshot in dataSnapshot.children) {
                    val contractDetails = contractSnapshot.child("contract").getValue(ContractData::class.java)
                    val contractorBidsSnapshot = contractSnapshot.child("contractorBids")
                    val contractorBidsList = mutableListOf<BidOfContractor>()

                    for (contractorBidSnapshot in contractorBidsSnapshot.children) {
                        val bid = contractorBidSnapshot.child("bid").getValue(BidDetails::class.java)
                        val contractorDetails = contractorBidSnapshot.child("contractor").getValue(ContractorData::class.java)

                        if (bid != null && contractorDetails != null) {
                            contractorBidsList.add(BidOfContractor(contractorDetails,bid))
                        }
                    }

                    if (contractDetails != null) {
                        val offers = OffersOnContract(contractDetails, contractorBidsList)
                        offersList.add(offers)
                    }
                }
                onOffersRetrieved(offersList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Database error: ${databaseError.message}")
            }
        })
    }

    fun onSelectingContractor(navController: NavController, rankedContractors: List<Pair<BidOfContractor, String>>, bidOfContractor: BidOfContractor, contractID: String, selected: String) {
        val ContractorId = bidOfContractor.contractor.contractorID
        val offersRef = database.child("View Offers MNC")
        val ContractsRef = database.child("Contracts")
        val GovtUpdatesRef = database.child("Govt Updates")
//        val activeContractsRef = database.child("Active Contracts")
        val submittedContractorRef = database.child("Contractors").child(ContractorId).child("Submitted Contracts").child(contractID)
        //val submittedContractList : MutableList<FinalContractData> = mutableListOf()


        Log.d(TAG, "govtUpdate updated successfully")

        submittedContractorRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val submittedContract = childSnapshot.getValue(FinalContractData::class.java)
                        if (submittedContract != null) {

                            childSnapshot.ref.child("assigned").setValue(true)
                                .addOnSuccessListener {
                                    GovtUpdatesRef.child(contractID).child("assigned").setValue(bidOfContractor.contractor.companyName)
                                    GovtUpdatesRef.child(contractID).child("quoteToBeAssigned").setValue(bidOfContractor.bid.totalCost.toDouble())
                                    // activeContractsRef.child(contractID).setValue(activeContractData(contractID,bidOfContractor.bid.totalCost.toDouble(),WorkDone(false,false,false,false)))
                                    Log.d(TAG, "Assigned value updated successfully")
                                    navController.popBackStack()
                                    offersRef.child(contractID).removeValue()
                                    ContractsRef.child(contractID).removeValue()
                                    Log.d(TAG, " value deleted from Cont,Offers database successfully")

                                }
                                .addOnFailureListener { error ->
                                    Log.e(TAG, "Error updating assigned value: ${error.message}")
                                }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error querying contracts: ${error.message}")
                }
            })

    }

    fun updateCurrentWorks(listOfActiveWorks: MutableList<ComplainUpdatesData>) {
//        val activeContractsRef = database.child("Active Contracts")
        val GovtUpdatesRef = database.child("Govt Updates")
        GovtUpdatesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val activeContract = childSnapshot.getValue(ComplainUpdatesData::class.java)
                    activeContract?.let {
                        listOfActiveWorks.add(it)
                        Log.d(TAG, "Data retrieved successfully")
                    }
                }
                //onDataLoaded(complaintList)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error retrieving data: ${error.message}")
            }
        })
    }

    fun onSubmittingWorkUpdate(contract: ComplainUpdatesData, workDone: WorkDone) {
        val activeContractsRef = database.child("Govt Updates")
        activeContractsRef.child(contract.complaintID).child("workDone").setValue(workDone)
    }
    /*
-----------------------------------------------  VVVVVVVVV On Contractor. Screen VVVVVVVVV ----------------------------------------------------------------------------
------------------------------------------------------------- Contractor -----------------------------------------------------------------
*/

    fun ContractorLogin(email: String, password: String, navController: NavController, OnWrongEmail: () -> Unit, noAccountForEmail: () -> Unit) {
        Log.d("tag", "Contractor Login called")
        val firebaseAuth = FirebaseAuth.getInstance()

        val database = Firebase.database.reference
        val contractorRef = database.child("Contractors")
        val contractorEmails = mutableListOf<String>()

        val currentContractor = FirebaseAuth.getInstance().currentUser
        val retrievedAvailableContractList: MutableList<ContractData> = mutableListOf()


        println("Getting contracts and storing")
        RetrieveContracts(retrievedAvailableContractList)


        contractorRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (eachContractor in snapshot.children) {
                    Log.d("tag", "picking emails")
                    val emailID =
                        eachContractor.child("contractorEmail").getValue(String::class.java)
                    if (emailID != null) {
                        Log.d("tag", "adding emails in list")
                        contractorEmails.add(emailID)
                    }
                }
                if (contractorEmails.contains(email)) {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (currentContractor != null) {
                                    val ContractorId = currentContractor.uid
                                    val availContractsRef =
                                        database.child("Contractors").child(ContractorId).child("Available Contracts")

                                    availContractsRef.setValue(retrievedAvailableContractList).addOnCompleteListener{
                                        if(it.isSuccessful){
                                            Log.d("STARC", "Logging in")
                                        }
                                    }         // set value to current available contracts globally
                                    println("Contracts stored")
                                }
                                Log.d("tag", "contractor sign in success")
                                navController.navigate(Screen.Contractor.route)

                            } else {
                                Log.d("tag", "contractor sign in Error")
                                OnWrongEmail()
                            }
                        }
                } else {
                    Log.d("tag", "no acnt")
                    noAccountForEmail()
                }


            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }

    fun getOwnerName(ownerName: (String)-> Unit){
        val currentContractor = FirebaseAuth.getInstance().currentUser

        if (currentContractor != null) {
            database.child("Contractors").child(currentContractor.uid).child("ownerName")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val nameOfOwner = snapshot.getValue(String::class.java)
                        println("Owner Name: $nameOfOwner")
                        if (nameOfOwner != null) {
                          ownerName(nameOfOwner)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle possible errors.
                        println("Error: ${error.message}")
                    }
                })
        }
    }

    fun RetrieveContracts(ContractList: MutableList<ContractData>) {

        val ComplaintDataRef = database.child("Contracts")

        ComplaintDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ContractList.clear()
                for (childSnapshot in snapshot.children) {
                    val complaints = childSnapshot.getValue(ContractData::class.java)
                    complaints?.let {
                        ContractList.add(it)
                        Log.d(TAG, "contracts retrieved successfully")
                        println(ContractList)
                    }
                }
                //onDataLoaded(complaintList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error retrieving data: ${error.message}")
            }
        })
    }

    fun RetrieveContractsForCurrentContractor(availableContractList: MutableList<ContractData>) {

        val currentContractor = FirebaseAuth.getInstance().currentUser
        // var retrievedAllAvailableContractList: MutableList<ContractData> = mutableListOf()
        if (currentContractor != null) {
            val ContractorId = currentContractor.uid
            val availContractsRef = database.child("Contractors").child(ContractorId)
                .child("Available Contracts") // get specific contractors avail contracts so if we edit it they would be deleted for specific contractor
            val submittedContractorRef =
                database.child("Contractors").child(ContractorId).child("Submitted Contracts")

            availContractsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    availableContractList.clear()
                    for (availableSnapshot in snapshot.children) {
                        val contract = availableSnapshot.getValue(ContractData::class.java)
                        contract?.let {
                            availableContractList.add(it)
                            //retrieved all avail. contracts globally - now remove the contracts we already submitted bid for VVV
                            submittedContractorRef.orderByChild("contractID")
                                .equalTo(contract.contractID)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (submittedSnapshot in snapshot.children) {
                                            val key = submittedSnapshot.key
                                            if (key != null) {
                                                availableSnapshot.ref.removeValue()     // remove submitted values from available one's
                                                availableContractList.remove(contract)  // remove same contract as submitted & update list

                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        //handles any error
                                    }
                                })

                            Log.d(TAG, "Data retrieved successfully")
                        }

                        //onDataLoaded(complaintList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error retrieving data: ${error.message}")
                }
            })
        }
    }

    fun onSubmitBidClick(contract: ContractData, bidDetails: BidDetails, availableContractList: MutableList<ContractData>) {
        val currentContractor = FirebaseAuth.getInstance().currentUser
        val submittedContracts: MutableList<ContractData> = mutableListOf()


        if (currentContractor != null) {
            val ContractorId = currentContractor.uid
            val ContractorRef = database.child("Contractors")

            val availableContractsRef =
                database.child("Contractors").child(ContractorId).child("Available Contracts")
            val submittedContractorRef =
                database.child("Contractors").child(ContractorId).child("Submitted Contracts")
            val OffersRef = database.child("View Offers MNC")

            if (contract.contractID != null) {
               // OffersRef.child(contract.contractID).setValue()
                val finalContract = FinalContractData(
                )
                submittedContractorRef.child(contract.contractID).setValue(contract)
                    .addOnSuccessListener {
                        availableContractsRef.orderByChild("contractID").equalTo(contract.contractID)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (childSnapshot in snapshot.children) {
                                        val key = childSnapshot.key
                                        if (key != null) {
                                            childSnapshot.ref.removeValue()
                                                .addOnSuccessListener {
                                                    availableContractList.remove(contract)
                                                }
                                            // Remove the contract entry from the "AvailableContracts" database
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    //handles any error
                                }
                            })
                    }
                // gets us current contractors info and save it or send it to mnc
                ContractorRef.child(ContractorId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val contractor = snapshot.getValue(ContractorData::class.java)
                        if (contractor != null) {
                            // send the retrieved contractor data & bidAMount to MNC
                            Log.d("STARC", "sent contractor details and bid to MNC")
                            OffersRef.child(contract.contractID).child("contractorBids").child(ContractorId).setValue(BidOfContractor(contractor,bidDetails))
                            //OffersRef.child(contract.contractID).child("Contractor Bids").child(ContractorId).child("Bid Amounts").setValue(bidDetails)
                            Log.d("STARC", "Data: $contractor")

                            // send the bid amounts to contractors SubmittedContracts data
                            submittedContractorRef.child(contract.contractID).setValue(FinalContractData(contract,bidDetails))

                            Log.d("STARC", "Saved Bid in Submitted Contracts RTDB")
                        } else {
                            Log.d("STARC", "No such contractor")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("STARC", "Failed to read data", error.toException())
                    }
                })


            }
        }
    }

    fun RetrieveSubmittedBids(submittedContractList : MutableList<FinalContractData>) {
        val currentContractor = FirebaseAuth.getInstance().currentUser

        if (currentContractor != null) {
            val ContractorId = currentContractor.uid

            val submittedContractorRef = database.child("Contractors").child(ContractorId).child("Submitted Contracts")
            submittedContractorRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    submittedContractList.clear()
                    for (childSnapshot in snapshot.children) {
                        val submittedContract = childSnapshot.getValue(FinalContractData::class.java)
                        if (submittedContract != null) {
                            Log.d(TAG, "${submittedContract.bidAmounts.totalCost} Rs")
                        }
                        submittedContract?.let {
                            submittedContractList.add(it)
                            Log.d(TAG, "submitted contracts retrieved successfully")
                        }
                    }
                    //onDataLoaded(complaintList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error retrieving data: ${error.message}")
                }
            })
        }
    }
}

