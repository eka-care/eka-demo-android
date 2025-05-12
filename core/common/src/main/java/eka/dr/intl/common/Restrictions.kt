package eka.dr.intl.common

enum class Restrictions(val value: String) {
    /** Access to All Patients page */
    ALL_PT("ALL_PT"),

    /** Ability to Start a Visit */
    START_VISIT("START_VISIT"),

    /** Access to Payment Dashboard Page */
    PAY_DASH("PAY_DASH"),

    /** Access to Download Patient Database Excel */
    DOWNLOAD_PATIENT_EXCEL("DOWNLOAD_PATIENT_EXCEL"),

    /** Access to Payment Dashboard Page beyond today */
    PAY_DASH_BEYOND_TODAY("PAY_DASH_BEYOND_TODAY"),

    /** Permission to add a patient to the queue */
    PATIENT_REGISTRATION("PATIENT_REGISTRATION"),

    /** Permission to edit or update patient information after registration */
    PATIENT_DATABASE("PATIENT_DATABASE"),

    /** Permission to update patient vitals */
    VITALS_COLLECTION("VITALS_COLLECTION"),

    /** Permission to request medical records from patients */
    CAN_REQUEST_RECORDS("CAN_REQUEST_RECORDS"),

    /** Permission to edit the availability of any doctor */
    AVAILABILITY("AVAILABILITY"),

    /** Permission to create services */
    SKU_CREATE("SKU_CREATE"),

    /** Permission to edit services */
    SKU_EDIT("SKU_EDIT"),

    /** Permission to manage services (edit, deactivate, bulk upload and more) */
    SERVICES("SERVICES"),

    /** Permission to edit receipts */
    RECEIPT_EDIT("RECEIPT_EDIT"),

    /** Permission to access the daily billing summary */
    DAILY_FINANCE_OVERVIEW_STRIP("DAILY_FINANCE_OVERVIEW_STRIP"),

    /** Permission to create, edit or delete packages */
    PACKAGES("PACKAGES"),

    /** Permission to download the queue and generate reports */
    DOWNLOAD_QUEUE("DOWNLOAD_QUEUE"),

    /** Permission to access and modify settings and preferences for the business */
    SETTINGS_AND_PREFERENCES("SETTINGS_AND_PREFERENCES"),

    /** Permission to edit and manage profiles of any doctor */
    PROFILE_MANAGEMENT("PROFILE_MANAGEMENT"),

    /** Permission to access and manage roles and permissions */
    ROLES_AND_PERMISSIONS("ROLES_AND_PERMISSIONS"),

    /** Permission to access analytics */
    ANALYTICS("ANALYTICS"),

    /** Permission to access payout reports */
    PAYOUT_REPORTS("PAYOUT_REPORTS"),

    /** Allow user to create receipt with appointment */
    CAN_CREATE_RECEIPT_WITH_APT("CAN_CREATE_RECEIPT_WITH_APT"),

    /** Payment ID in receipt will be mandatory while creating a receipt */
    PAYMENT_ID_IN_RECEIPT("PAYMENT_ID_IN_RECEIPT"),

    /** Access to Health Exchange Orders Dashboard */
    HXNG_ORDERS("HXNG_ORDERS"),

    /** Ability to Edit Prescription */
    EDIT_RX("EDIT_RX"),

    /** Access to Left Block on New Queue */
    LEFT_BLOCK_ON_NEW_QUEUE("LEFT_BLOCK_ON_NEW_QUEUE"),

    /** Ability to Switch to Old Queue */
    GO_TO_OLD_QUEUE("GO_TO_OLD_QUEUE"),

    /** Access to My Account Page */
    MY_ACCOUNT("MY_ACCOUNT"),

    /** Ability to view Patient Mobile Number */
    PATIENT_MOBILE_NUMBER("PATIENT_MOBILE_NUMBER"),

    /** Ability to Delete Receipt */
    DELETE_RECEIPT("DELETE_RECEIPT"),

    /** Ability to Add Patient Address */
    ADD_PATIENT_ADDRESS("ADD_PATIENT_ADDRESS"),

    /** Ability to Upload Patient Medical Records */
    UPLOAD_MEDICAL_RECORDS("UPLOAD_MEDICAL_RECORDS"),

    /** Ability to Create ABHA */
    ABHA("ABHA"),

    /** Ability to View Self Assessment */
    SA("SA"),

    /** Ability to Create Treatment */
    TREATMENT("TREATMENT"),

    /** Ability to Refer a Patient to Doctor */
    REFER_PATIENT_TO_DOCTOR("REFER_PATIENT_TO_DOCTOR"),

    /** Ability to Send Google Review */
    SEND_GOOGLE_REVIEW("SEND_GOOGLE_REVIEW"),

    /** Ability to Send Attachment */
    SEND_ATTACHMENT("SEND_ATTACHMENT"),

    /** Ability to Request Vitals */
    REQUEST_VITALS("REQUEST_VITALS"),

    /** Ability to Send Payment Link */
    SEND_PAYMENT_LINK("SEND_PAYMENT_LINK"),

    /** Ability to Send Prescription via Own WhatsApp */
    SEND_RX_VIA_OWN_WA("SEND_RX_VIA_OWN_WA"),

    /** Ability to Send Rx */
    SEND_RX("SEND_RX"),

    /** Ability to Create Custom Queue */
    CREATE_CUSTOM_QUEUE("CREATE_CUSTOM_QUEUE"),

    /** Access to Doc Assist */
    DOC_ASSIST("DOC_ASSIST"),

    /** Ability to Create Medical Documents (medical certificate, consent form etc.) */
    CREATE_MEDICAL_DOCUMENTS("CREATE_MEDICAL_DOCUMENTS"),

    /** Access to Eka Credits */
    EKA_CREDITS("EKA_CREDITS"),

    /** Ability to Merge Patients */
    MERGE_PATIENTS("MERGE_PATIENTS"),

    /** Access to Appointment Widget (In Global Search) */
    APPOINTMENT_WIDGET("APPOINTMENT_WIDGET"),

    /** Ability to Perform Visit Actions from Patient Details Page (Start visit, Resume visit etc.) */
    PT_DETAILS_VISIT_ACTIONS("PT_DETAILS_VISIT_ACTIONS"),

    /** Ability to Edit Queue */
    EDIT_QUEUE("EDIT_QUEUE"),

    /** Ability to Create Prescription Template */
    CAN_CREATE_RX_TEMPLATE("CAN_CREATE_RX_TEMPLATE"),

    /** Ability to Cancel Appointment */
    CANCEL_APPT("CANCEL_APPT"),

    /** Ability to Add Follow Up Date */
    ADD_FOLLOW_UP_DATE("ADD_FOLLOW_UP_DATE"),

    /** Ability to Exit Appointment */
    EXIT_APPT("EXIT_APPT"),
}