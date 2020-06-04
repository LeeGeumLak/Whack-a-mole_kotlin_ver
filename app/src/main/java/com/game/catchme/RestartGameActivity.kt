package com.game.catchme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.game.catchme.PlayGameActivity

class RestartGameActivity : AppCompatActivity() {
    private var highScoreValue = 0
    // 게임 재시작 액티비티 초기화
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restart_game)
        // 받아야할 데이터(점수, 최고 점수)를 위한 intent
        val passedIntent = intent
        // 인텐트에 있는 데이터 받아옴
        val scoreValue = passedIntent.getIntExtra(scoreIntentData, 0)
        highScoreValue = passedIntent.getIntExtra(highScoreIntentData, 0)
        // 받아온 데이터를 UI에 반영
        val scoreText = findViewById<View>(R.id.lastScore) as TextView
        val highScore = findViewById<View>(R.id.highScore) as TextView
        val scoreString = resources.getString(R.string.score) + " " + scoreValue
        scoreText.text = scoreString
        val highScoreString = "최고 점수 : $highScoreValue"
        highScore.text = highScoreString
    }

    // 재시작 버튼 클릭시, 게임 플레이 액티비티로 이동 (최고점수를 intent 로 넘김)
    fun onClick(v: View?) {
        val switchIntent = Intent(this, PlayGameActivity::class.java)
        switchIntent.putExtra(highScoreIntentData, highScoreValue)
        startActivity(switchIntent)
    }

    companion object {
        private const val scoreIntentData = "점수"
        private const val highScoreIntentData = "최고 점수"
    }
}