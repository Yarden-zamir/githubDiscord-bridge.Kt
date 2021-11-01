import com.jcabi.github.Coordinates
import com.jcabi.github.Github
import com.jcabi.github.RtGithub
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingString
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.Color
import dev.kord.core.behavior.channel.TextChannelBehavior
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.Clock

class GithubExtension : Extension() {
    override val name = "GitHub_ext"
    val github: Github = RtGithub(env("GITHUB_BOT_TOKEN"))
    val repo = github.repos()[Coordinates.Simple(env("GITHUB_REPO").split("://github.com/")[1])]
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
                        embed {
                            this.title = arguments.title
                            this.description = arguments.description
                            this.url = env("GITHUB_REPO")+"/issues/${i.number()}"
                            this.author {
                                this.name = user.asUser().username //todo: work with nicknames
                                icon = user.asUser().avatar?.url
                            }
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
                    ).addUser(user.id)

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