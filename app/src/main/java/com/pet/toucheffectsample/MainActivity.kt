package com.pet.toucheffectsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.pet.toucheffectsample.ui.theme.TouchEffectSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TouchEffectSampleTheme {
                Greeting(name = "Android")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val touchedPoint by remember { mutableStateOf(mutableListOf<Offset>()) }
    var visible by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.tap_animation))
    Box(modifier = Modifier.fillMaxSize()) {
        val sizeAnimationDuration = 100
        val boxSize = 50.dp
        animateDpAsState(
            if (visible) boxSize else 0.dp,
            tween(
                durationMillis = sizeAnimationDuration,
                easing = LinearEasing
            ), label = ""
        )

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        while (true) {
                            val initialEvent = this.awaitPointerEvent(PointerEventPass.Main)
                            if (initialEvent.type == PointerEventType.Press) {
                                initialEvent.changes.forEach {
                                    touchedPoint.add(it.position)
                                    visible = true
                                    it.consume()
                                }
                            } else {
                                visible = false
                            }
                        }

                    }
                }
        ) { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                Text(
                    text = "Hello $name!",
                    modifier = modifier
                )

                touchedPoint.map {
                    // The touch offset is px and we need to convert to Dp
                    val density = LocalDensity.current
                    val (xDp, yDp) = with(density) {
                        (it.x.toDp() - boxSize / 2) to (it.y.toDp() - boxSize / 2)
                    }
                    // This box serves as container. It has a fixed size.
                    LottieAnimation(
                        modifier = Modifier
                            .offset(xDp, yDp)
                            .size(boxSize),
                        composition = composition,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TouchEffectSampleTheme {
        Greeting("Android")
    }
}