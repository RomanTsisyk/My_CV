package tsisyk.app.mycv.ui.experience.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.firm_list_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import tsisyk.app.mycv.R
import tsisyk.app.mycv.database.WorkExperienceEntry
import tsisyk.app.mycv.ui.BaseScopedFragment


@Suppress("DEPRECATION")
class FirmListFragment : BaseScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory by instance<FirmListViewModelFactory>()

    private lateinit var viewModel: FirmListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.firm_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(FirmListViewModel::class.java)
        bindUI()
    }

    fun bindUI() = launch(Dispatchers.Main) {
        val workExperienceEntry = viewModel.companiesEntry.await()
        workExperienceEntry.observe(viewLifecycleOwner, Observer { it ->
            if (it == null) {
                return@Observer
            }
            group.visibility = View.GONE
            it.toCompanyItems()
            initRecycleView(it.toCompanyItems())
        })
    }

    private fun initRecycleView(items: List<CompanyItem>) {
        val groupAdapter = GroupAdapter<ViewHolder>().apply { addAll(items) }
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@FirmListFragment.context)
            adapter = groupAdapter
        }
        groupAdapter.setOnItemClickListener { item, view -> Log.i("CLick", item.toString()) }
    }

    private fun List<WorkExperienceEntry>.toCompanyItems(): List<CompanyItem> {
        return this.map { CompanyItem(it) }
    }

}


