package com.reward.dne.model

 class OperatorSpinner(var id:Int?,var name:String?){

     //to display object as a string in spinner
     override fun toString(): String {
         return name!!
     }
 }
