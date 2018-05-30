package com.anwesh.uiprojects.kotlinlinecirclerotatorview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.linecirclerotatorview.LineCircleRotatorView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LineCircleRotatorView.create(this)
    }
}
