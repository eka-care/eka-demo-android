package eka.dr.intl.common

class PageParams {
    companion object {

        const val PARAM_DOCTOR_ID = "doid"
        const val PARAM_SA_ID = "saId"
        const val PARAM_DOCTOR_NAME = "dname"

        // Start self Assesment

        const val PARAMS_IS_SELF_ASSESSMENT = "self"
        const val PARAMS_DOCTOR_OID = "doctor_oid"
        const val PARAMS_ANONYMOUS = "anonymous"
        const val PARAMS_WORK_FLOW_ID = "wflow_id"
        const val PARAMS_PATIENT_INFO = "patient_info"
        const val PARAMS_DOC_INFO = "doc_info"
        const val PARAMS_SKIP_PROFILE = "skip_profile"
        const val PARAMS_SA_ERROR = "sa_error"
        const val PARAMS_SA_CONTEXT = "context"
        const val PARAMS_PATIENT = "patient"
        const val PARAMS_APPOINTMENT_ID = "aid"
        const val PARAMS_SA_ANON = "sa_anon"
        const val PARAMS_APPOINTMENT_SA = "appointment_sa"
        const val PARAMS_COVID_CARE = "covid_care"

        const val SHOW_DOCTOR_SELECTION = "show_doctor_selection"

        const val PARAM_WEB_VIEW_URL = "url"
        const val PARAM_URL = "url"
        const val PARAM_PATIENT_ID = "pid"
        const val PARAM_FROM = "from"
        const val SHOW_TOOLBAR = "toolbar"

        const val PAYMENT_TRANSACTION_ID = "transaction_id"
        const val PAYMENT_STATUS = "payment_status"


        //HOME
        const val PARAM_TAB_INDEX = "tabId"

        //TELE
        const val CALLER_NAME = "name"
        const val CALLER_IMAGE = "img"
        const val CHANNEL_ID = "cnId"
        const val TOKEN = "token"
        const val IS_AUDIO_MUTED = "audio"
        const val IS_VIDEO_MUTED = "video"
        const val USER_ID = "uid"


        // Files
        const val INTENT_FILE_URI = "fileUri"
        const val INTENT_PDF_URL = "url"
        const val INTENT_FILE_UPLOAD = "fileUploadPath"
        const val INTENT_FILE_UPLOAD_WITHOUT_TAG = "fileUploadWithoutTag"
        const val INTENT_FILES_TO_VIEW = "filesToView"
        const val INTENT_VIEWER_FROM_FILES = "viewerFromFiles"
        const val INTENT_SHARE_TEXT = "shareText"
        const val INTENT_DOCUMENT_ID = "documentId"
        const val PARAM_FILE_DATA = "file_data"
        const val PARAM_ANALYZER_TYPE = "analyzer"
        const val INTENT_EDIT_FILE_LIST_PREVIEW = "editFileListPreview"
        const val INTENT_EDIT_FILE_DETAILS = "editFileDetails"
        const val INTENT_EDIT_FILE_PREVIEW = "editFilePreview"
        const val INTENT_DOCUMENT_TYPE = "documentType"
        const val INTENT_DOCUMENT_SHAREABLE = "isDocumentShareable"
        const val INTENT_PATIENT_OID = "patientOid"
        const val SHOULD_OPEN_UPLOAD_BOTTOM_SHEET = "should_open_upload_bottom_sheet"
        const val INTENT_DOC_CREATION_DATE = "docCreationDate"
        const val IS_IMAGE_UPLOAD = "isImageUpload"
        const val IS_PDF_UPLOAD = "isPDFUpload"
        const val IS_MULTI_IMAGE_UPLOAD = "isMultiImageUpload"
        const val IS_STARTED_FROM_IMAGE_PREVIEW = "isStartedFromImagePreview"
        const val INTENT_DOCUMENT_TAGS = "documentTags"
        const val UPLOAD_FILE_CALLBACK = "uploadFileCallback"
        const val INTENT_CLEAR_ACTIVITY_STACK = "clearActivityStack"
        const val RN_PAGE_NAME = "pageName"
        const val RN_PAGE_PROPS = "pageProps"
        const val INTENT_FILE_TYPE = "fileType"
        const val INTENT_FILE_LIST = "fileList"
        const val IS_ENCRYPT_UPLOAD_FLOW = "is_encrypt_upload_flow"
        const val REFRESH_UI = "refreshUi"
        const val PARAM_LOCAL_ID = "local_id"

        //patient
        const val PARAM_HARD_REFRESH = "hard_refresh"
        const val PARAM_UPDATE = "update"
        const val MULTI_FILES = "multi_files"
        const val PARAM_CONTAINER = "container"
    }
}