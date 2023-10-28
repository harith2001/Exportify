package com.example.exportify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exportify.Adapters.BuyerRequestsAdapter
import com.example.exportify.databinding.ActivityExporterDashboardBinding
import com.example.exportify.models.BuyerRequestsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class Exporter_dashboard : AppCompatActivity() {

    private lateinit var binding: ActivityExporterDashboardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private var mList = ArrayList<BuyerRequestsModel>()
    private lateinit var adapter: BuyerRequestsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var  searchView :SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExporterDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddRequest.setOnClickListener {
            intent = Intent(applicationContext, AddNewService::class.java)
            startActivity(intent)
        }

        binding.btnListRequest.setOnClickListener {
            intent = Intent(applicationContext, MyServiceGigs::class.java)
            startActivity(intent)
        }

        binding.btnNotifiRequest.setOnClickListener {
            intent = Intent(applicationContext, notification_page::class.java)
            startActivity(intent)
        }

            //search function for exporter dashboard request gigs

            recyclerView = binding.recyclerview
            searchView = binding.searchView

            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    filterList(newText)
                    return true
                }
            })

        //initialize variables
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("buyer_requests")

        var recyclerView = binding.recyclerview

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this);

        //addDataToList()
        retrieveData()
        adapter = BuyerRequestsAdapter(mList)
        recyclerView.adapter = adapter

        //Setting onclick on recyclerView each item
        adapter.setOnItemClickListner(object: BuyerRequestsAdapter.onItemClickListner {
            override fun onItemClick(position: Int) {

            }
        })

        binding.ivProfile.setOnClickListener {
            intent = Intent(applicationContext, ExporterProfile::class.java)
            startActivity(intent)
        }
    }

    private fun addDataToList(){
        mList.add(BuyerRequestsModel("topic", "This is des","150000 - 210000","",""))
        mList.add(BuyerRequestsModel("topic", "This is des","150000 - 210000","",""))
        mList.add(BuyerRequestsModel("topic", "This is des","150000 - 210000","",""))
        mList.add(BuyerRequestsModel("topic", "This is des","150000 - 210000","",""))
        mList.add(BuyerRequestsModel("topic", "This is des","150000 - 210000","",""))
        mList.add(BuyerRequestsModel("topic", "This is des","150000 - 210000","",""))
    }

    private fun retrieveData() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for ( snapshot in snapshot.children){
                    val req = snapshot.getValue(BuyerRequestsModel::class.java)!!
                    if( req != null){
                        mList.add(req)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Exporter_dashboard, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    //search filteration
    private fun filterList(query: String?) {
        if(query!= null){
            val filteredList = ArrayList<BuyerRequestsModel>()
            for (i in mList){
                if (i.topic?.lowercase(Locale.ROOT)?.contains(query?.lowercase(Locale.ROOT) ?: "") == true){
                    filteredList.add(i)
                }
            }
            if(filteredList.isEmpty()){
                Toast.makeText(this,"NO data found",Toast.LENGTH_SHORT).show()
            }else{
                adapter.setFilteredList(filteredList)
            }
        }
    }

}