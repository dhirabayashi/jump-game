package com.jumpgame.core

import com.jumpgame.util.Vector2D

/**
 * Manages the game world state, including the player and game logic.
 *
 * This class serves as the central coordinator for game entities, handling
 * player updates, input processing, timing, and boundary enforcement.
 * It maintains the game's coordinate system and world properties.
 */
class GameWorld {
    
    /** The player character in the game world */
    private val player: Player = Player(Vector2D(100, 300))
    
    /** Timestamp of the last update for delta time calculation */
    private var lastUpdateTime: Long = System.nanoTime()
    
    /** Width of the game world in pixels */
    val gameWidth: Int = 800
    
    /** Height of the game world in pixels */
    val gameHeight: Int = 600
    
    /** Y-coordinate of the ground level */
    val groundLevel: Int = 400
    
    /**
     * Returns the player instance for external access.
     *
     * @return The Player object managed by this game world
     */
    fun getPlayer(): Player = player
    
    /**
     * Updates the game world state including player physics and boundary checks.
     * Calculates delta time for consistent physics simulation and updates the player.
     * Should be called once per frame during the game loop.
     */
    fun update() {
        val currentTime = System.nanoTime()
        val deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0
        lastUpdateTime = currentTime
        
        player.update(deltaTime)
        
        keepPlayerInBounds()
    }
    
    /**
     * Processes game input and applies it to the player.
     * Handles movement commands and jump input based on the provided GameInput state.
     *
     * @param input The current input state containing pressed key information
     */
    fun handleInput(input: GameInput) {
        val deltaTime = calculateDeltaTime()
        
        when {
            input.isLeftPressed -> player.moveLeft(deltaTime)
            input.isRightPressed -> player.moveRight(deltaTime)
            else -> player.stopHorizontalMovement()
        }
        
        if (input.isJumpPressed) {
            player.jump()
        }
    }
    
    /**
     * Calculates the time elapsed since the last update.
     * Caps the delta time to prevent large jumps that could break physics simulation.
     *
     * @return The delta time in seconds, capped at approximately 60 FPS
     */
    private fun calculateDeltaTime(): Double {
        val currentTime = System.nanoTime()
        val deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0
        return deltaTime.coerceAtMost(0.016) // Cap at ~60 FPS
    }
    
    /**
     * Ensures the player remains within the game world boundaries.
     * Prevents the player from moving outside the left and right edges of the screen.
     */
    private fun keepPlayerInBounds() {
        val playerBounds = player.getBounds()
        val currentPos = player.position
        
        when {
            playerBounds.x < 0 -> {
                player.position = Vector2D(0.0, currentPos.y)
            }
            playerBounds.x + playerBounds.width > gameWidth -> {
                player.position = Vector2D(gameWidth - playerBounds.width.toDouble(), currentPos.y)
            }
        }
    }
    
    /**
     * Resets the game world to its initial state.
     * Resets the player to the starting position and reinitializes the update timer.
     */
    fun reset() {
        player.reset(Vector2D(100, 300))
        lastUpdateTime = System.nanoTime()
    }
}

/**
 * Represents the current input state for the game.
 *
 * This data class encapsulates all the input information needed to control
 * the player character, including movement and jumping commands.
 *
 * @property isLeftPressed Whether the left movement key is currently pressed
 * @property isRightPressed Whether the right movement key is currently pressed
 * @property isJumpPressed Whether the jump key is currently pressed
 */
data class GameInput(
    val isLeftPressed: Boolean = false,
    val isRightPressed: Boolean = false,
    val isJumpPressed: Boolean = false
)