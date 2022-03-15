package com.uniix.lengo_chatapp.models

data class User(
    val name: String,
    val imageUrl: String,
    val thumbImage: String,
    val uid: String,
    val dob: String,
    val gender: String,
    val phoneNumber: String,
    val deviceToken: String,
    val status: String,
) {
    //Empty [Constructor] for Firebase
    constructor() :
            this("", "", "", "", "", "", "", "", "Hey There, I am using CLIQUE")

    constructor(
        name: String,
        imageUrl: String,
        thumbImage: String,
        uid: String,
        dob: String,
        gender: String,
        phoneNumber: String
    ) :
            this(
                name = name,
                imageUrl = imageUrl,
                thumbImage = thumbImage,
                uid = uid,
                dob = dob,
                gender = gender,
                phoneNumber = phoneNumber,
                "",
                status = "Hey There, I am using CLIQUE"
            )
}