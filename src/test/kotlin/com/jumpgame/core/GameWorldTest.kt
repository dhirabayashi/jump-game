package com.jumpgame.core

import com.jumpgame.util.Vector2D
import kotlin.test.*

class GameWorldTest {
    
    private fun createGameWorld(): GameWorld = GameWorld()
    
    @Test
    fun `game world initializes with correct constants`() {
        val gameWorld = createGameWorld()
        
        assertEquals(800, gameWorld.gameWidth)
        assertEquals(600, gameWorld.gameHeight)
        assertEquals(400, gameWorld.groundLevel)
    }
    
    @Test
    fun `game world initializes player at correct position`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        assertEquals(Vector2D(100, 300), player.position)
    }
    
    @Test
    fun `handleInput processes left movement`() {
        val gameWorld = createGameWorld()
        val input = GameInput(isLeftPressed = true)
        
        gameWorld.handleInput(input)
        
        val player = gameWorld.getPlayer()
        assertEquals(-200.0, player.velocity.x)
    }
    
    @Test
    fun `handleInput processes right movement`() {
        val gameWorld = createGameWorld()
        val input = GameInput(isRightPressed = true)
        
        gameWorld.handleInput(input)
        
        val player = gameWorld.getPlayer()
        assertEquals(200.0, player.velocity.x)
    }
    
    @Test
    fun `handleInput processes jump command`() {
        val gameWorld = createGameWorld()
        
        // First move player to ground
        val player = gameWorld.getPlayer()
        player.position = Vector2D(100.0, 352.0)
        gameWorld.update()
        
        val input = GameInput(isJumpPressed = true)
        gameWorld.handleInput(input)
        
        assertEquals(-400.0, player.velocity.y)
        assertEquals(true, player.isJumping)
    }
    
    @Test
    fun `handleInput stops horizontal movement when no directional input`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // First give player some horizontal movement
        gameWorld.handleInput(GameInput(isRightPressed = true))
        
        val input = GameInput() // No keys pressed
        gameWorld.handleInput(input)
        
        assertEquals(0.0, player.velocity.x)
    }
    
    @Test
    fun `handleInput processes combined left and jump input`() {
        val gameWorld = createGameWorld()
        
        // Move player to ground first
        val player = gameWorld.getPlayer()
        player.position = Vector2D(100.0, 352.0)
        gameWorld.update()
        
        val input = GameInput(isLeftPressed = true, isJumpPressed = true)
        gameWorld.handleInput(input)
        
        assertEquals(-200.0, player.velocity.x)
        assertEquals(-400.0, player.velocity.y)
    }
    
    @Test
    fun `update calls player update`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        val initialPosition = player.position
        
        // Give player some velocity by moving right to see if update is called
        gameWorld.handleInput(GameInput(isRightPressed = true))
        
        Thread.sleep(50) // Small delay to ensure time difference
        gameWorld.update()
        
        // Position should have changed if update was called
        assertNotEquals(initialPosition, player.position)
    }
    
    @Test
    fun `player is kept within left boundary`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Move player beyond left boundary
        player.position = Vector2D(-10.0, 300.0)
        gameWorld.update()
        
        assertEquals(0.0, player.position.x)
    }
    
    @Test
    fun `player is kept within right boundary`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Move player beyond right boundary
        player.position = Vector2D(800.0, 300.0)
        gameWorld.update()
        
        assertEquals(768.0, player.position.x) // 800 - 32 (player width)
    }
    
    @Test
    fun `player position is not changed when within boundaries`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        val validPosition = Vector2D(400.0, 300.0)
        
        player.position = validPosition
        gameWorld.update()
        
        assertEquals(validPosition.x, player.position.x)
    }
    
    @Test
    fun `reset restores initial game state`() {
        val gameWorld = createGameWorld()
        val player = gameWorld.getPlayer()
        
        // Change player state
        player.position = Vector2D(500.0, 100.0)
        
        gameWorld.reset()
        
        assertEquals(Vector2D(100, 300), player.position)
    }
    
    @Test
    fun `GameInput data class has correct default values`() {
        val input = GameInput()
        
        assertEquals(false, input.isLeftPressed)
        assertEquals(false, input.isRightPressed)
        assertEquals(false, input.isJumpPressed)
    }
    
    @Test
    fun `GameInput data class accepts custom values`() {
        val input = GameInput(
            isLeftPressed = true,
            isRightPressed = false,
            isJumpPressed = true
        )
        
        assertEquals(true, input.isLeftPressed)
        assertEquals(false, input.isRightPressed)
        assertEquals(true, input.isJumpPressed)
    }
    
    @Test
    fun `GameInput data class equality works correctly`() {
        val input1 = GameInput(isLeftPressed = true, isJumpPressed = true)
        val input2 = GameInput(isLeftPressed = true, isJumpPressed = true)
        val input3 = GameInput(isRightPressed = true)
        
        assertEquals(input1, input2)
        assertNotEquals(input1, input3)
    }
}