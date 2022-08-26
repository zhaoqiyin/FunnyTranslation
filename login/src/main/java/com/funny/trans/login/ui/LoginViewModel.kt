package com.funny.trans.login.ui

import android.content.Context
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funny.trans.login.bean.UserBean
import com.funny.trans.login.utils.UserUtils
import com.funny.translation.AppConfig
import com.funny.translation.helper.toastOnUi
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var username by mutableStateOf("FunnySaltyFish")
    val password by mutableStateOf("")
    var email by mutableStateOf("shen2183@foxmail.com")
    var verifyCode by mutableStateOf("")

    val isValidUsername by derivedStateOf { UserUtils.isValidUsername(username) }
    val isValidEmail by derivedStateOf { UserUtils.isValidEmail(email) }

    var finishSetFingerPrint by mutableStateOf(false)
    var finishValidateFingerPrint by mutableStateOf(false)

    var passwordType by mutableStateOf("1")
    // 当在新设备登录时，需要验证邮箱
    var shouldVerifyEmailWhenLogin by mutableStateOf(false)

    var encryptedInfo = ""
    var iv = ""

    val loginData
        get() = username + "@" + AppConfig.androidId

    private fun clear(){
        email = ""
        verifyCode = ""
        finishSetFingerPrint = false
        finishValidateFingerPrint = false
        encryptedInfo = ""
        iv = ""
    }

    fun login(
        onSuccess: (UserBean) -> Unit,
        onError: (String) -> Unit
    ){
        viewModelScope.launch {
            try {
                val userBean = UserUtils.login(username, "${AppConfig.androidId}#$encryptedInfo#$iv", passwordType, email)
                if (userBean != null) {
                    onSuccess(userBean)
                }else{
                    onError("登录失败，返回的用户信息为空！")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e.message ?: "登录失败，未知错误！")
            }
        }
    }

    fun register(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        viewModelScope.launch {
            try {
                UserUtils.register(username, "${AppConfig.androidId}#$encryptedInfo#$iv", passwordType, email, verifyCode, "")
                onSuccess()
                clear()
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e.message ?: "注册失败，未知错误！")
            }
        }

    }

    fun sendVerifyEmail(context: Context){
        viewModelScope.launch {
            try {
                UserUtils.sendVerifyEmail(username, email)
                context.toastOnUi("验证邮件已发送，请注意查收~")
            } catch (e: Exception) {
                e.printStackTrace()
                context.toastOnUi("发送失败，请稍后再试~（${e.message}）")
            }
        }
    }
}