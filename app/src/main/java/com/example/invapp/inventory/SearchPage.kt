package com.example.invapp.inventory

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.invapp.R
import com.example.invapp.adapter.ScanResultAdapter
import com.example.invapp.singleton.SingletonClass
import org.json.JSONArray

class SearchPage : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonGoHome = requireView().findViewById<Button>(R.id.button_goHome)
        val editTextKeyWord = requireView().findViewById<EditText>(R.id.editText_keyWord)
        val buttonSearch = requireView().findViewById<Button>(R.id.button_search)
        val progressBarSearch = requireView().findViewById<ProgressBar>(R.id.progressBar_search)

        // 回首頁
        buttonGoHome.setOnClickListener {
            activity?.finish()
        }

        // 查詢
        buttonSearch.setOnClickListener {
            val query = editTextKeyWord.text.toString().trim()
            if (query == "") {
                editTextKeyWord.error = "請輸入查詢關鍵字"
            } else {
                progressBarSearch.visibility = View.VISIBLE
                val url = SingletonClass.instance.ip + "/appGetSearchResult/$query"
                val queue = Volley.newRequestQueue(activity?.applicationContext)
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        if (JSONArray(response) == JSONArray()) {
                            progressBarSearch.visibility = View.INVISIBLE
                            Toast.makeText(context, "查詢無結果，請重新輸入關鍵字", Toast.LENGTH_SHORT).show()
                        } else {
                            progressBarSearch.visibility = View.INVISIBLE
                            val bundle = Bundle()
                            bundle.putString("searchResult", response)
                            val controller: NavController = requireView().let { it1 -> Navigation.findNavController(it1) }
                            controller.navigate(R.id.action_searchPage_to_searchResultPage, bundle)
                        }
                        // 關閉鍵盤
                        val imm : InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view?.windowToken,0)
                    },
                    {
                        progressBarSearch.visibility = View.INVISIBLE
                        Toast.makeText(context, "連線失敗", Toast.LENGTH_SHORT).show()
                    })
                queue.add(stringRequest)
            }
        }
    }

}