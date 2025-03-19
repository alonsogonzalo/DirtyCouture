package com.dirtycouture

fun main() {
    println("Hello World!")

    /*
 * 📌 File Naming and Type Conventions in Kotlin
 *
 * 1️⃣ Kotlin File (.kt)
 *    - Used when creating a file that does not define a specific class.
 *    - Recommended for utility functions, top-level functions, and singleton objects.
 *    - Example:
 *      fun sayHello() {
 *          println("Hello, world!")
 *      }
 *
 * 2️⃣ Kotlin Class (.kt)
 *    - Used when defining a class, interface, enum, or object.
 *    - IntelliJ IDEA provides options like Class, Enum, Interface, and Object.
 *    - Example:
 *      class User(val name: String)
 *
 * 🔥 Naming Conventions:
 *    - Classes and objects should start with an uppercase letter (PascalCase).
 *      Example: Main.kt, User.kt, MyService.kt
 *    - Utility or function-based files can be lowercase (camelCase or snake_case).
 *      Example: utils.kt, StringExtensions.kt
 *
 * ✅ Summary:
 *    - Use "Kotlin Class" for defining a class.
 *    - Use "Kotlin File" for standalone functions or utility code.
 *    - Always use `.kt` as the file extension.
 */
}