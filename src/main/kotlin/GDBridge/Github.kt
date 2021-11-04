package GDBridge

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.TextChannelBehavior
import dev.kord.core.behavior.channel.threads.ThreadChannelBehavior
import org.kohsuke.github.GHIssue

val enclosers = Pair<String, String>(
    first = "<details>\n" +
            "<summary>Synced Discord thread Id</summary>",
    second = "</details>"
)

internal fun GHIssue.hasSyncedDiscordThread(): Boolean {
    return body?.contains(enclosers.first) == true && body.contains(enclosers.second)
}

internal suspend fun GHIssue.startSyncedDiscordThread(channel: TextChannelBehavior): ThreadChannelBehavior {
    val thread = channel.startPublicThread("[${number}] - ${title}")
    this.body =
        enclosers.first + "  " + thread.id.value + "  " + enclosers.second + "  " + if (body != null) body else ""
    return thread
    enclosers.
}

internal suspend fun GHIssue.getSyncedDiscordThread(kord: Kord): ThreadChannelBehavior {
    println("Retrieving existing thread for $this")
    fun extractSnowflake(): Snowflake {
        return Snowflake(body.substringAfter(enclosers.first).substringBefore(enclosers.second).trim())
    }
    return kord.getChannel(extractSnowflake()) as ThreadChannelBehavior
}