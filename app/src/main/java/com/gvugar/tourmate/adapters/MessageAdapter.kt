package com.gvugar.tourmate.adapters

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gvugar.tourmate.databinding.MessageItemBinding
import com.gvugar.tourmate.retrofit.model.ChatMessage

class MessageAdapter (private val messageList:MutableList<ChatMessage>, private val onReportClick: () -> Unit): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(private val binding: MessageItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            if (message.message == "AI is typing..."){
                binding.sentMessage.visibility = View.GONE
                binding.receivedMessage.visibility = View.VISIBLE
                binding.reportButton.visibility = View.GONE
                binding.receivedMessageText.text = message.message
            } else{
                if (message.isSentByUser) {
                    binding.sentMessage.visibility = View.VISIBLE
                    binding.receivedMessage.visibility = View.GONE
                    binding.reportButton.visibility = View.GONE
                    binding.sentMessageText.text = message.message
                } else {
                    binding.sentMessage.visibility = View.GONE
                    binding.receivedMessage.visibility = View.VISIBLE
                    binding.reportButton.visibility = View.VISIBLE
                    binding.receivedMessageText.text = message.message
                }
            }
            binding.reportButton.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.alpha = 0.5f  // darken when pressed
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        v.alpha = 1.0f  // restore to normal
                    }
                }
                false  // return false to allow onClick to work
            }
            binding.reportButton.setOnClickListener {
                onReportClick()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = MessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messageList[position])
    }

    fun addMessage(message: ChatMessage) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }

    fun updateLastMessage(message: String) {
        if (messageList.isNotEmpty()) {
            messageList[messageList.size - 1].message = message
            notifyItemChanged(messageList.size - 1)
        }

    }
}