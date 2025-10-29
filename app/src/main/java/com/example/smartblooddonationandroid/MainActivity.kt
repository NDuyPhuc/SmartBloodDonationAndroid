//D:\SmartBloodDonationAndroid\app\src\main\java\com\example\smartblooddonationandroid\MainActivity.kt
package com.example.smartblooddonationandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartblooddonationandroid.ui.theme.SmartBloodDonationAndroidTheme
import com.smartblood.donation.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import com.smartblood.core.ui.theme.SmartBloodTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartBloodDonationAndroidTheme {
                AppNavHost()
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartBloodDonationAndroidTheme {
        Greeting("Android")
    }
}