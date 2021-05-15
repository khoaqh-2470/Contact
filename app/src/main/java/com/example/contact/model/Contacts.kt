package com.example.contact.model

//data class Contacts(
//    var id: String,
//    var name: String,
//    var phoneNumber: String
//)

class Contacts {
    var id: String = ""
    var name: String =""
    var phoneNumber: String =""

    constructor(id: String, name: String, phoneNumber: String) {
        this.id = id
        this.name = name
        this.phoneNumber = phoneNumber
    }

    constructor( name: String, phoneNumber: String) {

        this.name = name
        this.phoneNumber = phoneNumber
    }
}
