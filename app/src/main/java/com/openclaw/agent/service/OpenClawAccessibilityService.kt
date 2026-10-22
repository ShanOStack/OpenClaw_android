package com.openclaw.agent.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class OpenClawAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "OpenClawA11y"
        @Volatile
        var instance: OpenClawAccessibilityService? = null
            private set

        val isServiceRunning: Boolean
            get() = instance != null

        fun openAccessibilitySettings(context: android.content.Context) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.i(TAG, "OpenClaw Accessibility Service connected and active")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Monitors app context transitions and dynamic UI changes
    }

    override fun onInterrupt() {
        Log.w(TAG, "OpenClaw Accessibility Service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
        }
    }

    // ================= DEVICE GLOBAL CONTROLS ================= //

    fun goHome(): Boolean = performGlobalAction(GLOBAL_ACTION_HOME)

    fun goBack(): Boolean = performGlobalAction(GLOBAL_ACTION_BACK)

    fun openRecents(): Boolean = performGlobalAction(GLOBAL_ACTION_RECENTS)

    fun openNotifications(): Boolean = performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)

    fun openQuickSettings(): Boolean = performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)

    fun lockScreen(): Boolean = performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)

    fun takeScreenshot(): Boolean = performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)

    // ================= IN-APP UI INTERACTION & AUTOMATION ================= //

    /**
     * Search for any node matching target text and perform click.
     */
    fun clickByText(text: String, ignoreCase: Boolean = true): Boolean {
        val root = rootInActiveWindow ?: return false
        val list = mutableListOf<AccessibilityNodeInfo>()
        collectAllNodes(root, list)

        for (node in list) {
            val nodeText = node.text?.toString() ?: ""
            val contentDesc = node.contentDescription?.toString() ?: ""

            val match = if (ignoreCase) {
                nodeText.contains(text, ignoreCase = true) || contentDesc.contains(text, ignoreCase = true)
            } else {
                nodeText == text || contentDesc == text
            }

            if (match) {
                if (performClickRecursive(node)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Search for node by viewId and click it.
     */
    fun clickById(viewId: String): Boolean {
        val root = rootInActiveWindow ?: return false
        val nodes = root.findAccessibilityNodeInfosByViewId(viewId)
        for (node in nodes) {
            if (performClickRecursive(node)) {
                return true
            }
        }
        return false
    }

    private fun performClickRecursive(node: AccessibilityNodeInfo?): Boolean {
        var current = node
        while (current != null) {
            if (current.isClickable) {
                return current.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            current = current.parent
        }
        // If not marked clickable, tap center coordinates
        if (node != null) {
            val rect = Rect()
            node.getBoundsInScreen(rect)
            if (rect.width() > 0 && rect.height() > 0) {
                clickCoordinates(rect.centerX().toFloat(), rect.centerY().toFloat())
                return true
            }
        }
        return false
    }

    /**
     * Set text directly into the currently focused or first available input field.
     */
    fun typeText(text: String): Boolean {
        val root = rootInActiveWindow ?: return false
        var target = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)

        if (target == null) {
            val list = mutableListOf<AccessibilityNodeInfo>()
            collectAllNodes(root, list)
            target = list.firstOrNull { it.isEditable }
        }

        return if (target != null && target.isEditable) {
            val args = Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
            }
            target.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
        } else {
            false
        }
    }

    /**
     * Click specific screen coordinates (X, Y).
     */
    fun clickCoordinates(x: Float, y: Float) {
        val path = Path().apply { moveTo(x, y) }
        val stroke = GestureDescription.StrokeDescription(path, 0, 80)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        dispatchGesture(gesture, null, null)
    }

    /**
     * Swipe gesture on screen.
     */
    fun swipe(startX: Float, startY: Float, endX: Float, endY: Float, durationMs: Long = 300) {
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }
        val stroke = GestureDescription.StrokeDescription(path, 0, durationMs)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        dispatchGesture(gesture, null, null)
    }

    private fun collectAllNodes(node: AccessibilityNodeInfo, list: MutableList<AccessibilityNodeInfo>) {
        list.add(node)
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            collectAllNodes(child, list)
        }
    }
}
