package ru.spbau.mit

import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*

class DebuggerTest {
    @Test
    fun runTest() {
        TestDebugger {
            load("src/test/resources/runTest")
            run()
            Assert.assertTrue(finished())
        }.start()
    }

    @Test
    fun breakpointTest() {
        TestDebugger {
            load("src/test/resources/breakpointTest")
            breakpoint(2)
            run()
            Assert.assertEquals(2, currentLine())
            continueRun()
            Assert.assertEquals(4, currentLine())
            Assert.assertTrue(finished())
        }.start()
    }

    @Test
    fun stopTest() {
        TestDebugger {
            load("src/test/resources/breakpointTest")
            breakpoint(2)
            run()
            stop()
            run()
            Assert.assertEquals(2, currentLine())
            continueRun()
            Assert.assertEquals(4, currentLine())
            Assert.assertTrue(finished())
        }.start()
    }

    @Test
    fun conditionTest() {
        TestDebugger {
            load("src/test/resources/conditionTest")
            condition(3, "a > 2")
            run()
            Assert.assertEquals(3, currentLine())
            continueRun()
            Assert.assertEquals(3, currentLine())
            continueRun()
            Assert.assertEquals(3, currentLine())
            continueRun()
            Assert.assertTrue(finished())
        }.start()
    }

    @Test
    fun removeTest() {
        TestDebugger {
            load("src/test/resources/conditionTest")
            condition(3, "a > 2")
            run()
            Assert.assertEquals(3, currentLine())
            removeBreakpoint(3)
            continueRun()
            Assert.assertTrue(finished())
        }.start()
    }
}