package com.meenachinmay.api_gateway.service.pusher

import com.pusher.rest.Pusher
import org.springframework.stereotype.Service

@Service
class PusherService(private val pusher: Pusher) {
    fun triggerEvent(channelName: String, eventName: String, payload: String) {
       pusher.trigger(channelName, eventName, payload)
    }

}