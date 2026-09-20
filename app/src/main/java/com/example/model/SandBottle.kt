package com.example.model

data class SandBottle(
    val id: Int,
    val capacity: Int = 4,
    val layers: List<SandColor> = emptyList(),
    val isCompleted: Boolean = false,
    val isLocked: Boolean = false
) {
    val isFull: Boolean get() = layers.size >= capacity
    val isEmpty: Boolean get() = layers.isEmpty()
    val isNotEmpty: Boolean get() = layers.isNotEmpty()
    val topColor: SandColor? get() = layers.lastOrNull()

    /**
     * Number of continuous layers from the top having the exact same color.
     */
    val topGroupCount: Int
        get() {
            if (layers.isEmpty()) return 0
            val targetColor = layers.last()
            var count = 0
            for (i in layers.indices.reversed()) {
                if (layers[i] == targetColor) {
                    count++
                } else {
                    break
                }
            }
            return count
        }

    val availableCapacity: Int get() = capacity - layers.size

    val isUniformComplete: Boolean
        get() = layers.size == capacity && layers.all { it == layers[0] }

    val isSingleColor: Boolean
        get() = layers.isNotEmpty() && layers.all { it == layers[0] }
}

data class PourAnimation(
    val fromBottleId: Int,
    val toBottleId: Int,
    val sandColor: SandColor,
    val count: Int,
    val fromOffset: androidx.compose.ui.geometry.Offset = androidx.compose.ui.geometry.Offset.Zero,
    val toOffset: androidx.compose.ui.geometry.Offset = androidx.compose.ui.geometry.Offset.Zero
)

data class HintMove(
    val fromBottleId: Int,
    val toBottleId: Int
)
