package com.example.contact.ui.contact

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contact.R
import com.example.contact.model.Contacts
import com.example.contact.ui.diary.DiaryFragment
import com.example.contact.utils.FirstFragmentListener
import com.example.contact.utils.OnItemClickListener
import com.example.contact.utils.PassData
import com.example.contact.utils.SendData
import com.uits.baseproject.utils.PermissionUtil
import kotlinx.android.synthetic.main.fragment_contact.*


class ContactsFragment : Fragment() {
    lateinit var mImgBtnCall: ImageButton
    lateinit var mImgBtnMessage: ImageButton
    private var permissions = arrayOf(Manifest.permission.READ_CONTACTS)
    lateinit var mContactAdapter: ContactAdapter
    var mListContacts: MutableList<Contacts> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkRunTimePermission()
    }

    private fun initAdapter() {
        mContactAdapter = ContactAdapter(mListContacts)
        mRecyclerView.adapter = mContactAdapter

        mProgressBarLoading.visibility = View.GONE
        mRecyclerView.visibility = View.VISIBLE

    }

    fun getContacts(context: Context): MutableList<Contacts> {
        val list: MutableList<Contacts> = arrayListOf()
        val cr = context.contentResolver
        val cur = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        if (cur?.count ?: 0 > 0) {
            while (cur != null && cur.moveToNext()) {
                val id = cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts._ID)
                )
                val name = cur.getString(
                    cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )
                if (cur.getInt(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER
                        )
                    ) > 0
                ) {
                    val pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (pCur!!.moveToNext()) {
                        val phoneNo = pCur!!.getString(
                            pCur!!.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )
//                        Log.i("nnn", "id: $id" + " Name: $name" + " PhoneNB: $phoneNo")
                        list.add(Contacts(id, name, phoneNo))
                    }
                    pCur!!.close()
                }
            }
        }
        cur?.close()
        return list
    }

    private fun checkRunTimePermission() {
        if (Build.VERSION.SDK_INT < 23) {
            getContactsAsyncTask()
        } else {
            requestPermissions(permissions, 55)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 55) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContactsAsyncTask()
            }
        }
    }

    private fun getData(list: MutableList<Contacts>?) {
        mListContacts = list!!
        mListContacts.sortBy { it.name }
        initAdapter()
    }

    private fun getContactsAsyncTask() {
        @SuppressLint("StaticFieldLeak")
        val mAsyncTask = object : AsyncTask<Context, Void, MutableList<Contacts>?>() {
            override fun doInBackground(vararg params: Context?): MutableList<Contacts>? {
                var list = params[0]?.let { getContacts(it) }
                return list
            }

            override fun onPostExecute(result: MutableList<Contacts>?) {
                super.onPostExecute(result)
                getData(result)
            }
        }.execute(activity)
    }

    @SuppressLint("IntentReset")
    fun showPopup(pos: Int) {
        val inflater =
            activity?.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_contact_call, null, false)
        val pw = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        mImgBtnCall = view.findViewById(R.id.mImgBtnCall)
        mImgBtnMessage = view.findViewById(R.id.mImgBtnMessage)
        mImgBtnCall.setOnClickListener {
            PermissionUtil.checkPermissionContact(
                requireActivity()
            ) {
                val intent = Intent(
                    Intent.ACTION_CALL,
                    Uri.parse("tel:" + mListContacts[pos].phoneNumber)
                )
                startActivity(intent)
            }
        }
        mImgBtnMessage.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_MESSAGING)
            }
            startActivity(intent)
        }
        pw.showAtLocation(view, Gravity.CENTER, 0, 0)
    }
}
