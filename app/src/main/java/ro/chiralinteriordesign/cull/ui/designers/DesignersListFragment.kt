package ro.chiralinteriordesign.cull.ui.designers

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ro.chiralinteriordesign.cull.R
import ro.chiralinteriordesign.cull.databinding.DesignersListFragmentBinding
import ro.chiralinteriordesign.cull.databinding.DesignersListItemBinding
import ro.chiralinteriordesign.cull.model.designer.Designer
import ro.chiralinteriordesign.cull.ui.auth.AuthActivity

class DesignersListFragment : Fragment() {

    companion object {
        fun newInstance() = DesignersListFragment()
    }

    private val viewModel: DesignersListViewModel by viewModels()
    private var binding: DesignersListFragmentBinding? = null
    private val adapter = Adapter()

    private val contactDesignerContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            contactDesigners()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DesignersListFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = binding ?: return
        binding.recyclerView.adapter = adapter
        viewModel.designers.observe(viewLifecycleOwner) {
            adapter.data = it
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnContinue.visibility = View.INVISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                binding.btnContinue.visibility = View.VISIBLE
            }
        }

        binding.btnContinue.setOnClickListener {
            if (viewModel.isLoggedIn) {
                contactDesigners()
            } else {
                contactDesignerContent.launch(Intent(requireActivity(), AuthActivity::class.java))
            }
        }

        binding.navBar.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.navBar.btnClose.setOnClickListener {
            requireActivity().finish()
        }

        binding.navBar.titleView.setText(R.string.designers_title)

    }

    private fun contactDesigners() {
        val selectionList = adapter.selection.toList()
        val maxDesigners = 1
        if (selectionList.size > maxDesigners) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(resources.getString(R.string.designers_too_many, maxDesigners))
                .setPositiveButton(R.string.alert_ok, null)
                .show()
            return
        }

        viewModel.contactDesigners(adapter.selection.toList()).observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(if (!it) R.string.offer_failed else R.string.offer_sent)
                .setPositiveButton(R.string.alert_ok, null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.designers.value.isNullOrEmpty()) {
            viewModel.loadDesigners()
        }
    }


    class DesignerViewHolder(val binding: DesignersListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var data: Designer? = null
            set(newValue) {
                field = newValue
                field?.let {
                    Glide.with(binding.imageView)
                        .load(it.photo)
                        .into(binding.imageView)

                    binding.titleView.text = it.name
                    binding.subtitleView.text = it.description
                }
            }

        var isChecked: Boolean
            get() = binding.checkbox.isChecked
            set(newValue) {
                binding.checkbox.isChecked = newValue
            }
    }

    class Adapter : RecyclerView.Adapter<DesignerViewHolder>() {

        var data: List<Designer>? = null
            set(newValue) {
                field = newValue
                notifyDataSetChanged()
            }

        val selection = HashSet<Long>()

        override fun getItemCount(): Int {
            return data?.size ?: 0
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DesignerViewHolder {
            return DesignerViewHolder(DesignersListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
                .apply {
                    itemView.setOnClickListener {
                        data?.let { d ->
                            if (!selection.remove(d.id)) {
                                selection.add(d.id)
                            }
                            notifyItemChanged(adapterPosition)
                        }
                    }
                }
        }

        override fun onBindViewHolder(holder: DesignerViewHolder, position: Int) {
            holder.data = data?.get(position)
            holder.data?.let {
                holder.isChecked = selection.contains(it.id)
            }
        }
    }
}