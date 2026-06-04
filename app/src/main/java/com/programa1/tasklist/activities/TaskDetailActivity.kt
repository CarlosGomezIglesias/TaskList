package com.programa1.tasklist.activities

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.programa1.tasklist.R
import com.programa1.tasklist.Receivers.ReminderReceiver
import com.programa1.tasklist.data.Category
import com.programa1.tasklist.data.CategoryDAO
import com.programa1.tasklist.data.Task
import com.programa1.tasklist.data.TaskDAO
import com.programa1.tasklist.databinding.ActivityTaskDetailBinding
import java.util.Calendar

class TaskDetailActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_CATEGORY_ID = "CATEGORY_ID"
        const val EXTRA_TASK_ID = "TASK_ID"
    }

    lateinit var binding: ActivityTaskDetailBinding
    lateinit var task: Task
    var category: Category? = null

    lateinit var categoryDAO: CategoryDAO

    lateinit var taskDAO: TaskDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //llamada a la vista del binding
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_task_detail)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        categoryDAO=CategoryDAO(this)
        taskDAO= TaskDAO(this)

        val taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)
        val categoryId = intent.getIntExtra(EXTRA_CATEGORY_ID, -1)

        category=categoryDAO.getById(categoryId)
        if(taskId!=-1){
            task= taskDAO.getById(taskId)!!
            supportActionBar?.title=getString(R.string.alertDialog_title_editTask)
        }else{
            val position = taskDAO.countByCategory(category!!)
            task= Task(-1,"", "",false,null ,false, position, false,category!!)
            supportActionBar?.title=getString(R.string.alertDialog_title_createTask)
        }

        binding.titleTextField.editText!!.setText(task.title)
        binding.titleTextField.editText!!.addTextChangedListener {
            //Si esta el nombre en blanco avisa
            if (binding.titleTextField.editText!!.text.trim().isEmpty()) {
                binding.titleTextField.error = getString(R.string.error_texField)
                binding.saveButton.isEnabled = false
            } else {
                binding.titleTextField.error = null
                binding.saveButton.isEnabled = true
            }
        }
        binding.descriptionTextField.editText!!.setText(task.description)
        binding.priorityCheckBox.isChecked = task.priority
        binding.notificationCheckBox.isChecked = task.notification
        binding.menuReminder.visibility =
            if (task.notification == true) View.VISIBLE else View.GONE
        binding.notificationCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.menuReminder.visibility =
                if (isChecked) View.VISIBLE else View.GONE
        }

        if(task.limitDate != null){

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = task.limitDate!!

            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)

            val fechaTexto = "$day/$month/$year"

            binding.dateTextField.editText?.setText(fechaTexto)
        }
        binding.dateTextField.editText?.setOnClickListener {

            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->

                    val selectedCalendar = Calendar.getInstance()

                    selectedCalendar.set(
                        selectedYear,
                        selectedMonth,
                        selectedDay
                    )

                    // Guardar en milisegundos
                    task.limitDate = selectedCalendar.timeInMillis

                    // Mostrar texto bonito
                    val fecha =
                        "$selectedDay/${selectedMonth + 1}/$selectedYear"

                    binding.dateTextField.editText?.setText(fecha)

                },
                year,
                month,
                day
            )

            datePicker.show()
        }
        binding.dateTextField.setEndIconOnClickListener {
            binding.dateTextField.editText?.setText("")
            task.limitDate= null
        }

        binding.saveButton.setOnClickListener {
            task.title=binding.titleTextField.editText!!.text.toString()
            task.description=binding.descriptionTextField.editText!!.text.toString()
            task.priority = binding.priorityCheckBox.isChecked
            task.notification = binding.notificationCheckBox.isChecked
            taskDAO.save(task)
            Snackbar.make(binding.root, getString(R.string.snackBar_saveTask), Snackbar.LENGTH_LONG).show()

            scheduleAlarm(task)

            finish()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun scheduleAlarm(task: Task) {

        Log.d("ALARM_TEST", "scheduleAlarm ejecutado")

        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.TASK_ID, task.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            task.id,
                intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val triggerTime = getReminderTime(task.limitDate!!, 1)

        // 🔥 IMPORTANTE: usa exacta para pruebas
        //if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        //}
        Log.d("ALARM_TEST", "limitDate = ${task.limitDate}")
        Log.d("ALARM_TEST", "triggerTime = $triggerTime")
    }
    private fun getReminderTime(limitDate: Long, daysBefore: Int = 1): Long {

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = limitDate

        // Restar días
        calendar.add(Calendar.DAY_OF_YEAR, -daysBefore)

        // Fijar hora a las 09:00
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }
}