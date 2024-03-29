package com.safaashatha.allin1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.safaashatha.allin1.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.navigation.findNavController
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_store_add.*
import kotlinx.android.synthetic.main.showcart.*


class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityMainBinding
    private lateinit var productsarrylist: ArrayList<product>
    private lateinit var shopsarraylist: ArrayList<owner>
    var userid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userid = intent.getStringExtra("user_id")
        val emailid = intent.getStringExtra("email_id")
        //try to write user name after welcom in main page
        var usernameforwelcome = ""
        val namedata =
            FirebaseDatabase.getInstance("https://allin1-23085-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child(
                "users"
            ).child(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener {
                usernameforwelcome =
                    it.child("firstname").value.toString() + it.child("lastname").value.toString()
                shopsname.text = "Welcome Safaa"
            }
        binding = ActivityMainBinding.inflate(layoutInflater)
        readshops()
        readData()
        val reference = FirebaseDatabase.getInstance("https://allin1-23085-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("shops")
        val actionbar = supportActionBar
        actionbar!!.title = "Allin1"
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true


    }

    fun readshops() {
        shopsarraylist = ArrayList()
        database =
            FirebaseDatabase.getInstance("https://allin1-23085-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("shops")
        database.get().addOnSuccessListener {
            for (x in it.children)
                if (x.exists()) {
                    var shop = owner(x.child("name").value.toString(),
                        x.child("about").value.toString(),
                        x.child("address").value.toString(),
                        x.child("category").value.toString(),
                        x.child("phone").value.toString())
                    shopsarraylist.add(shop)

                }
        }
        val listView: GridView = findViewById(R.id.shopslist)
        listView.setAdapter(shopsadapter(this, shopsarraylist))
    }

    fun readData() {
        productsarrylist = ArrayList()
        database =
            FirebaseDatabase.getInstance("https://allin1-23085-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("shops")
        database.get().addOnSuccessListener {
            for (x in it.children)

                if (x.exists()) {
                    for (prod in x.child("products").children) {
                        var rating = 0
                        var numraters = 0
                        if (prod.child("numraters").exists()) {
                            numraters = Integer.valueOf(prod.child("numraters").value.toString())
                            rating = Integer.valueOf(prod.child("rating").value.toString())
                        }
                        val pr = product(
                            name = prod.child("name").value.toString(),
                            owner = prod.child("owner").value.toString(),
                            price = prod.child("price").value.toString(),
                            category = prod.child("category").value.toString(),
                            rating = Integer.valueOf(rating),
                            numraters = Integer.valueOf(numraters),
                            about = prod.child("about").value.toString()
                        )
                        productsarrylist.add(pr)

                    }
                }
            val listView: GridView = findViewById(R.id.products)
            listView.setAdapter(productadapter(this, productsarrylist))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        val search = menu!!.findItem(R.id.prodsearch)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Search"
        val favprod = menu!!.findItem(R.id.favorites)
        favprod.isVisible = true
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //products.adapter.filter.filter("hhh")
                searchforprod(newText!!)
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.books -> {
                Toast.makeText(this, "you click books category", Toast.LENGTH_LONG).show()
                showproductsbycategory("books")
                true
            }
            R.id.clothes -> {
                Toast.makeText(this, "you click clothes category", Toast.LENGTH_LONG).show()
                showproductsbycategory("clothes")
                true
            }
            R.id.women -> {
                Toast.makeText(this, "you click women clothes category", Toast.LENGTH_LONG).show()
                showproductsbycategory("women")
                true
            }
            R.id.men -> {
                Toast.makeText(this, "you click men clothes category", Toast.LENGTH_LONG).show()
                showproductsbycategory("men")
                true
            }
            R.id.nails -> {
                Toast.makeText(this, "you click nails category", Toast.LENGTH_LONG).show()
                showproductsbycategory("nails")
                true
            }
            R.id.home -> {
                Toast.makeText(this, "you click home category", Toast.LENGTH_LONG).show()
                showproductsbycategory("home")
                true
            }
            R.id.cart -> {
                showcart()
                true
            }
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()

                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            R.id.editprofile -> {
                val intent = Intent(this, editprofileuser::class.java)
                intent.putExtra("user_id", FirebaseAuth.getInstance()!!.uid)
                startActivity(intent)
                true
            }
            R.id.homepage -> {
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            R.id.favorites -> {
                startActivity(Intent(this, userfavorites::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    fun showcart() {
        //nocart.text=" "
        setContentView(R.layout.showcart)
        val cartarraylist = ArrayList<product>()
        database =
            FirebaseDatabase.getInstance("https://allin1-23085-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("cart")
        database.child(FirebaseAuth.getInstance()!!.uid!!).get().addOnSuccessListener {
            for (x in it.children)

                if (x.exists()) {
                    val pr = product(
                        name=x.child("name").value.toString(),
                        price= x.child("price").value.toString(),
                        category = x.child("category").value.toString(),
                        owner = x.child("owner").value.toString()
                    )
                    cartarraylist.add(pr)
                }
            if (cartarraylist.size == 0) {
                nocart.text = "There is no products in the cart"
            }

            val listVieww: ListView = findViewById(R.id.cartlist!!)
            listVieww.setAdapter(productadapter(this, cartarraylist))
        }
    }

    fun showproductsbycategory(cat: String) {
        shopsname.text = cat
        productsarrylist = ArrayList()
        noprod.text = ""
        database =
            FirebaseDatabase.getInstance("https://allin1-23085-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("shops")
        database.get().addOnSuccessListener {
            for (x in it.children)

                if (x.exists()) {
                    for (prod in x.child("products").children) {
                        var rating = 0
                        var numraters = 0
                        if (prod.child("numraters").exists()) {
                            numraters = Integer.valueOf(prod.child("numraters").value.toString())
                            rating = Integer.valueOf(prod.child("rating").value.toString())
                        }
                        if (cat in prod.child("category").value.toString()) {
                            val pr = product(
                                name = prod.child("name").value.toString(),
                                owner = prod.child("owner").value.toString(),
                                price = prod.child("price").value.toString(),
                                category = prod.child("category").value.toString(),
                                rating = Integer.valueOf(rating),
                                numraters = Integer.valueOf(numraters),
                                about = prod.child("about").value.toString()
                            )
                            productsarrylist.add(pr)
                        }
                    }
                    if (productsarrylist.size == 0) {
                        //noprod.text = "there is no products in this category"
                    }
                }
            val listView: GridView = findViewById(R.id.products)
            listView.setAdapter(productadapter(this, productsarrylist))
        }
    }

    fun searchforprod(newtext: String) {
        shopsname.text = newtext
        productsarrylist = ArrayList()
        noprod.text = ""
        database =
            FirebaseDatabase.getInstance("https://allin1-23085-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("shops")
        database.get().addOnSuccessListener {
            for (x in it.children)

                if (x.exists()) {
                    for (prod in x.child("products").children) {
                        if (newtext in prod.child("name").value.toString()) {
                            val pr = product(
                                prod.child("name").value.toString(),
                                prod.child("price").value.toString(),
                                prod.child("category").value.toString(),
                            )
                            productsarrylist.add(pr)
                        }
                    }
                    if (productsarrylist.size == 0) {
                        noprod.text = "there is no products in this category"
                    }
                }
            val listView: GridView = findViewById(R.id.products)
            listView.setAdapter(productadapter(this, productsarrylist))
        }
    }

    fun buy(view: View) {


        val namedata =
            FirebaseDatabase.getInstance("https://allin1-23085-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child(
                "Users"
            )
        namedata.get().addOnSuccessListener {
            for (x in it.children) {
                //print("shaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                if (x.exists()) {
                    if (x.key == FirebaseAuth.getInstance().currentUser!!.uid) {
                        print("\n------------------------------------------------------shatha1-----------------\n" + x.key)

                        val name = x.child("firstname").getValue().toString()
                        val owner = x.child("owner").getValue().toString()
                        val lastname = x.child("lastname").getValue().toString()
                        val email = x.child("email").getValue().toString()
                        val aaddress = x.child("address").getValue().toString()
                        val phone = x.child("phone").getValue().toString()
                        print("\nsssssssssssssssssssssssssss\n" + name + email + aaddress + phone + lastname + "\nsssssssssssssssss\n")

                        val intent = Intent(this, editopay::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra("user_id", FirebaseAuth.getInstance()!!.uid)
                        intent.putExtra("name", name)
                        intent.putExtra("owner", owner)

                        intent.putExtra("lastname", lastname)

                        intent.putExtra("email", email)

                        intent.putExtra("aaddress", aaddress)

                        intent.putExtra("phone", phone)
                        startActivity(intent)
                    }

                }
            }
        }
    }
}


