package dataAccessLayer

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Auth(Activity: AppCompatActivity)  {
    var firebaseAuth = FirebaseAuth.getInstance()
    var storedVerificationId: String? = null
    var storedToken: PhoneAuthProvider.ForceResendingToken? = null
    var success = false
    private lateinit var user: FirebaseUser
    lateinit var userId: String
    val tag = "PhoneAuth"
    private val activity = Activity
    private lateinit var  callback: (userId: String) -> Unit

    var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(tag, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }


        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(tag, "onVerificationFailed", e)
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request

            } else if (e is FirebaseTooManyRequestsException) {
                Log.w(tag,"Too Many requests to authenticate")
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(tag, "onCodeSent:$verificationId")
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            storedToken = token
            Log.d(tag,"Code was sent to the user")
        }
    }

    /**
     * Authentication to our firebase
     */
    fun authenticate(options: PhoneAuthOptions, callbackFunction: (userId: String) -> Unit){
        PhoneAuthProvider.verifyPhoneNumber(options)
        callback = callbackFunction
    }

    /**
     * Resend code in case you missed it
     */
    fun resendCode(options: PhoneAuthOptions){
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    /**
     * Verify the code
     */
    fun verify(credential:  PhoneAuthCredential){

        signInWithPhoneAuthCredential(credential)
    }
    /**
     * Authenticate the code the user has send
     */
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    user = task.result?.user!!
                    userId = user.uid
                    success = true
                    callback(userId)
                    Log.d(tag,"Authentication completed")

                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        success = false
                        Log.d(tag,"Code is invalid")
                        Toast.makeText(activity,"Wrong verification code",
                            Toast.LENGTH_SHORT).show()
                        // The verification code entered was invalid

                    }
                }
            }
    }

    fun signOut(){
        Firebase.auth.signOut()
    }

}


