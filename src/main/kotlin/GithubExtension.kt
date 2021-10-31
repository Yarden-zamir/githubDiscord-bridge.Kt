import com.jcabi.github.Coordinates
import com.jcabi.github.Github
import com.jcabi.github.RtGithub
import com.kotlindiscord.kord.extensions.checks.threadFor
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.*
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.Color
import dev.kord.common.entity.ArchiveDuration
import dev.kord.core.behavior.channel.TextChannelBehavior
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.Clock
import kotlin.concurrent.thread

class GithubExtension : Extension() {
    override val name = "GitHub_ext"
    val github: Github = RtGithub("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")
    val repo = github.repos()[Coordinates.Simple("Discord-Github-Bridge/turbo-waddle")]
    override suspend fun setup() {
        issue(arrayOf("issue", "todo", "task"))
    }

    private suspend fun issue(issue: Array<String>) {
        issue.forEach {
            publicSlashCommand(::IssueArguments) {
                name = it
                description = "Add an issue to the targeted github repo"
                action {
                    val i = repo.issues().create(arguments.title, arguments.description)
                    if (arguments.labels != "") i.labels().add(arguments.labels.split(","))
                    val r = respond {
//                        content = "Issue added ${"https://github.com/Discord-Github-Bridge/turbo-waddle/issues/"+i.number()}"
                        embed {
                            this.title = arguments.title
                            this.url = env("GITHUB_REPO")
                            color = Color(238636)
                            footer {
                                this.text = env("GITHUB_REPO").split("://github.com/")[1]
                                this.icon = env("PROJECT_ICON")
                            }
                            timestamp = Clock.System.now()
                        }
                    }.id
                    (this.channel.asChannel() as TextChannelBehavior).startPublicThreadWithMessage(
                        r,
                        "Issue : " + arguments.title
                    )

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