package com.ylallencheng.fakeim

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ylallencheng.fakeim.databinding.ActivityMainBinding
import com.ylallencheng.fakeim.databinding.ItemMessageBinding

class MainActivity : AppCompatActivity() {

    // View Binding
    private val mBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // View Model
    private val mViewModel: MainViewModel by viewModels()

    // Action Mode
    private val mActionModeCallback: ActionMode.Callback =
        object : ActionMode.Callback {
            override fun onActionItemClicked(
                mode: ActionMode?,
                item: MenuItem?
            ): Boolean {
                when (item?.itemId) {
                    R.id.menuDone -> {
                        mode?.finish()
                        mViewModel.finishActionMode(true)
                    }
                }
                return true
            }

            override fun onCreateActionMode(
                mode: ActionMode?,
                menu: Menu?
            ): Boolean {
                mode?.menuInflater?.inflate(R.menu.menu_main, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                mode?.setTitle(R.string.label_delete_message)
                return true
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                mViewModel.finishActionMode(false)
            }
        }

    /* ------------------------------ Override */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        initRecyclerView()
        observe()
    }

    /* ------------------------------ UI */

    /**
     * Initialize recycler view
     */
    private fun initRecyclerView() {
        mBinding.recyclerView.adapter = MessageAdapter(mViewModel)
    }

    /* ------------------------------ Model */

    /**
     * Observe data
     */
    private fun observe() = lifecycleScope.launchWhenResumed {
        mViewModel.addMessageTrigger.observe(
            this@MainActivity,
            Observer {
                (mBinding.recyclerView.adapter as MessageAdapter).submitList(it)
                (mBinding.recyclerView).smoothScrollToPosition(it.size - 1)
            })

        mViewModel.editMessageTrigger.observe(
            this@MainActivity,
            Observer {
                (mBinding.recyclerView.adapter as MessageAdapter).submitList(it)
            })

        mViewModel.actionModelTrigger.observe(
            this@MainActivity,
            Observer { this@MainActivity.startSupportActionMode(mActionModeCallback) })
    }
}

/**
 * Message list adapter
 */
class MessageAdapter(
    private val mViewModel: MainViewModel
) : ListAdapter<MessageBindingModel, MessageAdapter.MessageViewHolder>(MessageBindingModel.DIFF_CALLBACK) {

    /* ------------------------------ Overrides */

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageViewHolder {
        val binding: ItemMessageBinding =
            ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(mViewModel, binding)
    }

    override fun onBindViewHolder(
        holder: MessageViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    /* ------------------------------ View Holder */

    /**
     * Message View Holder
     */
    class MessageViewHolder(
        private val mViewModel: MainViewModel,
        private val mBinding: ItemMessageBinding
    ) : RecyclerView.ViewHolder(mBinding.root) {

        /**
         * Bind model to view
         */
        fun bind(model: MessageBindingModel) {
            mBinding.apply {
                bindingModel = model

                onClickListener = View.OnClickListener {
                    checkbox.isChecked = !checkbox.isChecked
                    model.checked = checkbox.isChecked
                    if (model.isEditing) {
                        mViewModel.updateSelectedBindingModel(model)
                    }
                }

                onLongClickListener = View.OnLongClickListener {
                    mViewModel.startActionMode()
                    true
                }
            }
        }
    }
}
