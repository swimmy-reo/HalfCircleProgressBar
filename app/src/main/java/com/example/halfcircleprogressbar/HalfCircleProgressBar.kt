package com.example.halfcircleprogressbar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private const val MIN_LEVEL = 0
private const val MAX_LEVEL = 100
private const val STROKE_WIDTH_RATIO = 0.08F
private const val START_ANGLE = -180F
private const val FINISH_ANGLE = 180F

@Composable
fun LevelCounter(
    currentLevel: Int,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val strokeWidth = configuration.screenWidthDp * STROKE_WIDTH_RATIO
    val sweepAngle = calcSweepAngle(currentLevel = currentLevel)
    HalfCircleProgressBar(
        modifier = modifier,
        content = {
            Spacer(modifier = Modifier.layoutId(ComponentId.TopSpacer))

            Canvas(
                modifier = Modifier.layoutId(ComponentId.ProgressBar)
            ) {
                drawArc(
                    color = Color.Gray,
                    startAngle = START_ANGLE,
                    sweepAngle = FINISH_ANGLE,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    size = Size(width = size.width, height = size.height * 2), // Canvasの空白エリアを埋めるため
                )
                drawArc(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFFA500), Color.Red),
                    ),
                    startAngle = START_ANGLE,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    size = Size(width = size.width, height = size.height * 2), // Canvasの空白エリアを埋めるため
                )
            }

            Image(
                painter = painterResource(R.drawable.baseline_speed_24),
                contentDescription = null,
                modifier = Modifier.layoutId(ComponentId.Image)
            )

            Text(
                text = MIN_LEVEL.toString(),
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier
                    .alpha(0.4F)
                    .layoutId(ComponentId.MinLevel),
                textAlign = TextAlign.Center,
            )
            Text(
                text = MAX_LEVEL.toString(),
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier
                    .alpha(0.4F)
                    .layoutId(ComponentId.MaxLevel),
                textAlign = TextAlign.Center,
            )

            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .layoutId(ComponentId.CurrentLevel),
            ) {
                Text(
                    text = "Lv.",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    ),
                    modifier = Modifier.alignByBaseline(),
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = currentLevel.toString(),
                    modifier = Modifier.alignByBaseline(),
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false,
                    maxLines = 1,
                    onTextLayout = {},
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    ),
                )
            }
        }
    )
}

