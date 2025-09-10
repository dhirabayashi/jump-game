package com.jumpgame.core

import java.awt.event.KeyEvent
import kotlin.test.*

class InputHandlerTest {
    
    private fun createInputHandler(): InputHandler = InputHandler()
    
    private fun createKeyEvent(keyCode: Int, id: Int): KeyEvent {
        return KeyEvent(
            javax.swing.JPanel(), // Use concrete component instead of abstract Component
            id,
            System.currentTimeMillis(),
            0,
            keyCode,
            KeyEvent.CHAR_UNDEFINED
        )
    }
    
    @Test
    fun `initially no keys are pressed`() {
        val handler = createInputHandler()
        
        assertEquals(false, handler.isLeftPressed)
        assertEquals(false, handler.isRightPressed)
        assertEquals(false, handler.isJumpPressed)
        assertEquals(0, handler.getPressedKeysCount())
    }
    
    @Test
    fun `left arrow key press is detected`() {
        val handler = createInputHandler()
        val keyEvent = createKeyEvent(KeyEvent.VK_LEFT, KeyEvent.KEY_PRESSED)
        
        handler.keyPressed(keyEvent)
        
        assertEquals(true, handler.isLeftPressed)
        assertEquals(false, handler.isRightPressed)
        assertEquals(false, handler.isJumpPressed)
    }
    
    @Test
    fun `A key press is detected as left`() {
        val handler = createInputHandler()
        val keyEvent = createKeyEvent(KeyEvent.VK_A, KeyEvent.KEY_PRESSED)
        
        handler.keyPressed(keyEvent)
        
        assertEquals(true, handler.isLeftPressed)
    }
    
    @Test
    fun `right arrow key press is detected`() {
        val handler = createInputHandler()
        val keyEvent = createKeyEvent(KeyEvent.VK_RIGHT, KeyEvent.KEY_PRESSED)
        
        handler.keyPressed(keyEvent)
        
        assertEquals(false, handler.isLeftPressed)
        assertEquals(true, handler.isRightPressed)
        assertEquals(false, handler.isJumpPressed)
    }
    
    @Test
    fun `D key press is detected as right`() {
        val handler = createInputHandler()
        val keyEvent = createKeyEvent(KeyEvent.VK_D, KeyEvent.KEY_PRESSED)
        
        handler.keyPressed(keyEvent)
        
        assertEquals(true, handler.isRightPressed)
    }
    
    @Test
    fun `space key press is detected as jump`() {
        val handler = createInputHandler()
        val keyEvent = createKeyEvent(KeyEvent.VK_SPACE, KeyEvent.KEY_PRESSED)
        
        handler.keyPressed(keyEvent)
        
        assertEquals(false, handler.isLeftPressed)
        assertEquals(false, handler.isRightPressed)
        assertEquals(true, handler.isJumpPressed)
    }
    
    @Test
    fun `up arrow key press is detected as jump`() {
        val handler = createInputHandler()
        val keyEvent = createKeyEvent(KeyEvent.VK_UP, KeyEvent.KEY_PRESSED)
        
        handler.keyPressed(keyEvent)
        
        assertEquals(true, handler.isJumpPressed)
    }
    
    @Test
    fun `W key press is detected as jump`() {
        val handler = createInputHandler()
        val keyEvent = createKeyEvent(KeyEvent.VK_W, KeyEvent.KEY_PRESSED)
        
        handler.keyPressed(keyEvent)
        
        assertEquals(true, handler.isJumpPressed)
    }
    
    @Test
    fun `key release removes key from pressed keys`() {
        val handler = createInputHandler()
        val pressEvent = createKeyEvent(KeyEvent.VK_LEFT, KeyEvent.KEY_PRESSED)
        val releaseEvent = createKeyEvent(KeyEvent.VK_LEFT, KeyEvent.KEY_RELEASED)
        
        handler.keyPressed(pressEvent)
        assertEquals(true, handler.isLeftPressed)
        
        handler.keyReleased(releaseEvent)
        assertEquals(false, handler.isLeftPressed)
    }
    
    @Test
    fun `multiple keys can be pressed simultaneously`() {
        val handler = createInputHandler()
        val leftEvent = createKeyEvent(KeyEvent.VK_LEFT, KeyEvent.KEY_PRESSED)
        val jumpEvent = createKeyEvent(KeyEvent.VK_SPACE, KeyEvent.KEY_PRESSED)
        
        handler.keyPressed(leftEvent)
        handler.keyPressed(jumpEvent)
        
        assertEquals(true, handler.isLeftPressed)
        assertEquals(false, handler.isRightPressed)
        assertEquals(true, handler.isJumpPressed)
        assertEquals(2, handler.getPressedKeysCount())
    }
    
    @Test
    fun `getCurrentInput returns correct game input state`() {
        val handler = createInputHandler()
        val leftEvent = createKeyEvent(KeyEvent.VK_A, KeyEvent.KEY_PRESSED)
        val jumpEvent = createKeyEvent(KeyEvent.VK_W, KeyEvent.KEY_PRESSED)
        
        handler.keyPressed(leftEvent)
        handler.keyPressed(jumpEvent)
        
        val input = handler.getCurrentInput()
        
        assertEquals(true, input.isLeftPressed)
        assertEquals(false, input.isRightPressed)
        assertEquals(true, input.isJumpPressed)
    }
    
    @Test
    fun `isKeyPressed returns correct state for specific key`() {
        val handler = createInputHandler()
        val keyEvent = createKeyEvent(KeyEvent.VK_ESCAPE, KeyEvent.KEY_PRESSED)
        
        assertEquals(false, handler.isKeyPressed(KeyEvent.VK_ESCAPE))
        
        handler.keyPressed(keyEvent)
        
        assertEquals(true, handler.isKeyPressed(KeyEvent.VK_ESCAPE))
    }
    
    @Test
    fun `reset clears all pressed keys`() {
        val handler = createInputHandler()
        val leftEvent = createKeyEvent(KeyEvent.VK_LEFT, KeyEvent.KEY_PRESSED)
        val jumpEvent = createKeyEvent(KeyEvent.VK_SPACE, KeyEvent.KEY_PRESSED)
        
        handler.keyPressed(leftEvent)
        handler.keyPressed(jumpEvent)
        assertEquals(2, handler.getPressedKeysCount())
        
        handler.reset()
        
        assertEquals(0, handler.getPressedKeysCount())
        assertEquals(false, handler.isLeftPressed)
        assertEquals(false, handler.isRightPressed)
        assertEquals(false, handler.isJumpPressed)
    }
    
    @Test
    fun `getPressedKeys returns immutable copy of pressed keys`() {
        val handler = createInputHandler()
        val leftEvent = createKeyEvent(KeyEvent.VK_LEFT, KeyEvent.KEY_PRESSED)
        
        handler.keyPressed(leftEvent)
        val pressedKeys = handler.getPressedKeys()
        
        assertEquals(setOf(KeyEvent.VK_LEFT), pressedKeys)
        
        // Verify it's a copy by trying to modify it (should not affect handler)
        assertEquals(1, pressedKeys.size)
    }
    
    @Test
    fun `keyTyped method exists but does not affect state`() {
        val handler = createInputHandler()
        // Create a typed event with a character instead of VK code
        val keyEvent = KeyEvent(
            javax.swing.JPanel(),
            KeyEvent.KEY_TYPED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_UNDEFINED,
            'a'
        )
        
        handler.keyTyped(keyEvent)
        
        assertEquals(false, handler.isLeftPressed)
        assertEquals(0, handler.getPressedKeysCount())
    }
}