package com.example.tourmate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tourmate.Database.ChatDatabase
import com.example.tourmate.adapters.MessageAdapter
import com.example.tourmate.databinding.FragmentChatbotBinding
import com.example.tourmate.entities.ChatMessagesModel
import com.example.tourmate.retrofit.builder.ApiUtils
import com.example.tourmate.retrofit.dao.MessagesRoomDao
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

    private lateinit var database: ChatDatabase
    private lateinit var messagesRoomDao: MessagesRoomDao

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

        database = ChatDatabase.getInstance(requireContext())
        messagesRoomDao = database.messagesRoomDao()
        messageAdapter = MessageAdapter(messageList)

        lifecycleScope.launch(Dispatchers.IO){
            val dbMessages = messagesRoomDao.getAllMessages()
            val mapped = dbMessages.map { ChatMessage(it.message, it.isSentByUser) }
            withContext(Dispatchers.Main) {
                messageList.addAll(mapped)
                messageAdapter.notifyDataSetChanged()
                binding.recyclerView2.scrollToPosition(messageAdapter.itemCount - 1)
            }
        }
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
        binding.recyclerView2.scrollToPosition(messageAdapter.itemCount - 1)
        lifecycleScope.launch(Dispatchers.IO){
            messagesRoomDao.insertMessage(ChatMessagesModel(message = message, isSentByUser = true))
        }
        val waitingMessage = ChatMessage("AI is typing...",false)
        messageAdapter.addMessage(waitingMessage)
        binding.recyclerView2.scrollToPosition(messageAdapter.itemCount - 1)

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
                       messageAdapter.updateLastMessage(chatResponse)
                       binding.recyclerView2.scrollToPosition(messageAdapter.itemCount - 1)

                       lifecycleScope.launch (Dispatchers.IO) {
                           messagesRoomDao.insertMessage(ChatMessagesModel(message = chatResponse, isSentByUser = false))
                       }

                   }else{
                       messageAdapter.updateLastMessage("Failed: ${response.code()}")
                       binding.recyclerView2.scrollToPosition(messageAdapter.itemCount - 1)
                   }
                }

                override fun onFailure(p0: Call<ChatResponseModel>, p1: Throwable) {
                    messageAdapter.addMessage(ChatMessage("Error: ${p1.message}", false))
                    binding.recyclerView2.scrollToPosition(messageAdapter.itemCount - 1)
                }
            })
        binding.messageText.text.clear()
        }

    }