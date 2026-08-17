package com.example.jecpackcomposeno1.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

/**
 * Animation chuyển màn kiểu native: mở màn mới trượt từ phải sang, back thì trượt về phải.
 * Dùng chung cho các NavHost để 4 chiều luôn khớp nhau.
 */
object NavAnim {

    private const val DURATION = 300

    /** Màn mới vào: trượt từ phải sang. */
    val enter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(DURATION, easing = FastOutSlowInEasing),
        )
    }

    /** Màn cũ bị đẩy đi: trượt sang trái. */
    val exit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(DURATION, easing = FastOutSlowInEasing),
        )
    }

    /** Back: màn dưới quay lại, trượt từ trái sang. */
    val popEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(DURATION, easing = FastOutSlowInEasing),
        )
    }

    /** Back: màn đang mở trượt ra phải. */
    val popExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(DURATION, easing = FastOutSlowInEasing),
        )
    }
}