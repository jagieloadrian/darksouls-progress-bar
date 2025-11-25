package com.github.jagieloadrian.darksoulsprogressbar.service

import com.intellij.execution.ExecutionManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.util.messages.MessageBus
import com.intellij.util.messages.MessageBusConnection
import com.intellij.util.messages.MessageHandler
import com.intellij.util.messages.SimpleMessageBusConnection
import com.intellij.util.messages.Topic
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class TestFailureStartActivityTest {

    @Test
    fun `execute should subscribe DSFailingProcessHandler`(): Unit = runBlocking {
        // given
        val stubConnection = StubMessageBusConnection()
        val stubBus = StubMessageBus(stubConnection, null, true)

        // mocks
        val project = mockk<Project>()
        every { project.messageBus } returns stubBus

        val activity = TestFailureStartActivity()

        // when
        activity.execute(project)

        // then
        stubConnection.subs.size shouldBe 1

        val (topic, listener) = stubConnection.subs.first()

        topic shouldBe ExecutionManager.EXECUTION_TOPIC
        listener.shouldBeInstanceOf<DSFailingProcessHandler>()
    }
}

class StubMessageBusConnection : SimpleMessageBusConnection, MessageBusConnection {
    val subs = mutableListOf<Pair<Topic<*>, Any>>()

    override fun <L : Any> subscribe(topic: Topic<L>, handler: L) {
        subs += topic to handler as Any
    }

    override fun disconnect() {
        //nothing
    }

    override fun <L : Any> subscribe(topic: Topic<L>) {
      throw UnsupportedOperationException()
    }

    override fun setDefaultHandler(handler: MessageHandler?) {
        throw UnsupportedOperationException()
    }

    override fun deliverImmediately() {
        throw UnsupportedOperationException()
    }

    override fun dispose() {
        throw UnsupportedOperationException()
    }


}

class StubMessageBus(
    val connection: StubMessageBusConnection,
    override val parent: MessageBus?,
    override val isDisposed: Boolean,
) : MessageBus {
    override fun connect(): MessageBusConnection = connection
    override fun simpleConnect(): SimpleMessageBusConnection {
            return connection
    }

    override fun connect(parentDisposable: Disposable): MessageBusConnection {
        return connection
    }

    override fun connect(coroutineScope: CoroutineScope): SimpleMessageBusConnection {
        return connection
    }

    override fun <L : Any> syncPublisher(topic: Topic<L>): L =
        throw UnsupportedOperationException()

    override fun <L : Any> syncAndPreloadPublisher(topic: Topic<L>): L {
        throw UnsupportedOperationException()
    }

    override fun dispose() {}
    override fun hasUndeliveredEvents(topic: Topic<*>): Boolean {
        return true
    }
}