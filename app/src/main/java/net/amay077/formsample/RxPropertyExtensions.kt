package net.amay077.formsample

import jp.keita.kagurazaka.rxproperty.RxProperty

fun <T> RxProperty<T>.setValidatorKt(validator:(T)->String?) : RxProperty<T> {
    return this.setValidator(object : RxProperty.Validator<T> {
        override fun summarizeErrorMessages(errors: MutableList<String>): String? {
            return if (errors.size == 0) null else errors[0]
        }

        override fun validate(arg: T): MutableList<String> {
            return validator(arg)?.let { err -> mutableListOf(err) } ?: mutableListOf()
        }
    })
}
