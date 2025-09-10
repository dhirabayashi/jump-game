package com.jumpgame.util

import kotlin.test.*

class Vector2DTest {
    
    @Test
    fun `constructor with doubles creates vector correctly`() {
        val vector = Vector2D(3.5, 4.2)
        
        assertEquals(3.5, vector.x)
        assertEquals(4.2, vector.y)
    }
    
    @Test
    fun `constructor with ints creates vector correctly`() {
        val vector = Vector2D(5, 7)
        
        assertEquals(5.0, vector.x)
        assertEquals(7.0, vector.y)
    }
    
    @Test
    fun `add returns correct sum of vectors`() {
        val vector1 = Vector2D(2.0, 3.0)
        val vector2 = Vector2D(4.0, 5.0)
        
        val result = vector1.add(vector2)
        
        assertEquals(6.0, result.x)
        assertEquals(8.0, result.y)
    }
    
    @Test
    fun `subtract returns correct difference of vectors`() {
        val vector1 = Vector2D(8.0, 6.0)
        val vector2 = Vector2D(3.0, 2.0)
        
        val result = vector1.subtract(vector2)
        
        assertEquals(5.0, result.x)
        assertEquals(4.0, result.y)
    }
    
    @Test
    fun `multiply returns vector scaled by scalar`() {
        val vector = Vector2D(2.0, 3.0)
        
        val result = vector.multiply(2.5)
        
        assertEquals(5.0, result.x)
        assertEquals(7.5, result.y)
    }
    
    @Test
    fun `magnitude returns correct length of vector`() {
        val vector = Vector2D(3.0, 4.0)
        
        val result = vector.magnitude()
        
        assertEquals(5.0, result)
    }
    
    @Test
    fun `magnitude returns zero for zero vector`() {
        val vector = Vector2D.ZERO
        
        val result = vector.magnitude()
        
        assertEquals(0.0, result)
    }
    
    @Test
    fun `normalize returns unit vector in same direction`() {
        val vector = Vector2D(3.0, 4.0)
        
        val result = vector.normalize()
        
        assertEquals(0.6, result.x)
        assertEquals(0.8, result.y)
        assertEquals(1.0, result.magnitude(), 0.0001)
    }
    
    @Test
    fun `normalize returns zero vector when normalizing zero vector`() {
        val vector = Vector2D.ZERO
        
        val result = vector.normalize()
        
        assertEquals(Vector2D.ZERO, result)
    }
    
    @Test
    fun `companion constants have correct values`() {
        assertEquals(Vector2D(0.0, 0.0), Vector2D.ZERO)
        assertEquals(Vector2D(0.0, -1.0), Vector2D.UP)
        assertEquals(Vector2D(0.0, 1.0), Vector2D.DOWN)
        assertEquals(Vector2D(-1.0, 0.0), Vector2D.LEFT)
        assertEquals(Vector2D(1.0, 0.0), Vector2D.RIGHT)
    }
    
    @Test
    fun `data class equality works correctly`() {
        val vector1 = Vector2D(1.0, 2.0)
        val vector2 = Vector2D(1.0, 2.0)
        val vector3 = Vector2D(2.0, 1.0)
        
        assertEquals(vector1, vector2)
        assertNotEquals(vector1, vector3)
    }
}