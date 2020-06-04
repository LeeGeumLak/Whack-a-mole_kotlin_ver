package com.game.catchme

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // start 버튼 클릭시 playgame 액티비티로 이동 (인텐트로)
    fun onStartClick(v: View?) {
        val playGameIntent = Intent(this, PlayGameActivity::class.java)
        startActivity(playGameIntent)
    }
}