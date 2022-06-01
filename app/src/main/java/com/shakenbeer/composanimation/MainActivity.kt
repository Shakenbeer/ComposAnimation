@file:Suppress("TransitionPropertiesLabel", "UpdateTransitionLabel")

package com.shakenbeer.composanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.Magenta
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shakenbeer.composanimation.Chapter.*
import com.shakenbeer.composanimation.ui.theme.ComposAnimationTheme

enum class Chapter {
    Content, AnimateAsState, UpdateTransition, AnimateVisibility, AnimateContentSize, Crossfade
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposAnimationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Animations()
                }
            }
        }
    }
}

@Composable
fun Animations() {
    var chapter by rememberSaveable { mutableStateOf(Content) }

    Column {
        if (chapter != Content) {
            TextButton(onClick = { chapter = Content }) {
                Text(text = "Back")
            }
        }
        when (chapter) {
            Content -> Column(modifier = Modifier.padding(24.dp)) {
                Chapter.values().sliceArray(1 until Chapter.values().size)
                    .forEachIndexed { index, ch ->
                        Text(
                            text = "${index + 1}. ${ch.name}",
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .clickable {
                                    chapter = ch
                                })
                    }
            }
            AnimateAsState -> AnimateAsStateDemo()
            UpdateTransition -> UpdateTransitionDemo()
            AnimateVisibility -> AnimateVisibilityDemo()
            AnimateContentSize -> AnimateContentSizeDemo()
            Crossfade -> CrossfadeDemo()
        }
    }
}

@Composable
fun AnimateAsStateDemo() {
    var blue by remember { mutableStateOf(true) }
    val color by animateColorAsState(if (blue) Blue else Magenta)

    Column(modifier = Modifier.padding(24.dp)) {
        Button(onClick = { blue = !blue }) {
            Text(text = "CHANGE COLOR")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(128.dp)
                .background(color)
        )
    }
}

enum class BoxState {
    Small, Large;

    fun switch() = when (this) {
        Small -> Large
        Large -> Small
    }
}

@Composable
fun UpdateTransitionDemo() {
    var boxState by remember { mutableStateOf(BoxState.Small) }
    val transition = updateTransition(targetState = boxState)
    val color by transition.animateColor { state ->
        when (state) {
            BoxState.Small -> Blue
            BoxState.Large -> Magenta
        }
    }
    val size by transition.animateDp { state ->
        when (state) {
            BoxState.Small -> 64.dp
            BoxState.Large -> 128.dp
        }
    }

    val corner by transition.animateDp { state ->
        when (state) {
            BoxState.Small -> 0.dp
            BoxState.Large -> 24.dp
        }
    }

    Column(modifier = Modifier.padding(24.dp)) {
        Button(onClick = { boxState = boxState.switch() }) {
            Text(text = "CHANGE")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(corner, corner, corner, corner))
                .background(color)
        )
    }
}

@Composable
fun AnimateVisibilityDemo() {
    var visible by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(24.dp)) {
        Button(onClick = { visible = !visible }) {
            Text(text = if (visible) "HIDE" else "SHOW")
        }
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibility(visible) {
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .background(Blue)
            )
        }
    }
}

@Composable
fun AnimateContentSizeDemo() {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(24.dp)) {
        Button(onClick = { expanded = !expanded }) {
            Text(text = if (expanded) "SHRINK" else "EXPAND")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .background(LightGray)
                .animateContentSize()
        ) {
            Text(
                text = stringResource(R.string.lorem_ipsum),
                fontSize = 16.sp,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(16.dp),
                maxLines = if (expanded) Int.MAX_VALUE else 2
            )
        }
    }
}

enum class DemoScene {
    Text, Icon;

    fun switch() = when (this) {
        Text -> Icon
        Icon -> Text
    }
}

@Composable
fun CrossfadeDemo() {
    var scene by remember { mutableStateOf(DemoScene.Text) }

    Column(modifier = Modifier.padding(24.dp)) {
        Button(onClick = { scene = scene.switch() }) {
            Text(text = "TOGGLE")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Crossfade(targetState = scene) { scene ->
            when (scene) {
                DemoScene.Text -> Text(
                    text = "Phone",
                    fontSize = 32.sp
                )
                DemoScene.Icon -> Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

