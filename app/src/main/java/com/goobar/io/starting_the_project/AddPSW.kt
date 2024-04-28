package com.goobar.io.starting_the_project

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.goobar.io.starting_the_project.databinding.AddingPasswordBinding
import org.mindrot.jbcrypt.BCrypt


class AddPSW : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {

    private lateinit var mBinding: AddingPasswordBinding
    private lateinit var db:passwordDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = AddingPasswordBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        mBinding.passwordEt.onFocusChangeListener = this
        mBinding.cPasswordEt.onFocusChangeListener = this

        db = passwordDatabaseHelper(this)


// Pass the Raw Password into the Database via For_Password crud using with SQLite Database
        mBinding.registerBtn.setOnClickListener {

                val cpsw = mBinding.cPasswordEt.text.toString()
                val psw  = mBinding.passwordEt.text.toString()

            if(psw == cpsw && validatePassword() && validateConfirmPassword()) {
                // Hash the password using bcrypt
                val hashedPassword = BCrypt.hashpw(cpsw, BCrypt.gensalt())
                val psw_db = For_password(0, hashedPassword)
                db.insertpsw(psw_db)
                Toast.makeText(this, "Password Saved", Toast.LENGTH_SHORT).show()
                // Navigate to Homepage.kt
                val intent = Intent(this, Homepage::class.java)
                startActivity(intent)

            } else {
                Toast.makeText(this, "Given password is not valid", Toast.LENGTH_SHORT).show()
            }

        }

    }

    // Binding is used to calling the activities and update the activities which are contain in the UI

    // This is for validate the password
    private fun validatePassword(): Boolean {
        var errorMsg: String? = null
        val value: String = mBinding.passwordEt.text.toString()
        if(value.isEmpty()){
            errorMsg = "Password is required"
        }else if(value.length < 6 || value == null){
            errorMsg = "Password must be 6 characters long"
        }


        if(errorMsg != null){
            mBinding.passwordTil.apply {
                isErrorEnabled = true
                error = errorMsg
            }
        }
        return errorMsg == null
    }

    private fun validateConfirmPassword(): Boolean{
        var errorMsg: String? = null
        val value: String = mBinding.cPasswordEt.text.toString()
        if(value.isEmpty()){
            errorMsg = "Confirm Password is required"
        }else if(value.length < 6 || value == null){
            errorMsg = "Confirm Password must be 6 characters long"
        }


        if(errorMsg != null){
            mBinding.cPasswordTil.apply {
                isErrorEnabled = true
                error = errorMsg
            }
        }
        return errorMsg == null

    }


    // Checking the similarity of password and Confirm password
    private fun validatePasswordAndConfirmPassword(): Boolean {
        var errorMsg: String? = null
        val password = mBinding.passwordEt.text.toString()
        val confirmPassword = mBinding.cPasswordEt.text.toString()
        if(password != confirmPassword){
            errorMsg = "Confirm password doen't match with the password"
        }


        if(errorMsg != null){
            mBinding.cPasswordTil.apply {
                isErrorEnabled = true
                error = errorMsg
            }
        }
        return errorMsg == null
    }




    override fun onClick(view: View?) {

    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {

                R.id.passwordEt -> {
                    if (hasFocus) {
                        if (mBinding.passwordTil.isErrorEnabled) {
                            mBinding.passwordTil.isErrorEnabled = false
                        }
                    } else {
                        if (validatePassword() && mBinding.cPasswordEt.text!!.isNotEmpty() && validateConfirmPassword()
                            && validatePasswordAndConfirmPassword()
                        ) {
                            if (mBinding.cPasswordTil.isErrorEnabled) {
                                mBinding.cPasswordTil.isErrorEnabled = false
                            }
                            mBinding.cPasswordTil.apply {
                                setStartIconDrawable(R.drawable.check_circle_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                        }
                    }
                }

                R.id.cPasswordEt -> {
                    if (hasFocus) {
                        if (mBinding.cPasswordTil.isErrorEnabled) {
                            mBinding.cPasswordTil.isErrorEnabled = false
                        }
                    } else {
                        if (validateConfirmPassword() && validatePassword() && validatePasswordAndConfirmPassword()) {
                            if (mBinding.passwordTil.isErrorEnabled) {
                                mBinding.passwordTil.isErrorEnabled = false
                            }
                            mBinding.cPasswordTil.apply {
                                setStartIconDrawable(R.drawable.check_circle_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                        }else{
                            mBinding.cPasswordTil.startIconDrawable = null
                        }

                    }
                }
            }
        }
    }

    override fun onKey(view: View?, event: Int, keyEvent: KeyEvent?): Boolean {
        return false
    }
}