@Composable
private fun HalfCircleProgressBar(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = { measurables, constraints ->
            // ProgressBarのサイズ
            val progressBarSize = ProgressBarSize(constraints.maxWidth)
            val progressBarConstraints = Constraints(
                minWidth = progressBarSize.width,
                maxWidth = progressBarSize.width,
                minHeight = progressBarSize.height,
                maxHeight = progressBarSize.height,
            )
            val progressBarPlaceable =
                measurables.find { it.layoutId == ComponentId.ProgressBar }
                    ?.measure(progressBarConstraints)
                    ?: error("not found")

            // 上部空白のサイズ
            val topSpacerSize = TopSpacerSize(progressBarSize.height)
            val topSpacerConstraints = Constraints(
                minHeight = topSpacerSize.height,
                maxHeight = topSpacerSize.height,
            )
            val topSpacerPlaceable =
                measurables.find { it.layoutId == ComponentId.TopSpacer }
                    ?.measure(topSpacerConstraints)
                    ?: error("not found")

            // 画像のサイズ
            val imageSize = ImageSize(
                progressBarWidth = progressBarSize.width,
                progressBarHeight = progressBarSize.height,
            )
            val imageConstraints = Constraints(
                minWidth = imageSize.width,
                maxWidth = imageSize.width,
                minHeight = imageSize.height,
                maxHeight = imageSize.height,
            )
            val imagePlaceable =
                measurables.find { it.layoutId == ComponentId.Image }
                    ?.measure(imageConstraints)
                    ?: error("not found")

            // 閾値のサイズ
            val minLevelSize = MinLevelSize(
                progressBarWidth = progressBarSize.width,
                progressBarHeight = progressBarSize.height
            )
            val minLevelConstraints = Constraints(
                minWidth = minLevelSize.width,
                maxWidth = minLevelSize.width,
                minHeight = minLevelSize.height,
                maxHeight = minLevelSize.height,
            )
            val minLevelPlaceable =
                measurables.find { it.layoutId == ComponentId.MinLevel }
                    ?.measure(minLevelConstraints)
                    ?: error("not found")
            val maxLeveSize = MaxLevelSize(
                progressBarWidth = progressBarSize.width,
                progressBarHeight = progressBarSize.height
            )
            val maxLevelConstraints = Constraints(
                minWidth = maxLeveSize.width,
                maxWidth = maxLeveSize.width,
                minHeight = maxLeveSize.height,
                maxHeight = maxLeveSize.height,
            )
            val maxLevelPlaceable =
                measurables.find { it.layoutId == ComponentId.MaxLevel }
                    ?.measure(maxLevelConstraints)
                    ?: error("not found")

            // 現在のレベルのサイズ
            val currentLevelSize = CurrentLevelSize(
                progressBarWidth = progressBarSize.width,
                progressBarHeight = progressBarSize.height,
            )
            val currentLevelSizeConstraints = Constraints(
                minWidth = currentLevelSize.width,
                maxWidth = currentLevelSize.width,
                minHeight = currentLevelSize.height,
                maxHeight = currentLevelSize.height,
            )
            val currentLevelPlaceable =
                measurables.find { it.layoutId == ComponentId.CurrentLevel }
                    ?.measure(currentLevelSizeConstraints)
                    ?: error("not found")

            // 全体のサイズ
            val width = constraints.maxWidth
            val height = topSpacerSize.height + progressBarSize.height + currentLevelSize.height

            // 左右片方のpadding
            val horizontalPadding = (constraints.maxWidth - progressBarPlaceable.width) / 2

            // ProgressBarのOffset
            val progressBarXOffset = ProgressBarOffset(
                parentWidth = constraints.maxWidth,
                progressBarWidth = progressBarSize.width,
            ).xOffset

            // 画像のoffset
            val imageOffset = ImageOffset(
                parentWidth = constraints.maxWidth,
                topSpacerHeight = topSpacerSize.height,
                progressBarHeight = progressBarSize.height,
                imageWidth = imageSize.width,
            )

            // 閾値のoffset
            val minLevelOffset = MinLevelOffset(
                topSpacerHeight = topSpacerSize.height,
                progressBarHeight = progressBarSize.height,
                horizontalPadding = horizontalPadding,
                minLevelWidth = minLevelPlaceable.width,
            )
            val maxLevelOffset = MaxLevelOffset(
                parentWidth = constraints.maxWidth,
                progressBarHeight = progressBarSize.height,
                horizontalPadding = horizontalPadding,
                maxLevelWidth = maxLevelPlaceable.width,
            )

            // 現在のレベルのoffset
            val currentLevelOffset = CurrentLevelOffset(
                parentWidth = constraints.maxWidth,
                totalStepCountWidth = currentLevelSize.width,
                progressBarHeight = progressBarSize.height,
                halfYOffset = minLevelOffset.halfYOffset,
                totalStepCountHeight = currentLevelSize.height,
            )
            layout(width = width, height = height) {
                var yOffset = 0
                topSpacerPlaceable.place(
                    x = 0,
                    y = yOffset,
                )
                yOffset += topSpacerSize.height
                progressBarPlaceable.place(
                    x = progressBarXOffset,
                    y = yOffset,
                )
                imagePlaceable.place(
                    x = imageOffset.xOffset,
                    y = imageOffset.yOffset,
                )
                yOffset += minLevelOffset.yOffset
                minLevelPlaceable.place(
                    x = minLevelOffset.xOffset,
                    y = yOffset,
                )
                maxLevelPlaceable.place(
                    x = maxLevelOffset.xOffset,
                    y = yOffset,
                )
                currentLevelPlaceable.place(
                    x = currentLevelOffset.xOffset,
                    y = currentLevelOffset.yOffset,
                )
            }
        }
    )
}

private sealed interface ComponentId {
    object TopSpacer : ComponentId
    object ProgressBar : ComponentId
    object Image : ComponentId
    object MinLevel : ComponentId
    object MaxLevel : ComponentId
    object CurrentLevel : ComponentId
}

private sealed interface VerticalRatio {
    val verticalRatio: Double
}

private sealed interface HorizontalRatio {
    val horizontalRatio: Double
}

