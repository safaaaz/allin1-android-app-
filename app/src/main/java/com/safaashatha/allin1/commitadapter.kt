package com.safaashatha.allin1

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class commitadapter(private val context: Activity,private val arrayList: ArrayList<commit>):
    ArrayAdapter<commit>(context, R.layout.commit_list,arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        print(arrayList)
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.commit_list, parent, false);
        val explainview: TextView = view.findViewById(R.id.explain)
        val userview: TextView = view.findViewById(R.id.commitsusername)

        explainview.text = arrayList[position].explain.toString()
        userview.text = arrayList[position].username.toString()


        return view
    }
}