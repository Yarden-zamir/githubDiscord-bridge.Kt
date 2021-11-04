import GDBridge.GithubExtension
import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.Snowflake


val SERVER_ID = Snowflake(  // Store this as a Discord snowflake, aka an ID
    env("SERVER_ID").toLong()
)
private val TOKEN = env("DISCORD_BOT_TOKEN")
suspend fun main() {

    val bot = ExtensibleBot(TOKEN) {
        applicationCommands { defaultGuild = SERVER_ID }
        extensions {
            add(::GithubExtension)
        }
    }
    bot.start()

}