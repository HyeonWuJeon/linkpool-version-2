package linkpool.event

import kotlinx.coroutines.*
import linkpool.common.log.kLogger
import linkpool.folder.port.`in`.FolderEventListener
import linkpool.link.port.`in`.LinkEventListener
import linkpool.user.model.UserSignedOutEvent
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.messaging.Message
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

fun <T> CoroutineScope.safeAsync(block: suspend () -> T): Deferred<Result<T>> {
    return async {
        runCatching {
            block()
        }
    }
}

@Component
class UserSignedOutEventListener(
    private val folderEventListener: FolderEventListener,
    private val linkEventListener: LinkEventListener
) {

    private val log = kLogger()

    @Async
    @ServiceActivator(inputChannel = "signedOutEvent")
    fun handleForSignedOutEventListener(@Payload message: Message<UserSignedOutEvent>) {
        CoroutineScope(Job()).launch {
            val folderEventDeferred = safeAsync {
                folderEventListener.deleteBatchAll(message.payload)
            }
            val linkEventDeferred = safeAsync {
                linkEventListener.deleteBatchAll(message.payload)
            }
            folderEventDeferred.await()
                .onSuccess { log.info("succeed in folderEventListener from UserSignedOut... userId: ${message.payload.userId}") }
                .onFailure { e -> log.info("Error in folderEventListener from UserSignedOut... userId: ${message.payload.userId} \n error: ${e.message}") }
            linkEventDeferred.await()
                .onSuccess { log.info("succeed in linkEventListener from UserSignedOut... userId: ${message.payload.userId}") }
                .onFailure { e -> log.info("Error in linkEventListener from UserSignedOut... userId: ${message.payload.userId} \n error: ${e.message}") }
        }
    }
}