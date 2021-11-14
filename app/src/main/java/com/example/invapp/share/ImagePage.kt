package com.example.invapp.share

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.invapp.R


class ImagePage : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val imageURL = arguments?.getString("imageURL").toString()
        val imageView = requireView().findViewById<ImageView>(R.id.imageView_large)
        Glide.with(this)
            .load(imageURL)
            .placeholder(R.drawable.ic_baseline_photo_24)
            .into(imageView)
        imageView.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

}