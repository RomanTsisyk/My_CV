package tsisyk.app.mycv.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tsisyk.app.mycv.database.*
import tsisyk.app.mycv.network.NetWorkDataSource
import tsisyk.app.mycv.network.response.InfoResponse
import tsisyk.app.mycv.network.response.WorkExperienceResponse

class MyCvRepositoryImpl(
    private val infoDao: InfoDao,
    private val workExperienceDao: WorkExperienceDao,
    private val netWorkDataSource: NetWorkDataSource
) : MyCvRepository {

    // MyInfo fragment

    init {
        netWorkDataSource.downloadedMyInfo.observeForever(this::presistFetchedInfo)
        netWorkDataSource.downloadedWorkExperiance.observeForever { presistFetchedWorkExperience(it) }
    }

    private suspend fun initFirstTime() {
        netWorkDataSource.fetchInfo()
        netWorkDataSource.fetchWorkExperience()
    }


    override suspend fun getInfo(): LiveData<InfoEntry> {
        return withContext(Dispatchers.IO) {
            initFirstTime()
            return@withContext infoDao.getInfo()
        }
    }


    private fun presistFetchedInfo(infoResponse: InfoResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            infoDao.upsert(infoResponse.InfoEntry)
        }
    }

    // List fragment


    override suspend fun getWorkExperience(): LiveData<List<WorkExperienceEntry>> {
        return withContext(Dispatchers.IO) {
            initFirstTime()
            return@withContext workExperienceDao.getWorkExperience()
        }
    }


    private fun presistFetchedWorkExperience(workExperienceResponse: WorkExperienceResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            workExperienceDao.upsert(workExperienceResponse.workExperienceEntry)
        }
    }

    // Detail fragment


    override suspend fun getWorkExperienceDetails(firmName: String): LiveData<WorkExperienceDetaileEntry> {
        return withContext(Dispatchers.IO) {
            initFirstTime()
            return@withContext workExperienceDao.getWorkExperienceDetaile(firmName)
        }
    }



}