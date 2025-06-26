package com.example.simplebankingapp.presentation.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.simplebankingapp.R
import com.example.simplebankingapp.presentation.activities.transfer.TransferActivity
import com.example.simplebankingapp.utils.SharedPreferencesManager


class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val sharedPreferencesManager = SharedPreferencesManager(requireContext())
        val username = sharedPreferencesManager.getUserName()

        val userNameTV = view.findViewById<TextView>(R.id.username_tv)
        userNameTV.text = username

        val transferLayout = view.findViewById<LinearLayout>(R.id.transfer_layout)
        transferLayout.setOnClickListener {
            requireContext().startActivity(Intent(activity, TransferActivity::class.java))
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}