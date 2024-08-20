package com.meenachinmay.api_gateway.controller.auth

import com.meenachinmay.api_gateway.service.game.GameService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("/api/game")
@Controller
class GameController (private val gameService: GameService) {
    @PostMapping("/join-room")
    fun joinRoom(@RequestParam roomId: String): ResponseEntity<String> {
        gameService.joinRoom(roomId)
        return ResponseEntity("Room joined successfully: $roomId", HttpStatus.ACCEPTED)
    }
}