package com.ucsc.conectaucsc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ucsc.conectaucsc.ui.navigation.Navigation
import com.ucsc.conectaucsc.ui.theme.ConectaUCSCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConectaUCSCTheme {
                Navigation()
            }
        }
    }
}