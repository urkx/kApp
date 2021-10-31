import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.*
import com.mongodb.ConnectionString
import org.litote.kmongo.reactivestreams.KMongo

val shoppingList = mutableListOf(
    ShoppingListItem("Cucumbers", 1),
    ShoppingListItem("Tomatoes", 2),
    ShoppingListItem("Orange juice", 3)
)

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Delete)
            anyHost()
        }
        install(Compression) {
            gzip()
        }
        routing {
           route(ShoppingListItem.path) {
               get {
                   call.respond(shoppingList)
               }
               post {
                   shoppingList += call.receive<ShoppingListItem>()
                   call.respond(HttpStatusCode.OK)
               }
               delete {
                   val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                   shoppingList.removeIf {it.id == id}
                   call.respond(HttpStatusCode.OK)
               }
           }
        }
    }.start(wait = true)
}