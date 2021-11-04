package GDBridge

import dev.kord.common.Color
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.channel.threads.ThreadChannelBehavior
import dev.kord.core.behavior.channel.threads.edit
import org.kohsuke.github.GHIssue
import org.kohsuke.github.GHIssueComment

internal suspend fun ThreadChannelBehavior.forwardGithubComment(comment: GHIssueComment) {
    createEmbed {
        author {
            name = comment.user.login //user.name is not a thing!
            icon = comment.user.avatarUrl
            url = comment.user.htmlUrl.toString()
        }
        description = comment.body
    }
}

internal suspend fun ThreadChannelBehavior.forwardIssueBody(issue: GHIssue) {
//    val user = if (issue.user.id != github.myself.id) issue.user else issue.user
    createEmbed {
        title = issue.title
        author {
            name = issue.user.login //user.name is not a thing!
            icon = issue.user.avatarUrl
            url = issue.user.htmlUrl.toString()
        }
        description = issue.body
    }
}

internal suspend fun ThreadChannelBehavior.forwardIssueClose() {
    createEmbed {
        this.title = "Issue closed"
        color = Color(137, 87, 229)
    }
    edit { archived = true }
}
