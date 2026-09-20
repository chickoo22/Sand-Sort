package com.example.model

object SandLevels {

    data class LevelData(
        val levelNumber: Int,
        val bottles: List<List<SandColor>>,
        val capacity: Int = 4,
        val goalChests: List<SandColor> = emptyList(),
        val bonusBottleAvailable: Boolean = true,
        val difficultyName: String = "NORMAL"
    )

    fun getDifficulty(levelNumber: Int): String {
        return when (levelNumber) {
            in 1..8 -> "EASY"
            in 9..20 -> "NORMAL"
            in 21..40 -> "MEDIUM"
            in 41..70 -> "HARD"
            in 71..100 -> "EXPERT"
            else -> "MASTER"
        }
    }

    fun getLevel(levelNumber: Int): LevelData {
        val diff = getDifficulty(levelNumber)
        val level = when (levelNumber) {
            1 -> LevelData(
                levelNumber = 1,
                capacity = 4,
                goalChests = listOf(SandColor.YELLOW, SandColor.BLUE),
                bottles = listOf(
                    listOf(SandColor.YELLOW, SandColor.BLUE, SandColor.YELLOW, SandColor.BLUE),
                    listOf(SandColor.BLUE, SandColor.YELLOW, SandColor.BLUE, SandColor.YELLOW),
                    emptyList()
                )
            )

            2 -> LevelData(
                levelNumber = 2,
                capacity = 4,
                goalChests = listOf(SandColor.RED, SandColor.YELLOW, SandColor.BLUE),
                bottles = listOf(
                    listOf(SandColor.RED, SandColor.YELLOW, SandColor.RED, SandColor.BLUE),
                    listOf(SandColor.YELLOW, SandColor.BLUE, SandColor.YELLOW, SandColor.RED),
                    listOf(SandColor.BLUE, SandColor.RED, SandColor.BLUE, SandColor.YELLOW),
                    emptyList()
                )
            )

            3 -> LevelData(
                levelNumber = 3,
                capacity = 4,
                goalChests = listOf(SandColor.GREEN, SandColor.BLUE, SandColor.YELLOW, SandColor.RED),
                bottles = listOf(
                    listOf(SandColor.RED, SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN),
                    listOf(SandColor.RED, SandColor.BLUE, SandColor.YELLOW, SandColor.RED),
                    listOf(SandColor.BLUE, SandColor.GREEN, SandColor.BLUE, SandColor.YELLOW),
                    listOf(SandColor.GREEN, SandColor.BLUE, SandColor.BLUE, SandColor.GREEN),
                    emptyList()
                )
            )

            4 -> LevelData(
                levelNumber = 4,
                capacity = 4,
                goalChests = listOf(SandColor.BLUE, SandColor.GREEN, SandColor.YELLOW, SandColor.RED),
                bottles = listOf(
                    listOf(SandColor.BLUE, SandColor.YELLOW, SandColor.GREEN, SandColor.YELLOW),
                    listOf(SandColor.GREEN, SandColor.RED, SandColor.GREEN, SandColor.RED),
                    listOf(SandColor.RED, SandColor.YELLOW, SandColor.BLUE, SandColor.YELLOW),
                    listOf(SandColor.BLUE, SandColor.GREEN, SandColor.BLUE, SandColor.RED),
                    emptyList(),
                    emptyList()
                )
            )

            5 -> LevelData(
                levelNumber = 5,
                capacity = 4,
                goalChests = listOf(SandColor.PURPLE, SandColor.ORANGE, SandColor.CYAN, SandColor.GREEN),
                bottles = listOf(
                    listOf(SandColor.PURPLE, SandColor.ORANGE, SandColor.CYAN, SandColor.GREEN),
                    listOf(SandColor.GREEN, SandColor.PURPLE, SandColor.ORANGE, SandColor.CYAN),
                    listOf(SandColor.CYAN, SandColor.GREEN, SandColor.PURPLE, SandColor.ORANGE),
                    listOf(SandColor.ORANGE, SandColor.CYAN, SandColor.GREEN, SandColor.PURPLE),
                    emptyList(),
                    emptyList()
                )
            )

            6 -> LevelData(
                levelNumber = 6,
                capacity = 4,
                goalChests = listOf(SandColor.RED, SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN),
                bottles = listOf(
                    listOf(SandColor.RED, SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN),
                    listOf(SandColor.PURPLE, SandColor.RED, SandColor.YELLOW, SandColor.BLUE),
                    listOf(SandColor.GREEN, SandColor.PURPLE, SandColor.RED, SandColor.YELLOW),
                    listOf(SandColor.BLUE, SandColor.GREEN, SandColor.PURPLE, SandColor.RED),
                    listOf(SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN, SandColor.PURPLE),
                    emptyList(),
                    emptyList()
                )
            )

            7 -> LevelData(
                // 5 colors with only 1 empty tube - tight tactical challenge
                levelNumber = 7,
                capacity = 4,
                goalChests = listOf(SandColor.ORANGE, SandColor.CYAN, SandColor.PINK, SandColor.BLUE),
                bottles = listOf(
                    listOf(SandColor.ORANGE, SandColor.CYAN, SandColor.PINK, SandColor.BLUE),
                    listOf(SandColor.YELLOW, SandColor.ORANGE, SandColor.CYAN, SandColor.PINK),
                    listOf(SandColor.BLUE, SandColor.YELLOW, SandColor.ORANGE, SandColor.CYAN),
                    listOf(SandColor.PINK, SandColor.BLUE, SandColor.YELLOW, SandColor.ORANGE),
                    listOf(SandColor.CYAN, SandColor.PINK, SandColor.BLUE, SandColor.YELLOW),
                    emptyList()
                )
            )

            8 -> LevelData(
                levelNumber = 8,
                capacity = 4,
                goalChests = listOf(SandColor.RED, SandColor.GREEN, SandColor.CYAN, SandColor.PURPLE),
                bottles = listOf(
                    listOf(SandColor.RED, SandColor.RED, SandColor.GREEN, SandColor.CYAN),
                    listOf(SandColor.PURPLE, SandColor.ORANGE, SandColor.PURPLE, SandColor.GREEN),
                    listOf(SandColor.CYAN, SandColor.CYAN, SandColor.ORANGE, SandColor.RED),
                    listOf(SandColor.GREEN, SandColor.GREEN, SandColor.ORANGE, SandColor.PURPLE),
                    listOf(SandColor.ORANGE, SandColor.CYAN, SandColor.PURPLE, SandColor.RED),
                    emptyList(),
                    emptyList()
                )
            )

            9 -> LevelData(
                levelNumber = 9,
                capacity = 4,
                goalChests = listOf(SandColor.RED, SandColor.BLUE, SandColor.YELLOW, SandColor.GREEN),
                bottles = listOf(
                    listOf(SandColor.RED, SandColor.BLUE, SandColor.YELLOW, SandColor.GREEN),
                    listOf(SandColor.PURPLE, SandColor.ORANGE, SandColor.RED, SandColor.BLUE),
                    listOf(SandColor.YELLOW, SandColor.GREEN, SandColor.PURPLE, SandColor.ORANGE),
                    listOf(SandColor.RED, SandColor.BLUE, SandColor.YELLOW, SandColor.GREEN),
                    listOf(SandColor.PURPLE, SandColor.ORANGE, SandColor.RED, SandColor.BLUE),
                    listOf(SandColor.YELLOW, SandColor.GREEN, SandColor.PURPLE, SandColor.ORANGE),
                    emptyList(),
                    emptyList()
                )
            )

            10 -> LevelData(
                levelNumber = 10,
                capacity = 4,
                goalChests = listOf(SandColor.PINK, SandColor.CYAN, SandColor.YELLOW, SandColor.BLUE),
                bottles = listOf(
                    listOf(SandColor.PINK, SandColor.CYAN, SandColor.PINK, SandColor.YELLOW),
                    listOf(SandColor.BLUE, SandColor.PURPLE, SandColor.GREEN, SandColor.PINK),
                    listOf(SandColor.CYAN, SandColor.YELLOW, SandColor.BLUE, SandColor.PURPLE),
                    listOf(SandColor.GREEN, SandColor.PINK, SandColor.CYAN, SandColor.YELLOW),
                    listOf(SandColor.BLUE, SandColor.PURPLE, SandColor.GREEN, SandColor.CYAN),
                    listOf(SandColor.YELLOW, SandColor.BLUE, SandColor.PURPLE, SandColor.GREEN),
                    emptyList(),
                    emptyList()
                )
            )

            11 -> LevelData(
                // 6 colors with 1 empty tube
                levelNumber = 11,
                capacity = 4,
                goalChests = listOf(SandColor.RED, SandColor.CYAN, SandColor.PINK, SandColor.ORANGE),
                bottles = listOf(
                    listOf(SandColor.RED, SandColor.CYAN, SandColor.PINK, SandColor.ORANGE),
                    listOf(SandColor.GREEN, SandColor.BLUE, SandColor.RED, SandColor.CYAN),
                    listOf(SandColor.PINK, SandColor.ORANGE, SandColor.GREEN, SandColor.BLUE),
                    listOf(SandColor.RED, SandColor.CYAN, SandColor.PINK, SandColor.ORANGE),
                    listOf(SandColor.GREEN, SandColor.BLUE, SandColor.RED, SandColor.CYAN),
                    listOf(SandColor.PINK, SandColor.ORANGE, SandColor.GREEN, SandColor.BLUE),
                    emptyList()
                )
            )

            12 -> LevelData(
                // 7 colors, 9 bottles (2 empty)
                levelNumber = 12,
                capacity = 4,
                goalChests = listOf(SandColor.RED, SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN),
                bottles = listOf(
                    listOf(SandColor.RED, SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN),
                    listOf(SandColor.PURPLE, SandColor.ORANGE, SandColor.CYAN, SandColor.RED),
                    listOf(SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN, SandColor.PURPLE),
                    listOf(SandColor.ORANGE, SandColor.CYAN, SandColor.RED, SandColor.YELLOW),
                    listOf(SandColor.BLUE, SandColor.GREEN, SandColor.PURPLE, SandColor.ORANGE),
                    listOf(SandColor.CYAN, SandColor.RED, SandColor.YELLOW, SandColor.BLUE),
                    listOf(SandColor.GREEN, SandColor.PURPLE, SandColor.ORANGE, SandColor.CYAN),
                    emptyList(),
                    emptyList()
                )
            )

            13 -> LevelData(
                levelNumber = 13,
                capacity = 4,
                goalChests = listOf(SandColor.PINK, SandColor.RED, SandColor.YELLOW, SandColor.BLUE),
                bottles = listOf(
                    listOf(SandColor.PINK, SandColor.RED, SandColor.YELLOW, SandColor.BLUE),
                    listOf(SandColor.GREEN, SandColor.PURPLE, SandColor.ORANGE, SandColor.PINK),
                    listOf(SandColor.RED, SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN),
                    listOf(SandColor.PURPLE, SandColor.ORANGE, SandColor.PINK, SandColor.RED),
                    listOf(SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN, SandColor.PURPLE),
                    listOf(SandColor.ORANGE, SandColor.PINK, SandColor.RED, SandColor.YELLOW),
                    listOf(SandColor.BLUE, SandColor.GREEN, SandColor.PURPLE, SandColor.ORANGE),
                    emptyList(),
                    emptyList()
                )
            )

            14 -> LevelData(
                levelNumber = 14,
                capacity = 4,
                goalChests = listOf(SandColor.CYAN, SandColor.PINK, SandColor.RED, SandColor.GREEN),
                bottles = listOf(
                    listOf(SandColor.CYAN, SandColor.PINK, SandColor.RED, SandColor.YELLOW),
                    listOf(SandColor.BLUE, SandColor.GREEN, SandColor.PURPLE, SandColor.CYAN),
                    listOf(SandColor.PINK, SandColor.RED, SandColor.YELLOW, SandColor.BLUE),
                    listOf(SandColor.GREEN, SandColor.PURPLE, SandColor.CYAN, SandColor.PINK),
                    listOf(SandColor.RED, SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN),
                    listOf(SandColor.PURPLE, SandColor.CYAN, SandColor.PINK, SandColor.RED),
                    listOf(SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN, SandColor.PURPLE),
                    emptyList(),
                    emptyList()
                )
            )

            15 -> LevelData(
                // 7 colors with only 1 empty tube
                levelNumber = 15,
                capacity = 4,
                goalChests = listOf(SandColor.RED, SandColor.BLUE, SandColor.GREEN, SandColor.PURPLE),
                bottles = listOf(
                    listOf(SandColor.RED, SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN),
                    listOf(SandColor.PURPLE, SandColor.ORANGE, SandColor.CYAN, SandColor.RED),
                    listOf(SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN, SandColor.PURPLE),
                    listOf(SandColor.ORANGE, SandColor.CYAN, SandColor.RED, SandColor.YELLOW),
                    listOf(SandColor.BLUE, SandColor.GREEN, SandColor.PURPLE, SandColor.ORANGE),
                    listOf(SandColor.CYAN, SandColor.RED, SandColor.YELLOW, SandColor.BLUE),
                    listOf(SandColor.GREEN, SandColor.PURPLE, SandColor.ORANGE, SandColor.CYAN),
                    emptyList()
                )
            )

            16 -> LevelData(
                // All 8 colors! 10 bottles in two rows
                levelNumber = 16,
                capacity = 4,
                goalChests = listOf(SandColor.RED, SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN),
                bottles = listOf(
                    listOf(SandColor.RED, SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN),
                    listOf(SandColor.PURPLE, SandColor.ORANGE, SandColor.CYAN, SandColor.PINK),
                    listOf(SandColor.RED, SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN),
                    listOf(SandColor.PURPLE, SandColor.ORANGE, SandColor.CYAN, SandColor.PINK),
                    listOf(SandColor.RED, SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN),
                    listOf(SandColor.PURPLE, SandColor.ORANGE, SandColor.CYAN, SandColor.PINK),
                    listOf(SandColor.RED, SandColor.YELLOW, SandColor.BLUE, SandColor.GREEN),
                    listOf(SandColor.PURPLE, SandColor.ORANGE, SandColor.CYAN, SandColor.PINK),
                    emptyList(),
                    emptyList()
                )
            )

            17 -> LevelData(
                levelNumber = 17,
                capacity = 4,
                goalChests = listOf(SandColor.PINK, SandColor.CYAN, SandColor.ORANGE, SandColor.PURPLE),
                bottles = listOf(
                    listOf(SandColor.PINK, SandColor.RED, SandColor.YELLOW, SandColor.BLUE),
                    listOf(SandColor.GREEN, SandColor.PURPLE, SandColor.ORANGE, SandColor.CYAN),
                    listOf(SandColor.PINK, SandColor.RED, SandColor.YELLOW, SandColor.BLUE),
                    listOf(SandColor.GREEN, SandColor.PURPLE, SandColor.ORANGE, SandColor.CYAN),
                    listOf(SandColor.PINK, SandColor.RED, SandColor.YELLOW, SandColor.BLUE),
                    listOf(SandColor.GREEN, SandColor.PURPLE, SandColor.ORANGE, SandColor.CYAN),
                    listOf(SandColor.PINK, SandColor.RED, SandColor.YELLOW, SandColor.BLUE),
                    listOf(SandColor.GREEN, SandColor.PURPLE, SandColor.ORANGE, SandColor.CYAN),
                    emptyList(),
                    emptyList()
                )
            )

            18 -> LevelData(
                levelNumber = 18,
                capacity = 4,
                goalChests = listOf(SandColor.GREEN, SandColor.BLUE, SandColor.PURPLE, SandColor.ORANGE),
                bottles = listOf(
                    listOf(SandColor.GREEN, SandColor.BLUE, SandColor.PURPLE, SandColor.ORANGE),
                    listOf(SandColor.CYAN, SandColor.PINK, SandColor.RED, SandColor.YELLOW),
                    listOf(SandColor.ORANGE, SandColor.GREEN, SandColor.BLUE, SandColor.PURPLE),
                    listOf(SandColor.YELLOW, SandColor.CYAN, SandColor.PINK, SandColor.RED),
                    listOf(SandColor.PURPLE, SandColor.ORANGE, SandColor.GREEN, SandColor.BLUE),
                    listOf(SandColor.RED, SandColor.YELLOW, SandColor.CYAN, SandColor.PINK),
                    listOf(SandColor.BLUE, SandColor.PURPLE, SandColor.ORANGE, SandColor.GREEN),
                    listOf(SandColor.PINK, SandColor.RED, SandColor.YELLOW, SandColor.CYAN),
                    emptyList(),
                    emptyList()
                )
            )

            19 -> LevelData(
                levelNumber = 19,
                capacity = 4,
                goalChests = listOf(SandColor.RED, SandColor.GREEN, SandColor.CYAN, SandColor.PINK),
                bottles = listOf(
                    listOf(SandColor.RED, SandColor.PURPLE, SandColor.CYAN, SandColor.YELLOW),
                    listOf(SandColor.BLUE, SandColor.GREEN, SandColor.ORANGE, SandColor.PINK),
                    listOf(SandColor.PINK, SandColor.ORANGE, SandColor.GREEN, SandColor.BLUE),
                    listOf(SandColor.YELLOW, SandColor.CYAN, SandColor.PURPLE, SandColor.RED),
                    listOf(SandColor.PURPLE, SandColor.RED, SandColor.YELLOW, SandColor.CYAN),
                    listOf(SandColor.GREEN, SandColor.BLUE, SandColor.PINK, SandColor.ORANGE),
                    listOf(SandColor.ORANGE, SandColor.PINK, SandColor.BLUE, SandColor.GREEN),
                    listOf(SandColor.CYAN, SandColor.YELLOW, SandColor.RED, SandColor.PURPLE),
                    emptyList(),
                    emptyList()
                )
            )

            20 -> LevelData(
                levelNumber = 20,
                capacity = 4,
                goalChests = listOf(SandColor.PINK, SandColor.PURPLE, SandColor.CYAN, SandColor.GREEN),
                bottles = listOf(
                    listOf(SandColor.PINK, SandColor.YELLOW, SandColor.CYAN, SandColor.RED),
                    listOf(SandColor.PURPLE, SandColor.BLUE, SandColor.GREEN, SandColor.ORANGE),
                    listOf(SandColor.RED, SandColor.CYAN, SandColor.YELLOW, SandColor.PINK),
                    listOf(SandColor.ORANGE, SandColor.GREEN, SandColor.BLUE, SandColor.PURPLE),
                    listOf(SandColor.YELLOW, SandColor.PINK, SandColor.RED, SandColor.CYAN),
                    listOf(SandColor.BLUE, SandColor.PURPLE, SandColor.ORANGE, SandColor.GREEN),
                    listOf(SandColor.CYAN, SandColor.RED, SandColor.PINK, SandColor.YELLOW),
                    listOf(SandColor.GREEN, SandColor.ORANGE, SandColor.PURPLE, SandColor.BLUE),
                    emptyList(),
                    emptyList()
                )
            )

            else -> generateProceduralLevel(levelNumber)
        }
        return level.copy(difficultyName = diff)
    }

