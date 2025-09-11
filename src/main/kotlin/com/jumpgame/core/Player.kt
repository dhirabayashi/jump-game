package com.jumpgame.core

import com.jumpgame.util.Vector2D

/**
 * Represents the player character in the game with physics-based movement.
 *
 * This class handles player position, velocity, jumping mechanics, and collision detection
 * with the ground. The player responds to user input and is affected by gravity.
 *
 * @property initialPosition The starting position of the player in the game world
 */
class Player(initialPosition: Vector2D) {
    
    /** Current position of the player in the game world */
    var position: Vector2D = initialPosition
    
    /** Current velocity of the player */
    var velocity: Vector2D = Vector2D.ZERO
    
    /** Flag indicating whether the player is currently touching the ground */
    var isOnGround: Boolean = false
        private set
    
    /** Flag indicating whether the player is currently in a jump state */
    var isJumping: Boolean = false
        private set
    
    /** Flag indicating whether the player is alive */
    var isAlive: Boolean = true
        private set
    
    /** Width of the player's collision box in pixels */
    val width: Int = 32
    
    /** Height of the player's collision box in pixels */
    val height: Int = 48
    
    companion object {
        /** Horizontal movement speed in pixels per second */
        private const val MOVE_SPEED = 200.0
        
        /** Initial upward velocity when jumping (negative because Y increases downward) */
        private const val JUMP_SPEED = -400.0
        
        /** Gravitational acceleration in pixels per second squared */
        private const val GRAVITY = 980.0
        
        /** Maximum downward falling speed to prevent unrealistic acceleration */
        private const val MAX_FALL_SPEED = 500.0
        
        /** Y-coordinate of the ground level */
        private const val GROUND_LEVEL = 400.0
    }
    
    /**
     * Moves the player to the left at the defined movement speed.
     *
     * @param deltaTime The time elapsed since the last update in seconds
     */
    fun moveLeft(deltaTime: Double) {
        if (!isAlive) return
        velocity = Vector2D(-MOVE_SPEED, velocity.y)
        updatePosition(deltaTime)
    }
    
    /**
     * Moves the player to the right at the defined movement speed.
     *
     * @param deltaTime The time elapsed since the last update in seconds
     */
    fun moveRight(deltaTime: Double) {
        if (!isAlive) return
        velocity = Vector2D(MOVE_SPEED, velocity.y)
        updatePosition(deltaTime)
    }
    
    /**
     * Makes the player jump if they are currently on the ground.
     * Only allows jumping when the player is on the ground and not already jumping.
     */
    fun jump() {
        if (isAlive && isOnGround && !isJumping) {
            velocity = Vector2D(velocity.x, JUMP_SPEED)
            isOnGround = false
            isJumping = true
        }
    }
    
    /**
     * Stops the player's horizontal movement while preserving vertical velocity.
     * Used when no movement input is detected.
     */
    fun stopHorizontalMovement() {
        velocity = Vector2D(0.0, velocity.y)
    }
    
    /**
     * Updates the player's state including physics simulation and collision detection.
     * Should be called once per frame to maintain proper game physics.
     *
     * @param deltaTime The time elapsed since the last update in seconds
     */
    fun update(deltaTime: Double) {
        if (!isAlive) return
        
        applyGravity(deltaTime)
        updatePosition(deltaTime)
        checkGroundCollision()
    }
    
    /**
     * Applies gravitational force to the player when they are not on the ground.
     * Limits falling speed to prevent excessive acceleration.
     *
     * @param deltaTime The time elapsed since the last update in seconds
     */
    private fun applyGravity(deltaTime: Double) {
        if (!isOnGround) {
            val newYVelocity = velocity.y + GRAVITY * deltaTime
            velocity = Vector2D(velocity.x, minOf(newYVelocity, MAX_FALL_SPEED))
        }
    }
    
    /**
     * Updates the player's position based on their current velocity.
     *
     * @param deltaTime The time elapsed since the last update in seconds
     */
    private fun updatePosition(deltaTime: Double) {
        position = position.add(velocity.multiply(deltaTime))
    }
    
    /**
     * Checks for collision with the ground and handles landing.
     * When the player touches the ground, stops vertical movement and updates ground state.
     */
    private fun checkGroundCollision() {
        if (position.y + height >= GROUND_LEVEL) {
            position = Vector2D(position.x, GROUND_LEVEL - height)
            velocity = Vector2D(velocity.x, 0.0)
            isOnGround = true
            isJumping = false
        } else {
            isOnGround = false
        }
    }
    
    /**
     * Returns the collision bounds of the player for rendering and collision detection.
     *
     * @return PlayerBounds object containing the player's rectangular boundaries
     */
    fun getBounds(): PlayerBounds {
        return PlayerBounds(
            position.x.toInt(),
            position.y.toInt(),
            width,
            height
        )
    }
    
    /**
     * Resets the player to a new position with default states.
     * Clears velocity and ground/jumping flags.
     *
     * @param newPosition The position to reset the player to
     */
    fun reset(newPosition: Vector2D) {
        position = newPosition
        velocity = Vector2D.ZERO
        isOnGround = false
        isJumping = false
        isAlive = true
    }
    
    /**
     * Kills the player, stopping all movement and preventing further actions.
     * Called when the player collides with an enemy or falls into a hazard.
     */
    fun die() {
        isAlive = false
        velocity = Vector2D.ZERO
    }
}

/**
 * Represents the rectangular boundaries of a player for collision detection and rendering.
 *
 * @property x The x-coordinate of the top-left corner
 * @property y The y-coordinate of the top-left corner
 * @property width The width of the bounding rectangle
 * @property height The height of the bounding rectangle
 */
data class PlayerBounds(val x: Int, val y: Int, val width: Int, val height: Int)