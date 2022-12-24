package Shared


import com.google.gson.annotations.SerializedName


data class Lesson(@SerializedName("lessonName") val lessonName: String,
                  @SerializedName("maxNumberOfParticipants") val numberOfParticipants: Int,
                  @SerializedName("level") val level: String,
                  @SerializedName("price") val price: Double,
                  @SerializedName("description") val description: String,
                  @SerializedName("ParticipantsList") var ParticipantsList: MutableList<String> = mutableListOf() )