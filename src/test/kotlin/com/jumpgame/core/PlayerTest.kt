package com.jumpgame.core

import com.jumpgame.util.Vector2D
import kotlin.test.*

class PlayerTest {
    
    private fun createPlayer(x: Double = 100.0, y: Double = 300.0): Player {
        return Player(Vector2D(x, y))
    }
    
    @Test
    fun `player initializes with correct position and defaults`() {
        val initialPosition = Vector2D(50.0, 200.0)
        val player = Player(initialPosition)
        
        assertEquals(initialPosition, player.position)
        assertEquals(Vector2D.ZERO, player.velocity)
        assertEquals(false, player.isOnGround)
        assertEquals(false, player.isJumping)
        assertEquals(true, player.isAlive)
        assertEquals(32, player.width)
        assertEquals(48, player.height)
    }
    
    @Test
    fun `moveLeft sets velocity and updates position`() {
        val player = createPlayer()
        val initialPosition = player.position
        
        player.moveLeft(0.1)
        
        assertEquals(-200.0, player.velocity.x)
        assertEquals(initialPosition.x - 20.0, player.position.x)
    }
    
    @Test
    fun `moveRight sets velocity and updates position`() {
        val player = createPlayer()
        val initialPosition = player.position
        
        player.moveRight(0.1)
        
        assertEquals(200.0, player.velocity.x)
        assertEquals(initialPosition.x + 20.0, player.position.x)
    }
    
    @Test
    fun `jump sets velocity when on ground`() {
        val player = createPlayer()
        player.position = Vector2D(100.0, 352.0) // On ground
        player.update(0.016) // Update to set ground state
        
        player.jump()
        
        assertEquals(-400.0, player.velocity.y)
        assertEquals(false, player.isOnGround)
        assertEquals(true, player.isJumping)
    }
    
    @Test
    fun `jump does nothing when not on ground`() {
        val player = createPlayer()
        val originalVelocity = player.velocity
        
        player.jump()
        
        assertEquals(originalVelocity, player.velocity)
        assertEquals(false, player.isJumping)
    }
    
    @Test
    fun `jump does nothing when already jumping`() {
        val player = createPlayer()
        player.position = Vector2D(100.0, 352.0) // On ground
        player.update(0.016) // Update to set ground state
        player.jump() // First jump
        val velocityAfterFirstJump = player.velocity
        
        player.jump() // Try to jump again
        
        assertEquals(velocityAfterFirstJump, player.velocity)
    }
    
    @Test
    fun `stopHorizontalMovement zeroes x velocity`() {
        val player = createPlayer()
        player.moveRight(0.1) // Set some x velocity
        
        player.stopHorizontalMovement()
        
        assertEquals(0.0, player.velocity.x)
    }
    
    @Test
    fun `update applies gravity when not on ground`() {
        val player = createPlayer()
        val initialYVelocity = player.velocity.y
        
        player.update(0.1)
        
        assertEquals(initialYVelocity + 98.0, player.velocity.y)
    }
    
    @Test
    fun `update does not apply gravity when on ground`() {
        val player = createPlayer()
        player.position = Vector2D(100.0, 352.0) // On ground
        player.update(0.016) // First update to set ground state
        val velocityAfterGroundSet = player.velocity
        
        player.update(0.1) // Second update
        
        assertEquals(velocityAfterGroundSet.y, player.velocity.y)
    }
    
    @Test
    fun `update caps fall speed at maximum`() {
        val player = createPlayer()
        // Make player fall for a long time to reach max speed
        repeat (20) {
            player.update(0.1)
            if (player.velocity.y >= 500.0) return@repeat
        }
        
        assertTrue(player.velocity.y <= 500.0, "Velocity should not exceed max fall speed")
    }
    
    @Test
    fun `ground collision sets player on ground`() {
        val player = createPlayer()
        player.position = Vector2D(100.0, 360.0) // Below ground
        
        player.update(0.016)
        
        assertEquals(352.0, player.position.y) // 400 - 48
        assertEquals(0.0, player.velocity.y)
        assertEquals(true, player.isOnGround)
        assertEquals(false, player.isJumping)
    }
    
    @Test
    fun `getBounds returns correct player bounds`() {
        val player = createPlayer(150.0, 250.0)
        
        val bounds = player.getBounds()
        
        assertEquals(150, bounds.x)
        assertEquals(250, bounds.y)
        assertEquals(32, bounds.width)
        assertEquals(48, bounds.height)
    }
    
    @Test
    fun `player bounds data class has correct properties`() {
        val bounds = PlayerBounds(10, 20, 32, 48)
        
        assertEquals(10, bounds.x)
        assertEquals(20, bounds.y)
        assertEquals(32, bounds.width)
        assertEquals(48, bounds.height)
    }
    
    @Test
    fun `die kills player and stops movement`() {
        val player = createPlayer()
        player.moveRight(0.1) // Give some velocity
        assertTrue(player.isAlive)
        
        player.die()
        
        assertFalse(player.isAlive)
        assertEquals(Vector2D.ZERO, player.velocity)
    }
    
    @Test
    fun `dead player cannot move left`() {
        val player = createPlayer()
        player.die()
        val positionBeforeDeath = player.position
        
        player.moveLeft(0.1)
        
        assertEquals(positionBeforeDeath, player.position)
        assertEquals(Vector2D.ZERO, player.velocity)
    }
    
    @Test
    fun `dead player cannot move right`() {
        val player = createPlayer()
        player.die()
        val positionBeforeDeath = player.position
        
        player.moveRight(0.1)
        
        assertEquals(positionBeforeDeath, player.position)
        assertEquals(Vector2D.ZERO, player.velocity)
    }
    
    @Test
    fun `dead player cannot jump`() {
        val player = createPlayer()
        player.position = Vector2D(100.0, 352.0) // On ground
        player.update(0.016) // Update to set ground state
        player.die()
        
        player.jump()
        
        assertFalse(player.isJumping)
        assertEquals(Vector2D.ZERO, player.velocity)
    }
    
    @Test
    fun `dead player does not update physics`() {
        val player = createPlayer()
        player.die()
        val positionAfterDeath = player.position
        val velocityAfterDeath = player.velocity
        
        player.update(0.1)
        
        assertEquals(positionAfterDeath, player.position)
        assertEquals(velocityAfterDeath, player.velocity)
    }
    
    @Test
    fun `reset revives dead player`() {
        val player = createPlayer()
        player.die()
        assertFalse(player.isAlive)
        
        val newPosition = Vector2D(200.0, 250.0)
        player.reset(newPosition)
        
        assertTrue(player.isAlive)
        assertEquals(newPosition, player.position)
        assertEquals(Vector2D.ZERO, player.velocity)
        assertFalse(player.isOnGround)
        assertFalse(player.isJumping)
    }
    
    @Test
    fun `reset clears all player states including alive status`() {
        val player = createPlayer()
        player.moveRight(0.1) // Set some velocity
        player.jump() // Try to jump
        player.die() // Kill player
        
        val resetPosition = Vector2D(300.0, 100.0)
        player.reset(resetPosition)
        
        assertEquals(resetPosition, player.position)
        assertEquals(Vector2D.ZERO, player.velocity)
        assertFalse(player.isOnGround)
        assertFalse(player.isJumping)
        assertTrue(player.isAlive)
    }
}
