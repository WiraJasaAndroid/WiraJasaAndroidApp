package com.wirajasa.wirajasabisnis.core.epoxy.model

import com.wirajasa.wirajasabisnis.R
import com.wirajasa.wirajasabisnis.core.epoxy.utility.ViewBindingKotlinModel
import com.wirajasa.wirajasabisnis.databinding.ScreenLoadingBinding

data class ScreenLoadingEpoxyModel(
    val title: String?
): ViewBindingKotlinModel<ScreenLoadingBinding>(R.layout.screen_loading) {

    override fun ScreenLoadingBinding.bind() {
        title?.let {
            tvScreenLoadingStatus.text = title
        }
    }
}
