package com.example.appyxnavigation

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.operation.replace
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.integration.NodeActivity
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import com.example.appyxnavigation.ui.theme.AppyxNavigationTheme
import kotlinx.parcelize.Parcelize

class MainActivity : NodeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppyxNavigationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NodeHost(
                        lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                        integrationPoint = appyxV2IntegrationPoint
                    ) {
                        RootNode(it)
                    }
                }
            }
        }
    }
}

class RootNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        model = BackStackModel(
            initialTarget = NavTarget.Child1,
            savedStateMap = buildContext.savedStateMap,
        ),
        motionController = { BackStackFader(it) }
    )
) : ParentNode<NavTarget>(
    buildContext = buildContext,
    appyxComponent = backStack // pass it here
) {

    @Composable
    override fun View(modifier: Modifier) {
        Column {
            Text("Hello world!")

            // Let's also add some controls so we can test it
            Row {
                TextButton(onClick = { backStack.replace(NavTarget.Child1) }) {
                    Text(text = "Push child 1")
                }
                TextButton(onClick = { backStack.replace(NavTarget.Child2) }) {
                    Text(text = "Push child 2")
                }
                TextButton(onClick = { backStack.replace(NavTarget.Child3) }) {
                    Text(text = "Push child 3")
                }
                TextButton(onClick = { backStack.pop() }) {
                    Text(text = "Pop")
                }
            }
            // Let's add the children to the composition
            AppyxComponent(
                appyxComponent = backStack
            )
        }
    }

    override fun resolve(interactionTarget: NavTarget, buildContext: BuildContext): Node =
        when (interactionTarget) {
            NavTarget.Child1 -> node(buildContext) { Text(text = "Placeholder for child 1") }
            NavTarget.Child2 -> node(buildContext) { Text(text = "Placeholder for child 2") }
            NavTarget.Child3 -> SomeChildNode(buildContext,"FK")
        }
}

/**
 * You can create this class inside the body of RootNode
 *
 * Note: You must apply the 'kotlin-parcelize' plugin to use @Parcelize
 * https://developer.android.com/kotlin/parcelize
 */
sealed class NavTarget : Parcelable {
    @Parcelize
    object Child1 : NavTarget()

    @Parcelize
    object Child2 : NavTarget()

    @Parcelize
    object Child3 : NavTarget()
}

class SomeChildNode(
    buildContext: BuildContext,
    private val name: String
) : Node(
    buildContext = buildContext
) {
    @Composable
    override fun View(modifier: Modifier) {
        Text("This is SomeChildNode replaced by $name")
    }
}

