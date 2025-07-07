package com.gvugar.tourmate

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gvugar.tourmate.Database.ChatDatabase
import com.gvugar.tourmate.adapters.MessageAdapter
import com.gvugar.tourmate.adapters.SuggestionAdapter
import com.gvugar.tourmate.databinding.FragmentChatbotBinding
import com.gvugar.tourmate.entities.ChatMessagesModel
import com.gvugar.tourmate.retrofit.builder.ApiUtils
import com.gvugar.tourmate.retrofit.dao.MessagesRoomDao
import com.gvugar.tourmate.retrofit.model.ChatMessage
import com.gvugar.tourmate.retrofit.model.request.ChatRequestModel
import com.gvugar.tourmate.retrofit.model.response.ChatResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.view.isVisible

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

        //Related with messages
        database = ChatDatabase.getInstance(requireContext())
        messagesRoomDao = database.messagesRoomDao()

        //Related with suggestions bar
        val bundle: ChatbotFragmentArgs by navArgs()
        val selectedCity = bundle.cityName

        val suggestions = listOf(
            Suggestion("Give me a 3-day trip plan for $selectedCity", "Explore a short but fulfilling itinerary."),
            Suggestion("What is the top places to visit in $selectedCity?", "Get a list of the must-see places in this city"),
            Suggestion("What is the best time to visit $selectedCity?", "Find the perfect season to explore this city.")
        )
        val suggestionAdapter = SuggestionAdapter(suggestions) { suggestion ->
            binding.messageText.setText(suggestion)
            binding.messageText.setSelection(suggestion.length)
        }
        binding.recyclerView1.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView1.adapter = suggestionAdapter

        lifecycleScope.launch(Dispatchers.IO){
            val hasMessages = messagesRoomDao.getAllMessages().isNotEmpty()
            withContext(Dispatchers.Main){
                if (!hasMessages){
                    binding.recyclerView1.visibility = View.VISIBLE
                } else{
                    binding.recyclerView1.visibility = View.GONE
                }
            }
        }

        binding.showSuggestionsBtn.setOnClickListener {
            if (binding.recyclerView1.isVisible){
                binding.recyclerView1.visibility = View.GONE
            } else{
                binding.recyclerView1.visibility = View.VISIBLE
            }
        }

        //Related with messages
        messageAdapter = MessageAdapter(messageList) { showReportDialog(requireContext()) }
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

        binding.deleteBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                messagesRoomDao.deleteAllMessages()
            }
            messageList.clear()
            messageAdapter.notifyDataSetChanged()
        }


    }

    private fun showReportDialog(context: Context){
        val reportView = LayoutInflater.from(context).inflate(R.layout.report_dialog, null)
        val spinner = reportView.findViewById<Spinner>(R.id.spinner_reason)
        val commentEditText = reportView.findViewById<EditText>(R.id.comment_text)
        val sendButton = reportView.findViewById<Button>(R.id.sendBtn)
        val cancelButton = reportView.findViewById<Button>(R.id.cancelBtn)

        ArrayAdapter.createFromResource(
            context,
            R.array.report_spinner,
            R.layout.spinner_item,
            ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_item)
            spinner.adapter = adapter
        }

        val reportDialog = AlertDialog.Builder(context).
                setView(reportView).
                setCancelable(false).
                create()

        reportDialog.show()

        cancelButton.setOnClickListener {
            reportDialog.dismiss()
        }

        sendButton.setOnClickListener {
            val selectedReason = spinner.selectedItem.toString()

            if (selectedReason == "Select reason"){
                Toast.makeText(context, "Please select a reason", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val comment = commentEditText.text.toString()

            reportDialog.dismiss()
            showConfirmationDialog(context, selectedReason, comment)
        }
    }

    private fun showConfirmationDialog(context: Context, selectedReason: String, comment: String) {
        val confirmView = LayoutInflater.from(context).inflate(R.layout.confirmation_dialog, null)
        val okButton = confirmView.findViewById<Button>(R.id.ok_button)

        val confirmDialog = AlertDialog.Builder(context).
                setView(confirmView).
                setCancelable(false).
                create()

        confirmDialog.show()

        okButton.setOnClickListener {
            confirmDialog.dismiss()
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
                       binding.recyclerView1.visibility = View.GONE

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