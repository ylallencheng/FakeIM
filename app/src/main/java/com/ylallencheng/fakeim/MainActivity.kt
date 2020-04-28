package com.ylallencheng.fakeim

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ylallencheng.fakeim.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
