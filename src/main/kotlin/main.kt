package connectfour

class ConnectFour {
    private val title = "Connect Four"
    private lateinit var player1: String
    private lateinit var player2: String

    private val defaultSymbol = " "
    private val player1Symbol = "o"
    private val player2Symbol = "*"
    private var currSymbol = ""

    // even -> player 1, odd - player 2
    private var currPlayer = 0

    private var rows = 0
    private var cols = 0

    private var totalGamesCount = 1
    private var currGameNum = 1
    private var endCurrGame = false

    private val winPoints = 2
    private val drawPoints = 1
    private var player1Score = 0
    private var player2Score = 0
    private var winner = ""
    private var endGame = false

    private lateinit var board: MutableList<MutableList<String>>

    private fun printTitle() {
        println(this.title)
    }

    private fun setPlayers() {
        println("First player's name:")
        var name = readln()
        this.player1 = if (name == "") "player1" else name

        println("Second player's name:")
        name = readln()
        this.player2 = if (name == "") "player2" else name
    }

    private fun setDefaultSize() {
        this.rows = 6
        this.cols = 7
    }

    private fun setSize() {
        val validRange = 5..9
        val validInput = Regex("^\\s*\\d+\\s*[x|X]\\s*\\d+\\s*$")
        val defaultInput = ""
        var isInputCorrect = false

        while (!isInputCorrect) {
            println("Set the board dimensions (Rows x Columns)")
            println("Press Enter for default (6 x 7)")
            val input = readln()

            try {
                when {
                    validInput.matches(input) -> {
                        val (rows, cols) = input.replace("\\s*".toRegex(), "")
                            .replace("x", " ").replace("X", " ")
                            .split(" ").map { it.toInt() }
                        if (rows !in validRange) {
                            throw Exception("Board rows should be from 5 to 9")
                        } else if (cols !in validRange) {
                            throw Exception("Board columns should be from 5 to 9")
                        } else {
                            this.rows = rows
                            this.cols = cols
                            isInputCorrect = true
                        }
                    }

                    input == defaultInput -> {
                        setDefaultSize()
                        isInputCorrect = true
                    }

                    else -> {
                        throw Exception("Invalid input")
                    }
                }
            } catch (e: Exception) {
                println(e.message)
            }

        }
    }

    private fun setBoard() {
        this.board = MutableList(rows) { MutableList(cols) { defaultSymbol } }
    }

    private fun setGamesCount() {
        var isSet = false
        while (!isSet) {
            println("Do you want to play single or multiple games?")
            println("For a single game, input 1 or press Enter")
            println("Input a number of games:")
            try {
                val input = readln()
                if (input.isBlank()) return
                when {
                    input.toInt() > 0 -> {
                        this.totalGamesCount = input.toInt()
                        isSet = true
                    }

                    else -> throw Exception()
                }

            } catch (e: Exception) {
                println("Invalid input")
            }
        }
    }

    private fun printSets() {
        println("$player1 VS $player2")
        println("$rows X $cols board")
        println(
            if (totalGamesCount == 1) {
                "Single game"
            } else {
                "Total $totalGamesCount games"
            }
        )
    }

    private fun printBoard() {
        val borders = listOf("║", "═", "╚", "╝", "╩")

        // print numbers of columns
        for (i in 1..cols) {
            print(" $i")
        }
        print(" \n")

        // print fields according to board size presets "r" -> rows, "c" -> columns
        for (r in board) {
            for (c in r) {
                print("${borders[0]}$c")
            }
            print("${borders[0]}\n")
        }

        // print decorative bottom of the board
        print(borders[2])
        print("${borders[1]}${borders[4]}".repeat(cols - 1))
        print(borders[1])
        println(borders[3])

    }

    private fun isAnyFreeCell(): Boolean {
        // iterate through each cell on most top layer to find first empty cells
        for (c in board[0]) {
            if (c == defaultSymbol) return true
        }
        return false
    }

    private fun isColHaveFreeCell(col: Int): Boolean {
        // iterate throw all rows to find first empty cell in column
        for (row in board) {
            if (row[col] == defaultSymbol) return true
        }
        return false
    }

    private fun putSymbolOnBoard(col: Int, symbol: String) {
        for (i in board.lastIndex downTo 0) {
            if (board[i][col] == defaultSymbol) {
                board[i][col] = symbol
                return
            } else {
                continue
            }
        }
        return
    }

