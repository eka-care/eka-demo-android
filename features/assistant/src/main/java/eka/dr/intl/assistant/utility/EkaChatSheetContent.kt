package eka.dr.intl.assistant.utility

sealed class EkaChatSheetContent {
    data object Voice2RxInitialBottomSheet : EkaChatSheetContent()
    data object Voice2RxPatientBottomSheet : EkaChatSheetContent()
    data object Voice2RxMedicalBottomSheet : EkaChatSheetContent()
    data object Voice2RxPatientDetailBottomSheet : EkaChatSheetContent()
}