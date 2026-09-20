package com.example

import com.example.model.SandBottle
import com.example.model.SandColor
import com.example.model.SandLevels
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SandGameLogicTest {

    @Test
    fun testLevelLoadingAndValidity() {
        // Verify all curated levels 1 through 20
        for (lvl in 1..20) {
            val level = SandLevels.getLevel(lvl)
            assertEquals("Level number should match", lvl, level.levelNumber)
            assertTrue("Level $lvl should have at least 3 bottles", level.bottles.size >= 3)
            val emptyBottles = level.bottles.count { it.isEmpty() }
            assertTrue("Level $lvl should have at least 1 empty bottle", emptyBottles >= 1)

            // Verify color distribution: each color present must have exactly 4 units
            val allUnits = level.bottles.flatten()
            val colorCounts = allUnits.groupingBy { it }.eachCount()
            colorCounts.forEach { (color, count) ->
                assertEquals("Level $lvl color $color should have exactly 4 units", 4, count)
            }
        }
    }

    @Test
    fun testProceduralLevelGeneration() {
        for (lvl in listOf(21, 25, 35, 50)) {
            val level = SandLevels.getLevel(lvl)
            assertTrue("Procedural level $lvl must have at least 1 empty bottle", level.bottles.any { it.isEmpty() })
            val allUnits = level.bottles.flatten()
            val colorCounts = allUnits.groupingBy { it }.eachCount()
            colorCounts.forEach { (color, count) ->
                assertEquals("Procedural level $lvl color $color should have 4 units", 4, count)
            }
        }
    }

    @Test
    fun testBottleProperties() {
        val emptyBottle = SandBottle(id = 0, capacity = 4, layers = emptyList())
        assertTrue(emptyBottle.isEmpty)
        assertFalse(emptyBottle.isFull)
        assertEquals(0, emptyBottle.topGroupCount)
        assertEquals(4, emptyBottle.availableCapacity)

        val fullBottle = SandBottle(
            id = 1,
            capacity = 4,
            layers = listOf(SandColor.BLUE, SandColor.BLUE, SandColor.BLUE, SandColor.BLUE)
        )
        assertTrue(fullBottle.isFull)
        assertFalse(fullBottle.isEmpty)
        assertEquals(4, fullBottle.topGroupCount)
        assertTrue(fullBottle.isUniformComplete)

        val mixedBottle = SandBottle(
            id = 2,
            capacity = 4,
            layers = listOf(SandColor.RED, SandColor.YELLOW, SandColor.YELLOW, SandColor.YELLOW)
        )
        assertEquals(3, mixedBottle.topGroupCount)
        assertEquals(SandColor.YELLOW, mixedBottle.topColor)
        assertFalse(mixedBottle.isUniformComplete)
    }
}
