package com.common.control.model

data class PurchaseModelKt(
    private val productIdInitial: String, // Keep 'type' as a public var if it needs public set
    var type: String
) {
    var productId: String = productIdInitial
        private set // This makes the setter private

    companion object {
        const val INAPP = "inapp"
        const val SUBS = "subs"
    }

    // The @ProductType interface from Java is typically represented 
    // in Kotlin using a companion object with const vals for simple string constants,
    // or an enum class or sealed class for more complex type definitions.
    // For this case, INAPP and SUBS are already defined in the companion object.
    // If @ProductType was intended to be used as an annotation on a String to restrict its value,
    // you might consider using an enum class for the 'type' field directly,
    // or a String type with a custom setter/validator if an annotation-like check is needed.
}
