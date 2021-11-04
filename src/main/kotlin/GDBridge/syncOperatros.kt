package GDBridge

import dev.kord.core.behavior.channel.TextChannelBehavior
import dev.kord.core.behavior.channel.threads.ThreadChannelBehavior
import org.kohsuke.github.GHIssue
import org.kohsuke.github.GHRepository

internal suspend fun syncOpenedIssue(url: String?, targetChannel: TextChannelBehavior, repo: GHRepository): ThreadChannelBehavior {
    val issue = repo.getIssue(extractParams(url).toInt())
    val thread = acquireDiscordThread(issue, targetChannel)
    thread.forwardIssueBody(issue)
    return thread
}


internal suspend fun syncNewComment(url: String?, targetChannel: TextChannelBehavior, repo: GHRepository): ThreadChannelBehavior {
    val params = extractParams(url)
    val issue = repo.getIssue(params.substringBefore("#").toInt())
    val comment = issue.listComments().first {
        it.id == params.substringAfter("-").toLong()
    }
    val thread = acquireDiscordThread(issue, targetChannel)
    thread.forwardGithubComment(comment)
    return thread
}

internal suspend fun syncClosedIssue(url: String?, targetChannel: TextChannelBehavior, repo: GHRepository): ThreadChannelBehavior {
    val issue = repo.getIssue(extractParams(url).toInt())
    val thread = acquireDiscordThread(issue, targetChannel)
    thread.forwardIssueClose()
    return thread
}


private suspend fun acquireDiscordThread(issue: GHIssue, targetChannel: TextChannelBehavior): ThreadChannelBehavior {
    return when (issue.hasSyncedDiscordThread()) {
        true -> issue.getSyncedDiscordThread(targetChannel.kord)
        false -> issue.startSyncedDiscordThread(targetChannel)
    }
}

private fun extractParams(url: String?): String {
//    embedeEventIsNotGithubEvent()
    return url?.split("/")?.last()
        ?: throw IllegalArgumentException("Embed Does not include link, possibly not sourced from github webhook?")
}