package com.gvugar.tourmate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gvugar.tourmate.databinding.MessageItemBinding
import com.gvugar.tourmate.retrofit.model.ChatMessage

class MessageAdapter (private val messageList:MutableList<ChatMessage>): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(private val binding: MessageItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            if (message.isSentByUser) {
                binding.sentMessage.visibility = View.VISIBLE
                binding.receivedMessage.visibility = View.GONE
                binding.sentMessageText.text = message.message
            } else {
                binding.sentMessage.visibility = View.GONE
                binding.receivedMessage.visibility = View.VISIBLE
                binding.receivedMessageText.text = message.message
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