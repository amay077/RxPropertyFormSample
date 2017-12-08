package net.amay077.formsample

import android.app.DatePickerDialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckBox
import net.amay077.formsample.databinding.ActivityFormBinding
import android.arch.lifecycle.Observer
import android.widget.Toast
import java.util.*

class FormActivity : AppCompatActivity() {
    val properties = FormProperties()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityFormBinding>(this, R.layout.activity_form)
        binding.activity = this

        // LiveData を監視して Toast を表示
        properties.toast.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    fun onGenderClicked() {
        val items = arrayOf("男性", "女性")
        AlertDialog.Builder(this)
                .setTitle("性別を選択してください")
                .setItems(items, { _, which -> properties.gender.set(GenderFromId(which))})
                .show()
    }

    fun onBirthdayClicked() {
        DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener({ _, y, m, d -> properties.birthday.set(Calendar.getInstance().apply { set(y, m, d) }) }),
                2000, 0, 1).show()
    }

    fun onCheckedChanged(v: View) {
        properties.isAgreed.set((v as CheckBox).isChecked)
    }

    override fun onDestroy() {
        properties.dispose()
        super.onDestroy()
    }
}
