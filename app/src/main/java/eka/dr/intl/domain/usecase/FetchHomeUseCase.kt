package eka.dr.intl.domain.usecase

import com.google.gson.Gson
import eka.dr.intl.common.OrbiUserManager
import eka.dr.intl.common.Resource
import eka.dr.intl.data.remote.dto.response.toHomeDomain
import eka.dr.intl.data.repository.HomeRepositoryImpl
import eka.dr.intl.domain.repository.HomeRepository
import eka.dr.intl.presentation.model.HomeDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchHomeUseCase {
    private val repository: HomeRepository = HomeRepositoryImpl()
    private val gson = Gson() // Single instance for better performance

    operator fun invoke(
        source: String,
        campaign: String,
        medium: String,
    ): Flow<Resource<HomeDomain>> = flow {
        try {
            emit(Resource.Loading())

            // Handle corrupted cache
            val cachedHomePage = try {
                OrbiUserManager.getHomeData()?.let {
                    gson.fromJson(it, HomeDomain::class.java)
                }
            } catch (e: Exception) {
                OrbiUserManager.saveHomeData("") // Clear corrupted cache
                null
            }

            // Network call
            val data = repository.getHome(
                source = source.trim(),
                campaign = campaign.trim(),
                medium = medium.trim(),
            )

            when {
                // Case 1: No data and no cache
                data == null && cachedHomePage == null -> {
                    emit(Resource.Error("Unable to fetch data"))
                    return@flow
                }

                // Case 2: API failure and no cache
                data?.success == false && cachedHomePage == null -> {
                    emit(Resource.Error(data.message ?: "Something went wrong!"))
                    return@flow
                }

                // Case 3: API success
                data?.success == true -> {
                    val homeDomain = data.toHomeDomain()
                    try {
                        val homeJson = gson.toJson(homeDomain)
                        OrbiUserManager.saveHomeData(homeJson)
                        emit(Resource.Success(homeDomain))
                    } catch (e: Exception) {
                        emit(Resource.Error("Cache update failed"))
                    }
                }

                // Case 4: API failure but cache available
                cachedHomePage != null -> {
                    emit(Resource.Success(cachedHomePage))
                }

                // Case 5: Fallback
                else -> {
                    emit(Resource.Error("Something went wrong!"))
                }
            }
        } catch (ex: Exception) {
            // Log the exception for debugging
            ex.printStackTrace()
            emit(Resource.Error(ex.localizedMessage ?: "Something went wrong!"))
        }
    }
}