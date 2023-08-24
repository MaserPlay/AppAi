package com.maserplay.appai

import android.content.Context
import android.widget.Adapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.exception.GenericIOException
import com.aallam.openai.api.exception.OpenAITimeoutException
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.maserplay.AppAi.R
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


class HomeViewModel : ViewModel() {

    val ada: MutableLiveData<Adapter> = MutableLiveData()
    val writen: MutableLiveData<Boolean> = MutableLiveData()
    val errortr: MutableLiveData<String> = MutableLiveData()
    private var products = ArrayList<Product>()
    private lateinit var adapter: Adapter
    private lateinit var ServiceNeed: ServiceDop

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
        ServiceNeed = ServiceDop()
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
        ServiceNeed.add(ChatMessage(role = ChatRole.User, content = prompt))

        viewModelScope.launch {
            val shpref = con.getSharedPreferences(GlobalVariables.SHAREDPREFERENCES_NAME, AppCompatActivity.MODE_PRIVATE)
            val apiKey = shpref.getString("api", "")
            val gptver = shpref.getString("gptver", GlobalVariables.GPTVER_DEFVAl)
            val openAI = OpenAI(
                OpenAIConfig(
                    token = apiKey.toString(),
                    timeout = Timeout(socket = 60.seconds)
                )
            )
            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId(gptver.toString()),
                messages = ServiceNeed.GetList()

            )
            try {
                val completion = openAI.chatCompletion(chatCompletionRequest)
                pr.name = completion.choices[0].message?.content.toString()
                completion.choices[0].message?.let { ServiceNeed.add(it) }
                ada.postValue(adapter)
                writen.postValue(true)
            } catch (e: AuthenticationException) {
                products.remove(pr)
                products.add(Product(con.getString(R.string.api_not_correct), 3))
                if (con.getSharedPreferences(GlobalVariables.SHAREDPREFERENCES_NAME, AppCompatActivity.MODE_PRIVATE).getBoolean(GlobalVariables.SHAREDPREFERENCES_DEBUG, false)) { products.add(Product(e.toString(), 4)) }
                writen.postValue(true)
            } catch (e: GenericIOException) {
                products.remove(pr)
                products.add(Product(con.getString(R.string.no_intenet), 3))
                if (con.getSharedPreferences(GlobalVariables.SHAREDPREFERENCES_NAME, AppCompatActivity.MODE_PRIVATE).getBoolean(GlobalVariables.SHAREDPREFERENCES_DEBUG, false)) { products.add(Product(e.toString(), 4)) }
                writen.postValue(true)
            } catch (e: OpenAITimeoutException) {
                Toast.makeText(con, con.getString(R.string.time_out), Toast.LENGTH_LONG).show()
                products.remove(pr)
                products.add(Product(con.getString(R.string.time_out), 3))
                if (con.getSharedPreferences(GlobalVariables.SHAREDPREFERENCES_NAME, AppCompatActivity.MODE_PRIVATE).getBoolean(GlobalVariables.SHAREDPREFERENCES_DEBUG, false)) { products.add(Product(e.toString(), 4)) }
                errortr.postValue(e.toString())
                writen.postValue(true)
            } catch (e: Exception) {
                Toast.makeText(con, con.getString(R.string.fatal_error), Toast.LENGTH_LONG).show()
                products.remove(pr)
                products.add(Product(con.getString(R.string.fatal_error), 3))
                if (con.getSharedPreferences(GlobalVariables.SHAREDPREFERENCES_NAME, AppCompatActivity.MODE_PRIVATE).getBoolean(GlobalVariables.SHAREDPREFERENCES_DEBUG, false)) { products.add(Product(e.toString(), 4)) }
                writen.postValue(true)
                errortr.postValue(e.toString())
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        ServiceDop().saveText()
    }


}