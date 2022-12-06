package businessLogic

import dataAccessLayer.Auth
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class AuthBL(PhoneNumber: String, Activity: AppCompatActivity) {
    private var phoneNumber = PhoneNumber
    private var activity = Activity
    val authentication = Auth(activity)


    /**
     * Authentication to our firebase
     */
    fun authenticate(callback:(userId: String) -> Unit){
        val options = PhoneAuthOptions.newBuilder(authentication.firebaseAuth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity)                 // Activity (for callback binding)
            .setCallbacks(authentication.callbacks)     // OnVerificationStateChangedCallbacks
            .build()
        authentication.authenticate(options,callback)
    }

    /**
     * Resend code in case you missed it
     */
    fun resendCode(){
        val options = PhoneAuthOptions.newBuilder(authentication.firebaseAuth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity)                 // Activity (for callback binding)
            .setCallbacks(authentication.callbacks)    // OnVerificationStateChangedCallbacks
            .setForceResendingToken(authentication.storedToken!!) // ForceResendingToken from callbacks
            .build()
        authentication.resendCode(options)
    }

    /**
     * Vertify the code
     */
    fun verify(code: String){
        val credential = PhoneAuthProvider.getCredential(authentication.storedVerificationId!!,
            code
        )
        authentication.verify(credential)
    }
    /**
     * Authenticate the code the user has send
     */



    fun signOut(){
        authentication.signOut()
    }

}


