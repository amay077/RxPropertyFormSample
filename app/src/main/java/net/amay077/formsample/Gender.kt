package net.amay077.formsample

enum class Gender(val id :Int) {
    MAN(0),
    WOMAN(1),
    NOT_SET(2)

}

/***
 * TopLevel なのがびみょー
 */
fun GenderFromId(id: Int) : Gender
        = Gender.values().first { it.id == id }