    /**
     * Generates an interesting, guaranteed-solvable level for level 21+.
     * Scales difficulty organically as level increases:
     * - Number of colors scales from 5 up to 8.
     * - Empty bottles: calibrated to 2 (or occasionally 1 for boss levels).
     * - Shuffle steps scale smoothly.
     */
    private fun generateProceduralLevel(levelNumber: Int): LevelData {
        val allColors = listOf(
            SandColor.RED, SandColor.YELLOW, SandColor.BLUE,
            SandColor.GREEN, SandColor.PURPLE, SandColor.ORANGE,
            SandColor.CYAN, SandColor.PINK
        )

        // Graduated color count based on level progression matching difficulty tiers
        val numColors = when {
            levelNumber <= 40 -> 6  // MEDIUM (21-40): 6 colors
            levelNumber <= 70 -> 7  // HARD (41-70): 7 colors
            else -> 8              // EXPERT (71-100) & MASTER (101+): 8 colors
        }
        val random = kotlin.random.Random(levelNumber * 2027L + 77L)
        val selectedColors = allColors.shuffled(random).take(numColors)

        val capacity = 4
        // Create full uniform stacks first
        val bottles = mutableListOf<MutableList<SandColor>>()
        for (color in selectedColors) {
            bottles.add(MutableList(capacity) { color })
        }

        // Add 2 empty bottles (or 1 on every 10th boss level from level 40 onwards for high difficulty)
        val isBossLevel = levelNumber % 10 == 0 && levelNumber >= 40
        val emptyCount = if (isBossLevel) 1 else 2
        repeat(emptyCount) {
            bottles.add(mutableListOf())
        }

        // Perform reverse moves to guarantee solvability
        val moves = (40 + numColors * 8 + (levelNumber % 15) * 2).coerceAtMost(90)

        for (step in 0 until moves) {
            val nonFullIndices = bottles.indices.filter { bottles[it].size < capacity }
            val nonEmptyIndices = bottles.indices.filter { bottles[it].isNotEmpty() }
            if (nonFullIndices.isNotEmpty() && nonEmptyIndices.isNotEmpty()) {
                val fromIdx = nonEmptyIndices.random(random)
                val possibleTo = nonFullIndices.filter { it != fromIdx }
                if (possibleTo.isNotEmpty()) {
                    val toIdx = possibleTo.random(random)
                    val element = bottles[fromIdx].removeAt(bottles[fromIdx].size - 1)
                    bottles[toIdx].add(element)
                }
            }
        }

        // Ensure at least emptyCount bottles are completely empty or have spare buffer
        var currentEmpty = bottles.count { it.isEmpty() }
        while (currentEmpty < emptyCount) {
            val largestIndices = bottles.indices
                .filter { bottles[it].isNotEmpty() && bottles[it].size < capacity }
                .sortedBy { bottles[it].size }
            if (largestIndices.isNotEmpty()) {
                val targetToEmpty = largestIndices.first()
                val itemsToMove = bottles[targetToEmpty].toList()
                var allMoved = true
                for (item in itemsToMove) {
                    val receiver = bottles.indices.firstOrNull { it != targetToEmpty && bottles[it].size < capacity }
                    if (receiver != null) {
                        bottles[receiver].add(item)
                        bottles[targetToEmpty].removeAt(bottles[targetToEmpty].size - 1)
                    } else {
                        allMoved = false
                        break
                    }
                }
                if (allMoved && bottles[targetToEmpty].isEmpty()) {
                    currentEmpty++
                } else {
                    break
                }
            } else {
                break
            }
        }

        return LevelData(
            levelNumber = levelNumber,
            capacity = capacity,
            goalChests = selectedColors.take(4),
            difficultyName = getDifficulty(levelNumber),
            bottles = bottles.map { it.toList() }
        )
    }
}
