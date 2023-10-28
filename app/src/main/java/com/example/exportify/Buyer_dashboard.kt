package com.example.exportify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import android.speech.RecognizerIntent
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exportify.Adapters.SeviceGigsAdapter
import com.example.exportify.databinding.ActivityBuyerDashboardBinding
import com.example.exportify.models.BuyerRequestsModel
import com.example.exportify.models.ServiceGig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Locale
import java.util.Objects

class Buyer_dashboard : AppCompatActivity() {

    private lateinit var binding: ActivityBuyerDashboardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private var mList = ArrayList<ServiceGig>()
    private lateinit var adapter: SeviceGigsAdapter
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageView: ImageView
    private val REQUEST_CODE_SPEECH_INPUT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //search function
        recyclerView = binding.recyclerview
        searchView = binding.searchView

        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        //voice implementation
        imageView = binding.voiceicon

        imageView.setOnClickListener{
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            val pacakgeManager = packageManager

            if(intent.resolveActivity(pacakgeManager) != null) {
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
                } catch (e: Exception) {
                    Toast.makeText(this, "" + e.message, Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this,"Your device does not support speech input",Toast.LENGTH_LONG).show()
            }
        }


        //initialize variables
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("service_gigs")


        binding.btnAddRequest.setOnClickListener {
            intent = Intent(applicationContext, CreateRequest::class.java)
            startActivity(intent)
        }

        binding.btnListRequest.setOnClickListener {
            intent = Intent(applicationContext, MyRequests::class.java)
            startActivity(intent)
        }

        binding.btnNotifiRequest.setOnClickListener {
            intent = Intent(applicationContext, notification_page::class.java)
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

    //voice implementation
    override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?){
        super.onActivityResult(requestCode,resultCode,data)

        if(requestCode == REQUEST_CODE_SPEECH_INPUT){
            if (resultCode == RESULT_OK && data != null){
                val res: ArrayList<String> = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>

                //display output in the search box
                //textView.setText(Objects.requireNonNull(res)[0])
                searchView.setQuery(res[0],true)

            }
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

    private fun filterList(query: String?) {
        if(query!= null){
            val filteredList = ArrayList<ServiceGig>()
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