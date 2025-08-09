package telran.project.gardenshop.swagger;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface SwaggerResponses {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item added to cart"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Cart or product not found")
    })
    @interface CartItemCreated {
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantity updated"),
            @ApiResponse(responseCode = "400", description = "Invalid quantity"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @interface CartItemUpdated {
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item removed")
    })
    @interface CartItemRemoved {
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of cart items retrieved")
    })
    @interface CartItemListRetrieved {
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cart cleared")
    })
    @interface CartCleared {
    }
}