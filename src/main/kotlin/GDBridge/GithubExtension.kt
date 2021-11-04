package GDBridge

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingString
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.TextChannelBehavior
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.Clock
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHubBuilder


var github = GitHubBuilder().withOAuthToken(env("GITHUB_BOT_TOKEN")).build()

class GithubExtension : Extension() {
    override val name = "GitHub_ext"

    override suspend fun setup() {
        val repo = github.getRepository(env("GITHUB_REPO"))
        assembleSync(repo, 904700061479469066, 904685079387865139)
        generateIssueCommands(repo, arrayOf("issue", "todo", "task"))
    }

    private suspend fun assembleSync(repo: GHRepository, inputChannelSnowflake: Long, issueChannelSnowflake: Long) {
        kord.on<MessageCreateEvent> {
            val inputChannel = kord.getChannel(Snowflake(inputChannelSnowflake)) as TextChannelBehavior
            val issuesChannel = kord.getChannel(Snowflake(issueChannelSnowflake)) as TextChannelBehavior
            if (message.channel.id != inputChannel.id) return@on
            if (message.embeds.isEmpty()) return@on
            val embed = message.embeds[0]
            val prefix = "[${repo.fullName}]"
            with(embed.title!!) { //We know this cannot be null
                when {
                    startsWith("$prefix New comment on issue") ->
                        syncNewComment(embed.url, issuesChannel, repo)
                    startsWith("$prefix Issue opened") || startsWith("$prefix Issue reopened") ->
                        syncOpenedIssue(embed.url, issuesChannel, repo)
                    startsWith("$prefix Issue closed") ->
                        syncClosedIssue(embed.url, issuesChannel, repo)
                    else -> {}
                }
            }
            message.delete()//message addressed and removed
        }
    }


    private suspend fun generateIssueCommands(repo: GHRepository, commands: Array<String>) {
        commands.forEach {
            publicSlashCommand(::IssueArguments) {
                name = it
                description = "Add an issue to the targeted github repo"
                action {
                    val issue = repo.createIssue(arguments.title).body(arguments.description).create()
                    if (arguments.labels != "")
                        arguments.labels.split(",").forEach { issue.addLabels(it) }
                    val issuesChannel = this.guild?.getChannel(Snowflake(904685079387865139)) as TextChannelBehavior
                    val issueThread = syncOpenedIssue(issue.url.toString(),issuesChannel,repo)
                    issueThread.addUser(user.id)
                    val r = respond {
                        embed {
                            this.title = arguments.title
                            this.description =
                                "${issueThread.mention}/[Github Link](https://github.com/${env("GITHUB_REPO")}/issues/${issue.number})" +
                                        "\n" + arguments.description
                            this.url = "https://github.com/${env("GITHUB_REPO")}/issues/${issue.number}"
                            this.author {
                                this.name = user.asUser().username //todo: work with nicknames
                                icon = user.asUser().avatar?.url
                            }
                            color = Color(238636)
                            footer {
                                this.text = env("GITHUB_REPO")
                                this.icon = env("PROJECT_ICON")
                            }
                            timestamp = Clock.System.now()
                        }
                    }.id
                }
            }
        }

    }

    inner class IssueArguments : Arguments() {
        val title by string(
            displayName = "Title",
            description = "The issue title",
        )
        val description by defaultingString(
            displayName = "Description",
            description = "The issue description",
            defaultValue = ""
        )
        val labels by defaultingString(
            displayName = "Labels",
            description = "A comma separated list of labels to apply to the issue (whatever doesn't exist will be created)",
            defaultValue = ""
        )
    }
}







