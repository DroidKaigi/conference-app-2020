package io.github.droidkaigi.confsched2019.widget.component

import io.mockk.MockKAnnotations
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class MockkRule(val target: Any) : TestWatcher() {
    override fun starting(description: Description?) {
        super.starting(description)
        MockKAnnotations.init(target, relaxUnitFun = true)
    }
}