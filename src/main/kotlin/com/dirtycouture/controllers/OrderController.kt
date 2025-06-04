package  com.dirtycouture.controllers
import com.dirtycouture.DBFactory
import com.dirtycouture.db.generated.tables.CartItems
import com.dirtycouture.db.generated.tables.Orders
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

object OrderController {
    fun addOrderHome(call: ApplicationCall) {

    }

    suspend fun getAllOrderByIdUser(call: ApplicationCall) {
        val userId= call.parameters["userId"]?.toLong()
        if(userId==null){
            call.respond(HttpStatusCode.BadRequest, "Empty fields")
            return
        }
        val allUserOrders= DBFactory.dslContext.selectFrom(Orders.ORDERS)
            .where(CartItems.CART_ITEMS.USER_ID.eq(userId))
            .fetchInto(Orders::class.java)

        if(allUserOrders.isEmpty()){
            call.respond(HttpStatusCode.BadRequest, "No orders found")
            return
        }else{
            call.respond(allUserOrders)
        }
    }

}