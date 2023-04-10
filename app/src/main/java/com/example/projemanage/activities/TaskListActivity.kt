package com.example.projemanage.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projemanage.R
import com.example.projemanage.adapters.TaskListItemsAdapter
import com.example.projemanage.databinding.ActivityTaskListBinding
import com.example.projemanage.firebase.FireStoreClass
import com.example.projemanage.models.Board
import com.example.projemanage.models.Card
import com.example.projemanage.models.Task
import com.example.projemanage.models.User
import com.example.projemanage.utils.Constants
import java.text.FieldPosition

class TaskListActivity : BaseActivity() {

    private lateinit var binding: ActivityTaskListBinding

    private lateinit var mBoardDetails: Board
    private lateinit var mBoardDocumentId: String
    lateinit var mAssignedMemberDetailList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this, mBoardDocumentId)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MEMBERS_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardDetails(this, mBoardDocumentId)
        } else {
            Log.e("Canceled", "Canceled")
        }
    }

    fun cardDetails(taskListPosition: Int, carPosition: Int) {
        val intent = Intent(this, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, carPosition)
        intent.putParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST, mAssignedMemberDetailList)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_members -> {
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarTaskListActivity)
        val actionBar = supportActionBar
        if (actionBar != null) with(actionBar) {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.name
        }
        binding.toolbarTaskListActivity.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    fun boardDetails(board: Board) {

        mBoardDetails = board


        hideProgressDialog()
        setupActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)

    }


    fun addUpdateTaskListSuccess() {

        hideProgressDialog()

        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().getBoardDetails(this, mBoardDetails.documentId)

    }

    fun createTaskList(taskListName: String) {
        val task = Task(taskListName, FireStoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }


    fun updateTaskList(position: Int, listName: String, model: Task) {
        val task = Task(listName, model.createBy)

        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)


        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)

    }

    fun deleteTaskList(position: Int) {
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)


    }

    fun addCardToTaskList(position: Int, cardName: String) {
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        val cardAssignedUsersList: ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FireStoreClass().getCurrentUserId())


        val card = Card(cardName, FireStoreClass().getCurrentUserId(), cardAssignedUsersList)

        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)

        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createBy,
            cardsList
        )


        mBoardDetails.taskList[position] = task

        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)

    }

    fun boardMembersDetailsList(list: ArrayList<User>) {
        mAssignedMemberDetailList = list

        hideProgressDialog()

        val addTaskList = Task(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)


        binding.rvTaskList.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        binding.rvTaskList.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(this, mBoardDetails.taskList)
        binding.rvTaskList.adapter = adapter

    }

    fun updateCardsInTaskList(taskListPosition: Int, cards: ArrayList<Card>){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        mBoardDetails.taskList[taskListPosition].cards = cards

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    companion object {
        const val MEMBERS_REQUEST_CODE: Int = 13
        const val CARD_DETAILS_REQUEST_CODE: Int = 14
    }
}