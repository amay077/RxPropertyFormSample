package net.amay077.formsample

import android.databinding.ObservableField
import android.util.Log
import android.view.View
import android.widget.CheckBox
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function4
import io.reactivex.subjects.BehaviorSubject
import java.text.SimpleDateFormat
import java.util.*

class FormProperties {

    enum class Gender { MAN, WOMAN, NOT_SET }

    // 各フォームの値が流れる
    private val nicknameSubject = BehaviorSubject.create<String>()
    private val genderSubject = BehaviorSubject.create<Gender>()
    private val birthdaySubject = BehaviorSubject.create<Calendar>()
    private val agreementSubject = BehaviorSubject.create<Boolean>()

    // 各フォームに表示する値が流れる
    val genderValueField: ObservableField<Boolean> = ObservableField()
    val birthdayValueField: ObservableField<String> = ObservableField()

    // 各フォームに入力された値が条件を満たしているかどうかの値が流れる
    val isValidNickNameField: ObservableField<Boolean> = ObservableField()
    val isValidGenderField: ObservableField<Boolean> = ObservableField()
    val isValidBirthdayField: ObservableField<Boolean> = ObservableField()
    val canRegister: ObservableField<Boolean> = ObservableField()

    init {
        birthdaySubject.onNext(Calendar.getInstance())
        genderSubject.onNext(Gender.NOT_SET)
        nicknameSubject.onNext("")
        agreementSubject.onNext(false)
    }

    var nickname: String = ""
        set(value) {
            field = value
            nicknameSubject.onNext(value)
        }

    var birthday : Calendar? = null
        set(value) {
            field = value
            value?.let {
                birthdayValueField.set(SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN).format(value.time))
                birthdaySubject.onNext(value)
            }
        }

    var isMan: Boolean? = null
        set(value) {
            field = value
            value?.let {
                genderValueField.set(isMan)
                genderSubject.onNext(if(isMan == true) Gender.MAN else Gender.WOMAN)
            }
        }

    private var isAgreed: Boolean = false
    fun onCheckedChanged(v: View) {
        isAgreed = (v as CheckBox).isChecked
        agreementSubject.onNext(isAgreed)
    }

    private val nicknameValidationObservable = nicknameSubject.map {
        it.isNotEmpty() && it.length >= 2 && it.length <= 10 // ニックネームが2文字以上10文字以下
    }.doOnNext {
        isValidNickNameField.set(it)
    }

    private val birthdayValidationObservable = birthdaySubject.map {
        it.apply { add(Calendar.YEAR, 18 ) } < Calendar.getInstance() // 18歳以上
    }.doOnNext {
        isValidBirthdayField.set(it)
    }

    private val genderValidationObservable = genderSubject.map {
        it != Gender.NOT_SET // 男女どちらかが設定されている
    }.doOnNext {
        isValidGenderField.set(it)
    }

    fun getValidationObservable(): Disposable {
        return Observable
                .combineLatest(
                        nicknameValidationObservable,
                        birthdayValidationObservable,
                        genderValidationObservable,
                        agreementSubject,
                        Function4<Boolean, Boolean, Boolean, Boolean, Boolean> {
                            isValidName, isValidDob, isValidGender, isAgreed -> isValidName && isValidDob && isValidGender && isAgreed
                        })
                .subscribe({ canRegister.set(it) },{ Log.e("error", it.message) })
    }
}