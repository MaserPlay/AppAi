package com.maserplay.appai

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Adapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.exception.GenericIOException
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.exception.OpenAITimeoutException
import com.aallam.openai.api.exception.RateLimitException
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.ProxyConfig
import com.maserplay.AppAi.R
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


class HomeViewModel : ViewModel() {
    private val CHANNEL_ID = "1"
    private var notificationId = 1
    val ada: MutableLiveData<Adapter> = MutableLiveData()
    val writen: MutableLiveData<Boolean> = MutableLiveData()
    val errortr: MutableLiveData<String> = MutableLiveData()
    private var products = ArrayList<Product>()
    private lateinit var adapter: Adapter

    fun getpr(): ArrayList<Product> {
        return products
    }

    fun start(ma: MainActivity) {
        adapter = ProductAdapter(
            ma,
            R.layout.list_item,
            R.layout.list_item2,
            R.layout.list_item3,
            R.layout.list_item4,
            products
        )
        ada.postValue(adapter)
    }

    @OptIn(BetaOpenAI::class)
    fun parse() {
        for (i in ServiceDop.GetList()) {
            if (i.role == ChatRole.User) {
                products.add(Product(i.content, 2))
            }
            if (i.role == ChatRole.Assistant) {
                products.add(Product(i.content, 1))
            }
            if (i.role == ChatRole.System) {
                products.add(Product(i.content, 3))
            }
        }
    }

    fun clear() {
        products.clear()
        ada.postValue(adapter)
    }

