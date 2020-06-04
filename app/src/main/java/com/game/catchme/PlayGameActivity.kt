package com.game.catchme

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.game.catchme.PlayGameActivity
import java.util.*
import java.util.concurrent.TimeUnit

class PlayGameActivity : AppCompatActivity() {
    private var overallScore = 0 // 획득 점수
    private val moleButtons = arrayOfNulls<ImageButton>(numOfMoles)
    private val holeButtons = arrayOfNulls<ImageButton>(numOfMoles)
    private val buttonStatus = BooleanArray(numOfMoles)
    private val buttonTimers = arrayOfNulls<CountDownTimer>(numOfMoles)

    // 각 새 스레드 시작과 게임의 초기 상태를 생성
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)

        // 플레이한 게임 중 얻은 최고 점수 intent data
        val passedIntent = intent
        val highScoreValue = passedIntent.getIntExtra(highScoreIntentData, 0)

        // 게임 타이머
        object : CountDownTimer(fullPlayTime.toLong(), playTimeInterval.toLong()) {
            var timeText = findViewById<View>(R.id.countdownTimer) as TextView

            override fun onTick(timeLeftMillis: Long) { // 1초마다 증가하는 현재 시간 UI에 적용
                var timeTextString = LeftTimeString + TimeUnit.MILLISECONDS.toMinutes(timeLeftMillis) + ":" + TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis)
                if (TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis) <= 9) {
                    timeTextString = LeftTimeString + TimeUnit.MILLISECONDS.toMinutes(timeLeftMillis) + ":0" + TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis)
                }
                timeText.text = timeTextString
            }

            override fun onFinish() { // 타이머가 끝나면 최종점수를 putExtra하고 restartgame 액티비티로 이동

                val restartGameIntent = Intent(this@PlayGameActivity, RestartGameActivity::class.java)
                restartGameIntent.putExtra(scoreIntentData, overallScore)

                // 최고 점수 계산
                if (highScoreValue > overallScore) {
                    restartGameIntent.putExtra(highScoreIntentData, highScoreValue)
                } else {
                    restartGameIntent.putExtra(highScoreIntentData, overallScore)
                }
                startActivity(restartGameIntent)
            }
        }.start()
        updateScoreText()
        enableButtonsForGame()
        gameLogic()
    }

    // 구멍과 두더지 이미지 초기화 및 점수계산
    fun enableButtonsForGame() { //Populating each array of buttons for access elsewhere
        holeButtons[0] = findViewById<View>(R.id.hole1) as ImageButton
        holeButtons[1] = findViewById<View>(R.id.hole2) as ImageButton
        holeButtons[2] = findViewById<View>(R.id.hole3) as ImageButton
        holeButtons[3] = findViewById<View>(R.id.hole4) as ImageButton
        holeButtons[4] = findViewById<View>(R.id.hole5) as ImageButton
        holeButtons[5] = findViewById<View>(R.id.hole6) as ImageButton
        holeButtons[6] = findViewById<View>(R.id.hole7) as ImageButton
        holeButtons[7] = findViewById<View>(R.id.hole8) as ImageButton
        holeButtons[8] = findViewById<View>(R.id.hole9) as ImageButton
        moleButtons[0] = findViewById<View>(R.id.moleButton1) as ImageButton
        moleButtons[1] = findViewById<View>(R.id.moleButton2) as ImageButton
        moleButtons[2] = findViewById<View>(R.id.moleButton3) as ImageButton
        moleButtons[3] = findViewById<View>(R.id.moleButton4) as ImageButton
        moleButtons[4] = findViewById<View>(R.id.moleButton5) as ImageButton
        moleButtons[5] = findViewById<View>(R.id.moleButton6) as ImageButton
        moleButtons[6] = findViewById<View>(R.id.moleButton7) as ImageButton
        moleButtons[7] = findViewById<View>(R.id.moleButton8) as ImageButton
        moleButtons[8] = findViewById<View>(R.id.moleButton9) as ImageButton
        // 구멍 이미지 클릭할 때, 점수 계산(감소)
        for (index in 0 until numOfMoles) {
            onClickSubtractScore(holeButtons[index])
        }
        // 두더지 이미지 클릭할 때, 점수 계산(증가)
        for (index in 0 until numOfMoles) {
            onClickAddScore(moleButtons[index], index)
        }
    }

    // 구멍 이미지 클릭할 때, 점수 계산(감소) 메서드
    fun onClickSubtractScore(buttonToListen: ImageButton?) {
        buttonToListen!!.setOnClickListener {
            if (overallScore > 0) {
                overallScore--
            } else {
                overallScore = 0
            }
            updateScoreText()
        }
    }

    // 두더지 이미지 클릭할 때, 점수 계산(증가) 메서드
    fun onClickAddScore(buttonToListen: ImageButton?, buttonNumber: Int) {
        buttonToListen!!.setOnClickListener {
            overallScore++
            updateScoreText()
            // 두더지 이미지 클릭시, 두더지의 등장시간 타이머 취소 및 이미지 disable
            buttonTimers[buttonNumber]!!.cancel()
            disableMoleButton(buttonNumber)
            // 랜덤으로 두더지 이미지 enable ( disable 된 이미지 중에서 )
            val randomNumGenerator = Random()
            val timerStartTime = randomNumGenerator.nextInt(upperTimerLimit) + lowerTimerLimit
            object : CountDownTimer(timerStartTime.toLong(), playTimeInterval.toLong()) {
                override fun onTick(l: Long) {}
                override fun onFinish() {
                    restartButton(buttonNumber)
                }
            }.start()
        }
    }

    // 획득 점수가 바뀌면 UI update 하기 위한 메서드
    fun updateScoreText() {
        val scoreText = findViewById<View>(R.id.scoreCounter) as TextView
        scoreText.text = scoreString + overallScore
    }

    // true/false를 랜덤으로 얻기위한 메서드
    fun getRandomTrueFalse(upperBound: Int): Boolean {
        val randNumGenerator = Random()
        val randomStatus = randNumGenerator.nextInt(upperBound)
        return randomStatus == 1
    }

    // 게임의 로직(9개의 두더지 이미지)을 세팅하기 위한 메서드
    fun gameLogic() {
        for (index in 0 until numOfMoles) {
            restartButton(index)
        }
    }

    // 인자로 받은 index의 두더지 이미지를 등장시킬지 말지를 결정하는 메서드
    fun restartButton(buttonNumber: Int) {
        val getRandomNum = Random()
        val chanceTrue = 2
        buttonStatus[buttonNumber] = getRandomTrueFalse(chanceTrue)
        val timerStartTime = getRandomNum.nextInt(upperTimerLimit) + lowerTimerLimit
        buttonTimers[buttonNumber] = object : CountDownTimer(timerStartTime.toLong(), playTimeInterval.toLong()) {
            override fun onTick(l: Long) {}
            override fun onFinish() {
                restartButton(buttonNumber)
            }
        }.start()
        // 설정한 버튼 상태에 따라서 두더지 이미지 enable/disable 함
        if (buttonStatus[buttonNumber]) {
            enableMoleButton(buttonNumber)
        } else {
            disableMoleButton(buttonNumber)
        }
    }

    // 인자로 받은 숫자에 맞는 두더지 이미지 disable
    fun disableMoleButton(buttonNumber: Int) {
        moleButtons[buttonNumber]!!.isEnabled = false
        moleButtons[buttonNumber]!!.alpha = 0f
        holeButtons[buttonNumber]!!.bringToFront()
    }

    // 인자로 받은 숫자에 맞는 두더지 이미지 enable
    fun enableMoleButton(buttonNumber: Int) {
        moleButtons[buttonNumber]!!.isEnabled = true
        moleButtons[buttonNumber]!!.alpha = 1f
        moleButtons[buttonNumber]!!.bringToFront()
    }

    companion object {
        private const val fullPlayTime = 30000 // 게임 플레이시간
        private const val playTimeInterval = 1000 // 플레이시간 인터벌
        private const val numOfMoles = 9 // 나오는 두더지의 최대 개수
        private const val upperTimerLimit = 500
        private const val lowerTimerLimit = 500
        private const val LeftTimeString = "남은 시간 : "
        private const val scoreString = "점수 : "
        private const val scoreIntentData = "점수"
        private const val highScoreIntentData = "최고 점수"
    }
}