private data class ProgressBarSize(
    val parentWidth: Int,
) : HorizontalRatio, VerticalRatio {
    val width get() = (parentWidth * horizontalRatio).roundToInt()
    val height get() = (width * verticalRatio).roundToInt()
    override val horizontalRatio: Double = 0.737
    override val verticalRatio: Double = 0.494
}

private data class TopSpacerSize(
    val progressBarHeight: Int,
) : VerticalRatio {
    val height get() = (progressBarHeight * verticalRatio).roundToInt()
    override val verticalRatio: Double = 0.2
}

private data class ImageSize(
    val progressBarWidth: Int,
    val progressBarHeight: Int,
) : HorizontalRatio, VerticalRatio {
    val width get() = (progressBarWidth * horizontalRatio).roundToInt()
    val height get() = (progressBarHeight * verticalRatio).roundToInt()
    override val horizontalRatio: Double = 0.466
    override val verticalRatio: Double = 0.857
}

private data class CurrentLevelSize(
    val progressBarWidth: Int,
    val progressBarHeight: Int,
) : HorizontalRatio, VerticalRatio {
    val width get() = (progressBarWidth * horizontalRatio).roundToInt()
    val height get() = (progressBarHeight * verticalRatio).roundToInt()
    override val horizontalRatio: Double = 0.552
    override val verticalRatio: Double = 0.3
}

private data class MinLevelSize(
    val progressBarWidth: Int,
    val progressBarHeight: Int,
) : HorizontalRatio, VerticalRatio {
    val width get() = (progressBarWidth * horizontalRatio).roundToInt()
    val height get() = (progressBarHeight * verticalRatio).roundToInt()
    override val horizontalRatio: Double = 0.1
    override val verticalRatio: Double = 0.2
}

private data class MaxLevelSize(
    val progressBarWidth: Int,
    val progressBarHeight: Int,
) : HorizontalRatio, VerticalRatio {
    val width get() = (progressBarWidth * horizontalRatio).roundToInt()
    val height get() = (progressBarHeight * verticalRatio).roundToInt()
    override val horizontalRatio: Double = 0.1
    override val verticalRatio: Double = 0.2
}

private sealed interface XOffset {
    val xOffset: Int
}

private sealed interface YOffset {
    val yOffset: Int
}

private data class ProgressBarOffset(
    val parentWidth: Int,
    val progressBarWidth: Int,
) : XOffset {
    override val xOffset get() = (parentWidth / 2) - (progressBarWidth / 2)
}

private data class ImageOffset(
    val parentWidth: Int,
    val topSpacerHeight: Int,
    val progressBarHeight: Int,
    val imageWidth: Int,
) : VerticalRatio, XOffset, YOffset {
    override val verticalRatio: Double get() = 0.15
    override val yOffset get() = (topSpacerHeight + progressBarHeight * verticalRatio).toInt()
    override val xOffset get() = (parentWidth / 2) - (imageWidth / 2)
}

private data class MinLevelOffset(
    val topSpacerHeight: Int,
    val progressBarHeight: Int,
    val horizontalPadding: Int,
    val minLevelWidth: Int,
) : VerticalRatio, XOffset, YOffset {
    override val verticalRatio: Double = 0.12
    override val xOffset get() = horizontalPadding - (minLevelWidth / 2)
    override val yOffset get() = (progressBarHeight + progressBarHeight * verticalRatio).toInt()
    val halfYOffset get() = (progressBarHeight * verticalRatio / 2).toInt()
}

private data class MaxLevelOffset(
    val parentWidth: Int,
    val progressBarHeight: Int,
    val horizontalPadding: Int,
    val maxLevelWidth: Int,
) : VerticalRatio, XOffset {
    override val verticalRatio: Double = 0.12
    override val xOffset get() = parentWidth - horizontalPadding - (maxLevelWidth / 2)
}

private data class CurrentLevelOffset(
    val parentWidth: Int,
    val totalStepCountWidth: Int,
    val progressBarHeight: Int,
    val halfYOffset: Int,
    val totalStepCountHeight: Int,
) : XOffset, YOffset {
    override val xOffset get() = (parentWidth / 2) - (totalStepCountWidth / 2)
    override val yOffset get() = progressBarHeight + halfYOffset + (totalStepCountHeight / 2)
}

private fun calcSweepAngle(currentLevel: Int): Float = 180F * currentLevel / MAX_LEVEL
