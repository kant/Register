package com.nytimes.android.external.register.bundle

import android.os.Bundle
import com.nytimes.android.external.register.APIOverrides
import com.nytimes.android.external.register.Purchases
import com.nytimes.android.external.registerlib.GoogleUtil
import javax.inject.Inject

class PurchasesBundleBuilder @Inject
constructor(apiOverrides: APIOverrides, private val purchases: Purchases) : BaseBundleBuilder(apiOverrides) {

    private var type: String? = null
    private var continuationToken: String? = null

    fun newBuilder(): PurchasesBundleBuilder {
        bundle = Bundle()
        return this
    }

    fun type(type: String): PurchasesBundleBuilder {
        this.type = type
        return this
    }

    fun continuationToken(continuationToken: String?): PurchasesBundleBuilder {
        this.continuationToken = continuationToken
        return this
    }

    fun build(): Bundle {
        val responseCode = responseCode()
        bundle.putInt(GoogleUtil.RESPONSE_CODE, responseCode)

        if (responseCode == GoogleUtil.RESULT_OK) {
            val purchasesLists = purchases.getPurchasesLists(type!!, continuationToken)
            bundle.putStringArrayList(GoogleUtil.INAPP_PURCHASE_ITEM_LIST,
                    ArrayList(purchasesLists.purchaseItemList))
            bundle.putStringArrayList(GoogleUtil.INAPP_PURCHASE_DATA_LIST,
                    ArrayList(purchasesLists.purchaseDataList))
            bundle.putStringArrayList(GoogleUtil.INAPP_DATA_SIGNATURE_LIST,
                    ArrayList(purchasesLists.dataSignatureList))
            if (!purchasesLists.continuationToken.isNullOrEmpty()) {
                bundle.putString(GoogleUtil.INAPP_CONTINUATION_TOKEN, purchasesLists.continuationToken)
            }
        }
        return bundle
    }

    override fun rawResponseCode(): Int {
        return apiOverrides.getPurchasesResponse
    }
}
