package net.amay077.formsample

import jp.keita.kagurazaka.rxproperty.RxProperty

/**
 * RxProperty への Validator 設定メソッド
 *
 * 標準の RxProperty.setValidator が (T)->String で null を返せないので作った
 */
fun <T> RxProperty<T>.setValidatorKt(validator:(T)->String?) : RxProperty<T> {
    return this.setValidator(object : RxProperty.Validator<T> {

        // 入力値検証の実行。エラーがある場合はエラー内容を含むリスト、ない(null)の場合は空リストを返す
        override fun validate(arg: T): MutableList<String> {
            return validator(arg)?.let { err -> mutableListOf(err) } ?: mutableListOf()
        }

        // エラー内容のサマリ。とりあえずエラーリストの先頭。エラーがない場合は null
        override fun summarizeErrorMessages(errors: MutableList<String>): String? {
            return if (errors.size == 0) null else errors[0]
        }
    })
}
