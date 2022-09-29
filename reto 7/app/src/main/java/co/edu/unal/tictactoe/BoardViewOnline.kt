package co.edu.unal.tictactoe

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class BoardViewOnline : View {
    // Width of the board grid lines
    private val GRID_WIDTH = 30

    //BitMap for images
    private lateinit var mHumanBitmap: Bitmap
    private lateinit var mComputerBitmap: Bitmap

    private var mPaint: Paint? = null

    private var mGame: TicTacToeGameOnline = TicTacToeGameOnline()

    private var winner: Boolean = false

    private lateinit var wPos: IntArray


    private fun initialize() {
        mHumanBitmap = BitmapFactory.decodeResource(resources, R.drawable.x_symbol_m)
        mComputerBitmap = BitmapFactory.decodeResource(resources, R.drawable.o_symbol_m)

        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        wPos = intArrayOf(-1, -1, -1)


    }

    constructor (context: Context?) : super(context) {
        initialize()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initialize()
    }

    constructor (context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize()
    }

    fun setGame(game: TicTacToeGameOnline) {
        mGame = game
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)
        // Determine the width and height of the View
        val boardWidth = width
        val boardHeight = height
        // Make thick, light gray lines
        mPaint!!.color = Color.WHITE
        mPaint!!.strokeWidth = GRID_WIDTH.toFloat()
        // Draw the two vertical board lines
        val cellWidth = boardWidth / 3
        canvas.drawLine(
            cellWidth.toFloat(), 0.0F, cellWidth.toFloat(),
            boardHeight.toFloat(), mPaint!!
        )
        canvas.drawLine(
            (cellWidth * 2).toFloat(), 0.0F,
            (cellWidth * 2).toFloat(), boardHeight.toFloat(), mPaint!!
        )

        // Draw the two horizontal board lines
        val cellHeight = boardHeight / 3
        canvas.drawLine(
            0.0F, cellHeight.toFloat(), boardWidth.toFloat(),
            cellHeight.toFloat(), mPaint!!
        )
        canvas.drawLine(
            0.0F, (cellHeight * 2).toFloat(), boardWidth.toFloat(),
            (cellHeight * 2).toFloat(), mPaint!!
        )

        // Draw all the X and O images
        for (i in 0 until TicTacToeGameOnline.BOARD_SIZE) {
            val col = i % 3
            val row = i / 3
            // Define the boundaries of a destination rectangle for the image
            val left = cellWidth * col
            val top = cellHeight * row
            val right = left + cellWidth
            val bottom = top + cellHeight

            if (mGame.getBoardOccupant(i) == TicTacToeGameOnline.PLAYER_HOST) {
                if(winner && wPos.contains(i)){
                    mPaint!!.colorFilter =
                        PorterDuffColorFilter(Color.argb(255, 206, 147, 216), PorterDuff.Mode.SRC_IN)
                }
                canvas.drawBitmap(mHumanBitmap, null, Rect(left, top, right, bottom), mPaint)

            } else if (mGame.getBoardOccupant(i) == TicTacToeGameOnline.PLAYER_CLIENT) {
                if(winner && wPos.contains(i)){
                    mPaint!!.colorFilter =
                        PorterDuffColorFilter(Color.argb(255, 206, 147, 216), PorterDuff.Mode.SRC_IN)
                }
                canvas.drawBitmap(mComputerBitmap, null, Rect(left, top, right, bottom),mPaint)
            }
            mPaint!!.colorFilter = null

        }
    }

    fun setWinner(winner: Boolean) {
        this.winner = winner
    }

    fun setWPos(wPos: IntArray) {
        for(i in 1 until 4){
            this.wPos[i-1] = wPos[i]
        }
    }

    fun getBoardCellWidth(): Int {
        return width / 3
    }

    fun getBoardCellHeight(): Int {
        return height / 3
    }

}