    private fun getPlayerInput(player: String) {
        var isSuccess = false
        while (!isSuccess) {
            try {
                println("$player's turn:")
                // first step checking if input finishing the game
                when (val rawInput = readln()) {
                    "end" -> {
                        finishOneGame()
                        endGame()
                        isSuccess = true
                    }

                    else -> {
                        // second step checking if input in board range
                        val input = rawInput.toInt()
                        val currCol = input - 1
                        when (input in 1..cols) {
                            true -> {
                                // third step checking if column have any free cell
                                when (isColHaveFreeCell(currCol)) {
                                    true -> {
                                        putSymbolOnBoard(currCol, currSymbol)
                                        isSuccess = true
                                    }

                                    false -> throw Exception("Column $input is full")
                                }

                            }

                            false -> throw Exception("The column number is out of range (1 - $cols)")
                        }
                    }
                }
            } catch (e: NumberFormatException) {
                println("Incorrect column number")
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    private fun playerTurn() {
        if (currPlayer % 2 == 0) {
            this.currSymbol = player1Symbol
            getPlayerInput(player1)
        } else {
            this.currSymbol = player2Symbol
            getPlayerInput(player2)
        }
    }

    private fun checkRowWinCondition(symbol: String): Boolean {
        for (r in board.lastIndex downTo 0) {
            for (c in board[r].indices) {
                try {
                    if (
                        board[r][c] == symbol &&
                        board[r][c + 1] == symbol &&
                        board[r][c + 2] == symbol &&
                        board[r][c + 3] == symbol
                    ) {
                        return true
                    }
                } catch (_: Exception) {
                }
            }
        }
        return false
    }

    private fun checkColWinCondition(symbol: String): Boolean {
        for (r in board.lastIndex downTo 0) {
            for (c in board[r].indices) {
                try {
                    if (
                        board[r][c] == symbol &&
                        board[r - 1][c] == symbol &&
                        board[r - 2][c] == symbol &&
                        board[r - 3][c] == symbol
                    ) {
                        return true
                    }
                } catch (_: Exception) {
                }
            }
        }
        return false
    }

    private fun checkDiagonalWinCondition(symbol: String): Boolean {
        // r -> rows, c -> cols
//        for (r in board.lastIndex downTo 0) {
//            for (c in board[r].indices) {
        for (r in board.lastIndex downTo 0) {
            for (c in board[r].indices) {
                try {
                    if (
                        board[r][c] == symbol &&
                        board[r - 1][c + 1] == symbol &&
                        board[r - 2][c + 2] == symbol &&
                        board[r - 3][c + 3] == symbol
                    ) {
                        return true
                    }
                } catch (_: Exception) {
                }
                try {
                    if (
                        board[r][c] == symbol &&
                        board[r - 1][c - 1] == symbol &&
                        board[r - 2][c - 2] == symbol &&
                        board[r - 3][c - 3] == symbol
                    ) {
                        return true
                    }
                } catch (_: Exception) {
                }
            }
        }
//            }
//        }
        return false
    }

    private fun checkIfWin(): Boolean {
        return if (
            checkRowWinCondition(currSymbol) ||
            checkColWinCondition(currSymbol) ||
            checkDiagonalWinCondition(currSymbol)
        ) {
            if (currPlayer % 2 == 0) {
                this.player1Score += winPoints
                this.winner = this.player1
            } else {
                this.player2Score += winPoints
                this.winner = this.player2
            }
            true
        } else {
            false
        }
    }

    private fun checkIfDraw(): Boolean {
        if (!isAnyFreeCell()) {
            this.player1Score += drawPoints
            this.player2Score += drawPoints
            return true
        }
        return false
    }

    private fun printWin() {
        println("Player $winner won")
    }

    private fun printScore() {
        println("Score")
        println("$player1: $player1Score $player2: $player2Score")
    }

    private fun finishOneGame() {
        this.endCurrGame = true
    }

    private fun continuePlay() {
        playerTurn()
        if (!this.endGame || !this.endCurrGame) printBoard()
        if (checkIfDraw() || checkIfWin()) finishOneGame()

        currPlayer++
    }

    fun setGame() {
        printTitle()
        setPlayers()
        setSize()
        setBoard()
        setGamesCount()
        printSets()
    }

    fun startGame() {
        while (!this.endGame) {
            this.endCurrGame = false
            if (totalGamesCount > 1) {
                println("Game #$currGameNum")
            }

            printBoard()
            while (!this.endCurrGame) {
                continuePlay()
            }

            if (this.winner.isNotBlank()) {
                printWin()
            } else {
                if (!isAnyFreeCell()) println("It is a draw")
            }

            if (totalGamesCount > 1) printScore()

            currGameNum++
            if (currGameNum > totalGamesCount) endGame()
            setBoard()
            this.winner = ""
        }

        println("Game Over!")
    }

    private fun endGame() {
        this.endGame = true
    }
}

fun main() {
    val connectFour = ConnectFour()

    connectFour.setGame()
    connectFour.startGame()
}