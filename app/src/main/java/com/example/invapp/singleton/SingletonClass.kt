package com.example.invapp.singleton

import com.example.invapp.model.Data

class SingletonClass private constructor(){
    var currentUser : String? = null
    var ip : String? = null

    var qrcodeUnit : String? = null
    var qrcodeGroup : String? = null
    var qrcodeCode : String? = null
    var qrcodeName : String? = null

    var inventoryUnit : String? = null
    var inventoryGroup : String? = null
    var inventoryCode : String? = null
    var inventoryName : String? = null

    companion object {
        val instance = SingletonClass()
    }
}