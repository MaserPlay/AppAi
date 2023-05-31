package com.maserplay.appai

import android.content.Context
import android.util.Log
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
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.client.OpenAI
import com.aallam.openai.api.exception.GenericIOException
import com.maserplay.AppAi.R
import kotlinx.coroutines.launch


class HomeViewModel : ViewModel() {

    val ada: MutableLiveData<Adapter> = MutableLiveData()
    val writen: MutableLiveData<Boolean> = MutableLiveData()
    var products = ArrayList<Product>()
    private lateinit var adapter: Adapter
    private lateinit var ServiceNeed: ServiceDop

    private val PREF_NAME = "api"
    private val PREFS_FILE = "Main"
    fun getpr(): ArrayList<Product>{
        return products
    }
    fun start(ma: MainActivity){
        adapter = ProductAdapter(ma, R.layout.list_item, R.layout.list_item2, R.layout.list_item3, products)
        ada.postValue(adapter)
        ServiceNeed = ServiceDop()
    }
    fun clear(){
        products.clear()
        ada.postValue(adapter)
    }
    @OptIn(BetaOpenAI::class)
    fun exec(prompt: String, con: Context) {
        products.add(Product(prompt, 2))
        val pr = Product(con.getString(R.string.bot_writting) , 1)
        products.add(pr)
        ada.postValue(adapter)
        ServiceNeed.add(ChatMessage( role = ChatRole.User, content = prompt ))

        viewModelScope.launch {
            val apiKey = con.getSharedPreferences(PREFS_FILE, AppCompatActivity.MODE_PRIVATE).getString(PREF_NAME, "alo")
            val openAI = OpenAI(apiKey.toString())
            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo"),
                messages = ServiceNeed.GetList()

            )
            try {
                val completion = openAI.chatCompletion(chatCompletionRequest)
                pr.name =  completion.choices[0].message?.content
                completion.choices[0].message?.let { ServiceNeed.add(it) }
                ada.postValue(adapter)
                writen.postValue(true)
            } catch (e: AuthenticationException) {
                Toast.makeText(con, con.getString(R.string.api_not_correct), Toast.LENGTH_LONG).show()
                products.remove(pr)
                products.add(Product(con.getString(R.string.api_not_correct), 3))
                products.add(Product(e.toString(), 3))
                writen.postValue(true)
            } catch (e: GenericIOException) {
                Toast.makeText(con, con.getString(R.string.no_intenet), Toast.LENGTH_LONG).show()
                products.remove(pr)
                products.add(Product(con.getString(R.string.no_intenet), 3))
                products.add(Product(e.toString(), 3))
                writen.postValue(true)
            } catch (e: com.aallam.openai.api.exception.OpenAITimeoutException) {
                Toast.makeText(con, con.getString(R.string.time_out), Toast.LENGTH_LONG).show()
                products.remove(pr)
                products.add(Product(con.getString(R.string.time_out), 3))
                products.add(Product(e.toString(), 3))
                writen.postValue(true)
            } catch (e: Exception){
                Toast.makeText(con, con.getString(R.string.fatal_error), Toast.LENGTH_LONG).show()
                products.remove(pr)
                products.add(Product(con.getString(R.string.fatal_error), 3))
                products.add(Product(e.toString(), 3))
                writen.postValue(true)
                Log.e("Exception", e.toString())
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("Data", "save")
        ServiceDop().saveText()
    }


}