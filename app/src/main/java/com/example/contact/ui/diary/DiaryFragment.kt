package com.example.contact.ui.diary

import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.contact.R
import com.example.contact.model.Logs
import kotlinx.android.synthetic.main.fragment_diary.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DiaryFragment : Fragment() {
    private var mListDiary: MutableList<Logs> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_diary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initAdapter()
    }

    private fun initData() {
        mListDiary = getCallLogs()
    }

    private fun initAdapter() {
        mRecyclerViewDiary.adapter = DiaryAdapter(mListDiary)
    }

    private fun getCallLogs(): MutableList<Logs> {
        val list: MutableList<Logs> = arrayListOf()

        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.DURATION,
            CallLog.Calls.DATE,
        )

        requireActivity().applicationContext.contentResolver.query(
            Uri.parse("content://call_log/calls"),
            projection,
            null,
            null,
            CallLog.Calls._ID + " DESC"
        )?.let { cursor ->
            if (cursor.count <= 0) {
                return@let
            }
            cursor.moveToFirst()
            do {
                cursor.apply {
                    val id = getString(getColumnIndex(CallLog.Calls._ID))
                    val number = getString(getColumnIndex(CallLog.Calls.NUMBER))
                    val duration = getString(getColumnIndex(CallLog.Calls.DURATION))
                    val dateStart = getString(getColumnIndex(CallLog.Calls.DATE))

                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = dateStart.toLong()

                    val stringDate = SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.US).let {
                        it.format(Date(dateStart.toLong()))
                    }
                    val stringDuration = duration.let {
                        "[$it]"
                    }
                    list.add(Logs(id, number, stringDuration, stringDate))
                }
            } while (cursor.moveToNext())
        }
        return list
    }
}
