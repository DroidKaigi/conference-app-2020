package io.github.droidkaigi.confsched2020.widget.component

import io.mockk.MockKAnnotations
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class MockkRule(private val target: Any) : TestWatcher() {
    override fun starting(description: Description?) {
        super.starting(description)
        MockKAnnotations.init(target, relaxUnitFun = true)
    }
}