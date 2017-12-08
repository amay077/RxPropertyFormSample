package net.amay077.formsample

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import jp.keita.kagurazaka.rxproperty.NoParameter
import jp.keita.kagurazaka.rxproperty.RxProperty
import jp.keita.kagurazaka.rxproperty.toReadOnlyRxProperty
import jp.keita.kagurazaka.rxproperty.toRxCommand
import java.text.SimpleDateFormat
import java.util.*

class FormProperties {
    enum class Gender(val id :Int) {
        MAN(0),
        WOMAN(1),
        OTHER(2),
        NOT_SET(9)
    }

    private val disposables = CompositeDisposable()

    /** ニックネーム */
    val nickname = RxProperty<String>("")
            .setValidator {
                if (it.length < 2 || it.length > 10)
                    // エラーの場合はその説明を、エラーなしの場合は null を返却
                    "ニックネームは2文字以上10文字以下にしてください" else null }

    /** 誕生日(Rawデータ) */
    val birthday = RxProperty<Calendar>(Calendar.getInstance())
            .setValidator {
                if (it >= Calendar.getInstance().apply { add(Calendar.YEAR, -18 ) }) "18歳以上が必要です" else null
            }

    /** 誕生日(表示用文字列) */
    val birthdayText = birthday.map {
        SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN).format(it.time)
    }.toReadOnlyRxProperty()

    /** 性別(Rawデータ) */
    val gender = RxProperty<Gender>(Gender.NOT_SET)
            .setValidator { if (it == Gender.NOT_SET) "性別を何か選択してください" else null }

    /** 性別(表示用文字列) */
    val genderTextResId = gender.map {
        when (it) {
            Gender.MAN -> R.string.male
            Gender.WOMAN -> R.string.female
            Gender.OTHER -> R.string.other
            else -> R.string.empty
        }
    }.toReadOnlyRxProperty()

    /** 利用規約同意 */
    val isAgreed = RxProperty<Boolean>(false)

    /** Toast を通知するためだけの LiveData */
    private val _toast = MutableLiveData<String>()
    val toast : LiveData<String> = _toast

    /** 登録ボタンが実行できるか */
    private val canRegistration: Observable<Boolean> = Observable
            .combineLatest(listOf(
                    nickname.onHasErrorsChanged().map { !it },
                    gender.onHasErrorsChanged().map { !it },
                    birthday.onHasErrorsChanged().map { !it },
                    isAgreed),
                    { anyList -> anyList.map { it as Boolean }.all { it }})

    /** 登録ボタンを押したときのコマンド */
    // canRegistration が true の時だけ実行可能なコマンド
    val register = canRegistration.toRxCommand<NoParameter>()
            .apply { this.subscribe {
                // RxCommand の subscribe が呼ばれた時 = ボタンが押された時
                // とりあえずトースト投げる
                _toast.postValue("RegistrationCompleteActivity へ移動するよ")
            }.addTo(disposables) }

    fun dispose() {
        disposables.clear()
    }
}