    @OptIn(BetaOpenAI::class)
    fun exec(prompt: String, con: Context) {
        products.add(Product(prompt, 2))
        val pr = Product(con.getString(R.string.bot_writting), 1)
        products.add(pr)
        ada.postValue(adapter)
        ServiceDop.add(ChatMessage(role = ChatRole.User, content = prompt))

        viewModelScope.launch {
            val shpref = con.getSharedPreferences(
                GlobalVariables.SHAREDPREFERENCES_NAME,
                AppCompatActivity.MODE_PRIVATE
            )
            val apiKey = shpref.getString("api", "")
            val gptver = shpref.getString("gptver", GlobalVariables.GPTVER_DEFVAl)
            var proxy: ProxyConfig? = null
            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId(gptver.toString()),
                messages = ServiceDop.GetList()

            )
            try {
                when (shpref.getInt("proxytype", -1)) {
                    (0) -> {
                        val sh = shpref.getStringSet("proxy", null)
                        if (sh != null) {
                            proxy = ProxyConfig.Http(sh.elementAt(0))
                        }
                    }

                    (1) -> {
                        val sh = shpref.getStringSet("proxy", null)
                        if (sh != null) {
                            proxy = ProxyConfig.Socks(sh.elementAt(0), sh.elementAt(1).toInt())
                        }
                    }

                }
                val openAI = OpenAI(
                    OpenAIConfig(
                        token = apiKey.toString(),
                        timeout = Timeout(socket = 60.seconds),
                        proxy = proxy
                    )
                )
                val completion = openAI.chatCompletion(chatCompletionRequest)
                pr.name = completion.choices[0].message?.content.toString()
                completion.choices[0].message?.let { ServiceDop.add(it) }
                CreateNotification(completion.choices[0].message?.content.toString(), con)
                ada.postValue(adapter)
                writen.postValue(true)
            } catch (e: AuthenticationException) {
                products.remove(pr)
                products.add(Product(con.getString(R.string.api_not_correct), 3))
                CreateNotification(con.getString(R.string.api_not_correct), con)
                if (con.getSharedPreferences(
                        GlobalVariables.SHAREDPREFERENCES_NAME,
                        AppCompatActivity.MODE_PRIVATE
                    ).getBoolean(GlobalVariables.SHAREDPREFERENCES_DEBUG, false)
                ) {
                    products.add(Product(e.toString(), 4))
                }
                writen.postValue(true)
            } catch (e: GenericIOException) {
                products.remove(pr)
                products.add(Product(con.getString(R.string.no_intenet), 3))
                CreateNotification(con.getString(R.string.no_intenet), con)
                if (con.getSharedPreferences(
                        GlobalVariables.SHAREDPREFERENCES_NAME,
                        AppCompatActivity.MODE_PRIVATE
                    ).getBoolean(GlobalVariables.SHAREDPREFERENCES_DEBUG, false)
                ) {
                    products.add(Product(e.toString(), 4))
                }
                writen.postValue(true)
            } catch (e: OpenAITimeoutException) {
                Toast.makeText(con, con.getString(R.string.time_out), Toast.LENGTH_LONG).show()
                products.remove(pr)
                products.add(Product(con.getString(R.string.time_out), 3))
                CreateNotification(con.getString(R.string.time_out), con)
                if (con.getSharedPreferences(
                        GlobalVariables.SHAREDPREFERENCES_NAME,
                        AppCompatActivity.MODE_PRIVATE
                    ).getBoolean(GlobalVariables.SHAREDPREFERENCES_DEBUG, false)
                ) {
                    products.add(Product(e.toString(), 4))
                }
                errortr.postValue(e.toString())
                writen.postValue(true)
            } catch (e: RateLimitException) {
                Toast.makeText(con, con.getString(R.string.error_quota), Toast.LENGTH_LONG).show()
                products.remove(pr)
                products.add(Product(con.getString(R.string.error_quota), 3))
                CreateNotification(con.getString(R.string.error_quota), con)
                if (con.getSharedPreferences(
                        GlobalVariables.SHAREDPREFERENCES_NAME,
                        AppCompatActivity.MODE_PRIVATE
                    ).getBoolean(GlobalVariables.SHAREDPREFERENCES_DEBUG, false)
                ) {
                    products.add(Product(e.toString(), 4))
                }
                errortr.postValue(e.toString())
                writen.postValue(true)
            } catch (e: InvalidRequestException) {
                Toast.makeText(con, con.getString(R.string.error_invreq), Toast.LENGTH_LONG).show()
                products.remove(pr)
                products.add(Product(con.getString(R.string.error_invreq), 3))
                CreateNotification(con.getString(R.string.error_invreq), con)
                if (con.getSharedPreferences(
                        GlobalVariables.SHAREDPREFERENCES_NAME,
                        AppCompatActivity.MODE_PRIVATE
                    ).getBoolean(GlobalVariables.SHAREDPREFERENCES_DEBUG, false)
                ) {
                    products.add(Product(e.toString(), 4))
                }
                errortr.postValue(e.toString())
                writen.postValue(true)
            } catch (e: Exception) {
                Toast.makeText(con, con.getString(R.string.fatal_error), Toast.LENGTH_LONG).show()
                products.remove(pr)
                products.add(Product(con.getString(R.string.fatal_error), 3))
                CreateNotification(con.getString(R.string.fatal_error), con)
                if (con.getSharedPreferences(
                        GlobalVariables.SHAREDPREFERENCES_NAME,
                        AppCompatActivity.MODE_PRIVATE
                    ).getBoolean(GlobalVariables.SHAREDPREFERENCES_DEBUG, false)
                ) {
                    products.add(Product(e.toString(), 4))
                }
                writen.postValue(true)
                errortr.postValue(e.toString())
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        ServiceDop.saveText()
    }

    fun CreateNotification(message: String, con: Context) {
        Log.i("TAGNOTIFICATION", App.isTargetActivityStarted.toString())
        if (!App.isTargetActivityStarted) {
            val intent = Intent(con, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(con, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val builder = NotificationCompat.Builder(con, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("AppAi answered your question")
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            if (ActivityCompat.checkSelfPermission(
                    con,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            NotificationManagerCompat.from(con).notify(notificationId++, builder.build())
        }
    }
}