package com.example.exportify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exportify.Adapters.SeviceGigsAdapter
import com.example.exportify.databinding.ActivityBuyerDashboardBinding
import com.example.exportify.models.BuyerRequestsModel
import com.example.exportify.models.ServiceGig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Buyer_dashboard : AppCompatActivity() {

    private lateinit var binding: ActivityBuyerDashboardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private var mList = ArrayList<ServiceGig>()
    private lateinit var adapter: SeviceGigsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize variables
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("service_gigs")


        binding.btnAddRequest.setOnClickListener {
            intent = Intent(applicationContext, CreateRequest::class.java)
            startActivity(intent)
        }



        var recyclerView = binding.recyclerview

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this);

        //addDataToList()
        retrieveData()

        adapter = SeviceGigsAdapter(mList)
        recyclerView.adapter = adapter

        //Setting onclick on recyclerView each item
        adapter.setOnItemClickListner(object: SeviceGigsAdapter.onItemClickListner {
            override fun onItemClick(position: Int) {
                intent = Intent(applicationContext, OrderDetails::class.java).also {
                    it.putExtra("topic", mList[position].topic)
                    it.putExtra("type", mList[position].type)
                    it.putExtra("des", mList[position].description)
                    it.putExtra("noOfUnits", mList[position].noOfUnits)
                    it.putExtra("pricePerUnit", mList[position].price)
                    it.putExtra("reqId", mList[position].id)
                    startActivity(it)
                }
            }
        })

        binding.ivProfile.setOnClickListener {
            intent = Intent(applicationContext, BuyerProfile::class.java)
            startActivity(intent)
        }
    }

    private fun addDataToList(){
        mList.add(ServiceGig("topic", "This is des","150000 - 210000","",""))
        mList.add(ServiceGig("topic", "This is des","150000 - 210000","",""))
        mList.add(ServiceGig("topic", "This is des","150000 - 210000","",""))
        mList.add(ServiceGig("topic", "This is des","150000 - 210000","",""))
        mList.add(ServiceGig("topic", "This is des","150000 - 210000","",""))
        mList.add(ServiceGig("topic", "This is des","150000 - 210000","",""))
    }

    private fun retrieveData() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for ( snapshot in snapshot.children){
                    val gig = snapshot.getValue(ServiceGig::class.java)!!
                    if( gig != null){
                        mList.add(gig)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Buyer_dashboard, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
}