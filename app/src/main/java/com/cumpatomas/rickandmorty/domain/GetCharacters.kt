package com.cumpatomas.rickandmorty.domain

import com.cumpatomas.rickandmorty.data.network.CharService
import com.cumpatomas.rickandmorty.data.network.ResponseEvent
import com.cumpatomas.rickandmorty.domain.model.CharModel

class GetCharacters {
    private val provider = CharService()

    suspend operator fun invoke(
        query: String
    ): List<CharModel> {
        val charList = mutableListOf<CharModel>()
        when (val result = provider.getAllChars(query)) {
            is ResponseEvent.Error -> {
                println("Error getting characters")
                result.exception
            }

            is ResponseEvent.Success -> {
                val charsList = result.data.results
                for (page in 1..result.data.info.pages) {
                    val newPageList = provider.getAllChars(query, page.toString())
                    if (newPageList is ResponseEvent.Success)
                        for (i in 0 until (newPageList.data.results?.count() ?: 0)) {
                            charList.add(
                                CharModel(
                                    newPageList.data.results?.get(i)?.id ?: "N/A",
                                    newPageList.data.results?.get(i)?.name ?: "N/A",
                                    newPageList.data.results?.get(i)?.species ?: "N/A",
                                    newPageList.data.results?.get(i)?.gender ?: "N/A",
                                    newPageList.data.results?.get(i)?.url ?: "N/A",
                                    newPageList.data.results?.get(i)?.image ?: "N/A"
                                )
                            )
                        }
                }

                if (charsList != null) {
                    for (i in 0 until charsList.count()) {
                        charList.add(
                            CharModel(
                                charsList[i].id ?: "N/A",
                                charsList[i].name ?: "N/A",
                                charsList[i].species ?: "N/A",
                                charsList[i].gender ?: "N/A",
                                charsList[i].url ?: "N/A",
                                charsList[i].image ?: "N/A"
                            )
                        )
                    }
                }
            }
        }
        return charList.distinct()
    }
}