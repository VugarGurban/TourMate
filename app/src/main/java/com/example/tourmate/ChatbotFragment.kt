package com.example.tourmate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tourmate.adapters.MessageAdapter
import com.example.tourmate.databinding.FragmentChatbotBinding
import com.example.tourmate.retrofit.builder.ApiUtils
import com.example.tourmate.retrofit.model.ChatMessage
import com.example.tourmate.retrofit.model.request.ChatRequestModel
import com.example.tourmate.retrofit.model.response.ChatResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatbotFragment : Fragment() {

    private lateinit var binding: FragmentChatbotBinding
    private lateinit var messageAdapter: MessageAdapter
    private val messageList = mutableListOf<ChatMessage>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatbotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as NavigationListener).changeBottomMenuVisibility(View.GONE)

        messageAdapter = MessageAdapter(messageList)
        binding.recyclerView2.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView2.adapter = messageAdapter

        binding.sendBtn.setOnClickListener {
            val message = binding.messageText.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessageToOpenAI(message)
            }else{
                Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun sendMessageToOpenAI(message: String) {
        val chatDao = ApiUtils.getChatDao()

        val userMessage = ChatMessage(message, true)
        messageAdapter.addMessage(userMessage)
        val waitingMessage = ChatMessage("AI is typing...",false)
        messageAdapter.addMessage(waitingMessage)

        val request = ChatRequestModel(
            model = "gpt-3.5-turbo",
            messages = listOf(ChatRequestModel.Message(role = "user", content = message))
        )
        chatDao.sendMessage(request).enqueue(object : Callback<ChatResponseModel>{
            override fun onResponse(
                call: Call<ChatResponseModel>,
                response: Response<ChatResponseModel>
                ) {
                   if (response.isSuccessful){
                       val chatResponse = response.body()!!.choices.firstOrNull()!!.message.content
                       val botMessage = ChatMessage(chatResponse, false)
                       messageAdapter.updateLastMessage(chatResponse)
                   }else{
                       messageAdapter.updateLastMessage("Failed: ${response.code()}")
                   }
                }

                override fun onFailure(p0: Call<ChatResponseModel>, p1: Throwable) {
                    messageAdapter.addMessage(ChatMessage("Error: ${p1.message}", false))
                }
            })
        binding.messageText.text.clear()
        }
    }